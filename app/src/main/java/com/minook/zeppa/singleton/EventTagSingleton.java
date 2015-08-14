package com.minook.zeppa.singleton;

import android.util.Log;

import com.appspot.zeppa_cloud_1821.eventtagendpoint.Eventtagendpoint;
import com.appspot.zeppa_cloud_1821.eventtagendpoint.model.EventTag;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAuthIOException;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.adapter.tagadapter.MyTagAdapter;
import com.minook.zeppa.mediator.AbstractEventTagMediator;
import com.minook.zeppa.mediator.DefaultEventTagMediator;
import com.minook.zeppa.mediator.MyEventTagMediator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * This class holds all the Event Tag Managers for the users event Tags. it does
 * not hold other friends event tags because they are held by the
 * UserInfoManager
 * 
 * @author DrunkWithFunk21
 * 
 */
public class EventTagSingleton {

	public interface OnTagLoadListener {
		public void onTagsLoaded();
		public void onErrorLoadingTags();
	}

	private static EventTagSingleton singleton;

	private final String TAG = "EventTagSingleton";
	private List<AbstractEventTagMediator> tagMediators;
	private MyTagAdapter waitingAdapter;
	private boolean hasLoadedTags;

	/*
	 * Instance Handlers
	 */

	private EventTagSingleton() {
		tagMediators = new ArrayList<AbstractEventTagMediator>();
		waitingAdapter = null;
		hasLoadedTags = false;
	}

	/*
	 * Basic Singleton instance grabber
	 */
	public static EventTagSingleton getInstance() {
		if (singleton == null) // if null, create a new instance and set
			singleton = new EventTagSingleton();
		return singleton; // return instance
	}

	public void restore() {
		singleton = new EventTagSingleton();
	}

	/*
	 * Create new tag instance
	 */
	public EventTag newTagInstance() {
		EventTag tag = new EventTag();
		tag.setOwnerId(getUserId());
		return tag;
	}

	public void clear() {
		tagMediators.clear();
	}

	/**
	 * This method sets an adapter that is waiting for this users tags to load.
	 * 
	 * @param adapter
	 */
	public void setWaitingAdapter(MyTagAdapter adapter) {
		this.waitingAdapter = adapter;
	}

	/*
	 * Getters
	 */

	private Long getUserId() {
		return ZeppaUserSingleton.getInstance().getUserId();
	}

	// /**
	// * This method returns all held AbstractEventTagMediators
	// * @return List<AbstractEventTagMediator>
	// */
	// public List<AbstractEventTagMediator> getTags() {
	// return tagMediators;
	// }

	/**
	 * This method returns all held tag mediators for users tags
	 * 
	 * @return List<MyEventTagMediator>
	 */
	public List<AbstractEventTagMediator> getMyTags() {
		return getTagMediatorsForUser(getUserId());
	}

	/**
	 * get a single mediator for a given event ID if it is held.
	 * 
	 * @param tagId
	 *            - Event Tag in question
	 * @return AbstractEventTagMediator - Abstract Mediator for Event Tag
	 */
	public AbstractEventTagMediator getTagMediatorWithId(Long tagId) {

		for (AbstractEventTagMediator mediator : tagMediators) {
			if (mediator.getTagId().equals(tagId)) {
				return mediator;
			}
		}

		return null;
	}

	public void updateEventTagsForUser(long userId,
			List<DefaultEventTagMediator> mediators) {

		List<AbstractEventTagMediator> remove = getTagMediatorsForUser(userId);
		tagMediators.removeAll(remove);
		tagMediators.addAll(mediators);

	}

	/**
	 * get a list of all event tag mediators for a given list of event tag ids
	 * 
	 * @param tagIds
	 *            - list of requested tag ids
	 * @return List<AbstractEventTagMediator> - result list of requested tags
	 */
	public List<AbstractEventTagMediator> getTagsFrom(List<Long> tagIds) {

		List<AbstractEventTagMediator> result = new ArrayList<AbstractEventTagMediator>();

		if (tagIds == null || tagIds.isEmpty()) {
			Log.d(TAG, "Null Array Passed");
		} else {
			for (Long tagId : tagIds) {
				AbstractEventTagMediator mediator = getTagMediatorWithId(tagId);
				if (mediator != null) {
					result.add(mediator);
				}
			}
		}
		return result;
	}

