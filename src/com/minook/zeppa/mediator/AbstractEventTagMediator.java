package com.minook.zeppa.mediator;

import android.view.View;
import android.view.View.OnClickListener;

import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.eventtagendpoint.model.EventTag;
import com.minook.zeppa.observer.MemoryObserver;

public abstract class AbstractEventTagMediator extends AbstractMediator implements OnClickListener, MemoryObserver{

	
	protected EventTag eventTag;
	
	public AbstractEventTagMediator(EventTag eventTag){
		this.eventTag = eventTag;
	}
	

	public Long getUserId(){
		return eventTag.getUserId();
	}
	
	public String getText(){
		return eventTag.getTagText();
	}
	
	public void convertView(AuthenticatedFragmentActivity context, View convertView){
		super.convertView(context);
	}
	
	
	public Long getTagId(){
		return eventTag.getKey().getId();
	}
	
	
}
