package com.minook.zeppa.task;

import java.io.IOException;

import android.widget.CheckBox;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.mediator.DefaultUserInfoMediator;
import com.minook.zeppa.mediator.MyZeppaUserMediator;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppanotificationendpoint.model.ZeppaNotification;
import com.minook.zeppa.zeppausertouserrelationshipendpoint.Zeppausertouserrelationshipendpoint;
import com.minook.zeppa.zeppausertouserrelationshipendpoint.model.ZeppaUserToUserRelationship;

public class RequestMingleTask extends NotifyUserTask {

	protected MyZeppaUserMediator mMediator;
	protected DefaultUserInfoMediator dMediator;
	protected CheckBox callbackListener;

	public RequestMingleTask(GoogleAccountCredential credential,
			CheckBox callbackListener, MyZeppaUserMediator mMediator,
			DefaultUserInfoMediator dMediator) {
		super(credential);
		// TODO Auto-generated constructor stub
		this.mMediator = mMediator;
		this.dMediator = dMediator;
		this.callbackListener = callbackListener;

	}

	@Override
	protected Boolean doInBackground(Void... params) {

		Boolean success = Boolean.FALSE;
		Zeppausertouserrelationshipendpoint.Builder builder = new Zeppausertouserrelationshipendpoint.Builder(
				transport, factory, credential);

		builder = CloudEndpointUtils.updateBuilder(builder);
		Zeppausertouserrelationshipendpoint endpoint = builder.build();

		ZeppaUserToUserRelationship relationship = createRelationship();

		try {

			relationship = endpoint.insertZeppaUserToUserRelationship(
					relationship).execute();
			dMediator.setUserRelationship(relationship);
			success = Boolean.TRUE;

			// Just in case there was concurrent requests, make sure the
			// relationship type is pending
			if (relationship.getRelationshipType().toString()
					.equals("PENDING_REQUEST")) {
				// create and send notification
				ZeppaNotification notification = createNotification(relationship);
				insertNotificationObject(notification);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return success;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

		try {
			if (!result) {
				callbackListener.setChecked(false);
			}

			callbackListener.setClickable(true);

		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}
	}

	private ZeppaUserToUserRelationship createRelationship() {
		ZeppaUserToUserRelationship relationship = new ZeppaUserToUserRelationship();

		relationship.setCreatorId(mMediator.getUserId());
		relationship.setSubjectId(dMediator.getUserId());
		relationship.setRelationshipType("PENDING_REQUEST");

		return relationship;
	}

	private ZeppaNotification createNotification(
			ZeppaUserToUserRelationship relationship) {
		ZeppaNotification notification = new ZeppaNotification();
		StringBuilder message = new StringBuilder();
		message.append(mMediator.getDisplayName());
		message.append(" would like to mingle!");

		notification.setRecipientId(dMediator.getUserId());
		notification.setSenderId(ZeppaUserSingleton.getInstance().getUserId());
		notification.setExtraMessage(message.toString());
		notification.setExpires(null);
		notification.setHasSeen(Boolean.FALSE);
		notification.setType("MINGLE_REQUEST");

		return notification;
	}

}
