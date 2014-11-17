package com.minook.zeppa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Application;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.observer.MemoryObserver;
import com.minook.zeppa.singleton.EventTagSingleton;
import com.minook.zeppa.singleton.NotificationSingleton;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.task.InsertDeviceTask;
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUser;

public class ZeppaApplication extends Application {

//	private final String TAG = getClass().getName();

	private List<MemoryObserver> memoryObservers;

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

	public void registerMemoryObserver(MemoryObserver observer) {
		memoryObservers.add(observer);
	}

	public void unregisterMemoryObservery(MemoryObserver observer) {
		memoryObservers.remove(observer);
	}

	/*
	 * -------------- Setters --------------------
	 */

	public void initialize(ZeppaUser user, GoogleAccountCredential credential) {

		// Set the User singleton to hold the current user object
		ZeppaUserSingleton userSingleton = ZeppaUserSingleton.getInstance();
		userSingleton.setUser(user);
		// Load Users this user has relationships with
		userSingleton.loadConnectedUsers(this, credential, user.getId());
		
		// Load events to show in the feed
		ZeppaEventSingleton.getInstance().loadInitialEvents(credential, user.getId());
		// Load notifications for this user
		NotificationSingleton.getInstance().loadInitialNotificationsInAsync(
				credential, user.getId());
		// Load this users event tags
		EventTagSingleton.getInstance().loadMyTagsInAsync(credential, user.getId());

		new InsertDeviceTask(this, user.getId(), credential).execute();
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
