/**
 * 
 */
package com.minook.zeppa.mediator;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.CalendarContract.Instances;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.appspot.zeppa_cloud_1821.zeppaeventendpoint.model.ZeppaEvent;
import com.appspot.zeppa_cloud_1821.zeppaeventtouserrelationshipendpoint.model.ZeppaEventToUserRelationship;
import com.appspot.zeppa_cloud_1821.zeppausertouserrelationshipendpoint.model.ZeppaUserToUserRelationship;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.activity.DefaultEventViewActivity;
import com.minook.zeppa.runnable.ThreadManager;
import com.minook.zeppa.runnable.UpdateEventToUserRelationshipRunnable;
import com.minook.zeppa.singleton.EventTagSingleton;
import com.minook.zeppa.singleton.ZeppaEventSingleton;

import java.util.List;

/**
 * @author DrunkWithFunk21
 * 
 */
public class DefaultZeppaEventMediator extends AbstractZeppaEventMediator {

	private ZeppaEventToUserRelationship relationship; // null if non existent;
	private List<ZeppaUserToUserRelationship> minglerRelationships;
	private OnAttendanceChangeListener attendanceChangeListener;

	private static final String TAG = DefaultZeppaEventMediator.class.getName();

	public interface OnMinglerRelationshipsLoadedListener {
		public void onMinglerRelationshipsLoaded();

		public void onErrorLoadingMinglerRelationships();
	}

	public interface OnAttendanceChangeListener {
		public void onAttendanceChanged();
	}

	public DefaultZeppaEventMediator(ZeppaEvent event,
			ZeppaEventToUserRelationship relationship) {
		super(event);
		this.relationship = relationship;

	}

	/**
	 * This method converts the quick action bar for a user to interact with</p>
	 * It will display the user's status of
	 */
	@Override
	public View convertQuickActionBar(Context context, View barView) {

		barView.setVisibility(View.VISIBLE);

		CheckBox watchCheckBox = (CheckBox) barView
				.findViewById(R.id.quickaction_watch);
		watchCheckBox.setTag(this);

		TextView textButton = (TextView) barView
				.findViewById(R.id.quickaction_text);
		textButton.setTag(this);
		CheckBox joinCheckBox = (CheckBox) barView
				.findViewById(R.id.quickaction_join);
		joinCheckBox.setTag(this);

		AbstractZeppaUserMediator mediator = getHostMediator();

		StringBuilder builder = new StringBuilder();

		try {
			mediator.getPrimaryPhoneNumber();
			builder.append(context.getResources().getString(R.string.text));
		} catch (NullPointerException e) {
			builder.append(context.getResources().getString(R.string.email));
		}

		builder.append(" ");
		builder.append(mediator.getGivenName());
		textButton.setText(builder.toString());

		watchCheckBox.setChecked(isWatching());
		joinCheckBox.setChecked(isAttending());

		return barView;
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

		AbstractZeppaUserMediator hostMediator = getHostMediator();

		TextView hostName = (TextView) view
				.findViewById(R.id.eventview_hostname);

		hostName.setText(hostMediator.getDisplayName());
		ImageView hostImage = (ImageView) view
				.findViewById(R.id.eventview_hostimage);
		hostMediator.setImageWhenReady(hostImage);

	}

	@Override
	public void setConflictIndicator(Context context, ImageView image) {

		if (relationship.getIsAttending().booleanValue()) {
			conflictStatus = ConflictStatus.ATTENDING;
			setConflictImageDrawable(image);
			return;
		} else if (conflictStatus != ConflictStatus.UNKNOWN) {
			setConflictImageDrawable(image);
		} else {
			image.setVisibility(View.GONE);
		}

		new DetermineAndSetConflictStatus(context, image).execute();

	}

	private void setConflictImageDrawable(ImageView image) {
		switch (conflictStatus.ordinal()) {
		case 0:
			image.setImageResource(R.drawable.conflict_blue);
			break;
		case 1:
			image.setImageResource(R.drawable.conflict_green);
			break;
		case 2:
			image.setImageResource(R.drawable.conflict_yellow);
			break;
		case 3:
			image.setImageResource(R.drawable.conflict_red);
			break;
		default:
			image.setVisibility(View.GONE);
			return;
		}

		image.setVisibility(View.VISIBLE);
	}

	public List<DefaultEventTagMediator> getUsedTagMediators() {
		return EventTagSingleton.getInstance().getDefaultTagsFrom(getTagIds());
	}

	public List<ZeppaUserToUserRelationship> getMinglerRelationships() {
		return minglerRelationships;
	}

	public void registerAttendanceStatusChangeListener(
			OnAttendanceChangeListener attendanceStatusListener) {
		this.attendanceChangeListener = attendanceStatusListener;
	}

	public void unregisterAttendanceStatusChangeListener() {
		this.attendanceChangeListener = null;
	}

