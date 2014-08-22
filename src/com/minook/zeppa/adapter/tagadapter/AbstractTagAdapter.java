package com.minook.zeppa.adapter.tagadapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;

public abstract class AbstractTagAdapter extends BaseAdapter {

	protected AuthenticatedFragmentActivity activity;

	protected LayoutInflater inflater;
	protected LinearLayout tagHolder;
	protected boolean hasLoadedTags;
	
	public AbstractTagAdapter(AuthenticatedFragmentActivity activity,
			LinearLayout tagHolder) {

		this.activity = activity;
		this.inflater = activity.getLayoutInflater();
		this.tagHolder = tagHolder;
		this.tagHolder.setClickable(false);

	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		drawTags();
	}
		
	public void drawTags() {
		tagHolder.removeAllViews();

		if (getCount() > 0) {

			LinearLayout currentLine = (LinearLayout) inflater.inflate(
					R.layout.view_tag_line, null, false);

			tagHolder.addView(currentLine);
			currentLine.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.EXACTLY);
			int lineWidth = activity.getResources().getDisplayMetrics().widthPixels;
			int tagsWidth = 0;

			for (int i = 0; i < getCount(); i++) {
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

		}

	}

}
