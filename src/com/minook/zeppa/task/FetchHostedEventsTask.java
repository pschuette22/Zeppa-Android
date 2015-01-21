package com.minook.zeppa.task;

import java.io.IOException;
import java.util.Iterator;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.mediator.MyZeppaEventMediator;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppaeventendpoint.Zeppaeventendpoint;
import com.minook.zeppa.zeppaeventendpoint.Zeppaeventendpoint.ListZeppaEvent;
import com.minook.zeppa.zeppaeventendpoint.model.CollectionResponseZeppaEvent;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;

public class FetchHostedEventsTask extends FetchEventsTask {

	public FetchHostedEventsTask(GoogleAccountCredential credential, Long userId) {
		super(credential, userId);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Boolean doInBackground(Void... params) {

		Boolean doUpdate = Boolean.FALSE;
		Zeppaeventendpoint endpoint = buildZeppaEventEndpoint();

		String cursor = null;
		String filter = "hostId == "
				+ ZeppaUserSingleton.getInstance().getUserId().longValue()
				+ " && end > " + System.currentTimeMillis();
		String ordering = "end asc";

		int exceptionCount = 0;
		do {

			try {
				ListZeppaEvent listEventsTask = endpoint.listZeppaEvent();

				listEventsTask.setFilter(filter);
				listEventsTask.setCursor(cursor);
				listEventsTask.setOrdering(ordering);
				listEventsTask.setLimit(25);

				CollectionResponseZeppaEvent response = listEventsTask
						.execute();
				exceptionCount = 0;

				if (response != null && response.getItems() != null
						&& !response.getItems().isEmpty()) {
					Iterator<ZeppaEvent> iterator = response.getItems()
							.iterator();
					while (iterator.hasNext()) {
						ZeppaEvent event = iterator.next();
						if (ZeppaEventSingleton.getInstance().getEventById(
								event.getId()) == null) {
							MyZeppaEventMediator mediator = new MyZeppaEventMediator(
									event);
							ZeppaEventSingleton.getInstance().addMediator(
									mediator, false);
							doUpdate = true;
						}

					}

					cursor = response.getNextPageToken();

				} else {
					cursor = null;
				}

			} catch (IOException e) {
				e.printStackTrace();

				if (exceptionCount >= 5) {
					break;
				} else {
					exceptionCount++;
				}

			}
		} while (cursor != null);

		return doUpdate;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

		ZeppaEventSingleton.getInstance().setHasLoadedInitialHostedEvents();

	}

}
