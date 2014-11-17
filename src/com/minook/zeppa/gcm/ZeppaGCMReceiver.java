package com.minook.zeppa.gcm;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.minook.zeppa.Constants;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.deviceinfoendpoint.model.DeviceInfo;
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
	public static DeviceInfo getRegisteredDeviceInstance(Context context, Long userId) {

		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
		
		DeviceInfo device = null;
		try {
			
			registrationId = gcm.register(Constants.PROJECT_NUMBER);
			Log.d(TAG, "gcm.register( " + registrationId + " )");
			device = new DeviceInfo();
			device.setOwnerId(userId);
			device.setPhoneType("ANDROID");
			device.setVersion(Constants.VERSION_CODE);
			device.setUpdate(Constants.UPDATE_CODE);
			device.setBugfix(Constants.BUGFIX_CODE);
			device.setRegistrationId(registrationId);
			

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

		Log.d(TAG, "received Google Cloud Message");

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

			// Handle incoming request.
			
		} else {
			Log.d(TAG, "WTF are you..? " + intent.toString());
		}

	}

	
	
	private void pushNotification(ZeppaNotification notification){
		
	}

}
