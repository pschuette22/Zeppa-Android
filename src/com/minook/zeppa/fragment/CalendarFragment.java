package com.minook.zeppa.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tyczj.extendedcalendarview.ExtendedCalendarView;

public class CalendarFragment extends Fragment {

	private ExtendedCalendarView calendar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);


		if (calendar == null) {
			calendar = new ExtendedCalendarView(getActivity().getBaseContext(),
					getActivity().getLayoutInflater());
		}
		

		return calendar;
	}

}
