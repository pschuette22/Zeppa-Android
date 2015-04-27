package com.minook.zeppa.adapter.eventlistadapter;

import java.util.List;

import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator;
import com.minook.zeppa.observer.ScrollStateListener;
import com.minook.zeppa.singleton.ZeppaEventSingleton;

public class FeedListAdapter extends AbstractEventListAdapter implements ScrollStateListener {

	private boolean isScrolling;
	private boolean datasetIsStale;
	
	public FeedListAdapter(AuthenticatedFragmentActivity activity) {
		super(activity);
		setEventMediators();
		isScrolling = false;
		datasetIsStale = false;
	}

	@Override
	protected List<AbstractZeppaEventMediator> getCurrentEventMediators() {
		return ZeppaEventSingleton.getInstance().getEventMediators();
	}

	
	@Override
	protected void setEventMediators() {
		eventMediators = ZeppaEventSingleton.getInstance().getEventMediators();
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
		if(datasetIsStale){
			notifyDataSetChanged();
		}
	}
	
	@Override
	public void notifyDataSetChanged() {
		if(isScrolling){
			datasetIsStale = true;
		} else {
			datasetIsStale = false;
			super.notifyDataSetChanged();
		}
	}


}