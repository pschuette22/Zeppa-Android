package com.minook.zeppa.mediator;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.facade.calendar.CalendarController;
import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.utils.Utils;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.model.ZeppaEventToUserRelationship;

public abstract class AbstractZeppaEventMediator extends AbstractMediator
		implements OnClickListener {

	/*
	 * This is the last context the event mediator was called in. Must call
	 * killContext() when moving to another activity
	 */

	public enum ConflictStatus {
		ATTENDING, NONE, PARTIAL, COMPLETE, UNKNOWN
	}

	protected ZeppaEvent event;

	protected boolean hasLoadedRelationships;
	protected List<ZeppaEventToUserRelationship> relationships;

	protected long lastUpdateTimeInMillis;
	protected ConflictStatus conflictStatus;

	public AbstractZeppaEventMediator(ZeppaEvent event) {
		this.event = event;
		this.conflictStatus = ConflictStatus.UNKNOWN;
		this.lastUpdateTimeInMillis = System.currentTimeMillis();
		this.relationships = new ArrayList<ZeppaEventToUserRelationship>();
		this.hasLoadedRelationships = false;
		loadRelationshipsForEventInAsync();

	}

	/**
	 * Pass in a ConvertView for a ZeppaEvent and set elements of this view to
	 * reflect the given zeppa event
	 */
	public void convertEventListItemView(AuthenticatedFragmentActivity context,
			View convertView) {
		super.convertView(context);
		TextView title = (TextView) convertView
				.findViewById(R.id.eventview_eventtitle);
		TextView description = (TextView) convertView
				.findViewById(R.id.eventview_description);
		TextView eventTime = (TextView) convertView
				.findViewById(R.id.eventview_eventtime);

		TextView eventlocation = (TextView) convertView
				.findViewById(R.id.eventview_eventlocation);

		title.setText(event.getTitle());
		eventTime.setText(Utils.getDisplayDatesString(event.getStart()
				.longValue(), event.getEnd().longValue()));

		String location = getDisplayLocation();
		if (location == null) {
			eventlocation.setVisibility(View.GONE);
		} else {
			eventlocation.setText(location);
		}

		description.setText(event.getDescription());

		ImageView conflictIndicator = (ImageView) convertView
				.findViewById(R.id.eventview_conflictionindicator);

		setConflictIndicator(conflictIndicator);

		setHostInfo(convertView);

		
		View quickActionBar = (View) convertView.findViewById(R.id.eventview_quickactionbar);
		convertQuickActionBar(quickActionBar);
		
		convertView.setOnClickListener(this);

	}

	public abstract void convertQuickActionBar(View barView);

	public Long getEventId() {
		return event.getKey().getId();
	}

	public Long getHostId() {
		return event.getHostId();
	}

	public Long getRepostedFromEventId() {
		return event.getRepostedEventId();
	}

	public Long getOriginalEventId() {
		return event.getOriginalEventId();

	}
	
	public Long getEndInMillis() {
		return event.getEnd();
	}

	public List<Long> getTagIds() {

		if (event.getTagIds() == null) {
			return new ArrayList<Long>();
		} else {
			return event.getTagIds();
		}

	}

	public abstract boolean isAgendaEvent();

	public String getTitle() {
		return event.getTitle();
	}

	public String getDescription() {
		return event.getDescription();
	}

	public String getDisplayLocation() {

		if (event.getDisplayLocation() != null
				&& !event.getDisplayLocation().isEmpty()) {
			return event.getDisplayLocation();
		} else if (event.getMapsLocation() != null
				&& !event.getMapsLocation().isEmpty()) {
			return event.getMapsLocation();
		} else
			return null;

	}

	public String getMapsLocation() {
		String mapsLocation = event.getMapsLocation();
		if (mapsLocation == null) {
			mapsLocation = event.getDisplayLocation();
		}
		return mapsLocation;
	}

	public String getTimeString() {
		return Utils.getDisplayDatesString(event.getStart().longValue(), event
				.getEnd().longValue());
	}

	public boolean doesMatchEventId(long eventId) {
		return (eventId == event.getKey().getId().longValue());
	}

	public boolean hostIdDoesMatch(long hostId) {
		return (event.getHostId().longValue() == hostId);
	}

	public void update() {
		new UpdateEventTask().execute();
	}

	public boolean isPublicEvent() {
		return event.getPrivacy().equals("PUBLIC");
	}

	public boolean isPrivateEvent() {
		return event.getPrivacy().equals("PRIVATE");
	}

	public boolean eventIsOld() {
		long currentTime = System.currentTimeMillis();
		return (event.getEnd().longValue() <= currentTime);
	}

	private void getNSetViaInAsync(TextView viaText, Long viaUserId) {
		// if (viaUserId == null || viaUserId < 1) {
		// viaText.setVisibility(View.GONE);
		//
		// } else if (viaUserId.longValue() == ZeppaUserSingleton.getInstance()
		// .getUserId().longValue()) {
		//
		// } else {
		// viaText.setVisibility(View.VISIBLE);
		// viaText.setText("Loading...");
		// Object params = new Object[] { viaText, viaUserId };
		// new AsyncTask<Object, Void, ZeppaUser>() {
		// private TextView viaText;
		//
		// @Override
		// protected ZeppaUser doInBackground(Object... params) {
		// viaText = (TextView) params[0];
		// Long userId = (Long) params[1];
		// return ZeppaUserSingleton.getInstance().getUserById(userId);
		// }
		//
		// @Override
		// protected void onPostExecute(ZeppaUser result) {
		// super.onPostExecute(result);
		// // viaUser = result;
		// // if (result != null) {
		// // viaText.setText("via " + result.getGivenName() + " "
		// // + result.getFamilyName());
		// //
		// // viaText.setOnClickListener(new OnClickListener() {
		// //
		// // @Override
		// // public void onClick(View v) {
		// // Intent toViaIntent = new Intent(activity,
		// // UserActivity.class);
		// // toViaIntent.putExtra(
		// // Constants.INTENT_ZEPPA_USER_ID, viaUser
		// // .getKey().getId());
		// // }
		// // });
		// //
		// // }
		// }
		//
		// }.execute(params);
		// }

	}

	protected abstract void setHostInfo(View view);

	protected void setConflictIndicator(ImageView image) {
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
		case 4:
			image.setImageResource(R.drawable.conflict_red);
			break;
		}
	}

	private void loadRelationshipsForEventInAsync() {
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				return Boolean.valueOf(loadRelationships());
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);

				if (result) {

				} else {

				}
			}

		}.execute();
	}

	private boolean loadRelationships() {
		boolean success = false;

		return success;
	}

	public void raiseCalendarDialog() {
		CalendarController controller = CalendarController
				.getInstance(getContext());

	}

	protected class UpdateEventTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	private class CalendarDialog extends DialogFragment {

	}

}
