package com.minook.zeppa.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.Constants;
import com.minook.zeppa.PrefsManager;
import com.minook.zeppa.R;
import com.minook.zeppa.Utils;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.runnable.SyncZeppaCalendarRunnable;
import com.minook.zeppa.runnable.ThreadManager;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppauserendpoint.Zeppauserendpoint;
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUser;
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUserInfo;

public class NewAccountActivity extends AbstractAccountBaseActivity {

	private final String TAG = NewAccountActivity.class.getName();
	private boolean setInfoOnConnect = false;

	@Override
	protected void onStart() {
		super.onStart();

		if (apiClient.isConnected()) {
			setInfo();
		} else {
			setInfoOnConnect = true;
			connectionProgress.show();
			connectionProgress.setCancelable(false);
		}
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
				loadAndSetImageInAsync(imageUrl);
			}
		}

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		super.onConnected(connectionHint);

		if (connectionProgress.isShowing()) {
			connectionProgress.dismiss();
		}

		if (setInfoOnConnect) {
			setInfoOnConnect = false;
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

					try {
						ZeppaUser createdUser = (ZeppaUser) params[0];
						progress = (ProgressDialog) params[1];
						GoogleAccountCredential credential = getGoogleAccountCredential();

						Zeppauserendpoint.Builder endpointBuilder = new Zeppauserendpoint.Builder(
								AndroidHttp.newCompatibleTransport(),
								new JacksonFactory(), credential);
						endpointBuilder = CloudEndpointUtils
								.updateBuilder(endpointBuilder);
						Zeppauserendpoint endpoint = endpointBuilder.build();

						ZeppaUser result = endpoint
								.insertZeppaUser(createdUser).execute();

						return result;
					} catch (IOException e) {
						e.printStackTrace();
						return null;
					}
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

			userInfo.setGoogleAccountEmail(userGmail);
			userInfo.setPrimaryUnformattedNumber(userPhoneNumber);

			Person currentPerson = Plus.PeopleApi.getCurrentPerson(apiClient);
			String personId = currentPerson.getId();

			zeppaUser.setGoogleProfileId(personId);
			zeppaUser.setUserInfo(userInfo);
			zeppaUser.setZeppaCalendarId("Temporary Value");

			return zeppaUser;

		} else {
			// TODO: raise errors dialog
			return null;
		}

	}

}
