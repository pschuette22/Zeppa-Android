package com.minook.zeppa.runnable;

import com.appspot.zeppa_cloud_1821.eventcommentendpoint.Eventcommentendpoint;
import com.appspot.zeppa_cloud_1821.eventcommentendpoint.Eventcommentendpoint.ListEventComment;
import com.appspot.zeppa_cloud_1821.eventcommentendpoint.model.CollectionResponseEventComment;
import com.appspot.zeppa_cloud_1821.eventcommentendpoint.model.EventComment;
import com.appspot.zeppa_cloud_1821.zeppauserinfoendpoint.Zeppauserinfoendpoint;
import com.appspot.zeppa_cloud_1821.zeppauserinfoendpoint.model.ZeppaUserInfo;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator.OnCommentLoadListener;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FetchEventCommentsRunnable extends BaseRunnable {

	private AbstractZeppaEventMediator mediator;
	private OnCommentLoadListener listener;
	private static final int QUERY_LIMIT = 10;

	public FetchEventCommentsRunnable(ZeppaApplication application,
			GoogleAccountCredential credential,
			AbstractZeppaEventMediator mediator, OnCommentLoadListener listener) {
		super(application, credential);

		this.mediator = mediator;
		this.listener = listener;

	}

	@Override
	public void run() {

		StringBuilder filterBuilder = new StringBuilder();
		filterBuilder.append("eventId == ");
		filterBuilder.append(mediator.getEventId().longValue());

		Eventcommentendpoint endpoint = buildCommentEndpoint();

		String commentCursor = null;
		List<EventComment> result = new ArrayList<EventComment>();
		do {
			try {
				ListEventComment task = endpoint.listEventComment();
				task.setFilter(filterBuilder.toString());

				task.setCursor(commentCursor);
				task.setLimit(QUERY_LIMIT);
				task.setOrdering("created desc");
				CollectionResponseEventComment response = task.execute();

				if (response != null && response.getItems() != null
						&& !response.isEmpty()) {

					List<EventComment> loadedComments = response.getItems();
					Iterator<EventComment> iterator = loadedComments.iterator();
					List<EventComment> remove = new ArrayList<EventComment>();

					Zeppauserinfoendpoint iEndpoint = buildUserInfoEndpoint();

					while (iterator.hasNext()) {
						EventComment comment = iterator.next();

						if (ZeppaUserSingleton.getInstance()
								.getAbstractUserMediatorById(
										comment.getCommenterId()) == null) {
							try {
								ZeppaUserInfo commenter = iEndpoint
										.getZeppaUserInfo(
												comment.getCommenterId())
										.execute();
								// Add this commenter singleton with the impression that there is no relationship to this user
								// Assumes if there were, a mediator for this user would already be held
								ZeppaUserSingleton.getInstance()
										.addDefaultZeppaUserMediator(commenter,
												null);
							} catch (IOException e) {
								e.printStackTrace();
								remove.add(comment);
							}
						}
					}

					if (loadedComments.size() < QUERY_LIMIT) {
						commentCursor = null;
					} else {
						// Set the next page token
						commentCursor = response.getNextPageToken();
					}

					loadedComments.removeAll(remove);
					result.addAll(result);

				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				listener.onErrorLoadingComments();
				break;
			}

		} while (commentCursor != null);

		if (!result.isEmpty()) {
			mediator.addAllComments(result);
			listener.onCommentsLoaded();
		}

	}
}
