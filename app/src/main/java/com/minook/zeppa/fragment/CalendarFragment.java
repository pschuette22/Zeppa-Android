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
import android.widget.FrameLayout;

import com.minook.zeppa.R;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.pschuette.android.calendarlibrary.DayAdapter.EventItemClickListener;
import com.pschuette.android.calendarlibrary.Event;
import com.pschuette.android.calendarlibrary.ExtendedCalendarView;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class CalendarFragment extends Fragment implements
		EventItemClickListener, OnRefreshListener {

	private static final String TAG = CalendarFragment.class.getName();

	private View view;
	private ExtendedCalendarView calendar;
	private PullToRefreshLayout pullToRefreshLayout;
	private FrameLayout frame;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		calendar = new ExtendedCalendarView(getActivity(), this);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		view = inflater.inflate(R.layout.fragment_calendar, container, false);

		frame = (FrameLayout) view.findViewById(R.id.calendarfragment_frame);

		pullToRefreshLayout = (PullToRefreshLayout) view
				.findViewById(R.id.calendarfragment_ptr);

		ActionBarPullToRefresh.from(getActivity())
				.options(Options.create().scrollDistance(.4f).build())
				.allChildrenArePullable().listener(this)
				.setup(pullToRefreshLayout);

		return view;

	}

	@Override
	public void onStart() {

		if (frame.getChildCount() == 0) {
			calendar.drawCalendarAndDayDetails();

			frame.addView(calendar);
		}
		
		

		super.onStart();
	}

	@Override
	public void onDestroyView() {

		frame.removeView(calendar);
		pullToRefreshLayout.removeView(frame);

		super.onDestroyView();
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

	@Override
	public void onRefreshStarted(View view) {
		calendar.onRefreshStarted();
		pullToRefreshLayout.setRefreshing(false);
	}

}
