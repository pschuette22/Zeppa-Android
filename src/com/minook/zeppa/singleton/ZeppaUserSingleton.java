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
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.content.Context;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.PrefsManager;
import com.minook.zeppa.Utils;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.adapter.MinglerFinderAdapter;
import com.minook.zeppa.mediator.AbstractZeppaUserMediator;
import com.minook.zeppa.mediator.DefaultUserInfoMediator;
import com.minook.zeppa.mediator.MyZeppaUserMediator;
import com.minook.zeppa.runnable.FindMinglersRunnable;
import com.minook.zeppa.runnable.ThreadManager;
import com.minook.zeppa.zeppauserendpoint.Zeppauserendpoint;
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUser;
import com.minook.zeppa.zeppauserinfoendpoint.Zeppauserinfoendpoint;
import com.minook.zeppa.zeppauserinfoendpoint.model.ZeppaUserInfo;
import com.minook.zeppa.zeppausertouserrelationshipendpoint.Zeppausertouserrelationshipendpoint;
import com.minook.zeppa.zeppausertouserrelationshipendpoint.Zeppausertouserrelationshipendpoint.ListZeppaUserToUserRelationship;
import com.minook.zeppa.zeppausertouserrelationshipendpoint.model.CollectionResponseZeppaUserToUserRelationship;
import com.minook.zeppa.zeppausertouserrelationshipendpoint.model.ZeppaUserToUserRelationship;

public class ZeppaUserSingleton {

	// Provides an interface for when minglers are loaded by the singleton
	public interface OnMinglersLoadListener {
		public void onMinglersLoaded();
	}

	private static ZeppaUserSingleton singleton;

	// private final String TAG = getClass().getName();

	private MyZeppaUserMediator userMediator;
	private List<DefaultUserInfoMediator> heldUserMediators;
	private Date lastFindMinglersTaskExecutionDate;
	private boolean hasLoadedInitial;

	private List<OnMinglersLoadListener> minglerLoadListeners;

	/*
	 * Waiting adapters. These will be notified as user mediators are added
	 */

	/*
	 * Instance should only be called once
	 */

	private ZeppaUserSingleton() {
		userMediator = null;
		hasLoadedInitial = false;
		lastFindMinglersTaskExecutionDate = null;
		heldUserMediators = new ArrayList<DefaultUserInfoMediator>();
		minglerLoadListeners = new ArrayList<OnMinglersLoadListener>();

	}

	public static ZeppaUserSingleton getInstance() {
		if (singleton == null)
			singleton = new ZeppaUserSingleton();
		return singleton;
	}

	public void restore() {
		singleton = new ZeppaUserSingleton();
	}

	public void clear() {
		heldUserMediators.clear();
	}

	public void onLowMemory() {

	}

	/*
	 * Setters
	 */
	public void setUser(ZeppaUser user) {
		userMediator = new MyZeppaUserMediator(user);
	}

	// public void registerWaitingMinglerListAdapter(MinglerListAdapter adapter)
	// {
	// this.minglerListAdapter = adapter;
	// }
	//
	// public void unregisterWaitingMinglerListAdapter(MinglerListAdapter
	// adapter) {
	// if (minglerListAdapter != null && minglerListAdapter == adapter) {
	// this.minglerListAdapter = null;
	// }
	// }

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

	public MyZeppaUserMediator fetchLoggedInUserByIdWithBlocking(
			GoogleAccountCredential credential, long userId) throws IOException {
		// If mediator already exists, return it. Called erroneously
		if (userMediator != null) {
			return userMediator;
		}

		// build endpoint and execute get task
		Zeppauserendpoint endpoint = buildZeppaUserEndpoint(credential);
		ZeppaUser user = endpoint.getZeppaUser(userId).execute();

		// if returned null object, return null
		if (user == null) {
			return null;
		}
		// if valid ZeppaUser object returned, create new mediator instance
		MyZeppaUserMediator mediator = new MyZeppaUserMediator(user);
		this.userMediator = mediator;
		return mediator;
	}

	public void executeFindMinglerTask(ZeppaApplication application,
			GoogleAccountCredential credential, MinglerFinderAdapter adapter) {
		lastFindMinglersTaskExecutionDate = new Date();
		ThreadManager.execute(new FindMinglersRunnable(application, credential,
				adapter));
	}

