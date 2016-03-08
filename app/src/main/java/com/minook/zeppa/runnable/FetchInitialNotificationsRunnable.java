package com.minook.zeppa.runnable;


import com.appspot.zeppa_cloud_1821.zeppaclientapi.Zeppaclientapi;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.CollectionResponseZeppaEventToUserRelationship;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.CollectionResponseZeppaNotification;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaEvent;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaNotification;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaUserInfo;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ApiClientHelper;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.mediator.DefaultZeppaEventMediator;
import com.minook.zeppa.singleton.NotificationSingleton;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

import java.io.IOException;
import java.util.Iterator;

public class FetchInitialNotificationsRunnable extends BaseRunnable {

	private long userId;
	private String nextPageToken;

	public FetchInitialNotificationsRunnable(ZeppaApplication application,
			GoogleAccountCredential credential, long userId,
			String nextPageToken) {
		super(application, credential);
		this.userId = userId;
		this.nextPageToken = nextPageToken;
	}

	@Override
	public void run() {
		ApiClientHelper helper = new ApiClientHelper();
		Zeppaclientapi api = helper.buildClientEndpoint();

		String filter = "recipientId == " + userId + " && expires > "
				+ System.currentTimeMillis();

		do {
			try {

				Zeppaclientapi.ListZeppaNotification listNotificationsTask = api
						.listZeppaNotification(credential.getToken());

				listNotificationsTask.setCursor(nextPageToken);
				listNotificationsTask.setFilter(filter);
				listNotificationsTask.setLimit(Integer.valueOf(25));

				CollectionResponseZeppaNotification response = listNotificationsTask
						.execute();

				if (response == null || response.getItems() == null
						|| response.getItems().isEmpty()) {
					// No notifications loaded
				} else {
					Iterator<ZeppaNotification> iterator = response.getItems()
							.iterator();

					while (iterator.hasNext()) {
						ZeppaNotification notification = iterator.next();

						try {
							if (ZeppaUserSingleton.getInstance()
									.getAbstractUserMediatorById(
											notification.getSenderId().longValue()) == null) {
								ZeppaUserInfo info = api
										.fetchZeppaUserInfoByParentId(
												credential.getToken(),notification.getSenderId())
										.execute();
								ZeppaUserSingleton
										.getInstance()
										.addDefaultZeppaUserMediator(info, null);
							}

							if (notification.getEventId() != null
									&& ZeppaEventSingleton.getInstance()
											.getEventById(
													notification.getEventId().longValue()) == null) {
								ZeppaEvent event = api
										.getZeppaEvent(
												notification.getEventId().longValue(), credential.getToken())
										.execute();

								Zeppaclientapi.ListZeppaEventToUserRelationship task = api
										.listZeppaEventToUserRelationship(credential.getToken());
								task.setFilter("eventId == " + event.getId()
										+ " && userId == " + userId);
								task.setLimit(1);
								CollectionResponseZeppaEventToUserRelationship response2 = task
										.execute();

								if (response2 == null
										|| response2.getItems() == null
										|| response2.getItems().size() != 1) {
									continue;
								}

								ZeppaEventSingleton.getInstance().addMediator(
										new DefaultZeppaEventMediator(event,
												response2.getItems().get(0)));
							}

							NotificationSingleton.getInstance()
									.addNotification(notification);

						} catch (IOException e) {
							e.printStackTrace();
						} catch (NullPointerException e){
							e.printStackTrace();
						}

					}

					if (response == null || response.getItems() == null
							|| response.getItems().size() < 25) {
						nextPageToken = null;
					} else {
						nextPageToken = response.getNextPageToken();
					}
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			} catch (GoogleAuthException ex) {
				ex.printStackTrace();
				break;
			}

		} while (nextPageToken != null);

		try {
			application.getCurrentActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					NotificationSingleton.getInstance().onLoadedNotifications();
					ZeppaEventSingleton.getInstance().notifyObservers();
					ThreadManager.execute(new FetchMoreEventsRunnable(
							application, credential, userId, null));
				}

			});

		} catch (NullPointerException e) {
			e.printStackTrace();
		}

	}

}
