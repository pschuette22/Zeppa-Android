package com.minook.zeppa.adapter.tagadapter;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.mediator.DefaultEventTagMediator;

public class FriendTagAdapter extends AbstractTagAdapter {

	private List<DefaultEventTagMediator> tagManagers;

	public FriendTagAdapter(AuthenticatedFragmentActivity activity, LinearLayout tagHolder, List<DefaultEventTagMediator> tagManagers) {
		super(activity, tagHolder);

		this.tagManagers = tagManagers;
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		DefaultEventTagMediator tagManager = getItem(position);

		if(convertView == null)
			convertView = inflater.inflate(R.layout.view_tag_others, null, false);

		tagManager.convertView(convertView);
		
		return convertView;
	}


	
	@Override
	public DefaultEventTagMediator getItem(int position) {
		return tagManagers.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return getItem(position).getTagId();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return tagManagers.size();
	}


}
