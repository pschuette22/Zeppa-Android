package com.minook.zeppa.adapters.tagadapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.R;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.eventtagendpoint.Eventtagendpoint;
import com.minook.zeppa.eventtagendpoint.Eventtagendpoint.GetUserTags;
import com.minook.zeppa.eventtagendpoint.model.CollectionResponseEventTag;
import com.minook.zeppa.eventtagendpoint.model.EventTag;
import com.minook.zeppa.singleton.EventTagSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUser;

public class FriendTagAdapter extends TagAdapter {

	protected ZeppaUser user;
	protected boolean hasLoadedTags;

	public FriendTagAdapter(FragmentActivity activity, LinearLayout tagHolder,
			ZeppaUser user) {
		super(activity, tagHolder);

		this.user = user;
		tags = new ArrayList<EventTag>();
		hasLoadedTags = false;
		loadTagsInAsync();

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		super.getView(position, convertView, parent);

		final EventTag eventTag = getItem(position);

		convertView = inflater.inflate(R.layout.view_tag_others, null, false);
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (eventTag.getUsersFollowingIds() != null
						&& eventTag.getUsersFollowingIds()
								.contains(getUserId())) {
					unfollowTagInAsync(eventTag, v);
				} else {
					followTagInAsync(eventTag, v);
				}

			}

		});

		CheckedTextView tagText = (CheckedTextView) convertView
				.findViewById(R.id.tagview_tagtext);
		tagText.setText(eventTag.getTagText());
		views.add(position, convertView); // Keep view array synced
		return convertView;
	}

	private Long getUserId() {
		return ZeppaUserSingleton.getInstance().getUserId();
	}

	private GoogleAccountCredential getCredential() {
		return ((ZeppaApplication) activity.getApplication())
				.getGoogleAccountCredential();
	}

	protected void followTagInAsync(EventTag tag, final View view) {

		Object[] params = { getCredential(), tag };

		new AsyncTask<Object, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Object... params) {

				GoogleAccountCredential credential = (GoogleAccountCredential) params[0];
				EventTag tag = (EventTag) params[0];

				return EventTagSingleton.getInstance().followTag(tag,
						credential);
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);

				if (result) {
					// followed Tag
				} else {
					((CheckedTextView) view).setChecked(false);
				}

			}

		}.execute(params);

	}

	protected void unfollowTagInAsync(EventTag tag, final View view) {

		Object[] params = { getCredential(), tag };

		new AsyncTask<Object, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Object... params) {
				GoogleAccountCredential credential = (GoogleAccountCredential) params[0];
				EventTag tag = (EventTag) params[1];

				return EventTagSingleton.getInstance().unfollowTag(tag,
						credential);
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if (result) {

				} else {
					((CheckedTextView) view).setChecked(true);
				}

			}

		}.execute(params);

	}

	protected void loadTagsInAsync() {

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {

				Eventtagendpoint.Builder endpointBuilder = new Eventtagendpoint.Builder(
						AndroidHttp.newCompatibleTransport(),
						new JacksonFactory(),
						((ZeppaApplication) activity.getApplication())
								.getGoogleAccountCredential());
				endpointBuilder = CloudEndpointUtils
						.updateBuilder(endpointBuilder);
				Eventtagendpoint endpoint = endpointBuilder.build();

				int start = 0;
				int increment = 20;
				while (true) {
					try {

						GetUserTags getTagsTask = endpoint.getUserTags(user
								.getKey().getId(), start, (start + increment));
						CollectionResponseEventTag collectionResponse = getTagsTask
								.execute();
						if (collectionResponse.getItems() != null
								&& !collectionResponse.getItems().isEmpty()) {
							List<EventTag> tagList = collectionResponse
									.getItems();
							tags.addAll(tagList);
							if (tagList.size() < increment) {
								break;
							} else {
								start += increment;
							}

						} else {
							break;
						}

					} catch (IOException e) {
						e.printStackTrace();
						break;

					}
				}

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				hasLoadedTags = true;
				notifyDataSetChanged();

			}

		}.execute();

	}

	@Override
	public boolean hasLoadedTags() {
		super.hasLoadedTags();
		return hasLoadedTags;
	}

}
