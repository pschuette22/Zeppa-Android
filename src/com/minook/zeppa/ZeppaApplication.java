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
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUser;

public class ZeppaApplication extends Application {

	private final String TAG = getClass().getName();

	private List<MemoryObserver> memoryObservers;

	private String authToken;
	private String refreshToken;

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

	@Override
	public void onTerminate() {
		super.onTerminate();
		onApplicationTerminate();
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

		ZeppaUserSingleton userSingleton = ZeppaUserSingleton.getInstance();
		userSingleton.setUser(user);

//		userSingleton.loadConnectedUsers(credential);
//		ZeppaEventSingleton.getInstance().loadInitialEvents(credential);
//		NotificationSingleton.getInstance().loadInitialNotificationsInAsync(
//				credential);
		EventTagSingleton.getInstance().loadTagsInAsync(credential);

	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
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
