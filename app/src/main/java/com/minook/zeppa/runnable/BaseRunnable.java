package com.minook.zeppa.runnable;

import com.appspot.zeppa_cloud_1821.deviceinfoendpoint.Deviceinfoendpoint;
import com.appspot.zeppa_cloud_1821.eventcommentendpoint.Eventcommentendpoint;
import com.appspot.zeppa_cloud_1821.eventtagendpoint.Eventtagendpoint;
import com.appspot.zeppa_cloud_1821.eventtagfollowendpoint.Eventtagfollowendpoint;
import com.appspot.zeppa_cloud_1821.zeppaeventendpoint.Zeppaeventendpoint;
import com.appspot.zeppa_cloud_1821.zeppaeventtouserrelationshipendpoint.Zeppaeventtouserrelationshipendpoint;
import com.appspot.zeppa_cloud_1821.zeppanotificationendpoint.Zeppanotificationendpoint;
import com.appspot.zeppa_cloud_1821.zeppauserendpoint.Zeppauserendpoint;
import com.appspot.zeppa_cloud_1821.zeppauserinfoendpoint.Zeppauserinfoendpoint;
import com.appspot.zeppa_cloud_1821.zeppausertouserrelationshipendpoint.Zeppausertouserrelationshipendpoint;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.adapter.tagadapter.AbstractTagAdapter;
import com.minook.zeppa.singleton.NotificationSingleton;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

public abstract class BaseRunnable implements Runnable {

	protected ZeppaApplication application;
	protected GoogleAccountCredential credential;
	private HttpTransport transport = AndroidHttp.newCompatibleTransport();
	private GsonFactory factory = GsonFactory.getDefaultInstance();

	public BaseRunnable(ZeppaApplication application,
			GoogleAccountCredential credential) {
		this.application = application;
		this.credential = credential;
	}

	@Override
	public abstract void run();

	protected void notifyEventObservers() {
		try {

			application.getCurrentActivity().runOnUiThread(new Runnable() {
				public void run() {
					ZeppaEventSingleton.getInstance().notifyObservers();
				}
			});

		} catch (NullPointerException e) {

		}
	}

	protected void notifyUserObservers() {
		try {

			application.getCurrentActivity().runOnUiThread(new Runnable() {
				public void run() {
					ZeppaUserSingleton.getInstance();
				}
			});

		} catch (NullPointerException e) {

		}
	}

	protected void notifyNotificationObservers() {
		try {

			application.getCurrentActivity().runOnUiThread(new Runnable() {
				public void run() {
					NotificationSingleton.getInstance().addNotification(null);
				}
			});

		} catch (NullPointerException e) {

		}
	}

	protected void notifyEventTagSingleton(final AbstractTagAdapter tagAdapter) {
		try {

			application.getCurrentActivity().runOnUiThread(new Runnable() {
				public void run() {
					tagAdapter.notifyDataSetChanged();
				}
			});

		} catch (NullPointerException e) {

		}
	}

	/*
	 * The following are builder methods for accessing the Zeppa Database
	 */

	protected Zeppauserinfoendpoint buildUserInfoEndpoint() {
		Zeppauserinfoendpoint.Builder builder = new Zeppauserinfoendpoint.Builder(
				transport, factory, credential);
		builder = CloudEndpointUtils.updateBuilder(builder);
		Zeppauserinfoendpoint endpoint = builder.build();
		return endpoint;
	}

	protected Zeppauserendpoint buildZeppaUserEndpoint() {
		Zeppauserendpoint.Builder builder = new Zeppauserendpoint.Builder(
				transport, factory, credential);
		builder = CloudEndpointUtils.updateBuilder(builder);
		Zeppauserendpoint endpoint = builder.build();
		return endpoint;
	}

	protected Zeppausertouserrelationshipendpoint buildZeppaUserToUserRelationshipEndpoint() {
		Zeppausertouserrelationshipendpoint.Builder builder = new Zeppausertouserrelationshipendpoint.Builder(
				transport, factory, credential);
		builder = CloudEndpointUtils.updateBuilder(builder);
		Zeppausertouserrelationshipendpoint endpoint = builder.build();
		return endpoint;
	}

	protected Zeppaeventendpoint buildEventEndpoint() {
		Zeppaeventendpoint.Builder builder = new Zeppaeventendpoint.Builder(
				transport, factory, credential);
		CloudEndpointUtils.updateBuilder(builder);
		Zeppaeventendpoint endpoint = builder.build();

		return endpoint;
	}

	protected Zeppaeventtouserrelationshipendpoint buildEventRelationshipEndpoint() {
		Zeppaeventtouserrelationshipendpoint.Builder builder = new Zeppaeventtouserrelationshipendpoint.Builder(
				transport, factory, credential);
		CloudEndpointUtils.updateBuilder(builder);
		Zeppaeventtouserrelationshipendpoint endpoint = builder.build();

		return endpoint;
	}

	protected Zeppanotificationendpoint buildNotificationEndpoint() {
		Zeppanotificationendpoint.Builder endpointBuilder = new Zeppanotificationendpoint.Builder(
				transport, factory, credential);

		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
		Zeppanotificationendpoint notificationEndpoint = endpointBuilder
				.build();

		return notificationEndpoint;
	}

	protected Eventtagfollowendpoint buildEventTagFollowEndpoint() {
		Eventtagfollowendpoint.Builder endpointBuilder = new Eventtagfollowendpoint.Builder(
				transport, factory, credential);
		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
		return endpointBuilder.build();
	}

	protected Eventtagendpoint buildEventTagEndpoint() {
		Eventtagendpoint.Builder endpointBuilder = new Eventtagendpoint.Builder(
				transport, factory, credential);
		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
		return endpointBuilder.build();
	}

	protected Deviceinfoendpoint buildDeviceInfoEndpoint() {
		Deviceinfoendpoint.Builder builder = new Deviceinfoendpoint.Builder(
				transport, factory, credential);

		CloudEndpointUtils.updateBuilder(builder);
		return builder.build();
	}

	protected Eventcommentendpoint buildCommentEndpoint() {
		Eventcommentendpoint.Builder builder = new Eventcommentendpoint.Builder(
				transport, factory, credential);
		builder = CloudEndpointUtils.updateBuilder(builder);
		return builder.build();
	}

}
