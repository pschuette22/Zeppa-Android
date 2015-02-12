package com.minook.zeppa.fragment;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.minook.zeppa.mediator.AbstractZeppaEventMediator;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.pschuette.android.calendarlibrary.DayAdapter.EventItemClickListener;
import com.pschuette.android.calendarlibrary.Event;
import com.pschuette.android.calendarlibrary.ExtendedCalendarView;

public class CalendarFragment extends Fragment implements
		EventItemClickListener {

	private static final String TAG = CalendarFragment.class.getName();
	private ExtendedCalendarView calendar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		calendar = new ExtendedCalendarView(getActivity(), this);
		return calendar;

	}

	@Override
	public void onResume() {
		calendar.drawCalendarAndDayDetails();

		super.onResume();
	}

	@Override
	public void OnEventItemClicked(Event event) {
		Log.d(TAG, "Clicked Event: " + event);

		AbstractZeppaEventMediator mediator = ZeppaEventSingleton.getInstance()
				.getMatchingEventMediator(event);
		if (mediator != null) {
			mediator.launchIntoEventView(getActivity());
		} else {
			Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI,
					event.getEventId());
			Intent calIntent = new Intent(Intent.ACTION_VIEW).setData(uri);
			calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
					event.getEndTime());
			calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
					event.getEndTime());
			calIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(calIntent);
		}

	}

}
