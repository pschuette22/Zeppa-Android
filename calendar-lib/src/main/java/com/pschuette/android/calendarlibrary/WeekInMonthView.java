package com.pschuette.android.calendarlibrary;

import android.content.Context;
import android.view.View;

public class WeekInMonthView extends View {

	public WeekInMonthView(Context context) {
		super(context);
		init();
	}

	private void init() {
		inflate(getContext(), R.layout.month_week_view, null);
	}

	public View getChildAt(int position) {
		switch (position) {
		case 0:
			return findViewById(R.id.dayofweek0);
		case 1:
			return findViewById(R.id.dayofweek1);
		case 2:
			return findViewById(R.id.dayofweek2);
		case 3:
			return findViewById(R.id.dayofweek3);
		case 4:
			return findViewById(R.id.dayofweek4);
		case 5:
			return findViewById(R.id.dayofweek5);
		case 6:
			return findViewById(R.id.dayofweek6);
		default:
			return null;
		}
	}

}
