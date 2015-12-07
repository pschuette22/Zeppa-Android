package com.minook.zeppa.mediator;

import android.view.View;

import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.EventTag;


public abstract class AbstractEventTagMediator {

	public static final int TAG = 400;
	protected EventTag eventTag;
	
	public AbstractEventTagMediator(EventTag eventTag){
		this.eventTag = eventTag;
	}
	

	public Long getUserId(){
		return eventTag.getOwnerId();
	}
	
	public String getText(){
		return eventTag.getTagText();
	}
	
	public View convertView(View convertView){
		convertView.setTag(this);
		return convertView;
	}
	
	
	public Long getTagId(){
		return eventTag.getId();
	}
	
	
}
