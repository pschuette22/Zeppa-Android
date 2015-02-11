package com.minook.zeppa.runnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.mediator.AbstractZeppaUserMediator;
import com.minook.zeppa.mediator.DefaultZeppaEventMediator.OnMinglerRelationshipsLoadedListener;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppauserinfoendpoint.Zeppauserinfoendpoint;
import com.minook.zeppa.zeppauserinfoendpoint.model.ZeppaUserInfo;
import com.minook.zeppa.zeppausertouserrelationshipendpoint.Zeppausertouserrelationshipendpoint;
import com.minook.zeppa.zeppausertouserrelationshipendpoint.Zeppausertouserrelationshipendpoint.ListZeppaUserToUserRelationship;
import com.minook.zeppa.zeppausertouserrelationshipendpoint.model.CollectionResponseZeppaUserToUserRelationship;
import com.minook.zeppa.zeppausertouserrelationshipendpoint.model.ZeppaUserToUserRelationship;

public class FetchMinglerRelationships extends BaseRunnable {

	private long userId;
	private OnMinglerRelationshipsLoadedListener listener;

	public FetchMinglerRelationships(ZeppaApplication application,
			GoogleAccountCredential credential,
			long userId,
			OnMinglerRelationshipsLoadedListener listener) {
		super(application, credential);
		this.userId = userId;
		this.listener = listener;
	}

	@Override
	public void run() {
		Zeppausertouserrelationshipendpoint endpoint = buildZeppaUserToUserRelationshipEndpoint();
		Zeppauserinfoendpoint iEndpoint = buildUserInfoEndpoint();
		
		List<ZeppaUserToUserRelationship> relationships = new ArrayList<ZeppaUserToUserRelationship>();

		String filter = "creatorId == " + userId;
		String cursor = null;
		ListZeppaUserToUserRelationship listInfotask = null;

		do {

			try {
				listInfotask = endpoint
						.listZeppaUserToUserRelationship();

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
			}

		} while (cursor != null);
		
		
		if (!relationships.isEmpty()) {
			Iterator<ZeppaUserToUserRelationship> iterator = relationships.iterator();
			
			while(iterator.hasNext()){
				ZeppaUserToUserRelationship relationship = iterator.next();
				if(ZeppaUserSingleton.getInstance().getAbstractUserMediatorById(relationship.getSubjectId()) == null){
					try {
						ZeppaUserInfo info = iEndpoint.getZeppaUserInfo(relationship.getSubjectId()).execute();
						ZeppaUserSingleton.getInstance().addDefaultZeppaUserMediator(info, relationship);
						
					} catch (IOException e){
					}
				}
				
				
			}
			

		}
		relationships.clear();
		

		filter = "subjectId == " + userId;
		cursor = null;
		do {

			try {
				listInfotask = endpoint
						.listZeppaUserToUserRelationship();

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
			}

		} while (cursor != null);

		if (!relationships.isEmpty()) {
			Iterator<ZeppaUserToUserRelationship> iterator = relationships.iterator();
			
			while(iterator.hasNext()){
				ZeppaUserToUserRelationship relationship = iterator.next();
				if(ZeppaUserSingleton.getInstance().getAbstractUserMediatorById(relationship.getCreatorId()) == null){
					try {
						ZeppaUserInfo info = iEndpoint.getZeppaUserInfo(relationship.getSubjectId()).execute();
						ZeppaUserSingleton.getInstance().addDefaultZeppaUserMediator(info, relationship);
						
					} catch (IOException e){
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
