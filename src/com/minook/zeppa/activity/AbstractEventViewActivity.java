package com.minook.zeppa.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.Utils;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.adapter.MinglerListAdapter;
import com.minook.zeppa.adapter.tagadapter.AbstractTagAdapter;
import com.minook.zeppa.eventcommentendpoint.Eventcommentendpoint;
import com.minook.zeppa.eventcommentendpoint.model.EventComment;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator.OnCommentLoadListener;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator.OnEventUpdateListener;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator.OnRelationshipsLoadedListener;
import com.minook.zeppa.mediator.AbstractZeppaUserMediator;
import com.minook.zeppa.mediator.DefaultUserInfoMediator;
import com.minook.zeppa.runnable.FetchCommentsRunnable;
import com.minook.zeppa.runnable.SendEventInvitesRunnable;
import com.minook.zeppa.runnable.ThreadManager;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.model.ZeppaEventToUserRelationship;
import com.minook.zeppa.zeppanotificationendpoint.model.ZeppaNotification;

public abstract class AbstractEventViewActivity extends
		AuthenticatedFragmentActivity implements OnClickListener,
		OnCommentLoadListener, OnEventUpdateListener,
		OnRelationshipsLoadedListener, OnRefreshListener {

	// final private String TAG = getClass().getName();

	private long eventId;
	protected AbstractZeppaEventMediator eventMediator;
	protected AbstractZeppaUserMediator hostMediator;
	protected AbstractTagAdapter tagAdapter;
	protected EventCommentAdapter commentAdapter;

	private AddInvitesAdapter mInvitesAdapter;

	// UI Elements
	protected TextView title;
	protected ImageView conflictIndicator;

	protected TextView time;
	protected TextView location;
	protected TextView attending;

	protected TextView hostName;
	protected ImageView hostImage;
	protected TextView description;
	protected TextView sendInvites;
	protected LinearLayout sendInvitesHolder;

	protected EditText commentText;
	protected TextView postComment;
	protected LinearLayout tagHolder;
	protected LinearLayout commentHolder;

	private PullToRefreshLayout pullToRefreshLayout;
	private boolean isUpdatingEventRelationships;

	protected View barView;

	// Held Entities

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_eventview);

		eventId = getIntent().getLongExtra(Constants.INTENT_ZEPPA_EVENT_ID, -1);

		if (eventId < 0) {
			Toast.makeText(this, "Event Not Specified", Toast.LENGTH_SHORT)
					.show();
			onBackPressed();
		}

		eventMediator = ZeppaEventSingleton.getInstance().getEventById(eventId);

		if (eventMediator == null) {
			onBackPressed();
		}

		setHostMediator();

		// UI Elements
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(getResources().getString(R.string.title_details));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);

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
		sendInvites = (TextView) findViewById(R.id.eventactivity_sendinvites);
		sendInvitesHolder = (LinearLayout) findViewById(R.id.eventactivity_sendinvitesholder);
		commentText = (EditText) findViewById(R.id.eventactivity_commenttext);
		postComment = (TextView) findViewById(R.id.eventactivity_postcomment);
		postComment.setOnClickListener(this);

		isUpdatingEventRelationships = false;
		pullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.eventactivity_ptr);

		ActionBarPullToRefresh.from(this)
				.options(Options.create().scrollDistance(.4f).build())
				.allChildrenArePullable().listener(this)
				.setup(pullToRefreshLayout);

		tagHolder = (LinearLayout) findViewById(R.id.eventactivity_tagholder);
		commentHolder = (LinearLayout) findViewById(R.id.eventactivity_commentholder);

		barView = findViewById(R.id.eventactivity_quickactionbar);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		try {
			setEventInfo();
			setHostInfo();
			setEventTagAdapter();
			setAttendingText();

			barView = eventMediator.convertQuickActionBar(this, barView);
			eventMediator.registerCommentLoadListener(this);
			eventMediator.registerOnRelationshipsLoadedListener(this);
			commentAdapter = new EventCommentAdapter();

		} catch (Exception e) {
			e.printStackTrace();
			onBackPressed();
		}

	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		super.onConnected(connectionHint);

		startFetchEventExtrasThread();
		fetchEventComments();

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
		super.onResume();
		drawComments();
		tagAdapter.drawTags();
	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	@Override
	protected void onDestroy() {

		eventMediator.unregisterCommentLoadListener();
		eventMediator.unregisterOnRelationshipsLoadedListener(this);

		super.onDestroy();
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
			// eventMediator;

			switch (eventMediator.getConflictStatus().ordinal()) {
			case 0:
				Toast.makeText(this, "You're going", Toast.LENGTH_SHORT).show();
				break;
			case 1:
				Toast.makeText(this, "No Calendar Conflicts",
						Toast.LENGTH_SHORT).show();
				break;
			case 2:
				Toast.makeText(this, "Partial Confliction", Toast.LENGTH_SHORT)
						.show();
				break;
			case 3:
				Toast.makeText(this, "Complete Confliction", Toast.LENGTH_SHORT)
						.show();
				break;
			}

			break;

		case R.id.eventactivity_time:
			eventMediator.viewInCalendarApplication(this);
			break;

		case R.id.eventactivity_location:
			startNavigation();
			break;

		case R.id.eventactivity_postcomment:
			postComment();
			break;

		case R.id.eventactivity_attending:
			showAttendingDialog();
			break;

		case R.id.eventactivity_sendinvites:
			showSendInvitesDialog();
			break;

		}

	}

	@Override
	public void onCommentsLoaded() {
		commentAdapter.notifyDataSetChanged();
		drawComments();
	}

	@Override
	public void onRelationshipsLoaded() {
		setAttendingText();
		attending.setOnClickListener(this);
		mInvitesAdapter = new AddInvitesAdapter(
				eventMediator.getEventRelationships());
		isUpdatingEventRelationships = false;
		pullToRefreshLayout.setRefreshing(false);
	}

	@Override
	public void onEventUpdate(ZeppaEvent event) {
		setEventInfo();

	}

	@Override
	public void onErrorLoadingRelationships() {
		Toast.makeText(this, "Error Updating Event", Toast.LENGTH_SHORT).show();
		isUpdatingEventRelationships = false;
		pullToRefreshLayout.setRefreshing(false);
	}

	@Override
	public void onErrorUpdatingEvent() {
		Toast.makeText(this, "Error Updating Event", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onErrorLoadingComments() {
		Toast.makeText(this, "Error Loading Comments", Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void onNotificationReceived(ZeppaNotification notification) {

		if (notification.getType().equalsIgnoreCase("COMMENT_ON_POST")
				&& notification.getEventId().longValue() == eventMediator
						.getEventId().longValue() && isConnected()) {

			fetchEventComments();

		} else {
			super.onNotificationReceived(notification);
		}

	}

	@Override
	public void onRefreshStarted(View view) {
		startFetchEventExtrasThread();
		fetchEventComments();
	}

	protected void setEventInfo() {
		title.setText(eventMediator.getTitle());
		description.setText(eventMediator.getDescription());
		time.setText(eventMediator.getTimeString());
		location.setText(eventMediator.getDisplayLocation());
		eventMediator.setConflictIndicator(this, conflictIndicator);
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
		hostMediator.setImageWhenReady(hostImage);
		hostName.setText(hostMediator.getDisplayName());
	}

	protected abstract void setAttendingText();

	private void showSendInvitesDialog() {

		if (mInvitesAdapter.getCount() == 0) {
			Toast.makeText(this, "Nobody Left To Invite", Toast.LENGTH_SHORT)
					.show();
			return;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		ListView list = new ListView(this);
		mInvitesAdapter = new AddInvitesAdapter(
				eventMediator.getEventRelationships());
		list.setAdapter(mInvitesAdapter);
		builder.setTitle("Send Invites");
		builder.setView(list);
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == DialogInterface.BUTTON_POSITIVE) {
					isUpdatingEventRelationships = true;
					ThreadManager.execute(new SendEventInvitesRunnable(
							(ZeppaApplication) getApplication(),
							getGoogleAccountCredential(), eventMediator
									.getEventId().longValue(),
							ZeppaUserSingleton.getInstance().getUserId()
									.longValue(),
							AbstractEventViewActivity.this, mInvitesAdapter
									.getEventRelationshipsToInsert(),
							mInvitesAdapter.getEventRelationshipsToUpdate()));

				}
				dialog.dismiss();
			}
		};

		builder.setNegativeButton("Dismiss", listener);
		builder.setPositiveButton("Send Invites", listener);
		builder.show();

	}

	private void showAttendingDialog() {

		List<Long> attendingMediators = eventMediator.getAttendingUserIds();
		List<DefaultUserInfoMediator> mediators = ZeppaUserSingleton
				.getInstance().getMinglersFrom(attendingMediators);
		if (mediators.isEmpty()) {

			Toast.makeText(this, "Nobody joined yet", Toast.LENGTH_SHORT)
					.show();

		} else {
			MinglerListAdapter mAdapter = new MinglerListAdapter(this,
					mediators);

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("People Going");
			ListView list = new ListView(this);
			list.setAdapter(mAdapter);
			list.setOnItemClickListener(mAdapter);
			builder.setView(list);

			builder.setPositiveButton("Dismiss",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

						}
					});
			builder.show();
		}

	}

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
	protected void startFetchEventExtrasThread() {

		if (isUpdatingEventRelationships) {
			return;
		} else {
			isUpdatingEventRelationships = true;
		}

		eventMediator.setConflictIndicator(this, conflictIndicator);
		pullToRefreshLayout.setRefreshing(true);
		eventMediator.loadEventRelationships(
				(ZeppaApplication) getApplication(),
				getGoogleAccountCredential(), this);

	}

	/**
	 * Start thread to fetch event comments
	 */
	protected void fetchEventComments() {

		ThreadManager.execute(new FetchCommentsRunnable(
				(ZeppaApplication) getApplication(),
				getGoogleAccountCredential(), eventMediator, commentAdapter
						.getLatestCommentPostTime().longValue()));

	}

	/*
	 * The following handles working with comments, loading at appropriate times
	 * and storing them in the mediator
	 */

	protected void drawComments() {

		commentHolder.removeAllViews();

		if (commentAdapter.getCount() > 0) {

			for (int i = 0; i < commentAdapter.getCount(); i++) {
				commentHolder.addView(commentAdapter.getView(i, null,
						commentHolder));
			}
		}
	}

	/**
	 * This sorts a list of comments by creation date
	 */
	protected final Comparator<EventComment> COMMENT_COMPARATOR = new Comparator<EventComment>() {

		@Override
		public int compare(EventComment lhs, EventComment rhs) {
			return (int) ((rhs.getCreated().longValue()) - (lhs.getCreated()
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

	/**
	 * This adapts comments to views
	 * 
	 * @author DrunkWithFunk21
	 * 
	 */
	public class EventCommentAdapter extends BaseAdapter {

		private List<EventComment> comments;

		public EventCommentAdapter() {
			comments = eventMediator.getEventComments();
			Collections.sort(comments, COMMENT_COMPARATOR);
		}

		public Long getLatestCommentPostTime() {

			if (comments.isEmpty()) {
				return Long.valueOf(-1);
			}

			return comments.get(0).getCreated();
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

	/**
	 * This class is for dynamically adding invites to a given event. As long as
	 * the user was not
	 * 
	 * @author DrunkWithFunk21
	 * 
	 */
	private class AddInvitesAdapter extends BaseAdapter implements
			OnClickListener {

		private List<DefaultUserInfoMediator> mediators;
		private List<DefaultUserInfoMediator> inviteMediators;
		private List<ZeppaEventToUserRelationship> relationships;

		public AddInvitesAdapter(
				List<ZeppaEventToUserRelationship> relationships) {

			this.relationships = relationships;
			this.mediators = new ArrayList<DefaultUserInfoMediator>();
			this.inviteMediators = new ArrayList<DefaultUserInfoMediator>();

			List<DefaultUserInfoMediator> current = ZeppaUserSingleton
					.getInstance().getMinglerMediators();
			Iterator<DefaultUserInfoMediator> iterator = current.iterator();

			while (iterator.hasNext()) {
				DefaultUserInfoMediator mediator = iterator.next();

				if (isInvitable(mediator.getUserId().longValue(), relationships)) {
					mediators.add(mediator);
				}

			}

		}

		public List<ZeppaEventToUserRelationship> getEventRelationshipsToUpdate() {
			List<ZeppaEventToUserRelationship> updateRelationships = new ArrayList<ZeppaEventToUserRelationship>();

			Iterator<DefaultUserInfoMediator> iterator = inviteMediators
					.iterator();

			while (iterator.hasNext()) {
				DefaultUserInfoMediator mediator = iterator.next();
				Iterator<ZeppaEventToUserRelationship> iterator2 = relationships
						.iterator();
				while (iterator2.hasNext()) {
					ZeppaEventToUserRelationship relationship = iterator2
							.next();
					if (relationship.getUserId().longValue() == mediator
							.getUserId().longValue()) {
						relationship.setInvitedByUserId(ZeppaUserSingleton
								.getInstance().getUserId().longValue());
						relationship.setWasInvited(true);
						updateRelationships.add(relationship);
						break;
					}
				}

			}

			return updateRelationships;
		}

		public List<ZeppaEventToUserRelationship> getEventRelationshipsToInsert() {
			List<ZeppaEventToUserRelationship> insertRelationships = new ArrayList<ZeppaEventToUserRelationship>();

			Iterator<DefaultUserInfoMediator> iterator = inviteMediators
					.iterator();

			while (iterator.hasNext()) {
				DefaultUserInfoMediator mediator = iterator.next();
				Iterator<ZeppaEventToUserRelationship> iterator2 = relationships
						.iterator();
				boolean doAdd = true;
				while (iterator2.hasNext()) {
					ZeppaEventToUserRelationship relationship = iterator2
							.next();
					if (relationship.getUserId().longValue() == mediator
							.getUserId().longValue()) {
						doAdd = false;
						break;
					}
				}

				if (doAdd) {
					ZeppaEventToUserRelationship relationship = new ZeppaEventToUserRelationship();
					relationship.setEventHostId(hostMediator.getUserId()
							.longValue());
					relationship.setEventId(eventMediator.getEventId()
							.longValue());
					relationship.setExpires(eventMediator.getEndInMillis()
							.longValue());
					relationship.setWasInvited(true);
					relationship.setInvitedByUserId(ZeppaUserSingleton
							.getInstance().getUserId());
					relationship.setIsRecommended(true);
					relationship.setIsAttending(false);
					relationship.setIsWatching(false);
					relationship.setUserId(mediator.getUserId().longValue());
					insertRelationships.add(relationship);
				}

			}

			return insertRelationships;
		}

		private boolean isInvitable(long userId,
				List<ZeppaEventToUserRelationship> relationships) {

			if (userId == hostMediator.getUserId().longValue()) {
				return false;
			}

			Iterator<ZeppaEventToUserRelationship> iterator = relationships
					.iterator();
			while (iterator.hasNext()) {
				ZeppaEventToUserRelationship relationship = iterator.next();
				if (relationship.getUserId().longValue() == userId) {

					if (relationship.getWasInvited()
							|| relationship.getIsAttending()) {
						return false;
					} else {
						return true;
					}

				}

			}

			return true;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mediators.size();
		}

		@Override
		public DefaultUserInfoMediator getItem(int position) {
			// TODO Auto-generated method stub
			return mediators.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return getItem(position).getUserId().longValue();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.view_invitelist_item, parent, false);
			}

			DefaultUserInfoMediator mediator = getItem(position);
			convertView = mediator.convertInviteListItemView(convertView);
			convertView.setOnClickListener(this);
			convertView.setTag(mediator);

			return convertView;
		}

		@Override
		public void onClick(View v) {
			DefaultUserInfoMediator mediator = (DefaultUserInfoMediator) v
					.getTag();
			CheckBox box = (CheckBox) v.findViewById(R.id.inviteitem_checkbox);

			if (inviteMediators.remove(mediator)) {
				box.setChecked(false);
			} else {
				inviteMediators.add(mediator);
				box.setChecked(true);
			}

		}

	}

}
