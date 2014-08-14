package com.minook.zeppa.activity;

import java.io.IOException;

import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.zeppauserendpoint.Zeppauserendpoint;
import com.minook.zeppa.zeppauserendpoint.Zeppauserendpoint.GetZeppaUser;
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUser;

public class LoginActivity extends AuthenticatedFragmentActivity implements
		OnClickListener {

	/*
	 * This class handles user launching the app and ensuring that they have
	 * connection to the backend/ authentication Reference:
	 * 
	 * For plus client:
	 * https://developers.google.com/+/mobile/android/getting-started
	 * https://developers.google.com/+/mobile/android/sign-in
	 * 
	 * implements: PlusClient.ConnectionCallbacks,
	 * PlusClient.OnConnectionFailedListener,
	 * PlusClient.OnMomentsLoadedListener,
	 * 
	 * For GoogleAuthUtil
	 * http://developer.android.com/reference/com/google/android
	 * /gms/auth/GoogleAuthUtil.html
	 * http://developer.android.com/google/play-services/auth.html#obtain
	 */

	// ----------- Global Variables Bank ------------- \\
	// Debug
	private String TAG = "LoginActivity";

	private enum UserResult {
		FETCH_SUCCESS, CREATE_NEW_USER, NETWORK_FAIL, AUTHORIZATION_FAIL, NOT_FOUND, UNKNOWN

	};

	/*
	 * ------------------- Override methods ----------------------- NOTES:
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		findViewById(R.id.sign_in_button).setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sign_in_button:

			if (checkPlayServices()) {
				if (apiClient == null
						|| (!apiClient.isConnecting() && !apiClient
								.isConnected())) {

					if (getSharedPreferences(Constants.SHARED_PREFS,
							MODE_PRIVATE).getString(Constants.GOOGLE_ACCOUNT,
							null) == null) {
						Intent intent = AccountPicker.newChooseAccountIntent(
								null, null, new String[] { "com.google" },
								false, null, null, null, null);
						startActivityForResult(intent, REQUEST_ACCOUNT_PICKER);
					} else if (connectionResult == null) {
						connectionProgress.show();
						apiClient.connect();
					} else {
						try {
							connectionResult.startResolutionForResult(this,
									REQUEST_CODE_RESOLVE_ERR);
						} catch (SendIntentException ex) {
							connectionResult = null;
							apiClient.connect();
						}

					}
				} else if (apiClient.isConnected()) {
					loadAndLaunch();
					// Login
				} else if (apiClient.isConnecting()
						&& !connectionProgress.isShowing()) {
					connectionProgress.show();
				}
			}

			break;
		}

	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		if (connectionProgress.isShowing()) {
			connectionProgress.dismiss();
		}

		Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onConnected(Bundle arg0) {
		loadAndLaunch();
	}

	/*
	 * ---------------------- My Methods ---------------------------- NOTES:
	 */

	/**
	 * Verify device has GooglePlayServices
	 * 
	 * @return
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	private void loadAndLaunch() {

		new AsyncTask<Void, Void, ZeppaUser>() {

			private UserResult resultCode;

			@Override
			protected ZeppaUser doInBackground(Void... params) {

				resultCode = UserResult.UNKNOWN;
				ZeppaUser zeppaUser = null;

				Zeppauserendpoint.Builder endpointBuilder = new Zeppauserendpoint.Builder(
						AndroidHttp.newCompatibleTransport(),
						AndroidJsonFactory.getDefaultInstance(),
						getGoogleAccountCredential());
				endpointBuilder = CloudEndpointUtils
						.updateBuilder(endpointBuilder);

				Zeppauserendpoint userEndpoint = endpointBuilder.build();
				try {

					resultCode = UserResult.UNKNOWN;
					Long appEngineId = getSharedPreferences(
							Constants.SHARED_PREFS, MODE_PRIVATE).getLong(
							Constants.USER_ID, -1);

					if (appEngineId.longValue() > 0) {

						GetZeppaUser fetchUserById = userEndpoint
								.getZeppaUser(appEngineId);
						zeppaUser = fetchUserById.execute();

						resultCode = UserResult.FETCH_SUCCESS;
						return zeppaUser;

					}
					Person currentPerson = Plus.PeopleApi
							.getCurrentPerson(apiClient);

					if (currentPerson == null) {
						// TODO: throw new unrecognized user error
						return null;
					}

					zeppaUser = userEndpoint.fetchMatchingUser(
							currentPerson.getId()).execute();

					if (zeppaUser != null) {

						SharedPreferences.Editor editor = getSharedPreferences(
								Constants.SHARED_PREFS, MODE_PRIVATE).edit();
						editor.putLong(Constants.USER_ID, zeppaUser.getKey()
								.getId());
						editor.commit();

						resultCode = UserResult.FETCH_SUCCESS;

					} else {
						resultCode = UserResult.CREATE_NEW_USER;
					}

				} catch (GoogleJsonResponseException ex) {
					
					resultCode = UserResult.CREATE_NEW_USER;
					ex.printStackTrace();
					return null;
				} catch (IOException ioEx) {

					Log.d(TAG, "IOException caught");
					ioEx.printStackTrace();
					resultCode = UserResult.NETWORK_FAIL;

					return null;
				}

				return zeppaUser;
			}

			@Override
			protected void onPostExecute(ZeppaUser result) {
				super.onPostExecute(result);

				if (connectionProgress.isShowing()) {
					connectionProgress.dismiss();
				}

				if (result == null) {

					switch (resultCode.ordinal()) {
					case 1: // UserResult.CREATE_NEW_USER
						Intent launchNewUser = new Intent(
								getApplicationContext(),
								NewAccountActivity.class);
						startActivity(launchNewUser);
						break;

					case 2: // UserResut.NETWORK_FAIL
						Toast.makeText(getApplicationContext(),
								"Connection Error", Toast.LENGTH_SHORT).show();
						break;

					case 3: // UserResult.AUTHORIZATION_FAIL
						Toast.makeText(getApplicationContext(),
								"Authorization Error", Toast.LENGTH_SHORT)
								.show();
						break;

					case 4:
						// TODO: raise toast asking if this is an error or if a
						// new account should be made
						Toast.makeText(getApplicationContext(),
								"Saved Account Not Found", Toast.LENGTH_SHORT)
								.show();
						break;

					case 5: // UserResult.UNKNOWN
					default:
						Toast.makeText(getApplicationContext(),
								"Error Occured", Toast.LENGTH_SHORT).show();

					}

				} else { // Launch Main
					((ZeppaApplication) getApplication()).initialize(result,
							getGoogleAccountCredential());

					Intent launchMain = new Intent(getApplicationContext(),
							MainActivity.class);
					launchMain.putExtra(Constants.INTENT_NOTIFICATIONS, false);
					startActivity(launchMain);
					finish();
				}

			}

		}.execute();

	}
}
