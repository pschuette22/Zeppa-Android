package com.minook.zeppa.mediator;

import java.io.IOException;

import android.os.AsyncTask;
import android.view.View;
import android.widget.CheckedTextView;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.R;
import com.minook.zeppa.eventtagendpoint.model.EventTag;
import com.minook.zeppa.eventtagfollowendpoint.Eventtagfollowendpoint;
import com.minook.zeppa.eventtagfollowendpoint.model.EventTagFollow;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

public class DefaultEventTagMediator extends AbstractEventTagMediator {

	// private boolean hasLoaded; // in follow is still being looked for
	private EventTagFollow myFollow; // null if non existent.

	public DefaultEventTagMediator(EventTag eventTag,
			GoogleAccountCredential credential, EventTagFollow myFollow) {
		super(eventTag, credential);
		this.myFollow = myFollow;

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.tagview_tagtext) {
			CheckedTextView tagView = (CheckedTextView) v;
			if (myFollow == null) {
				followTagInAsync(eventTag, tagView);
				tagView.setChecked(true);
			} else {
				unfollowTagInAsync(eventTag, tagView);
				tagView.setChecked(false);
			}

		}

	}

	/**
	 * This method takes a view for a tag and sets
	 */
	@Override
	public void convertView(View convertView) throws NullPointerException {
		CheckedTextView textView = (CheckedTextView) convertView
				.findViewById(R.id.tagview_tagtext);
		textView.setText(eventTag.getTagText());

		if (myFollow == null) {
			textView.setChecked(false);
		} else {
			textView.setChecked(true);
		}

		textView.setOnClickListener(this);

	}

	/**
	 * 
	 * @param tag
	 * @param view
	 */
	protected void followTagInAsync(EventTag tag, CheckedTextView view) {

		Object[] params = { credential, tag, view };

		new AsyncTask<Object, Void, EventTagFollow>() {

			private CheckedTextView view;

			@Override
			protected EventTagFollow doInBackground(Object... params) {

				GoogleAccountCredential credential = (GoogleAccountCredential) params[0];
				EventTag tag = (EventTag) params[1];
				view = (CheckedTextView) params[2];

				return createAndPersistFollowFor(tag, credential);
			}

			@Override
			protected void onPostExecute(EventTagFollow result) {
				super.onPostExecute(result);

				if (result != null) {
					myFollow = result;
				} else {
					view.setChecked(false);
				}

			}

		}.execute(params);

	}

	protected void unfollowTagInAsync(EventTag tag, CheckedTextView view) {

		if (myFollow == null) {
			return;
		}

		myFollow = null;

		Object[] params = { credential, myFollow, view };

		new AsyncTask<Object, Void, Boolean>() {

			private EventTagFollow follow;
			private CheckedTextView view;

			@Override
			protected Boolean doInBackground(Object... params) {
				GoogleAccountCredential credential = (GoogleAccountCredential) params[0];
				follow = (EventTagFollow) params[1];
				view = (CheckedTextView) params[2];

				return deleteEventTagFollow(follow, credential);
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if (result) {
					// nothing to do here
				} else {
					myFollow = follow;
					view.setChecked(true);
				}

			}

		}.execute(params);

	}

	/**
	 * This method creates and persists a EventTagFollow item for this tag so
	 * the user is now following the tag
	 * 
	 * @param tag
	 * @param credential
	 * @return
	 */
	private EventTagFollow createAndPersistFollowFor(EventTag tag,
			GoogleAccountCredential credential) {
		EventTagFollow tagFollow = new EventTagFollow();
		tagFollow.setEventTagId(tag.getKey().getId());
		tagFollow
				.setTimeCreatedMillis(Long.valueOf(System.currentTimeMillis()));
		tagFollow.setUserFollowingId(ZeppaUserSingleton.getInstance()
				.getUserId());

		Eventtagfollowendpoint.Builder endpointBuilder = new Eventtagfollowendpoint.Builder(
				new NetHttpTransport(), new JacksonFactory(), credential);
		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);

		Eventtagfollowendpoint endpoint = (Eventtagfollowendpoint) endpointBuilder
				.build();

		try {
			tagFollow = endpoint.insertEventTagFollow(tagFollow).execute();
		} catch (IOException e) {
			e.printStackTrace();
			tagFollow = null;
		}

		return tagFollow;
	}

	/**
	 * This method deletes the EventTagFollow object from the datastore so the
	 * user is no longer following this.
	 * 
	 * @param myFollow
	 * @param credential
	 * @return
	 */
	private boolean deleteEventTagFollow(EventTagFollow myFollow,
			GoogleAccountCredential credential) {
		boolean success = false;
		Eventtagfollowendpoint.Builder endpointBuilder = new Eventtagfollowendpoint.Builder(
				new NetHttpTransport(), new JacksonFactory(), credential);
		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);

		Eventtagfollowendpoint endpoint = (Eventtagfollowendpoint) endpointBuilder
				.build();

		try {
			endpoint.removeEventTagFollow(myFollow.getKey().getId()).execute();
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
			success = false;
		}
		return success;
	}

	@Override
	public boolean onMemoryWarning() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onMemoryLow() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onMemoryCritical() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onApplicationTerminate() {
		// TODO Auto-generated method stub
		return false;
	}

}