package com.minook.zeppa.runnable;

import com.appspot.zeppa_cloud_1821.zeppauserinfoendpoint.model.ZeppaUserInfo;
import com.appspot.zeppa_cloud_1821.zeppausertouserrelationshipendpoint.Zeppausertouserrelationshipendpoint.ListZeppaUserToUserRelationship;
import com.appspot.zeppa_cloud_1821.zeppausertouserrelationshipendpoint.model.CollectionResponseZeppaUserToUserRelationship;
import com.appspot.zeppa_cloud_1821.zeppausertouserrelationshipendpoint.model.ZeppaUserToUserRelationship;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
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

		String filter = "creatorId == " + userId;
		String cursor = null;
		Integer limit = Integer.valueOf(50);
		String ordering = "created desc";

		do {
			try {

				ListZeppaUserToUserRelationship task = buildZeppaUserToUserRelationshipEndpoint()
						.listZeppaUserToUserRelationship();
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

							userInfo = buildUserInfoEndpoint()
									.fetchZeppaUserInfoByParentId(
											relationship.getSubjectId())
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
			}
		} while (cursor != null);

		filter = "subjectId == " + userId;

		do {
			try {

				ListZeppaUserToUserRelationship task = buildZeppaUserToUserRelationshipEndpoint()
						.listZeppaUserToUserRelationship();
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
							ZeppaUserInfo userInfo = buildUserInfoEndpoint()
									.fetchZeppaUserInfoByParentId(
											relationship.getCreatorId())
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
