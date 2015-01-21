package com.minook.zeppa.adapter.eventlistadapter;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator;
import com.minook.zeppa.mediator.DefaultUserInfoMediator;
import com.minook.zeppa.singleton.ZeppaEventSingleton;

public class FriendEventsAdapter extends AbstractEventLayoutAdapter {

	private DefaultUserInfoMediator friendMediator;

	public FriendEventsAdapter(DefaultUserInfoMediator friendMediator,
			AuthenticatedFragmentActivity activity, LinearLayout eventHolder) {
		super(activity, eventHolder);

		this.friendMediator = friendMediator;
		loadEventsInAsync();

	}	

	@Override
	protected List<AbstractZeppaEventMediator> getCurrentEventMediators() {
		return ZeppaEventSingleton.getInstance().getEventMediatorsForFriend(friendMediator.getUserId());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return super.getView(position, convertView, parent);
	}

	public void loadEventsInAsync() {

		// eventHolder.addView(getLoaderView(), 0);
		//
		// new AsyncTask<Void, Void, Boolean>() {
		//
		// @Override
		// protected Boolean doInBackground(Void... params) {
		// return ZeppaEventSingleton.getInstance().fetchEventsFor(
		// activity, friendMediator.getUserId(), getCredential());
		// }
		//
		// @Override
		// protected void onPostExecute(Boolean result) {
		// super.onPostExecute(result);
		//
		// }
		//
		// }.execute();
		onFinishLoad();

	}

	@Override
	protected void setEventMediators() {
		eventMediators = ZeppaEventSingleton.getInstance()
				.getEventMediatorsForFriend(
						friendMediator.getUserId().longValue());
	}

}
