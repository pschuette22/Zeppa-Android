package com.minook.zeppa.adapter.eventlistadapter;

import android.widget.ListView;

import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.singleton.ZeppaEventSingleton;

public class WatchListAdapter extends FeedListAdapter {
	
	public WatchListAdapter(AuthenticatedFragmentActivity activity,
			ListView list) {
		super(activity, list);
	}

	@Override
	public void verifyDatasetValid() {
//		List<ZeppaEvent> interestingEvents = ZeppaEventSingleton.getInstance()
//				.getInterestingEvents();
//		if (!events.containsAll(events)
//				|| !interestingEvents.containsAll(events)) {
//			notifyDataSetChanged();
//		}

	}

	@Override
	protected void setEventMediators() {
		eventMediators = ZeppaEventSingleton.getInstance().getInterestingEventMediators();
	}


	

}
