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
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.SyncStateContract.Helpers;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAuthIOException;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.adapter.eventlistadapter.AbstractEventListAdapter;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator;
import com.minook.zeppa.mediator.DefaultZeppaEventMediator;
import com.minook.zeppa.mediator.MyZeppaEventMediator;
import com.minook.zeppa.task.FetchHostedEventsTask;
import com.minook.zeppa.task.FetchJoinableEventsTask;
import com.minook.zeppa.zeppaeventendpoint.Zeppaeventendpoint;
import com.minook.zeppa.zeppaeventendpoint.Zeppaeventendpoint.GetZeppaEvent;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.model.ZeppaEventToUserRelationship;

public class ZeppaEventSingleton {

	private final String TAG = "ZeppaEventSingleton";

	private static ZeppaEventSingleton singleton;

	private List<AbstractZeppaEventMediator> eventMediators;

	private boolean hasLoadedInitialEvents;
	private boolean hasLoadedInitialHostedEvent;
	private boolean hasLoadedInitialFeedEvents;

	private String nextRelationshipPageToken;

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
		new FetchJoinableEventsTask(credential, userId, nextRelationshipPageToken).execute();
	}

	public void setNextRelationshipPageToken(String token) {
		this.nextRelationshipPageToken = token;
	}

	/**
	 * 
	 * 
	 * Only call if we need to tighten the belt Ensures all old events are
	 * cleared
	 * 
	 * Also clears "uninteresting" events
	 */

	public void onLowMemory() {
		// TODO: tighten belt a little more if possible
	}

	public void clearOldEvents() {

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

	public List<AbstractZeppaEventMediator> getEventMediators() {
		return eventMediators;
	}

	public AbstractZeppaEventMediator getEventById(long eventId) {
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

		return friendEvents;

	}

	/**
	 * 
	 * @return list of
	 */
	public List<MyZeppaEventMediator> getHostedEventMediators() {
		long userId = getUserId();
		List<MyZeppaEventMediator> hostedEventMediators = new ArrayList<MyZeppaEventMediator>();
		for (AbstractZeppaEventMediator facade : eventMediators) {
			if (facade.hostIdDoesMatch(userId)) {
				hostedEventMediators.add(((MyZeppaEventMediator) facade));
			}
		}

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

	public void registerObserver(AbstractEventListAdapter adapter) {
		ZeppaEventObserver.registerObserver(adapter);
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

	public void addMediator(AbstractZeppaEventMediator mediator) {
		this.eventMediators.add(mediator);
	}

	public boolean removeMediator(AbstractZeppaEventMediator mediator) {
		return this.eventMediators.remove(mediator);
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
	 * Private, embedded, observer class which notifies adapters as events are
	 * loaded
	 */
	private static class ZeppaEventObserver {

		private static List<AbstractEventListAdapter> waitingListAdapters;

		public static void registerObserver(AbstractEventListAdapter adapter) {
			if (waitingListAdapters == null) {
				waitingListAdapters = new ArrayList<AbstractEventListAdapter>();
			}

			waitingListAdapters.add(adapter);
		}

		private static void unregisterObserver(AbstractEventListAdapter adapter) {
			waitingListAdapters.remove(adapter);
		}

		public static void notifyDatasetChanged() {

			Log.d("TAG", "EventObserver notifiedDatasetChanged");
			if (waitingListAdapters != null && !waitingListAdapters.isEmpty()) {
				Iterator<AbstractEventListAdapter> adapterIterator = waitingListAdapters
						.iterator();
				while (adapterIterator.hasNext()) {
					AbstractEventListAdapter adapter = adapterIterator.next();

					try {
						adapter.notifyDataSetChanged();
					} catch (NullPointerException n) {
						n.printStackTrace();
						unregisterObserver(adapter);
					} catch (Exception e) {
						unregisterObserver(adapter);
					}

				}

			}
		}

	}

}
