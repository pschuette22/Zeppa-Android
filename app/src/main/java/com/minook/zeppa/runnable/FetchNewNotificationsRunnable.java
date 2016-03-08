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

public class FetchNewNotificationsRunnable extends BaseRunnable {

	private long lastCallTime;
	private long userId;

	public FetchNewNotificationsRunnable(ZeppaApplication application,
			GoogleAccountCredential credential, long userId, long lastCallTime) {
		super(application, credential);
		this.userId = userId;
		this.lastCallTime = lastCallTime;
	}

	@Override
	public void run() {

		ApiClientHelper helper = new ApiClientHelper();
		Zeppaclientapi api = helper.buildClientEndpoint();

		String filter = "receiverId == " + userId + " && created > "
				+ lastCallTime;
		String cursor = null;
		Integer limit = Integer.valueOf(20);
		String ordering = "created desc";

		do {
			try {
				Zeppaclientapi.ListZeppaNotification task = api
						.listZeppaNotification(credential.getToken());
				task.setFilter(filter);
				task.setCursor(cursor);
				task.setLimit(limit);
				task.setOrdering(ordering);

				CollectionResponseZeppaNotification response = task.execute();

				if (response == null || response.getItems() == null
						|| response.getItems().isEmpty()) {
					cursor = null;
				} else {
					Iterator<ZeppaNotification> iterator = response.getItems()
							.iterator();
					while (iterator.hasNext()) {
						ZeppaNotification notification = iterator.next();

						try {
							if (ZeppaUserSingleton.getInstance()
									.getAbstractUserMediatorById(
											notification.getSenderId()) == null) {
								ZeppaUserInfo info = api
										.fetchZeppaUserInfoByParentId(credential.getToken(),
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
								ZeppaEvent event = api
										.getZeppaEvent(
												notification.getEventId(), credential.getToken())
										.execute();

								Zeppaclientapi.ListZeppaEventToUserRelationship task2 = api
										.listZeppaEventToUserRelationship(credential.getToken());
								task2.setFilter("eventId == " + event.getId()
										+ " && userId == " + userId);
								task2.setLimit(1);
								CollectionResponseZeppaEventToUserRelationship response2 = task2
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

					
					if(response.getItems().size() < 20){
						cursor = null;
					} else {
						cursor = response.getNextPageToken();
					}
					
					
				}

			} catch (IOException | GoogleAuthException e) {
				e.printStackTrace();
				break;
			}
		} while (cursor != null);
		
		
		try {
			application.getCurrentActivity().runOnUiThread(new Runnable(){

				@Override
				public void run() {
					NotificationSingleton.getInstance().notifyObservers();
					ZeppaEventSingleton.getInstance().notifyObservers();
					ZeppaUserSingleton.getInstance().notifyObservers();
				}
				
			});
			
		} catch (NullPointerException e){
			e.printStackTrace();
		}

	}

}
