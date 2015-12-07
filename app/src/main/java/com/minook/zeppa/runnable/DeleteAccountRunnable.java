package com.minook.zeppa.runnable;

import com.appspot.zeppa_cloud_1821.zeppaclientapi.Zeppaclientapi;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ApiClientHelper;
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
			ApiClientHelper helper = new ApiClientHelper();
			Zeppaclientapi api = helper.buildClientEndpoint();

			api.removeCurrentZeppaUser(credential.getToken()).execute();
			
		} catch (IOException e){
			e.printStackTrace();
		} catch (GoogleAuthException ex) {
			ex.printStackTrace();
		}
		
	}
	
	

}
