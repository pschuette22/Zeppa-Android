package com.minook.zeppa.adapter.eventlistadapter;

import android.widget.LinearLayout;

import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.observer.OnLoadListener;

public abstract class AbstractEventLayoutAdapter extends AbstractEventListAdapter implements OnLoadListener {

	protected LinearLayout eventHolder;

	public AbstractEventLayoutAdapter(AuthenticatedFragmentActivity activity,
			LinearLayout eventHolder) {
		super(activity);
		this.eventHolder = eventHolder;

	}

	@Override
	public abstract void verifyDatasetValid();

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		drawEvents();
	}

	public void drawEvents() {
		eventHolder.removeAllViews();
		if (didLoadInitial()) {
			if (!eventMediators.isEmpty()) {
				
				for (int i = eventHolder.getChildCount(); i < eventMediators.size(); i++) {
					eventHolder.addView(getView(i, null, eventHolder));

				}
			}
		} else {
			if(loaderView == null){
				loaderView = getLoaderView();
			}
			eventHolder.addView(loaderView);
		}
	}

}
