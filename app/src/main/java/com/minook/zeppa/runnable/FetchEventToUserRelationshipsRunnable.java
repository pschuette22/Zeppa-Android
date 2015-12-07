package com.minook.zeppa.runnable;


import com.appspot.zeppa_cloud_1821.zeppaclientapi.Zeppaclientapi;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.CollectionResponseZeppaEventToUserRelationship;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaEventToUserRelationship;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaUserInfo;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ApiClientHelper;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator.OnRelationshipsLoadedListener;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author  Pete Schuette
 *
 * This runnable is for retrieving all relationships for a given event *EXCLUDING* the current user
 * We do this because we assume the application already holds this object
 *
 */
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

		ApiClientHelper helper = new ApiClientHelper();
		Zeppaclientapi api = helper.buildClientEndpoint();

		String filter = "eventId == " + eventId + " && userId != " + userId;
		String cursor = null;
		Integer limit = Integer.valueOf(50);

		loadedRelationships = new ArrayList<ZeppaEventToUserRelationship>();
		do {
			try {
				Zeppaclientapi.ListZeppaEventToUserRelationship listRelationshipsTask = api
						.listZeppaEventToUserRelationship(credential.getToken());
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
			} catch (GoogleAuthException ex) {
				ex.printStackTrace();
				cursor = null;
				break;
			}

		} while (cursor != null);

		if (!loadedRelationships.isEmpty()) {

			Iterator<ZeppaEventToUserRelationship> iterator = loadedRelationships
					.iterator();

			List<ZeppaEventToUserRelationship> remove = new ArrayList<ZeppaEventToUserRelationship>();
			while (iterator.hasNext()) {
				ZeppaEventToUserRelationship relationship = iterator.next();
				if (ZeppaUserSingleton.getInstance()
						.getAbstractUserMediatorById(relationship.getUserId()) == null) {
					try {
						ZeppaUserInfo info = api.fetchZeppaUserInfoByParentId(
								relationship.getUserId(), credential.getToken()).execute();

						ZeppaUserSingleton.getInstance()
								.addDefaultZeppaUserMediator(info, null);

					} catch (IOException e) {
						remove.add(relationship);
						e.printStackTrace();
					} catch (GoogleAuthException ex) {
						ex.printStackTrace();
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
