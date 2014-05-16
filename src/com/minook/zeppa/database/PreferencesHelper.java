package com.minook.zeppa.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

public class PreferencesHelper {

	private static PreferencesHelper helper;

	private PreferencesHelper(Context context) {

		SharedPreferences prefs = getPrefs(context);

		float PREFS_VERSION = prefs.getInt(Constants.PREFS_VERSION, 0);

		boolean createPrefs = (PREFS_VERSION < 1);
		if (createPrefs)
			createSettingsVersion1(context);

	}

	public static PreferencesHelper getInstance(Context context) {
		if (helper == null)
			helper = new PreferencesHelper(context);

		return helper;
	}

	
	// Organize prefs by user so if other people log on it will still hold prefs
	public String getKey(String constantKey) {
		String accountName = ZeppaUserSingleton.getInstance().getUser()
				.getEmail();
		return (accountName + "-" + constantKey);
	}

	public static boolean didInitializeFor(Context context) {

		return (helper != null);
	}

	
	public SharedPreferences getPrefs(Context context) {
		return context.getSharedPreferences(getKey(Constants.SHARED_PREFS),
				Context.MODE_PRIVATE);
	}
	
	public static Long getHeldUserIdForAccountWithoutInitializing(Context context, String selectedAccount){
		
		SharedPreferences prefs = context.getSharedPreferences((selectedAccount + "-" + Constants.SHARED_PREFS),
				Context.MODE_PRIVATE);
		
		long id = -1;
		String heldEmail = prefs.getString(Constants.EMAIL_ADDRESS, "");
		
		if(heldEmail.equalsIgnoreCase(selectedAccount))
			id = prefs.getLong(Constants.USER_ID, -1);
			
			
		return Long.valueOf(id);
	}
	
	private boolean createSettingsVersion1(Context context) {
		SharedPreferences.Editor editor = getPrefs(context).edit();

		// Set the Prefs version: constant 1 for this method
		editor.putInt(Constants.PREFS_VERSION, 1);

		// Set User Id
		editor.putLong(Constants.USER_ID,
				ZeppaUserSingleton.getInstance().getUserId()).apply();

		// Set Email address of current user
		editor.putString(Constants.EMAIL_ADDRESS,
				ZeppaUserSingleton.getInstance().getUser().getEmail()).apply();

		int calId = createCalendar(context);
		editor.putInt(Constants.ZEPPA_INTERNAL_CALENDAR_ID, calId).apply();

		editor.putBoolean(Constants.SHOW_TUTORIAL, true).apply();
		editor.putBoolean(Constants.IS_12HR_FORMAT, true).apply();

		editor.putBoolean(Constants.PUSH_NOTIFICAIONS, true).apply();
		editor.putBoolean(Constants.PN_EVENT_RECCOMENDATION, true).apply();
		editor.putBoolean(Constants.PN_FRIEND_REQUEST, true).apply();
		editor.putBoolean(Constants.PN_FRIEND_ACCEPT, true).apply();
		editor.putBoolean(Constants.PN_FRIEND_JOINS, true).apply();
		editor.putBoolean(Constants.PN_EVENT_JOINED, true).apply();

		editor.putBoolean(Constants.PN_EVENT_LEFT, true).apply();
		editor.putBoolean(Constants.PN_SOUND_ON, false).apply();
		editor.putBoolean(Constants.PN_VIBRARTE_ON, true).apply();

		return editor.commit();
	}

	private int getValidCalendarId(Context context) {
		int calId = -1;

		ContentResolver resolver = context.getContentResolver();
		Uri uri = Calendars.CONTENT_URI;

		Cursor calendarCursor = resolver.query(uri,
				new String[] { Calendars._ID }, null, null, Calendars._ID
						+ " ASC");

		if (calendarCursor.getCount() == 0) {
			calId = 1;
		} else {
			calendarCursor.moveToLast();
			calId = (calendarCursor.getInt(0) + 1); // last Id + 1
		}

		calendarCursor.close();
		return calId;
	}

	private int createCalendar(Context context) {

		SharedPreferences prefs = getPrefs(context);

		int calendarId = prefs.getInt(Constants.ZEPPA_INTERNAL_CALENDAR_ID, -1);

		if (calendarId >= 0) {
			// needs to be created
			return calendarId; // Should never happen
		}

		calendarId = getValidCalendarId(context);

		Uri calUri = CalendarContract.Calendars.CONTENT_URI;
		ContentValues cv = new ContentValues();

		cv.put(CalendarContract.Calendars.ACCOUNT_NAME,
				prefs.getString(Constants.EMAIL_ADDRESS, null));

		cv.put(CalendarContract.Calendars.ACCOUNT_TYPE,
				CalendarContract.ACCOUNT_TYPE_LOCAL);

		cv.put(CalendarContract.Calendars.NAME, Constants.CAL_NAME_INTERNAL);
		cv.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
				Constants.CAL_NAME_DISPLAY);
		cv.put(CalendarContract.Calendars.CALENDAR_COLOR, context.getResources()
				.getColor(R.color.teal));
		cv.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
				CalendarContract.Calendars.CAL_ACCESS_OWNER);
		cv.put(CalendarContract.Calendars.OWNER_ACCOUNT, true);
		cv.put(CalendarContract.Calendars.VISIBLE, 1);
		cv.put(CalendarContract.Calendars.SYNC_EVENTS, 1);

		calUri = calUri
				.buildUpon()
				.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER,
						"true")
				.appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME,
						prefs.getString(Constants.EMAIL_ADDRESS, null))
				.appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE,
						CalendarContract.ACCOUNT_TYPE_LOCAL).build();
		context.getContentResolver().insert(calUri, cv);

		
		return calendarId;
	}

}
