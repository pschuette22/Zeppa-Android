package com.minook.zeppa.gcm;

import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.activity.AbstractEventViewActivity;
import com.minook.zeppa.activity.MainActivity;
import com.minook.zeppa.activity.NewFriendsActivity;
import com.minook.zeppa.activity.UserActivity;
import com.minook.zeppa.deviceinfoendpoint.model.DeviceInfo;
import com.minook.zeppa.singleton.NotificationSingleton;
import com.minook.zeppa.zeppanotificationendpoint.Zeppanotificationendpoint;
import com.minook.zeppa.zeppanotificationendpoint.Zeppanotificationendpoint.GetUnseenNotifications;
import com.minook.zeppa.zeppanotificationendpoint.model.CollectionResponseZeppaNotification;
import com.minook.zeppa.zeppanotificationendpoint.model.ZeppaNotification;


public class ZeppaGCMReceiver extends WakefulBroadcastReceiver {

	final private static String TAG = "GCMIntentService";
	private static String registrationId = null;

	/**
	 * This parameter takes an info object and returns the corresponding</p>
	 * Zeppa App Engine deviceInfo object
	 * 
	 * @param context
	 * @return deviceInfo object for this device
	 */
	public static DeviceInfo register(Context context) {

		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
		
		DeviceInfo device = null;
		try {
			
			registrationId = gcm.register(Constants.PROJECT_NUMBER);
			Log.d(TAG, "gcm.register( " + registrationId + " )");
			device = new DeviceInfo();
			device.setPhoneType("ANDROID");
			device.setRegistrationId(registrationId);
			device.setTimeRegisteredInMillis(Long.valueOf(System.currentTimeMillis()));

		} catch (IOException e) {
			e.printStackTrace();
		}

		return device;
	}

