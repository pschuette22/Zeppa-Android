package com.minook.zeppa.runnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator.OnRelationshipsLoadedListener;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.Zeppaeventtouserrelationshipendpoint;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.Zeppaeventtouserrelationshipendpoint.ListZeppaEventToUserRelationship;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.model.CollectionResponseZeppaEventToUserRelationship;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.model.ZeppaEventToUserRelationship;
import com.minook.zeppa.zeppauserinfoendpoint.Zeppauserinfoendpoint;
import com.minook.zeppa.zeppauserinfoendpoint.model.ZeppaUserInfo;

public class FetchEventToUserRelationshipsRunnable extends BaseRunnable {

	private long eventId;
	private long userId;
	private OnRelationshipsLoadedListener listener;
	private List<ZeppaEventToUserRelationship> loadedRelationships;

	public FetchEventToUserRelationshipsRunnable(ZeppaApplication application,
			GoogleAccountCredential credential, long eventId, long userId,
			OnRelationshipsLoadedListener listener) {
		super(application, credential);
		this.eventId = eventId;
		this.userId = userId;
		this.listener = listener;
	}

	@Override
	public void run() {

		Zeppaeventtouserrelationshipendpoint endpoint = buildEventRelationshipEndpoint();

		String filter = "eventId == " + eventId + " && userId != " + userId;
		String cursor = null;
		Integer limit = Integer.valueOf(30);

		loadedRelationships = new ArrayList<ZeppaEventToUserRelationship>();
		do {
			try {
				ListZeppaEventToUserRelationship listRelationshipsTask = endpoint
						.listZeppaEventToUserRelationship();
				listRelationshipsTask.setFilter(filter);
				listRelationshipsTask.setCursor(cursor);
				listRelationshipsTask.setLimit(limit);

				CollectionResponseZeppaEventToUserRelationship response = listRelationshipsTask
						.execute();

				if (response != null && response.getItems() != null
						&& !response.getItems().isEmpty()) {
					loadedRelationships.addAll(response.getItems());
					cursor = response.getNextPageToken();

				} else {
					cursor = null;
					break;
				}

			} catch (IOException e) {
				e.printStackTrace();
				cursor = null;
				break;
			}

		} while (cursor != null);

		if (!loadedRelationships.isEmpty()) {
			Zeppauserinfoendpoint iEndpoint = buildUserInfoEndpoint();
			Iterator<ZeppaEventToUserRelationship> iterator = loadedRelationships
					.iterator();

			List<ZeppaEventToUserRelationship> remove = new ArrayList<ZeppaEventToUserRelationship>();
			while (iterator.hasNext()) {
				ZeppaEventToUserRelationship relationship = iterator.next();
				if (ZeppaUserSingleton.getInstance()
						.getAbstractUserMediatorById(relationship.getUserId()) == null) {
					try {
						ZeppaUserInfo info = iEndpoint.fetchZeppaUserInfoByParentId(
								relationship.getUserId()).execute();

						ZeppaUserSingleton.getInstance()
								.addDefaultZeppaUserMediator(info, null);

					} catch (IOException e) {
						remove.add(relationship);
						e.printStackTrace();
					}
				}

			}

			loadedRelationships.removeAll(remove);

		}

		application.getCurrentActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				try {
					ZeppaEventSingleton.getInstance().getEventById(eventId)
							.setEventRelationships(loadedRelationships);
					listener.onRelationshipsLoaded();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});

	}

}
