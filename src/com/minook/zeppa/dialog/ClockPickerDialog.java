package com.minook.zeppa.dialog;

import android.app.TimePickerDialog;
import android.content.Context;

public class ClockPickerDialog extends TimePickerDialog{

	public ClockPickerDialog(Context context, OnTimeSetListener callBack,
			int hourOfDay, int minute, boolean is24HourView) {
		super(context, callBack, hourOfDay, minute, is24HourView);
		
		
	}
	
	
	

}
