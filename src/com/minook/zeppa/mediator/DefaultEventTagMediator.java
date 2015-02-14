package com.minook.zeppa.mediator;

import java.io.IOException;

import android.os.AsyncTask;
import android.view.View;
import android.widget.CheckedTextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
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
	

	public DefaultEventTagMediator(EventTag eventTag, EventTagFollow myFollow) {
		super(eventTag);
		this.myFollow = myFollow;
	}

	/**
	 * This method takes a view for a tag and sets
	 */
	@Override
	public View convertView(View convertView) {
		convertView = super.convertView(convertView);

		CheckedTextView textView = (CheckedTextView) convertView
				.findViewById(R.id.tagview_tagtext);
		textView.setText(eventTag.getTagText());
		textView.setChecked(isFollowing());
		textView.setTag(this);
		
		return convertView;
	}

	
	public boolean isFollowing(){
		return (myFollow != null);
	}

	/**
	 * 
	 * @param tag
	 * @param view
	 */
	public void followTagInAsync(GoogleAccountCredential credential, CheckedTextView view) {

		Object[] params = { credential, eventTag,
				view };

		new AsyncTask<Object, Void, EventTagFollow>() {

			private CheckedTextView view;

			@Override
			protected EventTagFollow doInBackground(Object... params) {

				GoogleAccountCredential credential = (GoogleAccountCredential) params[0];
				EventTag tag = (EventTag) params[1];
				view = (CheckedTextView) params[2];

				EventTagFollow tagFollow = new EventTagFollow();

				tagFollow.setTagId(tag.getId());
				tagFollow.setTagOwnerId(tag.getOwnerId());
				tagFollow.setFollowerId(ZeppaUserSingleton.getInstance().getUserId());

				Eventtagfollowendpoint.Builder endpointBuilder = new Eventtagfollowendpoint.Builder(
						AndroidHttp.newCompatibleTransport(), GsonFactory.getDefaultInstance(), credential);
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

			@Override
			protected void onPostExecute(EventTagFollow result) {
				super.onPostExecute(result);

				if (result != null) {
					myFollow = result;
				} else {
					view.setChecked(false);
				}

				view.setClickable(true);

			}

		}.execute(params);

	}

	public void unfollowTagInAsync( GoogleAccountCredential credential, CheckedTextView view) {

		if (myFollow == null) {
			return;
		}
		
		Object[] params = { credential, myFollow, view };

		new AsyncTask<Object, Void, Boolean>() {

			private EventTagFollow follow;
			private CheckedTextView view;

			@Override
			protected Boolean doInBackground(Object... params) {
				GoogleAccountCredential credential = (GoogleAccountCredential) params[0];
				follow = (EventTagFollow) params[1];
				view = (CheckedTextView) params[2];

				Eventtagfollowendpoint.Builder endpointBuilder = new Eventtagfollowendpoint.Builder(
						new NetHttpTransport(), new JacksonFactory(), credential);
				endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);

				Eventtagfollowendpoint endpoint = (Eventtagfollowendpoint) endpointBuilder
						.build();

				try {

					endpoint.removeEventTagFollow(myFollow.getKey().getId()).execute();
					return true;
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if (result) {
					myFollow = null;
				} else {
					myFollow = follow;
					view.setChecked(true);
				}
				view.setClickable(true);

			}

		}.execute(params);

	}





}
