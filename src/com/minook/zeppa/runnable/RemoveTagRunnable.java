package com.minook.zeppa.runnable;

import java.io.IOException;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ZeppaApplication;

public class RemoveTagRunnable extends BaseRunnable {

	private long tagId;
	
	public RemoveTagRunnable(ZeppaApplication application,
			GoogleAccountCredential credential, long tagId) {
		super(application, credential);
		this.tagId = tagId;
	}

	@Override
	public void run() {
		
		try {
			buildEventTagEndpoint().removeEventTag(tagId).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	

}