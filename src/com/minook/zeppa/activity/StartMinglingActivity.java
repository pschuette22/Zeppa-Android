package com.minook.zeppa.activity;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.minook.zeppa.R;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.adapter.MinglerFinderAdapter;
import com.minook.zeppa.singleton.NotificationSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton.OnMinglersLoadListener;
import com.minook.zeppa.zeppanotificationendpoint.model.ZeppaNotification;

public class StartMinglingActivity extends AuthenticatedFragmentActivity
		implements OnMinglersLoadListener {

	private final String TAG = getClass().getName();
	private MinglerFinderAdapter adapter;
	private ListView listView;
	private View loaderView;
	private ProgressDialog dialog;

	/*
	 * -------------- Override Methods ---------------------
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		listView = new ListView(this);
		listView.setBackgroundResource(R.color.white);

		setContentView(listView);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle("Start Mingling");
		
		dialog = new ProgressDialog(this);
		dialog.setIndeterminate(true);
		dialog.setTitle("Finding Potential Minglers");
		dialog.setMessage("One moment please. This may take a minute or two...");
		dialog.setCancelable(false);
	}

	@Override
	protected void onStart() {
		super.onStart();

		Log.d(TAG, "On Start, Registering new adapter");
		adapter = new MinglerFinderAdapter(this);
		listView.setAdapter(adapter);
		ZeppaUserSingleton.getInstance().registerLoadListener(this);

	}

	@Override
	protected void onStop() {
		super.onStop();

		ZeppaUserSingleton.getInstance().unregisterMinglerLoadListener(this);
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

		}

		return true;
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		super.onConnected(connectionHint);

		if (ZeppaUserSingleton.getInstance()
				.getLastMinglerFinderTaskExecutionDate() == null
				&& ZeppaUserSingleton.getInstance().hasLoadedInitial()) {
			findMinglers();
		}

	}

	private void findMinglers() {
		dialog.show();
		ZeppaUserSingleton.getInstance().executeFindMinglerTask((ZeppaApplication) getApplication(), getGoogleAccountCredential());
	}

	@Override
	public void onNotificationReceived(ZeppaNotification notification) {

		int notificationType = NotificationSingleton.getInstance()
				.getNotificationTypeOrder(notification);
		if (notificationType == 0 || notificationType == 1) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					adapter.notifyDataSetChanged();
				}

			});
		}

		super.onNotificationReceived(notification);
	}

	@Override
	public void onMinglersLoaded() {
		if(dialog.isShowing()){
			dialog.dismiss();
		}

		adapter.notifyDataSetChanged();
		
	}

}
