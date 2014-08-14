/**
 * 
 */
package com.minook.zeppa.mediator;

import java.util.List;

import android.view.View;

import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.model.ZeppaEventToUserRelationship;

/**
 * @author DrunkWithFunk21
 *
 */
public class DefaultZeppaEventMediator extends AbstractZeppaEventMediator {
	
	private ZeppaEventToUserRelationship relationship; // null if non existent;
	private List<DefaultEventTagMediator> tagManagers;
	
	public DefaultZeppaEventMediator(ZeppaEvent event, ZeppaEventToUserRelationship relationship) {
		super(event);
		this.relationship = relationship;

		determineConflictStatus();
	}
	
	
	/**
	 * 
	 * @return true if the relationship is not equal to null
	 */
	
	@Override
	public boolean isInterestingEvent() {
		return (relationship != null);
	}

	public boolean isWatching(){
		if(relationship == null)
			return false;
		
		return relationship.getRelationshipType() == 1;
	}

	public boolean isAttending(){
	
		if(relationship == null)
			return false;
		
		return relationship.getRelationshipType() == 0;
	}
	
//	public boolean didRepost(){
//		boolean didRepost = false;
//		if(repostManager != null && hostManager.getUserId()){
//		}
//		return didRepost;
//	}
//	
//	public MyZeppaEventManager getMyRepostManager(){
//		return myRepostManager;
//	}
	
	
	protected void determineConflictStatus() {
		
	}
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
}
