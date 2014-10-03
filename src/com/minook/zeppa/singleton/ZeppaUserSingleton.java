package com.minook.zeppa.singleton;

/**
 * @author Pete Schuette 
 * Date Created: Thursday March 6th, 2014;
 * 
 * Singleton class update, holding everything in the application context was getting overwhelming
 * 
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAuthIOException;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.Constants;
import com.minook.zeppa.mediator.DefaultUserInfoMediator;
import com.minook.zeppa.mediator.MyZeppaUserMediator;
import com.minook.zeppa.observer.OnLoadListener;
import com.minook.zeppa.utils.Utils;
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUser;
import com.minook.zeppa.zeppauserinfoendpoint.Zeppauserinfoendpoint;
import com.minook.zeppa.zeppauserinfoendpoint.model.CollectionResponseZeppaUserInfo;
import com.minook.zeppa.zeppauserinfoendpoint.model.ZeppaUserInfo;
import com.minook.zeppa.zeppausertouserrelationshipendpoint.Zeppausertouserrelationshipendpoint;
import com.minook.zeppa.zeppausertouserrelationshipendpoint.model.CollectionResponseZeppaUserToUserRelationship;
import com.minook.zeppa.zeppausertouserrelationshipendpoint.model.ZeppaUserToUserRelationship;

public class ZeppaUserSingleton {
	private static ZeppaUserSingleton singleton;

	private final String TAG = getClass().getName();

	private MyZeppaUserMediator userMediator;
	private List<DefaultUserInfoMediator> heldUserMediators;
	private List<OnLoadListener> loadListeners;
	private boolean hasLoadedInitial;
	private boolean hasLoadedPossible;

	/*
	 * Instance should only be called once
	 */

	private ZeppaUserSingleton() {
		userMediator = null;
		hasLoadedInitial = false;
		hasLoadedPossible = false;
		heldUserMediators = new ArrayList<DefaultUserInfoMediator>();
		loadListeners = new ArrayList<OnLoadListener>();

	}

	public static ZeppaUserSingleton getInstance() {
		if (singleton == null)
			singleton = new ZeppaUserSingleton();
		return singleton;
	}

	public void onLowMemory() {

	}

	/*
	 * Setters
	 */
	public void setUser(ZeppaUser user) {
		userMediator = new MyZeppaUserMediator(user);
	}

	/**
	 * Create a new defaultUserInfoMediator at runtime and add it to the friends
	 * list
	 * 
	 * @param userInfo
	 * @param relationship
	 * @param credential
	 */
	public void addFriendZeppaUser(ZeppaUserInfo userInfo,
			ZeppaUserToUserRelationship relationship,
			GoogleAccountCredential credential) {
		heldUserMediators.add(new DefaultUserInfoMediator(userInfo,
				relationship, credential));

	}

	/**
	 * This method sets the contactLoader view which should be hidden when
	 * contacts have loaded
	 * 
	 * @param view
	 */
	public void registerLoadListener(OnLoadListener listener) {
		this.loadListeners.add(listener);
	}

	/*
	 * Getters
	 */

	public boolean hasLoadedInitial() {
		return hasLoadedInitial;
	}

	public MyZeppaUserMediator getUserMediator() {
		return userMediator;
	}

	public Long getUserId() {
		return userMediator.getUserId();
	}

	public ArrayList<DefaultUserInfoMediator> getFriendsFrom(List<Long> list) {
		ArrayList<DefaultUserInfoMediator> friends = new ArrayList<DefaultUserInfoMediator>();

		Iterator<DefaultUserInfoMediator> iterator = heldUserMediators
				.iterator();

		while (iterator.hasNext()) {
			DefaultUserInfoMediator userInfoMediator = iterator.next();
			if (userInfoMediator.isFriend()
					&& list.contains(userInfoMediator.getUserId())) {
				friends.add(userInfoMediator);
			}

		}

		return friends;
	}

	public List<Long> getAllFriendZeppaIds() {
		List<Long> allFriendZeppaIds = new ArrayList<Long>();
		Iterator<DefaultUserInfoMediator> iterator = getFriendInfoMediators()
				.iterator();
		while (iterator.hasNext()) {
			allFriendZeppaIds.add(iterator.next().getUserId());
		}
		return allFriendZeppaIds;
	}

	/**
	 * 
	 * @return friendInfoMediators
	 */
	public List<DefaultUserInfoMediator> getFriendInfoMediators() {
		List<DefaultUserInfoMediator> friendList = new ArrayList<DefaultUserInfoMediator>();

		Iterator<DefaultUserInfoMediator> iterator = heldUserMediators
				.iterator();

		while (iterator.hasNext()) {
			DefaultUserInfoMediator infoMediator = iterator.next();

			if (infoMediator.isFriend())
				friendList.add(infoMediator);
		}

		return friendList;

	}

	public List<DefaultUserInfoMediator> getPossibleFriendInfoMediators() {
		List<DefaultUserInfoMediator> potentialConnectionList = new ArrayList<DefaultUserInfoMediator>();

		Iterator<DefaultUserInfoMediator> iterator = heldUserMediators
				.iterator();

		while (iterator.hasNext()) {
			DefaultUserInfoMediator mediator = iterator.next();

			if (!mediator.isFriend() && 
					(!mediator.requestPending() || // No Pending Requests
					(mediator.requestPending() && mediator.didSendRequest()))) { // Request Pending, Did send it
				potentialConnectionList.add(mediator);
			}
		}

		return potentialConnectionList;
	}

	public List<DefaultUserInfoMediator> getPendingFriendRequests() {
		List<DefaultUserInfoMediator> pendingRequests = new ArrayList<DefaultUserInfoMediator>();

		Iterator<DefaultUserInfoMediator> iterator = heldUserMediators
				.iterator();

		while (iterator.hasNext()) {
			DefaultUserInfoMediator mediator = iterator.next();

			if (!mediator.isFriend() && mediator.requestPending() && !mediator.didSendRequest()) {
					pendingRequests.add(mediator);
			}
		}

		return pendingRequests;
	}

	public DefaultUserInfoMediator getUserFor(Long userId) {
		Iterator<DefaultUserInfoMediator> iterator = heldUserMediators
				.iterator();

		while (iterator.hasNext()) {
			DefaultUserInfoMediator userInfoMediator = iterator.next();
			if (userInfoMediator.getUserId().longValue() == userId.longValue()) {
				return userInfoMediator;
			}
		}

		return null;
	}

	/*
	 * Private methods
	 */

	private boolean isGmailAddress(String email) {
		return email.endsWith("@gmail.com");
	}

	/*
	 * Loader Methods
	 */

	/**
	 * 
	 * This method loads all users who share a relationship with the user
	 * 
	 * It does this by retrieving lists of relationships then fetching the
	 * userInfo directly
	 * 
	 * @param context
	 * @param credential
	 */

	public void loadConnectedUsers(GoogleAccountCredential credential) {
		Object[] params = { credential, getUserId() };

		new AsyncTask<Object, Void, Void>() {

			@Override
			protected Void doInBackground(Object... params) {
				GoogleAccountCredential credential = (GoogleAccountCredential) params[0];
				Long userId = (Long) params[1];

				Zeppausertouserrelationshipendpoint.Builder endpointBuilder = new Zeppausertouserrelationshipendpoint.Builder(
						AndroidHttp.newCompatibleTransport(),
						AndroidJsonFactory.getDefaultInstance(), credential);
				endpointBuilder = CloudEndpointUtils
						.updateBuilder(endpointBuilder);
				Zeppausertouserrelationshipendpoint endpoint = endpointBuilder
						.build();

				boolean keepGoing = true;
				Long lastRelationshipCreatedTime = Long.valueOf(-1);
				while (keepGoing) {
					try {
						CollectionResponseZeppaUserToUserRelationship relationshipCollection = endpoint
								.fetchUserRelationshipList(userId,
										lastRelationshipCreatedTime).execute();

						if (relationshipCollection.getItems() != null
								&& !relationshipCollection.getItems().isEmpty()) {
							Iterator<ZeppaUserToUserRelationship> relationshipIterator = relationshipCollection
									.getItems().iterator();

							while (relationshipIterator.hasNext()) {
								loadUserByRelationship(credential, userId,
										relationshipIterator.next());
							}

							if (relationshipCollection.getItems().size() < 20) {
								keepGoing = false;
							} else {
								lastRelationshipCreatedTime = relationshipCollection
										.getItems().get(19)
										.getTimeCreatedInMillis(); // get last
																	// relationship
																	// object
							}

						} else {
							keepGoing = false;
						}

					} catch (GoogleAuthIOException aEx) {
						Log.wtf(TAG, "AuthException");
						keepGoing = false;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						keepGoing = false;
						e.printStackTrace();
					}

				}

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				hasLoadedInitial = true;
				onFinishLoad();
			}

		}.execute(params);

	}

	/**
	 * This method is called when loads have completed</p> Iterates through list
	 * of listeners and calls onFinishLoad()
	 */
	private void onFinishLoad() {
		Iterator<OnLoadListener> iterator = loadListeners.iterator();
		while (iterator.hasNext()) {
			try {
				iterator.next().onFinishLoad();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method loads the userInfo for someone who shares a relationship with
	 * user
	 * 
	 * 
	 * @param context
	 * @param credential
	 * @param userId
	 * @param relationship
	 */
	private void loadUserByRelationship(GoogleAccountCredential credential,
			Long userId, ZeppaUserToUserRelationship relationship) {
		List<Long> userIds = relationship.getUserIds();

		int index = userIds.indexOf(userId);
		index = (index * -1) + 1;
		Long otherUserId = userIds.get(index);

		Zeppauserinfoendpoint.Builder endpointBuilder = new Zeppauserinfoendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				AndroidJsonFactory.getDefaultInstance(), credential);

		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);

		Zeppauserinfoendpoint endpoint = endpointBuilder.build();

		try {
			ZeppaUserInfo userInfo = endpoint.fetchZeppaUserInfoByParentId(
					otherUserId).execute();

			heldUserMediators.add(new DefaultUserInfoMediator(userInfo,
					relationship, credential));

		} catch (GoogleAuthIOException aEx) {
			Log.wtf(TAG, "AuthException");
		} catch (IOException ioEx) {
			ioEx.printStackTrace();
		}

	}

	public void loadPossible(Context context, GoogleAccountCredential credential) {

		// Get Phone Emails
		ContentResolver resolver = context.getContentResolver();

		Cursor gCursor = resolver.query(
				ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, null,
				null, null);
		List<String> emailList = new ArrayList<String>();
		while (gCursor.moveToNext()) {
			String email = gCursor
					.getString(gCursor
							.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
			if (isGmailAddress(email) && !userIsHeld(email)
					&& !emailList.contains(email)) {
				emailList.add(email);
			}

			if (emailList.size() == 20) {
				fetchForList(credential, emailList);
				emailList.clear();
			}

		}

		if (!emailList.isEmpty()) { // for the last batch, if needed
			fetchForList(credential, emailList);
			emailList.clear();
		}

	}

	private boolean userIsHeld(String email) {
		boolean isHeld = false;

		if(getUserMediator().getGmail().equalsIgnoreCase(email)){
			return true;
		}
		
		Iterator<DefaultUserInfoMediator> iterator = heldUserMediators
				.iterator();

		while (iterator.hasNext()) {
			if (iterator.next().getGmail().equalsIgnoreCase(email))
				return true;
		}

		return isHeld;
	}

	/**
	 * This method fetches a list of userInfoObjects in the datastore which
	 * match to a list of the users held phone contacts. Fetched userInfoItems
	 * are then held in a handler
	 * 
	 * @param context
	 * @param emailList
	 */
	private void fetchForList(GoogleAccountCredential credential,
			List<String> emailList) {

		Zeppauserinfoendpoint.Builder endpointBuilder = new Zeppauserinfoendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				AndroidJsonFactory.getDefaultInstance(), credential);
		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
		Zeppauserinfoendpoint endpoint = endpointBuilder.build();

		String encodedEmails = Utils.encodeListString(emailList);

		try {
			CollectionResponseZeppaUserInfo collectionResponse = endpoint
					.fetchFriendsByEmailList(encodedEmails).execute();
			if (collectionResponse.getItems() != null
					&& !collectionResponse.isEmpty()) {

				Iterator<ZeppaUserInfo> iterator = collectionResponse
						.getItems().iterator();
				while (iterator.hasNext()) {
					Log.d(TAG, "Fetched a potential contact from: "
							+ encodedEmails);
					ZeppaUserInfo userInfo = iterator.next();
					heldUserMediators.add(new DefaultUserInfoMediator(userInfo,
							null, credential));
				}

			}
		} catch (GoogleAuthIOException aEx) {
			Log.wtf(TAG, "AuthException");
		} catch (IOException ioEx) {
			ioEx.printStackTrace();
		}

	}

}
