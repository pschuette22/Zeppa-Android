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

		ApiClientHelper helper = new ApiClientHelper();
		Zeppaclientapi api = helper.buildClientEndpoint();

		String filter = "userId == " + userId + " && created > " + minCallTime;
		Integer limit = Integer.valueOf(25);
		String ordering = "created asc";
		String cursor = null;


		try {
			do {

				Zeppaclientapi.ListZeppaEventToUserRelationship task = api
						.listZeppaEventToUserRelationship(credential.getToken());
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
								ZeppaEvent event = api.getZeppaEvent(
										relationship.getEventId(), credential.getToken()).execute();

								if(ZeppaUserSingleton.getInstance().getAbstractUserMediatorById(event.getHostId().longValue()) == null){
									ZeppaUserInfo info = api.fetchZeppaUserInfoByParentId(credential.getToken(),event.getHostId().longValue()).execute();
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
			
		} catch (IOException | GoogleAuthException e) {
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
