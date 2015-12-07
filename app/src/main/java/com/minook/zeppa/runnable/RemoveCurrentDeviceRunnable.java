package com.minook.zeppa.runnable;

import com.appspot.zeppa_cloud_1821.zeppaclientapi.Zeppaclientapi;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.DeviceInfo;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ApiClientHelper;
import com.minook.zeppa.ZeppaApplication;

import java.io.IOException;

public class RemoveCurrentDeviceRunnable extends BaseRunnable {

	private DeviceInfo info = null;

	public RemoveCurrentDeviceRunnable(ZeppaApplication application,
			GoogleAccountCredential credential) {
		super(application, credential);
		info = application.getCurrentDeviceInfo();
	}

	@Override
	public void run() {

		ApiClientHelper helper = new ApiClientHelper();
		Zeppaclientapi api = helper.buildClientEndpoint();

		try {
			if (info != null) {
				api.removeDeviceInfo(info.getId(), credential.getToken()).execute();
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
