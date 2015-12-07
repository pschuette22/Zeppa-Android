package com.minook.zeppa.runnable;

import com.appspot.zeppa_cloud_1821.zeppaclientapi.Zeppaclientapi;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ApiClientHelper;
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
		ApiClientHelper helper = new ApiClientHelper();
		Zeppaclientapi api = helper.buildClientEndpoint();

		try {
		api.removeZeppaEvent(eventId, credential.getToken()).execute();
		} catch (IOException | GoogleAuthException e){
			e.printStackTrace();
		}
		
	}
	
	

}
