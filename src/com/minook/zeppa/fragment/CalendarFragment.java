package com.minook.zeppa.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facade.calendar.CalendarController;
import com.facade.calendar.CalendarController.EventHandler;
import com.facade.calendar.CalendarController.EventInfo;
import com.facade.calendar.CalendarController.EventType;
import com.facade.calendar.DayFragment;
import com.facade.calendar.MonthByWeekFragment;
import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.activity.NewEventActivity;

public class CalendarFragment extends Fragment implements EventHandler {

	// Global Variables
	// Dynamic

	private View layout;
	private CalendarController mController;
	private Fragment monthFrag;
	private Fragment dayFrag;
	private boolean dayView;

	// Constants

	// handles the creating of the view
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
//		new ImportEntries().execute(getActivity());

		layout = inflater.inflate(R.layout.fragment_calendar, null, false);

		monthFrag = new MonthByWeekFragment(System.currentTimeMillis(), false);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.calfragment_calframe, monthFrag).commit();

		mController = CalendarController.getInstance(getActivity());

		mController.registerEventHandler(R.id.calfragment_calframe,
				(EventHandler) monthFrag);
		mController.registerFirstEventHandler(0, this);

		Log.d("TAG", "CalendarFragCreated");
		dayView = false;
		return layout;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public long getSupportedEventTypes() {
		return EventType.GO_TO | EventType.VIEW_EVENT | EventType.CREATE_EVENT
				| EventType.DELETE_EVENT;
	}

	@Override
	public void handleEvent(EventInfo eventInfo) {
		if (eventInfo.eventType == EventType.GO_TO) {
			dayView = true;
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			dayFrag = new DayFragment(eventInfo.startTime.toMillis(true), 1);
			ft.replace(R.id.calfragment_calframe, dayFrag).addToBackStack(null)
					.commit();
		} else if (eventInfo.eventType == EventType.VIEW_EVENT) {

//			EventData result = CalendarFacade.getEventById(getActivity().getBaseContext(),
//					eventInfo.id);
//
//			if (result == null) {
//				Toast.makeText(getActivity(), R.string.couldntfind,
//						Toast.LENGTH_SHORT).show();
//
//			} else {
//
//				
//
//				if (zeppaEvent != null) {
//					Intent launchEvent = new Intent(getActivity(),
//							EventViewActivity.class);
//					launchEvent.putExtra(Constants.INTENT_ZEPPA_EVENT_ID,
//							zeppaEvent.getKey().getId());
//					getActivity().startActivity(launchEvent);
//					getActivity().overridePendingTransition(
//							R.anim.slide_left_in, R.anim.slide_left_out);
//				} else if (result != null) {
//					Uri.Builder builder = CalendarContract.CONTENT_URI
//							.buildUpon();
//					builder.appendPath("events");
//					ContentUris.appendId(builder, result.eventId);
//					Intent intent = new Intent(Intent.ACTION_VIEW)
//							.setData(builder.build());
//					startActivity(intent);
//				}
//			}

		} else if (eventInfo.eventType == EventType.CREATE_EVENT) {

			long startMillis = eventInfo.startTime.toMillis(true);
			long endMillis = eventInfo.endTime.toMillis(true);

			Intent toNewEvent = new Intent(getActivity(),
					NewEventActivity.class);
			toNewEvent.putExtra(Constants.INTENT_EVENT_STARTTIME, startMillis);
			toNewEvent.putExtra(Constants.INTENT_EVENT_ENDTIME, endMillis);
			startActivity(toNewEvent);
			getActivity().overridePendingTransition(R.anim.slide_up_in,
					R.anim.hold);

		} else if (eventInfo.eventType == EventType.DELETE_EVENT) {
			// TODO: Delete the eventInfo and notify everyone. Also, get shares
			// and
			// delete those..? or just notify the owner and ask them to delete
		}

	}

	@Override
	public void eventsChanged() {

	}

	/*
	 * ------------------- Private Methods --------------------- NOTES: Did
	 * handle dayView is necessary public method for back press navigation
	 */

	public void updateCalendar() {
		mController.refreshCalendars();

	}

	private void setMonthView() {

		if (monthFrag == null)
			monthFrag = new MonthByWeekFragment(System.currentTimeMillis(),
					false);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.calfragment_calframe, monthFrag).commit();

		dayView = false;
	}

	public boolean didHandleDayView() {
		if (dayView) {
			setMonthView();
			return true;
		} else
			return false;
	}

	private void setPhoneEvents() {

	}

	/*
	 * ------------------- Private Class ----------------------- NOTES:
	 */

}
