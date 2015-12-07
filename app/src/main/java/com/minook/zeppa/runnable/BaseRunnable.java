package com.minook.zeppa.runnable;


import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
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

}
