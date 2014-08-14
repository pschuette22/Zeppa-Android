package com.minook.zeppa.activity;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.minook.zeppa.R;
import com.minook.zeppa.adapter.ContactFinderAdapter;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

public class NewFriendsActivity extends AuthenticatedFragmentActivity {

	private final String TAG = getClass().getName();
	ContactFinderAdapter adapter;

	/*
	 * -------------- Override Methods ---------------------
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ListView listView = new ListView(this);
		listView.setBackgroundResource(R.color.white);

		setContentView(listView);

		adapter = new ContactFinderAdapter(this);
		listView.setAdapter(adapter);

		loadPossibleContacts();

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);

	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.hold, R.anim.slide_down_out);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;

		case R.id.action_refresh:
			loadPossibleContacts();
			break;

		}

		return true;
	}

	/*
	 * ---------------- Private Methods --------------------
	 */

	private void loadPossibleContacts() {

		ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("Finding Friends");
		progressDialog
				.setMessage(getResources().getString(R.string.one_moment));
		progressDialog.show();

		Object[] params = { getApplicationContext(), progressDialog };

		new AsyncTask<Object, Void, ProgressDialog>() {

			@Override
			protected ProgressDialog doInBackground(Object... params){
				
				long callTime = System.currentTimeMillis();
				Log.d(TAG, "call time: " + callTime);
				
				Context context = (Context) params[0];
				ZeppaUserSingleton.getInstance().loadPossible(context, getGoogleAccountCredential());

				long returnTime = System.currentTimeMillis();
				Log.d(TAG, "Return time: " + returnTime);
				return ((ProgressDialog) params[1]);
			}

			@Override
			protected void onPostExecute(ProgressDialog dialog) {
				super.onPostExecute(dialog);
				if (dialog.isShowing())
					dialog.dismiss();
				adapter.notifyDataSetChanged();
			}

		}.execute(params);
	}

	// private String makeValidNumber(String phoneNumber) {
	//
	// String result = "";
	//
	// if (phoneNumber.startsWith("+")) {
	// phoneNumber = phoneNumber.substring(1);
	// }
	// if (phoneNumber.startsWith("1")) {
	// phoneNumber = phoneNumber.substring(1);
	// }
	//
	// for (int i = 0; i < phoneNumber.length(); i++) {
	// char c = phoneNumber.charAt(i);
	// if (Character.isDigit(c)) {
	// result += c;
	// }
	// }
	//
	// if (result.length() == 10) {
	// result = "+1" + result;
	// } else {
	// result = null;
	// }
	//
	// return result;
	// }

	/*
	 * ---------------- Private Classes ----------------------
	 */

}
