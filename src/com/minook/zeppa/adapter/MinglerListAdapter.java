package com.minook.zeppa.adapter;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.mediator.DefaultUserInfoMediator;
import com.minook.zeppa.observer.OnLoadListener;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

public class MinglerListAdapter extends BaseAdapter implements OnLoadListener {

	private AuthenticatedFragmentActivity activity;
	private List<DefaultUserInfoMediator> infoManagers;

	public MinglerListAdapter(AuthenticatedFragmentActivity activity,
			List<DefaultUserInfoMediator> friendInfoManagers) {
		this.activity = activity;

		this.infoManagers = ZeppaUserSingleton.getInstance()
				.getFriendInfoMediators();
	}

	@Override
	public int getCount() {
		return infoManagers.size();
	}

	@Override
	public DefaultUserInfoMediator getItem(int position) {
		return infoManagers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getUserId().longValue();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = activity.getLayoutInflater().inflate(
					R.layout.view_basicuseritem, parent, false);
		}
		DefaultUserInfoMediator infoManager = getItem(position);

		convertView = infoManager.convertFriendListItemView(activity, convertView);

		return convertView;
	}

	/*
	 * Load listener methods
	 */
	@Override
	public boolean didLoadInitial() {
		return ZeppaUserSingleton.getInstance().hasLoadedInitial();
	}

	@Override
	public void onFinishLoad() {
		notifyDataSetChanged();
	}


}