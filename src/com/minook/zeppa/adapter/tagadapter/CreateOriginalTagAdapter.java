package com.minook.zeppa.adapter.tagadapter;

import java.util.HashMap;
import java.util.Iterator;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.eventtagendpoint.model.EventTag;

public class CreateOriginalTagAdapter extends BaseAdapter {

	private AuthenticatedFragmentActivity activity;
	private HashMap<EventTag, View> tags;

	private String[] DEFAULT_TAGS = { "Sports", "HangingOut", "NightOut",
			"Relaxing", "RandomAdventures", "HealthyLiving" };

	public CreateOriginalTagAdapter(AuthenticatedFragmentActivity activity) {
		this.activity = activity;
		this.tags = new HashMap<EventTag, View>();
		
		for(int i = 0; i < 6; i++ ){
			EventTag tag = new EventTag();
			tag.setTagText(DEFAULT_TAGS[i]);
			View v = createTagView(tag, null);
			tags.put(tag, v);
		}
		
	}

	public View addEventTag(String tagText) {
		tagText = tagText.trim();

		if (tagText.isEmpty()) {
			return null;
		}

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < tagText.length(); i++) {
			char c = tagText.charAt(i);
			if (!Character.isWhitespace(c)) {
				builder.append(c);
			}

		}

		tagText = builder.toString();
		if (tagText.isEmpty()) {
			return null;
		}

		Iterator<EventTag> iterator = tags.keySet().iterator();
		while (iterator.hasNext()) {
			if (iterator.next().getTagText().equalsIgnoreCase(tagText)) {
				// Already exists
				return null;
			}
		}

		EventTag tag = new EventTag();
		tag.setTagText(tagText);

		View v = createTagView(tag, null);
		tags.put(tag, v);

		return v;
	}

	public void removeTagByView(View v) {
		tags.remove(v);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return tags.size();
	}

	@Override
	public EventTag getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {

		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		
		return null;
	}

	// Quickly inflate a deletable tag view
	private View createTagView(EventTag tag, ViewGroup parent) {
		View v = activity.getLayoutInflater().inflate(R.layout.view_tag_delete,
				parent, false);
		TextView text = (TextView) v.findViewById(R.id.deletetag_text);
		text.setText(tag.getTagText());

		return v;
	}

}
