package com.pschuette.android.calendarlibrary;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pschuette.android.calendarlibrary.Day.OnEventsLoadListener;

public class DayAdapter extends BaseAdapter implements OnClickListener,
		OnEventsLoadListener {

	public interface EventItemClickListener {
		public void OnEventItemClicked(Event event);
	}

	private Context context;
	private Day day;
	private EventItemClickListener listener;
	private LinearLayout viewHolder;

	public DayAdapter(Context context, LinearLayout viewHolder, Day day,
			EventItemClickListener listener) {
		this.context = context;
		this.day = day;
		this.viewHolder = viewHolder;
		this.listener = listener;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return day.getEvents().size();
	}

	@Override
	public Event getItem(int position) {
		// TODO Auto-generated method stub
		return day.getEvents().get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getEventId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.event_item, parent, false);
		}

		Event event = getItem(position);
		int colorCode = event.getColor();

		LinearLayout background = (LinearLayout) convertView
				.findViewById(R.id.eventitem_background);
		background.setAlpha(1);

		if (colorCode == 0) {
			background.setBackgroundColor(Color.parseColor("#0AD2FF"));
		} else {
			// String hexColor = String.format("#%06X", (0xFFFFFF & colorCode));
			//
			// int redCode = Color.red(colorCode);
			// int greenCode = Color.green(colorCode);
			// int blueCode = Color.blue(colorCode);
			// int alphaCode = Color.alpha(colorCode);
			//
			// Paint paintBackground = new Paint();
			// paintBackground.setColor(colorCode);
			//
			// colorCode = Color.argb(255,Color.red(colorCode),
			// Color.green(colorCode), Color.blue(colorCode));
			
			background.setBackgroundColor(colorCode);

		}

		TextView title = (TextView) convertView
				.findViewById(R.id.eventitem_title);
		title.setText(event.getTitle());

		TextView content = (TextView) convertView
				.findViewById(R.id.eventitem_content);
		StringBuilder builder = new StringBuilder();
		builder.append(event.getPrettyEventTimeString());

		if (event.getLocation() != null && !event.getLocation().isEmpty()) {
			builder.append(" at ");
			builder.append(event.getLocation());
		}
		content.setText(builder.toString());

		convertView.setTag(event);
		convertView.setOnClickListener(this);
		return convertView;
	}

	@Override
	public void onClick(View v) {
		Event event = (Event) v.getTag();
		if (listener != null) {
			listener.OnEventItemClicked(event);
		}
	}

	@Override
	public void onEventLoadComplete() {
		drawEvents();

	}

	public void drawEvents() {
		viewHolder.removeAllViews();

		int eventCount = getCount();

		if (eventCount > 0) {
			for (int i = 0; i < eventCount; i++) {
				View eventView = getView(i, null, viewHolder);
				viewHolder.addView(eventView);
			}
		}
	}

}
