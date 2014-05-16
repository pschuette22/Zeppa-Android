package com.minook.zeppa.singleton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.adapters.tagadapter.MyTagAdapter;
import com.minook.zeppa.eventtagendpoint.Eventtagendpoint;
import com.minook.zeppa.eventtagendpoint.Eventtagendpoint.GetUserTags;
import com.minook.zeppa.eventtagendpoint.model.CollectionResponseEventTag;
import com.minook.zeppa.eventtagendpoint.model.EventTag;
import com.minook.zeppa.zeppauserendpoint.Zeppauserendpoint;
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUser;

public class EventTagSingleton {
	private static EventTagSingleton singleton;

	private List<EventTag> tags;
	private MyTagAdapter waitingAdapter;
	private boolean hasLoadedTags;

	/*
	 * Instance Handlers
	 */

	private EventTagSingleton() {
		tags = new ArrayList<EventTag>();
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
		tag.setUsersFollowingIds(new ArrayList<Long>());

		return tag;
	}

	/*
	 * Getters
	 */

	private Long getUserId() {
		return ZeppaUserSingleton.getInstance().getUserId();
	}

	public List<EventTag> getTags() {
		return tags;
	}

	public List<EventTag> getTagsFrom(List<Long> tagIds) {
		List<EventTag> usedTags = new ArrayList<EventTag>();
		for (EventTag tag : tags) {
			if (tagIds.contains(tag.getKey().getId()))
				usedTags.add(tag);
		}
		return usedTags;
	}

	public boolean hasLoadedTags() {
		return hasLoadedTags;
	}

	/*
	 * Setters
	 */

	private boolean addAllEventTags(List<EventTag> tags) {
		
		boolean didChange = false;
		if (this.tags.containsAll(tags) && tags.containsAll(this.tags)) {
			didChange = true;
		} else {
			this.tags.removeAll(tags);
			this.tags.addAll(tags);
		}
		
		return didChange;
	}

	private void addEventTag(EventTag tag) {
		if (!tags.contains(tag))
			this.tags.add(tag);
	}

	public void waitForLoad(MyTagAdapter adapter) {
		waitingAdapter = adapter;
	}

	/*
	 * Private
	 */

	/*
	 * Loader Methods
	 */

	public void loadTagsInAsync(Context context) {

		GoogleAccountCredential credential = ((ZeppaApplication) context
				.getApplicationContext()).getGoogleAccountCredential();
		GoogleAccountCredential[] params = { credential };

		new AsyncTask<GoogleAccountCredential, Void, List<EventTag>>() {

			@Override
			protected List<EventTag> doInBackground(GoogleAccountCredential... params) {

				GoogleAccountCredential credential = params[0];
				Eventtagendpoint.Builder endpointBuilder = new Eventtagendpoint.Builder(
						AndroidHttp.newCompatibleTransport(),
						new JacksonFactory(), credential);
				endpointBuilder = CloudEndpointUtils
						.updateBuilder(endpointBuilder);
				Eventtagendpoint tagEndpoint = endpointBuilder.build();

				List<EventTag> result = new ArrayList<EventTag>();
				
				try {

					int start = 0;
					while (true) {

						GetUserTags getUserTags = tagEndpoint.getUserTags(
								getUserId(), start, (start + 15));
						start += 15;
						CollectionResponseEventTag response = getUserTags
								.execute();

						if (response == null)
							break;

						List<EventTag> listResponse = response.getItems();
						if (listResponse != null && !listResponse.isEmpty()) {
							result.addAll(result);
							if (listResponse.size() < 15) {
								break;
							}

						} else {
							break;
						}
					}

				} catch (IOException e) {
					e.printStackTrace();
				}

				return result;
			}

			@Override
			protected void onPostExecute(List<EventTag> result) {
				super.onPostExecute(result);
				
				boolean didChange = false;
				if(!result.isEmpty()){
					didChange = addAllEventTags(result);
				}
				
				hasLoadedTags = true;
				if(waitingAdapter != null && didChange)
					waitingAdapter.notifyDataSetChanged();
			}

		}.execute(params);

	}

	public EventTag insertEventTag(EventTag tag,
			GoogleAccountCredential credential) {

		Eventtagendpoint.Builder endpointBuilder = new Eventtagendpoint.Builder(
				AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
				credential);
		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
		Eventtagendpoint endpoint = endpointBuilder.build();

		try {

			tag = endpoint.insertEventTag(tag).execute();
			addEventTag(tag);

		} catch (IOException ioEx) {
			ioEx.printStackTrace();
		}

		return tag;
	}

	public boolean deleteEventTag(EventTag tag,
			GoogleAccountCredential credential) {
		boolean success = false;

		Eventtagendpoint.Builder endpointBuilder = new Eventtagendpoint.Builder(
				AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
				credential);
		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
		Eventtagendpoint endpoint = endpointBuilder.build();

		try {

			endpoint.removeEventTag(tag.getKey().getId()).execute();
			tags.remove(tag);
			success = true;
		} catch (IOException ioEx) {
			ioEx.printStackTrace();
		}

		// I am going to need to iterate through all the events and remove
		// references to this

		return success;
	}

	public boolean followTag(EventTag tag, GoogleAccountCredential credential) {

		boolean success = false;
		Eventtagendpoint.Builder endpointBuilder = new Eventtagendpoint.Builder(
				AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
				credential);
		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
		Eventtagendpoint endpoint = endpointBuilder.build();

		try {
			endpoint.addFollowingUser(tag.getKey().getId(), getUserId())
					.execute();
			success = true;

		} catch (IOException ioEx) {
			ioEx.printStackTrace();
		}

		return success;
	}

	public boolean unfollowTag(EventTag tag, GoogleAccountCredential credential) {

		boolean success = false;
		Eventtagendpoint.Builder endpointBuilder = new Eventtagendpoint.Builder(
				AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
				credential);
		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
		Eventtagendpoint endpoint = endpointBuilder.build();

		try {
			endpoint.removeFollowingUser(tag.getKey().getId(), getUserId())
					.execute();
			success = true;

		} catch (IOException ioEx) {
			ioEx.printStackTrace();
		}

		return success;
	}

	public boolean followNewTags(ZeppaUser user,
			GoogleAccountCredential credential) {
		boolean success = false;

		Zeppauserendpoint.Builder endpointBuilder = new Zeppauserendpoint.Builder(
				AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
				credential);
		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
		Zeppauserendpoint endpoint = endpointBuilder.build();

		try {
			user = endpoint.addNewTagFollower(getUserId(),
					user.getKey().getId()).execute();
			user.getNewTagFollowerIds().remove(getUserId());
			success = true;
		} catch (IOException ioEx) {
			ioEx.printStackTrace();
		}

		return success;

	}

	public boolean unfollowNewTags(ZeppaUser user,
			GoogleAccountCredential credential) {
		boolean success = false;

		Zeppauserendpoint.Builder endpointBuilder = new Zeppauserendpoint.Builder(
				AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
				credential);
		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
		Zeppauserendpoint endpoint = endpointBuilder.build();

		try {
			user = endpoint.removeNewtagFollower(getUserId(),
					user.getKey().getId()).execute();
			user.getNewTagFollowerIds().add(getUserId());
			success = true;

		} catch (IOException ioEx) {
			ioEx.printStackTrace();
		}

		return success;

	}

}
