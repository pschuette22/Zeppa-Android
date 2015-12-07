package com.minook.zeppa.runnable;


import com.appspot.zeppa_cloud_1821.zeppaclientapi.Zeppaclientapi;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaNotification;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ApiClientHelper;
import com.minook.zeppa.ZeppaApplication;

import java.io.IOException;

public class UpdateNotificationRunnable extends BaseRunnable {

	private ZeppaNotification notification;
	
	public UpdateNotificationRunnable(ZeppaApplication application,
			GoogleAccountCredential credential, ZeppaNotification notification) {
		super(application, credential);
		// TODO Auto-generated constructor stub
		this.notification = notification;
	}

	@Override
	public void run() {

		ApiClientHelper helper = new ApiClientHelper();
		Zeppaclientapi api = helper.buildClientEndpoint();
		
		try {
			api.updateZeppaNotification(credential.getToken(), notification).execute();
		} catch (IOException | GoogleAuthException e) {
			e.printStackTrace();
		}
		
		
	}

}
