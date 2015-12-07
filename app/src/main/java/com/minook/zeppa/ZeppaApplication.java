package com.minook.zeppa;

import android.app.Application;

import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.DeviceInfo;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.observer.MemoryObserver;
import com.minook.zeppa.runnable.FetchInitialMinglersRunnable;
import com.minook.zeppa.runnable.FetchMyEventTagsRunnable;
import com.minook.zeppa.runnable.LoginDeviceRunnable;
import com.minook.zeppa.runnable.SyncZeppaCalendarRunnable;
import com.minook.zeppa.runnable.ThreadManager;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ZeppaApplication extends Application {

	// private final String TAG = getClass().getName();

	private List<MemoryObserver> memoryObservers;
	private AuthenticatedFragmentActivity currentActivity;
	private DeviceInfo currentDeviceInfo;

	/*
	 * ------------ Override Methods -------------
	 */

	@Override
	public void onCreate() {
		super.onCreate();
		memoryObservers = new ArrayList<MemoryObserver>();
	}

	
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();

	}

	/*
	 * --------- Private Methods ----------------
	 */

	/*
	 * -------------- Public Methods --------------
	 */

	public DeviceInfo getCurrentDeviceInfo() {
		return currentDeviceInfo;
	}

	public void setCurrentDeviceInfo(DeviceInfo currentDeviceInfo) {
		this.currentDeviceInfo = currentDeviceInfo;
	}

	public void registerMemoryObserver(MemoryObserver observer) {
		memoryObservers.add(observer);
	}

	public void unregisterMemoryObservery(MemoryObserver observer) {
		memoryObservers.remove(observer);
	}

	public void setCurrentActivity(AuthenticatedFragmentActivity activity) {
		this.currentActivity = activity;
	}

	

	public AuthenticatedFragmentActivity getCurrentActivity() {
		return currentActivity;
	}

	/*
	 * -------------- Setters --------------------
	 */

	public void initialize(GoogleAccountCredential credential) {

		// Set the User singleton to hold the current user object
		ZeppaUserSingleton userSingleton = ZeppaUserSingleton.getInstance();
		Long userId = userSingleton.getUserId();
		// Set shared preferences
		PrefsManager.setLoggedInUserId(this, userId);
		PrefsManager.setLoggedInAccountEmail(this, credential.getSelectedAccountName());
		PrefsManager.setBaseNotificationPreferences(this);

		ThreadManager.execute(new FetchInitialMinglersRunnable(this,
				credential, userId));

		// Load this users event tags
		ThreadManager.execute(new FetchMyEventTagsRunnable(this, credential,
				userId));

		ThreadManager
				.execute(new LoginDeviceRunnable(this, credential, userId));

		ThreadManager.execute(new SyncZeppaCalendarRunnable(this, credential));

	}

	/*
	 * ------------ Memory Management -------------
	 */

	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);

		switch (level) {
		case TRIM_MEMORY_BACKGROUND:
			onMemoryWarning();
			break;

		case TRIM_MEMORY_RUNNING_LOW:
			onMemoryLow();
			break;

		case TRIM_MEMORY_COMPLETE:
			onMemoryCritical();
			break;

		}
	}

	private void onMemoryWarning() {
		Iterator<MemoryObserver> iterator = memoryObservers.iterator();
		while (iterator.hasNext()) {
			MemoryObserver observer = iterator.next();
			if (observer.onMemoryWarning()) {
				unregisterMemoryObservery(observer);
			}
		}

	}

	private void onMemoryLow() {
		Iterator<MemoryObserver> iterator = memoryObservers.iterator();
		while (iterator.hasNext()) {
			MemoryObserver observer = iterator.next();
			if (observer.onMemoryLow()) {
				unregisterMemoryObservery(observer);
			}
		}
	}

	private void onMemoryCritical() {
		Iterator<MemoryObserver> iterator = memoryObservers.iterator();
		while (iterator.hasNext()) {
			MemoryObserver observer = iterator.next();
			if (observer.onMemoryCritical()) {
				unregisterMemoryObservery(observer);
			}
		}
	}


}
