package com.minook.zeppa.runnable;


import com.appspot.zeppa_cloud_1821.zeppaclientapi.Zeppaclientapi;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.CollectionResponseEventTag;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.CollectionResponseEventTagFollow;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.EventTag;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ApiClientHelper;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.mediator.AbstractEventTagMediator;
import com.minook.zeppa.mediator.DefaultEventTagMediator;
import com.minook.zeppa.singleton.EventTagSingleton.OnTagLoadListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FetchMinglerTagsRunnable extends BaseRunnable {

	private long userId;
	private long minglerId;
//	private OnTagLoadListener listener;

	public FetchMinglerTagsRunnable(ZeppaApplication application,
			GoogleAccountCredential credential, long userId, long minglerId,
			OnTagLoadListener listener) {
		super(application, credential);
		this.userId = userId;
		this.minglerId = minglerId;
//		this.listener = listener;
	}

	@Override
	public void run() {

		ApiClientHelper helper = new ApiClientHelper();
		Zeppaclientapi api = helper.buildClientEndpoint();

		String filter = "userId == " + minglerId;
		String cursor = null;
		Integer limit = Integer.valueOf(30);
		String ordering = "created desc";

		List<AbstractEventTagMediator> result = new ArrayList<AbstractEventTagMediator>();
		
		do {
			try {

				Zeppaclientapi.ListEventTag task = api.listEventTag(credential.getToken());

				task.setFilter(filter);
				task.setCursor(cursor);
				task.setLimit(limit);
				task.setOrdering(ordering);
				CollectionResponseEventTag response = task.execute();

				if (response == null || response.getItems() == null
						|| response.getItems().isEmpty()) {
					cursor = null;
				} else {

					Iterator<EventTag> iterator = response.getItems()
							.iterator();

					while (iterator.hasNext()) {
						try {
							EventTag tag = iterator.next();
							Zeppaclientapi.ListEventTagFollow task2 = api
									.listEventTagFollow(credential.getToken());
							task.setFilter("tagId == " + tag.getId()
									+ " && followerId == " + userId);
							task.setLimit(1);
							CollectionResponseEventTagFollow response2 = task2
									.execute();

							if(response2 == null || response2.getItems() == null || response2.getItems().isEmpty()){
								result.add(new DefaultEventTagMediator(tag, null));
							} else {
								result.add(new DefaultEventTagMediator(tag, response2.getItems().get(0)));
							}
							
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GoogleAuthException ex) {
				ex.printStackTrace();
				break;
			}

		} while (cursor != null);
		
		

	}

}
