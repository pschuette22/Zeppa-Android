package com.minook.zeppa.adapter.eventlistadapter;

import java.util.List;

import android.view.View;
import android.widget.LinearLayout;

import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator;
import com.minook.zeppa.singleton.ZeppaEventSingleton;

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



}
