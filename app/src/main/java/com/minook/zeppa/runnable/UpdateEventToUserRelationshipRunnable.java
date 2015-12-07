package com.minook.zeppa.runnable;

import com.appspot.zeppa_cloud_1821.zeppaclientapi.Zeppaclientapi;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaEventToUserRelationship;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ApiClientHelper;
import com.minook.zeppa.ZeppaApplication;

import java.io.IOException;

public class UpdateEventToUserRelationshipRunnable extends BaseRunnable {

	private ZeppaEventToUserRelationship relationship;

	public UpdateEventToUserRelationshipRunnable(ZeppaApplication application,
			GoogleAccountCredential credential,
			ZeppaEventToUserRelationship relationship) {
		super(application, credential);

		this.relationship = relationship;

	}

	@Override
	public void run() {

		ApiClientHelper helper = new ApiClientHelper();
		Zeppaclientapi api = helper.buildClientEndpoint();

		try {
			api
					.updateZeppaEventToUserRelationship(credential.getToken(), relationship).execute();
			
		} catch (IOException | GoogleAuthException e) {
			e.printStackTrace();
		}

	}
}
