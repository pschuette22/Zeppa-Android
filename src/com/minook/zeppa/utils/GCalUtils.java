package com.minook.zeppa.utils;

import java.io.IOException;
import java.util.List;

import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Event.Creator;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.minook.zeppa.mediator.MyZeppaUserMediator;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;

/**
 * This handles all transactions with the google calendar. </p> It will also
 * interact with the calendar view library to keep things in sync
 * 
 */
public class GCalUtils {

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
	 * This method creates a calendar to be used for the current user to hold
	 * Zeppa Events important to them
	 * 
	 * @param credential
	 * @return calendar created
	 * @throws IOException
	 */
	public static Calendar createZeppaCalendar(GoogleCredential credential)
			throws IOException {

		Calendar zeppaCalendar = new Calendar();

		com.google.api.services.calendar.Calendar client = new com.google.api.services.calendar.Calendar.Builder(
				AndroidHttp.newCompatibleTransport(),
				AndroidJsonFactory.getDefaultInstance(), credential).build();

		zeppaCalendar.setSummary("Zeppa Events");
		zeppaCalendar
				.setDescription("Google Calendar for the Zeppa Application");

		Calendar result = client.calendars().insert(zeppaCalendar).execute();

		return result;
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
	public static ZeppaEvent createZeppaEventInCal(ZeppaEvent zeppaEvent,
			String calendarId, GoogleCredential credential) throws IOException {

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
		endTime.setDateTime(new DateTime(zeppaEvent.getEnd().longValue()));
		startTime.setDateTime(new DateTime(zeppaEvent.getStart().longValue()));

		// Event details
		calendarEvent.setEnd(endTime);
		calendarEvent.setStart(startTime);
		calendarEvent.setAnyoneCanAddSelf(true); // so others may join
		calendarEvent.setGuestsCanModify(false); // but cannot edit

		calendarEvent.setCreated(new DateTime(System.currentTimeMillis()));

		com.google.api.services.calendar.Calendar client = new com.google.api.services.calendar.Calendar.Builder(
				new NetHttpTransport(), new JacksonFactory(), credential)
				.build();

		Event result = client.events().insert(calendarId, calendarEvent)
				.execute();

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
				new NetHttpTransport(), new JacksonFactory(), credential)
				.build();
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
				new NetHttpTransport(), new JacksonFactory(), credential)
				.build();
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

}
