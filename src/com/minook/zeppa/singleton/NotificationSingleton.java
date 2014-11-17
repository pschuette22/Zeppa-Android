package com.minook.zeppa.singleton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAuthIOException;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.observer.OnLoadListener;
import com.minook.zeppa.utils.Utils;
import com.minook.zeppa.zeppanotificationendpoint.Zeppanotificationendpoint;
import com.minook.zeppa.zeppanotificationendpoint.Zeppanotificationendpoint.ListZeppaNotification;
import com.minook.zeppa.zeppanotificationendpoint.model.CollectionResponseZeppaNotification;
import com.minook.zeppa.zeppanotificationendpoint.model.ZeppaNotification;
import com.minook.zeppa.zeppanotificationendpoint.model.ZeppaUser;

public class NotificationSingleton {
	private static NotificationSingleton singleton;

	private final String TAG = "NotificationSingleton";
	private List<ZeppaNotification> notifications;
	private List<OnLoadListener> loadListeners;
	private boolean hasLoadedInitial;

	/*
	 * Instance Handlers
	 */

	private NotificationSingleton() {
		notifications = new ArrayList<ZeppaNotification>();
		loadListeners = new ArrayList<OnLoadListener>();
		hasLoadedInitial = false;
	}

	public static NotificationSingleton getInstance() {
		if (singleton == null)
			singleton = new NotificationSingleton();
		return singleton;
	}

	/*
	 * Getters
	 */

	public List<ZeppaNotification> getNotifications() {
		return notifications;
	}

	private Long getUserId() {
		return ZeppaUserSingleton.getInstance().getUserId();
	}

	public boolean hasLoadedInitial() {
		return hasLoadedInitial;
	}

	/*
	 * Setters
	 */

	/**
	 * This method creates a new basic instance of a ZeppaNotification</p> This
	 * instance is only to be sent to another user from this user.
	 * 
	 * @return instance - Basic ZeppaNotification Instance
	 */
	public ZeppaNotification newNotificationInstance() {
		ZeppaNotification instance = new ZeppaNotification();

		instance.setHasSeen(Boolean.FALSE);
		ZeppaUser sender = new ZeppaUser();
		sender.setId(ZeppaUserSingleton.getInstance().getUserId());
		instance.setSender(sender);

		return instance;
	}

	public void registerOnLoadListener(OnLoadListener listener) {
		if (!loadListeners.contains(listener)) {
			this.loadListeners.add(listener);
		}
	}

	public void addNotification(ZeppaNotification notification) {
		notifications.add(notification);
	}

	public void addAllNotifications(List<ZeppaNotification> notifications) {

		notifications.removeAll(notifications);
		notifications.addAll(notifications);

		Collections.sort(notifications, Utils.NOTIFICAITON_COMPARATOR);
	}

	/*
	 * Private
	 */

	private void onFinishLoad() {
		Iterator<OnLoadListener> listeners = loadListeners.iterator();

		while (listeners.hasNext()) {
			OnLoadListener listener = listeners.next();
			try {
				listener.onFinishLoad();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	/*
	 * Loaders
	 */

	public boolean markNotificationAsSeen(ZeppaNotification notification,
			GoogleAccountCredential credential) {
		boolean success = false;

		Zeppanotificationendpoint.Builder endpointBuilder = new Zeppanotificationendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				AndroidJsonFactory.getDefaultInstance(), credential);
		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
		Zeppanotificationendpoint notificationEndpoint = endpointBuilder
				.build();

		try {
			notificationEndpoint.updateZeppaNotification(notification)
					.execute();
			success = true;
		} catch (GoogleAuthIOException aEx) {
			Log.wtf(TAG, "AuthException");
			success = false;
		} catch (IOException ex) {

		}
		return success;
	}

	public void loadInitialNotificationsInAsync(
			GoogleAccountCredential credential, Long userId) {

		Object[] params = { credential, userId };

		new AsyncTask<Object, Void, Void>() {

			@Override
			protected Void doInBackground(Object... params) {

				GoogleAccountCredential credential = (GoogleAccountCredential) params[0];
				Long userId = (Long) params[1];

				Zeppanotificationendpoint.Builder endpointBuilder = new Zeppanotificationendpoint.Builder(
						AndroidHttp.newCompatibleTransport(),
						AndroidJsonFactory.getDefaultInstance(), credential);
				endpointBuilder = CloudEndpointUtils
						.updateBuilder(endpointBuilder);
				Zeppanotificationendpoint notificationEndpoint = endpointBuilder
						.build();

				// TODO: list notifications for this user

				String cursor = null;
				String filter = "recipientId == userIdParam";
				String ordering = "created desc";
				String parameterDeclaration = "Long userIdParam";

				do {
					try {

						ListZeppaNotification listNotificationsTask = notificationEndpoint
								.listZeppaNotification();

						listNotificationsTask.setCursor(cursor);
						listNotificationsTask.setFilter(filter);
						listNotificationsTask.setParameterDeclaration(parameterDeclaration);
						listNotificationsTask.setOrdering(ordering);
						listNotificationsTask.setLimit(40);
						listNotificationsTask.setLongParam(userId);
						
						CollectionResponseZeppaNotification response = listNotificationsTask.execute();
						
						if(response == null || response.getItems() == null || response.getItems().isEmpty()){
							cursor = null;
							break;
						} else {
							addAllNotifications(response.getItems());
							cursor = response.getNextPageToken();
						}
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} while (cursor != null);

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

}
