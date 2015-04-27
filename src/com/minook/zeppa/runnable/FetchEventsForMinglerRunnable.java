package com.minook.zeppa.runnable;

import java.io.IOException;
import java.util.Iterator;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.mediator.DefaultZeppaEventMediator;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.Zeppaeventtouserrelationshipendpoint.ListZeppaEventToUserRelationship;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.model.CollectionResponseZeppaEventToUserRelationship;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.model.ZeppaEventToUserRelationship;

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

		do {
			try {
				ListZeppaEventToUserRelationship task = buildEventRelationshipEndpoint()
						.listZeppaEventToUserRelationship();
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
								ZeppaEvent event = buildEventEndpoint()
										.getZeppaEvent(
												relationship.getEventId())
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
