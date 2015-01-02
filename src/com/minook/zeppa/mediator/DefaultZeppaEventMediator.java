/**
 * 
 */
package com.minook.zeppa.mediator;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.activity.DefaultEventViewActivity;
import com.minook.zeppa.activity.MinglerActivity;
import com.minook.zeppa.singleton.EventTagSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.Zeppaeventtouserrelationshipendpoint;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.model.ZeppaEventToUserRelationship;

/**
 * @author DrunkWithFunk21
 * 
 */
public class DefaultZeppaEventMediator extends AbstractZeppaEventMediator {

	private ZeppaEventToUserRelationship relationship; // null if non existent;

	public DefaultZeppaEventMediator(ZeppaEvent event,
			ZeppaEventToUserRelationship relationship) {
		super(event);
		this.relationship = relationship;

	}

	@Override
	public void convertEventListItemView(AuthenticatedFragmentActivity context,
			View convertView) {
		super.convertEventListItemView(context, convertView);
	}

	/**
	 * This method converts the quick action bar for a user to interact with</p>
	 * It will display the user's status of
	 */
	@Override
	public void convertQuickActionBar(View barView) {
		CheckBox watchCheckBox = (CheckBox) barView
				.findViewById(R.id.quickaction_watch);
		Button textButton = (Button) barView
				.findViewById(R.id.quickaction_text);
		CheckBox joinCheckBox = (CheckBox) barView
				.findViewById(R.id.quickaction_join);

		watchCheckBox.setChecked(isWatching());
		joinCheckBox.setChecked(isAttending());

		watchCheckBox.setOnClickListener(this);
		textButton.setOnClickListener(this);
		joinCheckBox.setOnClickListener(this);

	}

	/**
	 * 
	 * @return true if the relationship is not equal to null
	 */

	@Override
	public boolean isAgendaEvent() {
		return (isWatching() || isAttending());
	}

	@Override
	protected void setHostInfo(View view) {

		DefaultUserInfoMediator hostMediator = ZeppaUserSingleton.getInstance()
				.getDefaultUserMediatorById(event.getHostId());

		TextView hostName = (TextView) view
				.findViewById(R.id.eventview_hostname);

		hostName.setText(hostMediator.getDisplayName());
		ImageView hostImage = (ImageView) view
				.findViewById(R.id.eventview_hostimage);
		hostMediator.setImageWhenReady(hostImage);

		((LinearLayout) view.findViewById(R.id.eventview_hostinfo))
				.setOnClickListener(this);

	}

	public List<DefaultEventTagMediator> getUsedTagMediators() {
		return EventTagSingleton.getInstance().getDefaultTagsFrom(getTagIds());
	}

	/**
	 * Returns true if user is watching this event
	 * 
	 * @return
	 */
	public boolean isWatching() {
		if (relationship == null) {
			Log.wtf("TAG", "Null Relationship for mediator");
			return false;
		}

		return relationship.getIsWatching();
	}

	/**
	 * Returns true if user is attending this event
	 * 
	 * @return
	 */
	public boolean isAttending() {

		if (relationship == null) {
			Log.wtf("TAG", "Null Relationship for mediator");
			return false;
		}

		return relationship.getIsAttending();
	}

	/**
	 * This is decides if a given relationship represents the current one. This
	 * is to avoid loading in an event twice.
	 * 
	 * @param relationship
	 * @return
	 */
	public boolean relationshipDoesMatch(
			ZeppaEventToUserRelationship relationship) {
		return (this.relationship.getId().longValue() == relationship.getId()
				.longValue());
	}

	// public boolean didRepost(){
	// boolean didRepost = false;
	// if(repostManager != null && hostManager.getUserId()){
	// }
	// return didRepost;
	// }
	//
	// public MyZeppaEventManager getMyRepostManager(){
	// return myRepostManager;
	// }

