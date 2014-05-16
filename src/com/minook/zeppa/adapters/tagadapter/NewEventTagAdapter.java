package com.minook.zeppa.adapters.tagadapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.minook.zeppa.R;
import com.minook.zeppa.eventtagendpoint.model.EventTag;
import com.minook.zeppa.singleton.EventTagSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

public class NewEventTagAdapter extends MyTagAdapter {

	private Queue<EventTag> tagQue;
	private Queue<View> viewQue;

	public NewEventTagAdapter(FragmentActivity activity, LinearLayout tagHolder) {

		super(activity, tagHolder);
		tagQue = new LinkedList<EventTag>();
		viewQue = new LinkedList<View>();

		drawTags();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		// Get needed variables
		convertView = inflater.inflate(R.layout.view_tag_others, parent, false);
		EventTag tag = getItem(position);
		CheckedTextView textView = (CheckedTextView) convertView
				.findViewById(R.id.tagview_tagtext);

		textView.setText(tag.getTagText());

		if (tagQue.contains(tag)) {
			textView.setChecked(true);
		} else {
			textView.setChecked(false);
		}

		textView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				handleItemClick(position);
			}

		});

		views.add(position, convertView); // Keep view array synced
		return convertView;
	}

	private void handleItemClick(int position) {

		EventTag tag = getItem(position);
		if (tagQue.contains(tag)) {
			removeTagFromQue(tag);
		} else {
			addTagToQue(tag);
		}

	}

	private void addTagToQue(EventTag tag) {
		View view = views.get(tags.indexOf(tag));
		CheckedTextView textView = (CheckedTextView) view
				.findViewById(R.id.tagview_tagtext);

		if (tagQue.size() >= 6) {
			tagQue.remove();
			View oldTagView = viewQue.remove();
			
			CheckedTextView oldTextView = (CheckedTextView) oldTagView
					.findViewById(R.id.tagview_tagtext);
			oldTextView.setChecked(false);
		}

		textView.setChecked(true);
		tagQue.add(tag);
		viewQue.add(view);

	}

	
	
	private void removeTagFromQue(EventTag tag) {

		int index = tags.indexOf(tag);
		View view = views.get(index);
		tagQue.remove(tag);
		viewQue.remove(view);
		
		CheckedTextView textView = (CheckedTextView) view
				.findViewById(R.id.tagview_tagtext);
		textView.setChecked(false);
		
	}

	public List<Long> getSelectedTagIds() {
		List<Long> usedTags = new ArrayList<Long>();

		if (!tagQue.isEmpty()) {
			Iterator<EventTag> iterator = tagQue.iterator();
			while (iterator.hasNext()) {
				usedTags.add(iterator.next().getKey().getId());
			}

		}
		return usedTags;
	}

	public boolean createdTagInAsync(String tagText) {

		final String text = trimTag(tagText);

		final EventTagSingleton tagSingleton = EventTagSingleton.getInstance();

		for (EventTag tag : tags) {
			if (tagText.equalsIgnoreCase(tag.getTagText())) {

				return false;
			}
		}

		String[] params = { text };

		new AsyncTask<String, Void, EventTag>() {

			@Override
			protected EventTag doInBackground(String... params) {
				String tagText = params[0];
				EventTag tag = tagSingleton.newTagInstance();
				tag.setHostId(ZeppaUserSingleton.getInstance().getUserId());
				tag.setTagText(tagText);

				tag = tagSingleton.insertEventTag(tag,
						getGoogleAccountCredential());
				tags.add(tag);
				View view = getView(tags.indexOf(tag), null, null);
				views.add(view);

				return tag;
			}

			@Override
			protected void onPostExecute(EventTag result) {
				super.onPostExecute(result);
				if (result != null) {

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

}
