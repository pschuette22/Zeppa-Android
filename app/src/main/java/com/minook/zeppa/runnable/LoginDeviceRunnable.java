package com.minook.zeppa.runnable;


import com.appspot.zeppa_cloud_1821.zeppaclientapi.Zeppaclientapi;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.CollectionResponseDeviceInfo;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.DeviceInfo;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ApiClientHelper;
import com.minook.zeppa.Constants;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.gcm.ZeppaGCMUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Task to insert the current device into the backend
 * 
 * @author DrunkWithFunk21
 * 
 */
public class LoginDeviceRunnable extends BaseRunnable {

	private Long userId;

	public LoginDeviceRunnable(ZeppaApplication application,
			GoogleAccountCredential credential, Long userId) {
		super(application, credential);
		this.userId = userId;
	}

	@Override
	public void run() {

		ApiClientHelper helper = new ApiClientHelper();
		Zeppaclientapi api = helper.buildClientEndpoint();

		DeviceInfo info = ZeppaGCMUtils.getRegisteredDeviceInstance(
				application, userId);

		if (info == null || info.getRegistrationId() == null) {
			return;
		}

		List<DeviceInfo> userDevices = new ArrayList<DeviceInfo>();

		try {
			String filter = "ownerId == " + userId.longValue();
			String cursor = null;

				Zeppaclientapi.ListDeviceInfo listInfoTask = api.listDeviceInfo(credential.getToken());

				listInfoTask.setFilter(filter);
				listInfoTask.setCursor(cursor);

				CollectionResponseDeviceInfo response = listInfoTask.execute();

				if (response != null && response.getItems() != null
						&& !response.getItems().isEmpty()) {
					userDevices.addAll(response.getItems());

				} else {
					cursor = null;
				}


			// If one of the pulled devices matches this one, don't make it
			// persistent
			if (!userDevices.isEmpty()) {
				Iterator<DeviceInfo> iterator = userDevices.iterator();
				while (iterator.hasNext()) {
					DeviceInfo device = iterator.next();

					// Check to see if device already exists
					try {

						String registrationId = device.getRegistrationId();
						if (registrationId != null
								&& registrationId.trim().equals(
										info.getRegistrationId().trim())) {

							setDeviceLoggedinInfo(device);
							device = api.updateDeviceInfo(credential.getToken(), device)
									.execute();

							application.setCurrentDeviceInfo(device);

							return;
						}
					} catch (NullPointerException e) {
						e.printStackTrace();
						return;
					}
				}
			}

			setDeviceLoggedinInfo(info);
			info = api.insertDeviceInfo(credential.getToken(), info).execute();
			application.setCurrentDeviceInfo(info);

		} catch (IOException | GoogleAuthException e) {
			e.printStackTrace();
			return; // If error occurred, don't do anything.
		}
	}

	private void setDeviceLoggedinInfo(DeviceInfo info) {
		info.setLoggedIn(Boolean.TRUE);
		info.setLastLogin(System.currentTimeMillis());
		info.setVersion(Constants.VERSION_CODE);
		info.setUpdate(Constants.UPDATE_CODE);
		info.setBugfix(Constants.BUGFIX_CODE);
	}

}
