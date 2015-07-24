package com.pschuette.android.calendarlibrary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.List;

public class CalendarSyncStateAdapter extends BaseAdapter implements
		OnCheckedChangeListener {

	public interface OnSyncStateChangedListener {
		public void onSyncStateChanged(CalendarData data, Switch switchView);
	}

	private OnSyncStateChangedListener syncStateChangeListener;
	private Context context;
	private List<CalendarData> data;

	public CalendarSyncStateAdapter(Context context, List<CalendarData> data) {
		this.context = context;
		this.data = data;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public CalendarData getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return getItem(position).getLocalId().longValue();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.calendarsyncstate_view,
					parent, false);
		}
		CalendarData calData = getItem(position);

		ImageView calColorImage = (ImageView) convertView
				.findViewById(R.id.calendarsyncstate_color);
		calColorImage.setBackgroundColor(calData.getColor());

		Switch calSyncSwitch = (Switch) convertView
				.findViewById(R.id.calendarsyncstate_switch);
		calSyncSwitch.setText(calData.getDisplayName());
		calSyncSwitch.setTag(calData);
		calSyncSwitch.setChecked(calData.isSynced());
		calSyncSwitch.setOnCheckedChangeListener(this);

		return convertView;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		try {
			CalendarData data = (CalendarData) buttonView.getTag();
			data.changeSyncState(context, isChecked);
			if (syncStateChangeListener != null) {
				syncStateChangeListener.onSyncStateChanged(data,
						(Switch) buttonView);
			}

		} catch (NullPointerException e) {
			e.printStackTrace();
			Toast.makeText(context, "Error Syncing Calendar", Toast.LENGTH_SHORT).show();
		}
	}

	public void registerSyncStateChangeListener(
			OnSyncStateChangedListener syncStateChangeListener) {
		this.syncStateChangeListener = syncStateChangeListener;
	}

	public void unregisterSyncStateChangeListener(
			OnSyncStateChangedListener syncStateChangeListener) {
		if (this.syncStateChangeListener == syncStateChangeListener) {
			this.syncStateChangeListener = null;
		}
	}
}
