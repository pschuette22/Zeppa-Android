package com.pschuette.android.calendarlibrary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pschuette.android.calendarlibrary.DayAdapter.EventItemClickListener;
import com.pschuette.android.calendarlibrary.MonthView.OnDaySelectListener;

import java.util.Calendar;
import java.util.Locale;

public class ExtendedCalendarView extends RelativeLayout implements
		OnClickListener, OnDaySelectListener {

	private Context context;
	private LinearLayout dayViewHolder;
	private FrameLayout monthViewHolder;
	private MonthView monthView;
	private CalendarAdapter mAdapter;
	private Calendar cal;
	private TextView month;
	private RelativeLayout base;
	private ImageView next, prev;
	private EventItemClickListener eventClickListener;
	
	
	public ExtendedCalendarView(Context context,
			EventItemClickListener eventClickListener) {
		super(context);
		this.context = context;
		this.eventClickListener = eventClickListener;
		init();
	}

	public ExtendedCalendarView(Context context, AttributeSet attrs,
			EventItemClickListener eventClickListener) {
		super(context, attrs);
		this.context = context;
		this.eventClickListener = eventClickListener;

		init();
	}

	public ExtendedCalendarView(Context context, AttributeSet attrs,
			int defStyle, EventItemClickListener eventClickListener) {
		super(context, attrs, defStyle);
		this.context = context;
		this.eventClickListener = eventClickListener;

		init();
	}

	private void init() {

		cal = Calendar.getInstance();

		inflate(context, R.layout.base_view, this);

		prev = (ImageView) findViewById(R.id.baseview_previous);
		prev.setOnClickListener(this);

		next = (ImageView) findViewById(R.id.baseview_next);
		next.setOnClickListener(this);

		monthViewHolder = (FrameLayout) findViewById(R.id.baseview_monthviewholder);
		dayViewHolder = (LinearLayout) findViewById(R.id.baseview_dayviewholder);

		mAdapter = new CalendarAdapter(context, cal);
		drawCalendarAndDayDetails();
	}

	@Override
	public void onClick(View v) {

		if (v.equals(prev)) {
			previousMonth();
		} else if (v.equals(next)) {
			nextMonth();
		}

	}

	@Override
	public void onDaySelected(Day day) {
		drawEvents(day);
	}

	private void previousMonth() {
		if (cal.get(Calendar.MONTH) == Calendar.JANUARY) {
			cal.set((cal.get(Calendar.YEAR) - 1), Calendar.DECEMBER, 1);
		} else {
			cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
			cal.set(Calendar.DAY_OF_MONTH, 1);
		}
		mAdapter = new CalendarAdapter(context, cal);
		drawCalendarAndDayDetails();

	}

	private void nextMonth() {
		if (cal.get(Calendar.MONTH) == Calendar.DECEMBER) {
			cal.set((cal.get(Calendar.YEAR) + 1), Calendar.JANUARY, 1);
		} else {
			cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
			cal.set(Calendar.DAY_OF_MONTH, 1);
		}
		
		mAdapter = new CalendarAdapter(context, cal);
		drawCalendarAndDayDetails();

	}

	public void drawCalendarAndDayDetails() {
		month = (TextView) findViewById(R.id.baseview_title);
		month.setText(cal.getDisplayName(Calendar.MONTH, Calendar.LONG,
				Locale.getDefault())
				+ " " + cal.get(Calendar.YEAR));

		monthViewHolder.removeAllViews();

		monthView = new MonthView(context, mAdapter);
		monthView.setDaySelectListener(this);

		monthViewHolder.addView(monthView);

		drawEvents(monthView.getSelectedDay());

	}

	private void drawEvents(Day day) {
		dayViewHolder.removeAllViews();

		DayAdapter adapter = new DayAdapter(getContext(), dayViewHolder, day,
				eventClickListener);
		day.registerOnEventsLoadListener(adapter);
		adapter.drawEvents();
	}

	/**
	 * 
	 * @param color
	 * 
	 *            Sets the background color of the month bar
	 */
	public void setMonthTextBackgroundColor(int color) {
		base.setBackgroundColor(color);
	}

	@SuppressLint("NewApi")
	/**
	 * 
	 * @param drawable
	 * 
	 * Sets the background color of the month bar. Requires at least API level 16
	 */
	public void setMonthTextBackgroundDrawable(Drawable drawable) {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
			base.setBackground(drawable);
		}

	}

	/**
	 * 
	 * @param resource
	 * 
	 *            Sets the background color of the month bar
	 */
	public void setMonthTextBackgroundResource(int resource) {
		base.setBackgroundResource(resource);
	}

	/**
	 * 
	 * @param recource
	 * 
	 *            change the image of the previous month button
	 */
	public void setPreviousMonthButtonImageResource(int recource) {
		prev.setImageResource(recource);
	}

	/**
	 * 
	 * @param bitmap
	 * 
	 *            change the image of the previous month button
	 */
	public void setPreviousMonthButtonImageBitmap(Bitmap bitmap) {
		prev.setImageBitmap(bitmap);
	}

	/**
	 * 
	 * @param drawable
	 * 
	 *            change the image of the previous month button
	 */
	public void setPreviousMonthButtonImageDrawable(Drawable drawable) {
		prev.setImageDrawable(drawable);
	}

	/**
	 * 
	 * @param recource
	 * 
	 *            change the image of the next month button
	 */
	public void setNextMonthButtonImageResource(int recource) {
		next.setImageResource(recource);
	}

	/**
	 * 
	 * @param bitmap
	 * 
	 *            change the image of the next month button
	 */
	public void setNextMonthButtonImageBitmap(Bitmap bitmap) {
		next.setImageBitmap(bitmap);
	}

	/**
	 * 
	 * @param drawable
	 * 
	 *            change the image of the next month button
	 */
	public void setNextMonthButtonImageDrawable(Drawable drawable) {
		next.setImageDrawable(drawable);
	
	}
	
	
	/**
	 * Call this to start a calendar refresh. This is a blocking method
	 */
	public void onRefreshStarted(){

		monthView.drawCalendar();		
		
	}

}
