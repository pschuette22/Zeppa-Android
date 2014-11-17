package com.minook.zeppa.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.mediator.MyZeppaEventMediator;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.model.ZeppaEvent;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.model.ZeppaEventToUserRelationship;

/**
 * This class is a task to create relationships to a given event and notify
 * users if necessary</p>
 * 
 * @author DrunkWithFunk21
 * 
 */
public class CreateEventRelationshipsTask extends NotifyUserTask {

	private static final List<Long> receivableUserIds = null;

	private final String TAG = getClass().getName();
	
	private GoogleAccountCredential credential;
	private MyZeppaEventMediator eventMediator;
	private List<Long> excludedUsersIds;
	private List<Long> eventTagIds;
	private List<Long> invitedUserIds;
	
	
	private String inviteMessage;
	private String recommendationMessage;

	public CreateEventRelationshipsTask(GoogleAccountCredential credential, MyZeppaEventMediator eventMediator, List<Long> excludedUsersIds,
			List<Long> eventTagIds, List<Long> invitedUserIds) {
		super(credential);
		
		this.credential = credential;
		this.eventMediator = eventMediator;
		
		if(excludedUsersIds == null){
			this.excludedUsersIds = new ArrayList<Long>();
		} else {
			this.excludedUsersIds = excludedUsersIds;
		}
		this.eventTagIds = eventTagIds;
		this.invitedUserIds = invitedUserIds;
		
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		List<Long> receivableUsersIds = new ArrayList<Long>();

		// Create relationships
		if(eventMediator.isPrivateEvent()){
			receivableUsersIds.addAll(invitedUserIds);
		} else {
			receivableUsersIds.addAll(ZeppaUserSingleton.getInstance().getAllFriendZeppaIds());
		}
		
		Iterator<Long> iterator = receivableUserIds.iterator();
		while(iterator.hasNext()){
			Long toUserId = iterator.next();
			ZeppaEventToUserRelationship relationship = makeRelationshipInstance(toUserId);
			if(invitedUserIds.contains(toUserId)){

			
			}
			
		}

		return null;
	}
	
	/**
	 * This method constructs a new instnace 
	 * @param toUserId
	 * @return
	 */
	private ZeppaEventToUserRelationship makeRelationshipInstance(Long eventId){
		ZeppaEventToUserRelationship result = new ZeppaEventToUserRelationship();

		ZeppaEvent event = new ZeppaEvent();
		event.setId(eventId);
		result.setEvent(event);
		
		result.setZeppaUserId(ZeppaUserSingleton.getInstance().getUserId());
		
		return result;
	}
	
	private boolean wasInvited(Long toUserId_){
		if(invitedUserIds == null || invitedUserIds.isEmpty()){
			return false;
		}
		
		long toUserId = toUserId_.longValue();
		
		Iterator<Long> iterator = invitedUserIds.iterator();
		while(iterator.hasNext()){
			if(toUserId == iterator.next().longValue())
				return true;
		}
		
		return false;
	}
	
	private boolean isRecommended(Long toUserId){
		
		return false;
	}

	
	private List<Long> fetchReccommendedUserIds(){
		List<Long> result = new ArrayList<Long>();
		
		
		return result;
	}
	
}
