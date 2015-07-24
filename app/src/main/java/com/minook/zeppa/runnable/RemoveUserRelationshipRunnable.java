package com.minook.zeppa.runnable;

import com.appspot.zeppa_cloud_1821.zeppausertouserrelationshipendpoint.Zeppausertouserrelationshipendpoint;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ZeppaApplication;

import java.io.IOException;

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
