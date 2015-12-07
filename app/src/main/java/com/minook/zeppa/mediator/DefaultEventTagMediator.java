package com.minook.zeppa.mediator;

import android.os.AsyncTask;
import android.view.View;
import android.widget.CheckedTextView;

import com.appspot.zeppa_cloud_1821.zeppaclientapi.Zeppaclientapi;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.EventTag;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.EventTagFollow;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ApiClientHelper;
import com.minook.zeppa.R;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

import java.io.IOException;

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
	 * @param credential
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

				ApiClientHelper helper = new ApiClientHelper();
				Zeppaclientapi api = helper.buildClientEndpoint();

				try {
					tagFollow = api.insertEventTagFollow(credential.getToken(), tagFollow).execute();
				} catch (IOException e) {
					e.printStackTrace();
					tagFollow = null;
				} catch (GoogleAuthException ex) {

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

				ApiClientHelper helper = new ApiClientHelper();
				Zeppaclientapi api = helper.buildClientEndpoint();

				try {

					api.removeEventTagFollow(myFollow.getKey().getId(),credential.getToken()).execute();
					return true;
				} catch (IOException e) {
					e.printStackTrace();
				} catch (NullPointerException e){
					e.printStackTrace();
				} catch (GoogleAuthException ex) {
					ex.printStackTrace();
				}
				return false;
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
