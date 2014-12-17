package com.minook.zeppa.utils;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Event.Creator;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.R;
import com.minook.zeppa.mediator.MyZeppaUserMediator;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;

/**
 * This handles all transactions with the google calendar. </p> It will also
 * interact with the calendar view library to keep things in sync
 * 
 */
public class GCalUtils {

	private static final String TAG = "GCalUtils";
	private static final String TESTCALENDARID = "TestCalendarID";

	private static final HttpTransport HTTP_TRANSPORT = AndroidHttp
			.newCompatibleTransport();
	private static final JsonFactory JSON_FACTORY = GsonFactory
			.getDefaultInstance();

	/*
	 * Calendar Clients used for interacting with the Google Calendar API
	 */

	/**
	 * 
	 * @param credential
	 * @return client to interact with Google Calendar Objects
	 */
	private static final com.google.api.services.calendar.Calendar getCalendarClient(
			GoogleAccountCredential credential) {
		com.google.api.services.calendar.Calendar.Builder builder = new com.google.api.services.calendar.Calendar.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, credential);

		CloudEndpointUtils.updateBuilder(builder);

		return builder.build();
	}

	/**
	 * This gets the zeppa CalendarListEntry if it exists
	 * @param credential
	 *            - authentication credential
	 * @return zeppaCalendarListEntry - CalendarListEntry for Zeppa Calendar
	 * @throws IOException
	 *             - thrown if object does not exist for this ID
	 */
	public static Calendar fetchZeppaCalendar(
			GoogleAccountCredential credential, String calendarId) throws IOException {
		
		try {
		return getCalendarClient(credential).calendars().get(calendarId).execute();
		
		} catch (IOException e){
			handleGoogleException(e);
			throw e;
		}
	}

	/**
	 * Inserts a new instance of the Zeppa Calendar into the users calendar list
	 * 
	 * @param context
	 * @param credential
	 * @return
	 * @throws IOException
	 */
	public static Calendar insertZeppaCalendar(Context context,
			GoogleAccountCredential credential) throws IOException {
		com.google.api.services.calendar.Calendar client = getCalendarClient(credential);

		Calendar zeppaCalendar = new Calendar();

		zeppaCalendar.setDescription(context.getResources().getString(
				R.string.zeppacalendar_description));
		zeppaCalendar.setSummary(context.getResources().getString(
				R.string.zeppacalendar_summary));
		
		return client.calendars().insert(zeppaCalendar).execute();
	}
	
	
