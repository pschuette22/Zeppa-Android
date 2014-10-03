package com.minook.zeppa.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.adapter.tagadapter.AbstractTagAdapter;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator;
import com.minook.zeppa.mediator.AbstractZeppaUserMediator;
import com.minook.zeppa.singleton.ZeppaEventSingleton;

public abstract class AbstractEventViewActivity extends AuthenticatedFragmentActivity implements
		OnClickListener {

	final private String TAG = getClass().getName();

	protected AbstractZeppaEventMediator eventMediator;
	protected AbstractZeppaUserMediator hostMediator;
	protected AbstractTagAdapter tagAdapter;
	
	// UI Elements
	protected TextView title;
	protected ImageView conflictIndicator;

	protected TextView time;
	protected TextView location;
	protected TextView attending;

	protected TextView hostName;
	protected ImageView hostImage;
	protected TextView viaName;
	protected TextView description;

	protected EditText commentText;
	protected ImageView postComment;
	protected LinearLayout tagHolder;

	// Held Entities
	protected Long eventId;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_eventview);

		eventId = getIntent().getLongExtra(Constants.INTENT_ZEPPA_EVENT_ID, -1);

		if(eventId < 0){
			Toast.makeText(this, "Error Loading Event", Toast.LENGTH_SHORT).show();
			onBackPressed();
		}
		
		eventMediator = ZeppaEventSingleton.getInstance().getEventById(eventId);
		
		// UI Elements
		final ActionBar actionBar = getActionBar();

		title = (TextView) findViewById(R.id.eventactivity_eventtitle);
		conflictIndicator = (ImageView) findViewById(R.id.eventactivity_stateindicator);

		time = (TextView) findViewById(R.id.eventactivity_time);
		location = (TextView) findViewById(R.id.eventactivity_location);
		attending = (TextView) findViewById(R.id.eventactivity_attending);

		hostImage = (ImageView) findViewById(R.id.eventactivity_hostimage);
		hostName = (TextView) findViewById(R.id.eventactivity_hostname);
		description = (TextView) findViewById(R.id.eventactivity_description);

		commentText = (EditText) findViewById(R.id.eventactivity_commenttext);
		postComment = (ImageView) findViewById(R.id.eventactivity_postcomment);
		tagHolder = (LinearLayout) findViewById(R.id.eventactivity_tagholder);

		postComment.setOnClickListener(this);
		conflictIndicator.setOnClickListener(this);
		hostName.setOnClickListener(this);
		hostImage.setOnClickListener(this);
		
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		eventMediator.setContext(this);
		setEventInfo();
		setHostInfo();
		setEventTagAdapter();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.eventactivity_stateindicator:
			raiseCalendarDialog();
			break;

		}

	}


	/*
	 * -------------- My Methods -------------------
	 */

	protected void setEventInfo(){
		title.setText(eventMediator.getTitle());
		description.setText(eventMediator.getDescription());
		time.setText(eventMediator.getTimeString());
		location.setText(eventMediator.getDisplayLocation());
		setConfliction();
	}

	protected abstract void setHostMediator();
	protected abstract void setEventTagAdapter();
	
	protected void setHostInfo(){
		setHostMediator();
		hostMediator.setImageWhenReady(hostImage);
		hostName.setText(hostMediator.getDisplayName());
	}
	
	protected abstract void setAttendingText();
	
	private void raiseCalendarDialog(){
		eventMediator.raiseCalendarDialog();
	}

	protected abstract void setConfliction();
	
}
