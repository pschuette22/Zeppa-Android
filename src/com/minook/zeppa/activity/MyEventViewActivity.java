package com.minook.zeppa.activity;

import java.util.List;

import android.view.Menu;
import android.view.MenuItem;

import com.minook.zeppa.R;
import com.minook.zeppa.adapter.tagadapter.MyTagAdapter;
import com.minook.zeppa.singleton.EventTagSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

/**
 * This class is for displaying an event view activity for an event the host
 * owns
 * 
 * @author DrunkWithFunk21
 */
public class MyEventViewActivity extends AbstractEventViewActivity {

	/*
	 * Hold the originals just in case
	 */
	// private DefaultUserInfoMediator repostedFromUserInfoMediator;
	// private DefaultZeppaEventMediator repostedFromEventMediator;


	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.menu_event_hosted, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(super.onOptionsItemSelected(item)){
			return true;
		}

		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;

			/*
			 * Host Controls
			 */
		case R.id.menu_event_cancel:
			return true;
			
		case R.id.menu_event_seereposts:
			return true;

		}

		return false;

	}

	@Override
	protected void setHostMediator() {
		hostMediator = ZeppaUserSingleton.getInstance().getUserMediator();
	}

	@Override
	protected void setEventTagAdapter() {
		tagAdapter = new MyTagAdapter(this, tagHolder,
				eventMediator.getTagIds());

		if (!EventTagSingleton.getInstance().didLoadInitialTags()) {
			EventTagSingleton.getInstance().setWaitingAdapter(
					(MyTagAdapter) tagAdapter);
		}
		
		tagAdapter.drawTags();
	}

	@Override
	protected void setEventInfo() {
		super.setEventInfo();
		time.setText(eventMediator.getTimeString());
		location.setText(eventMediator.getDisplayLocation());
		setAttendingText();

	}

	@Override
	protected void setAttendingText() {
		if(eventMediator.getHasLoadedAttendingRelationship()){
			List<Long> attendingUserIds = eventMediator.getAttendingUserIds();
			if(attendingUserIds.isEmpty()){
				attending.setText("Nobody has joined yet");
			} else {
				attending.setText(attendingUserIds.size() + " people joined");
			}
			
		} else {
			attending.setText("Loading...");
		}

	}

	@Override
	protected void setConfliction() {
		conflictIndicator.setImageResource(R.drawable.conflict_blue);

	}




}
