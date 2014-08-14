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

import com.minook.zeppa.Constants;
import com.minook.zeppa.R;

public abstract class AbstractEventViewActivity extends AuthenticatedFragmentActivity implements
		OnClickListener {

	final private String TAG = getClass().getName();

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

		// UI Elements
		final ActionBar actionBar = getActionBar();

		title = (TextView) findViewById(R.id.eventactivity_eventtitle);
		conflictIndicator = (ImageView) findViewById(R.id.eventactivity_stateindicator);

		time = (TextView) findViewById(R.id.eventactivity_time);
		location = (TextView) findViewById(R.id.eventactivity_location);
		attending = (TextView) findViewById(R.id.eventactivity_attending);

		hostImage = (ImageView) findViewById(R.id.eventactivity_hostimage);
		hostName = (TextView) findViewById(R.id.eventactivity_hostname);
		viaName = (TextView) findViewById(R.id.eventactivity_vianame);
		description = (TextView) findViewById(R.id.eventactivity_description);

		commentText = (EditText) findViewById(R.id.eventactivity_commenttext);
		postComment = (ImageView) findViewById(R.id.eventactivity_postcomment);
		tagHolder = (LinearLayout) findViewById(R.id.eventactivity_tagholder);

		postComment.setOnClickListener(this);
		
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;


		case R.id.menu_event_refresh:
			// TODO: call refresh method..
			return true;


		case R.id.menu_event_directions:
//			String location = (zeppaEvent.getExactLocation() != null ? zeppaEvent
//					.getExactLocation() : zeppaEvent.getShortLocation());
//			Intent toDirections = new Intent(
//					android.content.Intent.ACTION_VIEW,
//					Uri.parse("http://maps.google.com/maps?f=d&daddr="
//							+ location));
//			startActivity(toDirections);
			return true;


		}

		return true;

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
			// TODO: raise conflict dialog,
			break;

		}

	}


	/*
	 * -------------- My Methods -------------------
	 */

	protected abstract void setEventInfo();

	protected abstract void setHostInfo();

	protected abstract void setTags();


}
