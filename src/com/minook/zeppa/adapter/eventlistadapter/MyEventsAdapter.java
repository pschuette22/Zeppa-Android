package com.minook.zeppa.adapter.eventlistadapter;

import android.widget.LinearLayout;

import com.minook.zeppa.activity.AuthenticatedFragmentActivity;

public class MyEventsAdapter extends AbstractEventLayoutAdapter {

	public MyEventsAdapter(AuthenticatedFragmentActivity activity,
			LinearLayout eventHolder) {
		super(activity, eventHolder);

//		events = ZeppaEventSingleton.getInstance().getHostedEvents();
//		if (!ZeppaEventSingleton.getInstance().hasLoadedInitial()) {
//			drawEvents();
//		}
		
		
	}

	

	@Override
	public boolean didLoadInitial() {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public void onFinishLoad() {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void verifyDatasetValid() {
//		List<ZeppaEvent> hostedEvents = ZeppaEventSingleton.getInstance().getHostedEvents();
//		
//		if(!hostedEvents.containsAll(events) || !events.containsAll(hostedEvents)){
//			notifyDataSetChanged();
//		}
		
	}

	
	@Override
	public void notifyDataSetChanged() {
//		events = ZeppaEventSingleton.getInstance().getHostedEvents();
//		super.notifyDataSetChanged();
	}


	@Override
	protected void setEventMediators() {
		// TODO Auto-generated method stub
		
	}



}
