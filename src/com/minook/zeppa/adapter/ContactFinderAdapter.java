package com.minook.zeppa.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.minook.zeppa.R;
import com.minook.zeppa.mediator.DefaultUserInfoMediator;
import com.minook.zeppa.mediator.MyZeppaUserMediator;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

public class ContactFinderAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;

	private MyZeppaUserMediator userManager;

	private List<DefaultUserInfoMediator> infoManagers;

	public ContactFinderAdapter(Context context) {
		this.context = context;
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.userManager = ZeppaUserSingleton.getInstance().getUserMediator();

		infoManagers = new ArrayList<DefaultUserInfoMediator>();

	}

	@Override
	public void notifyDataSetChanged() {

		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() { // size is the addition between pending and
		// possible connections
		return infoManagers.size();
	}

	@Override
	public DefaultUserInfoMediator getItem(int position) {
		// if (position == 0) {
		// return null;
		// } else if (position == (1 + pendingList.size())) {
		// return null;
		// } else if (position <= pendingList.size()) {
		// return pendingList.get(position - 1);
		// } else {
		// return potentialList.get(position - (2 + pendingList.size()));
		// }

		return null;
	}

	@Override
	public long getItemId(int position) {

		return getItem(position).getUserId().longValue();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		
		return convertView;
	}

	/*
	 * Private methods
	 */

	private String getHeader(int position) {
		Resources res = context.getResources();
		if (position == 0) {
			return res.getString(R.string.pending_requests);
		} else {
			return res.getString(R.string.contacts_using);
		}
	}

	/*
	 * AsyncTasks
	 */

}
