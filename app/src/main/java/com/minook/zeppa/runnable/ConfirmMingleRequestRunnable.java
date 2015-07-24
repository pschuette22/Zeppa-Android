package com.minook.zeppa.runnable;

import com.appspot.zeppa_cloud_1821.zeppausertouserrelationshipendpoint.Zeppausertouserrelationshipendpoint;
import com.appspot.zeppa_cloud_1821.zeppausertouserrelationshipendpoint.model.ZeppaUserToUserRelationship;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.mediator.DefaultUserInfoMediator;


import java.io.IOException;

public class ConfirmMingleRequestRunnable extends BaseRunnable {

	protected DefaultUserInfoMediator dMediator;

	public ConfirmMingleRequestRunnable(ZeppaApplication application,
			GoogleAccountCredential credential, DefaultUserInfoMediator mediator) {
		super(application, credential);
		this.dMediator = mediator;
	}

	@Override
	public void run() {


		Zeppausertouserrelationshipendpoint endpoint = buildZeppaUserToUserRelationshipEndpoint();

		ZeppaUserToUserRelationship relationship = dMediator
				.getUserRelationship();
		relationship.setRelationshipType("MINGLING");

		try {
			relationship = endpoint.updateZeppaUserToUserRelationship(
					relationship).execute();
			if (relationship != null) {
				dMediator.setUserRelationship(relationship);
				notifyUserObservers();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}


}
