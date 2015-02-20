package com.minook.zeppa.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.R;
import com.minook.zeppa.adapter.tagadapter.DeleteTagAdapter;
import com.minook.zeppa.mediator.MyZeppaUserMediator;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

public class EditAccountActivity extends AbstractAccountBaseActivity {

	private MyZeppaUserMediator myMediator;
	private DeleteTagAdapter deleteTagAdapter;
	private LinearLayout tagHolder;
	private boolean isUpdating;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		myMediator = ZeppaUserSingleton.getInstance().getUserMediator();
		tagHolder = (LinearLayout) findViewById(R.id.accountactivity_tagholder);
		deleteTagAdapter = new DeleteTagAdapter(this, tagHolder);
		isUpdating = false;

	}

	@Override
	protected void onResume() {
		super.onResume();
		setInfo();
		deleteTagAdapter.drawTags();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;

		}

		return false;
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);

		switch (v.getId()) {

		case R.id.accountactivity_cancel:
			onBackPressed();
			break;

		case R.id.accountactivity_confirm:
			updateAccount();
			break;

		}

	}

	@Override
	protected void setInfo() {
		givenNameField.setText(myMediator.getGivenName());
		familyNameField.setText(myMediator.getFamilyName());
		myMediator.setImageWhenReady(userImage);
		try {
			numberField.setText(myMediator.getPrimaryPhoneNumber());
		} catch (NullPointerException e) {
			e.printStackTrace();
			numberField.setVisibility(View.GONE);
		}
		emailField.setText(myMediator.getGmail());

	}

	private void updateAccount() {

		if (isConnected() && !isUpdating) {

			isUpdating = true;
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setTitle("Updating Profile");
			dialog.setMessage("One moment please...");
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			dialog.show();

			String givenName = givenNameField.getText().toString().trim();
			String familyName = familyNameField.getText().toString().trim();
			GoogleAccountCredential credential = getGoogleAccountCredential();

			Object[] params = { credential, givenName, familyName, dialog };
			new AsyncTask<Object, Void, Boolean>() {

				private ProgressDialog dialog;

				@Override
				protected Boolean doInBackground(Object... params) {
					GoogleAccountCredential credential = (GoogleAccountCredential) params[0];
					String givenName = (String) params[1];
					String familyName = (String) params[2];
					dialog = (ProgressDialog) params[3];

					return myMediator.updateUserInfoWithBlocking(credential,
							givenName, familyName, imageUrl, null);
				}

				@Override
				protected void onPostExecute(Boolean result) {
					super.onPostExecute(result);
					isUpdating = false;
					dialog.dismiss();

					if (result) {
						Toast.makeText(EditAccountActivity.this,
								"Updated Account", Toast.LENGTH_SHORT).show();
						
						
					} else {
						Toast.makeText(EditAccountActivity.this,
								"Didn't Update Properly", Toast.LENGTH_SHORT)
								.show();
					}

				}

			}.execute(params);

		} else {
			Toast.makeText(this, "Connecting... try again soon",
					Toast.LENGTH_SHORT).show();
		}
	}

}
