package com.minook.zeppa.adapter.tagadapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.mediator.AbstractEventTagMediator;
import com.minook.zeppa.mediator.MyEventTagMediator;

public class CreateEventTagAdapter extends MyTagAdapter implements
		OnClickListener {

	private final String TAG = "CreateEventTagAdapter";
	private Queue<Long> tagIdsQue;

	public CreateEventTagAdapter(AuthenticatedFragmentActivity activity,
			LinearLayout tagHolder) {
		// Pass the activity context and the holder. tagIds is null so all are returned. 
		super(activity, tagHolder, null);

		tagIdsQue = new LinkedList<Long>();

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Get needed variables
		if (convertView == null)
			convertView = activity.getLayoutInflater().inflate(R.layout.view_tag_checkable, parent,
					false);

		MyEventTagMediator tagManager = getItem(position);
		CheckedTextView textView = (CheckedTextView) convertView
				.findViewById(R.id.tagview_tagtext);

		textView.setText(tagManager.getText());

		Long tagId = tagManager.getTagId();

		if (tagIdsQue.contains(tagId)) {
			textView.setChecked(true);
		} else {
			textView.setChecked(false);
		}

		textView.setTag(tagId);
		textView.setOnClickListener(this);

		return convertView;
	}

	private void addTagToQue(View view, Long tagId) {

		if(view == null){
			view = locateViewByTag(tagId);
		}
		
		CheckedTextView textView = (CheckedTextView) view;

		if (tagIdsQue.size() >= 6) {
			Long oldTagId = tagIdsQue.remove();

			CheckedTextView checkedText = locateViewByTag(oldTagId);
			checkedText.setChecked(false);

		}

		textView.setChecked(true);
		tagIdsQue.add(tagId);

	}

	private void removeTagFromQue(View view, Long tagId) {

		CheckedTextView textView = (CheckedTextView) view;
		tagIdsQue.remove(tagId);
		textView.setChecked(false);

	}

	private CheckedTextView locateViewByTag(Long tagId) {
		CheckedTextView view = null;
		for (int i = 0; i < tagHolder.getChildCount(); i++) {

			LinearLayout tagLine = (LinearLayout) tagHolder.getChildAt(i);

			view = (CheckedTextView) tagLine.findViewWithTag(tagId);
			if (view != null) {
				break;
			}

		}

		return view;
	}

	public List<Long> getSelectedTagIds() {
		List<Long> usedTags = new ArrayList<Long>();

		if (!tagIdsQue.isEmpty()) {
			Iterator<Long> iterator = tagIdsQue.iterator();
			while (iterator.hasNext()) {
				usedTags.add(iterator.next());
			}

		}
		return usedTags;
	}

	
	

	@Override
	protected AbstractEventTagMediator getMatchingMediator(String tagText) {
		AbstractEventTagMediator mediator = super.getMatchingMediator(tagText);
		
		if(mediator != null){
			addTagToQue(null, mediator.getTagId());
		}
		
		return mediator;
	}

	@Override
	protected void onTagCreated(MyEventTagMediator tagManager) {

		if (tagManager != null) {
			if (tagIdsQue.size() >= 6) {
				tagIdsQue.remove();
			}

			tagIdsQue.add(tagManager.getTagId());
		}
		
		super.onTagCreated(tagManager);
	}

	@Override
	public void onClick(View v) {
		Long tagId = (Long) v.getTag();

		Log.d(TAG, "Clicked Tag: " + tagId);
		if (tagIdsQue.contains(tagId)) {
			removeTagFromQue(v, tagId);
		} else {
			addTagToQue(v, tagId);
		}

	}

}
