package com.minook.zeppa.runnable;

import com.appspot.zeppa_cloud_1821.deviceinfoendpoint.model.DeviceInfo;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ZeppaApplication;

import java.io.IOException;

public class RemoveCurrentDeviceRunnable extends BaseRunnable {

	private DeviceInfo info;

	public RemoveCurrentDeviceRunnable(ZeppaApplication application,
			GoogleAccountCredential credential) {
		super(application, credential);
		info = application.getCurrentDeviceInfo();
	}

	@Override
	public void run() {
		try {
			if (info != null) {
				buildDeviceInfoEndpoint().removeDeviceInfo(info).execute();
				application.setCurrentDeviceInfo(null);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
