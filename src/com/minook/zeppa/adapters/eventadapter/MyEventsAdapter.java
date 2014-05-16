package com.minook.zeppa.adapters.eventadapter;

import android.widget.LinearLayout;

import com.minook.zeppa.activities.AuthenticatedFragmentActivity;
import com.minook.zeppa.singleton.ZeppaEventSingleton;

public class MyEventsAdapter extends EventLayoutAdapter {


	public MyEventsAdapter(AuthenticatedFragmentActivity activity,
			LinearLayout eventHolder) {
		super(activity, eventHolder);
		
		if(ZeppaEventSingleton.getInstance().hasLoadedInitial()){
		events = ZeppaEventSingleton.getInstance().getHostedEvents();
		drawEvents();
		} else {
			
		}
	}

	

}
