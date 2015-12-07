package com.minook.zeppa.runnable;


import com.appspot.zeppa_cloud_1821.zeppaclientapi.Zeppaclientapi;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.CollectionResponseZeppaUserToUserRelationship;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaUserToUserRelationship;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ApiClientHelper;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.mediator.DefaultUserInfoMediator;
import com.minook.zeppa.mediator.DefaultZeppaEventMediator.OnMinglerRelationshipsLoadedListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FetchMutualMingersRunnable extends BaseRunnable {

	private long userId;
	private DefaultUserInfoMediator mediator;
	private OnMinglerRelationshipsLoadedListener listener;

	public FetchMutualMingersRunnable(ZeppaApplication application,
			GoogleAccountCredential credential, long userId,
			DefaultUserInfoMediator mediator,
			OnMinglerRelationshipsLoadedListener listener) {
		super(application, credential);
		// TODO Auto-generated constructor stub
		this.userId = userId;
		this.mediator = mediator;
		this.listener = listener;
	}

	@Override
	public void run() {

		ApiClientHelper helper = new ApiClientHelper();
		Zeppaclientapi api = helper.buildClientEndpoint();

		String filter = "creatorId == " + mediator.getUserId().longValue()
				+ " && subjectId != " + userId
				+ " && relationshipType == 'MINGLING'";
		String cursor = null;
		Integer limit = Integer.valueOf(50);

		List<Long> result = new ArrayList<Long>();

		try {
			do {

				Zeppaclientapi.ListZeppaUserToUserRelationship task = api
						.listZeppaUserToUserRelationship(credential.getToken());
				task.setFilter(filter);
				task.setCursor(cursor);
				task.setLimit(limit);
				CollectionResponseZeppaUserToUserRelationship response = task
						.execute();
				if (response == null || response.getItems() == null
						|| response.getItems().isEmpty()) {
					cursor = null;
				} else {
					Iterator<ZeppaUserToUserRelationship> iterator = response
							.getItems().iterator();
					while (iterator.hasNext()) {
						result.add(iterator.next().getSubjectId());
					}

					if (response.getItems().size() < 50) {
						cursor = null;
					} else {
						cursor = response.getNextPageToken();
					}
				}

			} while (cursor != null);
		} catch (IOException | GoogleAuthException e) {
			e.printStackTrace();
			try {
				application.getCurrentActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						listener.onErrorLoadingMinglerRelationships();

					}
				});
			} catch (NullPointerException npe) {
				npe.printStackTrace();
			}
			return;
		}

		filter = "subjectId == " + mediator.getUserId().longValue()
				+ " && creatorId != " + userId
				+ " && relationshipType == 'MINGLING'";
		
		try {
			do {

				Zeppaclientapi.ListZeppaUserToUserRelationship task = api
						.listZeppaUserToUserRelationship(credential.getToken());
				task.setFilter(filter);
				task.setCursor(cursor);
				task.setLimit(limit);
				CollectionResponseZeppaUserToUserRelationship response = task
						.execute();
				if (response == null || response.getItems() == null
						|| response.getItems().isEmpty()) {
					cursor = null;
				} else {
					Iterator<ZeppaUserToUserRelationship> iterator = response
							.getItems().iterator();
					while (iterator.hasNext()) {
						result.add(iterator.next().getCreatorId());
					}

					if (response.getItems().size() < 50) {
						cursor = null;
					} else {
						cursor = response.getNextPageToken();
					}
				}

			} while (cursor != null);
		} catch (IOException | GoogleAuthException e) {
			e.printStackTrace();
			try {
				application.getCurrentActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						listener.onErrorLoadingMinglerRelationships();

					}
				});
			} catch (NullPointerException npe) {
				npe.printStackTrace();
			}
			return;
		}
		
		mediator.setMinglingWithIds(result);
		try {
			
			application.getCurrentActivity().runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					listener.onMinglerRelationshipsLoaded();
					
				}
			});
		} catch (NullPointerException e){
			e.printStackTrace();
		}
	}

}
