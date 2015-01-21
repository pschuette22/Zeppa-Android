package com.minook.zeppa.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.Utils;
import com.minook.zeppa.adapter.tagadapter.AbstractTagAdapter;
import com.minook.zeppa.eventcommentendpoint.Eventcommentendpoint;
import com.minook.zeppa.eventcommentendpoint.Eventcommentendpoint.ListEventComment;
import com.minook.zeppa.eventcommentendpoint.model.CollectionResponseEventComment;
import com.minook.zeppa.eventcommentendpoint.model.EventComment;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator;
import com.minook.zeppa.mediator.AbstractZeppaUserMediator;
import com.minook.zeppa.observer.OnLoadListener;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

public abstract class AbstractEventViewActivity extends
		AuthenticatedFragmentActivity implements OnClickListener,
		OnLoadListener {

	final private String TAG = getClass().getName();

	protected AbstractZeppaEventMediator eventMediator;
	protected AbstractZeppaUserMediator hostMediator;
	protected AbstractTagAdapter tagAdapter;
	protected EventCommentAdapter commentAdapter;

	// UI Elements
	protected TextView title;
	protected ImageView conflictIndicator;

	protected TextView time;
	protected TextView location;
	protected TextView attending;

	protected TextView hostName;
	protected ImageView hostImage;
	protected TextView viaName;
	protected TextView description;

	protected EditText commentText;
	protected Button postComment;
	protected LinearLayout tagHolder;
	protected LinearLayout commentHolder;

	// Held Entities

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_eventview);

		Long eventId = getIntent().getLongExtra(
				Constants.INTENT_ZEPPA_EVENT_ID, -1);

		if (eventId < 0) {
			Toast.makeText(this, "Event Not Specified", Toast.LENGTH_SHORT)
					.show();
			onBackPressed();
		}

		eventMediator = ZeppaEventSingleton.getInstance().getEventById(eventId);
		
		
		// UI Elements
		final ActionBar actionBar = getActionBar();
		actionBar.setTitle(R.string.title_details);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		title = (TextView) findViewById(R.id.eventactivity_eventtitle);
		conflictIndicator = (ImageView) findViewById(R.id.eventactivity_stateindicator);
		conflictIndicator.setOnClickListener(this);
		
		time = (TextView) findViewById(R.id.eventactivity_time);
		time.setOnClickListener(this);
		location = (TextView) findViewById(R.id.eventactivity_location);
		location.setOnClickListener(this);
		attending = (TextView) findViewById(R.id.eventactivity_attending);
		attending.setText("Loading...");

		hostImage = (ImageView) findViewById(R.id.eventactivity_hostimage);
		hostName = (TextView) findViewById(R.id.eventactivity_hostname);
		description = (TextView) findViewById(R.id.eventactivity_description);

		commentText = (EditText) findViewById(R.id.eventactivity_commenttext);
		postComment = (Button) findViewById(R.id.eventactivity_postcomment);
		postComment.setOnClickListener(this);
		
		tagHolder = (LinearLayout) findViewById(R.id.eventactivity_tagholder);
		commentHolder = (LinearLayout) findViewById(R.id.eventactivity_commentholder);


		View barView = findViewById(R.id.eventactivity_quickactionbar);
		eventMediator.convertQuickActionBar(barView);

		commentAdapter = new EventCommentAdapter();
	}

	@Override
	protected void onStart() {
		super.onStart();

		eventMediator.setContext(this);
		setEventInfo();
		setHostInfo();
		setEventTagAdapter();
		setAttendingText();
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		super.onConnected(connectionHint);
		
		if (didLoadInitial()) {
			fetchAttendingUserRelationshipsInAsync();
			updateCommentsInAsync();
		} else {
			ZeppaUserSingleton.getInstance().registerLoadListener(this);
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {

		case android.R.id.home:
			onBackPressed();
			return true;

		}

		return false;
	}
	
	

	@Override
	protected void onResume() {
		drawComments();
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.eventactivity_stateindicator:
		case R.id.eventactivity_time:
			raiseCalendarDialog();
			break;

		case R.id.eventactivity_location:
			startNavigation();
			break;

		case R.id.eventactivity_postcomment:
			postComment();
			break;

		}

	}

	@Override
	public boolean didLoadInitial() {
		// TODO Auto-generated method stub
		return ZeppaUserSingleton.getInstance().hasLoadedInitial();
	}

	@Override
	public void onFinishLoad() {
		// TODO Auto-generated method stub
		fetchAttendingUserRelationshipsInAsync();
		updateCommentsInAsync();
	}

	/*
	 * -------------- My Methods -------------------
	 */

	protected void setEventInfo() {
		title.setText(eventMediator.getTitle());
		description.setText(eventMediator.getDescription());
		time.setText(eventMediator.getTimeString());
		location.setText(eventMediator.getDisplayLocation());
		setConfliction();
		setEventTagAdapter();
	}

	protected void startNavigation() {
		String location = eventMediator.getMapsLocation();

		if (location == null) {
			Toast.makeText(this, "Address Not Set", Toast.LENGTH_SHORT).show();
		}

		Intent toDirections = new Intent(android.content.Intent.ACTION_VIEW,
				Uri.parse("http://maps.google.com/maps?f=d&daddr=" + location));
		startActivity(toDirections);
	}

	protected abstract void setHostMediator();

	protected abstract void setEventTagAdapter();

	protected void setHostInfo() {
		setHostMediator();
		hostMediator.setImageWhenReady(hostImage);
		hostName.setText(hostMediator.getDisplayName());
	}

	protected abstract void setAttendingText();

	private void raiseCalendarDialog() {
		eventMediator.raiseCalendarDialog();
	}

	protected abstract void setConfliction();

	private void postComment() {
		if (isConnected()) {
			String message = commentText.getText().toString().trim();
			if (!message.isEmpty()) {
				// Disable until call is completed
				commentText.setEnabled(false);
				postComment.setClickable(false);

				// Create comment
				EventComment comment = new EventComment();
				comment.setCommenterId(ZeppaUserSingleton.getInstance()
						.getUserId());
				comment.setEventId(eventMediator.getEventId());
				comment.setText(message);
				new PostCommentTask(getGoogleAccountCredential(), comment)
						.execute();
			}
		}
	}

	/**
	 * This method fetches the attending user relationships in a new thread
	 */
	protected void fetchAttendingUserRelationshipsInAsync() {


			Object[] params = { getGoogleAccountCredential(), eventMediator };

			new AsyncTask<Object, Void, Boolean>() {

				@Override
				protected Boolean doInBackground(Object... params) {
					GoogleAccountCredential credentail = (GoogleAccountCredential) params[0];
					AbstractZeppaEventMediator mediator = (AbstractZeppaEventMediator) params[1];
					return mediator
							.loadAttendingRelationshipsWithBlocking(credentail);
				}

				@Override
				protected void onPostExecute(Boolean result) {
					super.onPostExecute(result);
					if (result) {
						setAttendingText();
					} else {
						attending.setText("Load Error");
					}
				}

			}.execute(params);

		

	}

	protected void updateCommentsInAsync() {
		if(commentAdapter.getCount() == 0){
			new LoadCommentsTask(getGoogleAccountCredential()).execute();
		} else {
			Long lastCommentPostTimeInMillis = commentAdapter.getLatestPostTime();
			new LoadNewCommentsTask(getGoogleAccountCredential(), lastCommentPostTimeInMillis).execute();
		}
	}
	
	/*
	 * The following handles working with comments, loading at appropriate times
	 * and storing them in the mediator
	 */

	protected void drawComments() {

		
		commentHolder.removeAllViews();
		for (int i = 0; i < commentAdapter.getCount(); i++) {
			commentHolder.addView(commentAdapter.getView(i, null, commentHolder));
		}

	}

	/**
	 * This sorts a list of comments by creation date
	 */
	protected final Comparator<EventComment> COMMENT_COMPARATOR = new Comparator<EventComment>() {

		@Override
		public int compare(EventComment lhs, EventComment rhs) {
			return (int) ((lhs.getCreated().longValue()) - (rhs.getCreated()
					.longValue()));
		}

	};

	/**
	 * Posts a comment to an event through a spun thread
	 * 
	 * @author DrunkWithFunk21
	 * 
	 */
	private class PostCommentTask extends AsyncTask<Void, Void, Boolean> {

		private GoogleAccountCredential credential;
		private EventComment comment;

		public PostCommentTask(GoogleAccountCredential credential,
				EventComment comment) {
			this.credential = credential;
			this.comment = comment;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			Boolean success = Boolean.FALSE;

			Eventcommentendpoint.Builder builder = new Eventcommentendpoint.Builder(
					AndroidHttp.newCompatibleTransport(),
					GsonFactory.getDefaultInstance(), credential);
			builder = CloudEndpointUtils.updateBuilder(builder);
			Eventcommentendpoint endpoint = builder.build();

			try {
				comment = endpoint.insertEventComment(comment).execute();
				eventMediator.addAllComments(Arrays.asList(comment));
				success = Boolean.TRUE;
			} catch (IOException e) {
				e.printStackTrace();
			}

			return success;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			commentText.setEnabled(true);
			postComment.setClickable(true);

			if (result) {
				commentText.setText("");
				commentAdapter.notifyDataSetChanged();
				drawComments();
			} else {
				Toast.makeText(getApplicationContext(), "Error Adding Comment",
						Toast.LENGTH_SHORT).show();
			}

		}

	}

	private class LoadNewCommentsTask extends AsyncTask<Void, Void, Boolean> {

		private GoogleAccountCredential credential;
		private Long minPostTime;

		public LoadNewCommentsTask(GoogleAccountCredential credential,
				Long minPostTime) {
			this.credential = credential;
			this.minPostTime = minPostTime;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			Boolean redraw = Boolean.FALSE;

			Eventcommentendpoint.Builder builder = new Eventcommentendpoint.Builder(
					AndroidHttp.newCompatibleTransport(),
					GsonFactory.getDefaultInstance(), credential);
			builder = CloudEndpointUtils.updateBuilder(builder);
			Eventcommentendpoint endpoint = builder.build();

			String cursor = null;
			String filter = "eventId == "
					+ eventMediator.getEventId().longValue() + " && created > "
					+ minPostTime.longValue();
			Integer limit = Integer.valueOf(20);
			String order = "created desc";
			do {
				try {
					ListEventComment task = endpoint.listEventComment();
					task.setFilter(filter);
					task.setCursor(cursor);
					task.setLimit(limit);
					task.setOrdering(order);

					CollectionResponseEventComment response = task.execute();

					if (response == null || response.getItems() == null
							|| response.getItems().isEmpty()) {
						cursor = null;
					} else {
						List<EventComment> loadedComments = response.getItems();
						Iterator<EventComment> iterator = loadedComments
								.iterator();

						List<EventComment> addComments = new ArrayList<EventComment>();
						while (iterator.hasNext()) {
							EventComment comment = iterator.next();
							if (ZeppaUserSingleton.getInstance()
									.fetchUserAndRelationshipWithBlocking(
											comment.getCommenterId(),
											credential, false)) {
								addComments.add(comment);
							}

						}

						if (loadedComments.size() < limit) {
							cursor = null;
						} else {
							cursor = response.getNextPageToken();
						}

						if (!addComments.isEmpty()) {
							eventMediator.addAllComments(addComments);
							redraw = Boolean.TRUE;
						}

					}

				} catch (IOException e) {
					e.printStackTrace();
					cursor = null;
				}
			} while (cursor != null);

			return redraw;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			if (result) {
				commentAdapter.notifyDataSetChanged();
				drawComments();
			}

		}

	}

	private class LoadCommentsTask extends AsyncTask<Void, Void, Boolean> {

		private GoogleAccountCredential credential;

		public LoadCommentsTask(GoogleAccountCredential credential) {
			this.credential = credential;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			Boolean redraw = Boolean.FALSE;

			Eventcommentendpoint.Builder builder = new Eventcommentendpoint.Builder(
					AndroidHttp.newCompatibleTransport(),
					GsonFactory.getDefaultInstance(), credential);
			builder = CloudEndpointUtils.updateBuilder(builder);
			Eventcommentendpoint endpoint = builder.build();

			String filter = "eventId == "
					+ eventMediator.getEventId().longValue();
			Integer limit = Integer.valueOf(20);
			String order = "created desc";

			ListEventComment task;
			try {
				task = endpoint.listEventComment();

				task.setCursor(eventMediator.getCommentCursor());
				task.setFilter(filter);
				task.setLimit(limit);
				task.setOrdering(order);
				CollectionResponseEventComment response = task.execute();

				if (response != null && response.getItems() != null
						&& !response.getItems().isEmpty()) {
					List<EventComment> loadedComments = response.getItems();
					Iterator<EventComment> iterator = loadedComments.iterator();

					List<EventComment> addComments = new ArrayList<EventComment>();
					while (iterator.hasNext()) {
						EventComment comment = iterator.next();
						if (ZeppaUserSingleton.getInstance()
								.fetchUserAndRelationshipWithBlocking(
										comment.getCommenterId(), credential, false)) {
							addComments.add(comment);
						}

						// TODO: else, flag

					}

					eventMediator.setCommentCursor(response.getNextPageToken());

					if (!addComments.isEmpty()) {
						eventMediator.addAllComments(addComments);
						redraw = Boolean.TRUE;
					}

				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return redraw;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			if (result) {
				commentAdapter.notifyDataSetChanged();
				drawComments();
			}

		}

	}

	/**
	 * This adapts comments to views
	 * 
	 * @author DrunkWithFunk21
	 * 
	 */
	private class EventCommentAdapter extends BaseAdapter {

		private List<EventComment> comments;

		public EventCommentAdapter() {
			comments = eventMediator.getEventComments();
			Collections.sort(comments, COMMENT_COMPARATOR);
		}

		public Long getLatestPostTime(){
			
			return comments.get(comments.size() - 1).getCreated();
		}
		
		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
			comments = eventMediator.getEventComments();
			Collections.sort(comments, COMMENT_COMPARATOR);

		}

		@Override
		public int getCount() {
			return comments.size();
		}

		@Override
		public EventComment getItem(int position) {
			// TODO Auto-generated method stub
			return comments.get(position);
		}

		@Override
		public long getItemId(int position) {
			return getItem(position).getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.view_comment, parent, false);
			}

			// Get the Comment and the Mediator
			EventComment comment = getItem(position);
			AbstractZeppaUserMediator mediator = ZeppaUserSingleton
					.getInstance().getAbstractUserMediatorById(
							comment.getCommenterId());

			// Set User Name
			TextView name = (TextView) convertView
					.findViewById(R.id.comment_name);

			if (mediator == null) {
				name.setText("Load Error!");
			} else {
				name.setText(mediator.getDisplayName());

				// Set the User Image
				ImageView image = (ImageView) convertView
						.findViewById(R.id.comment_pic);
				mediator.setImageWhenReady(image);

			}
			// Set the Date
			TextView dateText = (TextView) convertView
					.findViewById(R.id.comment_date);
			dateText.setText(Utils.getDisplayDateString(comment.getCreated()));

			// Set comment text
			TextView message = (TextView) convertView
					.findViewById(R.id.comment_text);
			message.setText(comment.getText());

			return convertView;
		}

	}

}
