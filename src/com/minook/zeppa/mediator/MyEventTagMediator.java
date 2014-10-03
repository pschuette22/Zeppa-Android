/**
 * 
 */
package com.minook.zeppa.mediator;

import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.TextView;

import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.eventtagendpoint.model.EventTag;

/**
 * @author DrunkWithFunk21
 * 
 */
public class MyEventTagMediator extends AbstractEventTagMediator implements
		OnLongClickListener {


	/**
	 * @param eventTag
	 */
	public MyEventTagMediator(EventTag eventTag) {
		super(eventTag);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onLongClick(View v) {


		return true;
	}

	@Override
	public void convertView(AuthenticatedFragmentActivity context, View convertView) {
		setContext(context);
		TextView tagText = (TextView) convertView
				.findViewById(R.id.tagview_mytagtext);
		tagText.setText(eventTag.getTagText());
		convertView.setOnLongClickListener(this);

	}

	@Override
	public boolean onMemoryWarning() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onMemoryLow() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onMemoryCritical() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onApplicationTerminate() {
		// TODO Auto-generated method stub
		return false;
	}

	public void deleteTag(Long tagId) {

	}

	private void enterDeleteState(View v) {

	}

	private void leaveDeleteState(View v) {

	}

}
