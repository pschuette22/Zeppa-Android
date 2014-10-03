package com.minook.zeppa.activity;

import android.app.ActionBar;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.minook.zeppa.R;
import com.minook.zeppa.adapter.ContactFinderAdapter;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

public class NewFriendsActivity extends AuthenticatedFragmentActivity {

	private final String TAG = getClass().getName();
	ContactFinderAdapter adapter;

	private ListView listView;
	
	/*
	 * -------------- Override Methods ---------------------
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		listView = new ListView(this);
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

	
	// THIS IS A TEMPORARY METHOD THAT WILL BE REMOVED WHEN I INSERT DATA SYNC METHODS
	private void loadPossibleContacts() {


		Object[] params = { getApplicationContext() };

		new AsyncTask<Object, Void, Void>() {

			@Override
			protected Void doInBackground(Object... params){
				
				Context context = (Context) params[0];
				ZeppaUserSingleton.getInstance().loadPossible(context, getGoogleAccountCredential());

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);

				
				adapter.notifyDataSetChanged();
			}

		}.execute(params);
	}

	/*
	 * ---------------- Private Classes ----------------------
	 */

}
