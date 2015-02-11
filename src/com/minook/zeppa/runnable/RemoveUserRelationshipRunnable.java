package com.minook.zeppa.runnable;

import java.io.IOException;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.zeppausertouserrelationshipendpoint.Zeppausertouserrelationshipendpoint;

public class RemoveUserRelationshipRunnable extends BaseRunnable {

	private long relationshipId;

	public RemoveUserRelationshipRunnable(ZeppaApplication application,
			GoogleAccountCredential credential,
			long relationshipId) {
		super(application, credential);
		this.relationshipId = relationshipId;
	}

	@Override
	public void run() {

		Zeppausertouserrelationshipendpoint endpoint = buildZeppaUserToUserRelationshipEndpoint();

		try {
			endpoint.removeZeppaUserToUserRelationship(relationshipId)
					.execute();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
