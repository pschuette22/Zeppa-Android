package com.minook.zeppa.runnable;


import com.appspot.zeppa_cloud_1821.zeppaclientapi.Zeppaclientapi;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.DeviceInfo;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ApiClientHelper;
import com.minook.zeppa.ZeppaApplication;

import java.io.IOException;

public class LogoutDeviceRunnable extends BaseRunnable {

	private DeviceInfo currentDevice;

	public LogoutDeviceRunnable(ZeppaApplication application,
			GoogleAccountCredential credential) {
		super(application, credential);
		this.currentDevice = application.getCurrentDeviceInfo();
	}

	@Override
	public void run() {

		ApiClientHelper helper = new ApiClientHelper();
		Zeppaclientapi api = helper.buildClientEndpoint();

		currentDevice.setLoggedIn(Boolean.FALSE);
		try {
			api.updateDeviceInfo(credential.getToken(), currentDevice).execute();

		} catch (IOException | GoogleAuthException e) {
			e.printStackTrace();
		}

	}

}
