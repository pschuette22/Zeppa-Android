package com.minook.zeppa.runnable;

import com.appspot.zeppa_cloud_1821.zeppaclientapi.Zeppaclientapi;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ApiClientHelper;
import com.minook.zeppa.ZeppaApplication;

import java.io.IOException;

public class RemoveTagRunnable extends BaseRunnable {

	private long tagId;
	
	public RemoveTagRunnable(ZeppaApplication application,
			GoogleAccountCredential credential, long tagId) {
		super(application, credential);
		this.tagId = tagId;
	}

	@Override
	public void run() {

		ApiClientHelper helper = new ApiClientHelper();
		Zeppaclientapi api = helper.buildClientEndpoint();

		try {
			api.removeEventTag(tagId, credential.getToken()).execute();
		} catch (IOException | GoogleAuthException e) {
			e.printStackTrace();
		}
		
	}
	
	

}
