package com.minook.zeppa.runnable;

import com.appspot.zeppa_cloud_1821.zeppaeventtouserrelationshipendpoint.model.ZeppaEventToUserRelationship;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
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

		try {
			buildEventRelationshipEndpoint()
					.updateZeppaEventToUserRelationship(relationship).execute();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
