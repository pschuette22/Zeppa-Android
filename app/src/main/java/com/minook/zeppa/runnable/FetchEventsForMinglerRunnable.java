package com.minook.zeppa.runnable;


import com.appspot.zeppa_cloud_1821.zeppaclientapi.Zeppaclientapi;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.CollectionResponseZeppaEventToUserRelationship;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaEvent;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaEventToUserRelationship;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ApiClientHelper;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.mediator.DefaultZeppaEventMediator;
import com.minook.zeppa.singleton.ZeppaEventSingleton;

import java.io.IOException;
import java.util.Iterator;

public class FetchEventsForMinglerRunnable extends BaseRunnable {

	private long userId;
	private long minglerId;

	public FetchEventsForMinglerRunnable(ZeppaApplication application,
			GoogleAccountCredential credential, long userId, long minglerId) {
		super(application, credential);
		this.userId = userId;
		this.minglerId = minglerId;
	}

	@Override
	public void run() {
		String filter = "eventHostId == " + minglerId + " && userId == "
				+ userId + " && expires > " + System.currentTimeMillis();
		String cursor = null;
		Integer limit = Integer.valueOf(25);
		String ordering = "expires desc";

		ApiClientHelper helper = new ApiClientHelper();
		Zeppaclientapi api = helper.buildClientEndpoint();

		do {
			try {
				Zeppaclientapi.ListZeppaEventToUserRelationship task = api
						.listZeppaEventToUserRelationship(credential.getToken());
				task.setFilter(filter);
				task.setCursor(cursor);
				task.setLimit(limit);
				task.setOrdering(ordering);

				CollectionResponseZeppaEventToUserRelationship response = task
						.execute();

				if (response == null || response.getItems() == null
						|| response.getItems().isEmpty()) {
					cursor = null;
				} else {

					Iterator<ZeppaEventToUserRelationship> iterator = response
							.getItems().iterator();
					while (iterator.hasNext()) {
						ZeppaEventToUserRelationship relationship = iterator
								.next();
						if (!ZeppaEventSingleton.getInstance()
								.relationshipAlreadyHeld(relationship)) {
							try {
								ZeppaEvent event = api
										.getZeppaEvent(
												relationship.getEventId(), credential.getToken())
										.execute();
								ZeppaEventSingleton.getInstance().addMediator(
										new DefaultZeppaEventMediator(event,
												relationship));

							} catch (IOException e) {
								e.printStackTrace();
							}
						}

					}

					if (response.getItems().size() < limit.intValue()) {
						cursor = null;
					} else {
						cursor = response.getNextPageToken();
					}

				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (GoogleAuthException ex) {
				ex.printStackTrace();
				break;
			}
		} while (cursor != null);

		try {
			application.getCurrentActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					ZeppaEventSingleton.getInstance().notifyObservers();
				}

			});
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

	}

}
