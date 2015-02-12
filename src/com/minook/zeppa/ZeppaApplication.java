package com.minook.zeppa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Application;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.deviceinfoendpoint.model.DeviceInfo;
import com.minook.zeppa.observer.MemoryObserver;
import com.minook.zeppa.runnable.FetchInitialEventsRunnable;
import com.minook.zeppa.runnable.FetchInitialMinglersRunnable;
import com.minook.zeppa.runnable.FetchMyEventTagsRunnable;
import com.minook.zeppa.runnable.InsertDeviceRunnable;
import com.minook.zeppa.runnable.SyncZeppaCalendarRunnable;
import com.minook.zeppa.runnable.ThreadManager;
import com.minook.zeppa.singleton.EventTagSingleton;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

public class ZeppaApplication extends Application {

//	private final String TAG = getClass().getName();

	
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


	public void registerMemoryObserver(MemoryObserver observer) {
		memoryObservers.add(observer);
	}

	public void unregisterMemoryObservery(MemoryObserver observer) {
		memoryObservers.remove(observer);
	}
	
	public void setCurrentActivity(AuthenticatedFragmentActivity activity){
		this.currentActivity = activity;
	}
	
	public void removeCurrentActivityIfMatching(AuthenticatedFragmentActivity activity){
		if(this.currentActivity == activity){
			this.currentActivity = null;
		}
	}
	
	public void setCurrentDeviceInfo(DeviceInfo currentDeviceInfo){
		this.currentDeviceInfo = currentDeviceInfo;
	}
	
	public AuthenticatedFragmentActivity getCurrentActivity(){
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
		PrefsManager.setLoggedInAccountEmail(this, userSingleton.getUserMediator().getGmail());
		PrefsManager.setBaseNotificationPreferences(this);
		
	
		ThreadManager.execute(new FetchInitialMinglersRunnable(this, credential, userId));
		
		// Load this users event tags
		ThreadManager.execute(new FetchMyEventTagsRunnable(this, credential, userId));

		ThreadManager.execute(new InsertDeviceRunnable(this, credential, userId));

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

	private void onApplicationTerminate() {
		Iterator<MemoryObserver> iterator = memoryObservers.iterator();
		while (iterator.hasNext()) {
			MemoryObserver observer = iterator.next();
			if (observer.onApplicationTerminate()) {
				unregisterMemoryObservery(observer);
			}
		}
	}

}
