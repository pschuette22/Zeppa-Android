package com.minook.zeppa.mediator;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.activity.UserActivity;
import com.minook.zeppa.zeppauserinfoendpoint.model.ZeppaUserInfo;
import com.minook.zeppa.zeppausertouserrelationshipendpoint.model.ZeppaUserToUserRelationship;

public class DefaultUserInfoMediator extends AbstractZeppaUserMediator
		implements OnClickListener {

	private ZeppaUserInfo userInfo;
	private ZeppaUserToUserRelationship relationship;
	private List<DefaultEventTagMediator> tagManagers;
	private List<ZeppaUserToUserRelationship> mutualFriends;

	private boolean loadedTags;
	private boolean loadedMutualFriendRelationships;

	/**
	 * Constructs a new DefaultUserInfoManager, the class which manages a held
	 * userInfo Object
	 * 
	 * @param userInfo
	 *            , object to be managed
	 * @param relationship
	 *            , held relationship to user object. Null if no relationship
	 * @param credential
	 *            , Credential for making authenticated calls
	 */
	public DefaultUserInfoMediator(ZeppaUserInfo userInfo,
			ZeppaUserToUserRelationship relationship,
			GoogleAccountCredential credential) {
		this.loadedTags = false;
		this.loadedMutualFriendRelationships = false;

		this.userInfo = userInfo;
		this.relationship = relationship;
		this.userImage = null;
		this.tagManagers = new ArrayList<DefaultEventTagMediator>();
		this.mutualFriends = new ArrayList<ZeppaUserToUserRelationship>();

		init();
		loadImageInAsync(userInfo.getImageUrl());
		loadMutualFriendRelationshipsInAsync();
		loadEventTagManagers();
	}

	@Override
	public String getGivenName() {
		return userInfo.getGivenName();
	}

	@Override
	public String getFamilyName() {
		return userInfo.getFamilyName();
	}

	@Override
	public String getDisplayName() {
		return getGivenName() + " " + getFamilyName();
	}

	@Override
	public String getGmail() {
		return userInfo.getGoogleAccountEmail();
	}

	@Override
	public Long getUserId() {
		return userInfo.getKey().getId();
	}

	/**
	 * Converts a view to display info of a connected friend
	 * 
	 * @param convertView
	 * @throws NullPointerException
	 */
	public View convertFriendListItemView(View convertView)
			throws NullPointerException {
		TextView userName = (TextView) convertView
				.findViewById(R.id.contacts_listitem_name);
		ImageView image = (ImageView) convertView
				.findViewById(R.id.contacts_listitem_picture);

		userName.setText(getDisplayName());
		setImageWhenReady(image);

		return convertView;
	}

	/**
	 * Converts an view to display info for a contact to send info
	 * 
	 * @param convertView
	 *            , assumes inflated from R.layout.view_invitelist_item
	 * @throws NullPointerException
	 */
	public void convertInviteListItemView(View convertView)
			throws NullPointerException {

		TextView contactName = (TextView) convertView
				.findViewById(R.id.inviteitem_contactname);
		ImageView contactImage = (ImageView) convertView
				.findViewById(R.id.inviteitem_contactimage);

		contactName.setText(getDisplayName());
		setImageWhenReady(contactImage);

	}

	/**
	 * 
	 * @param convertView
	 * @throws NullPointerException
	 */
	public void convertConnectListItemView(View convertView)
			throws NullPointerException {

		ImageView image = (ImageView) convertView
				.findViewById(R.id.newcontact_name);
		TextView name = (TextView) convertView
				.findViewById(R.id.newcontact_picture);

		setImageWhenReady(image);
		name.setText(getDisplayName());

	}

	/**
	 * This method converts an activity item object for this user
	 * 
	 * @param convertView
	 * @throws NullPointerException
	 */
	public void convertNotificationsListAdapter(View convertView)
			throws NullPointerException {

		ImageView image = (ImageView) convertView
				.findViewById(R.id.notificationitem_userimage);
		setImageWhenReady(image);

	}

	public boolean isFriend() {
		if (relationship == null)
			return false;
		return relationship.getRelationshipType().intValue() == 0;
	}

	public List<DefaultEventTagMediator> getEventTagManagers() {
		return tagManagers;
	}

	private void loadMutualFriendRelationshipsInAsync() {
		// Object[] params = { userInfo.getZeppaUserId(),
		// ZeppaUserSingleton.getInstance().getUserId() };
		// new AsyncTask<Object, Void, Boolean>() {
		//
		// @Override
		// protected Boolean doInBackground(Object... params) {
		//
		//
		//
		// return null;
		// }
		//
		// @Override
		// protected void onPostExecute(Boolean result) {
		// // TODO Auto-generated method stub
		// super.onPostExecute(result);
		// }
		//
		// }.execute(params);
	}

	/**
	 * This method loads the event tag managers for a given user.</p> Method
	 * does open a thread.
	 */
	private void loadEventTagManagers() {

		// Object[] params = { userInfo.getZeppaUserId() };
		// new AsyncTask<Object, Void, Boolean>() {
		//
		// @Override
		// protected Boolean doInBackground(Object... params) {
		// // TODO Auto-generated method stub
		// return null;
		// }
		//
		// @Override
		// protected void onPostExecute(Boolean result) {
		// // TODO Auto-generated method stub
		// super.onPostExecute(result);
		// }
		//
		// }.execute(params);

	}

	/**
	 * 
	 * 
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		}
	}

	public Intent getToUserIntent(Context context) {
		Intent intent = new Intent(context, UserActivity.class);
		intent.putExtra(Constants.INTENT_ZEPPA_USER_ID, getUserId().longValue());

		return intent;
	}

}
