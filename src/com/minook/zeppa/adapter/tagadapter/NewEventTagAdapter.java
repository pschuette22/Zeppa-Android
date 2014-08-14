package com.minook.zeppa.adapter.tagadapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.eventtagendpoint.model.EventTag;
import com.minook.zeppa.mediator.AbstractEventTagMediator;
import com.minook.zeppa.mediator.MyEventTagMediator;
import com.minook.zeppa.singleton.EventTagSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

public class NewEventTagAdapter extends MyTagAdapter implements OnClickListener {

	private Queue<Long> tagIdsQue;

	public NewEventTagAdapter(AuthenticatedFragmentActivity activity,
			LinearLayout tagHolder, List<MyEventTagMediator> tagManagers) {
		super(activity, tagHolder, tagManagers);

		tagIdsQue = new LinkedList<Long>();

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Get needed variables
		if (convertView == null)
			convertView = inflater.inflate(R.layout.view_tag_others, parent,
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

		convertView.setTag(tagId);
		convertView.setOnClickListener(this);

		return convertView;
	}

	private void addTagToQue(View view, Long tagId) {

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

	public boolean createdTagInAsync(EditText textView) {

		final String text = trimTag(textView.getText().toString());

		for (AbstractEventTagMediator tagManager : tagManagers) {
			if (text.equalsIgnoreCase(tagManager.getText())) {
				if (!tagIdsQue.contains(tagManager.getTagId())) {
					CheckedTextView checkText = locateViewByTag(tagManager
							.getTagId());
					addTagToQue(checkText, tagManager.getTagId());
				}
				return false;
			}
		}

		textView.setEnabled(false);

		Object[] params = { text, textView };

		new AsyncTask<Object, Void, MyEventTagMediator>() {

			private EditText textView;

			@Override
			protected MyEventTagMediator doInBackground(Object... params) {
				String tagText = (String) params[0];
				textView = (EditText) params[1];
				EventTagSingleton tagSingleton = EventTagSingleton
						.getInstance();

				EventTag tag = tagSingleton.newTagInstance();
				tag.setHostId(ZeppaUserSingleton.getInstance().getUserId());
				tag.setTagText(tagText);

				MyEventTagMediator tagManager = tagSingleton
						.insertEventTag(tag,
								activity.getGoogleAccountCredential());

				if (tagIdsQue.size() >= 6) {
					tagIdsQue.remove();
				}

				tagIdsQue.add(tag.getKey().getId());

				return tagManager;
			}

			@Override
			protected void onPostExecute(MyEventTagMediator result) {
				super.onPostExecute(result);

				textView.setEnabled(true);

				if (result != null) {
					tagManagers.add(result);
					drawTags();
					textView.setText("");

				} else {
					Toast.makeText(activity, "Error Creating Tag",
							Toast.LENGTH_SHORT).show();
				}

			}

		}.execute(params);

		return true;

	}

	private String trimTag(String originalText) {
		StringBuilder newText = new StringBuilder();

		for (int i = 0; i < originalText.length(); i++) {
			char character = originalText.charAt(i);
			if (!Character.isWhitespace(character)) {
				newText.append(character);
			}
		}

		return newText.toString();
	}

	@Override
	public void onClick(View v) {
		Long tagId = (Long) v.getTag();

		if (tagIdsQue.contains(tagId)) {
			removeTagFromQue(v, tagId);
		} else {
			addTagToQue(v, tagId);
		}

	}

}
