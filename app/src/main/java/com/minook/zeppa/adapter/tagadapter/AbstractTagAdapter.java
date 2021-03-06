package com.minook.zeppa.adapter.tagadapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.minook.zeppa.R;
import com.minook.zeppa.Utils;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.mediator.AbstractEventTagMediator;

import java.util.List;

public abstract class AbstractTagAdapter extends BaseAdapter {

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

	public void setTagHolder(LinearLayout tagHolder) {
		this.tagHolder = tagHolder;
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

	protected abstract boolean didLoadTags();

	@SuppressLint("InflateParams")
	public void drawTags() {

		tagHolder.removeAllViews();

		if (didLoadTags()) {

			if (getCount() > 0) {

				LayoutInflater inflater = activity.getLayoutInflater();
				LinearLayout currentLine = (LinearLayout) inflater.inflate(
						R.layout.view_tag_line, null, false);

				tagHolder.addView(currentLine);
				currentLine.measure(MeasureSpec.UNSPECIFIED,
						MeasureSpec.EXACTLY);
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

		} else {

			View loaderView = Utils.makeLoaderView(activity, "Loading Tags...");
			tagHolder.addView(loaderView);
			
		}

	}
}
