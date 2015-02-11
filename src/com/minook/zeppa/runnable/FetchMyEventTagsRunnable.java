package com.minook.zeppa.runnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.eventtagendpoint.Eventtagendpoint;
import com.minook.zeppa.eventtagendpoint.Eventtagendpoint.ListEventTag;
import com.minook.zeppa.eventtagendpoint.model.CollectionResponseEventTag;
import com.minook.zeppa.eventtagendpoint.model.EventTag;
import com.minook.zeppa.mediator.AbstractEventTagMediator;
import com.minook.zeppa.mediator.MyEventTagMediator;
import com.minook.zeppa.singleton.EventTagSingleton;

public class FetchMyEventTagsRunnable extends BaseRunnable {

	private Long userId;

	public FetchMyEventTagsRunnable(ZeppaApplication application,
			GoogleAccountCredential credential, Long userId) {
		super(application, credential);
		this.userId = userId;
	}

	@Override
	public void run() {

		Eventtagendpoint endpoint = buildEventTagEndpoint();

		// TODO: List Query for all of this users event tags

		String cursor = null;
		String filter = "userId == " + userId.longValue();

		List<AbstractEventTagMediator> myTagList = new ArrayList<AbstractEventTagMediator>();
		do {

			try {
				ListEventTag listTagTask = endpoint.listEventTag();

				listTagTask.setCursor(cursor);
				listTagTask.setFilter(filter);
				listTagTask.setLimit(50);

				CollectionResponseEventTag tagResponse = listTagTask.execute();

				if (tagResponse == null || tagResponse.getItems() == null
						|| tagResponse.getItems().isEmpty()) {
					cursor = null;
					break;
				} else {

					Iterator<EventTag> iterator = tagResponse.getItems().iterator();
					while(iterator.hasNext()){
						myTagList.add(new MyEventTagMediator(iterator.next()));
					}

					if (tagResponse.getItems().size() < 50) {
						cursor = null;
					} else {
						cursor = tagResponse.getNextPageToken();
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
				break;
			}

		} while (cursor != null);
		
		EventTagSingleton.getInstance().addEventTags(myTagList);
		
		try {
			
			application.getCurrentActivity().runOnUiThread(new Runnable(){

				@Override
				public void run() {
					EventTagSingleton.getInstance().onMyTagsLoaded();	
				}
				
			});
			
		} catch (NullPointerException e){
			e.printStackTrace();
		}

	}

}
