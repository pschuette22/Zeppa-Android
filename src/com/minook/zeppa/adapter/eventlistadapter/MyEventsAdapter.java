package com.minook.zeppa.adapter.eventlistadapter;

import java.util.List;

import android.view.View;
import android.widget.LinearLayout;

import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator;
import com.minook.zeppa.singleton.ZeppaEventSingleton;

public class MyEventsAdapter extends AbstractEventLayoutAdapter {

	protected View loaderView;
	
	public MyEventsAdapter(AuthenticatedFragmentActivity activity,
			LinearLayout eventHolder) {
		super(activity, eventHolder);

		if(didLoadInitial()){
			setEventMediators();
		} else {
			ZeppaEventSingleton.getInstance().registerObserver(this);
		}
		
	}

	

	@Override
	public boolean didLoadInitial() {
		return ZeppaEventSingleton.getInstance().hasLoadedInitial();
	}



	@Override
	public void onFinishLoad() {
		notifyDataSetChanged();
	}

	
	@Override
	public void verifyDatasetValid() {
		List<AbstractZeppaEventMediator> hostedEvents = ZeppaEventSingleton.getInstance().getHostedEventMediators();
		
		if(!hostedEvents.containsAll(eventMediators) || !eventMediators.containsAll(hostedEvents)){
			notifyDataSetChanged();
		}
		
	}

	
	@Override
	public void notifyDataSetChanged() {
		setEventMediators();
		super.notifyDataSetChanged();
		
	}


	@Override
	protected void setEventMediators() {
		eventMediators = ZeppaEventSingleton.getInstance().getHostedEventMediators();
		
	}



}
