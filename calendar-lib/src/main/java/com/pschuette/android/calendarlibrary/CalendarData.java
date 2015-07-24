package com.pschuette.android.calendarlibrary;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.util.Log;

public class CalendarData {

	private static final String  TAG = CalendarData.class.getName();
	
	public static final String[] SYNC_DATA_PROJECTION = {
		CalendarContract.Calendars._ID,
		CalendarContract.Calendars.NAME,
		CalendarContract.Calendars.ACCOUNT_NAME,
		CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
		CalendarContract.Calendars.CALENDAR_COLOR,
		CalendarContract.Calendars.SYNC_EVENTS
	};
	
	private Long localId;
	private String name;
	private String accountName;
	private String displayName;
	private int color;
	private boolean isSynced;
	
	
	public CalendarData(Cursor cursor){
		localId = cursor.getLong(0);
		name = cursor.getString(1);
		accountName = cursor.getString(2);
		displayName = cursor.getString(3);
		color = cursor.getInt(4);
		isSynced = (cursor.getInt(5) == 1);
	}
	

	public Long getLocalId() {
		return localId;
	}


	public void setLocalId(Long localId) {
		this.localId = localId;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getAccountName() {
		return accountName;
	}


	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}


	public String getDisplayName() {
		return displayName;
	}


	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}


	public int getColor() {
		return color;
	}


	public void setColor(int color) {
		this.color = color;
	}


	public boolean isSynced() {
		return isSynced;
	}


	public void setSynced(boolean isSynced) {
		this.isSynced = isSynced;
	}
	
	public void changeSyncState(Context context, boolean isSynced){
		ContentResolver cr = context.getContentResolver();
		Uri.Builder builder = Calendars.CONTENT_URI.buildUpon();
		ContentUris.appendId(builder, localId);
		
		ContentValues updateValue = new ContentValues();
		updateValue.put(Calendars.SYNC_EVENTS, isSynced?1:0);
		
		
		int rowsupdated = cr.update(builder.build(), updateValue, null, null);
		
		if(rowsupdated == 0){
			Log.d(TAG, "Nothing Updated");
		} else if(rowsupdated == 1){
			Log.d(TAG, "Successfully Updated");
			setSynced(isSynced);
		} else if (rowsupdated > 1){
			Log.d(TAG, "Too many rows updated");

		} else {
			Log.d(TAG, "Negative rows updated..?");
		}
		
	}
	
	
	
}
