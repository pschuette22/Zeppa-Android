package com.minook.zeppa.runnable;

import java.io.IOException;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.mediator.DefaultUserInfoMediator;
import com.minook.zeppa.zeppausertouserrelationshipendpoint.Zeppausertouserrelationshipendpoint;
import com.minook.zeppa.zeppausertouserrelationshipendpoint.model.ZeppaUserToUserRelationship;

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
