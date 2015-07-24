package com.pschuette.android.calendarlibrary;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.CalendarContract.Instances;
import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class Day {

	public interface OnEventsLoadListener {
		public void onEventLoadComplete();
	}

	// int startDay;
	// int monthEndDay;
	int day;
	int year;
	int month;
	long dayInMillis = 86400000;
	Calendar cal;
	Context context;
	CalendarAdapter adapter;
	ArrayList<Event> events = new ArrayList<Event>();
	private GetEvents eventsTask;
	private OnEventsLoadListener loadListener;
	private boolean eventFetchCompleted;
	private View hasEventsIndicatorView;

	private static final String[] INSTANCE_PROJECTION = new String[] {
			Instances.EVENT_ID, // 0
			Instances.TITLE, // 1
			Instances.DESCRIPTION, // 2
			Instances.EVENT_LOCATION, // 3
			Instances.BEGIN, // 4
			Instances.END, // 5
			Instances.CALENDAR_COLOR }; // 7

	// Selection: Begin is less than maxMillis and more than minMillis,
	// AND End is less than maxMillis and more than minMillis,
	// AND Begin is less than minMillis and End is greater than maxMillis

	Day(Context context, int day, int year, int month) {
		this.day = day;
		this.year = year;
		this.month = month;
		this.context = context;
		this.eventFetchCompleted = false;
		cal = Calendar.getInstance();
		cal.set(year, month, day);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);

	}

	public int getMonth() {
		return month;
	}

	public int getYear() {
		return year;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getDay() {
		return day;
	}

	public int getDayOfWeek() {
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	public Calendar getCalendarDay() {
		return cal;
	}

	public void setHasEventsIndicatorView(View indicatorView){
		hasEventsIndicatorView = indicatorView;
	}
	
	/**
	 * Add an event to the day
	 * 
	 * @param event
	 */
	public void addEvent(Event event) {
		events.add(event);
	}

	/**
	 * Set the start day
	 * 
	 * @param startDay
	 */
	public void startFetchEventsTask(OnEventsLoadListener listener) {
		this.loadListener = listener;
		eventsTask = new GetEvents();
		eventsTask.execute();
	}

	public void stopFetchingEvents() {
		eventsTask.cancel(true);
	}

	public int getNumOfEvents() {
		return events.size();
	}

	public void registerOnEventsLoadListener(OnEventsLoadListener listener) {
		this.loadListener = listener;
	}

	public void unregisterOnEventsLoadListener(OnEventsLoadListener listener) {
		if (loadListener == listener) {
			loadListener = null;
		}
	}

	/**
	 * Returns a list of all the colors on a day
	 * 
	 * @return list of colors
	 */
	public Set<Integer> getColors() {
		Set<Integer> colors = new HashSet<Integer>();
		for (Event event : events) {
			colors.add(event.getColor());
		}

		return colors;
	}

	/**
	 * Get all the events on the day
	 * 
	 * @return list of events
	 */
	public ArrayList<Event> getEvents() {
		return events;
	}

	public void setAdapter(CalendarAdapter adapter) {
		this.adapter = adapter;
	}

	private class GetEvents extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			ContentResolver cr = context.getContentResolver();

			Calendar calDay = Calendar.getInstance();
			calDay.set(Calendar.YEAR, cal.get(Calendar.YEAR));
			calDay.set(Calendar.MONTH, cal.get(Calendar.MONTH));
			calDay.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));

			// Zero out the calendar Day
			calDay.set(Calendar.MILLISECOND, 0);
			calDay.set(Calendar.SECOND, 0);
			calDay.set(Calendar.MINUTE, 0);
			calDay.set(Calendar.HOUR_OF_DAY, 0);

			// Format for getting it in millis
			// if (calDay.get(Calendar.MONDAY) == Calendar.JANUARY) {
			// calDay.add(Calendar.YEAR, -1);
			// calDay.set(Calendar.MONTH, Calendar.DECEMBER);
			// } else {
			// calDay.add(Calendar.MONTH, -1);
			// }

			long startDayMillis = calDay.getTimeInMillis();
			long endDayMillis = startDayMillis + dayInMillis;

			// String selection = Instances.AVAILABILITY + " == " +
			// Instances.AVAILABILITY_BUSY;

			Cursor c = Instances.query(cr, INSTANCE_PROJECTION, startDayMillis,
					endDayMillis);
			try {
				if (c != null && c.moveToFirst()) {
					do {
						Event event = new Event(c.getLong(0), c.getLong(4),
								c.getLong(5));
						event.setName(c.getString(1));
						event.setDescription(c.getString(2));
						event.setLocation(c.getString(3));

						String colorString = c.getString(6);
						
						int calColor = Integer.parseInt(colorString);

						event.setColor(calColor);

						events.add(event);

					} while (c.moveToNext());

				}

			} finally {

				c.close();
			}

			return null;
		}

		protected void onPostExecute(Void par) {
			try {
				adapter.notifyDatasetChangedForDay(Day.this);

				if (loadListener != null) {
					loadListener.onEventLoadComplete();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
