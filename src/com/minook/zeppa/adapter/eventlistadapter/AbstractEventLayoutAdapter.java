package com.minook.zeppa.adapter.eventlistadapter;

import java.util.List;

import android.view.View;
import android.widget.LinearLayout;

import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator;
import com.minook.zeppa.observer.OnLoadListener;
import com.minook.zeppa.singleton.ZeppaEventSingleton;

public abstract class AbstractEventLayoutAdapter extends
		AbstractEventListAdapter implements OnLoadListener {

	protected LinearLayout eventHolder;

	public AbstractEventLayoutAdapter(AuthenticatedFragmentActivity activity,
			LinearLayout eventHolder) {
		super(activity);
		this.eventHolder = eventHolder;

	}

	@Override
	public boolean didLoadInitial() {
		return ZeppaEventSingleton.getInstance().hasLoadedInitial();
	}

	@Override
	public void onFinishLoad() {
		verifyDatasetValid();
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
	
	

	@Override
	protected void removeLoaderViewIfVisible() {

		if(loaderView != null && loaderView.getVisibility() == View.VISIBLE){
			eventHolder.removeView(loaderView);
			loaderView = null;
		}
	}

	public void drawEvents() throws Exception {
		eventHolder.removeAllViews();
		if (didLoadInitial()) {
			if (!eventMediators.isEmpty()) {

				for (int i = eventHolder.getChildCount(); i < eventMediators
						.size(); i++) {
					eventHolder.addView(getView(i, null, eventHolder));

				}
			}
		} else {
			if (loaderView == null) {
				loaderView = getLoaderView();
			}
			eventHolder.addView(loaderView);
		}
	}
	

}
