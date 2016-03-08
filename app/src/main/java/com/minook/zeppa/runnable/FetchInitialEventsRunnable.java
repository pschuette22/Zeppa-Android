package com.minook.zeppa.runnable;


import com.appspot.zeppa_cloud_1821.zeppaclientapi.Zeppaclientapi;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.CollectionResponseZeppaEvent;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.CollectionResponseZeppaEventToUserRelationship;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaEvent;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaEventToUserRelationship;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaUserInfo;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ApiClientHelper;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.mediator.DefaultZeppaEventMediator;
import com.minook.zeppa.mediator.MyZeppaEventMediator;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

import java.io.IOException;
import java.util.Iterator;

public class FetchInitialEventsRunnable extends BaseRunnable {

	private Long userId;

	public FetchInitialEventsRunnable(ZeppaApplication application,
			GoogleAccountCredential credential, Long userId) {
		super(application, credential);
		this.userId = userId;
	}

	@Override
	public void run() {

		// Fetch Hosted Events first

		ApiClientHelper helper = new ApiClientHelper();
		Zeppaclientapi api = helper.buildClientEndpoint();

		String cursor = null;
		String filter = "hostId == " + userId + " && end > "
				+ System.currentTimeMillis();
		String ordering = "end asc";
		Integer limit = Integer.valueOf(25);

		do {
 
			try {
				Zeppaclientapi.ListZeppaEvent listEventsTask = api.listZeppaEvent(credential.getToken());

				listEventsTask.setFilter(filter);
				listEventsTask.setCursor(cursor);
				listEventsTask.setOrdering(ordering);
				listEventsTask.setLimit(limit);

				CollectionResponseZeppaEvent response = listEventsTask
						.execute();

				if (response != null && response.getItems() != null
						&& !response.getItems().isEmpty()) {
					Iterator<ZeppaEvent> iterator = response.getItems()
							.iterator();
					while (iterator.hasNext()) {

						MyZeppaEventMediator mediator = new MyZeppaEventMediator(
								iterator.next());
						ZeppaEventSingleton.getInstance().addMediator(mediator);

					}

					if (response.getItems().size() < 25) {
						cursor = null;
					} else {
						cursor = response.getNextPageToken();
					}

				} else {
					cursor = null;
				}

			} catch (IOException e) {
				cursor = null;
			} catch (GoogleAuthException ex) {
				ex.printStackTrace();
				break;
			}
		} while (cursor != null);

		// Fetch Attending Events

		filter = "userId == " + userId + " && isAttending == " + Boolean.TRUE + " && expires > " + System.currentTimeMillis();
		// Know cursor is null at this point

		do {

			Zeppaclientapi.ListZeppaEventToUserRelationship task = null;
			try {
				task = api.listZeppaEventToUserRelationship(credential.getToken());

				task.setFilter(filter);
				task.setCursor(cursor);
				task.setLimit(limit);

				CollectionResponseZeppaEventToUserRelationship response = task
						.execute();

				if (response != null && response.getItems() != null
						&& !response.getItems().isEmpty()) {

					Iterator<ZeppaEventToUserRelationship> iterator = response
							.getItems().iterator();

					while (iterator.hasNext()) {
						ZeppaEventToUserRelationship relationship = iterator
								.next();

						try {

							ZeppaEvent event = api.getZeppaEvent(
									relationship.getEventId(), credential.getToken()).execute();

							if (ZeppaUserSingleton.getInstance()
									.getAbstractUserMediatorById(
											relationship.getEventHostId()) == null) {
								ZeppaUserInfo hostInfo = api
										.fetchZeppaUserInfoByParentId(
												credential.getToken(),relationship.getEventHostId())
										.execute();
								ZeppaUserSingleton.getInstance()
										.addDefaultZeppaUserMediator(hostInfo,
												null);
							}

							ZeppaEventSingleton.getInstance().addMediator(
									new DefaultZeppaEventMediator(event,
											relationship));
						} catch (IOException e) {
							e.printStackTrace();
						}

					}

					if (response.getItems().size() < 25) {
						cursor = null;
					} else {
						cursor = response.getNextPageToken();
					}

				} else {
					cursor = null;
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GoogleAuthException ex) {
				ex.printStackTrace();
			}

		} while (cursor != null);

		filter = "userId == " + userId + " && isWatching == " + Boolean.TRUE
				+ " && isAttending == " + Boolean.FALSE + " && expires > "
				+ System.currentTimeMillis();
		do {

			Zeppaclientapi.ListZeppaEventToUserRelationship task = null;
			try {
				task = api.listZeppaEventToUserRelationship(credential.getToken());

				task.setFilter(filter);
				task.setCursor(cursor);
				task.setLimit(limit);

				CollectionResponseZeppaEventToUserRelationship response = task
						.execute();

				if (response != null && response.getItems() != null
						&& !response.getItems().isEmpty()) {

					Iterator<ZeppaEventToUserRelationship> iterator = response
							.getItems().iterator();

					while (iterator.hasNext()) {
						ZeppaEventToUserRelationship relationship = iterator
								.next();

						if (!ZeppaEventSingleton.getInstance()
								.relationshipAlreadyHeld(relationship)) {
							try {
								ZeppaEvent event = api.getZeppaEvent(
										relationship.getEventId(), credential.getToken()).execute();

								if (ZeppaUserSingleton.getInstance()
										.getAbstractUserMediatorById(
												relationship.getEventHostId()) == null) {
									ZeppaUserInfo hostInfo = api
											.fetchZeppaUserInfoByParentId(
													credential.getToken(), relationship.getUserId())
											.execute();
									ZeppaUserSingleton.getInstance()
											.addDefaultZeppaUserMediator(
													hostInfo, null);
								}

								ZeppaEventSingleton.getInstance().addMediator(
										new DefaultZeppaEventMediator(event,
												relationship));
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

					}

					if (response.getItems().size() < 25) {
						cursor = null;
					} else {
						cursor = response.getNextPageToken();
					}

				} else {
					cursor = null;
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			} catch (GoogleAuthException ex) {
				ex.printStackTrace();
				break;
			}

		} while (cursor != null);

		ThreadManager.execute(new FetchInitialNotificationsRunnable(application,
				credential, userId, null));

		try {

			application.getCurrentActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					ZeppaEventSingleton.getInstance().onInitialEventsLoaded();

				}
			});
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

	}

}
