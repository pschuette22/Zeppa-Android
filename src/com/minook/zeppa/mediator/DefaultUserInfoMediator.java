package com.minook.zeppa.mediator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.minook.zeppa.utils.Utils;
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
			ZeppaUserToUserRelationship relationship,
			GoogleAccountCredential credential) {
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
	public String getPrimaryPhoneNumber() {
		return Utils.formatPhoneNumber(userInfo.getPrimaryUnformatedNumber());
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
				removeRequestInAsync();
			} else {
				sendConnectRequestInAsync();
			}
			break;

		case R.id.newcontact_confirm_button:
			confirmRequestInAsync();
			break;

		case R.id.newcontact_deny_button:
			removeRequestInAsync();
			break;

		case R.id.contacts_listitem:
			navigateToUserPage();
			break;

		}
	}

	public boolean isConnected() {
		return relationship.getRelationshipType().intValue() == 0;
	}

	public boolean requestPending() {
		if (relationship == null)
			return false;
		else
			return relationship.getRelationshipType().intValue() == 1;
	}

	public boolean didSendRequest() {
		return (relationship.getConnectionRequesterId().longValue() == ZeppaUserSingleton
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
		ImageView denyButton = (ImageView) convertView
				.findViewById(R.id.newcontact_deny_button);
		ImageView confirmButton = (ImageView) convertView
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

	public boolean isFriend() {
		if (relationship == null)
			return false;
		return relationship.getRelationshipType().intValue() == 0;
	}

	public List<AbstractEventTagMediator> getEventTagMediators() {
		return EventTagSingleton.getInstance().getTagMediatorsForUser(getUserId());
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

	private void sendConnectRequestInAsync() {

		new AsyncTask<Void, Void, ZeppaUserToUserRelationship>() {

			@Override
			protected ZeppaUserToUserRelationship doInBackground(Void... params) {

				ZeppaUserToUserRelationship relationship = new ZeppaUserToUserRelationship();
				Long userId = ZeppaUserSingleton.getInstance().getUserId();

				relationship.setTimeCreatedInMillis(System.currentTimeMillis());
				relationship.setTimeUpdatedInMillis(System.currentTimeMillis());
				relationship.setConnectionRequesterId(userId);
				relationship.setRelationshipType(Integer.valueOf(1));
				List<Long> userIdList = new ArrayList<Long>();
				userIdList.add(userId);
				userIdList.add(getUserId());
				relationship.setUserIds(userIdList);

				Zeppausertouserrelationshipendpoint.Builder builder = new Zeppausertouserrelationshipendpoint.Builder(
						AndroidHttp.newCompatibleTransport(),
						AndroidJsonFactory.getDefaultInstance(), getContext()
								.getGoogleAccountCredential());
				builder = CloudEndpointUtils.updateBuilder(builder);
				Zeppausertouserrelationshipendpoint endpoint = builder.build();

				try {

					relationship = endpoint.insertZeppaUserToUserRelationship(
							relationship).execute();

				} catch (IOException ex) {
					ex.printStackTrace();
					relationship = null;
				}

				return relationship;
			}

			@Override
			protected void onPostExecute(ZeppaUserToUserRelationship result) {
				super.onPostExecute(result);

				if (result != null) {
					relationship = result;
					// Check to see if both users requested at the exact same
					// time
				} else {
					// TODO: let them know the transaction was not successful
				}
			}

		}.execute();

	}

	private void removeRequestInAsync() {

		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				Boolean success = Boolean.FALSE;
				Zeppausertouserrelationshipendpoint.Builder builder = new Zeppausertouserrelationshipendpoint.Builder(
						AndroidHttp.newCompatibleTransport(),
						AndroidJsonFactory.getDefaultInstance(), getContext()
								.getGoogleAccountCredential());
				builder = CloudEndpointUtils.updateBuilder(builder);
				Zeppausertouserrelationshipendpoint endpoint = builder.build();

				try {

					endpoint.removeZeppaUserToUserRelationship(
							relationship.getKey().getId()).execute();
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

		}.execute();

	}

	private void confirmRequestInAsync() {

		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {

				Boolean success = Boolean.FALSE;
				Zeppausertouserrelationshipendpoint.Builder builder = new Zeppausertouserrelationshipendpoint.Builder(
						AndroidHttp.newCompatibleTransport(),
						AndroidJsonFactory.getDefaultInstance(), getContext()
								.getGoogleAccountCredential());
				builder = CloudEndpointUtils.updateBuilder(builder);
				Zeppausertouserrelationshipendpoint endpoint = builder.build();

				// Hold a copy just in case the transaction is not successful
				try {

					// Update the local relationship's values

					// Try to update the cloud datastore
					ZeppaUserToUserRelationship result = endpoint
							.updateZeppaUserToUserRelationship(
									relationship.getKey().getId(),
									Integer.valueOf(0),
									Long.valueOf(System.currentTimeMillis()))
							.execute();

					if (result != null) {
						relationship = result;
					}

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
					// TODO: update local datastore and references
				} else {
					// TODO: notify transaction failed
				}
			}

		}.execute();

	}

	
	/**
	 * this method fetches all instances of eventTags for a given users 
	 * @return
	 */

	
	public void verifyAllTagsAreShown(FriendTagAdapter adapter){
		this.waitingAdapter = adapter;
		
		new AsyncTask<Void,Void,Boolean>(){

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
