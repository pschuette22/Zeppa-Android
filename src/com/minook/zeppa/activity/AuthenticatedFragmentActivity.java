package com.minook.zeppa.activity;

import java.util.ArrayList;
import java.util.List;

import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.Constants;

public class AuthenticatedFragmentActivity extends FragmentActivity implements
		ConnectionCallbacks, OnConnectionFailedListener {

	/*
	 * --------------- Intent Constants ----------------------
	 */

	private final String TAG = "AuthenticatedFragmentActivity";

	protected ProgressDialog connectionProgress;
	protected GoogleApiClient apiClient;
	protected ConnectionResult connectionResult;

	// Request Codes
	protected final int REQUEST_ACCOUNT_PICKER = 3;
	protected final int REQUEST_RECOVER_AUTH = 4;
	protected final int REQUEST_CODE_RESOLVE_ERR = 8000;
	protected final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	protected List<Intent> waitingOnCredential;

	protected enum Error {
		CONNECTION_ERROR, LOGIN_ERROR, AUTHENTICATION_ERROR,

	}
	
	private String accessToken;
	private String refreshToken;

	/*
	 * --------------- Override Methods ----------------------
	 */

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		waitingOnCredential = new ArrayList<Intent>();
		connectionProgress = new ProgressDialog(this);
		connectionProgress.setTitle("Signing in");
		connectionProgress
				.setMessage("Look up and enjoy the day while you wait");

	}

	@Override
	protected void onStart() {
		super.onStart();

		String heldAccountName = getSharedPreferences(Constants.SHARED_PREFS,
				MODE_PRIVATE).getString(Constants.GOOGLE_ACCOUNT, null);
		
		if (heldAccountName != null && !heldAccountName.isEmpty()) {
			
			buildApiClient(heldAccountName);
			apiClient.connect();

		}

		
	}

	@Override
	protected void onStop() {

		if (apiClient != null && apiClient.isConnected()) {
			apiClient.disconnect();
		}

		super.onStop();
	}
	
	
	private void buildApiClient(String accountName){
		apiClient = new GoogleApiClient.Builder(this, this, this)
		.setAccountName(accountName)
		.addApi(Plus.API, null) // Plus API
//		.addScope(com.google.api.services.calendar.CalendarScopes.CALENDAR) // Calendar API
		.addScope(Plus.SCOPE_PLUS_LOGIN).build(); // Plus scope for login
		
	}
	

	/**
	 * 
	 * @return credential				activity's google credential
	 * @throws NullPointerException 	if google credential is null
	 */
	public GoogleAccountCredential getGoogleAccountCredential() throws NullPointerException{
		String accountName = getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE).getString(Constants.GOOGLE_ACCOUNT, null);
		if(accountName == null){
			throw new NullPointerException("AccountName is Null");
		}
		GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(getApplicationContext(), Constants.APP_ENGINE_AUDIENCE_CODE);
		credential.setSelectedAccountName(accountName);
		
		return credential;
	}

//	/**
//	 * This method is for retrieving GoogleCredential
//	 * 
//	 * @return GoogleCredential	
//	 * @throws IOException
//	 */
//	public GoogleCredential getCredentialWithBlocking() throws IOException {
//		if (credential == null) {
//			buildCredential();
//		} else if (credential.getExpiresInSeconds() == 0) {
//			credential.refreshToken();
//		}
//		return credential;
//	}
//
//	/**
//	 * This method builds the GoogleCredential from client id and secret
//	 */
//	private void buildCredential() {
//		
//		GoogleCredential.Builder builder = new GoogleCredential.Builder();
//		builder.setClientSecrets(Constants.APP_ENGINE_CLIENT_ID,
//				Constants.APP_ENGINE_CLIENT_SECRET);
//		builder.setTransport(AndroidHttp.newCompatibleTransport());
//		builder.setJsonFactory(AndroidJsonFactory.getDefaultInstance());
//		credential = builder.build();
//		
//	}
	
	/**
	 * This method clears the current account address and launches Login Activity
	 */
	
	public void logout(){
		getSharedPreferences(Constants.SHARED_PREFS,
				MODE_PRIVATE).edit()
				.remove(Constants.GOOGLE_ACCOUNT).commit();
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
				getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE)
						.edit().putString(Constants.GOOGLE_ACCOUNT, accountName)
						.commit();
				buildApiClient(accountName);
				
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
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// API Client is now connected

	}

	@Override
	public void onConnectionSuspended(int cause) {

	}

}
