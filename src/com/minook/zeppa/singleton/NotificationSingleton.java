package com.minook.zeppa.singleton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.Constants;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.zeppanotificationendpoint.Zeppanotificationendpoint;
import com.minook.zeppa.zeppanotificationendpoint.Zeppanotificationendpoint.GetNotificationList;
import com.minook.zeppa.zeppanotificationendpoint.model.CollectionResponseZeppaNotification;
import com.minook.zeppa.zeppanotificationendpoint.model.ZeppaNotification;

public class NotificationSingleton {
	private static NotificationSingleton singleton;

	private List<ZeppaNotification> notifications;
	private List<View> loaderViews;
	private boolean hasLoadedInitial;

	/*
	 * Instance Handlers
	 */

	private NotificationSingleton() {
		notifications = new ArrayList<ZeppaNotification>();
		loaderViews = new ArrayList<View>();
		hasLoadedInitial = false;
	}

	public static NotificationSingleton getInstance() {
		if (singleton == null)
			singleton = new NotificationSingleton();
		return singleton;
	}

	public void loadInitialNotificationsInAsync(Context context) {
		GoogleAccountCredential credential = ((ZeppaApplication) context
				.getApplicationContext()).getGoogleAccountCredential();
		loadInitialNotificationsInAsync(credential);
	}

	/*
	 * Getters
	 */

	public List<ZeppaNotification> getNotifications() {
		return notifications;
	}

	private Long getUserId() {
		return ZeppaUserSingleton.getInstance().getUserId();
	}

	public boolean hasLoadedInitial() {
		return hasLoadedInitial;
	}

	/*
	 * Setters
	 */

	public void hideOnInitialLoad(View view) {
		if (!loaderViews.contains(view))
			loaderViews.add(view);
	}

	public void addNotification(ZeppaNotification notification) {
		notifications.add(notification);
	}

	public void addAllNotifcations(List<ZeppaNotification> notifications) {

		notifications.removeAll(notifications);
		notifications.addAll(notifications);

		Collections.sort(notifications, Constants.NOTIFICAITON_COMPARATOR);
	}

	/*
	 * Private
	 */

	private void hideViews() {
		if (!loaderViews.isEmpty()) {
			for (View loaderView : loaderViews) {
				try {
					loaderView.setVisibility(View.GONE);
				} catch (NullPointerException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	/*
	 * Loaders
	 */

	private void loadInitialNotificationsInAsync(
			GoogleAccountCredential credential) {

		GoogleAccountCredential[] params = { credential };

		new AsyncTask<GoogleAccountCredential, Void, Void>() {

			@Override
			protected Void doInBackground(GoogleAccountCredential... params) {

				GoogleAccountCredential credential = params[0];

				Zeppanotificationendpoint.Builder endpointBuilder = new Zeppanotificationendpoint.Builder(
						AndroidHttp.newCompatibleTransport(),
						new JacksonFactory(), credential);
				endpointBuilder = CloudEndpointUtils
						.updateBuilder(endpointBuilder);
				Zeppanotificationendpoint notificationEndpoint = endpointBuilder
						.build();

				try {

					int start = 0;
					ZeppaNotification lastNotification = null;

					while (start <= 30
							|| (lastNotification != null && !lastNotification
									.getHasSeen())) {

						GetNotificationList getNotificationList = notificationEndpoint
								.getNotificationList(getUserId(), start);
						CollectionResponseZeppaNotification result = getNotificationList
								.execute();
						List<ZeppaNotification> resultList = result.getItems();

						if (resultList == null || resultList.isEmpty()) {
							break;
						} else {

							notifications.addAll(resultList);

							if (resultList.size() < 15) {
								break;
							} else {
								start += 15;
							}
						}

					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				hasLoadedInitial = true;
				hideViews();
			}
		}.execute(params);

	}

}
