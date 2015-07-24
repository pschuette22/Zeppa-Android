package com.minook.zeppa.adapter.eventlistadapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator;
import com.minook.zeppa.mediator.DefaultUserInfoMediator;
import com.minook.zeppa.singleton.ZeppaEventSingleton;

import java.util.List;

public class MinglerEventsAdapter extends AbstractEventLayoutAdapter {

	private DefaultUserInfoMediator friendMediator;

	public MinglerEventsAdapter(DefaultUserInfoMediator friendMediator,
			AuthenticatedFragmentActivity activity, LinearLayout eventHolder) {
		super(activity, eventHolder);

		this.friendMediator = friendMediator;
		setEventMediators();
	}	

	@Override
	protected List<AbstractZeppaEventMediator> getCurrentEventMediators() {
		return ZeppaEventSingleton.getInstance().getEventMediatorsForFriend(friendMediator.getUserId());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return super.getView(position, convertView, parent);
	}


	@Override
	protected void setEventMediators() {
		eventMediators = ZeppaEventSingleton.getInstance()
				.getEventMediatorsForFriend(
						friendMediator.getUserId().longValue());
	}

}
