package com.minook.zeppa.runnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.deviceinfoendpoint.Deviceinfoendpoint;
import com.minook.zeppa.deviceinfoendpoint.Deviceinfoendpoint.ListDeviceInfo;
import com.minook.zeppa.deviceinfoendpoint.model.CollectionResponseDeviceInfo;
import com.minook.zeppa.deviceinfoendpoint.model.DeviceInfo;
import com.minook.zeppa.gcm.ZeppaGCMUtils;

/**
 * Task to insert the current device into the backend
 * 
 * @author DrunkWithFunk21
 * 
 */
public class InsertDeviceRunnable extends BaseRunnable {

	
	private Long userId;

	
	public InsertDeviceRunnable(ZeppaApplication application,
			GoogleAccountCredential credential, Long userId) {
		super(application, credential);
		this.userId = userId;
	}


	@Override
	public void run() {
		
		DeviceInfo info = ZeppaGCMUtils.getRegisteredDeviceInstance(application,
				userId);

		if (info == null || info.getRegistrationId() == null) {
			return;
		}

		List<DeviceInfo> userDevices = new ArrayList<DeviceInfo>();
		Deviceinfoendpoint endpoint = buildDeviceInfoEndpoint();
		
		try {
			String filter = "ownerId == " + userId.longValue();
			String cursor = null;

			do {
				ListDeviceInfo listInfoTask = endpoint.listDeviceInfo();

				listInfoTask.setFilter(filter);
				listInfoTask.setCursor(cursor);
				listInfoTask.setLimit(25);

				CollectionResponseDeviceInfo response = listInfoTask.execute();

				if (response != null && response.getItems() != null
						&& !response.getItems().isEmpty()) {
					userDevices.addAll(response.getItems());
					cursor = response.getNextPageToken();

				} else {
					cursor = null;
				}

			} while (cursor != null);

		} catch (IOException e1) {
			e1.printStackTrace();
			return; // If error occurred, don't do anything.
		}

		// If one of the pulled devices matches this one, don't make it
		// persistent
		if (!userDevices.isEmpty()) {
			Iterator<DeviceInfo> iterator = userDevices.iterator();
			while (iterator.hasNext()) {
				DeviceInfo device = iterator.next();
				if (info.getRegistrationId().trim()
						.equals(info.getRegistrationId().trim())) {
					application.setCurrentDeviceInfo(device);
					return;
				}
			}
		}

		try { // try to insert device. Exception left unhandled

			info = endpoint.insertDeviceInfo(info).execute();
			application.setCurrentDeviceInfo(info);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	

	
	
//	public InsertDeviceTask(ZeppaApplication context, Long userId,
//			GoogleAccountCredential credential) {
//		this.context = context;
//		this.userId = userId;
//		this.credential = credential;
//	}
//
//	@Override
//	protected Void doInBackground(Void... params) {
//
//

//
//		return null;
//	}

}
