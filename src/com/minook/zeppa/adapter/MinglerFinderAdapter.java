package com.minook.zeppa.adapter;

import java.util.List;

import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.mediator.DefaultUserInfoMediator;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

public class MinglerFinderAdapter extends BaseAdapter {

	private AuthenticatedFragmentActivity context;

	private List<DefaultUserInfoMediator> possibleConnectionMediators;
	private List<DefaultUserInfoMediator> pendingRequestMediators;

	public MinglerFinderAdapter(AuthenticatedFragmentActivity context) {
		this.context = context;
		setMediators();

	}

	@Override
	public void notifyDataSetChanged() {

		if (!verifyDatasetValid()) {
			setMediators();
			super.notifyDataSetChanged();

		}

	}

	@Override
	public int getCount() {
		return possibleConnectionMediators.size()
				+ pendingRequestMediators.size();
	}

	@Override
	public DefaultUserInfoMediator getItem(int position) {
		int pending = pendingRequestMediators.size();
		if (position >= pending) {
			return possibleConnectionMediators.get(position - pending);
		} else {
			return pendingRequestMediators.get(position);
		}
	}

	@Override
	public long getItemId(int position) {

		return getItem(position).getUserId().longValue();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		DefaultUserInfoMediator mediator = getItem(position);

		if (position < pendingRequestMediators.size()) {
			convertView = context.getLayoutInflater().inflate(
					R.layout.view_addcontact_itemrespond, parent, false);
			mediator.convertRespondConnectListItemView(context, convertView);
		} else {
			convertView = context.getLayoutInflater().inflate(
					R.layout.view_addcontact_itemsend, parent, false);
			mediator.convertRequestConnectListItemView(context, convertView);
		}

		return convertView;
	}

	public boolean verifyDatasetValid() {

		List<DefaultUserInfoMediator> currentPossibleMediators = ZeppaUserSingleton
				.getInstance().getPossibleFriendInfoMediators();
		List<DefaultUserInfoMediator> currentPendingMediators = ZeppaUserSingleton
				.getInstance().getPendingFriendRequests();

		return (possibleConnectionMediators.contains(currentPossibleMediators)
				&& pendingRequestMediators.containsAll(currentPendingMediators)
				&& currentPossibleMediators
						.containsAll(possibleConnectionMediators) && currentPendingMediators
					.containsAll(pendingRequestMediators));
	}

	/*
	 * Private methods
	 */

	private void setMediators() {
		possibleConnectionMediators = ZeppaUserSingleton.getInstance()
				.getPossibleFriendInfoMediators();
		pendingRequestMediators = ZeppaUserSingleton.getInstance()
				.getPendingFriendRequests();

	}

	private String getHeader(int position) {
		Resources res = context.getResources();
		if (position == 0) {
			return res.getString(R.string.pending_requests);
		} else {
			return res.getString(R.string.contacts_using);
		}
	}

}
