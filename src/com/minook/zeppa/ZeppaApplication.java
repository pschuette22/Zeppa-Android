package com.minook.zeppa;

import java.util.Calendar;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.example.calendarview.ImportEntries;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.database.PreferencesHelper;
import com.minook.zeppa.gcmhandler.ZeppaGCMReceiver;
import com.minook.zeppa.singleton.EventTagSingleton;
import com.minook.zeppa.singleton.NotificationSingleton;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUser;

public class ZeppaApplication extends Application {
	private final String TAG = getClass().getName();
	// Authorization
	private GoogleAccountCredential credential;

	/*
	 * ------------ Override Methods -------------
	 */

	@Override
	public void onCreate() {
		super.onCreate();

	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();

		ZeppaEventSingleton.getInstance().onLowMemory();
		ZeppaUserSingleton.getInstance().onLowMemory();

	}

	/*
	 * --------- Private Methods ----------------
	 */

	

	/*
	 * -------------- Public Methods --------------
	 */

	public String getAccountName() {
		return credential.getSelectedAccountName();
	}

	public void setCredential(GoogleAccountCredential credential) {
		this.credential = credential;
	}

	/*
	 * -------------- Setters --------------------
	 */

	public void initialize(ZeppaUser user) {

		Log.d(TAG, "Initializing with user at: " + System.currentTimeMillis());
		
		ZeppaUserSingleton userSingleton = ZeppaUserSingleton.getInstance();
		userSingleton.setUser(user);
		Log.d(TAG, "User set, starting loads: " + System.currentTimeMillis());

		userSingleton.loadFriends(this);

		ZeppaEventSingleton.getInstance().loadInitialEvents(credential);
		
		NotificationSingleton.getInstance().loadInitialNotificationsInAsync(this);
		EventTagSingleton.getInstance().loadTagsInAsync(this);
		
		new ImportEntries().execute(this);
		
		Object[] params = {this};
		new AsyncTask<Object, Void, Void>() {

			@Override
			protected Void doInBackground(Object... params) {
				ZeppaApplication application = (ZeppaApplication) params[0];
				ZeppaGCMReceiver.register(application);
				return null;
			}

		}.execute(params);

	}

	public void setGoogleAccountCredential(GoogleAccountCredential credential) {
		this.credential = credential;
	}

	/*
	 * -------------- Getters --------------------
	 */

	public boolean hasCredential() {
		return (credential == null ? false:true);
	}

	public GoogleAccountCredential getGoogleAccountCredential() {
		return credential;
	}

}
