package com.minook.zeppa.runnable;

import com.appspot.zeppa_cloud_1821.zeppaeventendpoint.Zeppaeventendpoint;
import com.appspot.zeppa_cloud_1821.zeppaeventendpoint.model.ZeppaEvent;
import com.appspot.zeppa_cloud_1821.zeppaeventtouserrelationshipendpoint.Zeppaeventtouserrelationshipendpoint;
import com.appspot.zeppa_cloud_1821.zeppaeventtouserrelationshipendpoint.Zeppaeventtouserrelationshipendpoint.ListZeppaEventToUserRelationship;
import com.appspot.zeppa_cloud_1821.zeppaeventtouserrelationshipendpoint.model.CollectionResponseZeppaEventToUserRelationship;
import com.appspot.zeppa_cloud_1821.zeppaeventtouserrelationshipendpoint.model.ZeppaEventToUserRelationship;
import com.appspot.zeppa_cloud_1821.zeppauserinfoendpoint.model.ZeppaUserInfo;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.mediator.DefaultZeppaEventMediator;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

import java.io.IOException;
import java.util.Iterator;

public class FetchMoreEventsRunnable extends BaseRunnable {

	private long userId;
	private String nextPageToken;

	public FetchMoreEventsRunnable(ZeppaApplication application,
			GoogleAccountCredential credential, long userId,
			String nextPageToken) {
		super(application, credential);
		this.userId = userId;
		this.nextPageToken = nextPageToken;

	}

	@Override
	public void run() {
		Zeppaeventtouserrelationshipendpoint endpoint = buildEventRelationshipEndpoint();
		Zeppaeventendpoint eEndpoint = buildEventEndpoint();

		String filter = "userId == " + userId + " &&  isAttending == "
				+ Boolean.FALSE + " && isWatching == " + Boolean.FALSE
				+ " && expires > " + System.currentTimeMillis();

		Integer limit = Integer.valueOf(10);

		try {
			ListZeppaEventToUserRelationship task = endpoint
					.listZeppaEventToUserRelationship();
			task.setFilter(filter);
			task.setCursor(nextPageToken);
			task.setFilter(filter);
			task.setOrdering("expires asc");
			task.setLimit(limit);

			CollectionResponseZeppaEventToUserRelationship response = task
					.execute();

			if (response != null && response.getItems() != null) {
				if (response.getItems().isEmpty()) {
					nextPageToken = null;
				} else {

					Iterator<ZeppaEventToUserRelationship> iterator = response
							.getItems().iterator();
					while (iterator.hasNext()) {
						ZeppaEventToUserRelationship relationship = iterator
								.next();
						if (!ZeppaEventSingleton.getInstance()
								.relationshipAlreadyHeld(relationship)) {
							
							try {
								if(ZeppaUserSingleton.getInstance().getAbstractUserMediatorById(relationship.getEventHostId().longValue()) == null){
									ZeppaUserInfo info = buildUserInfoEndpoint().getZeppaUserInfo(relationship.getEventHostId().longValue()).execute();
									ZeppaUserSingleton.getInstance().addDefaultZeppaUserMediator(info, null);
								}
								
								ZeppaEvent event = eEndpoint.getZeppaEvent(
										relationship.getEventId()).execute();
								if (event != null) {
									ZeppaEventSingleton.getInstance().addMediator(new DefaultZeppaEventMediator(event, relationship));
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

					}

					if (response.getItems().size() < limit.intValue()) {
						nextPageToken = null;
					} else {
						nextPageToken = response.getNextPageToken();
					}

				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			application.getCurrentActivity().runOnUiThread(new Runnable(){

				@Override
				public void run() {
					ZeppaEventSingleton.getInstance().setNextRelationshipPageToken(nextPageToken);
					ZeppaEventSingleton.getInstance().onFinishLoading();
				}
				
			});
		} catch (NullPointerException e){
			e.printStackTrace();
		}
		
	}

}
