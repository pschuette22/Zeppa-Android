package com.minook.zeppa.adapter.tagadapter;

import java.util.List;

import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.eventtagendpoint.model.EventTag;
import com.minook.zeppa.mediator.AbstractEventTagMediator;
import com.minook.zeppa.mediator.MyEventTagMediator;
import com.minook.zeppa.singleton.EventTagSingleton;

public class MyTagAdapter extends AbstractTagAdapter {

	private List<Long> tagIds;

	public MyTagAdapter(AuthenticatedFragmentActivity activity,
			LinearLayout tagHolder, List<Long> tagIds) {
		super(activity, tagHolder);
		this.tagIds = tagIds;

		setTags();

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		MyEventTagMediator tagMediator = (MyEventTagMediator) tagMediators
				.get(position);

		if (convertView == null)
			convertView = activity.getLayoutInflater().inflate(
					R.layout.view_tag_base, parent, false);

		tagMediator.convertView(convertView);
		convertView.setClickable(false);

		return convertView;

	}

	@Override
	public MyEventTagMediator getItem(int position) {
		return (MyEventTagMediator) tagMediators.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getTagId().longValue();
	}

	@Override
	public int getCount() {
		return tagMediators.size();
	}

	@Override
	public List<AbstractEventTagMediator> getCurrentTagMediators() {
		List<AbstractEventTagMediator> mediators;
		if (tagIds == null) {
			mediators = EventTagSingleton.getInstance().getMyTags();
		} else {
			mediators = EventTagSingleton.getInstance().getTagsFrom(tagIds);
		}

		return mediators;
	}

	@Override
	public void notifyDataSetChanged() {
		setTags();
		super.notifyDataSetChanged();
	}

	public boolean tagsAreCurrent() {

		List<AbstractEventTagMediator> currentMediators;

		if (tagIds == null) {
			currentMediators = EventTagSingleton.getInstance().getMyTags();
		} else {
			currentMediators = EventTagSingleton.getInstance().getTagsFrom(
					tagIds);
		}

		return (currentMediators.containsAll(tagMediators) && tagMediators
				.containsAll(currentMediators));

	}

	private void setTags() {
		if (tagIds == null) {
			tagMediators = EventTagSingleton.getInstance().getMyTags();
		} else {
			tagMediators = EventTagSingleton.getInstance().getTagsFrom(tagIds);
		}
	}

	public boolean createTagInAsync(EditText textView) {

		String text = trimTag(textView.getText().toString());

		if (text.isEmpty()) {
			return false;
		}

		if (getMatchingMediator(text) != null) {
			Toast.makeText(activity, "Already Made!", Toast.LENGTH_SHORT)
					.show();
			return false;
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
				tag.setTagText(tagText);

				MyEventTagMediator tagMediator = tagSingleton.insertEventTag(
						tag, activity.getGoogleAccountCredential());

				return tagMediator;
			}

			@Override
			protected void onPostExecute(MyEventTagMediator result) {
				super.onPostExecute(result);

				textView.setEnabled(true);

				if (result != null) {

					onTagCreated(result);
					textView.setText("");

				} else {
					Toast.makeText(activity, "Error Creating Tag",
							Toast.LENGTH_SHORT).show();
				}

			}

		}.execute(params);

		return true;

	}

	protected AbstractEventTagMediator getMatchingMediator(String tagText) {

		for (AbstractEventTagMediator tagMediator : tagMediators) {
			if (tagText.equalsIgnoreCase(tagMediator.getText())) {
				return tagMediator;
			}
		}

		return null;
	}

	protected void onTagCreated(MyEventTagMediator tagMediator) {
		tagMediators.add(tagMediator);
		drawTags(); // TODO: just add a single view instead of redrawing the
					// whole thing
	}

	protected String trimTag(String originalText) {
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