	/**
	 * Returns true if user is watching this event
	 * 
	 * @return
	 */
	public boolean isWatching() {
		try {

			return relationship.getIsAttending();
		} catch (NullPointerException e){
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Returns true if user is attending this event
	 * 
	 * @return
	 */
	public boolean isAttending() {

		try {
			return relationship.getIsAttending();

		} catch (NullPointerException e){
			e.printStackTrace();
			return false;
		}
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

	@Override
	public boolean isHostedByCurrentUser() {
		return false;
	}

	public void onWatchButtonClicked(AuthenticatedFragmentActivity activity) {
		relationship.setIsWatching(!isWatching());
		ThreadManager.execute(new UpdateEventToUserRelationshipRunnable(
				(ZeppaApplication) activity.getApplication(), activity
						.getGoogleAccountCredential(), relationship));

	}

	public void onTextButtonClicked(AuthenticatedFragmentActivity activity) {

		try {

			((DefaultUserInfoMediator) getHostMediator())
					.sendTextMessage(activity);

		} catch (Exception e) {
			e.printStackTrace();
			((DefaultUserInfoMediator) getHostMediator()).sendEmail(activity,
					null);

		}

	}

	public void onJoinButtonClicked(AuthenticatedFragmentActivity activity) {

		if (isAttending()) {
			relationship.setIsAttending(false);
			relationship.setIsWatching(false);
		} else {
			relationship.setIsAttending(true);
			relationship.setIsWatching(true);
		}

		
		ThreadManager.execute(new UpdateEventToUserRelationshipRunnable(
				(ZeppaApplication) activity.getApplication(), activity
						.getGoogleAccountCredential(), relationship));

		try {
			attendanceChangeListener.onAttendanceChanged();
		} catch (NullPointerException e) {
			// e.printStackTrace();
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
	public void updateRelationshipInAsync(ZeppaApplication application,
			GoogleAccountCredential credential,
			ZeppaEventToUserRelationship relationship) {

		this.relationship = relationship;
		ThreadManager.execute(new UpdateEventToUserRelationshipRunnable(
				application, credential, relationship));

	}

	/**
	 * Called when watch button is clicked to appropriately handle
	 */

	public void updateQuickActionBarUI(ViewParent parent) {

		ZeppaEventSingleton.getInstance().notifyObservers();

		if (parent instanceof ViewGroup) {

			ViewGroup group = (ViewGroup) parent;
			CheckBox watch = (CheckBox) group
					.findViewById(R.id.quickaction_watch);
			CheckBox join = (CheckBox) group
					.findViewById(R.id.quickaction_join);

			try {
				watch.setChecked(relationship.getIsWatching());
				join.setChecked(relationship.getIsAttending());

			} catch (NullPointerException e) {
				e.printStackTrace();
			}

		}

	}

	public class DetermineAndSetConflictStatus extends
			AsyncTask<Void, Void, ConflictStatus> {

		private Context context;

		private ImageView image;
		private final String[] PROJECTION = { Instances.EVENT_ID, // 0
				Instances.BEGIN, // 1
				Instances.END, // 2
				Instances.SELF_ATTENDEE_STATUS, // 3
				Instances.TITLE, // 4
				Instances.OWNER_ACCOUNT }; // 5

		public DetermineAndSetConflictStatus(Context context, ImageView image) {
			this.image = image;
			this.context = context;
		}

		@Override
		protected ConflictStatus doInBackground(Void... params) {
			ContentResolver resolver = context.getContentResolver();
			// String selection = Instances.STATUS + " != "
			// + Instances.STATUS_CANCELED;
			Cursor c = Instances.query(resolver, PROJECTION,
					getStartInMillis(), getEndInMillis());

			ConflictStatus status = ConflictStatus.NONE;

			long fiveMinBuffer = 5 * 60 * 1000;

			if (c.moveToFirst()) {
				long temp = getStartInMillis();
				Log.d(TAG, "Self attendee status = " + c.getInt(3)
						+ ", event name = " + c.getString(4));

				do {
					// if(c.getInt(3) == )

					String eventName = c.getString(4);
					// String ownerAccount = c.getString(5);
					long start = c.getLong(1);
					long end = c.getLong(2);

					if (eventName.equals(getTitle())
							&& event.getEnd().longValue() == end
							&& event.getStart().longValue() == start) {
						// Currently evaluating this item in cursor.
						// TODO: refresh calendar
					} else {

						long startWithBuffer = start - fiveMinBuffer;
						long endWithBuffer = end + fiveMinBuffer;

						long eventEnd = getEndInMillis();

						if (startWithBuffer > temp) {
							status = ConflictStatus.PARTIAL;
							break;
						} else if (endWithBuffer >= eventEnd) {
							status = ConflictStatus.COMPLETE;
							break;
						} else {
							status = ConflictStatus.PARTIAL;
							temp = end;
						}

					}
				} while (c.moveToNext());

				try {
					if (!c.isClosed()) {
						c.close();
					}
				} catch (Exception e) {
				}

				// There were no breaks in the schedule

			}

			return status;
		}

		@Override
		protected void onPostExecute(ConflictStatus result) {
			super.onPostExecute(result);

			if (result != null && !result.equals(ConflictStatus.UNKNOWN)) {
				conflictStatus = result;
				setConflictImageDrawable(image);
			}

		}

	}

	@Override
	public void launchIntoEventView(Context context) {
		Intent intent = new Intent(context, DefaultEventViewActivity.class);
		intent.putExtra(Constants.INTENT_ZEPPA_EVENT_ID, getEventId());
		context.startActivity(intent);
		if (context instanceof AuthenticatedFragmentActivity) {
			((AuthenticatedFragmentActivity) context)
					.overridePendingTransition(R.anim.slide_left_in,
							R.anim.slide_left_out);
		}
	}

	@Override
	public Intent getToEventViewIntent(Context context) {
		Intent intent = new Intent(context, DefaultEventViewActivity.class);
		intent.putExtra(Constants.INTENT_ZEPPA_EVENT_ID, getEventId());
		return intent;
	}

}
