package com.minook.zeppa.singleton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAuthIOException;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.Utils;
import com.minook.zeppa.activity.DefaultEventViewActivity;
import com.minook.zeppa.activity.MainActivity;
import com.minook.zeppa.activity.MinglerActivity;
import com.minook.zeppa.activity.MyEventViewActivity;
import com.minook.zeppa.activity.StartMinglingActivity;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator;
import com.minook.zeppa.mediator.AbstractZeppaUserMediator;
import com.minook.zeppa.observer.OnLoadListener;
import com.minook.zeppa.zeppanotificationendpoint.Zeppanotificationendpoint;
import com.minook.zeppa.zeppanotificationendpoint.Zeppanotificationendpoint.ListZeppaNotification;
import com.minook.zeppa.zeppanotificationendpoint.model.CollectionResponseZeppaNotification;
import com.minook.zeppa.zeppanotificationendpoint.model.ZeppaNotification;

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


	public boolean hasLoadedInitial() {
		return hasLoadedInitial;
	}

	public int getNotificationTypeOrder(ZeppaNotification notification) {
		String type = notification.getType();
		if (type.equals("MINGLE_REQUEST")) {
			return 0;
		} else if (type.equals("MINGLE_ACCEPTED")) {
			return 1;
		} else if (type.equals("EVENT_RECOMMENDATION")) {
			return 2;
		} else if (type.equals("DIRECT_INVITE")) {
			return 3;
		} else if (type.equals("COMMENT_ON_POST")) {
			return 4;
		} else if (type.equals("EVENT_CANCELED")) {
			return 5;
		} else if (type.equals("EVENT_UPDATED")) {
			return 6;
		} else if (type.equals("USER_JOINED")) {
			return 7;
		} else if (type.equals("USER_LEAVING")) {
			return 8;
		} else if (type.equals("EVENT_REPOSTED")) {
			return 9;
		} else {
			return -1;
		}
	}

	public String getNotificationTitle(ZeppaNotification notification) {
		StringBuilder builder = new StringBuilder();

		switch (getNotificationTypeOrder(notification)) {
		case 0:
			builder.append("Wanna Mingle?");
			break;
		case 1:
			builder.append("Mingling");
			break;
		case 2:
			builder.append("New Event Recommendation");
			break;
		case 3:
			builder.append("New Event Invitation");
			break;
		case 4:
			builder.append("Unread Comment");
			break;
		case 5:
			builder.append("Cancelation");
			break;
		case 6:
			builder.append("Event Updated");
			break;
		case 7:
			builder.append("New Event Attendee");
			break;
		case 8:
			builder.append("Attendee Left");
			break;
		case 9:
			builder.append("Event Reposted");
			break;

		}

		return builder.toString();
	}

	public String getNotificationMessage(ZeppaNotification notification) {
		StringBuilder builder = new StringBuilder();

		AbstractZeppaEventMediator eventMediator = ZeppaEventSingleton
				.getInstance().getEventById(notification.getEventId());
		AbstractZeppaUserMediator userInfoMediator = ZeppaUserSingleton
				.getInstance().getAbstractUserMediatorById(
						notification.getSenderId());

		switch (getNotificationTypeOrder(notification)) {
		case 0:
			builder.append(userInfoMediator.getDisplayName()
					+ " sent mingle request");
			break;
		case 1:
			builder.append(userInfoMediator.getDisplayName()
					+ " accepted mingle request");
			break;
		case 2:
			builder.append(userInfoMediator.getDisplayName() + " just started "
					+ eventMediator.getTitle());
			break;
		case 3:
			builder.append(userInfoMediator.getDisplayName()
					+ " invited you to " + eventMediator.getTitle());
			break;
		case 4:
			builder.append(userInfoMediator.getDisplayName() + " commented on "
					+ eventMediator.getTitle());
			break;
		case 5:
			// TODO: figure out how to retrieve canceled event name and put into
			// notification body
			builder.append(userInfoMediator.getDisplayName() + " canceled ");
			break;
		case 6:
			builder.append(userInfoMediator.getDisplayName() + " updated "
					+ eventMediator.getTitle());
			break;
		case 7:
			builder.append(userInfoMediator.getDisplayName() + " joined "
					+ eventMediator.getTitle());
			break;
		case 8:
			builder.append(userInfoMediator.getDisplayName() + " left "
					+ eventMediator.getTitle());
			break;
		case 9:
			builder.append(userInfoMediator.getDisplayName() + " reposted "
					+ eventMediator.getTitle());
			break;

		}

		return builder.toString();
	}

	public PendingIntent getPendingIntent(Context context,
			ZeppaNotification notification) {

		Intent intent = null;

		switch (getNotificationTypeOrder(notification)) {
		case 0:
			intent = new Intent(context, StartMinglingActivity.class);
			break;
		case 1:
			intent = new Intent(context, MinglerActivity.class);
			break;
		case 5:
			intent = new Intent(context, MainActivity.class);
			break;
		case 2:
		case 3:
		case 4:
		case 6:
		case 7:
		case 8:
		case 9:
			AbstractZeppaEventMediator mediator = ZeppaEventSingleton
					.getInstance().getEventById(notification.getEventId());

			if (mediator.isHostedByCurrentUser()) {
				intent = new Intent(context, MyEventViewActivity.class);
			} else {
				intent = new Intent(context, DefaultEventViewActivity.class);
			}
			
			break;

		}

		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		
		return contentIntent;
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
		instance.setSenderId(ZeppaUserSingleton.getInstance().getUserId());

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

	/**
	 * This method takes a Notification ID and loads all necessary values to
	 * display it. NOT THREAD SAFE
	 * 
	 * @param credential
	 * @param notificationId
	 * @return result
	 */
	public ZeppaNotification fetchNotificationAndElementsWithBlocking(
			GoogleAccountCredential credential, Long notificationId) {
		ZeppaNotification result = null;
		Zeppanotificationendpoint notificationEndpoint = buildNotificationEndpoint(credential);

		
		try {
			result = notificationEndpoint.getZeppaNotification(notificationId)
					.execute();

			
			if (fetchNotificationElementsWithBlocking(credential, result)) {
				addNotification(result);
			} else {
				return null;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	private boolean fetchNotificationElementsWithBlocking(
			GoogleAccountCredential credential, ZeppaNotification notification) {
		boolean success = false;

		int notificationType = NotificationSingleton.getInstance().getNotificationTypeOrder(notification);
		boolean updateUserRelationship = (notificationType <= 1);
		
		success = ZeppaUserSingleton.getInstance()
				.fetchUserAndRelationshipWithBlocking(notification.getSenderId(),
						credential, updateUserRelationship);


		if (notification.getEventId() != null) {
			success = ZeppaEventSingleton.getInstance()
					.fetchEventAndRelationshipWithBlocking(credential,
							notification.getEventId());
		} 
		return success;
	}

	/**
	 * This Updates Notification and sets it as seen
	 * 
	 * @param notification
	 * @param credential
	 * @return
	 */
	public boolean markNotificationAsSeen(ZeppaNotification notification,
			GoogleAccountCredential credential) {
		boolean success = false;
		Zeppanotificationendpoint notificationEndpoint = buildNotificationEndpoint(credential);

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

	private Zeppanotificationendpoint buildNotificationEndpoint(GoogleAccountCredential credential){
		Zeppanotificationendpoint.Builder endpointBuilder = new Zeppanotificationendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				AndroidJsonFactory.getDefaultInstance(), credential);
		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
		Zeppanotificationendpoint notificationEndpoint = endpointBuilder
				.build();
		
		return notificationEndpoint;
	}
	
	/**
	 * This method loads the initial instances of notifications the user should
	 * see
	 * 
	 * @param credential
	 * @param userId
	 */
	public void loadInitialNotificationsInAsync(
			GoogleAccountCredential credential, Long userId) {

		Object[] params = { credential, userId };

		new AsyncTask<Object, Void, Void>() {

			@Override
			protected Void doInBackground(Object... params) {

				GoogleAccountCredential credential = (GoogleAccountCredential) params[0];
				Long userId = (Long) params[1];

				
				Zeppanotificationendpoint notificationEndpoint = buildNotificationEndpoint(credential);

				// TODO: list notifications for this user

				String cursor = null;
				String filter = "recipientId == " + userId.longValue()
						+ " && expires > " + System.currentTimeMillis();

				do {
					try {

						ListZeppaNotification listNotificationsTask = notificationEndpoint
								.listZeppaNotification();

						listNotificationsTask.setCursor(cursor);
						listNotificationsTask.setFilter(filter);
						listNotificationsTask.setLimit(Integer.valueOf(25));

						CollectionResponseZeppaNotification response = listNotificationsTask
								.execute();

						if (response == null || response.getItems() == null
								|| response.getItems().isEmpty()) {
							cursor = null;
							break;
						} else {
							Iterator<ZeppaNotification> iterator = response
									.getItems().iterator();

							while (iterator.hasNext()) {
								ZeppaNotification notification = iterator
										.next();

								if (fetchNotificationElementsWithBlocking(
										credential, notification)) {
									addNotification(notification);
								}

							}

							cursor = response.getNextPageToken();
						}

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						cursor = null;
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
