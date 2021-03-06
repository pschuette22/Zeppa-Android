package com.minook.zeppa.runnable;

import com.appspot.zeppa_cloud_1821.zeppaclientapi.Zeppaclientapi;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaEventToUserRelationship;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ApiClientHelper;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator.OnRelationshipsLoadedListener;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class SendEventInvitesRunnable extends BaseRunnable {

	private List<ZeppaEventToUserRelationship> insertRelationships,
			updateEventRelationships;
	private long eventId, userId;
	private OnRelationshipsLoadedListener listener;

	public SendEventInvitesRunnable(ZeppaApplication application,
			GoogleAccountCredential credential, long eventId, long userId,
			OnRelationshipsLoadedListener listener,
			List<ZeppaEventToUserRelationship> insertRelationships,
			List<ZeppaEventToUserRelationship> updateEventRelationships) {
		super(application, credential);
		this.insertRelationships = insertRelationships;
		this.updateEventRelationships = updateEventRelationships;
		this.eventId = eventId;
		this.userId = userId;
		this.listener = listener;
	}

	@Override
	public void run() {
		// Insert new relationships, invited by you

		ApiClientHelper helper = new ApiClientHelper();
		Zeppaclientapi api = helper.buildClientEndpoint();

		if (insertRelationships != null) {
			Iterator<ZeppaEventToUserRelationship> iterator = insertRelationships
					.iterator();
			while (iterator.hasNext()) {
				ZeppaEventToUserRelationship relationship = iterator.next();
				try {
					api
							.insertZeppaEventToUserRelationship(credential.getToken(), relationship)
							.execute();
				} catch (IOException | GoogleAuthException e) {
					e.printStackTrace();
				}

			}
		}

		if (updateEventRelationships != null) {
			// Update existing relationships with your invite
			Iterator<ZeppaEventToUserRelationship> iterator2 = updateEventRelationships
					.iterator();
			while (iterator2.hasNext()) {
				ZeppaEventToUserRelationship relationship = iterator2.next();
				try {
					api
							.updateZeppaEventToUserRelationship(credential.getToken(), relationship)
							.execute();
				} catch (IOException | GoogleAuthException e) {
					e.printStackTrace();
				}

			}
		}

		// Execute thread to update event relationships
		ThreadManager.execute(new FetchEventToUserRelationshipsRunnable(
				application, credential, eventId, userId, listener));

	}

}
