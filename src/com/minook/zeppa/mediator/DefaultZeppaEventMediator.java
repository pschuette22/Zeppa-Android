/**
 * 
 */
package com.minook.zeppa.mediator;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.singleton.EventTagSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.model.ZeppaEventToUserRelationship;

/**
 * @author DrunkWithFunk21
 * 
 */
public class DefaultZeppaEventMediator extends AbstractZeppaEventMediator {

	private ZeppaEventToUserRelationship relationship; // null if non existent;

	public DefaultZeppaEventMediator(ZeppaEvent event,
			ZeppaEventToUserRelationship relationship) {
		super(event);
		this.relationship = relationship;


	}

	@Override
	public void convertView(AuthenticatedFragmentActivity context,
			View convertView) {
		super.convertView(context, convertView);

//		((Button) convertView.findViewById(R.id.eventview_buttonmiddle))
//				.setOnClickListener(this);
//
//		CheckedTextView watchButton = (CheckedTextView) convertView
//				.findViewById(R.id.eventview_buttonleft);
//
//		CheckedTextView joinButton = (CheckedTextView) convertView
//				.findViewById(R.id.eventview_buttonright);
//		joinButton.setOnClickListener(this);
//
//		if (isAttending()) {
//			joinButton.setChecked(true);
//			watchButton.setChecked(false);
//			watchButton.setEnabled(false);
//		} else {
//			joinButton.setChecked(false);
//			watchButton.setOnClickListener(this);
//			watchButton.setEnabled(true);
//			watchButton.setChecked(isWatching());
//		}

	}

	/**
	 * 
	 * @return true if the relationship is not equal to null
	 */

	@Override
	public boolean isAgendaEvent() {
		// User has a relationship to this event
		return (relationship != null);
	}

	@Override
	protected void setHostInfo(View view) {
		
		DefaultUserInfoMediator hostMediator = ZeppaUserSingleton.getInstance().getUserFor(event.getHost().getKey().getId());
		
		TextView hostName = (TextView) view
				.findViewById(R.id.eventview_hostname);
				
		hostName.setText(hostMediator.getDisplayName());
		ImageView hostImage = (ImageView) view
				.findViewById(R.id.eventview_hostimage);
		hostMediator.setImageWhenReady(hostImage);
		
		((LinearLayout) view.findViewById(R.id.eventview_hostinfo)).setOnClickListener(this);
		
	}

	public List<DefaultEventTagMediator> getUsedTagMediators(){
		return EventTagSingleton.getInstance().getDefaultTagsFrom(getTagIds());
	}
	
	public boolean isWatching() {
		if (relationship == null) {
			Log.wtf("TAG", "Null Relationship for mediator");
		}

		return relationship.getRelationshipType().equals("WATCHING");
	}
	
	public boolean isAttending() {

		if (relationship == null)
			return false;

		return relationship.getRelationshipType().equals("ATTENDING");
	}

	// public boolean didRepost(){
	// boolean didRepost = false;
	// if(repostManager != null && hostManager.getUserId()){
	// }
	// return didRepost;
	// }
	//
	// public MyZeppaEventManager getMyRepostManager(){
	// return myRepostManager;
	// }
	
	

	/**
	 * NOT THREAD SAFE</p>
	 * This method checks the time of the event vs other calendar events. It should be called when changes are made to the calendar
	 * 
	 * @param conflictIndicator optional ImageView to update upon determining conflict status.
	 */
	protected void determineConflictStatusWithBlocking(Context context, ImageView conflictIndicator) {
		if(isAttending()){
			conflictStatus = ConflictStatus.ATTENDING;
		} else {
			conflictStatus = ConflictStatus.UNKNOWN;
			// TODO: check 
			
		}
		
		if(conflictIndicator != null){
			// TODO: update conflict indicator
		}
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.eventview_buttonleft: // Watch/ Unwatch

			break;
		case R.id.eventview_buttonmiddle: // Text host

			break;

		case R.id.eventview_buttonright: // Join/ Leave

			break;

		case R.id.eventview_hostinfo:

			break;
		}

	}

}