	public void onMyTagsLoaded() {
		this.hasLoadedTags = true;
		if (waitingAdapter != null) {
			waitingAdapter.notifyDataSetChanged();
		}
	}

	public List<DefaultEventTagMediator> getDefaultTagsFrom(List<Long> tagIds) {
		List<AbstractEventTagMediator> abstractMediators = getTagsFrom(tagIds);
		List<DefaultEventTagMediator> result = new ArrayList<DefaultEventTagMediator>();

		for (AbstractEventTagMediator mediator : abstractMediators) {
			try {
				result.add((DefaultEventTagMediator) mediator);
			} catch (ClassCastException ex) {
				Log.wtf(TAG,
						"tried to get MyEventTagMediator for other users tag");
			}
		}

		return result;
	}

	public List<AbstractEventTagMediator> getTagMediatorsForUser(long userId) {
		List<AbstractEventTagMediator> result = new ArrayList<AbstractEventTagMediator>();

		for (AbstractEventTagMediator mediator : tagMediators) {
			if (mediator.getUserId().longValue() == userId) {
				result.add(mediator);
			}
		}

		return result;
	}

	public boolean didLoadInitialTags() {
		return hasLoadedTags;
	}

	/*
	 * Setters
	 */

	public boolean removeEventTagMediator(AbstractEventTagMediator mediator) {
		return tagMediators.remove(mediator);
	}

	public void addEventTags(List<AbstractEventTagMediator> tagMediators, boolean doClear) {

		if(doClear){
			this.tagMediators.clear();
		}

		this.tagMediators.addAll(tagMediators);
	}

	public void removeEventTagsForUser(long userId) {

		Iterator<AbstractEventTagMediator> iterator = tagMediators.iterator();
		List<AbstractEventTagMediator> removalList = new ArrayList<AbstractEventTagMediator>();
		while (iterator.hasNext()) {
			AbstractEventTagMediator mediator = iterator.next();
			if (mediator.getUserId().longValue() == userId) {
				removalList.add(mediator);
			}
		}

		tagMediators.removeAll(removalList);
	}

	/*
	 * Private
	 */

	/*
	 * Loader Methods
	 */

	/**
	 * This is a blocking call which creates a new event tag in the backedn and
	 * returns the mediator.
	 * 
	 * @param tag
	 * @param credential
	 * @return mediator
	 */
	public MyEventTagMediator insertEventTag(EventTag tag,
			GoogleAccountCredential credential) {

		Eventtagendpoint.Builder endpointBuilder = new Eventtagendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				AndroidJsonFactory.getDefaultInstance(), credential);
		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
		Eventtagendpoint endpoint = endpointBuilder.build();

		try {

			tag = endpoint.insertEventTag(tag).execute();

		} catch (GoogleAuthIOException aEx) {
			Log.wtf(TAG, "AuthException");
		} catch (IOException ioEx) {
			tag = null;
			ioEx.printStackTrace();
		}

		if (tag != null) {
			AbstractEventTagMediator mediator = new MyEventTagMediator(tag);
			addEventTags(Arrays.asList(mediator), false);

			return ((MyEventTagMediator) mediator);
		} else {
			return null;
		}

	}

	/**
	 * This method deletes an event tag and all its instances on the device and
	 * in the backend
	 * 
	 * @param tag
	 *            - event tag to be deleted
	 * @param credential
	 *            - authorization credential
	 * @return true if transaction was successful
	 */
	public boolean deleteEventTag(EventTag tag,
			GoogleAccountCredential credential) {
		boolean success = false;

		Eventtagendpoint.Builder endpointBuilder = new Eventtagendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				AndroidJsonFactory.getDefaultInstance(), credential);
		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
		Eventtagendpoint endpoint = endpointBuilder.build();

		try {

			endpoint.removeEventTag(tag.getKey().getId()).execute();

			success = true;
		} catch (GoogleAuthIOException aEx) {
			Log.wtf(TAG, "AuthException");
		} catch (IOException ioEx) {
			ioEx.printStackTrace();
		}

		// TODO: remove all instances of this tag

		return success;
	}

}
