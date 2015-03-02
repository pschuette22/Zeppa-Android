package com.minook.zeppa.singleton;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.Constants;
import com.minook.zeppa.PrefsManager;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator;
import com.minook.zeppa.mediator.AbstractZeppaUserMediator;
import com.minook.zeppa.runnable.FetchInitialNotificationsRunnable;
import com.minook.zeppa.runnable.ThreadManager;
import com.minook.zeppa.zeppanotificationendpoint.model.ZeppaNotification;

public class NotificationSingleton {

	public interface NotificationLoadListener {
		public void onNotificationsLoaded();
	}

	private static NotificationSingleton singleton;
//	private final String TAG = "NotificationSingleton";
	private List<ZeppaNotification> notifications;
	private List<NotificationLoadListener> loadListeners;
	private boolean hasLoadedInitial;
//	private boolean isFetchingNotifications;
	private String nextNotificationPageToken;
	
	/*
	 * Instance Handlers
	 */

	private NotificationSingleton() {
		notifications = new ArrayList<ZeppaNotification>();
		loadListeners = new ArrayList<NotificationLoadListener>();
		hasLoadedInitial = false;
//		isFetchingNotifications = false;
	}

	public static NotificationSingleton getInstance() {
		if (singleton == null)
			singleton = new NotificationSingleton();
		return singleton;
	}
	
	public void restore(){
		singleton = new NotificationSingleton();
	}
	
	public void clear(){
		notifications.clear();
	}
	
	public void removeNotificationsForEvent(long eventId){
		
		List<ZeppaNotification> remove = new ArrayList<ZeppaNotification>();
		Iterator<ZeppaNotification> iterator = notifications.iterator();
		while(iterator.hasNext()){
			ZeppaNotification notification = iterator.next();
			if(notification.getEventId() != null && notification.getEventId().longValue() == eventId){
				remove.add(notification);
			}
		}
		notifications.removeAll(remove);
	}

	public void removeNotification(long notificationId){
		
		ZeppaNotification notification = null;
		Iterator<ZeppaNotification> iterator = notifications.iterator();
		while(iterator.hasNext()){
			ZeppaNotification n = iterator.next();
			if(n.getId().longValue() == notificationId){
				notification = n;
				break;
			}
		}
		
		if(notification != null){
			notifications.remove(notification);
		}
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

	public void onLoadedNotifications(){
		hasLoadedInitial = true;
		notifyObservers();
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
			builder.append("New Request To Mingle");
			break;
		case 1:
			builder.append("Now Mingling");
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
			builder.append("Event Cancelation");
			break;
		case 6:
			builder.append("Event Updated");
			break;
		case 7:
			builder.append("New Event Attendee");
			break;
		case 8:
			builder.append("Attendee Left Event");
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
					+ " wants to mingle");
			break;
		case 1:
			builder.append(userInfoMediator.getDisplayName()
					+ " accepted mingle request");
			break;
		case 2:
			builder.append(userInfoMediator.getDisplayName() + " started "
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

	/*
	 * Setters
	 */


	public void registerOnLoadListener(NotificationLoadListener listener) {
		if (!loadListeners.contains(listener)) {
			this.loadListeners.add(listener);
		}
	}

	public void unregisterOnLoadListener(NotificationLoadListener listener) {
		this.loadListeners.remove(listener);

	}

	private boolean alreadyHoldingNotification(ZeppaNotification notification) {

		if (!notifications.isEmpty()) {
			Iterator<ZeppaNotification> iterator = notifications.iterator();
			while (iterator.hasNext()) {
				if (iterator.next().getId().longValue() == notification.getId()
						.longValue()) {
					return true;
				}
			}
		}

		return false;
	}

	public void addNotification(ZeppaNotification notification) {
		if (!alreadyHoldingNotification(notification)) {
			notifications.add(notification);
		}
	}

	
	// public void addAllNotifications(List<ZeppaNotification> notifications) {
	//
	// notifications.removeAll(notifications);
	// notifications.addAll(notifications);
	//
	// Collections.sort(notifications, Utils.NOTIFICAITON_COMPARATOR);
	// }

	public boolean doPushNotification(Context context,
			ZeppaNotification notification) {
		if (PrefsManager.getUserPreference(context,
				Constants.PUSH_NOTIFICATIONS)) {
			switch (getNotificationTypeOrder(notification)) {
			case 0:
				return PrefsManager.getUserPreference(context,
						Constants.PN_MINGLE_REQUEST);
			case 1:
				return PrefsManager.getUserPreference(context,
						Constants.PN_MINGLE_ACCEPT);

			case 2:
				return PrefsManager.getUserPreference(context,
						Constants.PN_EVENT_RECOMMENDATION);

			case 3:
				return PrefsManager.getUserPreference(context,
						Constants.PN_EVENT_RECOMMENDATION);

			case 4:
				return PrefsManager.getUserPreference(context,
						Constants.PN_EVENT_COMMENT);

			case 5:
				return PrefsManager.getUserPreference(context,
						Constants.PN_EVENT_CANCELED);

			case 6:
				return false;
			case 7:
				return PrefsManager.getUserPreference(context,
						Constants.PN_EVENT_JOINED);

			case 8:
				return PrefsManager.getUserPreference(context,
						Constants.PN_EVENT_LEFT);

			case 9:
				return false;

			default:
				return false;
			}

		} else {
			return false;
		}

	}
	
	

	/*
	 * Private
	 */

	public void notifyObservers() {
		Iterator<NotificationLoadListener> listeners = loadListeners.iterator();

		while (listeners.hasNext()) {
			NotificationLoadListener listener = listeners.next();
			try {
				listener.onNotificationsLoaded();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}


	/**
	 * This method loads the initial instances of notifications the user should
	 * see
	 * 
	 * @param credential
	 * @param userId
	 */
	public void fetchNotifications(ZeppaApplication application, 
			GoogleAccountCredential credential, Long userId) {

		
		ThreadManager.execute(new FetchInitialNotificationsRunnable(application, credential, userId, nextNotificationPageToken));
		
		
	}


}
