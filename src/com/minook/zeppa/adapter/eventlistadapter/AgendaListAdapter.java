package com.minook.zeppa.adapter.eventlistadapter;

import java.util.List;

import android.widget.ListView;

import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator;
import com.minook.zeppa.singleton.ZeppaEventSingleton;

public class AgendaListAdapter extends FeedListAdapter {
	
	public AgendaListAdapter(AuthenticatedFragmentActivity activity,
			ListView list) {
		super(activity, list);
	}

	@Override
	public void verifyDatasetValid() {

		List<AbstractZeppaEventMediator> currentMediators = ZeppaEventSingleton.getInstance().getInterestingEventMediators();

		if(!currentMediators.containsAll(eventMediators) || !eventMediators.containsAll(currentMediators)){
			notifyDataSetChanged();
		}
		
	}

	@Override
	protected void setEventMediators() {
		eventMediators = ZeppaEventSingleton.getInstance().getInterestingEventMediators();
	}


	

}
