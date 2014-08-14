package com.minook.zeppa.singleton;

import java.io.IOException;
import java.util.ArrayList;
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
import com.minook.zeppa.eventtagendpoint.Eventtagendpoint.GetUserTags;
import com.minook.zeppa.eventtagendpoint.model.CollectionResponseEventTag;
import com.minook.zeppa.eventtagendpoint.model.EventTag;
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
	private List<MyEventTagMediator> tagManagers;
	private MyTagAdapter waitingAdapter;
	private boolean hasLoadedTags;

	/*
	 * Instance Handlers
	 */

	private EventTagSingleton() {
		tagManagers = new ArrayList<MyEventTagMediator>();
		waitingAdapter = null;
		hasLoadedTags = false;
	}

	public static EventTagSingleton getInstance() {
		if (singleton == null)
			singleton = new EventTagSingleton();
		return singleton;
	}

	// If an adapter is waiting for tags to be loaded, I will set it and call
	// Draw on the adapter when it is loaded
	public void setWaitingAdapter(MyTagAdapter adapter) {
		this.waitingAdapter = adapter;
	}

	/*
	 * Create new tag instance
	 */

	public EventTag newTagInstance() {
		Long time = System.currentTimeMillis();
		EventTag tag = new EventTag();

		tag.setUserId(getUserId());
		tag.setDayCreated(time);
		tag.setTagText(new String());

		return tag;
	}

	/*
	 * Getters
	 */

	private Long getUserId() {
		return ZeppaUserSingleton.getInstance().getUserId();
	}

	public List<MyEventTagMediator> getTags() {
		return tagManagers;
	}

	public List<MyEventTagMediator> getTagsFrom(List<Long> tagIds) {
		List<MyEventTagMediator> usedTags = new ArrayList<MyEventTagMediator>();
		// for (EventTag tag : tags) {
		// if (tagIds.contains(tag.getKey().getId()))
		// usedTags.add(tag);
		// }
		return usedTags;
	}

	public boolean hasLoadedTags() {
		return hasLoadedTags;
	}

	/*
	 * Setters
	 */

	private boolean addAllEventTags(List<MyEventTagMediator> tags) {

		boolean didChange = true;
		// if (this.tags.containsAll(tags) && tags.containsAll(this.tags)) {
		// didChange = false;
		// } else {
		// this.tags.removeAll(tags);
		// this.tags.addAll(tags);
		// }

		return didChange;
	}

	private void addEventTag(EventTag tag, GoogleAccountCredential credential) {
		MyEventTagMediator myTagManager = new MyEventTagMediator(tag,
				credential);
		tagManagers.add(myTagManager);
	}

	/*
	 * Private
	 */

	/*
	 * Loader Methods
	 */

	public void loadTagsInAsync(GoogleAccountCredential credential) {

		GoogleAccountCredential[] params = { credential };

		new AsyncTask<GoogleAccountCredential, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(GoogleAccountCredential... params) {

				Boolean success = Boolean.FALSE;
				GoogleAccountCredential credential = params[0];
				Eventtagendpoint.Builder endpointBuilder = new Eventtagendpoint.Builder(
						AndroidHttp.newCompatibleTransport(),
						AndroidJsonFactory.getDefaultInstance(), credential);
				endpointBuilder = CloudEndpointUtils
						.updateBuilder(endpointBuilder);
				Eventtagendpoint tagEndpoint = endpointBuilder.build();

				try {

					int start = 0;
					while (true) {

						GetUserTags getUserTags = tagEndpoint.getUserTags(
								getUserId(), start, 40);

						CollectionResponseEventTag response = getUserTags
								.execute();
						success = Boolean.TRUE;

						if (response == null || response.getItems() == null
								|| response.getItems().isEmpty()) {
							break;

						} else {

						}

					}

				} catch (GoogleAuthIOException aEx) {
					Log.wtf(TAG, "AuthException");
					success = false;
				} catch (IOException e) {
					e.printStackTrace();
				}

				return success;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				// TODO: indicate an error here and/or try again

				hasLoadedTags = true;
				if (waitingAdapter != null) {
					waitingAdapter.notifyDataSetChanged();
					waitingAdapter = null;
				}
			}

		}.execute(params);

	}

	public MyEventTagMediator insertEventTag(EventTag tag,
			GoogleAccountCredential credential) {

		Eventtagendpoint.Builder endpointBuilder = new Eventtagendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				AndroidJsonFactory.getDefaultInstance(), credential);
		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
		Eventtagendpoint endpoint = endpointBuilder.build();

		try {

			tag = endpoint.insertEventTag(tag).execute();
			addEventTag(tag, credential);

		} catch (GoogleAuthIOException aEx) {
			Log.wtf(TAG, "AuthException");
		} catch (IOException ioEx) {
			tag = null;
			ioEx.printStackTrace();
		}

		if (tag != null) {
			return new MyEventTagMediator(tag, credential);
		} else {
			return null;
		}

	}

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

		// I am going to need to iterate through all the events and remove
		// references to this

		return success;
	}

}
