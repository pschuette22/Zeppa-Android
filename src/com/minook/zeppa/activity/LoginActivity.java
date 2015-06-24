package com.minook.zeppa.activity;

import java.io.IOException;

import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.minook.zeppa.Constants;
import com.minook.zeppa.PrefsManager;
import com.minook.zeppa.R;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.mediator.MyZeppaUserMediator;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

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
	private boolean launchIntoNotifications;

	/*
	 * ------------------- Override methods ----------------------- NOTES:
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().hide();

		setContentView(R.layout.activity_login);
		SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
		signInButton.setStyle(SignInButton.SIZE_WIDE, SignInButton.COLOR_DARK);
		signInButton.setOnClickListener(this);

		try {
			launchIntoNotifications = getIntent().getExtras().getBoolean(
					Constants.INTENT_NOTIFICATIONS);
		} catch (NullPointerException e) {
			launchIntoNotifications = false;
		}

	}

	@Override
	protected void onStart() {

		signinClicked = false;
		executingLaunch = false;
		super.onStart();

		
		if(Constants.LOCAL_RUN){
			// TODO: initialize for local run
		} else if (apiClient != null && apiClient.isConnecting()) {
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

					String loggedInAccount = PrefsManager
							.getLoggedInEmail(getApplication());
					if (loggedInAccount == null || loggedInAccount.isEmpty()) {
						Intent intent = AccountPicker.newChooseAccountIntent(
								null, null, new String[] { "com.google" },
								false, null, null, null, null);
						startActivityForResult(intent, REQUEST_ACCOUNT_PICKER);
					} else if (connectionResult == null) {
						// No connection result, try to connect
						if (Constants.LOCAL_RUN)
							loadAndLaunch();
						else
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

		if (result.hasResolution()) {

			try {
				result.startResolutionForResult(this, resolveConnectionFail);

			} catch (SendIntentException e) {
				e.printStackTrace();
				
				connectionProgress.dismiss();
				Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT)
						.show();
			}

		} else {
			connectionProgress.dismiss();

			Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT)
					.show();
			Log.d(TAG,
					"Connection Failed with Error Code: "
							+ result.getErrorCode());
		}

	}

	@Override
	public void onConnected(Bundle arg0) {
		String loggedInEmail = PrefsManager.getLoggedInEmail(getApplication());
		if ( loggedInEmail != null && !loggedInEmail.isEmpty()) {
			loadAndLaunch(); // Once the API Client connects, try to launch.
		}
	}
	
	
	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub
		super.onConnectionSuspended(cause);
		
		connectionProgress.dismiss();
		Toast.makeText(this, "Connection Issues", Toast.LENGTH_SHORT)
		.show();
		
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

			} else if (!Constants.LOCAL_RUN && !apiClient.isConnecting()) {
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

	/**
	 * Attempt to retrieve logged in user's ZeppaUser object and, if successful,
	 * launch into main activity
	 */
	private void loadAndLaunch() {

		if(executingLaunch){
			return;
		}
		
		executingLaunch = true;
		Object[] params = { getGoogleAccountCredential() };
		new AsyncTask<Object, Void, UserResult>() {

			@Override
			protected UserResult doInBackground(Object... params) {

				GoogleAccountCredential credential = (GoogleAccountCredential) params[0];
				UserResult resultCode = UserResult.UNKNOWN;

				try {

					MyZeppaUserMediator mediator = null;

					// try to fetch by logged in user id if held
					Long loggedInUserId = PrefsManager
							.getLoggedInUserId(getApplicationContext());
					if (loggedInUserId > 0) {
						mediator = ZeppaUserSingleton.getInstance()
								.fetchLoggedInUserByIdWithBlocking(credential,
										loggedInUserId);

					}

					// try to fetch user by authed email.
					if (mediator == null) {
						mediator = ZeppaUserSingleton.getInstance()
								.fetchLoggedInUserWithBlocking(credential);
					}

					if (mediator == null) {
						// Happens if user deleted account from another device
						resultCode = UserResult.CREATE_NEW_USER;
					} else {
						// Success
						resultCode = UserResult.FETCH_SUCCESS;
					}

				} catch (GoogleJsonResponseException ex) {
					ex.printStackTrace();
					if (ex.getStatusCode() == 404) {
						resultCode = UserResult.CREATE_NEW_USER;
					} else {
						resultCode = UserResult.UNKNOWN;
					}

				} catch (IOException ioEx) {

					Log.d(TAG, "IOException caught");
					ioEx.printStackTrace();
					resultCode = UserResult.NETWORK_FAIL;

				} catch (Exception ex) {
					resultCode = UserResult.UNKNOWN;
					ex.printStackTrace();
				}

				return resultCode;
			}

			@Override
			protected void onPostExecute(UserResult result) {
				super.onPostExecute(result);

				if (result != null) {

					switch (result.ordinal()) {
					case 0: // Fetch success, launch into app

						((ZeppaApplication) getApplication())
								.initialize(getGoogleAccountCredential());

						Intent launchMain = new Intent(getApplicationContext(),
								MainActivity.class);
						launchMain.putExtra(Constants.INTENT_NOTIFICATIONS,
								launchIntoNotifications);
						startActivity(launchMain);
						finish();
						break;

					case 1: // UserResult.CREATE_NEW_USER
						PrefsManager.setLoggedInAccountEmail(getApplication(),
								getGoogleAccountCredential()
										.getSelectedAccountName());

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

				} else {
					Log.wtf(TAG,
							"Shouldnt have got here. Error occured in startup protocol");
				}

				// Dismiss signin dialog
				connectionProgress.dismiss();
				

				executingLaunch = false;

			}

		}.execute(params);

	}

}
