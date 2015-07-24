package com.minook.zeppa.runnable;

import com.appspot.zeppa_cloud_1821.zeppausertouserrelationshipendpoint.model.ZeppaUserToUserRelationship;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
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
		try {
			buildZeppaUserToUserRelationshipEndpoint().insertZeppaUserToUserRelationship(relationship).execute();
			
		} catch (IOException e){
			e.printStackTrace();
		}
		
	}

}
