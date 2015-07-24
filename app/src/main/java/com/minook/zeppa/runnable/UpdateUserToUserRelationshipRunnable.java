package com.minook.zeppa.runnable;

import com.appspot.zeppa_cloud_1821.zeppausertouserrelationshipendpoint.model.ZeppaUserToUserRelationship;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ZeppaApplication;

import java.io.IOException;

public class UpdateUserToUserRelationshipRunnable extends BaseRunnable {

	private ZeppaUserToUserRelationship relationship;
	
	public UpdateUserToUserRelationshipRunnable(ZeppaApplication application,
			GoogleAccountCredential credential, ZeppaUserToUserRelationship relationship) {
		super(application, credential);
		this.relationship = relationship;
		
	}

	@Override
	public void run() {
		try {
			buildZeppaUserToUserRelationshipEndpoint().updateZeppaUserToUserRelationship(relationship).execute();
			
		} catch (IOException e){ 
			e.printStackTrace();
		}
		
	}

}
