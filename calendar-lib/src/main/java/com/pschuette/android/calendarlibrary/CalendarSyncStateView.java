package com.pschuette.android.calendarlibrary;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pschuette.android.calendarlibrary.CalendarSyncStateAdapter.OnSyncStateChangedListener;

import java.util.ArrayList;
import java.util.List;

public class CalendarSyncStateView extends LinearLayout {

	private boolean isLoadingCalendars;
	private List<CalendarData> data;
	private CalendarSyncStateAdapter syncStateAdapter;
	private OnSyncStateChangedListener syncChangeListener;

	public CalendarSyncStateView(Context context,
			OnSyncStateChangedListener syncStateChangeListener) {
		super(context);
		this.syncChangeListener = syncStateChangeListener;
		init();
	}

	private void init() {
		setOrientation(LinearLayout.VERTICAL);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		setLayoutParams(params);
		isLoadingCalendars = true;
		new FetchCalendarsWithSyncState(getContext()).execute();
		drawCalendarsWithSyncState();
	}

	private void drawCalendarsWithSyncState() {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		removeAllViews();

		if (isLoadingCalendars) {
			// TODO: inflate loader view
			View loaderView = inflater.inflate(R.layout.view_loaderview, this,
					false);
			TextView loaderText = (TextView) loaderView
					.findViewById(R.id.loaderview_text);
			loaderText.setText("Loading Calendar Data");
			ProgressBar progress = (ProgressBar) loaderView
					.findViewById(R.id.loaderview_progressbar);
			progress.setIndeterminate(true);
		} else if (data != null) {
			syncStateAdapter = new CalendarSyncStateAdapter(getContext(), data);
			syncStateAdapter
					.registerSyncStateChangeListener(syncChangeListener);
			if (syncStateAdapter.getCount() > 0) {
				for (int i = 0; i < syncStateAdapter.getCount(); i++) {
					View syncView = syncStateAdapter.getView(i, null, this);
					addView(syncView);

				}
			}
		}
	}

	private class FetchCalendarsWithSyncState extends
			AsyncTask<Void, Void, List<CalendarData>> {

		private Context context;

		public FetchCalendarsWithSyncState(Context context) {
			this.context = context;
		}

		@Override
		protected List<CalendarData> doInBackground(Void... params) {
			ContentResolver cr = context.getContentResolver();
			Cursor cursor = cr.query(CalendarContract.Calendars.CONTENT_URI,
					CalendarData.SYNC_DATA_PROJECTION, null, null,
					CalendarContract.Calendars.ACCOUNT_NAME + " desc");
			List<CalendarData> calendarDataList = new ArrayList<CalendarData>();
			try {

				if (cursor.moveToFirst()) {
					do {
						CalendarData data = new CalendarData(cursor);
						calendarDataList.add(data);
					} while (cursor.moveToNext());
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				cursor.close();
			}

			return calendarDataList;
		}

		@Override
		protected void onPostExecute(List<CalendarData> result) {
			super.onPostExecute(result);
			isLoadingCalendars = false;
			if (result != null) {
				data = result;
				drawCalendarsWithSyncState();
			}

		}

	}

}
