package com.minook.zeppa.adapters.eventadapter;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.minook.zeppa.R;
import com.minook.zeppa.activities.AuthenticatedFragmentActivity;

public class EventLayoutAdapter extends EventListAdapter {

	protected LinearLayout eventHolder;
	protected View loaderView;

	public EventLayoutAdapter(AuthenticatedFragmentActivity activity,
			LinearLayout eventHolder) {
		super(activity);
		this.eventHolder = eventHolder;

	}

	protected View getLoaderView() {
		loaderView = activity.getLayoutInflater().inflate(
				R.layout.view_loaderview, null, false);
		return loaderView;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		drawEvents();
	}

	public void drawEvents() {
		eventHolder.removeAllViews();
		if (events.isEmpty()) {
			Toast.makeText(activity, "Nothing to show", Toast.LENGTH_SHORT)
					.show();
		} else {

			for (int i = eventHolder.getChildCount(); i < events.size(); i++) {
				View view = getView(i, null, eventHolder);
				eventHolder.addView(view);

			}

		}
	}

}
