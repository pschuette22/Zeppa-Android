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
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.adapter.eventlistadapter.AbstractEventListAdapter;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator;
import com.minook.zeppa.mediator.DefaultZeppaEventMediator;
import com.minook.zeppa.mediator.MyZeppaEventMediator;
import com.minook.zeppa.observer.OnLoadListener;
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

	private List<AbstractZeppaEventMediator> eventManagers;
	private List<OnLoadListener> loadListeners;

	private boolean hasLoadedInitialEvents;
	private boolean hasLoadedInitialInterestingEvents;
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
		hasLoadedInitialInterestingEvents = false;
		hasLoadedInitialHostedEvent = false;
		hasLoadedInitialFeedEvents = false;
		loadListeners = new ArrayList<OnLoadListener>();

		eventManagers = new ArrayList<AbstractZeppaEventMediator>();

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

	public List<AbstractZeppaEventMediator> getEventManagers() {
		return eventManagers;
	}

	public AbstractZeppaEventMediator getEventById(long eventId) {
		for (AbstractZeppaEventMediator manager : eventManagers) {
			if (manager.doesMatchEventId(eventId))
				return manager;
		}
		return null;
	}

	public List<DefaultZeppaEventMediator> getEventManagersForFriend(long userId) {
		List<DefaultZeppaEventMediator> friendEvents = new ArrayList<DefaultZeppaEventMediator>();
		Iterator<AbstractZeppaEventMediator> iterator = eventManagers
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
	public List<MyZeppaEventMediator> getHostedEventManagers() {
		long userId = getUserId();
		List<MyZeppaEventMediator> hostedEventManagers = new ArrayList<MyZeppaEventMediator>();
		for (AbstractZeppaEventMediator facade : eventManagers) {
			if (facade.hostIdDoesMatch(userId)) {
				hostedEventManagers.add(((MyZeppaEventMediator) facade));
			}
		}

		return hostedEventManagers;
	}

	/**
	 * 
	 * @return list of facade objects for events considered interesting to the
	 *         user
	 * 
	 *         Interesting Event is one which is hosted or user has established
	 *         a relationship to it: Attending or Watching for v1
	 */
	public List<AbstractZeppaEventMediator> getInterestingEventManagers() {
		List<AbstractZeppaEventMediator> interestingEventManagers = new ArrayList<AbstractZeppaEventMediator>();
		for (AbstractZeppaEventMediator facade : eventManagers) {
			if (facade.isInterestingEvent()) {
				interestingEventManagers.add(facade);
			}
		}
		return interestingEventManagers;
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
		if (hasLoadedInitialHostedEvent && hasLoadedInitialFeedEvents
				&& hasLoadedInitialInterestingEvents) {
			hasLoadedInitialEvents = true;

			ZeppaEventObserver.notifyDatasetChanged();

		}
	}

	// TODO: move this to DefaultZeppaEventManager
	// private void addEventToCalendar(Context context, ZeppaEvent event,
	// ContentResolver resolver) {
	//
	// ContentValues values = new ContentValues();
	// Calendar c = Calendar.getInstance();
	//
	// int calendarId = PreferencesHelper.getInstance(context)
	// .getPrefs(context)
	// .getInt(Constants.ZEPPA_INTERNAL_CALENDAR_ID, -1);
	//
	// if (calendarId < 0) {
	// Log.d(TAG,
	// "couldnt find calendar id, returning w/out adding to internal calendar");
	// return;
	// }
	//
	// values.put(Events.CALENDAR_ID, calendarId);
	// values.put(Events.TITLE, event.getTitle());
	// values.put(Events.DESCRIPTION, event.getDescription());
	// values.put(Events.DTSTART, event.getStart());
	// values.put(Events.DTEND, event.getEnd());
	// values.put(Events.EVENT_TIMEZONE, c.getTimeZone().getID());
	//
	// values.put(Events.HAS_ALARM, 0);
	//
	// Uri uri = resolver.insert(Events.CONTENT_URI, values);
	//
	// Long eventId = Long.parseLong(uri.getLastPathSegment());
	// ZeppaEventDBHelper helper = new ZeppaEventDBHelper(context);
	//
	// helper.linkEventIds(event, eventId);
	// new ImportEntries().execute(context);
	// }
	//
	// private void removeEventFromCalendar(Context context, ZeppaEvent event) {
	//
	// ZeppaEventDBHelper helper = new ZeppaEventDBHelper(context);
	// long eventId = helper.getMatchingId(event);
	//
	// if (eventId < 0) {
	// Log.d(TAG, "Didnt find matching event");
	// return;
	// }
	//
	// Uri deleteUri = ContentUris.withAppendedId(Events.CONTENT_URI, eventId);
	// context.getContentResolver().delete(deleteUri, null, null);
	// }

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
								eventManagers.add(new MyZeppaEventMediator(
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

				Zeppaeventtouserrelationshipendpoint.Builder eturEndpointbuilder = new Zeppaeventtouserrelationshipendpoint.Builder(
						AndroidHttp.newCompatibleTransport(),
						AndroidJsonFactory.getDefaultInstance(), credential);
				eturEndpointbuilder = CloudEndpointUtils
						.updateBuilder(eturEndpointbuilder);
				Zeppaeventtouserrelationshipendpoint eturEndpoint = eturEndpointbuilder
						.build();

				Zeppaeventendpoint.Builder endpointBuilder = new Zeppaeventendpoint.Builder(
						AndroidHttp.newCompatibleTransport(),
						AndroidJsonFactory.getDefaultInstance(), credential);
				endpointBuilder = CloudEndpointUtils
						.updateBuilder(endpointBuilder);
				Zeppaeventendpoint endpoint = endpointBuilder.build();
				boolean keepGoing = true;
				Long minEventEndTime = Long.valueOf(-1);

				while (keepGoing) {
					try {
						CollectionResponseZeppaEventToUserRelationship relationshipCollection = eturEndpoint
								.getRelationshipsForUser(userId,
										minEventEndTime).execute();

						if (relationshipCollection.getItems() != null
								&& !relationshipCollection.getItems().isEmpty()) {
							Iterator<ZeppaEventToUserRelationship> relationships = relationshipCollection
									.getItems().iterator();

							while (relationships.hasNext()) {
								ZeppaEventToUserRelationship relationship = relationships
										.next();
								ZeppaEvent event = endpoint.getZeppaEvent(
										relationship.getZeppaEventId())
										.execute();

								eventManagers
										.add(new DefaultZeppaEventMediator(
												event, relationship));
							}

							if (relationshipCollection.getItems().size() < 10) {
								keepGoing = false;
							}

						} else {
							keepGoing = false;
						}

					} catch (GoogleAuthIOException aEx) {
						Log.wtf(TAG, "AuthException");
						keepGoing = false;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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
		 * Following task loads more generic events
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

				try {

					CollectionResponseZeppaEvent eventCollection = endpoint
							.fetchPossibleEvents(userId, lastCallTime,
									Long.valueOf(System.currentTimeMillis()))
							.execute();

					if (eventCollection.getItems() != null
							&& !eventCollection.getItems().isEmpty()) {
						Iterator<ZeppaEvent> eventIterator = eventCollection
								.getItems().iterator();

						while (eventIterator.hasNext()) {
							eventManagers.add(new DefaultZeppaEventMediator(
									eventIterator.next(), null));
						}
					}

				} catch (GoogleAuthIOException aEx) {
					Log.wtf(TAG, "AuthException");
				} catch (IOException e) {
					e.printStackTrace();

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

	public void loadNewZeppaEvents(GoogleAccountCredential credential) {

	}

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
			if (waitingListAdapters != null) {
				Iterator<AbstractEventListAdapter> adapterIterator = waitingListAdapters
						.iterator();
				while (adapterIterator.hasNext()) {
					AbstractEventListAdapter adapter = adapterIterator.next();

					try {
						adapter.verifyDatasetValid();
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
