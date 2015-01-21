package com.minook.zeppa.mediator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.R;
import com.minook.zeppa.Utils;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.eventcommentendpoint.model.EventComment;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.Zeppaeventtouserrelationshipendpoint;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.Zeppaeventtouserrelationshipendpoint.ListZeppaEventToUserRelationship;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.model.CollectionResponseZeppaEventToUserRelationship;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.model.ZeppaEventToUserRelationship;

public abstract class AbstractZeppaEventMediator extends AbstractMediator
		implements OnClickListener, Comparable<AbstractZeppaEventMediator> {

	/*
	 * This is the last context the event mediator was called in. Must call
	 * killContext() when moving to another activity
	 */

	public enum ConflictStatus {
		ATTENDING, NONE, PARTIAL, COMPLETE, UNKNOWN
	}

	// Event Object in question
	protected ZeppaEvent event;

	// Users' with attending relationships
	protected boolean hasLoadedRelationships;
	protected List<ZeppaEventToUserRelationship> attendingRelationships;


	private String commentCursor;
	protected List<EventComment> comments;
	
	protected long lastUpdateTimeInMillis;
	protected ConflictStatus conflictStatus;


	public AbstractZeppaEventMediator(ZeppaEvent event) {
		this.event = event;
		this.conflictStatus = ConflictStatus.UNKNOWN;
		this.lastUpdateTimeInMillis = System.currentTimeMillis();
		
		this.attendingRelationships = new ArrayList<ZeppaEventToUserRelationship>();
		this.hasLoadedRelationships = false;
		this.comments = new ArrayList<EventComment>();
		this.commentCursor = null;

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

		View quickActionBar = (View) convertView
				.findViewById(R.id.eventview_quickactionbar);
		convertQuickActionBar(quickActionBar);

		View container = convertView.findViewById(R.id.eventview);
		container.setOnClickListener(this);

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

	public boolean getHasLoadedAttendingRelationship() {
		return hasLoadedRelationships;
	}
	
	public abstract boolean isHostedByCurrentUser();

	public List<Long> getTagIds() {

		if (event.getTagIds() == null) {
			return new ArrayList<Long>();
		} else {
			return event.getTagIds();
		}

	}
	
	/*
	 * Getters and setters for comment objects
	 * 
	 */
	public String getCommentCursor(){
		return this.commentCursor;
	}
	
	public void setCommentCursor(String commentCursor){
		this.commentCursor = commentCursor;
	}
	
	public List<EventComment> getEventComments(){
		return this.comments;
	}
	
	public void addAllComments(List<EventComment> comments){
		this.comments.addAll(comments);
	}
	
	public void setEventComments(List<EventComment> comments){
		this.comments = comments;
	}
	
	
	
	/**
	 * This Method Returns an array List of ID values for users attending this event
	 * @return
	 */
	public List<Long> getAttendingUserIds() {

		List<Long> attendingUserIds = new ArrayList<Long>();
		if (!attendingRelationships.isEmpty()) {
			Iterator<ZeppaEventToUserRelationship> iterator = attendingRelationships
					.iterator();
			while(iterator.hasNext()){
				attendingUserIds.add(iterator.next().getUserId());
			}
		}

		return attendingUserIds;

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

	public boolean guestsMayInvite() {
		return event.getGuestsMayInvite();
	}

	public boolean eventIsOld() {
		long currentTime = System.currentTimeMillis();
		return (event.getEnd().longValue() <= currentTime);
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
		default:
			image.setVisibility(View.GONE);
			return;
		}

		image.setVisibility(View.VISIBLE);

	}
	
	/**
	 * This Method loads
	 * 
	 * @param credentail
	 * @return
	 */
	public boolean loadAttendingRelationshipsWithBlocking(
			GoogleAccountCredential credentail) {
		boolean success = true;
		Zeppaeventtouserrelationshipendpoint.Builder builder = new Zeppaeventtouserrelationshipendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				GsonFactory.getDefaultInstance(), credentail);
		CloudEndpointUtils.updateBuilder(builder);
		Zeppaeventtouserrelationshipendpoint endpoint = builder.build();

		String filter = "eventId == " + event.getId().longValue()
				+ " && isAttending == " + true + " && userId != "
				+ ZeppaUserSingleton.getInstance().getUserId().longValue();
		String cursor = null;
		Integer limit = Integer.valueOf(30);

		List<ZeppaEventToUserRelationship> loadedRelationships = new ArrayList<ZeppaEventToUserRelationship>();
		do {
			try {
				ListZeppaEventToUserRelationship listRelationshipsTask = endpoint
						.listZeppaEventToUserRelationship();
				listRelationshipsTask.setFilter(filter);
				listRelationshipsTask.setCursor(cursor);
				listRelationshipsTask.setLimit(limit);

				CollectionResponseZeppaEventToUserRelationship response = listRelationshipsTask
						.execute();

				if (response != null && response.getItems() != null
						&& !response.getItems().isEmpty()) {
					loadedRelationships.addAll(response.getItems());
					cursor = response.getNextPageToken();

				} else {
					cursor = null;
					break;
				}

			} catch (IOException e) {
				e.printStackTrace();
				cursor = null;
				break;
			}

		} while (cursor != null);

		if ((success || attendingRelationships.isEmpty())) {
			attendingRelationships = loadedRelationships;
		}

		hasLoadedRelationships = true;
		return success;
	}
	
	
	protected class UpdateEventTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return null;
		}

	}
	

	/*
	 * The Following section handles displaying the users calendar day as a fragment showing this event
	 */
	
	public void raiseCalendarDialog() {
//		CalendarController controller = CalendarController
//				.getInstance(getContext());
//		controller.setTime(event.getStart());
//		controller.setViewType(ViewType.DAY);

	}

	private class CalendarDialog extends DialogFragment {

	}


	@Override
	public int compareTo(AbstractZeppaEventMediator another) {

		long compare = getEndInMillis().longValue() - another.getEndInMillis().longValue();
		
		return (int) compare;
	}
	

	
	

}
