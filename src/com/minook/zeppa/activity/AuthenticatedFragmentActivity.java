package com.minook.zeppa.activity;

import java.util.ArrayList;
import java.util.Iterator;
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
import com.minook.zeppa.mediator.AbstractMediator;

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

	private List<AbstractMediator> mediatorsWithContext;
	

	protected enum Error {
		CONNECTION_ERROR, LOGIN_ERROR, AUTHENTICATION_ERROR,

	}


	/*
	 * --------------- Override Methods ----------------------
	 */

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		connectionProgress = new ProgressDialog(this);
		connectionProgress.setTitle("Signing in");
		connectionProgress.setMessage("One Moment Please");
		mediatorsWithContext = new ArrayList<AbstractMediator>();
	}

	@Override
	protected void onStart() {
		super.onStart();

		String heldAccountName = getSharedPreferences(Constants.SHARED_PREFS,
				MODE_PRIVATE).getString(Constants.GOOGLE_ACCOUNT, null);

		if (heldAccountName != null && !heldAccountName.isEmpty()) {

			initializeApiClient(heldAccountName);
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


	/**
	 * Activity view is lost, kill held context
	 */
	@Override
	protected void onPause() {
		super.onPause();
		
		if (!mediatorsWithContext.isEmpty()) {
			Iterator<AbstractMediator> iterator = mediatorsWithContext
					.iterator();
			while (iterator.hasNext()) {
				iterator.next().killContextIfMatching(this);
			}

		}

	}


	/**
	 * This method builds an apiClient instance
	 * @param accountName
	 */
	private void initializeApiClient(String accountName) {
		
		GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this,this,this);
		builder.setAccountName(accountName);
		builder.addApi(Plus.API);
		builder.addScope(Plus.SCOPE_PLUS_LOGIN);
		apiClient = builder.build();
		
	}

	/**
	 * This method is so that a given activity knows that its context is held by
	 * a given mediator
	 * 
	 * @param mediator
	 * @return
	 */
	public boolean addHeldContext(AbstractMediator mediator) {
		boolean success = false;

		if (!mediatorsWithContext.contains(mediator)) {
			mediatorsWithContext.add(mediator);
			success = true;
		}

		return success;

	}

	public boolean removeHeldContext(AbstractMediator mediator) {
		return mediatorsWithContext.remove(mediator);
	}

	/**
	 * 
	 * @return credential activity's google credential
	 * @throws NullPointerException
	 *             if google credential is null
	 */
	public GoogleAccountCredential getGoogleAccountCredential()
			throws NullPointerException {
		String accountName = getSharedPreferences(Constants.SHARED_PREFS,
				MODE_PRIVATE).getString(Constants.GOOGLE_ACCOUNT, null);
		if (accountName == null) {
			logout();
		}
		GoogleAccountCredential credential = GoogleAccountCredential
				.usingAudience(this, Constants.ANDROID_AUDIENCE);
		credential.setSelectedAccountName(accountName);

		return credential;
	}

	/**
	 * This method clears the current account address and launches Login
	 * Activity
	 */

	public void logout() {
		getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE).edit()
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
						.edit()
						.putString(Constants.GOOGLE_ACCOUNT, accountName)
						.commit();
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

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// API Client is now connected

	}

	@Override
	public void onConnectionSuspended(int cause) {

	}

}
