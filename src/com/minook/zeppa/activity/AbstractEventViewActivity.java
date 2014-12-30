package com.minook.zeppa.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.adapter.EventCommentAdapter;
import com.minook.zeppa.adapter.tagadapter.AbstractTagAdapter;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator;
import com.minook.zeppa.mediator.AbstractZeppaUserMediator;
import com.minook.zeppa.singleton.ZeppaEventSingleton;

public abstract class AbstractEventViewActivity extends
		AuthenticatedFragmentActivity implements OnClickListener {

	final private String TAG = getClass().getName();

	protected AbstractZeppaEventMediator eventMediator;
	protected AbstractZeppaUserMediator hostMediator;
	protected AbstractTagAdapter tagAdapter;
	protected EventCommentAdapter commentAdapter;

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
	protected Button postComment;
	protected LinearLayout tagHolder;

	// Held Entities

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_eventview);

		Long eventId = getIntent().getLongExtra(
				Constants.INTENT_ZEPPA_EVENT_ID, -1);

		if (eventId < 0) {
			Toast.makeText(this, "Event Not Specified", Toast.LENGTH_SHORT)
					.show();
			onBackPressed();
		}

		eventMediator = ZeppaEventSingleton.getInstance().getEventById(eventId);

		// UI Elements
		final ActionBar actionBar = getActionBar();
		actionBar.setTitle(R.string.title_details);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		title = (TextView) findViewById(R.id.eventactivity_eventtitle);
		conflictIndicator = (ImageView) findViewById(R.id.eventactivity_stateindicator);

		time = (TextView) findViewById(R.id.eventactivity_time);
		location = (TextView) findViewById(R.id.eventactivity_location);
		attending = (TextView) findViewById(R.id.eventactivity_attending);
		
		hostImage = (ImageView) findViewById(R.id.eventactivity_hostimage);
		hostName = (TextView) findViewById(R.id.eventactivity_hostname);
		description = (TextView) findViewById(R.id.eventactivity_description);

		commentText = (EditText) findViewById(R.id.eventactivity_commenttext);
		postComment = (Button) findViewById(R.id.eventactivity_postcomment);
		tagHolder = (LinearLayout) findViewById(R.id.eventactivity_tagholder);

		postComment.setOnClickListener(this);
		conflictIndicator.setOnClickListener(this);
		
		View barView = findViewById(R.id.eventactivity_quickactionbar);
		eventMediator.convertQuickActionBar(barView);

	}

	@Override
	protected void onStart() {
		super.onStart();

		eventMediator.setContext(this);
		setEventInfo();
		setHostInfo();
		setEventTagAdapter();
		setAttendingText();
	}
	
	
	@Override
	public void onConnected(Bundle connectionHint) {
		super.onConnected(connectionHint);
		fetchAttendingUserRelationshipsInAsync();

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
			
		case R.id.eventactivity_location:
			startNavigation();
			break;
			
		case R.id.eventactivity_time:
			raiseCalendarDialog();
			break;

		}

	}

	/*
	 * -------------- My Methods -------------------
	 */

	protected void setEventInfo() {
		title.setText(eventMediator.getTitle());
		description.setText(eventMediator.getDescription());
		time.setText(eventMediator.getTimeString());
		location.setText(eventMediator.getDisplayLocation());
		setConfliction();
		setEventTagAdapter();
	}
	
	protected void startNavigation() {
		String location = eventMediator.getMapsLocation();
		
		if(location == null){
			Toast.makeText(this, "Address Not Set", Toast.LENGTH_SHORT).show();
		}
		
		Intent toDirections = new Intent(
				android.content.Intent.ACTION_VIEW,
				Uri.parse("http://maps.google.com/maps?f=d&daddr="
						+ location));
		startActivity(toDirections);
	}

	protected abstract void setHostMediator();

	protected abstract void setEventTagAdapter();

	protected void setHostInfo() {
		setHostMediator();
		hostMediator.setImageWhenReady(hostImage);
		hostName.setText(hostMediator.getDisplayName());
	}

	protected abstract void setAttendingText();

	private void raiseCalendarDialog() {
		eventMediator.raiseCalendarDialog();
	}

	protected abstract void setConfliction();

	protected void fetchAttendingUserRelationshipsInAsync(){
		
		Object[] params = {getGoogleAccountCredential(), eventMediator};
		
		new AsyncTask<Object, Void, Boolean>(){

			@Override
			protected Boolean doInBackground(Object... params) {
				GoogleAccountCredential credentail = (GoogleAccountCredential) params[0];
				AbstractZeppaEventMediator mediator = (AbstractZeppaEventMediator) params[1];
				return mediator.loadAttendingRelationshipsWithBlocking(credentail);
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if(result){
					setAttendingText();
				} else {
					attending.setText("Load Error");
				}
			}
			
			
			
		}.execute(params);
		
	}

}
