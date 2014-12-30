package com.minook.zeppa.adapter.eventlistadapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.mediator.DefaultUserInfoMediator;
import com.minook.zeppa.singleton.ZeppaEventSingleton;

public class FriendEventsAdapter extends AbstractEventLayoutAdapter {

	private DefaultUserInfoMediator friendMediator;

	public FriendEventsAdapter(DefaultUserInfoMediator friendMediator,
			AuthenticatedFragmentActivity activity, LinearLayout eventHolder) {
		super(activity, eventHolder);

		this.friendMediator = friendMediator;
		eventMediators = ZeppaEventSingleton.getInstance()
				.getEventMediatorsForFriend(
						friendMediator.getUserId().longValue());
		this.initialDidLoad = false;
		setEventMediators();
		drawEvents();
		loadEventsInAsync();

	}

	@Override
	public boolean didLoadInitial() {
		// TODO Auto-generated method stub
		return initialDidLoad;
	}

	@Override
	public void onFinishLoad() {
		if (!initialDidLoad) {
			this.initialDidLoad = true;
		}
		notifyDataSetChanged();
		drawEvents();

	}

	@Override
	public void verifyDatasetValid() {
		// List<ZeppaEvent> friendEvents =
		// ZeppaEventSingleton.getInstance().getEventsFor(friend.getKey().getId().longValue());
		//
		// if(!friendEvents.containsAll(events) ||
		// !events.containsAll(friendEvents)){
		// notifyDataSetChanged();
		// }

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return super.getView(position, convertView, parent);
	}

	@Override
	public void notifyDataSetChanged() {
		// events =
		// ZeppaEventSingleton.getInstance().getEventsFor(friend.getKey().getId().longValue());
		super.notifyDataSetChanged();
	}

	public void loadEventsInAsync() {

//		eventHolder.addView(getLoaderView(), 0);
//
//		new AsyncTask<Void, Void, Boolean>() {
//
//			@Override
//			protected Boolean doInBackground(Void... params) {
//				return ZeppaEventSingleton.getInstance().fetchEventsFor(
//						activity, friendMediator.getUserId(), getCredential());
//			}
//
//			@Override
//			protected void onPostExecute(Boolean result) {
//				super.onPostExecute(result);
//
//			}
//
//		}.execute();
		onFinishLoad();

	}

	@Override
	protected void setEventMediators() {
		eventMediators = ZeppaEventSingleton.getInstance()
				.getEventMediatorsForFriend(
						friendMediator.getUserId().longValue());
	}

}
