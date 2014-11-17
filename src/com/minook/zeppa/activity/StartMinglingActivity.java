package com.minook.zeppa.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.minook.zeppa.R;
import com.minook.zeppa.adapter.MinglerFinderAdapter;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

public class StartMinglingActivity extends AuthenticatedFragmentActivity {

//	private final String TAG = getClass().getName();
	MinglerFinderAdapter adapter;

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

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		
	}

	@Override
	protected void onStart() {
		super.onStart();

		adapter = new MinglerFinderAdapter(this);
		listView.setAdapter(adapter);
		ZeppaUserSingleton.getInstance().registerWaitingFinderAdapter(adapter);
		
	}
	
	

	@Override
	protected void onStop() {
		super.onStop();
		
		ZeppaUserSingleton.getInstance().unregisterWaitingFinderAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		adapter.notifyDataSetChanged();

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
		
		if(ZeppaUserSingleton.getInstance().getLastMinglerFinderTaskExecutionDate() != null){
			ZeppaUserSingleton.getInstance().executeFindMinglerTask(this, getGoogleAccountCredential());
		}
	}
	
	

}
