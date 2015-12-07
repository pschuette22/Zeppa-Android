package com.minook.zeppa.mediator;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.EventComment;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaEvent;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaEventToUserRelationship;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.R;
import com.minook.zeppa.Utils;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.runnable.FetchEventCommentsRunnable;
import com.minook.zeppa.runnable.FetchEventToUserRelationshipsRunnable;
import com.minook.zeppa.runnable.ThreadManager;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractZeppaEventMediator implements
		Comparable<AbstractZeppaEventMediator> {

	/*
	 * This is the last context the event mediator was called in. Must call
	 * killContext() when moving to another activity
	 */

	public interface OnEventUpdateListener {
		public void onEventUpdate(ZeppaEvent event);

		public void onErrorUpdatingEvent();
	}

	public interface OnCommentLoadListener {
		public void onCommentsLoaded();

		public void onErrorLoadingComments();
	}

	public interface OnRelationshipsLoadedListener {
		public void onRelationshipsLoaded();

		public void onErrorLoadingRelationships();

	}

	public enum ConflictStatus {
		ATTENDING, NONE, PARTIAL, COMPLETE, UNKNOWN
	}

	// Event Object in question
	protected ZeppaEvent event;

	// Users' with attending relationships

	protected boolean hasLoadedRelationships;
	protected boolean isLoadingRelationships;
	protected List<ZeppaEventToUserRelationship> relationships;
	private OnRelationshipsLoadedListener relationshipListener;

	protected List<EventComment> comments;
	private OnCommentLoadListener commentLoadListener;

	protected long lastUpdateTimeInMillis;
	protected ConflictStatus conflictStatus;

	public AbstractZeppaEventMediator(ZeppaEvent event) {
		this.event = event;
		this.conflictStatus = ConflictStatus.UNKNOWN;
		this.relationships = new ArrayList<ZeppaEventToUserRelationship>();
		this.comments = new ArrayList<EventComment>();
		this.hasLoadedRelationships = false;
		this.isLoadingRelationships = false;

	}

	/*
	 * The Following section handles displaying the users calendar day as a
	 * fragment showing this event
	 */

	@Override
	public int compareTo(AbstractZeppaEventMediator another) {

		long compare = getEndInMillis().longValue()
				- another.getEndInMillis().longValue();

		return (int) compare;
	}

	/**
	 * Pass in a ConvertView for a ZeppaEvent and set elements of this view to
	 * reflect the given zeppa event
	 */
	public View convertEventListItemView(AuthenticatedFragmentActivity context,
			View convertView) {

		convertView.setTag(this);

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

		setConflictIndicator(context, conflictIndicator);

		setHostInfo(convertView);

		View quickActionBar = (View) convertView
				.findViewById(R.id.eventview_quickactionbar);
		convertQuickActionBar(context, quickActionBar);

		return convertView;

	}

	public abstract void launchIntoEventView(Context context);

	public abstract View convertQuickActionBar(Context context, View barView);

	public Long getEventId() {
		return event.getKey().getId();
	}

	public String getCalendarEventId() {
		return event.getGoogleCalendarEventId();
	}

	public Long getHostId() {
		return event.getHostId();
	}

	public Long getEndInMillis() {
		return event.getEnd();
	}

	public Long getStartInMillis() {
		return event.getStart();
	}

	public ConflictStatus getConflictStatus() {
		return conflictStatus;
	}

	public abstract boolean isHostedByCurrentUser();

	protected AbstractZeppaUserMediator getHostMediator() {
		AbstractZeppaUserMediator hostMediator = ZeppaUserSingleton
				.getInstance().getAbstractUserMediatorById(event.getHostId());
		return hostMediator;
	}

	public List<Long> getTagIds() {

		if (event.getTagIds() == null) {
			return new ArrayList<Long>();
		} else {
			return event.getTagIds();
		}

	}

	public List<ZeppaEventToUserRelationship> getEventRelationships() {
		return relationships;
	}

	public void setEventRelationships(
			List<ZeppaEventToUserRelationship> relationships) {
		this.hasLoadedRelationships = true;
		this.isLoadingRelationships = false;
		this.relationships = relationships;
		try {
			this.relationshipListener.onRelationshipsLoaded();
		} catch (NullPointerException e) {

		}
	}

	public void errorLoadingRelationships() {
		this.isLoadingRelationships = false;
		try {
			this.relationshipListener.onErrorLoadingRelationships();
		} catch (NullPointerException e) {

		}
	}

	public void registerOnRelationshipsLoadedListener(
			OnRelationshipsLoadedListener relationshipsListener) {
		this.relationshipListener = relationshipsListener;
	}

	public void unregisterOnRelationshipsLoadedListener(
			OnRelationshipsLoadedListener relationshipsListener) {
		if (this.relationshipListener == relationshipsListener) {
			this.relationshipListener = null;
		}
	}

	public boolean getHasLoadedRelationships() {
		return this.hasLoadedRelationships;
	}

	/*
	 * Getters and setters for comment objects
	 */

	public List<EventComment> getEventComments() {
		return this.comments;
	}

	public void setComments(List<EventComment> comments) {
		this.comments = comments;
	}

	public void addAllComments(List<EventComment> comments) {
		this.comments.addAll(comments);
	}

	public void addComment(EventComment comment) {
		this.comments.add(comment);
	}

	public void registerCommentLoadListener(
			OnCommentLoadListener commentLoadListener) {
		this.commentLoadListener = commentLoadListener;
	}

	public void unregisterCommentLoadListener() {
		this.commentLoadListener = null;
	}

	public void onCommentsLoaded() {
		if (commentLoadListener != null)
			this.commentLoadListener.onCommentsLoaded();

	}

	/**
	 * This Method Returns an array List of ID values for users attending this
	 * event
	 * 
	 * @return
	 */
	public List<Long> getAttendingUserIds() {

		List<Long> attendingUserIds = new ArrayList<Long>();
		if (!relationships.isEmpty()) {
			Iterator<ZeppaEventToUserRelationship> iterator = relationships
					.iterator();
			while (iterator.hasNext()) {
				ZeppaEventToUserRelationship relationship = iterator.next();
				if (relationship.getIsAttending().booleanValue()) {
					attendingUserIds.add(relationship.getUserId());
				}
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

	public abstract void setConflictIndicator(Context context, ImageView image);

	public void viewInCalendarApplication(Context context) {
		Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
		builder.appendPath("time");
		ContentUris.appendId(builder, getStartInMillis());
		Intent intent = new Intent(Intent.ACTION_VIEW).setData(builder.build());
		context.startActivity(intent);
	}

	public abstract Intent getToEventViewIntent(Context context);

	/**
	 * This Method loads
	 * 
	 * @param credential
	 * @return
	 */
	public void loadEventRelationships(ZeppaApplication application,
			GoogleAccountCredential credential,
			OnRelationshipsLoadedListener listener) {

		if (isLoadingRelationships) {
			return;
		}

		ThreadManager.execute(new FetchEventToUserRelationshipsRunnable(
				application, credential, getEventId().longValue(),
				ZeppaUserSingleton.getInstance().getUserId().longValue(),
				listener));

	}

	/**
	 *
	 * Execute a thread to load comments for this event
	 *
	 * @param application - application context
	 * @param credential - authorization to query the server
	 * @param listener - object to call back on when execution completes
	 *
	 */
	public void loadComments(ZeppaApplication application,
			GoogleAccountCredential credential, OnCommentLoadListener listener) {
		ThreadManager.execute(new FetchEventCommentsRunnable(application,
				credential, this, listener));
	}

}
