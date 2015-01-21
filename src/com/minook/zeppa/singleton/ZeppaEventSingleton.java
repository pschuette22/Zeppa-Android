package com.minook.zeppa.singleton;

/**
 * Calendar Singleton. Handles all Google Calendar Transactions
 * 
 * @author Pete Schuette 
 * Date Created: Thursday March 6th, 2014;
 * 
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAuthIOException;
import com.google.api.client.json.gson.GsonFactory;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.adapter.eventlistadapter.AbstractEventListAdapter;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator;
import com.minook.zeppa.mediator.DefaultZeppaEventMediator;
import com.minook.zeppa.task.FetchHostedEventsTask;
import com.minook.zeppa.task.FetchJoinableEventsTask;
import com.minook.zeppa.zeppaeventendpoint.Zeppaeventendpoint;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.Zeppaeventtouserrelationshipendpoint;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.Zeppaeventtouserrelationshipendpoint.ListZeppaEventToUserRelationship;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.model.CollectionResponseZeppaEventToUserRelationship;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.model.ZeppaEventToUserRelationship;

public class ZeppaEventSingleton {

	private final String TAG = "ZeppaEventSingleton";

	private static ZeppaEventSingleton singleton;

	private List<AbstractZeppaEventMediator> eventMediators;
	private List<AbstractEventListAdapter> eventAdapters;

	private boolean hasLoadedInitialEvents;
	private boolean hasLoadedInitialHostedEvent;
	private boolean hasLoadedInitialFeedEvents;

	private String nextRelationshipPageToken;
	private Long lastUpdateCallTime;

	/*
	 * Instance Handlers
	 */

	/**
	 * 
	 * Private Constructor. Cannot be called outside of singleton. This prevents
	 * multiple instances from being called
	 * 
	 */
	private ZeppaEventSingleton() {

		hasLoadedInitialEvents = false;
		hasLoadedInitialHostedEvent = false;
		hasLoadedInitialFeedEvents = false;

		eventMediators = new ArrayList<AbstractZeppaEventMediator>();
		eventAdapters = new ArrayList<AbstractEventListAdapter>();
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

	public void loadInitialEvents(GoogleAccountCredential credential,
			Long userId) {
		new FetchHostedEventsTask(credential, userId).execute();
		new FetchJoinableEventsTask(credential, userId,
				nextRelationshipPageToken).execute();
	}

	public void setNextRelationshipPageToken(String token) {
		this.nextRelationshipPageToken = token;
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

	public void clearOldEvents() {

		if (!eventMediators.isEmpty()) {
			long currentTime = System.currentTimeMillis();

			boolean changeMade = false;
			Iterator<AbstractZeppaEventMediator> iterator = eventMediators
					.iterator();

			while (iterator.hasNext()) {
				AbstractZeppaEventMediator mediator = iterator.next();
				if (mediator.getEndInMillis().longValue() < currentTime) {
					eventMediators.remove(mediator);
					changeMade = true;
				}
			}

			if (changeMade) {
				notifyObservers();
			}

		}
	}

	/*
	 * ----------- New Instances ---------
	 */

	public ZeppaEvent newRepostEventInstance(ZeppaEvent repostEvent) {

		ZeppaEvent event = new ZeppaEvent();
		event.setHostId(ZeppaUserSingleton.getInstance().getUserId());
		event.setRepostedEventId(repostEvent.getKey().getId());

		if (repostEvent.getOriginalEventId() == null) {
			event.setOriginalEventId(repostEvent.getKey().getId());
		} else {
			event.setOriginalEventId(repostEvent.getOriginalEventId());
		}

		event.setDescription(repostEvent.getDescription());
		event.setDisplayLocation(repostEvent.getDisplayLocation());
		event.setMapsLocation(repostEvent.getMapsLocation());
		event.setPrivacy(repostEvent.getPrivacy());
		event.setStart(repostEvent.getStart());
		event.setEnd(repostEvent.getEnd());
		event.setGoogleCalendarEventId(repostEvent.getGoogleCalendarEventId());
		event.setGoogleCalendarId(repostEvent.getGoogleCalendarId());
		event.setICalUID(repostEvent.getICalUID());

		return event;
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

	public boolean hasLoadedInitialHostedEvents() {
		return hasLoadedInitialHostedEvent;
	}

	public void setHasLoadedInitialHostedEvents() {
		hasLoadedInitialHostedEvent = true;
		if (hasLoadedInitialFeedEvents) {
			onInitialEventsLoaded();
		}
	}

	public boolean hasLoadedInitialFeedEvents() {
		return hasLoadedInitialFeedEvents;
	}

	public void setHasLoadedInitialFeedEvents() {
		hasLoadedInitialFeedEvents = true;
		if (hasLoadedInitialHostedEvent) {
			onInitialEventsLoaded();
		}
	}

	private void onInitialEventsLoaded() {
		hasLoadedInitialEvents = true;
		notifyObservers();
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
		List<AbstractZeppaEventMediator> interestingEventMediators = new ArrayList<AbstractZeppaEventMediator>();
		for (AbstractZeppaEventMediator facade : eventMediators) {
			if (facade.isAgendaEvent()) {
				interestingEventMediators.add(facade);
			}
		}
		Collections.sort(interestingEventMediators);
		return interestingEventMediators;
	}

	// public Long getMyRepostId(Long originalId) {
	// Long repostId = Long.valueOf(-1);
	// Long userId = getUserId();
	// for (ZeppaEvent event : events) {
	// if (event.getHostId().equals(userId)
	// && event.getKey().getId().equals(originalId)) {
	// return event.getKey().getId();
	// }
	// }
	//
	// return repostId;
	// }
	//
	// public List<ZeppaEvent> getEventsFor(long userId) {
	// List<ZeppaEvent> friendEvents = new ArrayList<ZeppaEvent>();
	//
	// for (ZeppaEvent event : events) {
	// if (event.getHostId().longValue() == userId) {
	// friendEvents.add(event);
	// }
	// }
	//
	// return friendEvents;
	// }
	//
	// public ZeppaEvent getZeppaEventFor(Event event) {
	// ZeppaEvent result = null;
	//
	// for (ZeppaEvent zeppaEvent : events) {
	// if (zeppaEvent.getTitle().equals(event.title)
	// && zeppaEvent.getStart().longValue() == event
	// .getStartMillis()
	// && zeppaEvent.getEnd().longValue() == event.getEndMillis()) {
	// result = zeppaEvent;
	// break;
	// }
	//
	// }
	// return result;
	// }

	/*
	 * Setters
	 */

	/**
	 * Register an adapter listening for changes made to the events list
	 * 
	 * @param adapter
	 */
	public void registerObserver(AbstractEventListAdapter adapter) {
		if (!eventAdapters.contains(adapter)) {
			eventAdapters.add(adapter);
		}
	}

	/**
	 * Unregister an adapter listening for changes made to the events list
	 * 
	 * @param adapter
	 */
	public void unregisterObserver(AbstractEventListAdapter adapter) {
		eventAdapters.remove(adapter);
	}

	/**
	 * When change is made to the events list (or a relationship is updated),
	 * notify waiting adapters
	 */
	public void notifyObservers() {
		if (!eventAdapters.isEmpty()) {
			Iterator<AbstractEventListAdapter> iterator = eventAdapters
					.iterator();
			while (iterator.hasNext()) {
				AbstractEventListAdapter adapter = iterator.next();
				try {
					adapter.verifyDatasetValid();

				} catch (Exception e) {
					e.printStackTrace();
					unregisterObserver(adapter);

				}

			}
		}
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
	 * This adds a mediator to a the list and notifies the observers
	 * 
	 * @param mediator
	 */
	public void addMediator(AbstractZeppaEventMediator mediator, boolean notify) {
		this.eventMediators.add(mediator);
		if (notify) {
			notifyObservers();
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
			notifyObservers();
		}

		return success;
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
		if (eventMediators.isEmpty()) {
			return false;
		}

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

		return false;
	}

	/*
	 * ------------ Loader Method ---------------
	 */

	/**
	 * This method verifies that an event and relationship are held or loads
	 * them by event ID THIS METHOD IS NOT THREAD SAFE
	 * 
	 * @param eventId
	 * @param credential
	 * @return true if event found or successfully loaded
	 */
	public boolean fetchEventAndRelationshipWithBlocking(
			GoogleAccountCredential credential, Long eventId) {

		if (getEventById(eventId) == null) {

			Zeppaeventendpoint endpointEvent = buildEventEndpoint(credential);
			Zeppaeventtouserrelationshipendpoint endpointRelationship = buildEventRelationshipEndpoint(credential);

			try {
				ZeppaEvent event = endpointEvent.getZeppaEvent(eventId)
						.execute();

				ListZeppaEventToUserRelationship listRelationshipsTask = endpointRelationship
						.listZeppaEventToUserRelationship();
				listRelationshipsTask.setFilter("userId == "
						+ ZeppaUserSingleton.getInstance().getUserId()
								.longValue() + " && eventId == "
						+ eventId.longValue());
				listRelationshipsTask.setLimit(Integer.valueOf(1)); // Only
																	// retrieve
																	// 1

				CollectionResponseZeppaEventToUserRelationship response = listRelationshipsTask
						.execute();

				if (response.getItems().isEmpty()) {
					return false;
				}

				ZeppaEventToUserRelationship relationship = response.getItems()
						.get(0);

				addMediator(new DefaultZeppaEventMediator(event, relationship),
						true);

			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

			return true;
		} else
			return true;

	}

	private Zeppaeventendpoint buildEventEndpoint(
			GoogleAccountCredential credential) {
		Zeppaeventendpoint.Builder builder = new Zeppaeventendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				GsonFactory.getDefaultInstance(), credential);
		CloudEndpointUtils.updateBuilder(builder);
		Zeppaeventendpoint endpoint = builder.build();

		return endpoint;
	}

	private Zeppaeventtouserrelationshipendpoint buildEventRelationshipEndpoint(
			GoogleAccountCredential credential) {
		Zeppaeventtouserrelationshipendpoint.Builder builder = new Zeppaeventtouserrelationshipendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				GsonFactory.getDefaultInstance(), credential);
		CloudEndpointUtils.updateBuilder(builder);
		Zeppaeventtouserrelationshipendpoint endpoint = builder.build();

		return endpoint;
	}

}
