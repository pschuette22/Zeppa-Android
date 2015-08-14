package com.minook.zeppa.singleton;

/**
 * Calendar Singleton. Handles all Google Calendar Transactions
 * 
 * @author Pete Schuette 
 * Date Created: Thursday March 6th, 2014;
 * 
 */

import com.appspot.zeppa_cloud_1821.zeppaeventendpoint.Zeppaeventendpoint;
import com.appspot.zeppa_cloud_1821.zeppaeventendpoint.model.ZeppaEvent;
import com.appspot.zeppa_cloud_1821.zeppaeventtouserrelationshipendpoint.Zeppaeventtouserrelationshipendpoint;
import com.appspot.zeppa_cloud_1821.zeppaeventtouserrelationshipendpoint.model.ZeppaEventToUserRelationship;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAuthIOException;
import com.google.api.client.json.gson.GsonFactory;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator;
import com.minook.zeppa.mediator.DefaultZeppaEventMediator;
import com.minook.zeppa.runnable.FetchMoreEventsRunnable;
import com.minook.zeppa.runnable.FetchNewEventsRunnable;
import com.minook.zeppa.runnable.ThreadManager;
import com.pschuette.android.calendarlibrary.Event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Delayed;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ZeppaEventSingleton {

	public interface OnZeppaEventLoadListener {
		public void onZeppaEventsLoaded();
	}




	// private final String TAG = ZeppaEventSingleton.class.getName();

	private static ZeppaEventSingleton singleton;

	private List<AbstractZeppaEventMediator> eventMediators;
	private List<OnZeppaEventLoadListener> eventLoadListeners;

	private boolean hasLoadedInitialEvents;
	private boolean isLoadingEvents;
	private boolean isMoreEvents;

	private String nextRelationshipPageToken;
	// Last system time in millis a call was made to fetch new events
	private long lastUpdateCallTime;
	// Last system time events were added or removed
	private long lastUpdateTime;

	/*
	 * Instance Handlers
	 */

	/**
	 * 
	 * Private Constructor. Cannot be called outside of singleton. This prevents
	 * multiple instances from being created
	 * 
	 */
	private ZeppaEventSingleton() {

		hasLoadedInitialEvents = false;
		isLoadingEvents = false;
		isMoreEvents = true;

		eventMediators = new ArrayList<AbstractZeppaEventMediator>();
		eventLoadListeners = new ArrayList<OnZeppaEventLoadListener>();
		this.lastUpdateCallTime = System.currentTimeMillis();
		this.lastUpdateTime = System.currentTimeMillis();

	}

	/**
	 * Holds all Zeppa Events, responsible for syncing events against calendar
	 * items. Also syncs to Google Calendar
	 * 
	 * @return CalendarSingleton
	 * 
	 * */
	public static ZeppaEventSingleton getInstance() {
		if (singleton == null)
			singleton = new ZeppaEventSingleton();
		return singleton;
	}

	public void restore() {
		singleton = new ZeppaEventSingleton();
	}

	public void clear() {
		eventMediators.clear();
	}

	public void setNextRelationshipPageToken(String token) {
		this.nextRelationshipPageToken = token;

		if (token == null) {
			isMoreEvents = false;
		}

	}

	public void setLastUpdateCallTime(long callTime) {
		this.lastUpdateCallTime = callTime;
	}

	public long getLastUpdateCallTime() {
		return lastUpdateCallTime;
	}

	public void onLowMemory() {
		// TODO: tighten belt a little more if possible
	}

	/**
	 *
	 * @param lastUpdateTime - epoch milliseconds when a set of events was last updated
	 * @return true if changes have been made to local data since lastUpdateTime
	 */
	public boolean isInfoStale(long lastUpdateTime){
		return lastUpdateTime < this.lastUpdateTime;
	}

	/**
	 * Remove old events cause... they're old
	 */
	public synchronized void clearOldEvents() {

		if (!eventMediators.isEmpty()) {
			long currentTime = System.currentTimeMillis();

			Iterator<AbstractZeppaEventMediator> iterator = eventMediators
					.iterator();


			List<AbstractZeppaEventMediator> remove = new ArrayList<AbstractZeppaEventMediator>();
			while (iterator.hasNext()) {
				AbstractZeppaEventMediator mediator = iterator.next();
				if (mediator.getEndInMillis().longValue() < currentTime) {
					remove.add(mediator);
					NotificationSingleton.getInstance()
							.removeNotificationsForEvent(
									mediator.getEventId().longValue());
				}
			}

			// If a change occured as a result of this, notify the observers
			if(eventMediators.removeAll(remove)){
				this.lastUpdateTime = System.currentTimeMillis();
				this.notifyObservers();
			}

		}
	}

	public boolean removeEventById(long eventId) {

		if (eventMediators != null && !eventMediators.isEmpty()) {

			AbstractZeppaEventMediator remove = null;
			Iterator<AbstractZeppaEventMediator> iterator = eventMediators
					.iterator();
			while (iterator.hasNext()) {
				AbstractZeppaEventMediator mediator = iterator.next();
				if (mediator.getEventId().longValue() == eventId) {
					remove = mediator;
					NotificationSingleton.getInstance()
							.removeNotificationsForEvent(eventId);
					break;
				}

			}

			try {
				if (eventMediators.remove(remove)) {
					this.lastUpdateTime = System.currentTimeMillis();
					return true;
				}
			} catch (NullPointerException e){
				// remove was null or some other issue;
			}
		}

		return false;
	}

	public boolean isLoadingEvents(){
		return isLoadingEvents;
	}
	
	/*
	 * ------------ Getters --------------
	 */

	/**
	 * get the Zeppa ID for the current user
	 * 
	 * @return userId
	 */
	private Long getUserId() {
		return ZeppaUserSingleton.getInstance().getUserId();
	}

	/**
	 * returns true if initial loading calls have been called. As of now, does
	 * not handle applications being brought out of pause state
	 * 
	 * @return hasLoadedInitialEvents
	 */
	public boolean hasLoadedInitial() {

		return hasLoadedInitialEvents;
	}

	public void onInitialEventsLoaded() {
		hasLoadedInitialEvents = true;
		isLoadingEvents = false;
		notifyObservers();
	}

	public void onFinishLoading() {
		isLoadingEvents = false;
		Collections.sort(eventMediators);
		notifyObservers();
	}

	/**
	 * Register an adapter listening for changes made to the events list
	 * 
	 * @param adapter
	 */
	public void registerEventLoadListener(OnZeppaEventLoadListener listener) {
		if (!eventLoadListeners.contains(listener)) {
			eventLoadListeners.add(listener);
		}
	}

	/**
	 * Unregister an adapter listening for changes made to the events list
	 * 
	 * @param adapter
	 */
	public void unregisterEventLoadListener(OnZeppaEventLoadListener listener) {
		eventLoadListeners.remove(listener);
	}

	/**
	 * When change is made to the events list (or a relationship is updated),
	 * notify waiting adapters
	 */
	public void notifyObservers() {
		clearOldEvents();
		if (!eventLoadListeners.isEmpty()) {
			Iterator<OnZeppaEventLoadListener> iterator = eventLoadListeners
					.iterator();
			while (iterator.hasNext()) {
				try {
					OnZeppaEventLoadListener listener = iterator.next();
					listener.onZeppaEventsLoaded();
				} catch (Exception e) {
					e.printStackTrace();

				}

			}
		}
	}

	public List<AbstractZeppaEventMediator> getEventMediators() {
		return eventMediators;
	}

	public AbstractZeppaEventMediator getEventById(Long eventId) {
		if (eventId == null) {
			return null;
		}

		for (AbstractZeppaEventMediator manager : eventMediators) {
			if (manager.doesMatchEventId(eventId))
				return manager;
		}
		return null;
	}

	public List<AbstractZeppaEventMediator> getEventMediatorsForFriend(
			long userId) {
		clearOldEvents();
		List<AbstractZeppaEventMediator> friendEvents = new ArrayList<AbstractZeppaEventMediator>();
		Iterator<AbstractZeppaEventMediator> iterator = eventMediators
				.iterator();
		while (iterator.hasNext()) {
			AbstractZeppaEventMediator eventManager = iterator.next();
			if (eventManager.hostIdDoesMatch(userId)) {
				friendEvents.add((DefaultZeppaEventMediator) eventManager);
			}
		}
		Collections.sort(friendEvents);
		return friendEvents;

	}

	/**
	 * 
	 * @return list of
	 */
	public List<AbstractZeppaEventMediator> getHostedEventMediators() {
		clearOldEvents();
		long userId = getUserId();
		List<AbstractZeppaEventMediator> hostedEventMediators = new ArrayList<AbstractZeppaEventMediator>();
		for (AbstractZeppaEventMediator mediator : eventMediators) {
			if (mediator.hostIdDoesMatch(userId)) {
				hostedEventMediators.add(mediator);
			}
		}

		Collections.sort(hostedEventMediators);
		return hostedEventMediators;
	}

	/**
	 * 
	 * @return list of facade objects for events considered interesting to the
	 *         user
	 * 
	 *         Interesting Event is one which is hosted or user has established
	 *         a relationship to it: Attending or Watching for v1
	 */
	public List<AbstractZeppaEventMediator> getInterestingEventMediators() {
		clearOldEvents();
		List<AbstractZeppaEventMediator> interestingEventMediators = new ArrayList<AbstractZeppaEventMediator>();
		for (AbstractZeppaEventMediator mediator : eventMediators) {
			if (mediator.isAgendaEvent()) {
				interestingEventMediators.add(mediator);
			}
		}
		Collections.sort(interestingEventMediators);
		return interestingEventMediators;
	}

	public AbstractZeppaEventMediator getMatchingEventMediator(Event event) {

		Iterator<AbstractZeppaEventMediator> iterator = eventMediators
				.iterator();
		while (iterator.hasNext()) {

			AbstractZeppaEventMediator mediator = iterator.next();
			String mTitle = mediator.getTitle();
			String eTitle = event.getTitle();

			// Get start and end times. remove milliseconds for compatibility
			long mStart = (mediator.getStartInMillis().longValue() / 1000) * 1000;
			long eStart = event.getStartTime();

			long mEnd = (mediator.getEndInMillis().longValue() / 1000) * 1000;
			long eEnd = event.getEndTime();

			if (mTitle.equals(eTitle) && (mStart == eStart) && (mEnd == eEnd)) {

				return mediator;

			}

		}

		return null;
	}

	/*
	 * ----------- Datastore Management Methods ------------
	 */

	/**
	 * Not thread safe method. This inserts an event instance into the zeppa
	 * backend
	 * 
	 * @param credential
	 *            // Authentication Credential
	 * @param event
	 *            // ZeppaEvent to insert
	 * @return
	 * @throws IOException
	 * @throws GoogleAuthIOException
	 */
	public ZeppaEvent createZeppaEventWithBlocking(
			GoogleAccountCredential credential, ZeppaEvent event)
			throws IOException, GoogleAuthIOException {

		Zeppaeventendpoint.Builder endpointBuilder = new Zeppaeventendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				AndroidJsonFactory.getDefaultInstance(), credential);
		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
		Zeppaeventendpoint eventEndpoint = endpointBuilder.build();

		return eventEndpoint.insertZeppaEvent(event).execute();
	}

	/*
	 * 
	 * Runtime objects
	 */

	/**
	 * This adds a mediator to a the list and notifies the observers This method
	 * assumes that the event is not already held
	 * 
	 * @param mediator
	 */
	public synchronized void addMediator(AbstractZeppaEventMediator mediator) {

		Iterator<AbstractZeppaEventMediator> iterator = eventMediators
				.iterator();
		while (iterator.hasNext()) {
			AbstractZeppaEventMediator m = iterator.next();
			if (m.getEventId().longValue() == mediator.getEventId().longValue()) {
				return;
			}

		}

		if(this.eventMediators.add(mediator)){
            Collections.sort(this.eventMediators);
			this.lastUpdateTime = System.currentTimeMillis();
		}
	}

	/**
	 * This removes a mediator from the held list. If successful, observers are
	 * notified
	 * 
	 * @param mediator
	 * @return
	 */
	public boolean removeMediator(AbstractZeppaEventMediator mediator) {
		boolean success = this.eventMediators.remove(mediator);
		if (success) {
			this.lastUpdateTime = System.currentTimeMillis();
			notifyObservers();
		}

		return success;
	}

	/**
	 * This method removes all events that a user has not joined from the
	 * 
	 * @param userId
	 */
	public void removeMediatorsForUser(long userId, boolean removeJoined) {

		if (eventMediators.isEmpty()) {
			return;
		}

		Iterator<AbstractZeppaEventMediator> iterator = eventMediators
				.iterator();
		List<AbstractZeppaEventMediator> removalList = new ArrayList<AbstractZeppaEventMediator>();

		while (iterator.hasNext()) {
			AbstractZeppaEventMediator mediator = iterator.next();
			if (mediator.getHostId().longValue() == userId) {

				// If you should keep joined and user is attending,continue on
				// loop
				if (!removeJoined
						&& ((DefaultZeppaEventMediator) mediator).isAttending()) {
					continue;
				}

				removalList.add(mediator);
				NotificationSingleton.getInstance()
						.removeNotificationsForEvent(
								mediator.getEventId().longValue());
			}
		}

		if (!removalList.isEmpty() && eventMediators.remove(removalList)) {
			this.lastUpdateTime = System.currentTimeMillis();
			this.notifyObservers();
		}
	}

	/**
	 * This method returns true if relationship is already held with event to
	 * prevent loading things twice
	 * 
	 * @param relationship
	 * @return
	 */
	public boolean relationshipAlreadyHeld(
			ZeppaEventToUserRelationship relationship) {
		if (!eventMediators.isEmpty()) {

			Iterator<AbstractZeppaEventMediator> iterator = eventMediators
					.iterator();
			while (iterator.hasNext()) {
				AbstractZeppaEventMediator mediator = iterator.next();
				if ((mediator instanceof DefaultZeppaEventMediator)
						&& ((DefaultZeppaEventMediator) mediator)
								.relationshipDoesMatch(relationship)) {
					return true;
				}
			}

		}

		return false;
	}

	protected Zeppaeventendpoint buildEventEndpoint(
			GoogleAccountCredential credential) {
		Zeppaeventendpoint.Builder builder = new Zeppaeventendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				GsonFactory.getDefaultInstance(), credential);
		CloudEndpointUtils.updateBuilder(builder);
		Zeppaeventendpoint endpoint = builder.build();

		return endpoint;
	}

	/**
	 * @param credential
	 * @return
	 */
	protected Zeppaeventtouserrelationshipendpoint buildEventRelationshipEndpoint(
			GoogleAccountCredential credential) {
		Zeppaeventtouserrelationshipendpoint.Builder builder = new Zeppaeventtouserrelationshipendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				GsonFactory.getDefaultInstance(), credential);
		CloudEndpointUtils.updateBuilder(builder);
		Zeppaeventtouserrelationshipendpoint endpoint = builder.build();

		return endpoint;
	}

	/*
	 * ------------ Loader Method ---------------
	 */

	public void fetchNewEvents(ZeppaApplication application,
			GoogleAccountCredential credential) {

		if (isLoadingEvents) {
			return;
		}

		isLoadingEvents = true;
		ThreadManager.execute(new FetchNewEventsRunnable(application,
				credential, ZeppaUserSingleton.getInstance().getUserId()
						.longValue(), lastUpdateCallTime));
		lastUpdateCallTime = System.currentTimeMillis();

	}

	/**
	 * Start a thread to fetch more events from the backend
	 * @param application
	 * @param credential
	 * @return true if runnable was added to thread manager
	 */
	public boolean fetchMoreEvents(ZeppaApplication application,
			GoogleAccountCredential credential) {

		if (isLoadingEvents || !isMoreEvents) {
			return false;
		}

		isLoadingEvents = true;
		ThreadManager.execute(new FetchMoreEventsRunnable(application,
				credential, ZeppaUserSingleton.getInstance().getUserId()
						.longValue(), nextRelationshipPageToken));

		return true;
	}

}
