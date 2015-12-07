package com.minook.zeppa.runnable;


import com.appspot.zeppa_cloud_1821.zeppaclientapi.Zeppaclientapi;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.CollectionResponseEventTag;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.CollectionResponseEventTagFollow;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.EventTag;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.EventTagFollow;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ApiClientHelper;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.mediator.DefaultEventTagMediator;
import com.minook.zeppa.mediator.DefaultUserInfoMediator;
import com.minook.zeppa.singleton.EventTagSingleton;
import com.minook.zeppa.singleton.EventTagSingleton.OnTagLoadListener;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FetchDefaultTagsForUserRunnable extends BaseRunnable {

	private long userIdSelf;
	private long userIdMingler;
	private OnTagLoadListener listener;

	public FetchDefaultTagsForUserRunnable(ZeppaApplication application,
			GoogleAccountCredential credential, long userIdSelf,
			long userIdMingler, OnTagLoadListener listener) {
		super(application, credential);
		this.userIdSelf = userIdSelf;
		this.userIdMingler = userIdMingler;
		this.listener = listener;
	}

	@Override
	public void run() {

		String filter = "tagOwnerId == " + userIdMingler + " && followerId == "
				+ userIdSelf;
		String cursor = null;
		Integer limit = Integer.valueOf(50);
		List<EventTagFollow> follows = new ArrayList<EventTagFollow>();

		ApiClientHelper helper = new ApiClientHelper();
		Zeppaclientapi api = helper.buildClientEndpoint();

		do {
			try {
				Zeppaclientapi.ListEventTagFollow task = api
						.listEventTagFollow(credential.getToken());
				task.setFilter(filter);
				task.setCursor(cursor);
				task.setLimit(limit);
				CollectionResponseEventTagFollow response = task.execute();
				if (response != null && response.getItems() != null
						&& !response.getItems().isEmpty()) {
					follows.addAll(response.getItems());
					if (response.getItems().size() < 50) {
						cursor = null;
					}
				} else {
					cursor = null;
				}

			} catch (IOException e) {
				e.printStackTrace();
				try {
					listener.onErrorLoadingTags();

				} catch (Exception e2) {
					e2.printStackTrace();
				}
				return;
			} catch (GoogleAuthException ex){
				ex.printStackTrace();
				break;
			}
		} while (cursor != null);


		filter = "userId == " + userIdMingler;
		cursor = null;
		limit = Integer.valueOf(50);
		String ordering = "created desc";

		List<DefaultEventTagMediator> result = new ArrayList<DefaultEventTagMediator>();
		do {
			try {

				Zeppaclientapi.ListEventTag task = api.listEventTag(credential.getToken());
				task.setFilter(filter);
				task.setCursor(cursor);
				task.setLimit(limit);
				task.setOrdering(ordering);

				CollectionResponseEventTag tagResponse = task.execute();

				if (tagResponse == null || tagResponse.getItems() == null
						|| tagResponse.getItems().isEmpty()) {
					cursor = null;
				} else {

					Iterator<EventTag> iterator = tagResponse.getItems()
							.iterator();
					while (iterator.hasNext()) {
						EventTag tag = iterator.next();

						EventTagFollow follow = null;
						Iterator<EventTagFollow> fIterator = follows.iterator();
						while (fIterator.hasNext()) {
							EventTagFollow f = fIterator.next();
							if (f.getTagId().longValue() == tag.getId()
									.longValue()) {
								follow = f;
								break;
							}
						}

						if (follow != null) {
							follows.remove(follow);
						}

						result.add(new DefaultEventTagMediator(tag, follow));

					}

					if (tagResponse.getItems().size() < 50) {
						cursor = null;
					} else {
						cursor = tagResponse.getNextPageToken();
					}

				}

			} catch (IOException e) {
				e.printStackTrace();
				try {
					listener.onErrorLoadingTags();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				return;
			} catch (GoogleAuthException ex){
				ex.printStackTrace();
				break;
			}

		} while (cursor != null);

		((DefaultUserInfoMediator) ZeppaUserSingleton.getInstance()
				.getAbstractUserMediatorById(userIdMingler))
				.setHasLoadedInitialTags(true);

		EventTagSingleton.getInstance().updateEventTagsForUser(userIdMingler,
				result);

		try {
			application.getCurrentActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					listener.onTagsLoaded();

				}

			});
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

	}

}
