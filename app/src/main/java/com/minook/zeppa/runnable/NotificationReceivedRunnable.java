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

import com.appspot.zeppa_cloud_1821.zeppausertouserrelationshipendpoint.Zeppausertouserrelationshipendpoint.ListZeppaUserToUserRelationship;
import com.appspot.zeppa_cloud_1821.zeppaeventendpoint.model.ZeppaEvent;
import com.appspot.zeppa_cloud_1821.zeppaeventtouserrelationshipendpoint.Zeppaeventtouserrelationshipendpoint.ListZeppaEventToUserRelationship;
import com.appspot.zeppa_cloud_1821.zeppaeventtouserrelationshipendpoint.model.CollectionResponseZeppaEventToUserRelationship;
import com.appspot.zeppa_cloud_1821.zeppaeventtouserrelationshipendpoint.model.ZeppaEventToUserRelationship;
import com.appspot.zeppa_cloud_1821.zeppanotificationendpoint.model.ZeppaNotification;
import com.appspot.zeppa_cloud_1821.zeppauserinfoendpoint.model.ZeppaUserInfo;
import com.appspot.zeppa_cloud_1821.zeppausertouserrelationshipendpoint.model.CollectionResponseZeppaUserToUserRelationship;
import com.appspot.zeppa_cloud_1821.zeppausertouserrelationshipendpoint.model.ZeppaUserToUserRelationship;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

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
		try {

			notification = buildNotificationEndpoint().getZeppaNotification(
					notificationId).execute();

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

			if (mediator == null) {
				userInfo = buildUserInfoEndpoint()
						.fetchZeppaUserInfoByParentId(
								notification.getSenderId()).execute();
				ListZeppaUserToUserRelationship relationshipTask = buildZeppaUserToUserRelationshipEndpoint()
						.listZeppaUserToUserRelationship();
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

			if (notification.getEventId() != null
					&& ZeppaEventSingleton.getInstance().getEventById(
							notification.getEventId().longValue()) == null) {
				ZeppaEvent event = buildEventEndpoint().getZeppaEvent(
						notification.getEventId()).execute();

				if (event.getHostId().longValue() == userId) {
					eventMediator = new MyZeppaEventMediator(event);
				} else {

					ZeppaEventToUserRelationship relationship = null;
					ListZeppaEventToUserRelationship task = buildEventRelationshipEndpoint()
							.listZeppaEventToUserRelationship();
					task.setFilter("userId == " + userId + " && eventId == "
							+ notification.getEventId().longValue());
					task.setLimit(Integer.valueOf(1));
					CollectionResponseZeppaEventToUserRelationship response = task
							.execute();

					if (response != null && response.getItems() != null
							&& !response.getItems().isEmpty()) {
						relationship = response.getItems().get(0);
					}

					eventMediator = new DefaultZeppaEventMediator(event,
							relationship);
				}
			}

			if (NotificationSingleton.getInstance().getNotificationTypeOrder(
					notification) <= 1) {
				if (userRelationship == null) {
					ListZeppaUserToUserRelationship relationshipTask = buildZeppaUserToUserRelationshipEndpoint()
							.listZeppaUserToUserRelationship();
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

			if (eventMediator != null) {
				ZeppaEventSingleton.getInstance().addMediator(eventMediator);
			}

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

		} catch (IOException e) {
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
