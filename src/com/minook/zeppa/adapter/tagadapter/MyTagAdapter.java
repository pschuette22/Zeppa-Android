package com.minook.zeppa.adapter.tagadapter;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.mediator.MyEventTagMediator;

public class MyTagAdapter extends AbstractTagAdapter {


	protected List<MyEventTagMediator> tagManagers;
	
	public MyTagAdapter(AuthenticatedFragmentActivity activity, LinearLayout tagHolder, List<MyEventTagMediator> tagManagers) {
		super(activity, tagHolder);
	
		this.tagManagers = tagManagers;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		MyEventTagMediator tagManager = tagManagers.get(position);

		if(convertView == null)
			convertView = inflater.inflate(R.layout.view_tag_owned, null, false);

		tagManager.convertView(convertView);
		
		return convertView;

	}


	@Override
	public MyEventTagMediator getItem(int position) {
		return (MyEventTagMediator) tagManagers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getTagId().longValue();
	}

	@Override
	public int getCount() {
		return tagManagers.size();
	}

}
