package com.minook.zeppa.activity;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.minook.zeppa.R;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.adapter.tagadapter.MyTagAdapter;
import com.minook.zeppa.mediator.MyZeppaEventMediator;
import com.minook.zeppa.runnable.RemoveEventRunnable;
import com.minook.zeppa.runnable.ThreadManager;
import com.minook.zeppa.singleton.EventTagSingleton;
import com.minook.zeppa.singleton.NotificationSingleton;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppanotificationendpoint.model.ZeppaNotification;

/**
 * This class is for displaying an event view activity for an event the host
 * owns
 * 
 * @author DrunkWithFunk21
 */
public class MyEventViewActivity extends AbstractEventViewActivity {

	private final static String TAG = MyEventViewActivity.class.getName();

	/*
	 * Hold the originals just in case
	 */
	// private DefaultUserInfoMediator repostedFromUserInfoMediator;
	// private DefaultZeppaEventMediator repostedFromEventMediator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sendInvites.setOnClickListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.menu_event_hosted, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (super.onOptionsItemSelected(item)) {
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
			raiseCancelDialog();
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
		if (eventMediator.getHasLoadedRelationships()) {
			List<Long> attendingUserIds = eventMediator.getAttendingUserIds();
			if (attendingUserIds.isEmpty()) {
				attending.setText("Nobody is going yet");
			} else if (attendingUserIds.size() == 1) {
				attending.setText("1 person is going");

			} else {
				attending
						.setText(attendingUserIds.size() + " people are going");
			}

		} else {
			attending.setText("Loading...");
		}

	}

	public void raiseCancelDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Cancel " + eventMediator.getTitle());
		builder.setMessage("Are you sure? This cannot be undone");

		AlertDialog.OnClickListener listener = new AlertDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == AlertDialog.BUTTON_POSITIVE) {
					cancelEvent();
				}
				dialog.dismiss();
			}

		};

		builder.setPositiveButton("Yes, Cancel", listener);
		builder.setNegativeButton("Nevermind", listener);
		builder.show();
	}

	private void cancelEvent() {
		if (eventMediator instanceof MyZeppaEventMediator) {

			ZeppaEventSingleton.getInstance().removeMediator(eventMediator);
			NotificationSingleton.getInstance().removeNotificationsForEvent(
					eventMediator.getEventId().longValue());
			ZeppaEventSingleton.getInstance().notifyObservers();
			ThreadManager.execute(new RemoveEventRunnable(
					(ZeppaApplication) getApplication(),
					getGoogleAccountCredential(), eventMediator.getEventId()
							.longValue()));
			onBackPressed();
		} else {
			Log.wtf(TAG, "Trying to delete unowned event");
		}
	}

	@Override
	public void onNotificationReceived(ZeppaNotification notification) {
		super.onNotificationReceived(notification);

		if (notification.getType().equals("USER_JOINED")
				|| notification.getType().equals("USER_LEFT")) {
			startFetchEventRelationshipsThread();
		}
	}

}
