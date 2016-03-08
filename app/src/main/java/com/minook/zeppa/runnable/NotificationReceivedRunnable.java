package com.minook.zeppa.runnable;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.appspot.zeppa_cloud_1821.zeppaclientapi.Zeppaclientapi;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.CollectionResponseZeppaEventToUserRelationship;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.CollectionResponseZeppaUserToUserRelationship;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaEvent;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaEventToUserRelationship;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaNotification;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaUserInfo;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaUserToUserRelationship;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ApiClientHelper;
import com.minook.zeppa.Constants;
import com.minook.zeppa.PrefsManager;
import com.minook.zeppa.R;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.activity.LoginActivity;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator;
import com.minook.zeppa.mediator.AbstractZeppaUserMediator;
import com.minook.zeppa.mediator.DefaultUserInfoMediator;
import com.minook.zeppa.mediator.DefaultZeppaEventMediator;
import com.minook.zeppa.mediator.MyZeppaEventMediator;
import com.minook.zeppa.singleton.NotificationSingleton;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

import java.io.IOException;

public class NotificationReceivedRunnable extends BaseRunnable {

	private long notificationId;
	private long userId;
	private NotificationManager mNotificationManager;
	public static final int NOTIFICATION_ID = 1;
	private ZeppaNotification notification;

	private ZeppaUserInfo userInfo;
	private ZeppaUserToUserRelationship userRelationship;

	private AbstractZeppaEventMediator eventMediator;

	public NotificationReceivedRunnable(ZeppaApplication application,
			GoogleAccountCredential credential, long notificationId, long userId) {
		super(application, credential);
		this.notificationId = notificationId;
		this.userId = userId;
	}

