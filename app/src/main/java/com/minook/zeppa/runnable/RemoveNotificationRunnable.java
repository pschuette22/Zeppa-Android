package com.minook.zeppa.runnable;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.singleton.NotificationSingleton;

import java.io.IOException;

public class RemoveNotificationRunnable extends BaseRunnable {

	private long notificationId;
	
	public RemoveNotificationRunnable(ZeppaApplication application,
			GoogleAccountCredential credential, long notificationId) {
		super(application, credential);
		this.notificationId = notificationId;
	}

	@Override
	public void run() {
		try {
			buildNotificationEndpoint().removeZeppaNotification(notificationId).execute();
			application.getCurrentActivity().runOnUiThread(new Runnable(){

				@Override
				public void run() {
					NotificationSingleton.getInstance().removeNotification(notificationId);
					NotificationSingleton.getInstance().notifyObservers();
				}
				
			});
			
		} catch (IOException e ){
			e.printStackTrace();
		} catch (NullPointerException e){
			e.printStackTrace();
		}
		
	}

}