	public static void unregister(final ZeppaApplication application) {
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(application
				.getApplicationContext());
		// TODO: registration id in preferences and
		try {
			gcm.unregister();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onReceive(Context context, Intent intent) {

		Log.d(TAG, "received message ping");

		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
		String messageType = gcm.getMessageType(intent);

		Log.d(TAG, "MessageType: " + messageType);

		if (messageType == null) {
			Log.d(TAG, "Message is null");
			return;
		} else if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
				.equals(messageType)) {
			Log.d(TAG, "Error!");
			return;
		} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
				.equals(messageType)) {
			Log.d(TAG, "Deleted");
			return;
		} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
				.equals(messageType)) {
			Log.d(TAG, "Message");
			handlePingInAsync(context);
		} else {
			Log.d(TAG, "WTF are you..? " + intent.toString());
		}

	}

	private void handlePingInAsync(Context context) {

		Context[] param = { context };
		new AsyncTask<Context, Void, Void>() {

			@Override
			protected Void doInBackground(Context... params) {
				Context context = params[0];
				GoogleCredential credential = getCredential(context);
				if (credential == null) {
					return null;
				}

				Zeppanotificationendpoint.Builder endpointBuilder = new Zeppanotificationendpoint.Builder(
						new NetHttpTransport(),
						new JacksonFactory(), credential);
				endpointBuilder = CloudEndpointUtils
						.updateBuilder(endpointBuilder);
				Zeppanotificationendpoint notificationEndpoint = endpointBuilder
						.build();

				try {
					SharedPreferences prefs = context.getSharedPreferences(
							Constants.SHARED_PREFS, Context.MODE_PRIVATE);

					Long userId = prefs.getLong(Constants.USER_ID, -1);
					if (userId > 0) {
						GetUnseenNotifications getUnseenNotifications = notificationEndpoint
								.getUnseenNotifications(userId);
						CollectionResponseZeppaNotification collectionResponse = getUnseenNotifications
								.execute();
						if (collectionResponse == null
								|| collectionResponse.getItems() == null) {
						} else {

							List<ZeppaNotification> notifications = collectionResponse
									.getItems();
							sendNotificationsForResult(notifications, context);

							try {
								NotificationSingleton.getInstance()
										.addAllNotifications(notifications);
							} catch (NullPointerException ex) {
								ex.printStackTrace();
							}
						}

					} else {
						Log.d(TAG, "No Set userId");
					}
				} catch (IOException ioEx) {
					ioEx.printStackTrace();
				}
				return null;

			}

		}.execute(param);

	}

	private GoogleCredential getCredential(Context context) {
//		GoogleCredential credential = ((ZeppaApplication) context
//				.getApplicationContext()).getGoogleCredential();
//		if (credential == null) {
//			SharedPreferences prefs = context.getSharedPreferences(
//					Constants.SHARED_PREFS, Context.MODE_PRIVATE);
//			String email = prefs.getString(Constants.EMAIL_ADDRESS, null);
//			if (email != null && !email.isEmpty() && Constants.IS_CONNECTED) {
//				credential = GoogleCredential.usingAudience(context,
//						Constants.APP_ENGINE_AUDIENCE_CODE);
//				credential.setSelectedAccountName(email);
//				return credential;
//			}
//
//			return null;
//		} else {
//			return credential;
//		}
		
		return null;
	}

	
	
	
	
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void sendNotificationsForResult(List<ZeppaNotification> resultList,
			Context context) {

		Log.d(TAG, "trying to send Notifications for result");
		Notification.Builder notifBuilder = new Notification.Builder(context);

		if (resultList.size() > 1) {
			notifBuilder.setContentTitle(resultList.size()
					+ " new notifications");

			StringBuilder stringBuilder = new StringBuilder();
			for (ZeppaNotification notification : resultList) {
				stringBuilder.append(notification.getExtraMessage()).append(
						'\n');
			}

			notifBuilder.setContentText(stringBuilder.toString());

			Intent intent = new Intent(context, MainActivity.class);
			intent.putExtra(Constants.INTENT_NOTIFICATIONS, true);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
					intent, 0);
			notifBuilder.setContentIntent(pendingIntent);
		} else {
			ZeppaNotification notification = resultList.get(0);
			manageSingleNotification(context, notification, notifBuilder);
			notifBuilder.setContentText(notification.getExtraMessage());
		}

		notifBuilder.setLights(Color.CYAN, 750, 3000);
		notifBuilder.setAutoCancel(true);
		notifBuilder.setSmallIcon(R.drawable.notif_ic_zeppa);
		Notification notification = null;

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
			notifBuilder.setPriority(Notification.PRIORITY_DEFAULT);
			notification = notifBuilder.build();
		} else {
			notification = notifBuilder.getNotification();
		}
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Log.d(TAG, "Notification Should Post");
		notificationManager.notify(0, notification);

	}

	private void manageSingleNotification(Context context,
			ZeppaNotification notification, Notification.Builder builder) {

		Intent intent = null;
		switch (notification.getNotificationOrdinal()) {
		case 0:
			builder.setContentTitle("New Friend Request");
			intent = new Intent(context, NewFriendsActivity.class);
			break;
		case 1:
			builder.setContentTitle("New Connection");
			intent = new Intent(context, UserActivity.class);
			intent.putExtra(Constants.INTENT_ZEPPA_USER_ID,
					notification.getFromUserId());
			break;

		case 2:
			builder.setContentTitle("Event Recommendation");
			intent = new Intent(context, AbstractEventViewActivity.class);
			intent.putExtra(Constants.INTENT_ZEPPA_EVENT_ID,
					notification.getEventId());
			break;

		case 3:
			builder.setContentTitle("New Invite");
			intent = new Intent(context, AbstractEventViewActivity.class);
			intent.putExtra(Constants.INTENT_ZEPPA_EVENT_ID,
					notification.getEventId());
			break;

		case 4:
			builder.setContentTitle("Event Comment");
			intent = new Intent(context, AbstractEventViewActivity.class);
			intent.putExtra(Constants.INTENT_ZEPPA_EVENT_ID,
					notification.getEventId());
			break;

		case 5:
			builder.setContentTitle("Event Canceled");
			intent = new Intent(context, MainActivity.class);
			intent.putExtra(Constants.INTENT_NOTIFICATIONS, false);
			break;

		case 6:
			builder.setContentTitle("Event Updated");
			intent = new Intent(context, AbstractEventViewActivity.class);
			intent.putExtra(Constants.INTENT_ZEPPA_EVENT_ID,
					notification.getEventId());
			break;

		case 7:
			builder.setContentTitle("Friend Joined Event");
			intent = new Intent(context, AbstractEventViewActivity.class);
			intent.putExtra(Constants.INTENT_ZEPPA_EVENT_ID,
					notification.getEventId());
			break;

		case 8:
			builder.setContentTitle("Friend Left Event");
			intent = new Intent(context, AbstractEventViewActivity.class);
			intent.putExtra(Constants.INTENT_ZEPPA_EVENT_ID,
					notification.getEventId());
			break;

		case 9:
			builder.setContentTitle("Let's Find a Time?");
			break;

		case 10:
			builder.setContentTitle("Time Found!");
			break;

		case 11:
			builder.setContentTitle("Event Reposted");
			intent = new Intent(context, AbstractEventViewActivity.class);
			intent.putExtra(Constants.INTENT_ZEPPA_EVENT_ID,
					notification.getEventId());
			break;

		default: // this shouldnt happen
			builder.setContentTitle("New Zeppa Notification");
			intent = new Intent(context, MainActivity.class);
			intent.putExtra(Constants.INTENT_NOTIFICATIONS, false);
			break;
		}

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, 0);

		builder.setContentIntent(pendingIntent);
	}

}
