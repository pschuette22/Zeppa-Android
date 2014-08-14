package com.minook.zeppa.adapter.eventlistadapter;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.mediator.DefaultUserInfoMediator;
import com.minook.zeppa.mediator.DefaultZeppaEventMediator;
import com.minook.zeppa.singleton.ZeppaEventSingleton;

public class FriendEventsAdapter extends AbstractEventLayoutAdapter {

	private DefaultUserInfoMediator friendManager;
	private List<DefaultZeppaEventMediator> eventManagers;

	public FriendEventsAdapter(DefaultUserInfoMediator friendManager,
			AuthenticatedFragmentActivity activity, LinearLayout eventHolder) {
		super(activity, eventHolder);

		this.friendManager = friendManager;
		this.eventManagers = ZeppaEventSingleton.getInstance()
				.getEventManagersForFriend(
						friendManager.getUserId().longValue());
		this.initialDidLoad = false;

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
	protected View makeLoaderView() {
		loaderView = super.makeLoaderView();
		TextView text = (TextView) loaderView
				.findViewById(R.id.loaderview_text);
		text.setText("Finding some of " + friendManager.getGivenName()
				+ "'s events");
		return loaderView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		super.getView(position, convertView, parent);
		if (convertView == null) {
			convertView = activity.getLayoutInflater().inflate(
					R.layout.view_eventlist_item, parent, false);
		}

		return convertView;
	}

	@Override
	public void notifyDataSetChanged() {
		// events =
		// ZeppaEventSingleton.getInstance().getEventsFor(friend.getKey().getId().longValue());
		super.notifyDataSetChanged();
	}

	public void loadEventsInAsync() {

		eventHolder.addView(makeLoaderView(), 0);

		// new AsyncTask<Void, Void, Boolean>(){
		//
		//
		// @Override
		// protected Boolean doInBackground(Void... params) {
		// return ZeppaEventSingleton.getInstance().fetchEventsFor(activity,
		// friend.getKey().getId(), getCredential());
		// }
		//
		// @Override
		// protected void onPostExecute(Boolean result) {
		// super.onPostExecute(result);
		//
		// if(!result){
		// Toast.makeText(activity, "Error On Load", Toast.LENGTH_SHORT).show();
		// }
		//
		// }
		//
		//
		//
		// }.execute();

	}

	@Override
	protected void setEventManagers() {
		eventManagers = ZeppaEventSingleton.getInstance()
				.getEventManagersForFriend(
						friendManager.getUserId().longValue());
	}

}