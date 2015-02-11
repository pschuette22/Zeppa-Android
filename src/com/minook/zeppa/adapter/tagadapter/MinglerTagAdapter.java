package com.minook.zeppa.adapter.tagadapter;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.mediator.AbstractEventTagMediator;
import com.minook.zeppa.mediator.AbstractZeppaUserMediator;
import com.minook.zeppa.mediator.DefaultEventTagMediator;
import com.minook.zeppa.mediator.DefaultUserInfoMediator;
import com.minook.zeppa.mediator.DefaultZeppaEventMediator;
import com.minook.zeppa.singleton.EventTagSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

public class MinglerTagAdapter extends AbstractTagAdapter implements
		OnClickListener {

	/**
	 * This contructor is invoked if the adapter should hold all mediators for a
	 * given user
	 * 
	 * @param activity
	 * @param tagHolder
	 * @param friendUserId
	 */

	private Long userId;
	private boolean doesMingleWithHost;
	private List<Long> tagIds;

	public MinglerTagAdapter(AuthenticatedFragmentActivity activity,
			LinearLayout tagHolder, Long userId, List<Long> tagIds) {
		super(activity, tagHolder);

		this.userId = userId;
		this.tagIds = tagIds;

		DefaultUserInfoMediator mediator = (DefaultUserInfoMediator) ZeppaUserSingleton
				.getInstance().getAbstractUserMediatorById(userId);
		doesMingleWithHost = mediator.isMingling();

		updateTagMediatorList();

	}

	@Override
	public List<AbstractEventTagMediator> getCurrentTagMediators() {

		List<AbstractEventTagMediator> mediators;
		if (tagIds == null) {
			mediators = EventTagSingleton.getInstance().getTagMediatorsForUser(
					userId);
		} else {
			mediators = EventTagSingleton.getInstance().getTagsFrom(tagIds);
		}

		return mediators;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		DefaultEventTagMediator tagMediator = (DefaultEventTagMediator) getItem(position);

		if (convertView == null)
			convertView = activity.getLayoutInflater().inflate(
					R.layout.view_tag_checkable, parent, false);

		tagMediator.convertView(convertView);
		if (doesMingleWithHost) {
			convertView.findViewById(R.id.tagview_tagtext).setClickable(true);
			convertView.findViewById(R.id.tagview_tagtext).setEnabled(true);
			convertView.findViewById(R.id.tagview_tagtext).setOnClickListener(
					this);
		} else {
			convertView.findViewById(R.id.tagview_tagtext).setClickable(false);
			convertView.findViewById(R.id.tagview_tagtext).setEnabled(false);
		}
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

	@Override
	public boolean didLoadInitial() {

		return true;
	}

	@Override
	public void onFinishLoad() {
		// TODO Auto-generated method stub

	}

	public boolean doUpdateAdapter(
			List<DefaultEventTagMediator> currentMediatorList) {

		if (currentMediatorList.size() == tagMediators.size()) {
			for (DefaultEventTagMediator mediator : currentMediatorList) {
				if (!tagMediators.contains(mediator)) {
					return true;
				}
			}
		} else {
			return true;
		}

		return false;
	}

	private void updateTagMediatorList() {
		if (tagIds != null) {
			tagMediators = EventTagSingleton.getInstance().getTagsFrom(tagIds);
		} else if (userId != null)
			tagMediators = EventTagSingleton.getInstance()
					.getTagMediatorsForUser(userId);
		else {
			tagMediators = new ArrayList<AbstractEventTagMediator>();
		}
	}

	@Override
	public void onClick(View v) {

		if (v instanceof CheckedTextView) {
			DefaultEventTagMediator mediator = (DefaultEventTagMediator) v
					.getTag();

			if (mediator.isFollowing()) {
				mediator.unfollowTagInAsync(
						activity.getGoogleAccountCredential(),
						(CheckedTextView) v);
				((CheckedTextView) v).setChecked(false);
			} else {
				mediator.followTagInAsync(
						activity.getGoogleAccountCredential(),
						(CheckedTextView) v);
				((CheckedTextView) v).setChecked(true);

			}

		}

	}

}