//	/**
//	 * 
//	 * @param context
//	 * @param credentail
//	 * @param summary
//	 * @param description
//	 * @return
//	 */
//	private static CalendarListEntry insertZeppaCalendarListEntry(
//			Context context, GoogleAccountCredential credentail, String summary, String description,String calendarId) {
//		
//		CalendarListEntry instance = new CalendarListEntry();
//
//		instance.setId(calendarId);
//		instance.setAccessRole("owner");
//		instance.setBackgroundColor("#0AD2FF");
//		instance.setForegroundColor("#FFFFFF");
//		instance.setSummary(summary);
//		instance.setDescription(description);
//		instance.setSelected(true);
//		instance.setPrimary(false);
//		instance.setDeleted(false);
//		
//		return instance;
//	}

	/**
	 * @return creator object for current user
	 */
	private static Creator getUserAsCreator() {
		MyZeppaUserMediator mediator = ZeppaUserSingleton.getInstance()
				.getUserMediator();

		Creator creator = new Creator();
		creator.setDisplayName(mediator.getDisplayName());
		creator.setEmail(mediator.getGmail());
		creator.setSelf(true);

		return null;
	}

	/**
	 * @return eventAttendee object of current user
	 */
	private static EventAttendee getUserAsAttendee() {

		MyZeppaUserMediator mediator = ZeppaUserSingleton.getInstance()
				.getUserMediator();

		EventAttendee attendee = new EventAttendee();

		attendee.setDisplayName(mediator.getDisplayName());
		attendee.setEmail(mediator.getGmail());
		attendee.setOrganizer(false);
		attendee.setSelf(true);

		return attendee;
	}

	/**
	 * This method creates a Calendar instance of a ZeppaEvent and inserts it
	 * into the calendar.</p> This process occurs before it is inserted into the
	 * App Engine Datastore
	 * 
	 * @param event
	 * @param calendarId
	 *            of ZeppaEvents Calendar
	 * @return updated event with ids and tokens or null if error occurs
	 */
	public static ZeppaEvent putZeppaEventInCal(Context context,
			ZeppaEvent zeppaEvent, GoogleAccountCredential credential)
			throws IOException {

		Event calendarEvent = new Event(); // New Event instance

		calendarEvent.setCreator(getUserAsCreator());
		calendarEvent.setSummary(zeppaEvent.getTitle());
		calendarEvent.setDescription(zeppaEvent.getDescription());

		/*
		 * This id for constructing start and end time. Seems like a lot for
		 * such a simple task
		 */
		EventDateTime endTime = new EventDateTime();
		EventDateTime startTime = new EventDateTime();
		endTime.setDateTime(new DateTime(zeppaEvent.getEnd()));
		startTime.setDateTime(new DateTime(zeppaEvent.getStart()));

		// Event details
		calendarEvent.setEnd(endTime);
		calendarEvent.setStart(startTime);
		calendarEvent.setAnyoneCanAddSelf(true); // so others may join
		calendarEvent.setGuestsCanModify(false); // but cannot edit

		calendarEvent.setGuestsCanInviteOthers(guestsCanInviteOthers(zeppaEvent.getPrivacy()));
		calendarEvent.setCreated(new DateTime(System.currentTimeMillis()));


		Event result = getCalendarClient(credential).events()
				.insert(zeppaEvent.getGoogleCalendarId(), calendarEvent).execute();

		zeppaEvent.setGoogleCalendarEventId(result.getId());
		zeppaEvent.setICalUID(result.getICalUID());

		return zeppaEvent;
	}

	/**
	 * This method deletes the calendar instance of a Calendar Event
	 * 
	 * @param event
	 * @return true if the calendar event was deleted
	 */

	public static boolean didDeleteZeppaEventInCal(String calendarId,
			ZeppaEvent event, GoogleCredential credential) throws IOException {

		com.google.api.services.calendar.Calendar client = new com.google.api.services.calendar.Calendar.Builder(
				AndroidHttp.newCompatibleTransport(),
				AndroidJsonFactory.getDefaultInstance(), credential).build();

		client.events().delete(calendarId, event.getGoogleCalendarEventId())
				.execute();

		return true;
	}

	/**
	 * This method adds the current user as an attendee to a given calendar
	 * event
	 * 
	 * @param zeppaEvent
	 *            user wishes to join.
	 * @throws IOException
	 */
	public static void joinZeppaEvent(ZeppaEvent zeppaEvent,
			GoogleCredential credential) throws IOException {

		com.google.api.services.calendar.Calendar client = new com.google.api.services.calendar.Calendar.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, credential).build();
		Event currentEvent = client
				.events()
				.get(zeppaEvent.getGoogleCalendarId(),
						zeppaEvent.getGoogleCalendarEventId()).execute();

		List<EventAttendee> attendees = currentEvent.getAttendees();
		attendees.add(getUserAsAttendee().setResponseStatus("accepted"));
		currentEvent.setAttendees(attendees);

		client.events()
				.update(zeppaEvent.getGoogleCalendarId(),
						zeppaEvent.getGoogleCalendarEventId(), currentEvent)
				.execute();
	}

	/**
	 * This method removes a user from the attendee list of a given event
	 * 
	 * @param zeppaEvent
	 *            user is leaving
	 * @return success - true if successfully completed transaction of removing
	 *         user from attendee list
	 * @throws IOException
	 */
	public static boolean didLeaveZeppaEvent(ZeppaEvent zeppaEvent,
			GoogleCredential credential) throws IOException {

		com.google.api.services.calendar.Calendar client = new com.google.api.services.calendar.Calendar.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, credential).build();
		Event currentEvent = client
				.events()
				.get(zeppaEvent.getGoogleCalendarId(),
						zeppaEvent.getGoogleCalendarEventId()).execute();

		List<EventAttendee> attendees = currentEvent.getAttendees();

		boolean success = attendees.remove(getUserAsAttendee());

		if (success) {
			currentEvent.setAttendees(attendees);

			client.events()
					.update(zeppaEvent.getGoogleCalendarId(),
							zeppaEvent.getGoogleCalendarEventId(), currentEvent)
					.execute();
		} else {
			Log.d("TAG", "didn't find attendee in list... damn");
		}

		return success;
	}

	// public boolean didUpdateZeppaEvent(ZeppaEvent updatedEvent) {
	// boolean success = false;
	//
	// return success;
	// }


	/**
	 * Taken from Google example. This is not needed for now but will remain in
	 * case I find a use for it.
	 * 
	 * @param e
	 */
	private static void handleGoogleException(IOException e) {
		if (e instanceof GoogleJsonResponseException) {
			GoogleJsonResponseException exception = (GoogleJsonResponseException) e;
			// TODO(yanivi): should only try this once to avoid infinite loop
			if (exception.getStatusCode() == 401) {

				return;
			}
		}
		Log.e(TAG, e.getMessage(), e);
	}
	
	private static boolean guestsCanInviteOthers(String zeppaEventPrivacy){
		return (zeppaEventPrivacy.equals("CASUAL") || zeppaEventPrivacy.equals("PUBLIC"));
	}

}
