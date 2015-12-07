package com.minook.zeppa.runnable;


import com.appspot.zeppa_cloud_1821.zeppaclientapi.Zeppaclientapi;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.CollectionResponseZeppaUserToUserRelationship;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaUserInfo;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaUserToUserRelationship;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ApiClientHelper;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

import java.io.IOException;
import java.util.Iterator;

public class FetchInitialMinglersRunnable extends BaseRunnable {

	private long userId;

	public FetchInitialMinglersRunnable(ZeppaApplication application,
			GoogleAccountCredential credential, long userId) {
		super(application, credential);
		this.userId = userId;
	}

	@Override
	public void run() {

		ApiClientHelper helper = new ApiClientHelper();
		Zeppaclientapi api = helper.buildClientEndpoint();

		String filter = "creatorId == " + userId;
		String cursor = null;
		Integer limit = Integer.valueOf(50);
		String ordering = "created desc";


		do {
			try {

				Zeppaclientapi.ListZeppaUserToUserRelationship task = api
						.listZeppaUserToUserRelationship(credential.getToken());
				task.setFilter(filter);
				task.setCursor(cursor);
				task.setLimit(limit);
				task.setOrdering(ordering);

				CollectionResponseZeppaUserToUserRelationship response = task
						.execute();

				if (response == null || response.getItems() == null
						|| response.getItems().isEmpty()) {
					cursor = null;
				} else {
					Iterator<ZeppaUserToUserRelationship> iterator = response
							.getItems().iterator();
					while (iterator.hasNext()) {
						ZeppaUserToUserRelationship relationship = iterator
								.next();
						try {
							ZeppaUserInfo userInfo;

							userInfo = api
									.fetchZeppaUserInfoByParentId(
											relationship.getSubjectId(), credential.getToken())
									.execute();

							ZeppaUserSingleton.getInstance()
									.addDefaultZeppaUserMediator(userInfo,
											relationship);
						} catch (IOException e) {
							e.printStackTrace();
						}

					}

				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (GoogleAuthException ex) {
				ex.printStackTrace();
				break;
			}
		} while (cursor != null);

		filter = "subjectId == " + userId;

		do {
			try {

				Zeppaclientapi.ListZeppaUserToUserRelationship task = api
						.listZeppaUserToUserRelationship(credential.getToken());
				task.setFilter(filter);
				task.setCursor(cursor);
				task.setLimit(limit);
				task.setOrdering(ordering);

				CollectionResponseZeppaUserToUserRelationship response = task
						.execute();

				if (response == null || response.getItems() == null
						|| response.getItems().isEmpty()) {
					cursor = null;
				} else {
					Iterator<ZeppaUserToUserRelationship> iterator = response
							.getItems().iterator();
					while (iterator.hasNext()) {
						ZeppaUserToUserRelationship relationship = iterator
								.next();
						try {
							ZeppaUserInfo userInfo = api
									.fetchZeppaUserInfoByParentId(
											relationship.getCreatorId(), credential.getToken())
									.execute();

							ZeppaUserSingleton.getInstance()
									.addDefaultZeppaUserMediator(userInfo,
											relationship);
						} catch (IOException e) {
							e.printStackTrace();
						}

					}

				}

			} catch (IOException e) {
				e.printStackTrace();
				break;
			} catch (GoogleAuthException ex) {
				ex.printStackTrace();
				break;
			}
		} while (cursor != null);

		ThreadManager.execute(new FetchInitialEventsRunnable(application,
				credential, userId));
		
		try {
			
			application.getCurrentActivity().runOnUiThread(new Runnable(){

				@Override
				public void run() {
					ZeppaUserSingleton.getInstance().setHasLoadedInitial();
					ZeppaUserSingleton.getInstance().notifyObservers();
					
				}
				
			});
			
		} catch (NullPointerException e){
			e.printStackTrace();
		}

	}

}
