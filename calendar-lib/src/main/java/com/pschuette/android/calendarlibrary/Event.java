package com.pschuette.android.calendarlibrary;

import android.graphics.Bitmap;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Event {
	
	private int color;
	private String name;
	private String description;
	private String location;
	private long start;
	private long end;
	private Bitmap image;
	private long eventId;
	private String calendarEventId;
	
	
	public static final int DEFAULT_EVENT_ICON = 0;
	public static final int COLOR_RED = 1;
	public static final int COLOR_BLUE = 2;
	public static final int COLOR_YELLOW = 3;
	public static final int COLOR_PURPLE = 4;
	public static final int COLOR_GREEN = 5;
	
	public Event(long eventID, long startMills, long endMills){
		this.eventId = eventID;
		this.start = startMills;
		this.end = endMills;
	}
	
	
	public Event(long eventId, String title, Calendar startTime, Calendar endTime, int color, String location) {
		this(eventId, startTime.getTimeInMillis(), endTime.getTimeInMillis());
		this.name = title;
		this.color = color;
		this.location = location;
	}


	public int getColor(){
		return color;
	}
	
	public void setColor(int color){
		this.color = color;
	}
	
	/**
	 * Get the event title
	 * 
	 * @return title
	 */
	public String getTitle(){
		return name;
	}
	
	/**
	 * Get the event description
	 * 
	 * @return description
	 */
	public String getDescription(){
		return description;
	}
	
	public String getPrettyEventTimeString(){
		StringBuilder builder = new StringBuilder();
		
		Calendar start = getStartCalendar();
		Calendar end = getEndCalendar();
		
		builder.append(getPrettyTimeString(start));
		builder.append(" - ");
		builder.append(getPrettyTimeString(end));
		
		
		
		return builder.toString();
	}
	
	private String getPrettyTimeString(Calendar calendar){
		StringBuilder builder = new StringBuilder();
		
		int hour = calendar.get(Calendar.HOUR);
		if(hour == 0) {
			hour = 12;
		}
		
		builder.append(hour);
		builder.append(":");
		
		int minute = calendar.get(Calendar.MINUTE);
		if(minute < 10){
			builder.append("0" + minute);
		} else {
			builder.append(minute);
		}
		
		if(calendar.get(Calendar.AM_PM) == Calendar.AM){
			builder.append(" AM");
		} else {
			builder.append(" PM");
		}
		
		
		return builder.toString();
	}
	
	public Bitmap getImage(){
		return image;
	}
	
	public void setDescription(String description){
		this.description = description;
	}
	
	public void setLocation(String location){
		this.location = location;
	}
	
	public String getLocation(){
		return location;
	}
	
	public long getStartTime(){
		return start;
	}
	
	public Calendar getStartCalendar(){
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTimeInMillis(start);
		return startCalendar;
	}
	
	public Calendar getEndCalendar(){
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTimeInMillis(end);
		return endCalendar;
	}
	
	public long getEndTime(){
		return end;
	}
	
	/**
	 * Set the name of the event
	 * 
	 * @param name
	 */
	public void setName(String name){
		this.name = name;
	}
	
	/**
	 * Gets the event id in the database
	 * 
	 * @return event database id
	 */
	public long getEventId(){
		return eventId;
	}
	
	/**
	 * Get the start date of the event
	 * 
	 * @return start date
	 */
	public String getStartDate(String dateFormat){
		DateFormat df = new SimpleDateFormat(dateFormat,Locale.getDefault());
		String date = df.format(start);
		
		return date;
	}
	
	/**
	 * Get the end date of the event
	 * 
	 * @return end date
	 */
	public String getEndDate(String dateFormat){
		DateFormat df = new SimpleDateFormat(dateFormat,Locale.getDefault());
		String date = df.format(end);
		
		return date;
	}

}
