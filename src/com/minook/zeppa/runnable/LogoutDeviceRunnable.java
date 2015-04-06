package com.minook.zeppa.runnable;

import java.io.IOException;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.deviceinfoendpoint.Deviceinfoendpoint;
import com.minook.zeppa.deviceinfoendpoint.model.DeviceInfo;

public class LogoutDeviceRunnable extends BaseRunnable {

	private DeviceInfo currentDevice;

	public LogoutDeviceRunnable(ZeppaApplication application,
			GoogleAccountCredential credential) {
		super(application, credential);
		this.currentDevice = application.getCurrentDeviceInfo();
	}

	@Override
	public void run() {

		Deviceinfoendpoint endpoint = buildDeviceInfoEndpoint();

		currentDevice.setLoggedIn(Boolean.FALSE);
		try {
			endpoint.updateDeviceInfo(currentDevice).execute();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
