package com.minook.zeppa.runnable;


import com.appspot.zeppa_cloud_1821.zeppaclientapi.Zeppaclientapi;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.CollectionResponseZeppaEventToUserRelationship;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaEvent;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaEventToUserRelationship;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaUserInfo;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ApiClientHelper;
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
		ApiClientHelper helper = new ApiClientHelper();
		Zeppaclientapi api = helper.buildClientEndpoint();


		String filter = "userId == " + userId + " &&  isAttending == "
				+ Boolean.FALSE + " && isWatching == " + Boolean.FALSE
				+ " && expires > " + System.currentTimeMillis();

		Integer limit = Integer.valueOf(10);

		try {
			Zeppaclientapi.ListZeppaEventToUserRelationship task = api
					.listZeppaEventToUserRelationship(credential.getToken());
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
									ZeppaUserInfo info = api.fetchZeppaUserInfoByParentId(credential.getToken(),relationship.getEventHostId().longValue()).execute();
									ZeppaUserSingleton.getInstance().addDefaultZeppaUserMediator(info, null);
								}
								
								ZeppaEvent event = api.getZeppaEvent(
										relationship.getEventId(), credential.getToken()).execute();
								if (event != null) {
									ZeppaEventSingleton.getInstance().addMediator(new DefaultZeppaEventMediator(event, relationship));
								}
							} catch (IOException | GoogleAuthException e) {
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

		} catch (IOException | GoogleAuthException e) {
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
