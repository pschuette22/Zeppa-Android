package com.minook.zeppa.task;

import java.io.IOException;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.mediator.DefaultUserInfoMediator;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppanotificationendpoint.model.ZeppaNotification;
import com.minook.zeppa.zeppausertouserrelationshipendpoint.Zeppausertouserrelationshipendpoint;
import com.minook.zeppa.zeppausertouserrelationshipendpoint.model.ZeppaUserToUserRelationship;

public class ConfirmMingleRequestTask extends ZeppaEndpointTask {

	protected DefaultUserInfoMediator dMediator;

	public ConfirmMingleRequestTask(GoogleAccountCredential credential,
			DefaultUserInfoMediator dMediator) {
		super(credential);

		this.dMediator = dMediator;

	}

	@Override
	protected Boolean doInBackground(Void... params) {

		Boolean success = Boolean.FALSE;
		Zeppausertouserrelationshipendpoint.Builder builder = new Zeppausertouserrelationshipendpoint.Builder(
				transport, factory, credential);
		builder = CloudEndpointUtils.updateBuilder(builder);
		Zeppausertouserrelationshipendpoint endpoint = builder.build();

		ZeppaUserToUserRelationship relationship = dMediator
				.getUserRelationship();
		relationship.setRelationshipType("MINGLING");

		try {
			relationship = endpoint.updateZeppaUserToUserRelationship(
					relationship).execute();
			if(relationship == null){
				return Boolean.FALSE;
			}
			
			dMediator.setUserRelationship(relationship);
			success = Boolean.TRUE;
			

		} catch (IOException e) {
			e.printStackTrace();
		}

		return success;
	}

	@Override
	protected void onPostExecute(Boolean success) {
		super.onPostExecute(success);

		if (success) {
			// Notify the data has changed for
		}

	}


}
