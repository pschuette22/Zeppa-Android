package com.minook.zeppa.adapter;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.facade.calendar.CalendarData;
import com.facade.calendar.CalendarFacade;
import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;

public class CalendarSelectionAdapter extends BaseAdapter implements OnItemClickListener{
	
	private AuthenticatedFragmentActivity context;
	private List<CalendarData> calDataList;
	
	public CalendarSelectionAdapter(AuthenticatedFragmentActivity context) {
		this.context = context;
		calDataList = CalendarFacade.getAllCalendars(context);
	}

	@Override
	public int getCount() {
		return calDataList.size();
	}

	@Override
	public CalendarData getItem(int position) {
		return calDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = context.getLayoutInflater().inflate(
					R.layout.view_calendar_sync, parent, false);
		}
		CalendarData data = getItem(position);

		((TextView) convertView.findViewById(R.id.calendarsync_text))
				.setText(data.getDisplayName() + " ("
						+ data.getOwnerAccountName() + ")");

		ImageView colorBox = (ImageView) convertView
				.findViewById(R.id.calendarsync_colorbox);
		colorBox.setBackgroundColor(data.getColorCode());
		
		Switch calendarSyncSwitch = (Switch) convertView.findViewById(R.id.calendarsync_switch);
		calendarSyncSwitch.setSelected(data.isSyncedToApplication());

		return convertView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		CalendarData data = getItem(position);
		
		
	}

}
