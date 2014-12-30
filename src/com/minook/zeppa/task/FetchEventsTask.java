package com.minook.zeppa.task;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.zeppaeventendpoint.Zeppaeventendpoint;

public abstract class FetchEventsTask extends ZeppaEndpointTask {

	protected Long userId;

	protected FetchEventsTask(GoogleAccountCredential credential, Long userId) {
		super(credential);
		this.userId = userId;
	}

	protected Zeppaeventendpoint buildZeppaEventEndpoint() {
		Zeppaeventendpoint.Builder builder = new Zeppaeventendpoint.Builder(
				transport, factory, credential);
		builder = CloudEndpointUtils.updateBuilder(builder);
		return builder.build();

	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if (result) {
			ZeppaEventSingleton.getInstance().notifyObservers();
		}
	}

}
