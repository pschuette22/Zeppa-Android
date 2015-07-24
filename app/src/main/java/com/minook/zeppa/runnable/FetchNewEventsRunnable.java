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

public class FetchNewEventsRunnable extends BaseRunnable {

	private long minCallTime;
	private long userId;

	public FetchNewEventsRunnable(ZeppaApplication application,
			GoogleAccountCredential credential, long userId, long minCallTime) {
		super(application, credential);
		this.userId = userId;
		this.minCallTime = minCallTime;

		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		String filter = "userId == " + userId + " && created > " + minCallTime;
		Integer limit = Integer.valueOf(25);
		String ordering = "created asc";
		String cursor = null;
		Zeppaeventtouserrelationshipendpoint relationshipEndpoint = buildEventRelationshipEndpoint();
		Zeppaeventendpoint endpoint = buildEventEndpoint();

		try {
			do {

				ListZeppaEventToUserRelationship task = relationshipEndpoint
						.listZeppaEventToUserRelationship();
				task.setFilter(filter);
				task.setCursor(cursor);
				task.setLimit(limit);
				task.setOrdering(ordering);

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

								if(ZeppaUserSingleton.getInstance().getAbstractUserMediatorById(event.getHostId().longValue()) == null){
									ZeppaUserInfo info = buildUserInfoEndpoint().getZeppaUserInfo(event.getHostId().longValue()).execute();
									ZeppaUserSingleton.getInstance().addDefaultZeppaUserMediator(info, null);
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

			} while (cursor != null);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			
			application.getCurrentActivity().runOnUiThread(new Runnable(){

				@Override
				public void run() {
					ZeppaEventSingleton.getInstance().onFinishLoading();
				}
				
			});
			
		} catch (NullPointerException e){
			e.printStackTrace();
		}
	}

}
