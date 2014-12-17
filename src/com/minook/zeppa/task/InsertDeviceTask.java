package com.minook.zeppa.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.deviceinfoendpoint.Deviceinfoendpoint;
import com.minook.zeppa.deviceinfoendpoint.Deviceinfoendpoint.ListDeviceInfo;
import com.minook.zeppa.deviceinfoendpoint.model.CollectionResponseDeviceInfo;
import com.minook.zeppa.deviceinfoendpoint.model.DeviceInfo;
import com.minook.zeppa.gcm.ZeppaGCMReceiver;

/**
 * Task to insert the current device into the backend
 * 
 * @author DrunkWithFunk21
 * 
 */
public class InsertDeviceTask extends AsyncTask<Void, Void, Void> {

	private Long userId;
	private Context context;
	private GoogleAccountCredential credential;

	public InsertDeviceTask(Context context, Long userId,
			GoogleAccountCredential credential) {
		this.context = context;
		this.userId = userId;
		this.credential = credential;
	}

	@Override
	protected Void doInBackground(Void... params) {

		Deviceinfoendpoint.Builder builder = new Deviceinfoendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				JacksonFactory.getDefaultInstance(), credential);

		CloudEndpointUtils.updateBuilder(builder);
		Deviceinfoendpoint endpoint = builder.build();

		DeviceInfo info = ZeppaGCMReceiver.getRegisteredDeviceInstance(context,
				userId);
		
		if(info == null || info.getRegistrationId() == null){
			return null;
		}

		List<DeviceInfo> userDevices = new ArrayList<DeviceInfo>();

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
			return null; // If error occurred, don't do anything.
		}

		// If one of the pulled devices matches this one, don't make it
		// persistent
		if (!userDevices.isEmpty()) {
			Iterator<DeviceInfo> iterator = userDevices.iterator();
			while (iterator.hasNext()) {
				if (iterator.next().getRegistrationId().trim()
						.equals(info.getRegistrationId().trim())) {
					return null;
				}
			}
		}

		try { // try to insert device. Exception left unhandled

			endpoint.insertDeviceInfo(info).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// TODO: Maintain a list of devices
		return null;
	}

}