	/**
	 * NOT THREAD SAFE</p> This method checks the time of the event vs other
	 * calendar events. It should be called when changes are made to the
	 * calendar
	 * 
	 * @param conflictIndicator
	 *            optional ImageView to update upon determining conflict status.
	 */
	protected void determineConflictStatusWithBlocking(Context context,
			ImageView conflictIndicator,
			GoogleAccountCredential calendarCredentail) {
		if (isAttending()) {
			conflictStatus = ConflictStatus.ATTENDING;
		} else {
//			conflictStatus = ConflictStatus.UNKNOWN;
//			Calendar calendarClient = new Calendar(
//					AndroidHttp.newCompatibleTransport(),
//					GsonFactory.getDefaultInstance(), calendarCredentail);
//			FreeBusyRequest request = new FreeBusyRequest();
//			request.setTimeMax(new DateTime(event.getEnd()));
//			request.setTimeMin(new DateTime(event.getStart()));
//
//			try {
//				FreeBusyResponse response = calendarClient.freebusy()
//						.query(request).execute();
//				Map<String, FreeBusyCalendar> calendars = response
//						.getCalendars();
//
//				if (calendars.isEmpty()) {
//					conflictStatus = ConflictStatus.NONE;
//				} else {
//					Iterator<FreeBusyCalendar> calIterator = calendars.values()
//							.iterator();
//					List<TimePeriod> busyTimes = new ArrayList<TimePeriod>();
//					while (calIterator.hasNext()) {
//						busyTimes.addAll(calIterator.next().getBusy());
//					}
//
//					Iterator<TimePeriod> timeIterator = busyTimes.iterator();
//					while (timeIterator.hasNext()) {
//
//					}
//
//				}
//
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.quickaction_watch: // Watch/ Unwatch

			onWatchButtonClicked(v);

			break;
		case R.id.quickaction_text: // Text host

			break;

		case R.id.quickaction_join: // Join/ Leave
			onJoinButtonClicked(v);
			break;

		case R.id.eventview_hostinfo:
			if (getContext() instanceof MinglerActivity)
				break;

			Intent hostIntent = new Intent(getContext(), MinglerActivity.class);
			hostIntent.putExtra(Constants.INTENT_ZEPPA_USER_ID, getHostId());
			getContext().startActivity(hostIntent);
			getContext().overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);

			break;

		case R.id.eventview:
			Intent eventIntent = new Intent(getContext(),
					DefaultEventViewActivity.class);
			eventIntent.putExtra(Constants.INTENT_ZEPPA_EVENT_ID, getEventId());
			getContext().startActivity(eventIntent);
			getContext().overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);
			break;
		}

	}

	/*
	 * These methods all have to do with updating the eventToUserRelationship.
	 * Always make calls through this to keep everything up to date
	 */

	/**
	 * This starts a thread to persist the current state of the relationship
	 * object
	 */
	private void updateRelationshipInAsync(
			ZeppaEventToUserRelationship originalState) {

		if (getContext().isConnected()) {

			Object[] params = { getGoogleAccountCredential(), relationship,
					originalState};

			new AsyncTask<Object, Void, ZeppaEventToUserRelationship>() {

				private ZeppaEventToUserRelationship originalState;

				@Override
				protected ZeppaEventToUserRelationship doInBackground(
						Object... params) {
					GoogleAccountCredential credential = (GoogleAccountCredential) params[0];
					ZeppaEventToUserRelationship relationship = (ZeppaEventToUserRelationship) params[1];
					this.originalState = (ZeppaEventToUserRelationship) params[2];

					try {

						Zeppaeventtouserrelationshipendpoint.Builder builder = new Zeppaeventtouserrelationshipendpoint.Builder(
								AndroidHttp.newCompatibleTransport(),
								GsonFactory.getDefaultInstance(), credential);
						builder = CloudEndpointUtils.updateBuilder(builder);
						Zeppaeventtouserrelationshipendpoint endpoint = builder
								.build();

						ZeppaEventToUserRelationship result = endpoint
								.updateZeppaEventToUserRelationship(
										relationship).execute();

						return result;
					} catch (IOException e) {
						e.printStackTrace();
					}

					return null;
				}

				@Override
				protected void onPostExecute(ZeppaEventToUserRelationship result) {
					super.onPostExecute(result);
					if (result != null) {
						// Yay, everything went well
					} else {
						// TODO: notify the user that something went wrong
						relationship = this.originalState;
					}
				}

			}.execute(params);

		} else {
			// TODO: throw an error saying to try again in a bit
		}

	}

	/**
	 * Called when watch button is clicked to appropriately handle
	 */
	protected void onWatchButtonClicked(View v) {
		ZeppaEventToUserRelationship original = relationship.clone();
		if (isWatching()) { // User is watching, stop watching
			relationship.setIsWatching(Boolean.FALSE);
		} else { // Start Watching
			relationship.setIsWatching(Boolean.TRUE);
		}

		updateRelationshipInAsync(original);
		// TODO: Update UI
	}

	protected void onJoinButtonClicked(View v) {
		ZeppaEventToUserRelationship original = relationship.clone();
		if (isAttending()) { // User is attending, leave
			relationship.setIsAttending(Boolean.FALSE);
			relationship.setIsWatching(Boolean.FALSE);

		} else {
			relationship.setIsAttending(Boolean.TRUE);
			relationship.setIsWatching(Boolean.TRUE);
		}

		updateRelationshipInAsync(original);
		// TODO: Update UI
	}

	protected void onTextButtonClicked(View v) {
		// TODO: Start private message with host
		// Determine if this device may send SMS
		// Determine if host has a phone number set
		// Send SMS if device can send and host has number set
		// Else, Open Email client and start email
	}

}
