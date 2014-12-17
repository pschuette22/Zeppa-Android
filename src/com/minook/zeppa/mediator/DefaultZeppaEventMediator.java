/**
 * 
 */
package com.minook.zeppa.mediator;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
	public void convertEventListItemView(AuthenticatedFragmentActivity context,
			View convertView) {
		super.convertEventListItemView(context, convertView);

		// ((Button) convertView.findViewById(R.id.eventview_buttonmiddle))
		// .setOnClickListener(this);
		//
		// CheckedTextView watchButton = (CheckedTextView) convertView
		// .findViewById(R.id.eventview_buttonleft);
		//
		// CheckedTextView joinButton = (CheckedTextView) convertView
		// .findViewById(R.id.eventview_buttonright);
		// joinButton.setOnClickListener(this);
		//
		// if (isAttending()) {
		// joinButton.setChecked(true);
		// watchButton.setChecked(false);
		// watchButton.setEnabled(false);
		// } else {
		// joinButton.setChecked(false);
		// watchButton.setOnClickListener(this);
		// watchButton.setEnabled(true);
		// watchButton.setChecked(isWatching());
		// }

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

		DefaultUserInfoMediator hostMediator = ZeppaUserSingleton.getInstance()
				.getUserFor(event.getHostId());

		TextView hostName = (TextView) view
				.findViewById(R.id.eventview_hostname);

		hostName.setText(hostMediator.getDisplayName());
		ImageView hostImage = (ImageView) view
				.findViewById(R.id.eventview_hostimage);
		hostMediator.setImageWhenReady(hostImage);

		((LinearLayout) view.findViewById(R.id.eventview_hostinfo))
				.setOnClickListener(this);

	}

	public List<DefaultEventTagMediator> getUsedTagMediators() {
		return EventTagSingleton.getInstance().getDefaultTagsFrom(getTagIds());
	}

	/**
	 * Returns true if user is watching this event
	 * @return
	 */
	public boolean isWatching() {
		if (relationship == null) {
			Log.wtf("TAG", "Null Relationship for mediator");
			return false;
		}

		return relationship.getIsWatching();
	}

	/**
	 * Returns true if user is attending this event
	 * @return
	 */
	public boolean isAttending() {

		if (relationship == null) {
			Log.wtf("TAG", "Null Relationship for mediator");
			return false;
		}

		return relationship.getIsAttending();
	}
	
	/**
	 * This is decides if a given relationship represents the current one. 
	 * This is to avoid loading in an event twice.
	 * 
	 * @param relationship
	 * @return
	 */
	public boolean relationshipDoesMatch(ZeppaEventToUserRelationship relationship){
		return (this.relationship.getId().longValue() == relationship.getId().longValue());
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
	 * NOT THREAD SAFE</p> This method checks the time of the event vs other
	 * calendar events. It should be called when changes are made to the
	 * calendar
	 * 
	 * @param conflictIndicator
	 *            optional ImageView to update upon determining conflict status.
	 */
	protected void determineConflictStatusWithBlocking(Context context,
			ImageView conflictIndicator) {
		if (isAttending()) {
			conflictStatus = ConflictStatus.ATTENDING;
		} else {
			conflictStatus = ConflictStatus.UNKNOWN;
			// TODO: check

		}

		if (conflictIndicator != null) {
			// TODO: update conflict indicator
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.quickaction_watch: // Watch/ Unwatch
			
			if(isWatching()){
				// Is watching, stop watching

			} else {
				// Is not watching, start watching
				
			}
			
			break;
		case R.id.quickaction_text: // Text host

			break;

		case R.id.quickaction_join: // Join/ Leave

			break;

		case R.id.eventview_hostinfo:

			break;
		}

	}

	/**
	 * This method converts the quick action bar for a user to interact with</p>
	 * It will display the user's status of 
	 */
	@Override
	public void convertQuickActionBar(View barView) {
		CheckBox watchCheckBox = (CheckBox) barView.findViewById(R.id.quickaction_watch);
		Button textButton = (Button) barView.findViewById(R.id.quickaction_text);
		CheckBox joinCheckBox = (CheckBox) barView.findViewById(R.id.quickaction_join);

		watchCheckBox.setChecked(isWatching());
		joinCheckBox.setChecked(isAttending());
		
		watchCheckBox.setOnClickListener(this);
		textButton.setOnClickListener(this);
		joinCheckBox.setOnClickListener(this);
		
		
	}

}
