package com.minook.zeppa.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.model.Calendar;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.utils.GCalUtils;
import com.minook.zeppa.zeppauserendpoint.Zeppauserendpoint;
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUser;
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUserInfo;

public class NewAccountActivity extends AbstractAccountBaseActivity {

	private final String TAG = "NewAccountActivity";
	private boolean setInfoOnConnect;

	@Override
	protected void onStart() {
		super.onStart();

		if (apiClient.isConnected()) {
			setInfoOnConnect = false;
			setInfo();
		} else {
			setInfoOnConnect = true;
			connectionProgress.show();
			connectionProgress
					.setOnCancelListener(new ProgressDialog.OnCancelListener() {

						@Override
						public void onCancel(DialogInterface dialog) {
							dialog.dismiss();
							logout();
						}
					});
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);

		switch (v.getId()) {
		case R.id.newuseractivity_cancel:
			logout();
			break;

		case R.id.newuseractivity_create:
			createUserAndLaunchIntoMain();
			break;

		}

	}

	@Override
	protected void setInfo() {
		super.setInfo();
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

			// CalendarList calendars = GCalU

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
						
						// Insert the Zeppa Calendar
						Calendar zeppaCalendar = GCalUtils.insertZeppaCalendar(NewAccountActivity.this, getGoogleCalendarCredential());
						createdUser.setZeppaCalendarId(zeppaCalendar.getId());
						
						Zeppauserendpoint.Builder endpointBuilder = new Zeppauserendpoint.Builder(
								AndroidHttp.newCompatibleTransport(),
								new JacksonFactory(), credential);
						endpointBuilder = CloudEndpointUtils
								.updateBuilder(endpointBuilder);
						Zeppauserendpoint endpoint = endpointBuilder.build();

						
						ZeppaUser result = endpoint.insertZeppaUser(createdUser).execute();
									
						
						
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

						((ZeppaApplication) getApplication()).initialize(
								result, getGoogleAccountCredential());

						SharedPreferences.Editor editor = getSharedPreferences(
								Constants.SHARED_PREFS, MODE_PRIVATE).edit();
						editor.putLong(Constants.USER_ID, result.getKey()
								.getId());
						editor.commit();

						Intent launchMain = new Intent(getApplicationContext(),
								MainActivity.class);
						launchMain.putExtra(Constants.INTENT_NOTIFICATIONS,
								false);
						startActivity(launchMain);
						finish();

					} else {
						Toast.makeText(NewAccountActivity.this, "Error Occured",
								Toast.LENGTH_SHORT).show();
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

		if (errorsList.isEmpty()) {

			userInfo.setImageUrl(imageUrl);

			userInfo.setGoogleAccountEmail(userGmail);
			userInfo.setPrimaryUnformatedNumber(userPhoneNumber);

			Person currentPerson = Plus.PeopleApi
					.getCurrentPerson(apiClient);
			String personId = currentPerson.getId();
			
			zeppaUser.setGoogleProfileId(personId);
			zeppaUser.setUserInfo(userInfo);

			return zeppaUser;

		} else {
			// TODO: raise errors dialog
			return null;
		}

	}
	
	

}
