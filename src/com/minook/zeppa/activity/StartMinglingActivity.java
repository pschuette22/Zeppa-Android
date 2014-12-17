package com.minook.zeppa.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.minook.zeppa.R;
import com.minook.zeppa.adapter.MinglerFinderAdapter;
import com.minook.zeppa.observer.OnLoadListener;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

public class StartMinglingActivity extends AuthenticatedFragmentActivity implements OnLoadListener{

	private final String TAG = getClass().getName();
	MinglerFinderAdapter adapter;

	private ListView listView;
	private View loaderView;

	/*
	 * -------------- Override Methods ---------------------
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		listView = new ListView(this);
		listView.setBackgroundResource(R.color.white);

		setContentView(listView);

		loaderView = getLayoutInflater().inflate(R.layout.view_loaderview, listView, false);
		((TextView) loaderView.findViewById(R.id.loaderview_text)).setText("Finding friends, one moment");
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		
	}

	@Override
	protected void onStart() {
		super.onStart();

		Log.d(TAG, "On Start, Registering new adapter");
		adapter = new MinglerFinderAdapter(this);
		listView.setAdapter(adapter);
		ZeppaUserSingleton.getInstance().registerWaitingFinderAdapter(adapter);
		
	}
	
	

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "On Stop, unregistering adapter");

		ZeppaUserSingleton.getInstance().unregisterWaitingFinderAdapter(adapter);
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
		
		if(didLoadInitial() && ZeppaUserSingleton.getInstance().getLastMinglerFinderTaskExecutionDate() == null){
			findMinglers();
		}
	}
	
	@Override
	public boolean didLoadInitial() {
		return ZeppaUserSingleton.getInstance().hasLoadedInitial();
	}

	@Override
	public void onFinishLoad() {
		if(listView.removeHeaderView(loaderView)){
			Log.d(TAG, "Removed header View");
		}
		
		adapter.notifyDataSetChanged();
		
	}
	
	private void findMinglers(){
		listView.addHeaderView(loaderView);
		ZeppaUserSingleton.getInstance().executeFindMinglerTask(this, getGoogleAccountCredential(), this);
	}

}
