package com.minook.zeppa.runnable;

import com.appspot.zeppa_cloud_1821.zeppaclientapi.Zeppaclientapi;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ApiClientHelper;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

import java.io.IOException;

public class RemoveUserRelationshipRunnable extends BaseRunnable {

	private long relationshipId;
	private long callingUserId;

	public RemoveUserRelationshipRunnable(ZeppaApplication application,
			GoogleAccountCredential credential,
			long relationshipId) {
		super(application, credential);
		this.relationshipId = relationshipId;
		this.callingUserId = ZeppaUserSingleton.getInstance().getUserId().longValue();
	}

	@Override
	public void run() {

		ApiClientHelper helper = new ApiClientHelper();
		Zeppaclientapi api = helper.buildClientEndpoint();

		try {
			api.removeZeppaUserToUserRelationship(relationshipId,credential.getToken())
					.execute();

		} catch (IOException | GoogleAuthException e) {
			e.printStackTrace();
		}
		
	}

}
