package com.minook.zeppa.task;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.DateTime;
import com.minook.zeppa.mediator.MyZeppaEventMediator;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.zeppaeventendpoint.Zeppaeventendpoint;
import com.minook.zeppa.zeppaeventendpoint.Zeppaeventendpoint.ListZeppaEvent;
import com.minook.zeppa.zeppaeventendpoint.model.CollectionResponseZeppaEvent;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;

public class FetchHostedEventsTask extends FetchEventsTask {

	public FetchHostedEventsTask(GoogleAccountCredential credential,
			Long userId) {
		super(credential, userId);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Void doInBackground(Void... params) {

		Zeppaeventendpoint endpoint = buildZeppaEventEndpoint();

		DateTime now = new DateTime(new Date());
		
		String cursor = null;
		String filter = "hostId == hostIdParam && end > minDateParam";
		String paramDeclaration = "Long hostIdParam, java.util.Date minDateParam";
		String ordering = "end asc";
		
		int exceptionCount = 0;
		do {

			try {
				ListZeppaEvent listEventsTask = endpoint.listZeppaEvent();

				listEventsTask.setFilter(filter);
				listEventsTask.setCursor(cursor);
				listEventsTask.setParam1(userId);
				listEventsTask.setParam2(now);
				listEventsTask.setParameterDeclaration(paramDeclaration);
				listEventsTask.setOrdering(ordering);
				listEventsTask.setLimit(25);

				CollectionResponseZeppaEvent response = listEventsTask
						.execute();
				exceptionCount = 0;
				
				if(response != null && response.getItems() != null && !response.getItems().isEmpty()){
					
					Iterator<ZeppaEvent> iterator = response.getItems().iterator();
					while (iterator.hasNext()){
						ZeppaEvent event = iterator.next();
						MyZeppaEventMediator mediator = new MyZeppaEventMediator(event);
						
						ZeppaEventSingleton.getInstance().addMediator(mediator);
						
					}
					
					cursor = response.getNextPageToken();
					
				} else {
					cursor = null;
				}
				
			} catch (IOException e) {
				e.printStackTrace();
				
				if(exceptionCount >= 5){
					break;
				} else {
					exceptionCount++;
				}
				
			}
		} while (cursor != null);

		return null;
	}
	
	
	

}
