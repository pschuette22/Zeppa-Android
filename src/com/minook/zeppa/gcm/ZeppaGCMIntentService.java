package com.minook.zeppa.gcm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
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
import com.minook.zeppa.PrefsManager;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.mediator.DefaultUserInfoMediator;
import com.minook.zeppa.runnable.NotificationReceivedRunnable;
import com.minook.zeppa.runnable.ThreadManager;
import com.minook.zeppa.singleton.EventTagSingleton;
import com.minook.zeppa.singleton.NotificationSingleton;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

public class ZeppaGCMIntentService extends IntentService implements
		ConnectionCallbacks, OnConnectionFailedListener {

	private static final String TAG = ZeppaGCMIntentService.class.getName();

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
				// Release the wake lock provided by the WakefulBroadcastReceiver.
				ZeppaGCMReceiver.completeWakefulIntent(intent);
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				Log.d(TAG, "Message Type Error");
				// Release the wake lock provided by the WakefulBroadcastReceiver.
				ZeppaGCMReceiver.completeWakefulIntent(intent);
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				Log.d(TAG, "Message Type Deleted");
				// If it's a regular GCM message, do some work.
				// Release the wake lock provided by the WakefulBroadcastReceiver.
				ZeppaGCMReceiver.completeWakefulIntent(intent);
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {
				// This loop represents the service doing some work

				if (intent.getExtras().getString("purpose")
						.equalsIgnoreCase("payload")) {
					Log.d(TAG, "Received Notification with Payload");
					notificationIntents.add(intent);

					if (apiClient != null && apiClient.isConnected()) {
						executeNotificationRunnables(getApplication(),
								getGoogleAccountCredential());
					} else if (apiClient == null || !apiClient.isConnecting()) {
						connectApiClient();
					}

				} else if (intent.getExtras().getString("purpose")
						.equalsIgnoreCase("unmingle")) {
					long userId = Long.valueOf(intent.getExtras().getString(
							"fromUserId"));
					try {

						DefaultUserInfoMediator mediator = (DefaultUserInfoMediator) ZeppaUserSingleton
								.getInstance().getAbstractUserMediatorById(
										userId);
						mediator.setUserRelationship(null);
						ZeppaEventSingleton.getInstance()
								.removeMediatorsForUser(userId);
						EventTagSingleton.getInstance().removeEventTagsForUser(
								userId);

						ZeppaUserSingleton.getInstance().notifyObservers();
						ZeppaEventSingleton.getInstance().notifyObservers();
						NotificationSingleton.getInstance().notifyObservers();
						
					} catch (NullPointerException e) {
						e.printStackTrace();
					}

				} else if(intent.getExtras().getString("purpose")
						.equalsIgnoreCase("accountdeleted")){
					
					long userId = Long.valueOf(intent.getExtras().getString(
							"fromUserId"));
					try {

						ZeppaUserSingleton.getInstance().removeHeldMediatorById(userId);
						ZeppaEventSingleton.getInstance()
								.removeMediatorsForUser(userId);
						EventTagSingleton.getInstance().removeEventTagsForUser(
								userId);
						
						ZeppaUserSingleton.getInstance().notifyObservers();
						ZeppaEventSingleton.getInstance().notifyObservers();
						NotificationSingleton.getInstance().notifyObservers();
						
					} catch (NullPointerException e) {
						e.printStackTrace();
					}
				} else {
					Log.d(TAG, "Notification Was Not Handled");
				}

			}
		}
		

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

	/**
	 * This method handles fetching the notification data and related elements
	 * It then verifies that the correct user received the notification and
	 * dispatches a push notification if appropriate
	 * 
	 * @param context
	 * @param credential
	 */
	private void executeNotificationRunnables(Context context,
			GoogleAccountCredential credential) {
		Log.d(TAG, "Fetching Notifications");
		if (notificationIntents.isEmpty()) {
			Log.d(TAG, "Notifications Intents Empty");
		} else {

			// Copy intents and clear global list
			List<Intent> copyIntents = new ArrayList<Intent>();
			copyIntents.addAll(notificationIntents);
			notificationIntents.clear();

			Iterator<Intent> iterator = copyIntents.iterator();

			while (iterator.hasNext()) {
				Intent intent = iterator.next();
				Long notificationId = Long.valueOf(intent.getExtras()
						.getString("notificationId"));

				ThreadManager.execute(new NotificationReceivedRunnable(
						(ZeppaApplication) getApplication(), credential,
						notificationId.longValue(), PrefsManager
								.getLoggedInUserId(getApplication())));
				
				// Release the wake lock provided by the WakefulBroadcastReceiver.
				ZeppaGCMReceiver.completeWakefulIntent(intent);

			}

		}

	}

	@Override
	public void onConnected(Bundle connectionHint) {

		executeNotificationRunnables(getApplication(),
				getGoogleAccountCredential());

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
