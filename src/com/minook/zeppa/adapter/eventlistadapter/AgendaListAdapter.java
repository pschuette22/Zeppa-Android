package com.minook.zeppa.adapter.eventlistadapter;

import java.util.List;

import android.widget.ListView;

import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator;
import com.minook.zeppa.singleton.ZeppaEventSingleton;

public class AgendaListAdapter extends FeedListAdapter {

	public AgendaListAdapter(AuthenticatedFragmentActivity activity) {
		super(activity);
	}

	

	@Override
	protected void setEventMediators() {
		eventMediators = ZeppaEventSingleton.getInstance()
				.getInterestingEventMediators();
	}



	@Override
	protected List<AbstractZeppaEventMediator> getCurrentEventMediators() {
		return ZeppaEventSingleton.getInstance().getInterestingEventMediators();
	}


	
	

}
