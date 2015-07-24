package com.minook.zeppa.adapter.eventlistadapter;

import android.widget.LinearLayout;

import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator;
import com.minook.zeppa.singleton.ZeppaEventSingleton;

import java.util.List;

public class MyEventsAdapter extends AbstractEventLayoutAdapter {

	
	public MyEventsAdapter(AuthenticatedFragmentActivity activity,
			LinearLayout eventHolder) {
		super(activity, eventHolder);

	}


	@Override
	protected void setEventMediators() {
		eventMediators = ZeppaEventSingleton.getInstance().getHostedEventMediators();
		
	}


	@Override
	protected List<AbstractZeppaEventMediator> getCurrentEventMediators() {
		// TODO Auto-generated method stub
		return ZeppaEventSingleton.getInstance().getHostedEventMediators();
	}

	public boolean isUpToDate(){
		List<AbstractZeppaEventMediator> current = getCurrentEventMediators();
		
		if(eventMediators == null){
			return false;
		}
		
		return (current.containsAll(eventMediators) && eventMediators.containsAll(current));
	}


}
