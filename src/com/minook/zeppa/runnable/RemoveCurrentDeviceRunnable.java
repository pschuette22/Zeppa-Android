package com.minook.zeppa.runnable;

import java.io.IOException;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ZeppaApplication;

public class RemoveCurrentDeviceRunnable extends BaseRunnable {

	public RemoveCurrentDeviceRunnable(ZeppaApplication application,
			GoogleAccountCredential credential) {
		super(application, credential);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		try {
			
			buildDeviceInfoEndpoint().removeDeviceInfo(application.getCurrentDeviceInfo()).execute();
			application.setCurrentDeviceInfo(null);
			
		} catch (IOException e){
			e.printStackTrace();
		} catch (NullPointerException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}

}
