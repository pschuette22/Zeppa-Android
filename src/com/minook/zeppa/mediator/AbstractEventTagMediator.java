package com.minook.zeppa.mediator;

import android.view.View;
import android.view.View.OnClickListener;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.eventtagendpoint.model.EventTag;
import com.minook.zeppa.observer.MemoryObserver;

public abstract class AbstractEventTagMediator implements OnClickListener, MemoryObserver{

	protected EventTag eventTag;
	protected GoogleAccountCredential credential;
	
	public AbstractEventTagMediator(EventTag eventTag, GoogleAccountCredential credential){
		this.eventTag = eventTag;
		this.credential = credential;
	}
	
	public String getText(){
		return eventTag.getTagText();
	}
	
	public abstract void convertView(View convertView);
	
	
	public Long getTagId(){
		return eventTag.getKey().getId();
	}
	
	
}
