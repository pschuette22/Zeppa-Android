package com.minook.zeppa.adapters.eventadapter;

import java.util.List;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.activities.AuthenticatedFragmentActivity;
import com.minook.zeppa.activities.NewEventActivity;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;

public class FeedListAdapter extends EventListAdapter {

	public FeedListAdapter(AuthenticatedFragmentActivity activity) {
		super(activity);

		events = ZeppaEventSingleton.getInstance().getZeppaEvents();
		if (!initialLoaded) {
			ZeppaEventSingleton.getInstance().listenForLoad(this);
		}

	}

	@Override
	public int getCount() {

		if (initialLoaded && events.size() == 0) {
			return 1;
		} else {
			return super.getCount();
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = super.getView(position, convertView, parent);
		
		if(initialLoaded && events.size() == 0){
			TextView text = new TextView(activity);
			text.setText("Somehow prompt users to create a new activity and click here for it");
			text.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent toNewEvent = new Intent(activity,
							NewEventActivity.class);

					long nullLong = -1;
					toNewEvent.putExtra(Constants.INTENT_EVENT_STARTTIME, nullLong);
					toNewEvent.putExtra(Constants.INTENT_EVENT_ENDTIME, nullLong);
					activity.startActivity(toNewEvent);
					activity.overridePendingTransition(R.anim.slide_up_in,
							R.anim.hold);
					
				}
			});
			return text;
		}
		
		return convertView;
	}

	@Override
	public void notifyIfDataChanged() {		
		List<ZeppaEvent> heldEvents = ZeppaEventSingleton.getInstance().getZeppaEvents();
		if(!events.containsAll(heldEvents)
				&& !heldEvents.containsAll(events)){
			events = heldEvents;
			notifyDataSetChanged();
		}
		
	}

}
