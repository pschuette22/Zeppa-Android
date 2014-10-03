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
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.adapter.eventlistadapter.AbstractEventListAdapter;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator;
import com.minook.zeppa.mediator.DefaultZeppaEventMediator;
import com.minook.zeppa.mediator.MyZeppaEventMediator;
import com.minook.zeppa.zeppaeventendpoint.Zeppaeventendpoint;
import com.minook.zeppa.zeppaeventendpoint.Zeppaeventendpoint.GetZeppaEvent;
import com.minook.zeppa.zeppaeventendpoint.model.CollectionResponseZeppaEvent;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.Zeppaeventtouserrelationshipendpoint;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.model.CollectionResponseZeppaEventToUserRelationship;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.model.ZeppaEventToUserRelationship;

public class ZeppaEventSingleton {

	private final String TAG = "ZeppaEventSingleton";

	private static ZeppaEventSingleton singleton;

	private List<AbstractZeppaEventMediator> eventMediators;

	private boolean hasLoadedInitialEvents;
	private boolean hasLoadedInitialHostedEvent;
	private boolean hasLoadedInitialFeedEvents;

	private long lastCallTime;

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

	public void loadInitialEvents(GoogleAccountCredential credential) {

		lastCallTime = System.currentTimeMillis();
		loadInitialEventsInAsync(credential);
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

	/**
	 * creates a new instance of a ZeppaEvent with default initial values
	 * 
	 * @return ZeppaEvent
	 */
	public ZeppaEvent newEventInstance() {
		Long time = System.currentTimeMillis();
		ZeppaEvent event = new ZeppaEvent();

		event.setHostId(getUserId());
		event.setOriginalEventId(Long.valueOf(-1));
		event.setRepostedFromEventId(Long.valueOf(-1));
		event.setDescription(new String());
		event.setDisplayLocation(new String());
		event.setMapsLocation(new String());

		event.setTimeCreated(time);
		event.setStart(time);
		event.setEnd(time);

		return event;
	}

	/**
	 * Takes in a ZeppaEvent and constructs a new event instance with default
	 * values added for reposting event
	 * 
	 * @param original
	 * @return
	 */

	private ZeppaEvent newRepostEventInstance(ZeppaEvent original) {
		long time = System.currentTimeMillis();
		ZeppaEvent event = new ZeppaEvent();

		event.setHostId(getUserId());
		if (original.getOriginalEventId().longValue() == -1) {
			event.setOriginalEventId(original.getKey().getId());
		} else {
			event.setOriginalEventId(original.getOriginalEventId());
		}
		event.setRepostedFromEventId(original.getKey().getId());
		event.setDescription(original.getDescription());
		event.setDisplayLocation(original.getDisplayLocation());
		event.setMapsLocation(original.getMapsLocation());
		event.setTimeCreated(Long.valueOf(time));
		event.setPrivacy(original.getPrivacy());
		event.setStart(original.getStart());
		event.setEnd(original.getEnd());

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

	public List<AbstractZeppaEventMediator> getEventMediatorsForFriend(long userId) {
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
	 * Fetch Operations
	 */

	/**
	 * 
	 * makes a call to the App Engine Datastore to retrieve all events which the
	 * calling user may participate in that the user in question has created
	 * 
	 * @param context
	 *            , context the application was called in
	 * @param userId
	 *            , Zeppa Database ID for the user in question
	 * @param credential
	 *            , Authentication for performing the call
	 * @return success, returns true if the operation was successful
	 */

	public boolean fetchEventsFor(Context context, Long userId,
			GoogleAccountCredential credential) {

		boolean success = false;

		return success;

	}

	/**
	 * returns the held event referenced by the ID. if it does not exist within
	 * the application, it will be retrieved from the datastore
	 * 
	 * This Method must be called in Async
	 * 
	 * @param eventId
	 *            , Datastore ID of the event in question
	 * @param credential
	 *            , for call authentication
	 * @return event, ZeppaEvent referenced by the ID
	 */

	public ZeppaEvent findOrFetchEventById(Long eventId,
			GoogleAccountCredential credential) {
		ZeppaEvent event = null;// getEventById(eventId.longValue());

		if (event == null) {
			Zeppaeventendpoint.Builder endpointBuilder = new Zeppaeventendpoint.Builder(
					AndroidHttp.newCompatibleTransport(),
					AndroidJsonFactory.getDefaultInstance(), credential);
			endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
			Zeppaeventendpoint endpoint = endpointBuilder.build();

			GetZeppaEvent getEventTask;
			try {
				getEventTask = endpoint.getZeppaEvent(eventId);
				event = getEventTask.execute();
				// addZeppaEvent(event, true);
			} catch (GoogleAuthIOException aEx) {
				Log.wtf(TAG, "AuthException");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return event;
	}

	// TODO: finish this method
	public List<ZeppaEvent> getHeldRepostsForEvent(ZeppaEvent event) {
		List<ZeppaEvent> heldReposts = new ArrayList<ZeppaEvent>();

		Long eventId = event.getKey().getId();

		return heldReposts;
	}

	public Dialog getConflictionDialog(ZeppaEvent event, Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(event.getTitle() + " confliction");

		return builder.create();
	}

	/*
	 * ------------ Private Methods -------------
	 */

	private void trySetAllLoaded() {
		if (hasLoadedInitialHostedEvent && hasLoadedInitialFeedEvents) {
			hasLoadedInitialEvents = true;

			ZeppaEventObserver.notifyDatasetChanged();

		}
	}

	/*
	 * ----------- Datastore Management Methods ------------
	 */

	public ZeppaEvent createZeppaEvent(Context contex,
			GoogleAccountCredential credential, ZeppaEvent event,
			ContentResolver resolver) throws IOException, GoogleAuthIOException {
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
	
	public void addMediator(AbstractZeppaEventMediator mediator){
		this.eventMediators.add(mediator);
	}
	
	public boolean removeMediator(AbstractZeppaEventMediator mediator){
		return this.eventMediators.remove(mediator);
	}
	
	/*
	 * ------------ Loader Method ---------------
	 */

	public boolean loadEventsForUser(GoogleAccountCredential credential,
			Long userId) {
		boolean success = false;

		return success;
	}

	private void loadPriorEvents() {

	}

	public boolean loadNewEvents(GoogleAccountCredential credential) {
		boolean success = false;

		return success;
	}

	/**
	 * @param credential
	 *            for making verified calls
	 * 
	 *            this method starts 3 new threads to retrieve ZeppaEvents
	 * 
	 *            thread 1: events hosted by calling user thread 2: events user
	 *            has relationship with thread 3: other events the user may join
	 * 
	 */

	private void loadInitialEventsInAsync(GoogleAccountCredential credential) {

		Object[] params = { credential, getUserId() };

		/*
		 * Thread 1: This Thread loads all hosted events for a given user
		 */
		new AsyncTask<Object, Void, Void>() {

			@Override
			protected Void doInBackground(Object... params) {

				GoogleAccountCredential credential = (GoogleAccountCredential) params[0];
				Long userId = (Long) params[1];

				Zeppaeventendpoint.Builder endpointBuilder = new Zeppaeventendpoint.Builder(
						AndroidHttp.newCompatibleTransport(),
						AndroidJsonFactory.getDefaultInstance(), credential);
				endpointBuilder = CloudEndpointUtils
						.updateBuilder(endpointBuilder);
				Zeppaeventendpoint endpoint = endpointBuilder.build();
				Long startTimeMillis = Long.valueOf(System.currentTimeMillis());

				while (true) {
					try {

						Log.d(TAG, "Calling fetchHostedEvents(" + userId + ", "
								+ startTimeMillis + ")");
						CollectionResponseZeppaEvent collectionResponse = endpoint
								.fetchHostedEvents(userId, startTimeMillis)
								.execute();

						if (collectionResponse.getItems() != null
								&& !collectionResponse.getItems().isEmpty()) {
							List<ZeppaEvent> events = collectionResponse
									.getItems();

							Iterator<ZeppaEvent> eventIterator = events
									.iterator();
							while (eventIterator.hasNext()) {
								eventMediators.add(new MyZeppaEventMediator(
										eventIterator.next()));
							}

							if (events.size() < 10) {
								break;
							} else {
								startTimeMillis = events.get(9).getStart();
							}

						} else {
							break;
						}

					} catch (GoogleAuthIOException aEx) {
						Log.wtf(TAG, "AuthException");
						break;
					} catch (IOException e) {
						e.printStackTrace();
						break;
					}

				}

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				hasLoadedInitialHostedEvent = true;
				trySetAllLoaded();
			}

		}.execute(params);

		/*
		 * Following task loads all event relationship entities and
		 * corresponding events.
		 */
		new AsyncTask<Object, Void, Void>() {

			@Override
			protected Void doInBackground(Object... params) {

				GoogleAccountCredential credential = (GoogleAccountCredential) params[0];
				Long userId = (Long) params[1];

				Zeppaeventtouserrelationshipendpoint.Builder ETUR_Endpointbuilder = new Zeppaeventtouserrelationshipendpoint.Builder(
						AndroidHttp.newCompatibleTransport(),
						AndroidJsonFactory.getDefaultInstance(), credential);
				ETUR_Endpointbuilder = CloudEndpointUtils
						.updateBuilder(ETUR_Endpointbuilder);
				Zeppaeventtouserrelationshipendpoint ETUR_Endpoint = ETUR_Endpointbuilder
						.build();

				Zeppaeventendpoint.Builder endpointBuilder = new Zeppaeventendpoint.Builder(
						AndroidHttp.newCompatibleTransport(),
						AndroidJsonFactory.getDefaultInstance(), credential);
				endpointBuilder = CloudEndpointUtils
						.updateBuilder(endpointBuilder);
				Zeppaeventendpoint endpoint = endpointBuilder.build();

				Long minEventEndTime = Long.valueOf(System.currentTimeMillis());
				Long startIndex = Long.valueOf(0);
				
				while (true) {
					try {
						Log.d(TAG, "Calling getEventToUserRelationships(" + userId
								+ ", " + minEventEndTime + ")");
						CollectionResponseZeppaEventToUserRelationship relationshipCollection = ETUR_Endpoint
								.getRelationshipsForUser(userId,
										minEventEndTime, startIndex).execute();

						if (relationshipCollection.getItems() != null
								&& !relationshipCollection.getItems().isEmpty()) {
							Iterator<ZeppaEventToUserRelationship> relationships = relationshipCollection
									.getItems().iterator();

							while (relationships.hasNext()) {
								ZeppaEventToUserRelationship relationship = relationships
										.next();
								try {
									Log.d(TAG, "Calling getZeppaEvent("
											+ relationship.getZeppaEventId()
											+ ")");
									ZeppaEvent event = endpoint.getZeppaEvent(
											relationship.getZeppaEventId())
											.execute();

									eventMediators
											.add(new DefaultZeppaEventMediator(
													event, relationship));

								} catch (GoogleJsonResponseException ex) {
									ex.printStackTrace();
								}
							}

							if (relationshipCollection.getItems().size() < 10) {
								break;
							} else {
								startIndex += 20;
							}

						} else {
							break;
						}

					} catch (GoogleAuthIOException aEx) {
						Log.wtf(TAG, "AuthException");
						break;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}

				}

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				hasLoadedInitialFeedEvents = true;
				trySetAllLoaded();
			}

		}.execute(params);

	}

	// public void loadNewZeppaEvents(GoogleAccountCredential credential) {
	//
	// }

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
