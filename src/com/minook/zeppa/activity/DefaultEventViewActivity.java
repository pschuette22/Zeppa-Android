package com.minook.zeppa.activity;

import java.util.List;

import com.minook.zeppa.adapter.tagadapter.FriendTagAdapter;
import com.minook.zeppa.mediator.DefaultUserInfoMediator;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

public class DefaultEventViewActivity extends AbstractEventViewActivity {

	
	@Override
	protected void setHostMediator() {
		hostMediator = ZeppaUserSingleton.getInstance().getAbstractUserMediatorById(eventMediator.getHostId());
		
	}

	@Override
	protected void setEventTagAdapter() {
		tagAdapter = new FriendTagAdapter(this, tagHolder, eventMediator.getTagIds());
		
	}

	@Override
	protected void setAttendingText() {
		if(eventMediator.getHasLoadedAttendingRelationship()){
			// Has Loaded initial relationships
			List<Long> attendingUserIds = eventMediator.getAttendingUserIds();
			if(attendingUserIds.isEmpty()){
				attending.setText("Be the first to join!");
			} else {
			
				List<DefaultUserInfoMediator> attendingMinglerIds = ZeppaUserSingleton.getInstance().getFriendsFrom(attendingUserIds);
				attending.setText("You mingle with " + attendingMinglerIds.size() + "/" + attendingMinglerIds.size() + " people going");
			
			}
		
		
		} else {
			attending.setText("Loading...");
		}
	}

	@Override
	protected void setConfliction() {
		
	}

}
