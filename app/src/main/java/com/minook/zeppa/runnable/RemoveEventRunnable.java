package com.minook.zeppa.runnable;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ZeppaApplication;

import java.io.IOException;

public class RemoveEventRunnable extends BaseRunnable{

	private long eventId;
	
	public RemoveEventRunnable(ZeppaApplication application,
			GoogleAccountCredential credential, long eventId) {
		super(application, credential);
		this.eventId = eventId;
	}

	@Override
	public void run() {
		try {
		buildEventEndpoint().removeZeppaEvent(eventId).execute();
		} catch (IOException e){
			e.printStackTrace();
		}
		
	}
	
	

}
