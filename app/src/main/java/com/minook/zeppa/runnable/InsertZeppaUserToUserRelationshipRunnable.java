package com.minook.zeppa.runnable;

import com.appspot.zeppa_cloud_1821.zeppaclientapi.Zeppaclientapi;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaUserToUserRelationship;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ApiClientHelper;
import com.minook.zeppa.ZeppaApplication;

import java.io.IOException;

public class InsertZeppaUserToUserRelationshipRunnable extends BaseRunnable {

	private ZeppaUserToUserRelationship relationship;
	
	public InsertZeppaUserToUserRelationshipRunnable(
			ZeppaApplication application, GoogleAccountCredential credential, ZeppaUserToUserRelationship relationship) {
		super(application, credential);
		// TODO Auto-generated constructor stub
		this.relationship = relationship;
	}

	@Override
	public void run() {

		ApiClientHelper helper = new ApiClientHelper();
		Zeppaclientapi api = helper.buildClientEndpoint();

		try {
			api.insertZeppaUserToUserRelationship(credential.getToken(), relationship).execute();
			
		} catch (IOException | GoogleAuthException e){
			e.printStackTrace();
		}
		
	}

}
