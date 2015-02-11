package com.minook.zeppa.adapter;

import java.util.List;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.mediator.DefaultUserInfoMediator;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton.OnMinglersLoadListener;

public class MinglerListAdapter extends BaseAdapter implements
		OnMinglersLoadListener, OnItemClickListener {

	private AuthenticatedFragmentActivity activity;
	private List<DefaultUserInfoMediator> infoManagers;

	public MinglerListAdapter(AuthenticatedFragmentActivity activity,
			List<DefaultUserInfoMediator> friendInfoManagers) {
		this.activity = activity;

		if (friendInfoManagers == null) {
			this.infoManagers = ZeppaUserSingleton.getInstance()
					.getFriendInfoMediators();
		} else {
			this.infoManagers = friendInfoManagers;
		}
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

		convertView = infoManager.convertFriendListItemView(activity,
				convertView);

		return convertView;
	}

	@Override
	public void onMinglersLoaded() {
		notifyDataSetChanged();

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		DefaultUserInfoMediator mediator = (DefaultUserInfoMediator) getItem(position);
		Intent intent = mediator.getToUserIntent(activity);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
		
	}

}