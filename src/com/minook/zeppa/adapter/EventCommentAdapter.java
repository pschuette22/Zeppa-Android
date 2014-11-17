package com.minook.zeppa.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.eventcommentendpoint.Eventcommentendpoint;
import com.minook.zeppa.eventcommentendpoint.model.EventComment;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.utils.Utils;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;

public class EventCommentAdapter extends BaseAdapter {

	private AuthenticatedFragmentActivity activity;
	private List<EventComment> comments;
	private LayoutInflater inflater;
	private LinearLayout commentHolder;
	private ZeppaEvent zeppaEvent;

	private View loaderView;

	protected final Comparator<EventComment> COMMENT_COMPARATOR = new Comparator<EventComment>() {

		@Override
		public int compare(EventComment lhs, EventComment rhs) {
			return (int) ((lhs.getCreated().getValue()) - (rhs.getCreated().getValue()));
		}

	};

	public EventCommentAdapter(AuthenticatedFragmentActivity activity,
			ZeppaEvent event, LinearLayout commentHolder) {
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

		loadCommenterInAsync(comment.getCommenterId(), commenterImage,
				commenterName);

		commentTime.setText(Utils.getDisplayDateString(comment
				.getCreated().getValue()));
		commentText.setText(comment.getText());

		return convertView;
	}

	private AuthenticatedFragmentActivity getActivity() {
		return activity;
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

	// private void updateComment(EventComment update) {
	// for (EventComment heldComment : comments) {
	// if (heldComment.getText().equals(update.getText())
	// && heldComment.getUserId().equals(update.getUserId())) {
	// heldComment = update;
	// }
	// }
	// }

	private GoogleAccountCredential getCredential() throws IOException {
		return ((AuthenticatedFragmentActivity) activity)
				.getGoogleAccountCredential();
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

	private void loadCommenterInAsync(Long commenterId, ImageView userImage,
			TextView userName) {

		// if(userId.longValue() ==
		// ZeppaUserSingleton.getInstance().getUserId().longValue()){
		//
		// }
		//
		//
		// Object[] params = {userId, userImage, userName};
		//
		// new AsyncTask<Object, Void, UserInfoManager>() {
		//
		// ImageView commentImage;
		// TextView commentName;
		//
		// @Override
		// protected ZeppaUser doInBackground(Object... params) {
		//
		//
		// return ZeppaUserSingleton.getInstance().getOrFetchZeppaUser(
		// userId, getCredential());
		// }
		//
		// @Override
		// protected void onPostExecute(ZeppaUser result) {
		// super.onPostExecute(result);
		// if (result != null) {
		// setImageInAsync(result, userImage);
		// commenterName.setText(result.getGivenName() + " " +
		// result.getFamilyName());
		// } else {
		// commenterName.setText("Error");
		// }
		// }
		//
		// }.execute(params);

	}

	public void postCommentInAsync(final String text) {
		EventComment comment = new EventComment();
		com.minook.zeppa.eventcommentendpoint.model.ZeppaEvent event = new com.minook.zeppa.eventcommentendpoint.model.ZeppaEvent();
		event.setId(zeppaEvent.getKey().getId());
		comment.setEvent(event);
		
		comment.setCommenterId(ZeppaUserSingleton.getInstance().getUserId());
		comment.setText(text);

		EventComment[] params = { comment };
		new AsyncTask<EventComment, Void, Boolean>() {

			private EventComment comment;

			@Override
			protected Boolean doInBackground(EventComment... params) {
				try {
					comment = params[0];

					Eventcommentendpoint.Builder builder = new Eventcommentendpoint.Builder(
							AndroidHttp.newCompatibleTransport(),
							AndroidJsonFactory.getDefaultInstance(),
							getCredential());

					builder = CloudEndpointUtils.updateBuilder(builder);
					Eventcommentendpoint endpoint = builder.build();

					comment = endpoint.insertEventComment(comment).execute();
				} catch (IOException e) {
					e.printStackTrace();
					return Boolean.FALSE;
				}

				return Boolean.TRUE;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);

				if (result) {
					// Success
					// TODO: show comment image
				} else {
					Toast.makeText(getActivity(), "Error Occured",
							Toast.LENGTH_SHORT).show();
				}
			}

		}.execute(params);

	}

	private void fetchCommentsInAsync(final ZeppaEvent event) {

		// TODO: Put in Loader View
		// TODO: Hold Comments, trim w/ memory issues
//		new AsyncTask<Void, Void, Boolean>() {
//
//			@Override
//			protected Boolean doInBackground(Void... params) {
//				boolean success = false;
//				try {
//					Eventcommentendpoint.Builder builder = new Eventcommentendpoint.Builder(
//							AndroidHttp.newCompatibleTransport(),
//							AndroidJsonFactory.getDefaultInstance(),
//							activity.getGoogleAccountCredential());
//
//					builder = CloudEndpointUtils.updateBuilder(builder);
//					Eventcommentendpoint endpoint = builder.build();
//
//					FetchCommentsForEvent fetchCommentsTask = endpoint
//							.fetchCommentsForEvent(event.getKey().getId());
//
//					CollectionResponseEventComment collectionResponse = fetchCommentsTask
//							.execute();
//
//					if (collectionResponse.getItems() != null
//							&& !collectionResponse.isEmpty()) {
//						List<EventComment> comments = collectionResponse
//								.getItems();
//						addAllComments(comments);
//
//					}
//
//					success = true;
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//
//				return success;
//			}
//
//			@Override
//			protected void onPostExecute(Boolean result) {
//				super.onPostExecute(result);
//				if (result) {
//					redrawAll();
//				} else {
//				}
//
//			}
//
//		}.execute();
	}

}