package com.minook.zeppa.adapter.tagadapter;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.mediator.AbstractEventTagMediator;
import com.minook.zeppa.mediator.DefaultEventTagMediator;
import com.minook.zeppa.singleton.EventTagSingleton;

public class FriendTagAdapter extends AbstractTagAdapter {

	/**
	 * This contructor is invoked if the adapter should hold all mediators for a
	 * given user
	 * 
	 * @param activity
	 * @param tagHolder
	 * @param friendUserId
	 */

	private Long userId;
	private List<Long> tagIds;
	

	public FriendTagAdapter(AuthenticatedFragmentActivity activity,
			LinearLayout tagHolder, Long userId) {
		super(activity, tagHolder);

		this.userId = userId;
		updateTagMediatorList();

	}

	public FriendTagAdapter(AuthenticatedFragmentActivity activity,
			LinearLayout tagHolder, List<Long> tagIds) {
		super(activity, tagHolder);
		
		this.tagIds = tagIds;
		
		updateTagMediatorList();
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		DefaultEventTagMediator tagMediator = (DefaultEventTagMediator) getItem(position);

		if (convertView == null)
			convertView = activity.getLayoutInflater().inflate(
					R.layout.view_tag_others, null, false);

		tagMediator.convertView(activity, convertView);

		return convertView;
	}

	@Override
	public long getItemId(int position) {

		return getItem(position).getTagId();
	}

	@Override
	public void notifyDataSetChanged() {
		updateTagMediatorList();
		super.notifyDataSetChanged();
	}

	public boolean doUpdateAdapter(List<DefaultEventTagMediator> currentMediatorList){
		
		if(currentMediatorList.size() == tagMediators.size()){
			for(DefaultEventTagMediator mediator: currentMediatorList){
				if(!tagMediators.contains(mediator)){
					return true;
				}
			}
		} else {
			return true;
		}
		
		return false;
	}
	
	private void updateTagMediatorList(){
		if(userId != null)
			tagMediators = EventTagSingleton.getInstance().getTagMediatorsForUser(userId);
		else if(tagIds != null){
			tagMediators = EventTagSingleton.getInstance().getTagsFrom(tagIds);
		} else {
			tagMediators = new ArrayList<AbstractEventTagMediator>();
		}
	}
	
}
