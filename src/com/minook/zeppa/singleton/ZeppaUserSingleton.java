package com.minook.zeppa.singleton;

/**
 * @author Pete Schuette 
 * Date Created: Thursday March 6th, 2014;
 * 
 * Singleton class update, holding everything in the application context was getting overwhelming
 * 
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.Constants;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.zeppauserendpoint.Zeppauserendpoint;
import com.minook.zeppa.zeppauserendpoint.Zeppauserendpoint.FetchFriendList;
import com.minook.zeppa.zeppauserendpoint.Zeppauserendpoint.GetZeppaUser;
import com.minook.zeppa.zeppauserendpoint.model.CollectionResponseZeppaUser;
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUser;

public class ZeppaUserSingleton {
	private static ZeppaUserSingleton singleton;

	private final String TAG = getClass().getName();

	private ZeppaUser user;
	private List<ZeppaUser> friends;
	private List<ZeppaUser> heldUsers;

	private View contactLoader;

	private boolean hasLoadedFriends;
	private Map<Long, Drawable> pictureMap;

	/*
	 * Instance should only be called once
	 */

	private ZeppaUserSingleton() {

		friends = new ArrayList<ZeppaUser>();
		heldUsers = new ArrayList<ZeppaUser>();
		pictureMap = new HashMap<Long, Drawable>();
	}

	public static ZeppaUserSingleton getInstance() {
		if (singleton == null)
			singleton = new ZeppaUserSingleton();
		return singleton;
	}

	public void onLowMemory() {
		for (ZeppaUser user : heldUsers) {
			Long userId = user.getKey().getId();
			pictureMap.remove(userId);
		}
		heldUsers.clear();
	}

	public void loadFriends(Context context) {
		if (user.getContactIds() == null || user.getContactIds().isEmpty()) {
			hasLoadedFriends = true;
		} else {
			hasLoadedFriends = false;
			loadFriendsInAsync(context);
		}
	}

	/*
	 * Setters
	 */
	public void setUser(ZeppaUser user) {
		this.user = user;
	}

	public void addFriendZeppaUser(ZeppaUser user) {
		heldUsers.remove(user);
		if (!friends.contains(user)) {
			friends.add(user);
		}
		Collections.sort(friends, Constants.USER_COMPARATOR);
	}

	public void setContactsLoader(View view) {
		this.contactLoader = view;
	}

	public boolean isUser(ZeppaUser user) {
		return this.user.getKey().equals(user.getKey());
	}

	/*
	 * Getters
	 */

	private GoogleAccountCredential getCredential(Context context) {
		return ((ZeppaApplication) context.getApplicationContext())
				.getGoogleAccountCredential();
	}

	public ZeppaUser getUser() throws NullPointerException {
		if (user == null) {
			throw new NullPointerException("User Instance NULL");
		} else
			return user;
	}

	public Long getUserId() {
		return user.getKey().getId();
	}

	public boolean isFriend(ZeppaUser user) {
		return friends.contains(user);
	}

	public boolean hasLoadedFriends() {
		return hasLoadedFriends;
	}

	public List<ZeppaUser> getFriends() {
		return friends;
	}

	public List<ZeppaUser> getHeldUsers() {
		return heldUsers;
	}

	public boolean isPendingRequest(ZeppaUser user) {
		return this.user.getFriendRequestIds().contains(user.getKey().getId());
	}

	public List<ZeppaUser> getAllHeldUsers() {
		List<ZeppaUser> allHeld = new ArrayList<ZeppaUser>();
		allHeld.addAll(friends);
		allHeld.addAll(heldUsers);
		Collections.sort(allHeld, Constants.USER_COMPARATOR);

		return allHeld;
	}

	public ZeppaUser getUserById(long userId) {
		if (userId == user.getKey().getId().longValue()) {
			return user;
		}

		for (ZeppaUser friend : friends) {
			if (friend.getKey().getId().equals(userId)) {
				return friend;
			}
		}

		for (ZeppaUser other : heldUsers) {
			if (other.getKey().getId().equals(userId)) {
				return other;
			}
		}

		return null;
	}

	public ZeppaUser getUserByEmail(String email) {
		ZeppaUser match = null;
		if (!friends.isEmpty()) {
			for (int i = 0; i < friends.size(); i++) {
				if (friends.get(i).getEmail().equals(email)) {
					match = friends.get(i);
					break;
				}
			}

		} else if (!heldUsers.isEmpty()) {
			for (int i = 0; i < heldUsers.size(); i++) {
				if (heldUsers.get(i).getEmail().equals(email)) {
					match = heldUsers.get(i);
					break;
				}
			}
		}

		return match;
	}

	public ZeppaUser getOrFetchZeppaUser(Long userId,
			GoogleAccountCredential credential) {
		ZeppaUser user = getUserById(userId);
		if (user == null) {
			Zeppauserendpoint.Builder endpointBuilder = new Zeppauserendpoint.Builder(
					AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
					credential);
			endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
			Zeppauserendpoint endpoint = endpointBuilder.build();

			try {
				GetZeppaUser getUser = endpoint.getZeppaUser(userId);
				user = getUser.execute();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (user.getContactIds() != null
					&& user.getContactIds().contains(getUserId())) {
				friends.add(user);
			} else {
				heldUsers.add(user);
			}

		}

		return user;
	}

	public ArrayList<ZeppaUser> getFriendsFrom(List<Long> list) {
		ArrayList<ZeppaUser> mutualFriends = new ArrayList<ZeppaUser>();

		for (int i = 0; i < friends.size(); i++)
			if (list.contains(friends.get(i).getKey().getId()))
				mutualFriends.add(friends.get(i));

		return mutualFriends;
	}

	public Drawable getUserImage(ZeppaUser zeppaUser) {
		Long userId = zeppaUser.getKey().getId();
		if (pictureMap.containsKey(userId)) {
			return pictureMap.get(userId);
		} else {
			try {
				URL url = new URL(zeppaUser.getImageUrl());
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setDoInput(true);
				connection.connect();
				InputStream input = connection.getInputStream();
				Drawable imageDrawable = Drawable.createFromStream(input,
						"UserImage");
				pictureMap.put(userId, imageDrawable);
				return imageDrawable;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

	}

	/*
	 * Private methods
	 */

	private boolean userIsHeld(String email) {

		if (user.getEmail().equals(email))
			return true;
		for (ZeppaUser user : friends) {
			if (user.getEmail().equals(email))
				return true;
		}

		for (ZeppaUser user : heldUsers) {
			if (user.getEmail().equals(email))
				return true;
		}

		return false;
	}

	private boolean isGmailAddress(String email) {
		return email.endsWith("@gmail.com");
	}

	private void hideContactLoader() {
		if (contactLoader != null) {
			try {
				contactLoader.setVisibility(View.GONE);
			} catch (NullPointerException ex) {
				ex.printStackTrace();
			}
		}
	}

	/*
	 * Loader Methods
	 */

	private void loadFriendsInAsync(Context context) {

		GoogleAccountCredential[] params = { getCredential(context) };
		new AsyncTask<GoogleAccountCredential, Void, Void>() {

			@Override
			protected Void doInBackground(GoogleAccountCredential... params) {

				GoogleAccountCredential credential = params[0];
				List<Long> friendIds = (ArrayList<Long>) user.getContactIds();
				if (friendIds == null) {
					return null;
				}

				Zeppauserendpoint.Builder endpointBuilder = new Zeppauserendpoint.Builder(
						AndroidHttp.newCompatibleTransport(),
						new JacksonFactory(), credential);
				endpointBuilder = CloudEndpointUtils
						.updateBuilder(endpointBuilder);
				Zeppauserendpoint endpoint = endpointBuilder.build();

				int start = 0;
				while (true) {
					try {
						FetchFriendList getFriendList = endpoint
								.fetchFriendList(getUserId(), start);
						CollectionResponseZeppaUser collectionResponse = getFriendList
								.execute();

						if (collectionResponse != null
								&& collectionResponse.getItems() != null) {
							List<ZeppaUser> items = collectionResponse
									.getItems();
							for (int i = 0; i < items.size(); i++) {
								friends.add(items.get(i));
							}

							if (items.size() < 15) {
								break;
							} else {
								start += 15;
							}

						} else {
							break;
						}

					} catch (IOException e) {
						e.printStackTrace();
						break;
					}

				}

				Collections.sort(friends, Constants.USER_COMPARATOR);

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				hasLoadedFriends = true;
				hideContactLoader();
			}

		}.execute(params);

		Log.d(TAG, "Executed Load Friends: " + System.currentTimeMillis());

	}

	public void loadPossible(Context context) {

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
				fetchForList(context, emailList);
				emailList.clear();
			}

		}

		if (!emailList.isEmpty()) { // for the last batch, if needed
			fetchForList(context, emailList);
			emailList.clear();
		}

	}

	private void fetchForList(Context context, List<String> emailList) {

		GoogleAccountCredential credential = getCredential(context);
		Zeppauserendpoint.Builder endpointBuilder = new Zeppauserendpoint.Builder(
				AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
				credential);
		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
		Zeppauserendpoint endpoint = endpointBuilder.build();

		String encodedEmails = Constants.encodeListString(emailList);
		Log.d(TAG, "Fetching from: " + encodedEmails);

		try {
			CollectionResponseZeppaUser collectionResponse = endpoint
					.findFriendsByEmailList(encodedEmails).execute();
			if (collectionResponse.getItems() != null
					&& !collectionResponse.isEmpty()) {
				heldUsers.addAll(collectionResponse.getItems());
			}
		} catch (IOException ioEx) {
			ioEx.printStackTrace();
		}

	}

	/*
	 * ------------- Updater Methods -------------
	 */

	public boolean followNewTags(Long userId, GoogleAccountCredential credential) {
		boolean success = false;
		Zeppauserendpoint.Builder endpointBuilder = new Zeppauserendpoint.Builder(
				AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
				credential);
		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
		Zeppauserendpoint endpoint = endpointBuilder.build();

		try {
			endpoint.addNewTagFollower(getUserId(), userId).execute();
			success = true;
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return success;
	}

	public boolean unfollowNewTags(Long userId,
			GoogleAccountCredential credential) {
		boolean success = false;
		Zeppauserendpoint.Builder endpointBuilder = new Zeppauserendpoint.Builder(
				AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
				credential);
		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
		Zeppauserendpoint endpoint = endpointBuilder.build();

		try {
			// TODO: change this to removeNewTagFollower
			endpoint.removeNewtagFollower(getUserId(), userId).execute();
			success = true;
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return success;
	}

	public void updateUser(ZeppaUser update) {
		if (update.getKey().getId().equals(getUserId())) {
			user = update;
		} else {
			boolean didUpdate = false;
			for (ZeppaUser friend : friends) {
				if (friend.getKey().getId().equals(update.getKey().getId())) {
					friends.remove(friend);
					friends.add(update);
					didUpdate = true;
				}
			}
			if (!didUpdate) {
				for (ZeppaUser otherUser : heldUsers) {
					if (otherUser.getKey().getId()
							.equals(update.getKey().getId())) {
						heldUsers.remove(otherUser);
						heldUsers.add(update);
						didUpdate = true;
					}
				}
				Collections.sort(heldUsers, Constants.USER_COMPARATOR);
			} else {
				Collections.sort(friends, Constants.USER_COMPARATOR);
			}

		}

	}

}
