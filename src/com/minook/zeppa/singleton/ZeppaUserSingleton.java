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
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.adapter.MinglerFinderAdapter;
import com.minook.zeppa.adapter.MinglerListAdapter;
import com.minook.zeppa.mediator.AbstractZeppaUserMediator;
import com.minook.zeppa.mediator.DefaultUserInfoMediator;
import com.minook.zeppa.mediator.DefaultZeppaEventMediator;
import com.minook.zeppa.mediator.MyZeppaUserMediator;
import com.minook.zeppa.observer.OnLoadListener;
import com.minook.zeppa.task.FindMinglersTask;
import com.minook.zeppa.zeppauserendpoint.Zeppauserendpoint;
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUser;
import com.minook.zeppa.zeppauserinfoendpoint.Zeppauserinfoendpoint;
import com.minook.zeppa.zeppauserinfoendpoint.model.ZeppaUserInfo;
import com.minook.zeppa.zeppausertouserrelationshipendpoint.Zeppausertouserrelationshipendpoint;
import com.minook.zeppa.zeppausertouserrelationshipendpoint.Zeppausertouserrelationshipendpoint.ListZeppaUserToUserRelationship;
import com.minook.zeppa.zeppausertouserrelationshipendpoint.model.CollectionResponseZeppaUserToUserRelationship;
import com.minook.zeppa.zeppausertouserrelationshipendpoint.model.ZeppaUserToUserRelationship;

public class ZeppaUserSingleton {
	private static ZeppaUserSingleton singleton;

	private final String TAG = getClass().getName();

	private MyZeppaUserMediator userMediator;
	private List<DefaultUserInfoMediator> heldUserMediators;
	private List<OnLoadListener> loadListeners;
	private Date lastFindMinglersTaskExecutionDate;
	private List<MinglerListAdapter> minglerAdapters;
	private boolean hasLoadedInitial;

	/*
	 * Waiting adapters. These will be notified as user mediators are added
	 */

	private MinglerFinderAdapter finderAdapter;
	private MinglerListAdapter minglerListAdapter;

	/*
	 * Instance should only be called once
	 */

