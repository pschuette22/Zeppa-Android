package com.minook.zeppa.adapter.eventlistadapter;

import android.widget.ListView;

import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.singleton.ZeppaEventSingleton;

public class AgendaListAdapter extends FeedListAdapter {

	public AgendaListAdapter(AuthenticatedFragmentActivity activity,
			ListView list) {
		super(activity, list);
	}

	

	@Override
	protected void setEventMediators() {
		eventMediators = ZeppaEventSingleton.getInstance()
				.getInterestingEventMediators();
	}

}
