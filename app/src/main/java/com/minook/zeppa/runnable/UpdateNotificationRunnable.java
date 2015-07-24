package com.minook.zeppa.runnable;

import com.appspot.zeppa_cloud_1821.zeppanotificationendpoint.Zeppanotificationendpoint;
import com.appspot.zeppa_cloud_1821.zeppanotificationendpoint.model.ZeppaNotification;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
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
		
		Zeppanotificationendpoint endpoint = buildNotificationEndpoint();
		
		try {
			endpoint.updateZeppaNotification(notification).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

}
