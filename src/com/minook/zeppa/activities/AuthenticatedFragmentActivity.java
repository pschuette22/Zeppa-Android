package com.minook.zeppa.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.ZeppaApplication;

public class AuthenticatedFragmentActivity extends FragmentActivity {

	@Override
	protected void onResume() {
		super.onResume();
		if (!Constants.IS_CONNECTED) {
			return;
		}

		if (((ZeppaApplication) getApplication()).hasCredential()) {
			// Nothing To Worry About here
		} else {
			ProgressDialog progress = new ProgressDialog(this);
			progress.setTitle(R.string.signing_in);
			progress.setMessage(getResources().getString(R.string.one_moment));
			progress.show();
			SharedPreferences prefs = getSharedPreferences(
					Constants.SHARED_PREFS, MODE_PRIVATE);
			String emailAddress = prefs
					.getString(Constants.EMAIL_ADDRESS, null);
			if (emailAddress != null && !emailAddress.isEmpty()) {
				GoogleAccountCredential credential = GoogleAccountCredential
						.usingAudience(getApplicationContext(),
								Constants.APP_ENGINE_AUDIENCE_CODE);
				credential.setSelectedAccountName(emailAddress);
				((ZeppaApplication) getApplication())
						.setGoogleAccountCredential(credential);
				progress.dismiss();

			} else {
				progress.dismiss();
				launchIntoLogin();
			}

		}

	}

	/*
	 * --------------- My Methods ----------------------
	 */

	private void launchIntoLogin() {
		Intent login = new Intent(this, LoginActivity.class);
		startActivity(login);
		finish();
	}
	
	protected GoogleAccountCredential getCredential() {
		return ((ZeppaApplication) getApplication())
				.getGoogleAccountCredential();
	}

}
