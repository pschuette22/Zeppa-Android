package com.minook.zeppa.activity;

import java.util.Iterator;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.minook.zeppa.R;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.adapter.tagadapter.MinglerTagAdapter;
import com.minook.zeppa.mediator.DefaultUserInfoMediator;
import com.minook.zeppa.mediator.DefaultZeppaEventMediator;
import com.minook.zeppa.mediator.DefaultZeppaEventMediator.OnAttendanceChangeListener;
import com.minook.zeppa.runnable.FetchDefaultTagsForUserRunnable;
import com.minook.zeppa.runnable.ThreadManager;
import com.minook.zeppa.singleton.EventTagSingleton.OnTagLoadListener;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppanotificationendpoint.model.ZeppaNotification;

public class DefaultEventViewActivity extends AbstractEventViewActivity
		implements OnAttendanceChangeListener, OnTagLoadListener {

	private static final String TAG = DefaultEventViewActivity.class.getName();

	@Override
	protected void setHostMediator() {
		hostMediator = ZeppaUserSingleton.getInstance()
				.getAbstractUserMediatorById(eventMediator.getHostId());

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (eventMediator instanceof DefaultZeppaEventMediator) {
			setInvitesHolderVisibility();
			((DefaultZeppaEventMediator) eventMediator)
					.registerAttendanceStatusChangeListener(this);
		} else {
			Log.wtf(TAG, "Not an instanace of DefaultZeppaEventMediator");
		}

		findViewById(R.id.quickaction_join).setOnClickListener(this);
		findViewById(R.id.quickaction_text).setOnClickListener(this);
		findViewById(R.id.quickaction_watch).setOnClickListener(this);

		if (eventMediator.guestsMayInvite()) {
			sendInvitesHolder.setVisibility(View.VISIBLE);
			sendInvites.setOnClickListener(this);
		} else {
			sendInvitesHolder.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	protected void onStop() {
		super.onStop();
		if (eventMediator instanceof DefaultZeppaEventMediator) {
			((DefaultZeppaEventMediator) eventMediator)
					.unregisterAttendanceStatusChangeListener();
		}
	}

	@Override
	protected void setEventTagAdapter() {
		tagAdapter = new MinglerTagAdapter(this, tagHolder, hostMediator
				.getUserId().longValue(), eventMediator.getTagIds());

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		super.onConnected(connectionHint);

		ThreadManager.execute(new FetchDefaultTagsForUserRunnable(
				(ZeppaApplication) getApplication(),
				getGoogleAccountCredential(), ZeppaUserSingleton.getInstance()
						.getUserId().longValue(), hostMediator.getUserId()
						.longValue(), this));
	}

	@Override
	protected void setAttendingText() {
		if (eventMediator.getHasLoadedRelationships()) {
			// Has Loaded initial relationships
			List<Long> attendingUserIds = eventMediator.getAttendingUserIds();

			boolean attendingContainsCurrentUser = false;
			Iterator<Long> iterator = attendingUserIds.iterator();
			long currentUserId = ZeppaUserSingleton.getInstance().getUserId()
					.longValue();
			while (iterator.hasNext()) {
				if (iterator.next().longValue() == currentUserId) {
					attendingContainsCurrentUser = true;
					break;
				}
			}

			boolean isAttending = ((DefaultZeppaEventMediator) eventMediator)
					.isAttending();

			if (attendingUserIds.isEmpty() && !isAttending) {

				attending.setText("Be the first to join");

			} else {

				List<DefaultUserInfoMediator> attendingMinglerIds = ZeppaUserSingleton
						.getInstance().getMinglersFrom(attendingUserIds);

				if (isAttending) {
					if (attendingUserIds.size() == 0
							|| (attendingUserIds.size() == 1 && !attendingContainsCurrentUser)) {
						attending.setText("You joined first");
					} else {
						int attendingSize = attendingUserIds.size();
						if (attendingContainsCurrentUser) {
							attendingSize -= 1;
						}
						attending.setText("You mingle with "
								+ attendingMinglerIds.size() + "/"
								+ attendingSize + " other people going");
					}

				} else {

					if (attendingUserIds.size() == 0
							|| (attendingUserIds.size() == 1 && attendingContainsCurrentUser)) {
						attending.setText("Be the first to join");
					} else {
						int attendingSize = attendingUserIds.size();
						if (attendingContainsCurrentUser) {
							attendingSize -= 1;
						}
						attending.setText("You mingle with "
								+ attendingMinglerIds.size() + "/"
								+ attendingSize + " people going");
					}

				}

			}

		} else {
			attending.setText("Loading...");
		}
	}

	@Override
	public void onAttendanceChanged() {
		eventMediator.setConflictIndicator(this, conflictIndicator);
		setAttendingText();
		setInvitesHolderVisibility();

	}

	@Override
	public void onTagsLoaded() {
		tagAdapter.notifyDataSetChanged();
		tagAdapter.drawTags();

	}

	@Override
	public void onErrorLoadingTags() {
		Toast.makeText(this, "Error loading tags", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.quickaction_join:
			((DefaultZeppaEventMediator) eventMediator)
					.onJoinButtonClicked(this);
			onAttendanceChanged();
			eventMediator.convertQuickActionBar(this, barView);
			
			return;
		case R.id.quickaction_text:
			((DefaultZeppaEventMediator) eventMediator)
					.onTextButtonClicked(this);
			return;
		case R.id.quickaction_watch:
			((DefaultZeppaEventMediator) eventMediator)
					.onWatchButtonClicked(this);
			return;
		}

		super.onClick(v);
	}

	@Override
	public void onNotificationReceived(ZeppaNotification notification) {
		super.onNotificationReceived(notification);

		if (notification.getType().equalsIgnoreCase("EVENT_CANCELED")
				&& notification.getEventId().longValue() == eventMediator
						.getEventId().longValue()) {

			onBackPressed();
		}

	}

	private void setInvitesHolderVisibility() {

		if (((DefaultZeppaEventMediator) eventMediator).guestsMayInvite()
				&& ((DefaultZeppaEventMediator) eventMediator).isAttending()) {
			sendInvitesHolder.setVisibility(View.VISIBLE);
			sendInvites.setOnClickListener(this);
		} else {
			sendInvitesHolder.setVisibility(View.GONE);
		}
	}

}
