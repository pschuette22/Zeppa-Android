package com.minook.zeppa.mediator;

import java.util.ArrayList;
import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.activity.MinglerActivity;
import com.minook.zeppa.runnable.InsertZeppaUserToUserRelationshipRunnable;
import com.minook.zeppa.runnable.RemoveUserRelationshipRunnable;
import com.minook.zeppa.runnable.ThreadManager;
import com.minook.zeppa.runnable.UpdateUserToUserRelationshipRunnable;
import com.minook.zeppa.singleton.EventTagSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppauserinfoendpoint.model.ZeppaUserInfo;
import com.minook.zeppa.zeppausertouserrelationshipendpoint.model.ZeppaUserToUserRelationship;

public class DefaultUserInfoMediator extends AbstractZeppaUserMediator {

	private ZeppaUserInfo userInfo;
	private ZeppaUserToUserRelationship relationship;
	private List<Long> minglingUserIds;
	private boolean hasLoadedInitialTags;

	// Hold info regarding loading tags

	/**
	 * Constructs a new DefaultUserInfoMediator, the class which manages a held
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
			ZeppaUserToUserRelationship relationship) {
		super();

		this.userInfo = userInfo;
		this.relationship = relationship;
		this.userImage = null;
		this.minglingUserIds = new ArrayList<Long>();
		this.hasLoadedInitialTags = false;

		try {
			loadImageInAsync(userInfo.getImageUrl());
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
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
		return userInfo.getKey().getParent().getId();
	}

	@Override
	public String getUnformattedPhoneNumber() throws NullPointerException {
		String phoneNumber = userInfo.getPrimaryUnformattedNumber();
		if (phoneNumber == null || phoneNumber.isEmpty()) {
			throw new NullPointerException();
		}
		return phoneNumber;
	}

	@Override
	protected String getImageUrl() {
		// TODO Auto-generated method stub
		return userInfo.getImageUrl();
	}

	public boolean hasLoadedInitialTags() {
		return hasLoadedInitialTags;
	}

	public void setHasLoadedInitialTags(boolean hasLoadedInitialTags) {
		this.hasLoadedInitialTags = hasLoadedInitialTags;
	}

	public ZeppaUserToUserRelationship getUserRelationship() {
		return this.relationship;
	}

	public void setUserRelationship(ZeppaUserToUserRelationship relationship) {
		this.relationship = relationship;
	}

	public void setMinglingWithIds(List<Long> minglingUserIds) {
		this.minglingUserIds = minglingUserIds;
	}

	public List<Long> getMinglingWithIds() {
		return minglingUserIds;
	}

	public boolean isMingling() {
		if (relationship == null) {
			return false;
		}
		return relationship.getRelationshipType().equals("MINGLING");
	}

	public boolean requestPending() {
		if (relationship == null)
			return false;
		else
			return relationship.getRelationshipType().equals("PENDING_REQUEST");
	}

	public boolean didSendRequest() {
		return (relationship.getCreatorId().longValue() == ZeppaUserSingleton
				.getInstance().getUserId().longValue());
	}

	/**
	 * Converts a view to display info of a connected friend
	 * 
	 * @param convertView
	 * @throws NullPointerException
	 */
	public View convertFriendListItemView(
			AuthenticatedFragmentActivity context, View convertView)
			throws NullPointerException {

		convertView.setTag(this);
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
	public View convertInviteListItemView(View convertView)
			throws NullPointerException {

		convertView.setTag(this);
		TextView contactName = (TextView) convertView
				.findViewById(R.id.inviteitem_contactname);
		ImageView contactImage = (ImageView) convertView
				.findViewById(R.id.inviteitem_contactimage);

		contactName.setText(getDisplayName());
		setImageWhenReady(contactImage);

		return convertView;

	}

	/**
	 * 
	 * @param convertView
	 * @throws NullPointerException
	 */
	public View convertRequestConnectListItemView(View convertView)
			throws NullPointerException {

		CheckedTextView requestConnect = (CheckedTextView) convertView
				.findViewById(R.id.newcontact_senditem_button);

		requestConnect.setTag(this);

		if (requestPending()) {
			requestConnect.setChecked(true);
			requestConnect.setText("Requested");
		} else {
			requestConnect.setChecked(false);
			requestConnect.setText("Request");
		}

		return convertView;
	}

	/**
	 * 
	 * @param convertView
	 * @throws NullPointerException
	 */
	public void convertRespondConnectListItemView(
			AuthenticatedFragmentActivity context, View convertView)
			throws NullPointerException {

		ImageButton denyButton = (ImageButton) convertView
				.findViewById(R.id.newcontact_deny_button);
		denyButton.setTag(this);
		ImageButton confirmButton = (ImageButton) convertView
				.findViewById(R.id.newcontact_confirm_button);
		confirmButton.setTag(this);

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

	/**
	 * This returns the current list of held event tag mediators for user
	 * @return held DefaultEventTagMediator's for current user
	 */
	public List<AbstractEventTagMediator> getEventTagMediators() {
		return EventTagSingleton.getInstance().getTagMediatorsForUser(
				getUserId());
	}

	/**
	 * This method spins a thread to remove the UserToUserRelationship
	 * Sets current relationship to null;
	 * TODO: update ui and notify user if transaction unsuccessful;
	 * 
	 * @param credential
	 */
	public void removeRelationship(ZeppaApplication application,
			GoogleAccountCredential credential) {

		try {
			ThreadManager.execute(new RemoveUserRelationshipRunnable(
					application, credential, relationship.getId().longValue()));
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		this.relationship = null;

	}

	/**
	 * Update the UserToUserRelationship from pending to accepted
	 * @param application
	 * @param credential
	 */
	public void acceptMingleRequest(ZeppaApplication application,
			GoogleAccountCredential credential) {
		try {
			this.relationship.setRelationshipType("MINGLING");
			ThreadManager.execute(new UpdateUserToUserRelationshipRunnable(
					application, credential, relationship));
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create a new UserToUserRelationship to this user and spin thread to insert it.
	 * TODO: update realtionship and notify of bad response if transaction is unsuccessful
	 * @param application
	 * @param credential
	 */
	public void sendMingleRequest(ZeppaApplication application,
			GoogleAccountCredential credential) {

		relationship = new ZeppaUserToUserRelationship();
		relationship.setCreatorId(ZeppaUserSingleton.getInstance().getUserId()
				.longValue());
		relationship.setSubjectId(getUserId().longValue());
		relationship.setRelationshipType("PENDING_REQUEST");

		ThreadManager.execute(new InsertZeppaUserToUserRelationshipRunnable(
				application, credential, relationship));

	}

	/**
	 * Navigate user to text message conversations to this user
	 * @param context
	 */
	public void sendTextMessage(Context context) {
		try {
		Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", getPrimaryPhoneNumber(), null));
		context.startActivity(smsIntent);
		} catch (ActivityNotFoundException e){
			Toast.makeText(context, "Can't send SMS", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Navigate user to default email client writing a message to this user
	 * @param context
	 * @param emailSubject
	 */
	public void sendEmail(Context context, String emailSubject) {
		Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
				"mailto", getGmail(), null));
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
		context.startActivity(Intent.createChooser(emailIntent, "Email "
				+ getGivenName()));
	}

	/**
	 * This method returns an intent that points to UserActivity with this
	 * users' id attached
	 * 
	 * @param context
	 * @return intent
	 */
	public Intent getToUserIntent(Context context) {
		Intent intent = new Intent(context, MinglerActivity.class);
		intent.putExtra(Constants.INTENT_ZEPPA_USER_ID, getUserId());
		return intent;
	}

}
