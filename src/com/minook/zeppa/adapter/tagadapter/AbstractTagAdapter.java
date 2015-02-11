package com.minook.zeppa.adapter.tagadapter;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.mediator.AbstractEventTagMediator;
import com.minook.zeppa.observer.OnLoadListener;

public abstract class AbstractTagAdapter extends BaseAdapter implements
		OnLoadListener {

	protected AuthenticatedFragmentActivity activity;
	protected LinearLayout tagHolder;
	protected boolean hasLoadedTags;
	protected List<AbstractEventTagMediator> tagMediators;

	public AbstractTagAdapter(AuthenticatedFragmentActivity activity,
			LinearLayout tagHolder) {

		this.activity = activity;
		this.tagHolder = tagHolder;
		this.tagHolder.setClickable(false);

	}

	@Override
	public void notifyDataSetChanged() {
		tagMediators = getCurrentTagMediators();
		super.notifyDataSetChanged();
	}

	public void verifyDatasetValid() {
		List<AbstractEventTagMediator> mediators = getCurrentTagMediators();

		if (mediators.containsAll(tagMediators)
				&& tagMediators.containsAll(mediators)) {
			// Dataset did not change
		} else {
			notifyDataSetChanged();
		}

	}

	public abstract List<AbstractEventTagMediator> getCurrentTagMediators();

	@Override
	public AbstractEventTagMediator getItem(int position) {
		if (tagMediators.isEmpty())
			return null;

		return tagMediators.get(position);
	}

	@Override
	public int getCount() {
		if (tagMediators == null)
			return 0;

		return tagMediators.size();
	}

	public void drawTags() {

		tagHolder.removeAllViews();

		if (getCount() > 0) {

			LayoutInflater inflater = activity.getLayoutInflater();
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
