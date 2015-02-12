package com.minook.zeppa.runnable;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.PrefsManager;
import com.minook.zeppa.ZeppaApplication;

public class SyncZeppaCalendarRunnable extends BaseRunnable {

	private static final String[] PROJECTION = { Calendars._ID, Calendars.NAME,
			Calendars.ACCOUNT_NAME, Calendars.SYNC_EVENTS, Calendars.VISIBLE };

	public SyncZeppaCalendarRunnable(ZeppaApplication application,
			GoogleAccountCredential credential) {
		super(application, credential);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {

		ContentResolver resolver = application.getContentResolver();

		for (int i = 0; i < 10; i++) {

			String loggedInAccount = PrefsManager.getLoggedInEmail(application);
			if (loggedInAccount == null) {
				return;
			}

			Cursor cursor = resolver.query(Calendars.CONTENT_URI, PROJECTION,
					"((" + Calendars.CALENDAR_DISPLAY_NAME + " = ?) AND ("
							+ Calendars.ACCOUNT_NAME + " = ?))", new String[] {
							"Zeppa", loggedInAccount }, null);

			if (cursor.moveToFirst()) {
				while (cursor.moveToNext()) {
					if (cursor.getInt(3) == 0) {
						Uri.Builder builder = Calendars.CONTENT_URI.buildUpon();
						ContentUris.appendId(builder, cursor.getInt(0));
						ContentValues updateValue = new ContentValues();
						updateValue.put(Calendars.SYNC_EVENTS, 1);
						resolver.update(builder.build(), updateValue, null,
								null);
					}

					if (cursor.getInt(4) == 0) {
						Uri.Builder builder = Calendars.CONTENT_URI.buildUpon();
						ContentUris.appendId(builder, cursor.getInt(0));
						ContentValues updateValue = new ContentValues();
						updateValue.put(Calendars.VISIBLE, 1);
						resolver.update(builder.build(), updateValue, null,
								null);
					}

				}
				return;
			} else {

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

}
