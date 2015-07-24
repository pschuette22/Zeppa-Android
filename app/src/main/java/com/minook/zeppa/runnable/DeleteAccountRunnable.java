package com.minook.zeppa.runnable;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ZeppaApplication;

import java.io.IOException;

public class DeleteAccountRunnable extends BaseRunnable {

	private long userId;
	
	public DeleteAccountRunnable(ZeppaApplication application,
			GoogleAccountCredential credential, long userId) {
		super(application, credential);
		this.userId = userId;
	}

	@Override
	public void run() {
		try {
			buildZeppaUserEndpoint().removeZeppaUser(userId).execute();
			
		} catch (IOException e){
			e.printStackTrace();
		}
		
	}
	
	

}
