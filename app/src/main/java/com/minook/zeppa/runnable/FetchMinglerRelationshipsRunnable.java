package com.minook.zeppa.runnable;


import com.appspot.zeppa_cloud_1821.zeppaclientapi.Zeppaclientapi;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.CollectionResponseZeppaUserToUserRelationship;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaUserInfo;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaUserToUserRelationship;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ApiClientHelper;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.mediator.DefaultZeppaEventMediator.OnMinglerRelationshipsLoadedListener;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FetchMinglerRelationshipsRunnable extends BaseRunnable {

	private long userId;
	private OnMinglerRelationshipsLoadedListener listener;

	public FetchMinglerRelationshipsRunnable(ZeppaApplication application,
			GoogleAccountCredential credential,
			long userId,
			OnMinglerRelationshipsLoadedListener listener) {
		super(application, credential);
		this.userId = userId;
		this.listener = listener;
	}

	@Override
	public void run() {

		ApiClientHelper helper = new ApiClientHelper();
		Zeppaclientapi api = helper.buildClientEndpoint();
		
		List<ZeppaUserToUserRelationship> relationships = new ArrayList<ZeppaUserToUserRelationship>();

		String filter = "creatorId == " + userId;
		String cursor = null;
		Zeppaclientapi.ListZeppaUserToUserRelationship listInfotask = null;

		do {

			try {
				listInfotask = api
						.listZeppaUserToUserRelationship(credential.getToken());

				listInfotask.setFilter(filter);
				listInfotask.setCursor(cursor);
				listInfotask.setLimit(25);

				CollectionResponseZeppaUserToUserRelationship response = listInfotask
						.execute();

				if (response == null || response.getItems() == null
						|| response.getItems().isEmpty()) {
					cursor = null;
				} else {
					relationships.addAll(response.getItems());
					filter = null;
					cursor = response.getNextPageToken();
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GoogleAuthException ex) {
				ex.printStackTrace();
			}

		} while (cursor != null);
		
		
		if (!relationships.isEmpty()) {
			Iterator<ZeppaUserToUserRelationship> iterator = relationships.iterator();
			
			while(iterator.hasNext()){
				ZeppaUserToUserRelationship relationship = iterator.next();
				if(ZeppaUserSingleton.getInstance().getAbstractUserMediatorById(relationship.getSubjectId()) == null){
					try {
						ZeppaUserInfo info = api.getZeppaUserInfo(relationship.getSubjectId(), credential.getToken()).execute();
						ZeppaUserSingleton.getInstance().addDefaultZeppaUserMediator(info, relationship);
						
					} catch (IOException e){
					} catch (GoogleAuthException ex){
						ex.printStackTrace();
						break;
					}
				}
				
				
			}
			

		}
		relationships.clear();
		

		filter = "subjectId == " + userId;
		cursor = null;
		do {

			try {
				listInfotask = api
						.listZeppaUserToUserRelationship(credential.getToken());

				listInfotask.setFilter(filter);
				listInfotask.setCursor(cursor);
				listInfotask.setLimit(25);

				CollectionResponseZeppaUserToUserRelationship response = listInfotask
						.execute();

				if (response == null || response.getItems() == null
						|| response.getItems().isEmpty()) {
					cursor = null;
				} else {
					relationships.addAll(response.getItems());
					filter = null;
					cursor = response.getNextPageToken();
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GoogleAuthException ex) {
				ex.printStackTrace();
				break;
			}

		} while (cursor != null);

		if (!relationships.isEmpty()) {
			Iterator<ZeppaUserToUserRelationship> iterator = relationships.iterator();
			
			while(iterator.hasNext()){
				ZeppaUserToUserRelationship relationship = iterator.next();
				if(ZeppaUserSingleton.getInstance().getAbstractUserMediatorById(relationship.getCreatorId()) == null){
					try {
						ZeppaUserInfo info = api.getZeppaUserInfo(relationship.getSubjectId(), credential.getToken()).execute();
						ZeppaUserSingleton.getInstance().addDefaultZeppaUserMediator(info, relationship);
						
					} catch (IOException e){
					} catch (GoogleAuthException ex ) {
						ex.printStackTrace();
					}
				}
				
				
			}
			

		}
		
		try {
			application.getCurrentActivity().runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					ThreadManager.execute(new FetchInitialEventsRunnable(application, credential, userId));
					listener.onMinglerRelationshipsLoaded();
				}
			});
			
		} catch (NullPointerException e){
			e.printStackTrace();
		}
		
	}
}
