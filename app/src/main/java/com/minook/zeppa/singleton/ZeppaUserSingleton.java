package com.minook.zeppa.singleton;

/**
 * @author Pete Schuette 
 * Date Created: Thursday March 6th, 2014;
 * 
 * Singleton class update, holding everything in the application context was getting overwhelming
 * 
 */

import android.content.Context;

import com.appspot.zeppa_cloud_1821.zeppaclientapi.Zeppaclientapi;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.CollectionResponseZeppaUserToUserRelationship;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaUser;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaUserInfo;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaUserToUserRelationship;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ApiClientHelper;
import com.minook.zeppa.PrefsManager;
import com.minook.zeppa.Utils;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.adapter.MinglerFinderAdapter;
import com.minook.zeppa.mediator.AbstractZeppaUserMediator;
import com.minook.zeppa.mediator.DefaultUserInfoMediator;
import com.minook.zeppa.mediator.MyZeppaUserMediator;
import com.minook.zeppa.mediator.VendorInfoMediator;
import com.minook.zeppa.runnable.FindMinglersRunnable;
import com.minook.zeppa.runnable.ThreadManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ZeppaUserSingleton {

	// Provides an interface for when minglers are loaded by the singleton
	public interface OnMinglersLoadListener {
		public void onMinglersLoaded();
	}

	private static ZeppaUserSingleton singleton;

	// private final String TAG = getClass().getName();

	private MyZeppaUserMediator userMediator;
	private List<DefaultUserInfoMediator> heldUserMediators;
	private List<VendorInfoMediator> vendorInfoMediators;
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
		vendorInfoMediators = new ArrayList<VendorInfoMediator>();

	}

	public static ZeppaUserSingleton getInstance() {
		if (singleton == null)
			singleton = new ZeppaUserSingleton();
		return singleton;
	}

	/**
	 * Replace the singleton instance with a brand new one
	 */
	public void restore() {
		singleton = new ZeppaUserSingleton();
	}

	/**
	 * Clear held user mediators
	 */
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
			GoogleAccountCredential credential) throws IOException, GoogleAuthException {

		if (userMediator != null) {
			return userMediator;
		}

		ApiClientHelper helper = new ApiClientHelper();
		Zeppaclientapi api = helper.buildClientEndpoint();

		// Fetch the id token string identifying this signed in user
		String idToken = credential.getToken();

		ZeppaUser zeppaUser = api.fetchCurrentZeppaUser(idToken).execute();

		if (zeppaUser == null) {
			return null;
		}

		MyZeppaUserMediator mediator = new MyZeppaUserMediator(zeppaUser);
		this.userMediator = mediator;
		return userMediator;
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
	 */
	public void registerLoadListener(OnMinglersLoadListener listener) {
		if (!minglerLoadListeners.contains(listener)) {
			this.minglerLoadListeners.add(listener);
		}
	}

	/**
	 * Unregister adapter listening to changes in list of minglers
	 * 
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
			numbers.add(userMediator.getPhoneNumber());
		} catch (NullPointerException e) {

		}

//		Iterator<DefaultUserInfoMediator> iterator = heldUserMediators
//				.iterator();
//		while (iterator.hasNext()) {
//			try {
//				String number = iterator.next().getUnformattedPhoneNumber();
//				numbers.add(number);
//			} catch (NullPointerException e) {
//				// Number is null
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}

		return numbers;
	}

	public List<String> getRecognizedEmails() {
		List<String> emails = new ArrayList<String>();

		emails.add(userMediator.getGmail());

		Iterator<DefaultUserInfoMediator> iterator = heldUserMediators
				.iterator();
//		while (iterator.hasNext()) {
//			try {
//				emails.add(iterator.next().getGmail());
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}

		return emails;
	}

	/**
	 * Get the vendor mediator by id or null if its not help
	 * @param id
	 * @return
	 */
	public VendorInfoMediator getVendorById(long id) {
		for(VendorInfoMediator mediator: vendorInfoMediators){
			if(mediator.getUserId().longValue()==id){
				return mediator;
			}
		}
		return null;
	}

	public void addVendorInfoMediator(VendorInfoMediator mediator) {
		this.vendorInfoMediators.add(mediator);
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
					&& (!mediator.isPendingRequest() || (mediator
							.isPendingRequest() && mediator.didSendRequest()))) {
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

			if (mediator.isPendingRequest() && !mediator.didSendRequest()) {
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
		ApiClientHelper helper = new ApiClientHelper();
		Zeppaclientapi api = helper.buildClientEndpoint();

		try {
			ZeppaUserInfo info = api.fetchZeppaUserInfoByParentId(credential.getToken(),userId).execute();
			if (info == null) {
				return null;
			}

			long loggedInUserId = PrefsManager.getLoggedInUserId(context);

			Zeppaclientapi.ListZeppaUserToUserRelationship listRequest = api
					.listZeppaUserToUserRelationship(credential.getToken());
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

		} catch (IOException | GoogleAuthException e) {
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

//	public boolean emailIsRecognized(String email) {
//
//		if (getUserMediator().getGmail().equalsIgnoreCase(email)) {
//			return true;
//		}
//
//		Iterator<DefaultUserInfoMediator> iterator = heldUserMediators
//				.iterator();
//
//		while (iterator.hasNext()) {
//			DefaultUserInfoMediator mediator = iterator.next();
//			if (mediator.getGmail().equalsIgnoreCase(email)) {
//				return true;
//			}
//		}
//
//		return false;
//	}
//
//	public boolean numberIsRecognized(String number) {
//		try {
//			if (getUserMediator().getPrimaryPhoneNumber().equals(number)) {
//				return true;
//			}
//		} catch (NullPointerException e) {
//			// This user does not have a phone number attached to their account
//			return false;
//		}
//
//		Iterator<DefaultUserInfoMediator> iterator = heldUserMediators
//				.iterator();
//
//		while (iterator.hasNext()) {
//
//			try {
//				DefaultUserInfoMediator mediator = iterator.next();
//				String primaryNumber = mediator.getPrimaryPhoneNumber();
//				if (primaryNumber != null && primaryNumber.equals(number))
//					return true;
//			} catch (NullPointerException e) {
//				// User may not have a phone number attached to their account
//			}
//		}
//
//		return false;
//	}



}