	@Override
	public void run() {

		ApiClientHelper helper = new ApiClientHelper();
		Zeppaclientapi api = helper.buildClientEndpoint();

		try {

			notification = api.getZeppaNotification(
					notificationId, credential.getToken()).execute();

			// Erroniously consumed this notification object, do not notify the
			// user
			if (notification.getRecipientId().longValue() != userId) {
				return;
			}

			AbstractZeppaUserMediator mediator = null;
			try {
				mediator = ZeppaUserSingleton
						.getInstance()
						.getAbstractUserMediatorById(notification.getSenderId());

			} catch (NullPointerException e) {

			}

			/*
			* Verify that we have userinfo for the sender. If not, fetch it
			* */
			if (mediator == null) {
				userInfo = api
						.fetchZeppaUserInfoByParentId(credential.getToken(),
								notification.getSenderId()).execute();
				Zeppaclientapi.ListZeppaUserToUserRelationship relationshipTask = api
						.listZeppaUserToUserRelationship(credential.getToken());
				relationshipTask.setFilter("(creatorId == " + userId
						+ "|| creatorId == "
						+ notification.getSenderId().longValue()
						+ ") && (subjectId == " + userId + " || subjectId == "
						+ notification.getSenderId().longValue() + ")");

				relationshipTask.setLimit(Integer.valueOf(1));
				CollectionResponseZeppaUserToUserRelationship response = relationshipTask
						.execute();
				if (response != null && response.getItems() != null
						&& !response.getItems().isEmpty()) {
					userRelationship = response.getItems().get(0);
				}

				ZeppaUserSingleton.getInstance().addDefaultZeppaUserMediator(
						userInfo, userRelationship);

			}

			/*
			 * If this notification has an event associated with it, (eventId > 0)
			 * Verify we hold the event, and if not, fetch it in the background
			 */
			if (notification.getEventId() != null && notification.getEventId().longValue() > 0
					&& ZeppaEventSingleton.getInstance().getEventById(
							notification.getEventId().longValue()) == null) {
				ZeppaEvent event = api.getZeppaEvent(
						notification.getEventId(), credential.getToken()).execute();

				/**
				 * This is a unique instance. If a user has two devices, an event is made on one and another user comments on this, the event will be fetched
				 * Add the event to the datastore
				 */
				if (event.getHostId().longValue() == userId) {
					eventMediator = new MyZeppaEventMediator(event);
				} else {

					ZeppaEventToUserRelationship relationship = null;
					Zeppaclientapi.ListZeppaEventToUserRelationship task = api
							.listZeppaEventToUserRelationship(credential.getToken());
					task.setFilter("userId == " + userId + " && eventId == "
							+ notification.getEventId().longValue());
					task.setLimit(Integer.valueOf(1));
					CollectionResponseZeppaEventToUserRelationship response = task
							.execute();

					if (response != null && response.getItems() != null
							&& !response.getItems().isEmpty()) {
						relationship = response.getItems().get(0);

						eventMediator = new DefaultZeppaEventMediator(event,
								relationship);

						ZeppaEventSingleton.getInstance().addMediator(eventMediator);
					}
				}
			}

			/*
			* If a change in the relationship between two users has been made, update it
			* */
			if (NotificationSingleton.getInstance().getNotificationTypeOrder(
					notification) <= 1) {
				if (userRelationship == null) {
					Zeppaclientapi.ListZeppaUserToUserRelationship relationshipTask = api
							.listZeppaUserToUserRelationship(credential.getToken());
					relationshipTask.setFilter("(creatorId == " + userId
							+ "|| creatorId == "
							+ notification.getSenderId().longValue()
							+ ") && (subjectId == " + userId
							+ " || subjectId == "
							+ notification.getSenderId().longValue() + ")");

					relationshipTask.setLimit(Integer.valueOf(1));
					CollectionResponseZeppaUserToUserRelationship response = relationshipTask
							.execute();
					if (response != null && response.getItems() != null
							&& !response.getItems().isEmpty()) {
						userRelationship = response.getItems().get(0);
					}
				}

				DefaultUserInfoMediator infoMediator = (DefaultUserInfoMediator) ZeppaUserSingleton
						.getInstance().getAbstractUserMediatorById(
								notification.getSenderId());
				infoMediator.setUserRelationship(userRelationship);
			}

			/*
			* If there was an event fetched
			* */

			boolean doPushNotification = true;

			try {

				AuthenticatedFragmentActivity currentActivity = application
						.getCurrentActivity();

				if (currentActivity != null
						&& !(currentActivity instanceof LoginActivity)
						&& currentActivity.isCurrentlyActive()) {

					doPushNotification = false;

					currentActivity.runOnUiThread(new Runnable() {

						@Override
						public void run() {

							NotificationSingleton.getInstance()
									.addNotification(notification);
							application.getCurrentActivity()
									.onNotificationReceived(notification);

							NotificationSingleton.getInstance()
									.notifyObservers();
						}

					});
				}

			} catch (NullPointerException e) {
				e.printStackTrace();
			}

			if (doPushNotification) {
				pushNotification();
			}

		} catch (IOException | GoogleAuthException e) {
			e.printStackTrace();
		}

	}

	/**
	 * This method pushes a notification to the status bar
	 */
	private void pushNotification() {

		mNotificationManager = (NotificationManager) application
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent intent = new Intent(application, LoginActivity.class);
		intent.putExtra(Constants.INTENT_NOTIFICATIONS, Boolean.TRUE);
		PendingIntent contentIntent = PendingIntent.getActivity(application, 0,
				intent, 0);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				application)
				.setSmallIcon(R.drawable.zeppa_icon)
				.setAutoCancel(true)
				.setDefaults(Notification.DEFAULT_LIGHTS)
				.setLights(Color.CYAN, 300, 2000)
				.setOnlyAlertOnce(true)
				.setContentTitle(
						NotificationSingleton.getInstance()
								.getNotificationTitle(notification));

		try {
			String content = NotificationSingleton.getInstance()
					.getNotificationMessage(notification);
			mBuilder.setContentText(content);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (PrefsManager.getUserPreference(application, Constants.PN_SOUND_ON)) {
			Uri sound = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			mBuilder.setSound(sound);
			mBuilder.setDefaults(Notification.DEFAULT_SOUND);
		}

		if (PrefsManager.getUserPreference(application,
				Constants.PN_VIBRARTE_ON)) {
			mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
		}

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

	}

}
