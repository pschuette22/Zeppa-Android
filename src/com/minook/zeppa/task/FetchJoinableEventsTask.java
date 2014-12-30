package com.minook.zeppa.task;

import java.io.IOException;
import java.util.Iterator;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.mediator.DefaultZeppaEventMediator;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.zeppaeventendpoint.Zeppaeventendpoint;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.Zeppaeventtouserrelationshipendpoint;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.Zeppaeventtouserrelationshipendpoint.ListZeppaEventToUserRelationship;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.model.CollectionResponseZeppaEventToUserRelationship;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.model.ZeppaEventToUserRelationship;

public class FetchJoinableEventsTask extends FetchEventsTask {

	private String relationshipCursor;

	public FetchJoinableEventsTask(GoogleAccountCredential credential,
			Long userId, String relationshipCursor) {
		super(credential, userId);

		this.relationshipCursor = relationshipCursor;
	}

	@Override
	protected Boolean doInBackground(Void... params) {

		Boolean success = Boolean.FALSE;
		Zeppaeventtouserrelationshipendpoint.Builder builder = new Zeppaeventtouserrelationshipendpoint.Builder(
				transport, factory, credential);
		builder = CloudEndpointUtils.updateBuilder(builder);
		Zeppaeventtouserrelationshipendpoint endpoint = builder.build();

		String filter = "userId == " + userId + " && expires > " + System.currentTimeMillis();
		String order = "expires asc";
		Integer limit = Integer.valueOf(25);

		try {
			ListZeppaEventToUserRelationship listRelationshipTask = endpoint
					.listZeppaEventToUserRelationship();

			listRelationshipTask.setFilter(filter);
			listRelationshipTask.setCursor(relationshipCursor);
			listRelationshipTask.setOrdering(order);
			listRelationshipTask.setLimit(limit);

			CollectionResponseZeppaEventToUserRelationship response = listRelationshipTask
					.execute();

			if (response != null && response.getItems() != null
					&& !response.getItems().isEmpty()) {
				Iterator<ZeppaEventToUserRelationship> iterator = response
						.getItems().iterator();

				while (iterator.hasNext()) {
					ZeppaEventToUserRelationship relationship = iterator.next();

					if (ZeppaEventSingleton.getInstance()
							.relationshipAlreadyHeld(relationship)) {
						continue;
					} else {

						DefaultZeppaEventMediator mediator = fetchEventForRelationship(relationship);

						if (mediator != null) {
							ZeppaEventSingleton.getInstance().addMediator(
									mediator);
						}
					}
				}

				ZeppaEventSingleton.getInstance().setNextRelationshipPageToken(
						response.getNextPageToken());
				success = Boolean.TRUE;
			}

			

		} catch (IOException e) {
			e.printStackTrace();

		}

		return success;
	} 
	
	
	
	

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		
		ZeppaEventSingleton.getInstance().setHasLoadedInitialFeedEvents();
	}

	/**
	 * This is a non-thread safe method of fetching an event for a given
	 * relationship
	 * 
	 * @param relationship
	 * @return
	 */
	private DefaultZeppaEventMediator fetchEventForRelationship(
			ZeppaEventToUserRelationship relationship) {
		DefaultZeppaEventMediator mediator = null;

		try {
			Zeppaeventendpoint endpoint = buildZeppaEventEndpoint();
			ZeppaEvent event = endpoint
					.getZeppaEvent(relationship.getEventId()).execute();
			mediator = new DefaultZeppaEventMediator(event, relationship);

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return mediator;
	}

}
