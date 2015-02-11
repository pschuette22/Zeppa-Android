package com.minook.zeppa.adapter.eventlistadapter;

import java.util.List;

import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator;
import com.minook.zeppa.singleton.ZeppaEventSingleton;

public class FeedListAdapter extends AbstractEventListAdapter {

	public FeedListAdapter(AuthenticatedFragmentActivity activity) {
		super(activity);
		setEventMediators();
	}

	@Override
	protected List<AbstractZeppaEventMediator> getCurrentEventMediators() {
		return ZeppaEventSingleton.getInstance().getEventMediators();
	}

	
	@Override
	protected void setEventMediators() {
		eventMediators = ZeppaEventSingleton.getInstance().getEventMediators();
	}

	


}