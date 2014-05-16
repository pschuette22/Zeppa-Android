package com.minook.zeppa.adapters.tagadapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.R;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.eventtagendpoint.model.EventTag;
import com.minook.zeppa.singleton.EventTagSingleton;

public class TagAdapter extends BaseAdapter {

	protected FragmentActivity activity;
	protected List<EventTag> tags;
	protected List<View> views;
	
	protected LayoutInflater inflater;
	protected LinearLayout tagHolder;

	public TagAdapter(FragmentActivity activity, LinearLayout tagHolder) {

		this.activity = activity;
		this.inflater = activity.getLayoutInflater();
		this.tagHolder = tagHolder;
		this.tags = new ArrayList<EventTag>();
		this.views = new ArrayList<View>();
		
	}

	@Override
	public int getCount() {
		return tags.size();
	}

	@Override
	public EventTag getItem(int position) {
		return tags.get(position);
	}

	@Override
	public long getItemId(int position) {
		return tags.get(position).getKey().getId().longValue();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		return null;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		drawTags();
	}

	protected View getViewFor(EventTag tag){
		return views.get(tags.indexOf(tag));
	}
	
	protected EventTag getTagFor(View view){
		return tags.get(views.indexOf(view));
	}
	
	public boolean hasLoadedTags() {
		return EventTagSingleton.getInstance().hasLoadedTags();
	}

	public void drawTags() {

		// clear all the views
		tagHolder.removeAllViews();

		if (!tags.isEmpty() && hasLoadedTags()) {
			EventTagSingleton tagSingleton = EventTagSingleton.getInstance();

			List<EventTag> tags = tagSingleton.getTags();

			LinearLayout currentLine = (LinearLayout) inflater.inflate(
					R.layout.view_tag_line, null, false);

			tagHolder.addView(currentLine);
			currentLine.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.EXACTLY);
			int lineWidth = activity.getResources().getDisplayMetrics().widthPixels;
			int tagsWidth = 0;

			for (int i = 0; i < tags.size(); i++) {
				View tagView = getView(i, null, null);
				tagView.measure(MeasureSpec.UNSPECIFIED,
						MeasureSpec.UNSPECIFIED);
				int tagWidth = tagView.getMeasuredWidth();

				if ((lineWidth - tagsWidth) < tagWidth) {
					currentLine = (LinearLayout) inflater.inflate(
							R.layout.view_tag_line, null, false);
					tagHolder.addView(currentLine);
					tagsWidth = 0;
				}

				currentLine.addView(tagView);

				tagsWidth += tagWidth;

			}

		} else if(!hasLoadedTags()) { // Tags have not been loaded by activity
			View tagLoaderView = (View) inflater.inflate(
					R.layout.view_loaderview, null, false);
			tagHolder.addView(tagLoaderView);

		}
		
	}

	protected GoogleAccountCredential getGoogleAccountCredential() {
		return ((ZeppaApplication) activity.getApplication())
				.getGoogleAccountCredential();
	}

}
