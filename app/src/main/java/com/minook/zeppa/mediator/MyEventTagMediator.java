/**
 * 
 */
package com.minook.zeppa.mediator;

import android.view.View;
import android.widget.TextView;

import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.EventTag;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.R;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.runnable.RemoveTagRunnable;
import com.minook.zeppa.runnable.ThreadManager;
import com.minook.zeppa.singleton.EventTagSingleton;

/**
 * @author DrunkWithFunk21
 * 
 */
public class MyEventTagMediator extends AbstractEventTagMediator {


	/**
	 * @param eventTag
	 */
	public MyEventTagMediator(EventTag eventTag) {
		super(eventTag);
	}
	
	@Override
	public View convertView(View convertView) {
		convertView = super.convertView(convertView);
		TextView tagText = (TextView) convertView
				.findViewById(R.id.tagview_mytagtext);
		tagText.setText(eventTag.getTagText());
		return convertView;
	}


	public void delete(ZeppaApplication application, GoogleAccountCredential credential){
		ThreadManager.execute(new RemoveTagRunnable(application, credential, getTagId()));
		EventTagSingleton.getInstance().removeEventTagMediator(this);
	}


}
