package com.pschuette.android.calendarlibrary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class CalendarAdapter extends BaseAdapter {

	static final int FIRST_DAY_OF_WEEK = 0;
	Context context;
	Calendar cal;
	public String[] days;
	private HashMap<Day, View> dayViewMap = new HashMap<Day, View>();
	// private List<View> dayViews = new ArrayList<View>();
	// OnAddNewEventClick mAddEvent;

	ArrayList<Day> dayList = new ArrayList<Day>();

	public CalendarAdapter(Context context, Calendar cal) {
		this.cal = cal;
		this.context = context;
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		refreshDays();
	}

	@Override
	public int getCount() {
		return days.length;
	}

	@Override
	public Day getItem(int position) {
		return dayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public int getPrevMonth() {
		if (cal.get(Calendar.MONTH) == cal.getActualMinimum(Calendar.MONTH)) {
			cal.set(Calendar.YEAR, cal.get(Calendar.YEAR - 1));
		}

		int month = cal.get(Calendar.MONTH);
		if (month == 0) {
			return month = 11;
		}

		return month - 1;
	}

	public int getMonth() {
		return cal.get(Calendar.MONTH);
	}

	public int getYear() {
		return cal.get(Calendar.YEAR);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (convertView == null) {
			convertView = vi.inflate(R.layout.day_view, parent, false);
		}

		convertView.setLayoutParams(new TableLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));

		FrameLayout today = (FrameLayout) convertView
				.findViewById(R.id.today_frame);
		Calendar cal = Calendar.getInstance(TimeZone.getDefault(),
				Locale.getDefault());
		Day d = dayList.get(position);
		if (d.getYear() == cal.get(Calendar.YEAR)
				&& d.getMonth() == cal.get(Calendar.MONTH)
				&& d.getDay() == cal.get(Calendar.DAY_OF_MONTH)) {
			today.setVisibility(View.VISIBLE);
		} else {
			today.setVisibility(View.INVISIBLE);
		}

		TextView dayTV = (TextView) convertView.findViewById(R.id.textView1);

		FrameLayout hasEvents = (FrameLayout) convertView
				.findViewById(R.id.hasevents_frame);

		hasEvents.setVisibility(View.VISIBLE);
		dayTV.setVisibility(View.VISIBLE);
		convertView.setVisibility(View.VISIBLE);

		Day day = dayList.get(position);

		if (day.getNumOfEvents() > 0) {
			hasEvents.setVisibility(View.VISIBLE);
		} else {
			hasEvents.setVisibility(View.INVISIBLE);
		}

		if (day.getDay() == 0) {
			convertView.setVisibility(View.INVISIBLE);
		} else {
			dayTV.setVisibility(View.VISIBLE);
			dayTV.setText(String.valueOf(day.getDay()));
			convertView.setTag(day);
		}

		dayViewMap.put(day, convertView);

		return convertView;
	}

	public void refreshDays() {
		// clear items
		dayList.clear();

		int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		int firstDay = cal.get(Calendar.DAY_OF_WEEK) - 1; // make this on a 0-6
															// basis
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);

		// figure size of the array
		int arraySize = lastDay + firstDay;

		int endBuffer = 7 - (arraySize % 7);
		if (endBuffer < 7) {
			arraySize += endBuffer;
		}

		days = new String[arraySize];

		int j = 0;

		// populate empty days before first real day
		if (firstDay > 0) {
			while (j < firstDay) {
				days[j] = "";
				Day d = new Day(context, 0, 0, 0);
				dayList.add(d);
				j++;
			}
		}

		// Populate non-empty days;
		while (j < firstDay + lastDay) {
			int day = (j - firstDay + 1);
			days[j] = "" + day;
			Day d = new Day(context, day, year, month);
			Calendar cTemp = Calendar.getInstance();
			cTemp.set(year, month, day);

			d.setAdapter(this);
			d.startFetchEventsTask(null);

			dayList.add(d);
			j++;
		}

		// populate empty days at the end of month
		if (endBuffer > 0) {
			while (j < arraySize) {
				days[j] = "";
				Day d = new Day(context, 0, 0, 0);
				dayList.add(d);
				j++;
			}
		}

	}

	public void notifyDatasetChangedForDay(Day day) {
		// if(!day.getEvents().isEmpty()){
		View dayView = dayViewMap.get(day);

		if (dayView != null) {
			FrameLayout frame = (FrameLayout) dayView
					.findViewById(R.id.hasevents_frame);
			if (day.getEvents().isEmpty()) {
				frame.setVisibility(View.INVISIBLE);
			} else {
				frame.setVisibility(View.VISIBLE);
			}
		}

	}

	// public abstract static class OnAddNewEventClick{
	// public abstract void onAddNewEventClick();
	// }

}
