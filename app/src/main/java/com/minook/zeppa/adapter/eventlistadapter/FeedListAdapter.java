package com.minook.zeppa.adapter.eventlistadapter;

import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator;
import com.minook.zeppa.observer.ScrollStateListener;
import com.minook.zeppa.singleton.ZeppaEventSingleton;

import java.util.List;

public class FeedListAdapter extends AbstractEventListAdapter implements ScrollStateListener {

	private boolean isScrolling;
	private boolean updateOnScrollStop;
	private long lastUpdateTime;


	public FeedListAdapter(AuthenticatedFragmentActivity activity) {
		super(activity);
		setEventMediators();
		isScrolling = false;
		this.updateOnScrollStop = false;
	}

	@Override
	protected List<AbstractZeppaEventMediator> getCurrentEventMediators() {
		return ZeppaEventSingleton.getInstance().getEventMediators();
	}

	
	@Override
	protected void setEventMediators() {
		eventMediators = getCurrentEventMediators();
	}

	@Override
	public void onScrollStart() {
		// TODO Auto-generated method stub
		isScrolling = true;
	}

	@Override
	public void onScrollStop() {
		// TODO Auto-generated method stub
		isScrolling = false;
		if(updateOnScrollStop){
			notifyDataSetChanged();
		}
	}
	
	@Override
	public void notifyDataSetChanged() {
		if(isScrolling){
			this.updateOnScrollStop = true;
		} else {
			this.updateOnScrollStop = false;

			// Make sure the info is stale before updating
			if(ZeppaEventSingleton.getInstance().isInfoStale(lastDataUpdateTime)) {
				super.notifyDataSetChanged();
			}
		}
	}


}