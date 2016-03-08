package com.minook.zeppa.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.appspot.zeppa_cloud_1821.zeppaclientapi.Zeppaclientapi;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaUser;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaUserInfo;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ApiClientHelper;
import com.minook.zeppa.Constants;
import com.minook.zeppa.PrefsManager;
import com.minook.zeppa.R;
import com.minook.zeppa.Utils;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewAccountActivity extends AbstractAccountBaseActivity {

	private final String TAG = NewAccountActivity.class.getName();
	private boolean didSetInfo = false;

	@Override
	protected void onStart() {
		super.onStart();

		if (apiClient.isConnected()) {
			setInfo();
		}

//        else if(!didSetInfo){
//
//			connectionProgress.show();
//			connectionProgress.setCancelable(false);
//		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);

		switch (v.getId()) {
		case R.id.accountactivity_cancel:
			logout();
			break;

		case R.id.accountactivity_confirm:
			createUserAndLaunchIntoMain();
			break;

		}

	}

	@Override
	protected void setInfo() {

		didSetInfo = true;

		String accountEmail = PrefsManager.getLoggedInEmail(getApplication());

		if (accountEmail == null || accountEmail.isEmpty()) {
			Log.wtf("TAG", "In Account Activity without account specified");
			logout();
		} else {
			userGmail = accountEmail;
			emailField.setText(userGmail);
		}

		TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		String numberString = tMgr.getLine1Number();

		
		if (numberString == null) {
			numberField.setVisibility(View.GONE);
		} else {
			Log.d("TAG", "Number: " + numberString);
			userPhoneNumber = numberString;
			numberField.setText(Utils.formatPhoneNumber(userPhoneNumber));
		}

		Person currentPerson = Plus.PeopleApi.getCurrentPerson(apiClient);
		if (currentPerson == null) {
			Log.wtf(TAG, "current person is null, something failed");
		} else {
			givenName = currentPerson.getName().getGivenName();
			givenNameField.setText(givenName);
			familyName = currentPerson.getName().getFamilyName();
			familyNameField.setText(familyName);

			if (currentPerson.getImage().isDataValid()) {
				
				imageUrl = currentPerson.getImage().getUrl();
				setUserImage();
			}
		}


	}

	@Override
	public void onConnected(Bundle connectionHint) {
		super.onConnected(connectionHint);

		if (connectionProgress.isShowing()) {
			connectionProgress.dismiss();
		}

		if (!didSetInfo) {
			setInfo();
		}

	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		super.onConnectionFailed(result);
		Toast.makeText(this, "Error Occured", Toast.LENGTH_SHORT).show();
		logout();
	}

	private void createUserAndLaunchIntoMain() {

		ZeppaUser createdUser = createUserInstance();
		if (createdUser != null) {

			ProgressDialog progress = new ProgressDialog(this);
			progress.setTitle("Creating Account");
			progress.setIndeterminate(true);
			progress.setMessage("One Moment Please");
			progress.setCancelable(false);
			progress.show();

			Object[] params = { createdUser, progress };

			new AsyncTask<Object, Void, ZeppaUser>() {

				private ProgressDialog progress;

				@Override
				protected ZeppaUser doInBackground(Object... params) {
					ZeppaUser result = null;
					try {
						ZeppaUser createdUser = (ZeppaUser) params[0];
						progress = (ProgressDialog) params[1];
						GoogleAccountCredential credential = getGoogleAccountCredential();

						// Build the api endpoint class
						ApiClientHelper helper = new ApiClientHelper();
						Zeppaclientapi api = helper.buildClientEndpoint();

						// Post request to insert this object
						result = api
								.insertZeppaUser(credential.getToken(), createdUser).execute();

					} catch (IOException e) {
						e.printStackTrace();
						return null;
					} catch (GoogleAuthException authEx) {
						authEx.printStackTrace();
					}
					return result;
				}

				@Override
				protected void onPostExecute(ZeppaUser result) {
					super.onPostExecute(result);
					progress.dismiss();
					if (result != null) {

						ZeppaUserSingleton.getInstance().setUser(result);
						((ZeppaApplication) getApplication())
								.initialize(getGoogleAccountCredential());

						SharedPreferences.Editor editor = getSharedPreferences(
								Constants.SHARED_PREFS, MODE_PRIVATE).edit();
						editor.putLong(Constants.LOGGED_IN_USER_ID, result
								.getKey().getId());
						editor.commit();

						Intent launchMain = new Intent(getApplicationContext(),
								MainActivity.class);
						launchMain.putExtra(Constants.INTENT_NOTIFICATIONS,
								false);
						startActivity(launchMain);
						finish();

					} else {
						Toast.makeText(NewAccountActivity.this,
								"Error Occured", Toast.LENGTH_SHORT).show();
					}

				}

			}.execute(params);

		}

	}

	private ZeppaUser createUserInstance() {
		ZeppaUser zeppaUser = new ZeppaUser();
		ZeppaUserInfo userInfo = new ZeppaUserInfo();

		List<String> errorsList = new ArrayList<String>();

		String givenName = givenNameField.getText().toString().trim();
		if (givenName == null || givenName.isEmpty()) {
			errorsList.add(" - First Name Not Set");
		} else {
			userInfo.setGivenName(givenName);
		}

		String familyName = familyNameField.getText().toString().trim();
		if (familyName == null || familyName.isEmpty()) {
			errorsList.add(" - Last Name Not Set");
		} else {
			userInfo.setFamilyName(familyName);
		}

		userInfo.setImageUrl(imageUrl);

		if (errorsList.isEmpty()) {

			zeppaUser.setAuthEmail(userGmail);
			zeppaUser.setPhoneNumber(userPhoneNumber);


			zeppaUser.setUserInfo(userInfo);
			zeppaUser.setZeppaCalendarId("Temporary Value");

			return zeppaUser;

		} else {
			// TODO: raise errors dialog
			return null;
		}

	}

}
