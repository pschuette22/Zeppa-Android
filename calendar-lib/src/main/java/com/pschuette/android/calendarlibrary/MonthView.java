package com.pschuette.android.calendarlibrary;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

public class MonthView extends LinearLayout implements OnClickListener {

	private CalendarAdapter adapter;
	private View selectedDayView;
	private OnDaySelectListener daySelectListener;

	public interface OnDaySelectListener {
		public void onDaySelected(Day day);
	}

	public MonthView(Context context, CalendarAdapter adapter) {
		super(context);
		this.adapter = adapter;
		init(context);

	}

	public MonthView(Context context, CalendarAdapter adapter,
			AttributeSet attrs) {
		super(context, attrs);
		this.adapter = adapter;
		init(context);
	}

	public MonthView(Context context, CalendarAdapter adapter,
			AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.adapter = adapter;
		init(context);
	}

	private void init(Context context) {
		setOrientation(LinearLayout.VERTICAL);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		setLayoutParams(params);

		drawCalendar();

	}

	public void setDaySelectListener(OnDaySelectListener daySelectListener) {
		this.daySelectListener = daySelectListener;
	}

	public void setAdapter(CalendarAdapter adapter) {
		this.adapter = adapter;
	}

	public Day getSelectedDay(){
		return (Day) selectedDayView.getTag();
	}
	
	public void drawCalendar() {


		// Redrawing calendar, make sure there is no selected day
		if (selectedDayView != null) {
			setDaySelected(selectedDayView, false);
			selectedDayView = null;
		}

		int selectedDayPosition = getCellStartoffset();
		Calendar instance = Calendar.getInstance();
		if (isSameMonth(instance, adapter.cal)) {
			selectedDayPosition += (instance.get(Calendar.DAY_OF_MONTH) - 1);
		}

		LinearLayout weekInMonth = null;

		removeAllViews();
		// Add the days of the week titles
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View dayTitlesView = inflater.inflate(R.layout.month_view_days, this, false);
		addView(dayTitlesView);
		

		for (int i = 0; i < adapter.getCount(); i++) {
			if ((i % 7) == 0) {
				weekInMonth = new LinearLayout(getContext());
				LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				weekInMonth.setLayoutParams(params);
				weekInMonth.setOrientation(LinearLayout.HORIZONTAL);
				
				addView(weekInMonth);
			}
			View dayView = adapter.getView(i, null,
					weekInMonth);
			weekInMonth.addView(dayView);
			
			if (dayView.getVisibility() == View.VISIBLE) {
				dayView.setOnClickListener(this);
				setDaySelected(dayView, (i == selectedDayPosition));
				
				
			}

		}

	}

	@Override
	public void onClick(View v) {
		Day day = (Day) v.getTag();

		if (day != null && !(day == (Day) selectedDayView.getTag())) {
			Log.d("TAG", "Selected day:" + day.getMonth() + "/" + day.getDay()
					+ "/" + day.getYear());

			setDaySelected(selectedDayView, false);
			setDaySelected(v, true);
			if (daySelectListener != null) {
				daySelectListener.onDaySelected(day);
			}
		}

		// if(selectedPosition != viewPosition){
		// View selectedDayView =
		// weekscontainer.findViewWithTag(Integer.valueOf(viewPosition));
		// View previousSelectedDayView =
		// weekscontainer.findViewWithTag(Integer.valueOf(selectedPosition));
		//

		// selectedPosition = viewPosition;
		// // TODO: update Day View
		//
		//
		// }

	}

	private void setDaySelected(View dayView, boolean selected) {
		TextView text = (TextView) dayView.findViewById(R.id.textView1);
		text.setSelected(selected);
		if(selected){
			selectedDayView = dayView;
		}
		
		
	}

	private boolean isSameMonth(Calendar cal1, Calendar cal2) {
		if (cal1 == null || cal2 == null) {
			return false;
		}

		return (cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH))
				&& (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR));
	}

	private int getCellStartoffset() {
		
		// Copy the calendar
		Calendar calendarCopy = Calendar.getInstance();
		calendarCopy.setTime(adapter.cal.getTime());
		
		// Set it to the first Day of the Month
		calendarCopy.set(Calendar.DAY_OF_MONTH, 1);

		return calendarCopy.get(Calendar.DAY_OF_WEEK) - 1;
	}


}