	/**
	 * Create a new defaultUserInfoMediator at runtime and add it to the
	 * minglers list
	 * 
	 * @param userInfo
	 * @param relationship
	 * @param credential
	 */
	public void addDefaultZeppaUserMediator(ZeppaUserInfo userInfo,
			ZeppaUserToUserRelationship relationship) {

		AbstractZeppaUserMediator mediator = null;
		try {
			mediator = getAbstractUserMediatorById(userInfo.getKey()
					.getParent().getId().longValue());
			if (mediator != null && mediator instanceof DefaultUserInfoMediator) {
				((DefaultUserInfoMediator) mediator)
						.setUserRelationship(relationship);
				return;
			}
		} catch (NullPointerException e) {
			// e.printStackTrace();
			return;
		}

		if (mediator == null) {

			heldUserMediators.add(new DefaultUserInfoMediator(userInfo,
					relationship));

		}

	}

	public void removeHeldMediatorById(Long userId) {

		if (heldUserMediators == null || heldUserMediators.isEmpty()) {
			return;
		}

		Iterator<DefaultUserInfoMediator> iterator = heldUserMediators
				.iterator();
		DefaultUserInfoMediator result = null;
		while (iterator.hasNext()) {
			DefaultUserInfoMediator mediator = iterator.next();
			if (mediator.getUserId().longValue() == userId.longValue()) {
				result = mediator;
			}
		}

		if (result != null) {
			heldUserMediators.remove(result);
		}

	}

	/**
	 * This method sets the contactLoader view which should be hidden when
	 * contacts have loaded
	 * 
	 * @param view
	 */
	public void registerLoadListener(OnMinglersLoadListener listener) {
		if (!minglerLoadListeners.contains(listener)) {
			this.minglerLoadListeners.add(listener);
		}
	}

	/**
	 * Unregister adapter listening to changes in list of minglers
	 * 
	 * @param adapter
	 */
	public boolean unregisterMinglerLoadListener(OnMinglersLoadListener listener) {
		return minglerLoadListeners.remove(listener);
	}

	public void notifyObservers() {

		if (!minglerLoadListeners.isEmpty()) {
			Iterator<OnMinglersLoadListener> iterator = minglerLoadListeners
					.iterator();

			while (iterator.hasNext()) {
				try {
					iterator.next().onMinglersLoaded();
				} catch (Exception e) {

				}
			}
		}

	}

	/*
	 * Getters
	 */

	public boolean hasLoadedInitial() {
		return hasLoadedInitial;
	}