	private ZeppaUserSingleton() {
		userMediator = null;
		hasLoadedInitial = false;
		lastFindMinglersTaskExecutionDate = null;
		heldUserMediators = new ArrayList<DefaultUserInfoMediator>();
		loadListeners = new ArrayList<OnLoadListener>();
		minglerAdapters = new ArrayList<MinglerListAdapter>();

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

	public void registerWaitingMinglerListAdapter(MinglerListAdapter adapter) {
		this.minglerListAdapter = adapter;
	}

	public void unregisterWaitingMinglerListAdapter(MinglerListAdapter adapter) {
		if (minglerListAdapter != null && minglerListAdapter == adapter) {
			this.minglerListAdapter = null;
		}
	}

	public void registerWaitingFinderAdapter(MinglerFinderAdapter adapter) {
		this.finderAdapter = adapter;
	}

	public void unregisterWaitingFinderAdapter(MinglerFinderAdapter adapter) {
		if (finderAdapter != null && finderAdapter == adapter) {
			this.finderAdapter = null;
		}
	}

	public void executeFindMinglerTask(Context context,
			GoogleAccountCredential credential, OnLoadListener listener) {
		lastFindMinglersTaskExecutionDate = new Date();
		new FindMinglersTask(context, credential, listener).execute();
	}

	/**
	 * Create a new defaultUserInfoMediator at runtime and add it to the friends
	 * list
	 * 
	 * @param userInfo
	 * @param relationship
	 * @param credential
	 */
	public synchronized void addDefaultZeppaUserMediator(
			ZeppaUserInfo userInfo, ZeppaUserToUserRelationship relationship,
			boolean notify) {

		if (getAbstractUserMediatorById(userInfo.getId()) == null) {

			heldUserMediators.add(new DefaultUserInfoMediator(userInfo,
					relationship));

			if (notify) {
				notifyAdaptersMinglerListChanged();
			}
		}

	}

	public void addAllPotentialMinglers(GoogleAccountCredential credential,
			List<ZeppaUserInfo> userInfoList, boolean notify) {

		Iterator<ZeppaUserInfo> iterator = userInfoList.iterator();
		while (iterator.hasNext()) {
			addDefaultZeppaUserMediator(iterator.next(), null, false);
			if (notify) {
				notifyAdaptersMinglerListChanged();
			}
		}

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

	/**
	 * Register an adapter to listen for changes to mingler list
	 * 
	 * @param adapter
	 */
	public void registerMinglerAdapter(MinglerListAdapter adapter) {
		if (!this.minglerAdapters.contains(adapter)) {
			minglerAdapters.add(adapter);
		}
	}

	/**
	 * Unregister adapter listening to changes in list of minglers
	 * 
	 * @param adapter
	 */
	public void unregisterMinglerAdapter(MinglerListAdapter adapter) {
		minglerAdapters.remove(adapter);
	}

	private void notifyAdaptersMinglerListChanged() {

		if (minglerAdapters.isEmpty()) {
			Iterator<MinglerListAdapter> iterator = minglerAdapters.iterator();

			while (iterator.hasNext()) {
				iterator.next().notifyDataSetChanged();
			}
		}

		if (finderAdapter != null) {
			notifyFinderAdapterDataChanged();
		}
	}

	public void notifyFinderAdapterDataChanged() {
		this.finderAdapter.notifyDataSetChanged();
	}

	/*
	 * Getters
	 */

	public boolean hasLoadedInitial() {
		return hasLoadedInitial;
	}

	public Date getLastMinglerFinderTaskExecutionDate() {
		return lastFindMinglersTaskExecutionDate;
	}

	public MyZeppaUserMediator getUserMediator() {
		return userMediator;
	}

	public Long getUserId() {
		return userMediator.getUserId();
	}

	public String getGoogleCalendarId() {
		return userMediator.getZeppaCalendarId();
	}

	public ArrayList<DefaultUserInfoMediator> getFriendsFrom(List<Long> list) {
		ArrayList<DefaultUserInfoMediator> friends = new ArrayList<DefaultUserInfoMediator>();

		Iterator<DefaultUserInfoMediator> iterator = heldUserMediators
				.iterator();

		while (iterator.hasNext()) {
			DefaultUserInfoMediator userInfoMediator = iterator.next();
			if (userInfoMediator.isConnected()
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

			if (infoMediator.isConnected())
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

			// If the user is
			if (!mediator.isConnected()
					&& (!mediator.requestPending() || (mediator
							.requestPending() && mediator.didSendRequest()))) {
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

			if (mediator.requestPending() && !mediator.didSendRequest()) {
				pendingRequests.add(mediator);
			}
		}

		return pendingRequests;
	}

	public AbstractZeppaUserMediator getAbstractUserMediatorById(Long userId) {

		if (userId.longValue() == userMediator.getUserId().longValue()) {
			return userMediator;
		}

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

	public MyZeppaUserMediator fetchLoggedInUserWithBlocking(
			GoogleAccountCredential credential) throws IOException {

		if (userMediator != null) {
			return userMediator;
		}

		Zeppauserendpoint endpoint = buildZeppaUserEndpoint(credential);

		ZeppaUser zeppaUser = endpoint.fetchCurrentZeppaUser().execute();

		if (zeppaUser == null) {
			return null;
		}

		MyZeppaUserMediator mediator = new MyZeppaUserMediator(zeppaUser);
		this.userMediator = mediator;
		return userMediator;

	}

	/**
	 * This creates
	 * 
	 * @param userId
	 * @param credential
	 * @return true if user is now held
	 */
	public boolean fetchUserAndRelationshipWithBlocking(Long userId,
			GoogleAccountCredential credential, boolean updateRelationship) {

		boolean success = false;

		AbstractZeppaUserMediator mediator = getAbstractUserMediatorById(userId);

		if (mediator == null) {
			Zeppauserinfoendpoint infoEndpoint = buildUserInfoEndpoint(credential);

			try {
				ZeppaUserInfo userInfo = infoEndpoint
						.fetchZeppaUserInfoByParentId(userId).execute();
				if (userInfo != null) {

					ZeppaUserToUserRelationship relationship = fetchRelationshipToUserWithBlocking(
							credential, userId);

					addDefaultZeppaUserMediator(userInfo, relationship, true);
					success = true;
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		} else if (mediator instanceof DefaultUserInfoMediator && updateRelationship){
			
			// Update the current state of the relationship
			try {
				ZeppaUserToUserRelationship relationship = fetchRelationshipToUserWithBlocking(
						credential, mediator.getUserId());
				((DefaultUserInfoMediator) mediator).setUserRelationship(relationship);
				success = true;
			} catch (IOException e){
				e.printStackTrace();
			}
			
		}

		return success;
	}
	

	private Zeppauserinfoendpoint buildUserInfoEndpoint(
			GoogleAccountCredential credential) {
		Zeppauserinfoendpoint.Builder builder = new Zeppauserinfoendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				GsonFactory.getDefaultInstance(), credential);
		builder = CloudEndpointUtils.updateBuilder(builder);
		Zeppauserinfoendpoint endpoint = builder.build();
		return endpoint;
	}

	private Zeppauserendpoint buildZeppaUserEndpoint(
			GoogleAccountCredential credential) {
		Zeppauserendpoint.Builder builder = new Zeppauserendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				GsonFactory.getDefaultInstance(), credential);
		builder = CloudEndpointUtils.updateBuilder(builder);
		Zeppauserendpoint endpoint = builder.build();
		return endpoint;
	}

	private Zeppausertouserrelationshipendpoint buildZeppaUserToUserRelationshipEndpoint(
			GoogleAccountCredential credential) {
		Zeppausertouserrelationshipendpoint.Builder builder = new Zeppausertouserrelationshipendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				GsonFactory.getDefaultInstance(), credential);
		builder = CloudEndpointUtils.updateBuilder(builder);
		Zeppausertouserrelationshipendpoint endpoint = builder.build();
		return endpoint;
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
	 * @param credential
	 */

	public void loadConnectedUsers(Context context,
			GoogleAccountCredential credential, Long userId) {
		Object[] params = { context, credential, userId };

		new AsyncTask<Object, Void, Void>() {

			private Context context;
			private GoogleAccountCredential credential;

			@Override
			protected Void doInBackground(Object... params) {
				context = (Context) params[0];
				credential = (GoogleAccountCredential) params[1];
				Long userId = (Long) params[2];

				Zeppausertouserrelationshipendpoint endpoint = buildZeppaUserToUserRelationshipEndpoint(credential);

				/*
				 * The following protocol executes a query and continues to do
				 * so until all values have been returned
				 */

				List<ZeppaUserToUserRelationship> relationships = new ArrayList<ZeppaUserToUserRelationship>();

				String filter = "creatorId == " + userId.longValue();
				String cursor = null;
				ListZeppaUserToUserRelationship listInfotask = null;

				do {

					try {
						listInfotask = endpoint
								.listZeppaUserToUserRelationship();

						Log.d(TAG, "Filter: " + filter);
						Log.d(TAG, "Cursor: " + cursor);
						listInfotask.setFilter(filter);
						listInfotask.setCursor(cursor);
						listInfotask.setLimit(25);

						CollectionResponseZeppaUserToUserRelationship response = listInfotask
								.execute();

						if (response == null || response.getItems() == null
								|| response.getItems().isEmpty()) {
							cursor = null;
						} else {
							relationships.addAll(response.getItems());
							filter = null;
							cursor = response.getNextPageToken();
						}

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} while (cursor != null);

				filter = "subjectId == " + userId.longValue();
				cursor = null;
				do {

					try {
						listInfotask = endpoint
								.listZeppaUserToUserRelationship();

						Log.d(TAG, "Filter: " + filter);
						Log.d(TAG, "Cursor: " + cursor);
						listInfotask.setFilter(filter);
						listInfotask.setCursor(cursor);
						listInfotask.setLimit(25);

						CollectionResponseZeppaUserToUserRelationship response = listInfotask
								.execute();

						if (response == null || response.getItems() == null
								|| response.getItems().isEmpty()) {
							cursor = null;
						} else {
							relationships.addAll(response.getItems());
							filter = null;
							cursor = response.getNextPageToken();
						}

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} while (cursor != null);

				if (!relationships.isEmpty()) {
					fetchUsersByRelationshipCollection(relationships,
							credential, userId);
					// TODO: error check for those who were not loaded properly
					// and flag internally

				}

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				hasLoadedInitial = true;
				onFinishLoad();

				// Once Connected Users are loaded, load notifications
				NotificationSingleton.getInstance()
						.loadInitialNotificationsInAsync(credential,
								getUserId());

				// new FindMinglersTask(context, credential).execute();

			}

		}.execute(params);

	}

	/**
	 * This method takes a collection of relationships and loads the
	 * corresponding UserInfo then creates a list of default user info
	 * 
	 * @param relationships
	 *            - list of loaded relationships
	 * @param credential
	 *            - authenticated credential
	 * @param userId
	 *            - userId of the current user
	 * @return successfullyLoaded - list of successfully loaded
	 *         DefaultUserInfoMediators
	 */
	private void fetchUsersByRelationshipCollection(
			List<ZeppaUserToUserRelationship> relationships,
			GoogleAccountCredential credential, Long userId) {

		Zeppauserinfoendpoint endpoint = buildUserInfoEndpoint(credential);

		Iterator<ZeppaUserToUserRelationship> iterator = relationships
				.iterator();

		boolean listDidChange = false;
		while (iterator.hasNext()) {

			ZeppaUserToUserRelationship relationship = iterator.next();
			try {
				ZeppaUserInfo info;

				if (relationship.getCreatorId().equals(userId)
						&& !relationship.getSubjectId().equals(userId)) {

					info = endpoint.fetchZeppaUserInfoByParentId(
							relationship.getSubjectId()).execute();

				} else if (!relationship.getCreatorId().equals(userId)
						&& relationship.getSubjectId().equals(userId)) {
					// TODO: fetch creator
					info = endpoint.fetchZeppaUserInfoByParentId(
							relationship.getCreatorId()).execute();
				} else {
					Log.wtf(TAG, "Relationship held that shouldnt be here");
					continue;
				}

				if (info != null) {
					addDefaultZeppaUserMediator(info, relationship, false);
					listDidChange = true;
				}
			} catch (IOException e) {
				e.printStackTrace();
				// Handle IOexception, Likely 404 not found.
			} catch (NullPointerException e) {
				e.printStackTrace();
				// TODO: flag internally, should not happen but dont let it
				// crash app
			}

		}

		if (listDidChange) {
			notifyAdaptersMinglerListChanged();
		}

	}

	/**
	 * Retrieves current users relationship to another if exists
	 * 
	 * @param credential
	 * @param userId
	 * @return relationship or null
	 * @throws IOException
	 */
	private ZeppaUserToUserRelationship fetchRelationshipToUserWithBlocking(
			GoogleAccountCredential credential, Long userId) throws IOException {
		ZeppaUserToUserRelationship relationship = null;

		Zeppausertouserrelationshipendpoint relationshipEndpoint = buildZeppaUserToUserRelationshipEndpoint(credential);

		ListZeppaUserToUserRelationship task = relationshipEndpoint
				.listZeppaUserToUserRelationship();
		task.setLimit(Integer.valueOf(1)); // Should be a single
											// instance
		task.setFilter("(creatorId == " + getUserId().longValue()
				+ " || creatorId == " + userId.longValue()
				+ ") && (subjectId == " + getUserId().longValue()
				+ " || subjectId == " + userId.longValue() + ")");
		CollectionResponseZeppaUserToUserRelationship response = task.execute();

		if (response != null && response.getItems() != null
				&& !response.isEmpty()) {
			relationship = response.getItems().get(0);
		}

		return relationship;
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

	public boolean emailIsRecognized(String email) {

		if (getUserMediator().getGmail().equalsIgnoreCase(email)) {
			return true;
		}

		Iterator<DefaultUserInfoMediator> iterator = heldUserMediators
				.iterator();

		while (iterator.hasNext()) {
			DefaultUserInfoMediator mediator = iterator.next();
			if (mediator.getGmail().equalsIgnoreCase(email)) {
				return true;
			}
		}

		return false;
	}

	public boolean numberIsRecognized(String number) {
		try {
			if (getUserMediator().getPrimaryPhoneNumber().equals(number)) {
				return true;
			}
		} catch (NullPointerException e) {
			// This user does not have a phone number attached to their account
			return false;
		}

		Iterator<DefaultUserInfoMediator> iterator = heldUserMediators
				.iterator();

		while (iterator.hasNext()) {

			try {
				DefaultUserInfoMediator mediator = iterator.next();
				String primaryNumber = mediator.getPrimaryPhoneNumber();
				if (primaryNumber != null && primaryNumber.equals(number))
					return true;
			} catch (NullPointerException e) {
				// User may not have a phone number attached to their account
			}
		}

		return false;
	}
}
