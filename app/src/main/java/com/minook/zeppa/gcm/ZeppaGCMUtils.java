package com.minook.zeppa.gcm;

import android.content.Context;
import android.util.Log;

import com.appspot.zeppa_cloud_1821.deviceinfoendpoint.model.DeviceInfo;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.minook.zeppa.Constants;

import java.io.IOException;

public class ZeppaGCMUtils {

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
			Log.d("GCMUtils", "gcm.register( " + registrationId + " )");
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
	
}
