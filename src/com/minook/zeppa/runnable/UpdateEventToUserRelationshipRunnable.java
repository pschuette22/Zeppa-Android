package com.minook.zeppa.runnable;

import java.io.IOException;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.model.ZeppaEventToUserRelationship;

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