	public void setHasLoadedInitial() {
		this.hasLoadedInitial = true;
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

	public List<String> getRecognizedNumbers() {
		List<String> numbers = new ArrayList<String>();

		try {
			numbers.add(userMediator.getUnformattedPhoneNumber());
		} catch (NullPointerException e) {

		}

		Iterator<DefaultUserInfoMediator> iterator = heldUserMediators
				.iterator();
		while (iterator.hasNext()) {
			try {
				String number = iterator.next().getUnformattedPhoneNumber();
				numbers.add(number);
			} catch (NullPointerException e) {
				// Number is null
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return numbers;
	}

	public List<String> getRecognizedEmails() {
		List<String> emails = new ArrayList<String>();

		emails.add(userMediator.getGmail());

		Iterator<DefaultUserInfoMediator> iterator = heldUserMediators
				.iterator();
		while (iterator.hasNext()) {
			try {
				emails.add(iterator.next().getGmail());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return emails;
	}

	private boolean listContainsId(List<Long> list, long id) {
		Iterator<Long> iterator = list.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().longValue() == id) {
				return true;
			}
		}

		return false;
	}

	public ArrayList<DefaultUserInfoMediator> getMinglersFrom(List<Long> list) {
		ArrayList<DefaultUserInfoMediator> minglers = new ArrayList<DefaultUserInfoMediator>();

		Iterator<DefaultUserInfoMediator> iterator = heldUserMediators
				.iterator();

		while (iterator.hasNext()) {
			DefaultUserInfoMediator userInfoMediator = iterator.next();
			if (userInfoMediator.isMingling()
					&& listContainsId(list, userInfoMediator.getUserId()
							.longValue())) {
				minglers.add(userInfoMediator);
			}

		}

		Collections.sort(minglers, Utils.USER_COMPARATOR);

		return minglers;
	}

	public List<Long> getAllFriendZeppaIds() {
		List<Long> allFriendZeppaIds = new ArrayList<Long>();
		Iterator<DefaultUserInfoMediator> iterator = getMinglerMediators()
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
	public List<DefaultUserInfoMediator> getMinglerMediators() {
		List<DefaultUserInfoMediator> friendList = new ArrayList<DefaultUserInfoMediator>();

		Iterator<DefaultUserInfoMediator> iterator = heldUserMediators
				.iterator();

		while (iterator.hasNext()) {
			DefaultUserInfoMediator infoMediator = iterator.next();

			if (infoMediator.isMingling())
				friendList.add(infoMediator);
		}
		Collections.sort(friendList, Utils.USER_COMPARATOR);
		return friendList;

	}

	public List<DefaultUserInfoMediator> getPossibleFriendInfoMediators() {
		List<DefaultUserInfoMediator> potentialConnectionList = new ArrayList<DefaultUserInfoMediator>();

		Iterator<DefaultUserInfoMediator> iterator = heldUserMediators
				.iterator();

		while (iterator.hasNext()) {
			DefaultUserInfoMediator mediator = iterator.next();

			// If the user is
			if (!mediator.isMingling()
					&& (!mediator.requestPending() || (mediator
							.requestPending() && mediator.didSendRequest()))) {
				potentialConnectionList.add(mediator);
			}
		}

		Collections.sort(potentialConnectionList, Utils.USER_COMPARATOR);

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

		Collections.sort(pendingRequests, Utils.USER_COMPARATOR);

		return pendingRequests;
	}

	/**
	 * @warning Not Thread Safe
	 * 
	 *          Blocking method that fetches a user's info by id
	 * 
	 * @param userId
	 * @param credential
	 * @return DefaultUserInfoMediator or null
	 */
	public DefaultUserInfoMediator fetchDefaultUserInfoMediatorWithBlocking(
			Context context, long userId, GoogleAccountCredential credential) {
		AbstractZeppaUserMediator mediator = getAbstractUserMediatorById(userId);

		if (mediator != null && mediator instanceof DefaultUserInfoMediator) {
			return (DefaultUserInfoMediator) mediator;
		}

		DefaultUserInfoMediator result = null;
		Zeppauserinfoendpoint infoEndpoint = buildZeppaUserInfoEndpoint(credential);
		Zeppausertouserrelationshipendpoint relationshipEndpoint = buildZeppaUserToUserRelationshipEndpoint(credential);

		try {
			ZeppaUserInfo info = infoEndpoint.fetchZeppaUserInfoByParentId(
					userId).execute();
			if (info == null) {
				return null;
			}

			long loggedInUserId = PrefsManager.getLoggedInUserId(context);

			ListZeppaUserToUserRelationship listRequest = relationshipEndpoint
					.listZeppaUserToUserRelationship();
			listRequest.setFilter("(subjectId == " + loggedInUserId
					+ " || creatorId == " + userId + ") && (subjectId == "
					+ userId + " || creatorId == " + loggedInUserId + ")");
			listRequest.setLimit(Integer.valueOf(1));

			CollectionResponseZeppaUserToUserRelationship response = listRequest
					.execute();
			ZeppaUserToUserRelationship relationship = null;

			if (response != null && response.getItems() != null
					&& !response.getItems().isEmpty()) {
				relationship = response.getItems().get(0);
			}
			result = new DefaultUserInfoMediator(info, relationship);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Iterator through all held users and return the mediator holding one with
	 * a matching identifier
	 * 
	 * @param userId
	 * @return AbstractUserMediator with matching identifier or null
	 */

	public AbstractZeppaUserMediator getAbstractUserMediatorById(Long userId) {

		try {
			if (userId.longValue() == userMediator.getUserId().longValue()) {
				return userMediator;
			}
		} catch (NullPointerException e) {
			// userMediator is not set yet
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

	/*
	 * Loader Methods
	 */

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

	/**
	 * Build an endpoint for ZeppaUser objects
	 * 
	 * @param credential
	 * @return
	 */
	private Zeppauserendpoint buildZeppaUserEndpoint(
			GoogleAccountCredential credential) {
		Zeppauserendpoint.Builder builder = new Zeppauserendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				GsonFactory.getDefaultInstance(), credential);
		builder = CloudEndpointUtils.updateBuilder(builder);
		Zeppauserendpoint endpoint = builder.build();

		return endpoint;
	}

	/**
	 * Build endpoint for ZeppaUserInfo objects
	 * 
	 * @param credential
	 * @return
	 */
	private Zeppauserinfoendpoint buildZeppaUserInfoEndpoint(
			GoogleAccountCredential credential) {
		Zeppauserinfoendpoint.Builder builder = new Zeppauserinfoendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				GsonFactory.getDefaultInstance(), credential);
		builder = CloudEndpointUtils.updateBuilder(builder);
		Zeppauserinfoendpoint endpoint = builder.build();

		return endpoint;
	}

	/**
	 * Build endpoint for ZeppaUserToUserRelationship objects
	 * 
	 * @param credential
	 * @return
	 */
	private Zeppausertouserrelationshipendpoint buildZeppaUserToUserRelationshipEndpoint(
			GoogleAccountCredential credential) {
		Zeppausertouserrelationshipendpoint.Builder builder = new Zeppausertouserrelationshipendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				GsonFactory.getDefaultInstance(), credential);
		builder = CloudEndpointUtils.updateBuilder(builder);
		Zeppausertouserrelationshipendpoint endpoint = builder.build();

		return endpoint;
	}

}
