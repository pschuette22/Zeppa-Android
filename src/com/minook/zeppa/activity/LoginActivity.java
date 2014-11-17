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
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.zeppauserendpoint.Zeppauserendpoint;
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUser;

public class LoginActivity extends AuthenticatedFragmentActivity implements
		OnClickListener {

	// Debug
	private String TAG = "LoginActivity";

	private enum UserResult {
		FETCH_SUCCESS, CREATE_NEW_USER, NETWORK_FAIL, AUTHORIZATION_FAIL, NOT_FOUND, UNKNOWN

	};

	private boolean signinClicked;
	private boolean executingLaunch;
	private final int resolveConnectionFail = 5;

	/*
	 * ------------------- Override methods ----------------------- NOTES:
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		getActionBar().hide();

		setContentView(R.layout.activity_login);
		findViewById(R.id.sign_in_button).setOnClickListener(this);

	}

	@Override
	protected void onStart() {
		signinClicked = false;
		executingLaunch = false;
		super.onStart();

		if (apiClient.isConnecting()) {
			connectionProgress.show();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sign_in_button:
			signinClicked = true;
			if (!connectionProgress.isShowing()) {
				connectionProgress.show();
			}

			if (checkPlayServices()) {

				if (apiClient == null
						|| (!apiClient.isConnecting() && !apiClient
								.isConnected())) {

					if (getSharedPreferences(Constants.SHARED_PREFS,
							MODE_PRIVATE).getString(
							Constants.LOGGED_IN_ACCOUNT, null) == null) {
						Intent intent = AccountPicker.newChooseAccountIntent(
								null, null, new String[] { "com.google" },
								false, null, null, null, null);
						startActivityForResult(intent, REQUEST_ACCOUNT_PICKER);
					} else if (connectionResult == null) {
						// No connectioconnectn result, try to connect
						apiClient.connect();
					} else if (connectionResult.hasResolution()) {
						try {
							connectionResult.startResolutionForResult(this,
									REQUEST_CODE_RESOLVE_ERR);
						} catch (SendIntentException ex) {
							connectionResult = null;
							apiClient.connect();
						}

					}
				} else if (apiClient.isConnected() && !executingLaunch) {
					loadAndLaunch();
					// Login
				} else if (apiClient.isConnecting()
						&& !connectionProgress.isShowing() && executingLaunch) {
					connectionProgress.show();
				}
			}

			break;
		}

	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {

		try {
			if (signinClicked && result.hasResolution()) {
				result.startResolutionForResult(this, resolveConnectionFail);
			}
		} catch (SendIntentException e) {
			e.printStackTrace();
			Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT)
					.show();
		}

	}

	@Override
	public void onConnected(Bundle arg0) {
		loadAndLaunch(); // Once the API Client connects, try to launch.
	}

	/*
	 * ---------------------- My Methods ---------------------------- NOTES:
	 */

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {

		case resolveConnectionFail:
			if (resultCode != RESULT_OK) {
				signinClicked = false;

			} else if (!apiClient.isConnecting()) {
				apiClient.connect();

			}

			break;
		}

	}

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
				// TODO: display a dialog saying the device wont work out
				finish();
			}
			return false;
		}
		return true;
	}

	private void loadAndLaunch() {

		executingLaunch = true;
		new AsyncTask<Void, Void, ZeppaUser>() {

			private UserResult resultCode;

			@Override
			protected ZeppaUser doInBackground(Void... params) {

				resultCode = UserResult.UNKNOWN;
				ZeppaUser zeppaUser = null;

				// Build the authenticated user endpoint to interact with GAE
				Zeppauserendpoint.Builder endpointBuilder = new Zeppauserendpoint.Builder(
						AndroidHttp.newCompatibleTransport(),
						AndroidJsonFactory.getDefaultInstance(),
						getGoogleAccountCredential());
				endpointBuilder = CloudEndpointUtils
						.updateBuilder(endpointBuilder);
				Zeppauserendpoint userEndpoint = endpointBuilder.build();

				try {
					zeppaUser = userEndpoint.fetchZeppaUser().execute();

					if (zeppaUser == null) { // Should not get here
						resultCode = UserResult.CREATE_NEW_USER;

					} else {
						SharedPreferences.Editor editor = getSharedPreferences(
								Constants.SHARED_PREFS, MODE_PRIVATE).edit();
						editor.putLong(Constants.USER_ID, zeppaUser.getKey()
								.getId());
						editor.commit();

						resultCode = UserResult.FETCH_SUCCESS;
					}

				} catch (GoogleJsonResponseException ex) {
					ex.printStackTrace();
					if (ex.getStatusCode() == 404) {
						resultCode = UserResult.CREATE_NEW_USER;
					} else {
						resultCode = UserResult.UNKNOWN;
					}

					return null;
				} catch (IOException ioEx) {

					Log.d(TAG, "IOException caught");
					ioEx.printStackTrace();
					resultCode = UserResult.NETWORK_FAIL;

					return null;
				} catch (Exception ex) {
					resultCode = UserResult.UNKNOWN;
					return null;
				}

				return zeppaUser;
			}

			@Override
			protected void onPostExecute(ZeppaUser result) {
				super.onPostExecute(result);

				// Dismiss signin dialog
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
						Toast.makeText(LoginActivity.this, "Connection Error",
								Toast.LENGTH_SHORT).show();
						break;

					case 3: // UserResult.AUTHORIZATION_FAIL
						Toast.makeText(LoginActivity.this,
								"Authorization Error", Toast.LENGTH_SHORT)
								.show();
						break;

					case 4:
						// TODO: raise toast asking if this is an error or if a
						// new account should be made
						Toast.makeText(LoginActivity.this,
								"Saved Account Not Found", Toast.LENGTH_SHORT)
								.show();
						break;

					case 5: // UserResult.UNKNOWN
					default:
						Toast.makeText(LoginActivity.this, "Error Occured!!!",
								Toast.LENGTH_SHORT).show();

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
				executingLaunch = false;

			}

		}.execute();

	}
}
