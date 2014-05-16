package com.minook.zeppa.adapters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.eventcommentendpoint.Eventcommentendpoint;
import com.minook.zeppa.eventcommentendpoint.Eventcommentendpoint.FetchCommentsForEvent;
import com.minook.zeppa.eventcommentendpoint.model.CollectionResponseEventComment;
import com.minook.zeppa.eventcommentendpoint.model.EventComment;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUser;

public class EventCommentAdapter extends BaseAdapter {

	private FragmentActivity activity;
	private List<EventComment> comments;
	private LayoutInflater inflater;
	private LinearLayout commentHolder;
	private ZeppaEvent zeppaEvent;
	
	private View loaderView;

	protected final Comparator<EventComment> COMMENT_COMPARATOR = new Comparator<EventComment>() {

		@Override
		public int compare(EventComment lhs, EventComment rhs) {
			return (int) (((long) lhs.getPostedDate()) - ((long) rhs
					.getPostedDate()));
		}

	};

	public EventCommentAdapter(FragmentActivity activity, ZeppaEvent event,
			LinearLayout commentHolder) {
		this.activity = activity;
		this.comments = new ArrayList<EventComment>();
		this.inflater = activity.getLayoutInflater();
		this.zeppaEvent = event;
		this.commentHolder = commentHolder;
		
		fetchCommentsInAsync(event);
	}

	@Override
	public int getCount() {
		return comments.size();
	}

	@Override
	public EventComment getItem(int position) {
		return comments.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getKey().getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		EventComment comment = getItem(position);
		TextView commenterName = null;
		TextView commentTime = null;
		TextView commentText = null;
		ImageView commenterImage = null;

		convertView = inflater.inflate(R.layout.view_comment, parent, false);
		commenterName = (TextView) convertView.findViewById(R.id.comment_name);
		commenterImage = (ImageView) convertView.findViewById(R.id.comment_pic);
		commentText = (TextView) convertView.findViewById(R.id.comment_text);
		commentTime = (TextView) convertView.findViewById(R.id.comment_date);

		loadCommenterInAsync(comment.getUserCommentedId(), commenterImage,
				commenterName);

		commentTime.setText(Constants.getDisplayDateString(comment
				.getPostedDate()));
		commentText.setText(comment.getText());

		return convertView;
	}

	public void redrawAll() {
		commentHolder.removeAllViews();
		for (int i = 0; i < comments.size(); i++) {
			View comment = getView(i, null, null);
			commentHolder.addView(comment);
		}
	}

	public void add(EventComment comment) {
		comments.add(comment);
		View commentView = getView((comments.size() - 1), null, null);
		commentHolder.addView(commentView);
	}


//	private void updateComment(EventComment update) {
//		for (EventComment heldComment : comments) {
//			if (heldComment.getText().equals(update.getText())
//					&& heldComment.getUserId().equals(update.getUserId())) {
//				heldComment = update;
//			}
//		}
//	}

	private GoogleAccountCredential getCredential(){
		return ((ZeppaApplication) activity.getApplication()).getGoogleAccountCredential();
	}
	
	private void addAllComments(List<EventComment> addComments) {
		boolean didAdd = false;
		for (EventComment comment : addComments) {
			if (!comments.contains(comment)) {
				comments.add(comment);
				didAdd = true;
			}
		}
		
		if (didAdd) {
			Collections.sort(comments, COMMENT_COMPARATOR);
		}

	}

	private void loadCommenterInAsync(final Long userId,
			final ImageView userImage, final TextView commenterName) {
		new AsyncTask<Void, Void, ZeppaUser>() {

			@Override
			protected ZeppaUser doInBackground(Void... params) {
				return ZeppaUserSingleton.getInstance().getOrFetchZeppaUser(
						userId, getCredential());
			}

			@Override
			protected void onPostExecute(ZeppaUser result) {
				super.onPostExecute(result);
				if (result != null) {
					setImageInAsync(result, userImage);
					commenterName.setText(result.getDisplayName());
				} else {
					commenterName.setText("Error");
				}
			}

		}.execute();

	}

	public void postCommentInAsync(final String text) {
		EventComment comment = new EventComment();
		comment.setEventId(zeppaEvent.getKey().getId());
		comment.setUserId(ZeppaUserSingleton.getInstance().getUserId());
		comment.setPostedDate(System.currentTimeMillis());
		comment.setText(text);

		EventComment[] params = { comment };
		new AsyncTask<EventComment, Void, Void>() {

			@Override
			protected Void doInBackground(EventComment... params) {
				EventComment comment = params[0];

				Eventcommentendpoint.Builder builder = new Eventcommentendpoint.Builder(
						AndroidHttp.newCompatibleTransport(),
						new JacksonFactory(), getCredential());

				builder = CloudEndpointUtils.updateBuilder(builder);
				Eventcommentendpoint endpoint = builder.build();

				try {
					comment = endpoint.insertEventComment(comment).execute();
				} catch (IOException e) {
					e.printStackTrace();
				}

				return null;
			}

		}.execute(params);

	}

	private void fetchCommentsInAsync(final ZeppaEvent event) {
		
		// Put in loader view?
		
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				boolean success = false;
				Eventcommentendpoint.Builder builder = new Eventcommentendpoint.Builder(
						AndroidHttp.newCompatibleTransport(),
						new JacksonFactory(),
						((ZeppaApplication) activity.getApplication())
								.getGoogleAccountCredential());

				builder = CloudEndpointUtils.updateBuilder(builder);
				Eventcommentendpoint endpoint = builder.build();

				try {
					FetchCommentsForEvent fetchCommentsTask = endpoint
							.fetchCommentsForEvent(event.getKey().getId());

					CollectionResponseEventComment collectionResponse = fetchCommentsTask
							.execute();

					if (collectionResponse.getItems() != null
							&& !collectionResponse.isEmpty()) {
						List<EventComment> comments = collectionResponse
								.getItems();
						addAllComments(comments);

					}

					success = true;
				} catch (IOException e) {
					e.printStackTrace();
				}

				return success;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if (result) {
					redrawAll();
				} else {
				}

			}

		}.execute();
	}

	private void setImageInAsync(final ZeppaUser user, final ImageView userImage) {
		new AsyncTask<Void, Void, Drawable>() {

			@Override
			protected Drawable doInBackground(Void... params) {

				return ZeppaUserSingleton.getInstance().getUserImage(user);
			}

			@Override
			protected void onPostExecute(Drawable result) {
				super.onPostExecute(result);
				if (result != null) {
					userImage.setImageDrawable(result);
				} else {
					Toast.makeText(
							activity,
							"Error Occured Loading " + user.getGivenName()
									+ "\'s Picture", Toast.LENGTH_SHORT).show();
				}
			}

		}.execute();
	}

}