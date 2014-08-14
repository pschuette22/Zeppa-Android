/**
 * 
 */
package com.minook.zeppa.mediator;

import android.view.View;
import android.widget.CheckedTextView;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.R;
import com.minook.zeppa.eventtagendpoint.model.EventTag;

/**
 * @author DrunkWithFunk21
 *
 */
public class MyEventTagMediator extends AbstractEventTagMediator {

	
	
	/**
	 * @param eventTag
	 */
	
	public MyEventTagMediator(EventTag eventTag, GoogleAccountCredential credential) {
		super(eventTag, credential);
		// TODO Auto-generated constructor stub
	}

	
	public void delete(){
		
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void convertView(View convertView) {
		CheckedTextView checkedText = (CheckedTextView) convertView.findViewById(R.id.tagview_mytagtext);
		checkedText.setText(eventTag.getTagText());

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
	
	
	

}
