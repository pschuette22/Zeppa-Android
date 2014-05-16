package com.minook.zeppa.singleton;

/**
 * @author Pete Schuette 
 * Date Created: Thursday March 6th, 2014;
 * 
 * Singleton class update, holding everything in the application context was getting overwhelming
 * 
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.calendarview.Event;
import com.example.calendarview.ImportEntries;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.Constants;
import com.minook.zeppa.Constants.ConflictionStatus;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.adapters.eventadapter.EventListAdapter;
import com.minook.zeppa.database.PreferencesHelper;
import com.minook.zeppa.database.ZeppaEventDBHelper;
import com.minook.zeppa.zeppaeventendpoint.Zeppaeventendpoint;
import com.minook.zeppa.zeppaeventendpoint.Zeppaeventendpoint.FetchAttendingEvents;
import com.minook.zeppa.zeppaeventendpoint.Zeppaeventendpoint.FetchHostedEvents;
import com.minook.zeppa.zeppaeventendpoint.Zeppaeventendpoint.FetchJoinableUserEvents;
import com.minook.zeppa.zeppaeventendpoint.Zeppaeventendpoint.FetchPossibleEvents;
import com.minook.zeppa.zeppaeventendpoint.Zeppaeventendpoint.FetchRecentlyPostedEvents;
import com.minook.zeppa.zeppaeventendpoint.Zeppaeventendpoint.FetchWatchingEvents;
import com.minook.zeppa.zeppaeventendpoint.Zeppaeventendpoint.GetZeppaEvent;
import com.minook.zeppa.zeppaeventendpoint.model.CollectionResponseZeppaEvent;
import com.minook.zeppa.zeppaeventendpoint.model.JsonMap;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;

public class ZeppaEventSingleton {

	private final String TAG = "ZeppaEventSingleton";

	private static ZeppaEventSingleton singleton;

	private List<ZeppaEvent> events;
	private List<EventListAdapter> waitingListAdapters;

	private boolean hasLoadedInitial;

	private boolean hasLoadedHostedEvents;
	private boolean hasLoadedJoinedEvents;
	private boolean hasLoadedWatchingEvents;
	private boolean hasLoadedOtherEvents;

	private long lastCallTime;

	/*
	 * Calendar Objects
	 */
	private static final String[] EVENTS_PROJECTION = {
			CalendarContract.Events._ID, CalendarContract.Events.TITLE,
			CalendarContract.Events.DESCRIPTION,
			CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND };

	private final int ID_INDEX = 0;
	private final int TITLE_INDEX = 1;
	private final int DESC_INDEX = 2;
	private final int START_INDEX = 3;
	private final int END_INDEX = 4;

	/*
	 * Instance Handlers
	 */

	private ZeppaEventSingleton() {

		events = new ArrayList<ZeppaEvent>();
		waitingListAdapters = new ArrayList<EventListAdapter>();

	}

	public static ZeppaEventSingleton getInstance() {
		if (singleton == null)
			singleton = new ZeppaEventSingleton();
		return singleton;
	}

	public void loadInitialEvents(GoogleAccountCredential credential) {

		hasLoadedInitial = false;
		hasLoadedHostedEvents = false;
		hasLoadedJoinedEvents = false;
		hasLoadedWatchingEvents = false;
		hasLoadedOtherEvents = false;
		lastCallTime = System.currentTimeMillis();
		loadInitialEventsInAsync(credential);
	}

	public void onLowMemory() {
		clearOldEvents();
		long userId = getUserId();
		for (ZeppaEvent event : events) {
			boolean doDelete = true;
			if (event.getHostId().longValue() == userId) {
				doDelete = false;
			} else if (event.getUsersInterested().contains(userId)) {
				doDelete = false;
			}
			if (doDelete) {
				events.remove(event);
			}
		}
	}

	public void clearOldEvents() {
		long currentTime = System.currentTimeMillis();
		for (ZeppaEvent event : events) {
			if (event.getEnd().longValue() < currentTime) {
				events.remove(event);
			}
		}
	}

	/*
	 * ----------- New Instances ---------
	 */

	public ZeppaEvent newEventInstance() {
		Long time = System.currentTimeMillis();
		ZeppaEvent event = new ZeppaEvent();

		event.setHostId(getUserId());
		event.setOriginalEventId(Long.valueOf(-1));
		event.setDescription(new String());
		event.setShortLocation(new String());
		event.setExactLocation(new String());
		event.setUsersGoingIds(new ArrayList<Long>());
		event.setUsersWatchingIds(new ArrayList<Long>());
		event.setUsersInvited(new ArrayList<Long>());
		event.setTagIds(new ArrayList<Long>());

		event.setTimeCreated(time);
		event.setStart(time);
		event.setEnd(time);
		event.setMinAge(Integer.valueOf(-1));
		event.setMaxAge(Integer.valueOf(1000));
		event.setGuestCanInvite(Boolean.FALSE);

		return event;
	}

	private ZeppaEvent newRepostEventInstance(ZeppaEvent original) {
		Long time = System.currentTimeMillis();
		ZeppaEvent event = new ZeppaEvent();

		event.setHostId(getUserId());
		event.setOriginalEventId(original.getKey().getId());
		event.setDescription("\"" + original.getDescription() + "\"");
		event.setShortLocation(original.getShortLocation());
		event.setExactLocation(original.getExactLocation());
		event.setTimeCreated(time);
		event.setStart(original.getStart());
		event.setEnd(original.getEnd());
		event.setUsersGoingIds(new ArrayList<Long>());
		event.setUsersWatchingIds(new ArrayList<Long>());
		event.setUsersInvited(new ArrayList<Long>());
		event.setReposts(new JsonMap());
		event.setTagIds(new ArrayList<Long>());

		event.setMinAge(original.getMinAge());
		event.setMaxAge(original.getMaxAge());
		event.setGuestCanInvite(original.getGuestCanInvite());

		return event;
	}

	/*
	 * ----------- Private Methods ---------
	 */

	/*
	 * ------------ Getters --------------
	 */

	private Long getUserId() {
		return ZeppaUserSingleton.getInstance().getUserId();
	}

	public boolean hasLoadedInitial() {
		return hasLoadedInitial;
	}

	public List<ZeppaEvent> getZeppaEvents() {
		return events;
	}

	public ZeppaEvent getEventById(long eventId) {
		for (ZeppaEvent event : events) {
			if (event.getKey().getId().longValue() == eventId)
				return event;
		}
		return null;
	}

	public List<ZeppaEvent> getHostedEvents() {
		long userId = getUserId();
		List<ZeppaEvent> hostedEvents = new ArrayList<ZeppaEvent>();
		for (ZeppaEvent event : events) {
			if (event.getHostId().longValue() == userId)
				hostedEvents.add(event);
		}
		return hostedEvents;
	}

	public List<ZeppaEvent> getInterestingEvents() {
		List<ZeppaEvent> interestingEvents = new ArrayList<ZeppaEvent>();
		Long userId = getUserId();
		for (ZeppaEvent event : events) {
			if (event.getHostId().longValue() == userId) {
				interestingEvents.add(event);
			} else if (event.getUsersWatchingIds() != null
					&& event.getUsersWatchingIds().contains(userId)) {
				interestingEvents.add(event);
			} else if (event.getUsersGoingIds() != null
					&& event.getUsersGoingIds().contains(userId)) {
				interestingEvents.add(event);
			}
		}
		return interestingEvents;
	}

	public Long getMyRepostId(Long originalId) {
		Long repostId = Long.valueOf(-1);
		Long userId = getUserId();
		for (ZeppaEvent event : events) {
			if (event.getHostId().equals(userId)
					&& event.getKey().getId().equals(originalId)) {
				return event.getKey().getId();
			}
		}

		return repostId;
	}

	public List<ZeppaEvent> getLocalReposts(long eventId) {
		List<ZeppaEvent> reposts = new ArrayList<ZeppaEvent>();
		for (ZeppaEvent event : events) {
			if (event.getOriginalEventId() != null
					&& event.getOriginalEventId().longValue() == eventId)
				reposts.add(event);
		}

		return reposts;
	}

	public List<ZeppaEvent> getEventsFor(long userId) {
		List<ZeppaEvent> friendEvents = new ArrayList<ZeppaEvent>();

		for (ZeppaEvent event : events) {
			if (event.getHostId().longValue() == userId) {
				friendEvents.add(event);
			}
		}

		return friendEvents;
	}

	public ZeppaEvent getZeppaEventFor(Event event) {
		ZeppaEvent result = null;

		for (ZeppaEvent zeppaEvent : events) {
			if (zeppaEvent.getTitle().equals(event.title)
					&& zeppaEvent.getStart().longValue() == event
							.getStartMillis()
					&& zeppaEvent.getEnd().longValue() == event.getEndMillis()) {
				result = zeppaEvent;
				break;
			}

		}
		return result;
	}

	/*
	 * Setters
	 */

	public void listenForLoad(EventListAdapter adapter) {
		if (!waitingListAdapters.contains(adapter))
			waitingListAdapters.add(adapter);
	}

	/*
	 * Fetch Operations
	 */

	public boolean fetchEventsFor(Context context, Long userId,
			GoogleAccountCredential credential) {

		boolean success = false;

		List<ZeppaEvent> fetchedEvents = new ArrayList<ZeppaEvent>();

		Zeppaeventendpoint.Builder endpointBuilder = new Zeppaeventendpoint.Builder(
				AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
				credential);
		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
		Zeppaeventendpoint endpoint = endpointBuilder.build();

		try {
			int start = 0;
			boolean keepGoing = true;
			Long time = System.currentTimeMillis();
			while (keepGoing) {
				keepGoing = false;
				CollectionResponseZeppaEvent eventCollection = endpoint
						.fetchJoinableUserEvents(userId, getUserId(), start,
								time).execute();
				success = true;
				if (eventCollection.getItems() == null
						|| eventCollection.isEmpty()) {
					break;
				} else {
					fetchedEvents.addAll(eventCollection.getItems());
					keepGoing = (eventCollection.getItems().size() >= 10);
				}
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		events.removeAll(fetchedEvents);
		events.addAll(fetchedEvents);

		Collections.sort(events, Constants.EVENT_COMPARATOR);

		return success;

	}

	public ZeppaEvent updateEvent(GoogleAccountCredential credential, ZeppaEvent event) {
		if (event == null) {
			return null;
		} else {

			Zeppaeventendpoint.Builder endpointBuilder = new Zeppaeventendpoint.Builder(
					AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
					credential);
			endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
			Zeppaeventendpoint endpoint = endpointBuilder.build();

			GetZeppaEvent getEventTask;
			try {
				getEventTask = endpoint.getZeppaEvent(event.getKey().getId());
				ZeppaEvent result = getEventTask.execute();

				if(result != null)
					event = result;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return event;
	}

	public ZeppaEvent findOrFetchEventById(Long id,
			GoogleAccountCredential credential) {
		ZeppaEvent event = getEventById(id.longValue());

		if (event == null) {
			Zeppaeventendpoint.Builder endpointBuilder = new Zeppaeventendpoint.Builder(
					AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
					credential);
			endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
			Zeppaeventendpoint endpoint = endpointBuilder.build();

			GetZeppaEvent getEventTask;
			try {
				getEventTask = endpoint.getZeppaEvent(id);
				event = getEventTask.execute();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return event;
	}

	public ConflictionStatus getConflictionStatus(Context context,
			ZeppaEvent event, String accountName, ContentResolver resolver) {

		Uri.Builder builder = Uri.parse(
				"content://com.android.calendar/instances/when").buildUpon();
		ContentUris.appendId(builder, Long.MIN_VALUE);
		ContentUris.appendId(builder, Long.MAX_VALUE);
		Uri uri = builder.build();
		String cursorString = "((" + CalendarContract.Instances.OWNER_ACCOUNT
				+ " = ?) AND (" + CalendarContract.Instances.BEGIN
				+ " = ?) AND (" + CalendarContract.Instances.END + " = ?))";
		String[] args = new String[] { accountName,
				String.valueOf(event.getStart()),
				String.valueOf(event.getEnd()) };
		String sortOrder = CalendarContract.Instances.BEGIN + " ASC";
		Cursor cursor = resolver.query(uri, EVENTS_PROJECTION, cursorString,
				args, sortOrder);
		if (cursor.getCount() == 0) {
			cursor.close();
			return ConflictionStatus.NO_CONFLICTION;
		} else {
			boolean fullConfliction = true;
			Long lastEnd = event.getStart();
			Long start = null;
			while (cursor.moveToNext()) {

				start = cursor.getLong(START_INDEX);
				if (start.compareTo(lastEnd) < 0) {
					fullConfliction = false;
					break;
				}
				lastEnd = cursor.getLong(END_INDEX);
			}

			if (fullConfliction && (lastEnd.compareTo(event.getEnd()) < 0)) {
				fullConfliction = false;
			}

			cursor.close();
			if (fullConfliction) {
				return ConflictionStatus.COMPLETE_CONFLICTION;
			} else {
				return ConflictionStatus.PARTIAL_CONFLICTION;
			}
		}

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
		if (hasLoadedHostedEvents && hasLoadedJoinedEvents
				&& hasLoadedWatchingEvents && hasLoadedOtherEvents) {
			hasLoadedInitial = true;
			Log.d(TAG, "Has Loaded Initial, " + events.size() + " held events");

			for (EventListAdapter adapter : waitingListAdapters) {
				adapter.notifyDataSetChanged();
			}

		}
	}

	private void addEventToCalendar(Context context, ZeppaEvent event,
			ContentResolver resolver) {

		ContentValues values = new ContentValues();
		Calendar c = Calendar.getInstance();

		int calendarId = PreferencesHelper.getInstance(context)
				.getPrefs(context)
				.getInt(Constants.ZEPPA_INTERNAL_CALENDAR_ID, -1);

		if (calendarId < 0) {
			Log.d(TAG,
					"couldnt find calendar id, returning w/out adding to internal calendar");
			return;
		}

		values.put(Events.CALENDAR_ID, calendarId);
		values.put(Events.TITLE, event.getTitle());
		values.put(Events.DESCRIPTION, event.getDescription());
		values.put(Events.DTSTART, event.getStart());
		values.put(Events.DTEND, event.getEnd());
		values.put(Events.EVENT_TIMEZONE, c.getTimeZone().getID());

		values.put(Events.HAS_ALARM, 0);

		Uri uri = resolver.insert(Events.CONTENT_URI, values);

		Long eventId = Long.parseLong(uri.getLastPathSegment());
		ZeppaEventDBHelper helper = new ZeppaEventDBHelper(context);

		helper.linkEventIds(event, eventId);
		new ImportEntries().execute(context);
	}

	private void removeEventFromCalendar(Context context, ZeppaEvent event) {

		ZeppaEventDBHelper helper = new ZeppaEventDBHelper(context);
		long eventId = helper.getMatchingId(event);

		if (eventId < 0) {
			Log.d(TAG, "Didnt find matching event");
			return;
		}

		Uri deleteUri = ContentUris.withAppendedId(Events.CONTENT_URI, eventId);
		context.getContentResolver().delete(deleteUri, null, null);
	}

	private void addEventsNoRepeat(List<ZeppaEvent> newEvents) {
		if (events.isEmpty()) {
			events.addAll(newEvents);
		} else {
			for (ZeppaEvent newEvent : newEvents) {
				boolean doAdd = true;
				for (ZeppaEvent heldEvent : events) {
					if (heldEvent.equals(newEvent)) {
						heldEvent = newEvent;
						doAdd = false;
					}
				}

				if (doAdd) {
					events.add(newEvent);
				}
			}
		}
		if (!events.isEmpty())
			Collections.sort(events, Constants.EVENT_COMPARATOR);
	}

	/*
	 * ----------- Datastore Management Methods ------------
	 */

	public boolean createZeppaEvent(Context contex,
			GoogleAccountCredential credential, ZeppaEvent event,
			ContentResolver resolver) {
		boolean success = false;
		Zeppaeventendpoint.Builder endpointBuilder = new Zeppaeventendpoint.Builder(
				AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
				credential);
		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
		Zeppaeventendpoint eventEndpoint = endpointBuilder.build();

		try {
			ZeppaEvent result = event = eventEndpoint.insertZeppaEvent(event)
					.execute();

			success = true;

			events.add(result);
			Collections.sort(events, Constants.EVENT_COMPARATOR);

			addEventToCalendar(contex, event, resolver);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return success;
	}

	public boolean deleteZeppaEvent(Context contex,
			GoogleAccountCredential credential, ZeppaEvent event,
			ContentResolver resolver) {
		boolean success = false;
		Zeppaeventendpoint.Builder endpointBuilder = new Zeppaeventendpoint.Builder(
				AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
				credential);
		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
		Zeppaeventendpoint eventEndpoint = endpointBuilder.build();
		try {
			eventEndpoint.removeZeppaEvent(event.getKey().getId()).execute();
			success = true;

			events.remove(event);

			removeEventFromCalendar(contex, event);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return success;
	}

	public boolean joinZeppaEvent(Context context,
			GoogleAccountCredential credential, ZeppaEvent event) {
		ContentResolver resolver = context.getContentResolver();
		boolean success = false;
		Zeppaeventendpoint.Builder endpointBuilder = new Zeppaeventendpoint.Builder(
				AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
				credential);
		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
		Zeppaeventendpoint endpoint = endpointBuilder.build();

		try {
			endpoint.addUserGoing(event.getKey().getId(), getUserId())
					.execute();

			success = true;
			event.getUsersWatchingIds().remove(getUserId());

			addEventToCalendar(context, event, resolver);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return success;
	}

	public boolean leaveZeppaEvent(Context context,
			GoogleAccountCredential credential, ZeppaEvent event) {
		boolean success = false;

		Zeppaeventendpoint.Builder endpointBuilder = new Zeppaeventendpoint.Builder(
				AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
				credential);
		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
		Zeppaeventendpoint endpoint = endpointBuilder.build();

		try {
			endpoint.removeUserGoing(event.getKey().getId(), getUserId())
					.execute();
			success = true;

			removeEventFromCalendar(context, event);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return success;
	}

	public boolean watchZeppaEvent(GoogleAccountCredential credential,
			ZeppaEvent event) {
		boolean success = false;

		Zeppaeventendpoint.Builder endpointBuilder = new Zeppaeventendpoint.Builder(
				AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
				credential);
		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
		Zeppaeventendpoint endpoint = endpointBuilder.build();

		try {
			endpoint.addUserWatching(event.getKey().getId(), getUserId())
					.execute();

			success = true;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return success;
	}

	public boolean stopWatchingZeppaEvent(GoogleAccountCredential credential,
			ZeppaEvent event) {
		boolean success = false;

		Zeppaeventendpoint.Builder endpointBuilder = new Zeppaeventendpoint.Builder(
				AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
				credential);
		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
		Zeppaeventendpoint endpoint = endpointBuilder.build();

		try {
			endpoint.removeUserWatching(event.getKey().getId(), getUserId())
					.execute();

			success = true;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return success;
	}

	public boolean repostedEvent(Context context,
			GoogleAccountCredential credential, ZeppaEvent original,
			List<Long> tagIds, List<Long> inviteIds) {
		boolean success = false;

		ZeppaEvent event = newRepostEventInstance(original);
		event.getTagIds().addAll(tagIds);
		event.getInvites().addAll(inviteIds);

		Zeppaeventendpoint.Builder endpointBuilder = new Zeppaeventendpoint.Builder(
				AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
				credential);
		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
		Zeppaeventendpoint endpoint = endpointBuilder.build();

		try {

			event = endpoint.repostZeppaEvent(event).execute();
			success = true;
			if (!original.getUsersGoingIds().contains(getUserId())) {
				joinZeppaEvent(context, credential, event);
			}

			events.add(event);

			original.getReposts().put(String.valueOf(getUserId()),
					event.getKey().getId());

			if (original.getUsersGoingIds() == null)
				original.setUsersGoingIds(new ArrayList<Long>());

			if (!original.getUsersGoingIds().contains(getUserId())) {
				original.getUsersGoingIds().add(getUserId());
			}

			if (original.getUsersWatchingIds() != null) {
				original.getUsersWatchingIds().remove(getUserId());
			}
			Collections.sort(events, Constants.EVENT_COMPARATOR);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return success;
	}

	/*
	 * ------------ Loader Method ---------------
	 */

	public boolean loadEventsForUser(GoogleAccountCredential credential,
			Long userId) {
		boolean success = false;
		List<ZeppaEvent> oldEvents = new ArrayList<ZeppaEvent>();
		for (ZeppaEvent event : events)
			if (event.getHostId().longValue() == userId.longValue())
				oldEvents.add(event);

		Zeppaeventendpoint.Builder endpointBuilder = new Zeppaeventendpoint.Builder(
				AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
				credential);
		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
		Zeppaeventendpoint endpoint = endpointBuilder.build();
		int start = 0;
		Long time = System.currentTimeMillis();
		List<ZeppaEvent> updated = new ArrayList<ZeppaEvent>();
		boolean keepGoing = true;
		while (keepGoing) {
			keepGoing = false;
			try {
				CollectionResponseZeppaEvent collection = endpoint
						.fetchJoinableUserEvents(userId, getUserId(), start,
								time).execute();
				success = true;
				if (collection.getItems() != null && !collection.isEmpty()) {
					keepGoing = collection.getItems().size() >= 10;
					updated.addAll(collection.getItems());

				} else {
					break;
				}

			} catch (IOException ex) {
				ex.printStackTrace();
			}
			if (keepGoing) {
				start += 10;
			}

		}

		events.removeAll(oldEvents);
		events.addAll(updated);

		Collections.sort(events, Constants.EVENT_COMPARATOR);
		return success;
	}

	private void loadPriorEvents() {

	}

	public boolean loadNewEvents(GoogleAccountCredential credential) {
		boolean success = false;

		Zeppaeventendpoint.Builder endpointBuilder = new Zeppaeventendpoint.Builder(
				AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
				credential);
		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
		Zeppaeventendpoint endpoint = endpointBuilder.build();
		int start = 0;
		Long time = System.currentTimeMillis();

		boolean keepGoing = true;
		while (keepGoing) {
			keepGoing = false;
			try {
				CollectionResponseZeppaEvent collection = endpoint
						.fetchRecentlyPostedEvents(getUserId(), lastCallTime,
								start).execute();
				success = true;
				if (collection.getItems() != null && !collection.isEmpty()) {
					keepGoing = collection.getItems().size() >= 10;
					List<ZeppaEvent> retrieved = collection.getItems();

					for (ZeppaEvent event : retrieved) {
						if (events.contains(event)) {
							events.remove(event);
						}
					}
					events.addAll(retrieved);

				} else {
					break;
				}

			} catch (IOException ex) {
				ex.printStackTrace();
			}

			if (keepGoing) {
				start += 10;
			}
		}

		if (success) {
			lastCallTime = time;
		}

		Collections.sort(events, Constants.EVENT_COMPARATOR);
		return success;
	}

	private void loadInitialEventsInAsync(GoogleAccountCredential credential) {

		GoogleAccountCredential[] params = { credential };
		/*
		 * Following task loads hosted events
		 */
		new AsyncTask<GoogleAccountCredential, Void, Void>() {

			@Override
			protected Void doInBackground(GoogleAccountCredential... params) {

				GoogleAccountCredential credential = params[0];

				Zeppaeventendpoint.Builder endpointBuilder = new Zeppaeventendpoint.Builder(
						AndroidHttp.newCompatibleTransport(),
						new JacksonFactory(), credential);
				endpointBuilder = CloudEndpointUtils
						.updateBuilder(endpointBuilder);
				Zeppaeventendpoint endpoint = endpointBuilder.build();
				int start = 0;
				Long currentTime = System.currentTimeMillis();
				while (true) {
					try {
						FetchHostedEvents fetchHostedEvents = endpoint
								.fetchHostedEvents(getUserId(), currentTime,
										start);
						CollectionResponseZeppaEvent collectionResponse = fetchHostedEvents
								.execute();
						Log.d(TAG, "Collection resonse on fetch Hosted");
						if (collectionResponse.getItems() != null
								&& !collectionResponse.getItems().isEmpty()) {

							List<ZeppaEvent> events = collectionResponse
									.getItems();
							addEventsNoRepeat(events);

							Log.d(TAG, "Fetched " + events.size() + " hosted at " + System.currentTimeMillis());
							if (events.size() < 10) {
								break;
							} else {
								start += 10;
							}
						} else {
							break;
						}

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
				Log.d(TAG, "Finished fetching hosted: " + System.currentTimeMillis());

				hasLoadedHostedEvents = true;
				trySetAllLoaded();

			}

		}.execute(params);
		Log.d(TAG, "Executing fetch hosted event tasks at: " + System.currentTimeMillis());


		/*
		 * Following task loads joined events
		 */
		new AsyncTask<GoogleAccountCredential, Void, Void>() {

			@Override
			protected Void doInBackground(GoogleAccountCredential... params) {
				GoogleAccountCredential credential = params[0];
				Zeppaeventendpoint.Builder endpointBuilder = new Zeppaeventendpoint.Builder(
						AndroidHttp.newCompatibleTransport(),
						new JacksonFactory(), credential);
				endpointBuilder = CloudEndpointUtils
						.updateBuilder(endpointBuilder);
				Zeppaeventendpoint endpoint = endpointBuilder.build();
				int start = 0;
				while (true) {
					try {
						FetchAttendingEvents fetchAttendingEvents = endpoint
								.fetchAttendingEvents(getUserId(),
										lastCallTime, start);
						CollectionResponseZeppaEvent collectionResponse = fetchAttendingEvents
								.execute();

						if (collectionResponse.getItems() != null
								&& !collectionResponse.getItems().isEmpty()) {

							List<ZeppaEvent> events = collectionResponse
									.getItems();
							addEventsNoRepeat(events);

							Log.d(TAG, "Fetched Joined: " + events.size());
							if (events.size() < 10) {
								break;
							} else {
								start += 10;
							}
						} else {
							break;
						}

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
				Log.d(TAG, "Finished fetch joined task: " + System.currentTimeMillis());
				hasLoadedJoinedEvents = true;
				trySetAllLoaded();
			}

		}.execute(params);

		Log.d(TAG, "Executing fetch joined at: " + System.currentTimeMillis());

		
		/*
		 * Following task loads watched events
		 */
		new AsyncTask<GoogleAccountCredential, Void, Void>() {

			@Override
			protected Void doInBackground(GoogleAccountCredential... params) {
				GoogleAccountCredential credential = params[0];
				Zeppaeventendpoint.Builder endpointBuilder = new Zeppaeventendpoint.Builder(
						AndroidHttp.newCompatibleTransport(),
						new JacksonFactory(), credential);
				endpointBuilder = CloudEndpointUtils
						.updateBuilder(endpointBuilder);
				Zeppaeventendpoint endpoint = endpointBuilder.build();
				int start = 0;
				while (true) {
					try {
						FetchWatchingEvents fetchWatchingEvents = endpoint
								.fetchWatchingEvents(getUserId(), lastCallTime,
										start);
						CollectionResponseZeppaEvent collectionResponse = fetchWatchingEvents
								.execute();
						if (collectionResponse.getItems() != null
								&& !collectionResponse.getItems().isEmpty()) {

							List<ZeppaEvent> events = collectionResponse
									.getItems();
							addEventsNoRepeat(events);

							Log.d(TAG,
									"Loaded Watching event: " + events.size());
							if (events.size() < 10) {
								break;
							} else {
								start += 10;
							}
						} else {
							break;
						}

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
				Log.d(TAG, "Finished executing fetch watching at: " + System.currentTimeMillis());

				hasLoadedWatchingEvents = true;
				trySetAllLoaded();
			}

		}.execute(params);
		Log.d(TAG, "Executing fetch watching at: " + System.currentTimeMillis());


		/*
		 * Following task loads other events
		 */
		new AsyncTask<GoogleAccountCredential, Void, Void>() {

			@Override
			protected Void doInBackground(GoogleAccountCredential... params) {

				GoogleAccountCredential credential = params[0];
				Zeppaeventendpoint.Builder endpointBuilder = new Zeppaeventendpoint.Builder(
						AndroidHttp.newCompatibleTransport(),
						new JacksonFactory(), credential);
				endpointBuilder = CloudEndpointUtils
						.updateBuilder(endpointBuilder);
				Zeppaeventendpoint endpoint = endpointBuilder.build();
				int start = 0;

				while (true) {
					try {
						FetchPossibleEvents fetchEvents = endpoint
								.fetchPossibleEvents(getUserId(), lastCallTime,
										start);

						CollectionResponseZeppaEvent collectionResponse = fetchEvents
								.execute();

						Log.d(TAG, "Fetched Possible: " + events.size());
						if (collectionResponse.getItems() != null
								&& !collectionResponse.getItems().isEmpty()) {
							List<ZeppaEvent> events = collectionResponse
									.getItems();
							addEventsNoRepeat(events);
							if (events.size() < 10) {
								break;
							} else {
								start += 10;
							}

						} else {
							break;
						}

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
				Log.d(TAG, "Finished executing fetch possible at: " + System.currentTimeMillis());

				hasLoadedOtherEvents = true;
				trySetAllLoaded();
			}

		}.execute(params);
		Log.d(TAG, "Executing fetch possible at: " + System.currentTimeMillis());


	}

	public void loadEventsForUser(Long userId,
			GoogleAccountCredential credential) {
		Zeppaeventendpoint.Builder endpointBuilder = new Zeppaeventendpoint.Builder(
				AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
				credential);
		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
		Zeppaeventendpoint endpoint = endpointBuilder.build();
		int start = 0;
		Long currentTime = System.currentTimeMillis();
		while (true) {
			try {
				FetchJoinableUserEvents fetchUserEvents = endpoint
						.fetchJoinableUserEvents(userId, getUserId(), start,
								currentTime);
				CollectionResponseZeppaEvent collectionResponse = fetchUserEvents
						.execute();
				if (collectionResponse != null
						&& collectionResponse.getItems() != null) {
					List<ZeppaEvent> events = collectionResponse.getItems();
					addEventsNoRepeat(events);
					if (events.size() == 10) {
						start += 10;
					} else {
						break;
					}

				} else {
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}

		}

	}

	public void loadNewZeppaEvents(GoogleAccountCredential credential) {

		Zeppaeventendpoint.Builder endpointBuilder = new Zeppaeventendpoint.Builder(
				AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
				credential);
		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
		Zeppaeventendpoint endpoint = endpointBuilder.build();
		int start = 0;

		while (true) {
			try {

				FetchRecentlyPostedEvents fetchEvents = endpoint
						.fetchRecentlyPostedEvents(getUserId(), lastCallTime,
								start);
				lastCallTime = System.currentTimeMillis();
				CollectionResponseZeppaEvent collectionResponse = fetchEvents
						.execute();

				if (collectionResponse.getItems() != null
						&& !collectionResponse.getItems().isEmpty()) {
					List<ZeppaEvent> events = collectionResponse.getItems();
					addEventsNoRepeat(events);
					if (events.size() < 10) {
						break;
					} else {
						start += 10;
					}

				} else {
					break;
				}

			} catch (IOException e) {
				e.printStackTrace();
				break;
			}

		}

	}

}
