package com.minook.zeppa.gcm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.plus.Plus;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.singleton.NotificationSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppanotificationendpoint.model.ZeppaNotification;

public class ZeppaGCMIntentService extends IntentService implements
		ConnectionCallbacks, OnConnectionFailedListener {

	private static final String TAG = ZeppaGCMIntentService.class.getName();
	public static final int NOTIFICATION_ID = 1;

	private static NotificationManager mNotificationManager;
	private static GoogleApiClient apiClient;
	private static List<Intent> notificationIntents;

	public ZeppaGCMIntentService() {
		super(ZeppaGCMIntentService.class.getName());
		notificationIntents = new ArrayList<Intent>();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "Handling intent");

		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) { // has effect of unparcelling Bundle
			/*
			 * Filter messages based on message type. Since it is likely that
			 * GCM will be extended in the future with new message types, just
			 * ignore any message types you're not interested in, or that you
			 * don't recognize.
			 */

			if (intent.getAction().equals(
					"com.google.android.c2dm.intent.REGISTRATION")) {
				Log.d(TAG, "Successfully registered GCM");
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				Log.d(TAG, "Message Type Error");
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				Log.d(TAG, "Message Type Deleted");
				// If it's a regular GCM message, do some work.
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {
				// This loop represents the service doing some work
				Log.d(TAG, "Received Notification with Payload");
				notificationIntents.add(intent);

				if (apiClient != null && apiClient.isConnected()) {
					fetchNotificationsAndPushInAsync(getGoogleAccountCredential());
				} else if (apiClient == null || !apiClient.isConnecting()) {
					connectApiClient();
				}

			}
		}
		// Release the wake lock provided by the WakefulBroadcastReceiver.
		ZeppaGCMReceiver.completeWakefulIntent(intent);

	}

	// Put the message into a notification and post it.
	// This is just one simple example of what you might choose to do with
	// a GCM message.

	private void connectApiClient() {
		String heldAccountName = getSharedPreferences(Constants.SHARED_PREFS,
				MODE_PRIVATE).getString(Constants.LOGGED_IN_ACCOUNT, null);

		// User is not logged in and should not receive the notification
		if (heldAccountName != null) {
			initializeApiClient(heldAccountName);
			apiClient.connect();
		}
	}

	/**
	 * This method builds an apiClient instance copied from authenticated
	 * fragment activity. Consider moving
	 * 
	 * @param accountName
	 */
	private void initializeApiClient(String accountName) {

		GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this,
				this, this);
		builder.setAccountName(accountName);
		builder.addApi(Plus.API);
		builder.addScope(Plus.SCOPE_PLUS_LOGIN);
		apiClient = builder.build();

	}

	/**
	 * 
	 * @return credential used to access App Engine API
	 */
	public GoogleAccountCredential getGoogleAccountCredential() {

		GoogleAccountCredential credential = GoogleAccountCredential
				.usingAudience(this, Constants.ANDROID_AUDIENCE);

		credential.setSelectedAccountName(Plus.AccountApi
				.getAccountName(apiClient));

		return credential;
	}

	private void fetchNotificationsAndPushInAsync(
			GoogleAccountCredential credential) {
		Log.d(TAG, "Fetching Notifications");
		if (notificationIntents.isEmpty()) {
			Log.d(TAG, "Notifications Intents Empty");
		} else {

			Object[] params = { credential };
			new AsyncTask<Object, Void, Void>() {

				@Override
				protected Void doInBackground(Object... params) {

					GoogleAccountCredential credential = (GoogleAccountCredential) params[0];

					try {

						// Verify that the application is logged in as the given
						// user
						ZeppaUserSingleton.getInstance()
								.fetchLoggedInUserWithBlocking(credential);

						Iterator<Intent> iterator = notificationIntents
								.iterator();

						while (iterator.hasNext()) {
							Intent intent = iterator.next();
							Long notificationId = Long.valueOf(intent
									.getExtras().getString("notificationId"));

							ZeppaNotification notification = NotificationSingleton
									.getInstance()
									.fetchNotificationAndElementsWithBlocking(
											credential, notificationId);
							if (notification != null) {
								pushZeppaNotification(notification);
							}

							notificationIntents.remove(intent);
						}

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					return null;
				}

			}.execute(params);


		}

	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void pushZeppaNotification(ZeppaNotification notification) {

		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification.Builder mBuilder = new Notification.Builder(this)
				.setSmallIcon(R.drawable.zeppa_icon)
				.setAutoCancel(true)
				.setContentTitle(
						NotificationSingleton.getInstance()
								.getNotificationTitle(notification))
				.setContentText(
						NotificationSingleton.getInstance()
								.getNotificationMessage(notification));

		PendingIntent intent = NotificationSingleton.getInstance()
				.getPendingIntent(this, notification);
		if (intent != null) {
			mBuilder.setContentIntent(intent);
		}

		if (Build.VERSION.SDK_INT >= 16) {
			mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		} else {
			mNotificationManager.notify(NOTIFICATION_ID,
					mBuilder.getNotification());
		}

	}

	@Override
	public void onConnected(Bundle connectionHint) {

		fetchNotificationsAndPushInAsync(getGoogleAccountCredential());

	}

	/*
	 * Unimplemented. If I cannot connect, don't bother trying to recover
	 */
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub

	}

}
