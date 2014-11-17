package com.minook.zeppa.mediator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.activity.UserActivity;
import com.minook.zeppa.adapter.tagadapter.FriendTagAdapter;
import com.minook.zeppa.singleton.EventTagSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.task.ConfirmMingleRequestTask;
import com.minook.zeppa.task.RequestMingleTask;
import com.minook.zeppa.zeppauserinfoendpoint.model.ZeppaUserInfo;
import com.minook.zeppa.zeppausertouserrelationshipendpoint.Zeppausertouserrelationshipendpoint;
import com.minook.zeppa.zeppausertouserrelationshipendpoint.model.ZeppaUserToUserRelationship;

public class DefaultUserInfoMediator extends AbstractZeppaUserMediator
		implements OnClickListener {

	private ZeppaUserInfo userInfo;
	private ZeppaUserToUserRelationship relationship;
	private List<Long> minglingWithIds;

	// Hold info regarding loading tags
	private boolean loadingTags;
	private boolean loadedTags;
	private FriendTagAdapter waitingAdapter;

	private boolean loadedMutualFriendRelationships;

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

		this.loadingTags = false;
		this.loadedTags = false;
		this.loadedMutualFriendRelationships = false;

		this.userInfo = userInfo;
		this.relationship = relationship;
		this.userImage = null;
		this.minglingWithIds = new ArrayList<Long>();

		loadImageInAsync(userInfo.getImageUrl());

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
	public String getUnformattedPhoneNumber() throws NullPointerException{
		String phoneNumber = userInfo.getPrimaryUnformatedNumber();
		if(phoneNumber == null || phoneNumber.isEmpty()){
			throw new NullPointerException();
		}
		return phoneNumber;
	}

	public ZeppaUserToUserRelationship getUserRelationship() {
		return this.relationship;
	}

	public void setUserRelationship(ZeppaUserToUserRelationship relationship) {
		this.relationship = relationship;
	}

	/**
	 * 
	 * 
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.newcontact_senditem_button:
			if (requestPending()) {
				// Remove the pending request to a given user
				try {
					GoogleAccountCredential credential = getGoogleAccountCredential();
					removeRequestInAsync(credential);
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			} else {
				// Send a request to the selected user
				sendConnectRequestInAsync((CheckBox) v);
			}
			break;

		case R.id.newcontact_confirm_button:
			confirmRequestInAsync();
			break;

		case R.id.newcontact_deny_button:
			try {
				removeRequestInAsync(getGoogleAccountCredential());
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			break;

		case R.id.contacts_listitem:
			navigateToUserPage();
			break;

		}
	}

	public boolean isConnected() {
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

	public void navigateToUserPage() {
		Intent toUserPage = getToUserIntent(getContext());
		getContext().startActivity(toUserPage);
		getContext().overridePendingTransition(R.anim.slide_left_in,
				R.anim.slide_left_out);

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
		setContext(context);
		TextView userName = (TextView) convertView
				.findViewById(R.id.contacts_listitem_name);
		ImageView image = (ImageView) convertView
				.findViewById(R.id.contacts_listitem_picture);

		userName.setText(getDisplayName());
		setImageWhenReady(image);

		convertView.setOnClickListener(this);

		return convertView;
	}

	/**
	 * Converts an view to display info for a contact to send info
	 * 
	 * @param convertView
	 *            , assumes inflated from R.layout.view_invitelist_item
	 * @throws NullPointerException
	 */
	public void convertInviteListItemView(
			AuthenticatedFragmentActivity context, View convertView)
			throws NullPointerException {

		setContext(context);
		TextView contactName = (TextView) convertView
				.findViewById(R.id.inviteitem_contactname);
		ImageView contactImage = (ImageView) convertView
				.findViewById(R.id.inviteitem_contactimage);

		contactName.setText(getDisplayName());
		setImageWhenReady(contactImage);

	}

	private void convertConnectListItemView(
			AuthenticatedFragmentActivity context, View convertView) {
		setContext(context);
		ImageView image = (ImageView) convertView
				.findViewById(R.id.newcontact_picture);
		TextView name = (TextView) convertView
				.findViewById(R.id.newcontact_name);

		setImageWhenReady(image);
		name.setText(getDisplayName());
	}

	/**
	 * 
	 * @param convertView
	 * @throws NullPointerException
	 */
	public void convertRequestConnectListItemView(
			AuthenticatedFragmentActivity context, View convertView)
			throws NullPointerException {
		convertConnectListItemView(context, convertView);
		CheckBox requestConnect = (CheckBox) convertView
				.findViewById(R.id.newcontact_senditem_button);

		requestConnect.setChecked(requestPending());
		requestConnect.setOnClickListener(this);
	}

	/**
	 * 
	 * @param convertView
	 * @throws NullPointerException
	 */
	public void convertRespondConnectListItemView(
			AuthenticatedFragmentActivity context, View convertView)
			throws NullPointerException {
		convertConnectListItemView(context, convertView);
		Button denyButton = (Button) convertView
				.findViewById(R.id.newcontact_deny_button);
		Button confirmButton = (Button) convertView
				.findViewById(R.id.newcontact_confirm_button);

		denyButton.setOnClickListener(this);
		confirmButton.setOnClickListener(this);
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

	public List<AbstractEventTagMediator> getEventTagMediators() {
		return EventTagSingleton.getInstance().getTagMediatorsForUser(
				getUserId());
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
	private void loadEventTagMediators() {

		Object[] params = { getUserId() };
		new AsyncTask<Object, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Object... params) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
			}

		}.execute(params);

	}

	private void sendConnectRequestInAsync(CheckBox callback) {

		new RequestMingleTask(getGoogleAccountCredential(), callback,
				ZeppaUserSingleton.getInstance().getUserMediator(), this)
				.execute();

	}

	private void removeRequestInAsync(GoogleAccountCredential credential) {

		Object params[] = { credential };
		new AsyncTask<Object, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Object... params) {
				Boolean success = Boolean.FALSE;

				GoogleAccountCredential credential = (GoogleAccountCredential) params[0];
				Zeppausertouserrelationshipendpoint.Builder builder = new Zeppausertouserrelationshipendpoint.Builder(
						AndroidHttp.newCompatibleTransport(),
						AndroidJsonFactory.getDefaultInstance(), credential);
				builder = CloudEndpointUtils.updateBuilder(builder);
				Zeppausertouserrelationshipendpoint endpoint = builder.build();

				try {

					endpoint.removeZeppaUserToUserRelationship(
							relationship.getId()).execute();
					
					success = Boolean.TRUE;
				} catch (IOException ex) {
					ex.printStackTrace();
					success = Boolean.FALSE;
				}
				return success;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);

				if (result) {
					relationship = null;
				} else {

					// TODO: let them know the transaction failed
				}

			}

		}.execute(params);

	}

	private void confirmRequestInAsync() {

		new ConfirmMingleRequestTask(getGoogleAccountCredential(), this)
				.execute();

	}

	/**
	 * this method fetches all instances of eventTags for a given users
	 * 
	 * @return
	 */

	public void verifyAllTagsAreShown(FriendTagAdapter adapter) {
		this.waitingAdapter = adapter;

		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {

				return null;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
			}

		}.execute();
	}

	/**
	 * This method returns an intent that points to UserActivity with this
	 * users' id attached
	 * 
	 * @param context
	 * @return intent
	 */
	public Intent getToUserIntent(Context context) {
		Intent intent = new Intent(context, UserActivity.class);
		intent.putExtra(Constants.INTENT_ZEPPA_USER_ID, getUserId().longValue());
		return intent;
	}

}
