package com.minook.zeppa.fragment;

import java.util.Calendar;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker.OnTimeChangedListener;

import com.minook.zeppa.R;

public class TimePickerFragment extends Fragment {

	private Context context;
	private OnTimeChangedListener listener;
	private Calendar calendar;
	
	public TimePickerFragment(Context context, OnTimeChangedListener listener, Calendar calendar){
		this.context = context;
		this.listener = listener;
		this.calendar = calendar;
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		View pickerview;
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			pickerview = inflater.inflate(R.layout.fragment_timepicker_clock, container);

	    } else {

	    }
		
		
		return null;
	}
	
	

}
