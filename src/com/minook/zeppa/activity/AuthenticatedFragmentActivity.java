package com.minook.zeppa.activity;

import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.Constants;
import com.minook.zeppa.PrefsManager;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.runnable.RemoveCurrentDeviceRunnable;
import com.minook.zeppa.runnable.ThreadManager;
import com.minook.zeppa.singleton.EventTagSingleton;
import com.minook.zeppa.singleton.NotificationSingleton;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppanotificationendpoint.model.ZeppaNotification;

public class AuthenticatedFragmentActivity extends ActionBarActivity implements
		ConnectionCallbacks, OnConnectionFailedListener, OnClickListener {

	/*
	 * --------------- Intent Constants ----------------------
	 */

//	private final String TAG = "AuthenticatedFragmentActivity";

	protected ProgressDialog connectionProgress;
	protected GoogleApiClient apiClient;
	protected ConnectionResult connectionResult;

	// Request Codes
	protected final int REQUEST_ACCOUNT_PICKER = 3;
	protected final int REQUEST_RECOVER_AUTH = 4;
	protected final int REQUEST_CODE_RESOLVE_ERR = 8000;
	protected final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	protected enum Error {
		CONNECTION_ERROR, LOGIN_ERROR, AUTHENTICATION_ERROR,

	}

	/*
	 * --------------- Override Methods ----------------------
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		connectionProgress = new ProgressDialog(this);
		connectionProgress.setTitle("Signing in");
		connectionProgress.setMessage("One Moment Please");
		connectionProgress.setCanceledOnTouchOutside(false);
		connectionProgress.setCancelable(false);
	}

	@Override
	protected void onStart() {
		super.onStart();

		if (!(this instanceof LoginActivity)
				&& !(this instanceof NewAccountActivity)
				&& ZeppaUserSingleton.getInstance().getUserMediator() == null) {
			Intent toLogin = new Intent(this, LoginActivity.class);
			startActivity(toLogin);
			finish();
		}

		((ZeppaApplication) getApplication()).setCurrentActivity(this);
		String heldAccountName = PrefsManager.getLoggedInEmail(getApplication());

		if (heldAccountName != null && !heldAccountName.isEmpty()) {
			initializeApiClient(heldAccountName);
			apiClient.connect();
		}

	}

	@Override
	protected void onStop() {
		super.onStop();

		((ZeppaApplication) getApplication()).removeCurrentActivityIfMatching(this);

		if (apiClient != null && apiClient.isConnected()) {
			apiClient.disconnect();
		}

	}

	public void onNotificationReceived(ZeppaNotification notification) {
		Toast.makeText(
				this,
				NotificationSingleton.getInstance().getNotificationMessage(
						notification), Toast.LENGTH_SHORT).show();

		// Event Canceled
		if (NotificationSingleton.getInstance().getNotificationTypeOrder(
				notification) == 5) {
			ZeppaEventSingleton.getInstance().removeEventById(
					notification.getEventId());
			ZeppaEventSingleton.getInstance().notifyObservers();

		} else if (notification.getType().equalsIgnoreCase(
				"EVENT_RECOMMENDATION")
				|| notification.getType().equalsIgnoreCase("DIRECT_INVITE")) {
			ZeppaEventSingleton.getInstance().notifyObservers();
		}

	}

	/**
	 * This method builds an apiClient instance
	 * 
	 * @param accountName
	 */
	private void initializeApiClient(String accountName) {

		GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this,
				this, this);
		builder.setAccountName(accountName);
		builder.addApi(Plus.API);
		builder.addScope(Plus.SCOPE_PLUS_LOGIN);
		apiClient = builder.build();

	}

	public boolean isConnected() {
		if (apiClient == null || !apiClient.isConnected()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 
	 * @return credential used to access App Engine API
	 */
	public GoogleAccountCredential getGoogleAccountCredential() throws NullPointerException {

		GoogleAccountCredential credential = GoogleAccountCredential
				.usingAudience(this, Constants.ANDROID_AUDIENCE);

		String loggedInAccount = PrefsManager.getLoggedInEmail(getApplication());
		
		if(loggedInAccount == null){
			// App Should crash if activity wants a credential and user is not logged in
			throw new NullPointerException("No Logged In Account");
		}
		
		credential.setSelectedAccountName(loggedInAccount);

		return credential;
	}

	/**
	 * This method clears the current account address and launches Login
	 * Activity
	 */

	public void logout() {
		
		// Remove current device on the backend so notifications are not sent to it
		ThreadManager.execute(new RemoveCurrentDeviceRunnable(
				(ZeppaApplication) getApplication(),
				getGoogleAccountCredential()));
		
		// Restore all singleton instances.
		EventTagSingleton.getInstance().restore();
		NotificationSingleton.getInstance().restore();
		ZeppaEventSingleton.getInstance().restore();
		ZeppaUserSingleton.getInstance().restore();
		
		// Clear logged in prefs
		getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE).edit()
				.remove(Constants.LOGGED_IN_ACCOUNT).commit();
		getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE).edit()
				.remove(Constants.LOGGED_IN_USER_ID).commit();

		// Return to Login activity
		Intent toLogin = new Intent(this, LoginActivity.class);
		startActivity(toLogin);
		finish();
	}

	/*
	 * --------------- My Methods ----------------------
	 */

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case REQUEST_ACCOUNT_PICKER:
			if (resultCode == RESULT_OK) {
				String accountName = data
						.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
				PrefsManager.setLoggedInAccountEmail(getApplication(), accountName);
				initializeApiClient(accountName);

				connectionProgress.show();
				apiClient.connect();
			}
			break;

		case REQUEST_CODE_RESOLVE_ERR:

			if (resultCode == RESULT_OK) {
				connectionResult = null;
				apiClient.connect();
			}

			break;

		case REQUEST_RECOVER_AUTH:

			break;

		}

	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// Do Nothing
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// Do Nothing

	}

	@Override
	public void onConnectionSuspended(int cause) {
		// Do Nothing
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

}
