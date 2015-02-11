package com.minook.zeppa.runnable;

import java.io.IOException;
import java.util.Iterator;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.mediator.DefaultZeppaEventMediator;
import com.minook.zeppa.mediator.MyZeppaEventMediator;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppaeventendpoint.Zeppaeventendpoint;
import com.minook.zeppa.zeppaeventendpoint.Zeppaeventendpoint.ListZeppaEvent;
import com.minook.zeppa.zeppaeventendpoint.model.CollectionResponseZeppaEvent;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.Zeppaeventtouserrelationshipendpoint;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.Zeppaeventtouserrelationshipendpoint.ListZeppaEventToUserRelationship;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.model.CollectionResponseZeppaEventToUserRelationship;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.model.ZeppaEventToUserRelationship;
import com.minook.zeppa.zeppauserinfoendpoint.Zeppauserinfoendpoint;
import com.minook.zeppa.zeppauserinfoendpoint.model.ZeppaUserInfo;

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

		Zeppaeventendpoint endpoint = buildEventEndpoint();

		String cursor = null;
		String filter = "hostId == " + userId + " && end > "
				+ System.currentTimeMillis();
		String ordering = "end asc";
		Integer limit = Integer.valueOf(25);

		do {

			try {
				ListZeppaEvent listEventsTask = endpoint.listZeppaEvent();

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
			}
		} while (cursor != null);

		// Fetch Attending Events
		Zeppaeventtouserrelationshipendpoint rEndpoint = buildEventRelationshipEndpoint();
		Zeppauserinfoendpoint iEndpoint = buildUserInfoEndpoint();

		filter = "userId == " + userId + " && isAttending == " + Boolean.TRUE;
		// Know cursor is null at this point

		do {

			ListZeppaEventToUserRelationship task = null;
			try {
				task = rEndpoint.listZeppaEventToUserRelationship();

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

							ZeppaEvent event = endpoint.getZeppaEvent(
									relationship.getEventId()).execute();

							if (ZeppaUserSingleton.getInstance()
									.getAbstractUserMediatorById(
											relationship.getEventHostId()) == null) {
								ZeppaUserInfo hostInfo = iEndpoint
										.fetchZeppaUserInfoByParentId(
												relationship.getUserId())
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
			}

		} while (cursor != null);

		filter = "userId == " + userId + " && isWatching == " + Boolean.TRUE
				+ " && isAttending == " + Boolean.FALSE + " && expires > "
				+ System.currentTimeMillis();
		do {

			ListZeppaEventToUserRelationship task = null;
			try {
				task = rEndpoint.listZeppaEventToUserRelationship();

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
								ZeppaEvent event = endpoint.getZeppaEvent(
										relationship.getEventId()).execute();

								if (ZeppaUserSingleton.getInstance()
										.getAbstractUserMediatorById(
												relationship.getEventHostId()) == null) {
									ZeppaUserInfo hostInfo = iEndpoint
											.getZeppaUserInfo(
													relationship.getUserId())
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
