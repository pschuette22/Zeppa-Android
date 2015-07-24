package com.minook.zeppa.runnable;

import com.appspot.zeppa_cloud_1821.eventcommentendpoint.Eventcommentendpoint.ListEventComment;
import com.appspot.zeppa_cloud_1821.eventcommentendpoint.model.CollectionResponseEventComment;
import com.appspot.zeppa_cloud_1821.eventcommentendpoint.model.EventComment;
import com.appspot.zeppa_cloud_1821.zeppauserinfoendpoint.model.ZeppaUserInfo;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FetchCommentsRunnable extends BaseRunnable {

	private AbstractZeppaEventMediator mediator;
	private long minCommentPostTime;

	public FetchCommentsRunnable(ZeppaApplication application,
			GoogleAccountCredential credential,
			AbstractZeppaEventMediator mediator, long minCommentPostTime) {
		super(application, credential);
		this.mediator = mediator;
		this.minCommentPostTime = minCommentPostTime;
	}

	@Override
	public void run() {

		String filter = "eventId == " + mediator.getEventId().longValue()
				+ " && created > " + minCommentPostTime;
		String cursor = null;
		Integer limit = Integer.valueOf(25);
		String ordering = "created desc";

		List<EventComment> result = new ArrayList<EventComment>();
		
		try {
			do {
				ListEventComment task = buildCommentEndpoint()
						.listEventComment();
				task.setFilter(filter);
				task.setCursor(cursor);
				task.setLimit(limit);
				task.setOrdering(ordering);

				CollectionResponseEventComment response = task.execute();

				if (response == null || response.getItems() == null
						|| response.getItems().isEmpty()) {
					cursor = null;
				} else {

					Iterator<EventComment> iterator = response.getItems()
							.iterator();
					
					while (iterator.hasNext()) {
						EventComment comment = iterator.next();

						try {

							if (ZeppaUserSingleton.getInstance()
									.getAbstractUserMediatorById(
											comment.getCommenterId()) == null) {
								ZeppaUserInfo info = buildUserInfoEndpoint()
										.getZeppaUserInfo(
												comment.getCommenterId()
														.longValue()).execute();
								ZeppaUserSingleton
										.getInstance()
										.addDefaultZeppaUserMediator(info, null);
							}
							
							result.add(comment);

						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					if(response.getItems().size() < 50){
						cursor = null;
					} else {
						cursor = response.getNextPageToken();
					}
					

				}

			} while (cursor != null);
			

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		mediator.addAllComments(result);
		
		try {
			application.getCurrentActivity().runOnUiThread(new Runnable(){

				@Override
				public void run() {
					mediator.onCommentsLoaded();
					
				}
				
			});
			
		} catch (NullPointerException e){
			e.printStackTrace();
		}
		

	}

}
