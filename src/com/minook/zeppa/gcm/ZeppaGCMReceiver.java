package com.minook.zeppa.gcm;

import java.io.IOException;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.minook.zeppa.ZeppaApplication;


public class ZeppaGCMReceiver extends WakefulBroadcastReceiver {

	final private static String TAG = ZeppaGCMReceiver.class.getName();

	/**
	 * This parameter takes an info object and returns the corresponding</p>
	 * Zeppa App Engine deviceInfo object
	 * 
	 * @param context
	 * @return deviceInfo object for this device
	 */

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
		ComponentName comp = new ComponentName(context.getPackageName(),
                ZeppaGCMIntentService.class.getName());

		startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);

        
        
	}
	

}
