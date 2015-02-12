package com.minook.zeppa.runnable;

import java.io.IOException;
import java.util.Iterator;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.mediator.DefaultZeppaEventMediator;
import com.minook.zeppa.singleton.NotificationSingleton;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.Zeppaeventtouserrelationshipendpoint.ListZeppaEventToUserRelationship;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.model.CollectionResponseZeppaEventToUserRelationship;
import com.minook.zeppa.zeppanotificationendpoint.Zeppanotificationendpoint;
import com.minook.zeppa.zeppanotificationendpoint.Zeppanotificationendpoint.ListZeppaNotification;
import com.minook.zeppa.zeppanotificationendpoint.model.CollectionResponseZeppaNotification;
import com.minook.zeppa.zeppanotificationendpoint.model.ZeppaNotification;
import com.minook.zeppa.zeppauserinfoendpoint.model.ZeppaUserInfo;

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
		Zeppanotificationendpoint notificationEndpoint = buildNotificationEndpoint();

		String filter = "recipientId == " + userId + " && expires > "
				+ System.currentTimeMillis();

		do {
			try {

				ListZeppaNotification listNotificationsTask = notificationEndpoint
						.listZeppaNotification();

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
											notification.getSenderId()) == null) {
								ZeppaUserInfo info = buildUserInfoEndpoint()
										.fetchZeppaUserInfoByParentId(
												notification.getSenderId())
										.execute();
								ZeppaUserSingleton
										.getInstance()
										.addDefaultZeppaUserMediator(info, null);
							}

							if (notification.getEventId() != null
									&& ZeppaEventSingleton.getInstance()
											.getEventById(
													notification.getEventId()) == null) {
								ZeppaEvent event = buildEventEndpoint()
										.getZeppaEvent(
												notification.getEventId())
										.execute();

								ListZeppaEventToUserRelationship task = buildEventRelationshipEndpoint()
										.listZeppaEventToUserRelationship();
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
