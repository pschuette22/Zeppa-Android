package com.minook.zeppa.adapters.eventadapter;

import java.util.List;

import com.minook.zeppa.activities.AuthenticatedFragmentActivity;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;

public class WatchListAdapter extends FeedListAdapter {

	public WatchListAdapter(AuthenticatedFragmentActivity activity) {
		super(activity);
		events = ZeppaEventSingleton.getInstance().getInterestingEvents();
	}

	@Override
	public void notifyIfDataChanged() {
		List<ZeppaEvent> heldEvents = ZeppaEventSingleton.getInstance()
				.getInterestingEvents();
		if (!events.containsAll(heldEvents) && !heldEvents.containsAll(events)) {
			events = heldEvents;
			notifyDataSetChanged();
		}

	}

}
