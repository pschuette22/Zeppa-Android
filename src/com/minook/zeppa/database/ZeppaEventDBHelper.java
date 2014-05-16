package com.minook.zeppa.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.minook.zeppa.Constants;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;



public class ZeppaEventDBHelper extends SQLiteOpenHelper {


	// Events DB Variables
	private static final String IDENTIFIERS_TABLE = "Event_Identifiers_Table";

	private static final String ENTRY_ID = BaseColumns._ID;
	private static final String ZEPPA_ID = "Zeppa_Event_Id";
	private static final String EVENT_ID = "Event_Id";
	

	public ZeppaEventDBHelper(Context context) {
		super(context, Constants.DB_NAME, null, Constants.DB_VERSION);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createIdentifiersTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	private void createIdentifiersTable(SQLiteDatabase db) {
		String sqlPD = "CREATE TABLE " + IDENTIFIERS_TABLE + " (" + ENTRY_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + ZEPPA_ID + " INTEGER, "+ EVENT_ID
				+ " INTEGER" + ");";
		db.execSQL(sqlPD);
	}

	public void linkEventIds(ZeppaEvent event, Long eventId) {
		
		Long identifier = event.getKey().getId();
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(ZEPPA_ID, identifier);
		values.put(EVENT_ID, eventId);
		
		db.insert(IDENTIFIERS_TABLE, null, values);
		
		db.close();

	}

	public void removeEventIds(ZeppaEvent event) {
		Long identifier = event.getKey().getId();
		SQLiteDatabase db = getWritableDatabase();
		String params = ZEPPA_ID + " = ";
		String[] args = { String.valueOf(identifier) };
		db.delete(IDENTIFIERS_TABLE, params, args);
	}

	public long getMatchingId(ZeppaEvent event) {
		Long l = Long.valueOf(-1);
		SQLiteDatabase db = getReadableDatabase();

		Long identifier = event.getKey().getId();
		String args[] = { String.valueOf(identifier) };
		String params = ZEPPA_ID + " = ?";

		Cursor cursor = db.query(IDENTIFIERS_TABLE, null, params, args, null,
				null, null);

		if (cursor.getCount() > 0) {
			if (cursor.getCount() > 1) {
				Log.d(getClass().getName(), "Houston, We have a problem...");
			}
			cursor.moveToFirst();

			l = cursor.getLong(cursor.getColumnIndex(EVENT_ID));
		}

		return l;
	}
	
	
	public ZeppaEvent getMatchingEvent(long googleCalendarId){
		ZeppaEvent event = null;
		SQLiteDatabase db = getReadableDatabase();

		String args[] = { String.valueOf(googleCalendarId) };
		String params = EVENT_ID + " = ?";

		Cursor cursor = db.query(IDENTIFIERS_TABLE, null, params, args, null,
				null, null);

		if (cursor.getCount() > 0) {
			if (cursor.getCount() > 1) {
				Log.d(getClass().getName(), "Houston, We have a problem...");
			}
			cursor.moveToFirst();

			long l = cursor.getLong(cursor.getColumnIndex(EVENT_ID));
			event = ZeppaEventSingleton.getInstance().getEventById(l);
		}

		
		return event;
	}
	
	

}
