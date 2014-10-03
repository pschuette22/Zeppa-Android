package com.minook.zeppa.adapter;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.mediator.DefaultUserInfoMediator;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

public class InviteListAdapter extends BaseAdapter implements OnClickListener {

	private List<DefaultUserInfoMediator> friendInfoManagers;
	private List<Long> invitedUserIds;
	private AuthenticatedFragmentActivity activity;

	public InviteListAdapter(AuthenticatedFragmentActivity activity) {

		this.activity = activity;

		friendInfoManagers = ZeppaUserSingleton.getInstance()
				.getFriendInfoMediators();
		invitedUserIds = new ArrayList<Long>();

	}

	@Override
	public int getCount() {
		return friendInfoManagers.size();
	}

	@Override
	public DefaultUserInfoMediator getItem(int position) {

		return friendInfoManagers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getUserId().longValue();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = activity.getLayoutInflater().inflate(
					R.layout.view_invitelist_item, null, false);
		}

		DefaultUserInfoMediator infoManager = getItem(position);
		infoManager.convertInviteListItemView(activity, convertView);

		CheckBox checkBox = (CheckBox) convertView
				.findViewById(R.id.inviteitem_checkbox);

		if (invitedUserIds.contains(infoManager.getUserId())) {
			checkBox.setChecked(true);
		} else {
			checkBox.setChecked(false);
		}

		convertView.setTag(infoManager.getUserId());

		convertView.setOnClickListener(this);
		return convertView;
	}

	public List<Long> getInvitedUserIds() {
		return invitedUserIds;
	}

	private void addToInvited(View view, Long userId) {
		CheckBox checkBox = (CheckBox) view
				.findViewById(R.id.inviteitem_checkbox);
		checkBox.setChecked(true);
		invitedUserIds.add(userId);
	}

	private void removeFromInvited(View view, Long userId) {
		CheckBox checkBox = (CheckBox) view
				.findViewById(R.id.inviteitem_checkbox);
		checkBox.setChecked(false);
		invitedUserIds.remove(userId);
	}

	@Override
	public void onClick(View v) {
		Long userId = (Long) v.getTag();
		if (invitedUserIds.contains(userId)) {
			removeFromInvited(v, userId);
		} else {
			addToInvited(v, userId);
		}

	}
}
