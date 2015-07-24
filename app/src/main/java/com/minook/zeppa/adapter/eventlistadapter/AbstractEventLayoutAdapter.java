package com.minook.zeppa.adapter.eventlistadapter;

import android.view.View;
import android.widget.LinearLayout;

import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator;

import java.util.List;

public abstract class AbstractEventLayoutAdapter extends
		AbstractEventListAdapter {

	protected LinearLayout eventHolder;

	public AbstractEventLayoutAdapter(AuthenticatedFragmentActivity activity,
			LinearLayout eventHolder) {
		super(activity);
		this.eventHolder = eventHolder;

	}

	@Override
	protected abstract List<AbstractZeppaEventMediator> getCurrentEventMediators();

	@Override
	protected abstract void setEventMediators();

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		try {
			drawEvents();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void drawEvents() throws Exception {
		eventHolder.removeAllViews();
		if (eventMediators != null && !eventMediators.isEmpty()) {

			for (int i = eventHolder.getChildCount(); i < eventMediators.size(); i++) {
				View v = getView(i, null, eventHolder);
				v.setOnClickListener(this);
				eventHolder.addView(v);

			}
		}

	}


}
