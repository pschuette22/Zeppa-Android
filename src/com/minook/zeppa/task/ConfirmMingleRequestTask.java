package com.minook.zeppa.task;

import java.io.IOException;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.mediator.DefaultUserInfoMediator;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppanotificationendpoint.model.ZeppaNotification;
import com.minook.zeppa.zeppanotificationendpoint.model.ZeppaUser;
import com.minook.zeppa.zeppausertouserrelationshipendpoint.Zeppausertouserrelationshipendpoint;
import com.minook.zeppa.zeppausertouserrelationshipendpoint.model.ZeppaUserToUserRelationship;

public class ConfirmMingleRequestTask extends NotifyUserTask {

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
			dMediator.setUserRelationship(relationship);
			success = Boolean.TRUE;

			ZeppaNotification notification = buildNotification();
			insertNotificationObject(notification);

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

	private ZeppaNotification buildNotification() {
		ZeppaNotification notification = new ZeppaNotification();

		notification.setExtraMessage("You're mingling with "
				+ ZeppaUserSingleton.getInstance().getUserMediator()
						.getDisplayName());

		notification.setType("MINGLE_ACCEPTED");
		
		notification.setHasSeen(Boolean.FALSE);
		notification.setRecipientId(dMediator.getUserId());

		ZeppaUser sender = new ZeppaUser();
		sender.setId(ZeppaUserSingleton.getInstance().getUserId());
		notification.setSender(sender);

		return notification;
	}

}
