package com.minook.zeppa.task;

import java.io.IOException;

import android.widget.CheckBox;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.mediator.DefaultUserInfoMediator;
import com.minook.zeppa.mediator.MyZeppaUserMediator;
import com.minook.zeppa.utils.GCalUtils;
import com.minook.zeppa.zeppausertouserrelationshipendpoint.Zeppausertouserrelationshipendpoint;
import com.minook.zeppa.zeppausertouserrelationshipendpoint.model.ZeppaUserToUserRelationship;

public class RequestMingleTask extends ZeppaEndpointTask {

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


}
