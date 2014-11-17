package com.minook.zeppa.singleton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAuthIOException;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.adapter.tagadapter.MyTagAdapter;
import com.minook.zeppa.eventtagendpoint.Eventtagendpoint;
import com.minook.zeppa.eventtagendpoint.Eventtagendpoint.ListEventTag;
import com.minook.zeppa.eventtagendpoint.model.CollectionResponseEventTag;
import com.minook.zeppa.eventtagendpoint.model.EventTag;
import com.minook.zeppa.eventtagfollowendpoint.Eventtagfollowendpoint;
import com.minook.zeppa.eventtagfollowendpoint.model.EventTagFollow;
import com.minook.zeppa.mediator.AbstractEventTagMediator;
import com.minook.zeppa.mediator.DefaultEventTagMediator;
import com.minook.zeppa.mediator.MyEventTagMediator;

/**
 * This class holds all the Event Tag Managers for the users event Tags. it does
 * not hold other friends event tags because they are held by the
 * UserInfoManager
 * 
 * @author DrunkWithFunk21
 * 
 */
public class EventTagSingleton {
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

	/*
	 * Create new tag instance
	 */
	public EventTag newTagInstance() {
		EventTag tag = new EventTag();
		tag.setOwnerId(getUserId());
		return tag;
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
				return ((MyEventTagMediator) mediator);
			}
		}

		return null;
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

	/**
	 * get a list of users event tag mediators from list
	 * 
	 * @param tagIds
	 *            - list of tags in question
	 * @return List<MyEventTagMediator> - list of mediators for given list
	 */
	public List<MyEventTagMediator> getMyTagsFrom(List<Long> tagIds) {
		List<AbstractEventTagMediator> abstractMediators = getTagsFrom(tagIds);
		List<MyEventTagMediator> result = new ArrayList<MyEventTagMediator>();

		for (AbstractEventTagMediator mediator : abstractMediators) {
			try {
				result.add((MyEventTagMediator) mediator);
			} catch (ClassCastException ex) {
				Log.wtf(TAG,
						"tried to get MyEventTagMediator for other users tag");
			}
		}

		return result;
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

	public List<AbstractEventTagMediator> getTagMediatorsForUser(Long userId) {
		List<AbstractEventTagMediator> result = new ArrayList<AbstractEventTagMediator>();

		for (AbstractEventTagMediator mediator : tagMediators) {
			if (mediator.getUserId().longValue() == userId.longValue()) {
				result.add(mediator);
			}
		}

		return result;
	}

	public boolean hasLoadedTags() {
		return hasLoadedTags;
	}

	/*
	 * Setters
	 */

	private AbstractEventTagMediator addMyEventTag(EventTag tag) {

		AbstractEventTagMediator myTagMediator = new MyEventTagMediator(tag);
		tagMediators.add(myTagMediator);
		return myTagMediator;
	}

	private AbstractEventTagMediator addDefaultTagMediator(EventTag tag,
			GoogleAccountCredential credential) {

		AbstractEventTagMediator tagMediator = new DefaultEventTagMediator(tag,
				credential);
		tagMediators.add(tagMediator);
		return tagMediator;
	}

	/*
	 * Private
	 */

	/*
	 * Loader Methods
	 */

	/**
	 * This method loads the current users Event Tags in a new thread
	 * 
	 * @param credential
	 */
	public void loadMyTagsInAsync(GoogleAccountCredential credential, Long userId) {

		Object[] params = { credential , userId };

		new AsyncTask<Object, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Object... params) {

				Boolean success = Boolean.TRUE;
				GoogleAccountCredential credential = (GoogleAccountCredential) params[0];
				Long userId = (Long) params[1];
				Eventtagendpoint.Builder endpointBuilder = new Eventtagendpoint.Builder(
						AndroidHttp.newCompatibleTransport(),
						AndroidJsonFactory.getDefaultInstance(), credential);
				endpointBuilder = CloudEndpointUtils
						.updateBuilder(endpointBuilder);
				Eventtagendpoint tagEndpoint = endpointBuilder.build();

				// TODO: List Query for all of this users event tags
				
				String cursor = null;
				String filter = "userId == userIdParam";
				String paramsDeclaration = "Long userIdParam";

				do{

					try {
						ListEventTag listTagTask = tagEndpoint.listEventTag();
						
						listTagTask.setCursor(cursor);
						listTagTask.setFilter(filter);
						listTagTask.setLongParam(userId);
						listTagTask.setParameterDeclaration(paramsDeclaration);
						listTagTask.setLimit(30);
						
						CollectionResponseEventTag tagResponse = listTagTask.execute();

						if(tagResponse == null || tagResponse.getItems() == null || tagResponse.getItems().isEmpty()){
							cursor = null;
							break;
						} else {
							Iterator<EventTag> iterator = tagResponse.getItems().iterator();
							while(iterator.hasNext()){
								addMyEventTag(iterator.next());
							}
							
							cursor = tagResponse.getNextPageToken();
						}
						
					} catch (IOException e) {
						e.printStackTrace();
						break;
					}
					
				} while (cursor != null);
				
				

				return success;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				
				if(!result){
					// TODO: indicate an error here and/or try again
				}
					
				hasLoadedTags = true;
				if (waitingAdapter != null) {
					waitingAdapter.notifyDataSetChanged();
					waitingAdapter = null;
				}
			}

		}.execute(params);

	}

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
			addMyEventTag(tag);

		} catch (GoogleAuthIOException aEx) {
			Log.wtf(TAG, "AuthException");
		} catch (IOException ioEx) {
			tag = null;
			ioEx.printStackTrace();
		}

		if (tag != null) {
			return new MyEventTagMediator(tag);
		} else {
			return null;
		}

	}

	/**
	 * This loads all tags for a given user ID and adds them to the list of held
	 * tags. It will not add duplicates, and it will remove deleted tags Cannot
	 * be called from the main thread
	 * 
	 * @param userId
	 * @return true if changes were made to held tagMediators
	 */
	public boolean fetchEventTagsForUser(Long userId,
			GoogleAccountCredential credential) {
		boolean didUpdate = false;

		Eventtagendpoint.Builder endpointBuilder = new Eventtagendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				AndroidJsonFactory.getDefaultInstance(), credential);
		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
		Eventtagendpoint endpoint = endpointBuilder.build();

		
		
		
		
		// TODO: fetch all the tags for given user

		return didUpdate;
	}

	/**
	 * Returns an event tag follow instance if found. does not error check, not
	 * thread safe
	 * 
	 * @param tag
	 *            - event tag to check if user is following
	 * @return result - event tag follow instance if exists
	 */
	public EventTagFollow fetchEventTagFollow(EventTag tag,
			GoogleAccountCredential credential) {
		EventTagFollow result = null;

		Eventtagfollowendpoint.Builder endpointBuilder = new Eventtagfollowendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				AndroidJsonFactory.getDefaultInstance(), credential);
		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
		Eventtagfollowendpoint endpoint = endpointBuilder.build();

		// TODO: fetch all the follow instances for a given tag

		return result;
	}

	/**
	 * This method determines what tags the application is not already holding
	 * and adds them. It also determines if the user is following this event tag
	 * 
	 * @param heldMediators
	 * @param eventTags
	 * @param credential
	 * @return
	 */
	private boolean addNewMediators(
			List<AbstractEventTagMediator> heldMediators,
			List<EventTag> eventTags, GoogleAccountCredential credential) {
		boolean didUpdate = false;
		Iterator<EventTag> tagIterator = eventTags.iterator();

		while (tagIterator.hasNext()) {
			EventTag eventTag = tagIterator.next();
			boolean doAdd = true;
			Iterator<AbstractEventTagMediator> mediatorIterator = heldMediators
					.iterator();

			while (mediatorIterator.hasNext()) {
				if (mediatorIterator.next().getTagId().longValue() == 
						eventTag.getKey().getId().longValue()) {
					doAdd = false;
					break;
				}
			}

			if (doAdd) {

				addDefaultTagMediator(eventTag, credential);
			}

		}

		return didUpdate;
	}

	/**
	 * This method takes an updated list of event tags and removes the ones not
	 * present which the
	 * 
	 * @param heldMediators
	 * @param eventTags
	 * @return
	 */
	private boolean removeOldEventTags(
			List<AbstractEventTagMediator> heldMediators,
			List<EventTag> eventTags) {
		boolean didUpdate = false;
		Iterator<AbstractEventTagMediator> mediatorIterator = heldMediators
				.iterator();

		while (mediatorIterator.hasNext()) {
			AbstractEventTagMediator mediator = mediatorIterator.next();
			Iterator<EventTag> tagIterator = eventTags.iterator();
			while (tagIterator.hasNext()) {
				if (tagIterator.next().getKey().getId().longValue() == 
						mediator.getTagId().longValue()) {
					tagMediators.remove(mediator);
					didUpdate = true;
					break;
				}
			}

		}

		return didUpdate;
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
