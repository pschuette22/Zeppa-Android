package com.minook.zeppa.activity;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
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
		implements OnMinglersLoadListener, OnRefreshListener {

	private final String TAG = getClass().getName();
	private MinglerFinderAdapter adapter;
	private ListView listView;
	private PullToRefreshLayout pullToRefreshLayout;
	private boolean isFetchingPossible;
	private AlertDialog explainDialog;
	

	/*
	 * -------------- Override Methods ---------------------
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.activity_startmingling);

		listView = (ListView) findViewById(R.id.startmingling_list);
		adapter = new MinglerFinderAdapter(this);
		listView.setAdapter(adapter);
		isFetchingPossible = false;

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle("Start Mingling");

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Finding People");
		builder.setMessage("This takes a bit, especially if you have a lot of contacts. So sit back, relax and enjoy the day");
		builder.setCancelable(true);
		builder.setPositiveButton("Dismiss",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

					}
				});
		explainDialog = builder.show();

		pullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.startmingling_ptr);

		ActionBarPullToRefresh.from(this)
				.options(Options.create().scrollDistance(.4f).build())
				.allChildrenArePullable().listener(this)
				.setup(pullToRefreshLayout);
	}

	@Override
	protected void onStart() {
		super.onStart();

		Log.d(TAG, "On Start, Registering new adapter");

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

		findMinglers();

	}

	private void findMinglers() {
		if (!isFetchingPossible && ZeppaUserSingleton.getInstance().hasLoadedInitial()) {

			isFetchingPossible = true;
			pullToRefreshLayout.setRefreshing(true);
			ZeppaUserSingleton.getInstance().executeFindMinglerTask(
					(ZeppaApplication) getApplication(),
					getGoogleAccountCredential(), adapter);
		}
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

		if (isFetchingPossible) {
			isFetchingPossible = false;
			try {
				pullToRefreshLayout.setRefreshing(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		adapter.notifyDataSetChanged();

		if(explainDialog.isShowing()){
			explainDialog.dismiss();
		}
		
	}

	@Override
	public void onRefreshStarted(View view) {
		if (isFetchingPossible) {
			return;
		} else {
			findMinglers();
		}

	}

}
