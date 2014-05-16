package com.minook.zeppa.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.Constants;
import com.minook.zeppa.Constants.ConflictionStatus;
import com.minook.zeppa.R;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.adapters.ContactListAdapter;
import com.minook.zeppa.adapters.EventCommentAdapter;
import com.minook.zeppa.adapters.RepostsListAdapter;
import com.minook.zeppa.adapters.tagadapter.EventTagAdapter;
import com.minook.zeppa.adapters.tagadapter.TagAdapter;
import com.minook.zeppa.fragmentdialogs.RepostDialogFragment;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;
import com.minook.zeppa.zeppauserendpoint.Zeppauserendpoint;
import com.minook.zeppa.zeppauserendpoint.Zeppauserendpoint.GetZeppaUser;
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUser;

public class EventViewActivity extends AuthenticatedFragmentActivity implements
		OnClickListener {

	final private String TAG = getClass().getName();

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
	protected ImageView postComment;

	// Held Entities
	protected Long eventId;
	protected ZeppaEvent zeppaEvent;
	protected ZeppaUser host;

	protected ZeppaEvent originalEvent; // in case event was reposted. This will alway

	// Held Arrays
	protected List<ZeppaUser> friendsGoing;
	
	// Adapters
	protected EventCommentAdapter commentAdapter;
	protected TagAdapter tagAdapter;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_eventview);
		
		eventId = getIntent().getLongExtra(Constants.INTENT_ZEPPA_EVENT_ID, -1);

		// UI Elements
		final ActionBar actionBar = getActionBar();

		title = (TextView) findViewById(R.id.eventactivity_eventtitle);
		conflictIndicator = (ImageView) findViewById(R.id.eventactivity_stateindicator);

		time = (TextView) findViewById(R.id.eventactivity_time);
		location = (TextView) findViewById(R.id.eventactivity_location);
		attending = (TextView) findViewById(R.id.eventactivity_attending);

		
		hostImage = (ImageView) findViewById(R.id.eventactivity_hostimage);
		hostName = (TextView) findViewById(R.id.eventactivity_hostname);
		viaName = (TextView) findViewById(R.id.eventactivity_vianame);
		description = (TextView) findViewById(R.id.eventactivity_description);

		
		commentText = (EditText) findViewById(R.id.eventactivity_commenttext);
		postComment = (ImageView) findViewById(R.id.eventactivity_postcomment);
		

		
		postComment.setOnClickListener(this);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		
		getNSetZeppaEvent(eventId);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		if (zeppaEvent == null) {
			return false;
		} else {
			Long userId = ZeppaUserSingleton.getInstance().getUserId();

			if (zeppaEvent.getUsersGoingIds() != null
					&& zeppaEvent.getUsersGoingIds().contains(userId)) {
				// Joined Event
				getMenuInflater().inflate(R.menu.menu_event_joined, menu);
				switch (zeppaEvent.getEventScope()) {
				case 1: // No Repost
					menu.getItem(1).setVisible(false); // repost gone
					menu.getItem(4).setVisible(false); // invite gone
					break;

				case 2: // Casual
					menu.getItem(1).setVisible(false); // repost gone
					break;
				}

			} else if (zeppaEvent.getUsersWatchingIds() != null
					&& zeppaEvent.getUsersWatchingIds().contains(userId)) {
				// Watching event
				getMenuInflater().inflate(R.menu.menu_event_watching, menu);

				if (zeppaEvent.getEventScope() != 0) {
					menu.getItem(2).setVisible(false);
				}

			} else {
				// Default
				getMenuInflater().inflate(R.menu.menu_event, menu);
				if (zeppaEvent.getEventScope() != 0) {
					menu.getItem(2).setVisible(false);
				}

			}

			return true;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;

		/*
		 * Watching Controls
		 */
		case R.id.menu_event_watch:
			handleWatchingInAsync(true);
			return true;

		case R.id.menu_event_stopwatching:
			handleWatchingInAsync(false);
			break;

		/*
			 * 
			 * */

		case R.id.menu_event_join:
			handleJoinInAsync(!item.isChecked(), item);
			item.setChecked(!item.isChecked());

			return true;

		case R.id.menu_event_refresh:
			// TODO: call refresh method..
			return true;

		case R.id.menu_event_repost:
			raiseRepostDialog();

			return true;

		case R.id.menu_event_directions:
			String location = (zeppaEvent.getExactLocation() != null ? zeppaEvent
					.getExactLocation() : zeppaEvent.getShortLocation());
			Intent toDirections = new Intent(
					android.content.Intent.ACTION_VIEW,
					Uri.parse("http://maps.google.com/maps?f=d&daddr="
							+ location));
			startActivity(toDirections);
			return true;

		case R.id.menu_event_texthost:

			String hostNumber = host.getPhoneNumber();
			if (hostNumber == null || hostNumber.isEmpty()) {
				Toast.makeText(this,
						"No number saved for " + host.getDisplayName(),
						Toast.LENGTH_SHORT).show();
			} else {
				Intent smsIntent = new Intent(Intent.ACTION_VIEW);
				smsIntent.setType("vnd.android-dir/mms-sms");
				smsIntent.putExtra("address", hostNumber);
				smsIntent.putExtra("sms_body",
						"Regarding " + zeppaEvent.getTitle() + ", ");
				startActivity(smsIntent);
			}

			return true;

		case R.id.menu_event_emailhost:

			Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
					Uri.fromParts("mailto", host.getEmail(), null));
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Regarding "
					+ zeppaEvent.getTitle());
			startActivity(Intent.createChooser(emailIntent, "Send email..."));

			return true;

		}

		return true;

	}
	
	protected boolean isMyEvent(){
		return false;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.eventactivity_postcomment:
			String text = commentText.getText().toString();
			if (text != null && !text.isEmpty()) {
				commentAdapter.postCommentInAsync(text);
				commentText.setText("");
			} else {
				Toast.makeText(this, "Invalid text", Toast.LENGTH_SHORT).show();
			}
			break;

		case R.id.eventactivity_vianame:
			Intent toEvent = new Intent(this, EventViewActivity.class);
			toEvent.putExtra(Constants.INTENT_ZEPPA_EVENT_ID,
					zeppaEvent.getOriginalEventId());
			startActivity(toEvent);
			overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);
			break;

		case R.id.eventactivity_attending:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Friends attending " + zeppaEvent.getTitle());
			ContactListAdapter adapter = new ContactListAdapter(this,
					friendsGoing);
			builder.setAdapter(adapter, adapter);
			builder.setNegativeButton(R.string.dismiss, adapter);
			builder.show();

			break;
			
		case R.id.eventactivity_stateindicator:
			// TODO: raise conflict dialog, 
			break;

		}

	}

	private EventViewActivity getThis() {
		return this;
	}


	private Long getUserId() {
		return ZeppaUserSingleton.getInstance().getUserId();
	}

	/*
	 * -------------- My Methods -------------------
	 */

	private void getNSetZeppaEvent(final Long eventId) {

		zeppaEvent = ZeppaEventSingleton.getInstance().getEventById(eventId);

		if (zeppaEvent != null) {
			setEventInfo(zeppaEvent);
		} else {
			fetchZeppaEvent(eventId);
		}
	}

	private void setEventInfo(final ZeppaEvent event) {
		ZeppaUserSingleton userSingleton = ZeppaUserSingleton.getInstance();
		ZeppaEventSingleton eventSingleton = ZeppaEventSingleton.getInstance();
		host = userSingleton.getUserById(event.getHostId());
		boolean isOwnEvent = host.equals(userSingleton.getUser());
		Long userId = userSingleton.getUserId();
			
		
		if (host == null) {
			getHostInAsync(event.getHostId());
		} else {
			setHostInfo();
		}

		title.setText(event.getTitle());
		location.setText(event.getShortLocation());

		description.setText(event.getDescription());

		time.setText(Constants.getDisplayDatesString(event.getStart(),
				event.getEnd()));

		List<Long> goingIds = event.getUsersGoingIds();

		if (goingIds != null && !goingIds.isEmpty()) {
			if (goingIds.contains(userId)) {
				friendsGoing = userSingleton.getFriendsFrom(goingIds);

				if (friendsGoing.size() == 1) {
					attending.setText("you and "
							+ friendsGoing.get(0).getDisplayName()
							+ " going + " + (goingIds.size() - 2) + " others");
				} else {

					attending
							.setText("you + " + friendsGoing.size()
									+ " friends of " + (goingIds.size() + 1)
									+ " going");
				}
				attending.setOnClickListener(this);
			} else {
				friendsGoing = userSingleton.getFriendsFrom(goingIds);
				attending.setText(friendsGoing.size() + " friends of "
						+ goingIds.size() + " going");
				attending.setOnClickListener(this);
			}

		} else {
			attending.setText("Be the first to join!");

		}



		if (zeppaEvent.getOriginalEventId() != null) {
			getNSetViaInAsync();
		}
		

		
		LinearLayout commentHolder = (LinearLayout) findViewById(R.id.eventactivity_commentholder);
		commentAdapter = new EventCommentAdapter(this,zeppaEvent, commentHolder);
		
		handleConflictIndicator(userSingleton.getUserId(), isOwnEvent,
				eventSingleton);
		invalidateOptionsMenu();

	}

	private void setHostInfo() {
		hostName.setText(host.getDisplayName());
		setImageInAsync(host, hostImage);
		setTags();
	}
	
	protected void setTags(){
		LinearLayout tagholder = (LinearLayout) findViewById(R.id.eventactivity_tagholder);
		tagAdapter = new EventTagAdapter(this, tagholder, host, zeppaEvent);
		tagAdapter.drawTags();
	}

	protected void handleConflictIndicator(Long userId, boolean isOwnEvent,
			ZeppaEventSingleton eventSingleton) {

		Resources res = getResources();
		if ((zeppaEvent.getUsersGoingIds().contains(userId))) {
			conflictIndicator.setImageDrawable(res
					.getDrawable(R.drawable.small_circle_blue));
		} else {
			String accountName = ((ZeppaApplication) getApplication())
					.getAccountName();
			ConflictionStatus status = eventSingleton.getConflictionStatus(
					this, zeppaEvent, accountName, getContentResolver());

			switch (status.ordinal()) {
			case 0: // No conflict
				conflictIndicator.setImageDrawable(res
						.getDrawable(R.drawable.small_circle_green));
				break;
			case 1: // Partial Conflict
				conflictIndicator.setImageDrawable(res
						.getDrawable(R.drawable.small_circle_yellow));
				break;
			case 2: // Complete conflict
				conflictIndicator.setImageDrawable(res
						.getDrawable(R.drawable.small_circle_red));
				break;

			case 3: // Going
				conflictIndicator.setImageDrawable(res
						.getDrawable(R.drawable.small_circle_blue));
				break;

			}
		}

		conflictIndicator.setOnClickListener(this);
	}

	protected void tagDataChanged() {
		tagAdapter.notifyDataSetChanged();
	}





	/*
	 * Async Execution Methods
	 */

	private void fetchZeppaEvent(final Long eventId) {

		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("Getting Event");
		progressDialog.setMessage(getResources().getString(R.string.loading));
		progressDialog.setIndeterminate(true);

		progressDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
				onBackPressed();
			}
		});

		progressDialog.show();

		new AsyncTask<Void, Void, ZeppaEvent>() {

			@Override
			protected ZeppaEvent doInBackground(Void... params) {
				GoogleAccountCredential credential = ((ZeppaApplication) getApplication()).getGoogleAccountCredential();
				return ZeppaEventSingleton.getInstance().findOrFetchEventById(eventId, credential);
			}

			@Override
			protected void onPostExecute(ZeppaEvent result) {
				super.onPostExecute(result);
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
					if (result != null) {
						setEventInfo(result);
						invalidateOptionsMenu();

					} else {
						Toast.makeText(getApplicationContext(),
								"Error! Event not found", Toast.LENGTH_SHORT)
								.show();
						onBackPressed();
					}
				}

			}

		}.execute();

	}

	private void handleWatchingInAsync(final boolean didWatch) {

		if(zeppaEvent.getUsersWatchingIds() == null){
			zeppaEvent.setUsersWatchingIds(new ArrayList<Long>());
		}
		
		if (didWatch) {
			zeppaEvent.getUsersWatchingIds().add(getUserId());
		} else {
			zeppaEvent.getUsersWatchingIds().remove(getUserId());
		}

		Object[] params = { zeppaEvent };
		new AsyncTask<Object, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Object... params) {
				boolean success = false;
				ZeppaEventSingleton eventSingleton = ZeppaEventSingleton
						.getInstance();
				ZeppaEvent eventPassed = (ZeppaEvent) params[0];
				if (didWatch)
					success = eventSingleton.watchZeppaEvent(getCredential(),
							eventPassed);
				else
					success = eventSingleton.stopWatchingZeppaEvent(
							getCredential(), eventPassed);

				return success;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if (result) { // task executed successfully
					// 'Merica.
				} else {
					Toast.makeText(getApplicationContext(),
							"Cannot connect at this time", Toast.LENGTH_SHORT)
							.show();
					if (didWatch) {
						zeppaEvent.getUsersWatchingIds().remove(getUserId());
					} else {
						zeppaEvent.getUsersWatchingIds().add(getUserId());
					}
					invalidateOptionsMenu();
				}
			}

		}.execute(params);

		invalidateOptionsMenu();

	}

	private void handleJoinInAsync(final boolean didJoin, final MenuItem item) {
		Object[] params = { zeppaEvent };

		if(zeppaEvent.getUsersGoingIds() == null)
			zeppaEvent.setUsersGoingIds(new ArrayList<Long>());
		if(zeppaEvent.getUsersWatchingIds() == null){
			zeppaEvent.setUsersWatchingIds(new ArrayList<Long>());
		}
		
		final boolean wasWatching = zeppaEvent.containsKey(getUserId());
		
		if (didJoin) {
			if (wasWatching) {
				zeppaEvent.getUsersWatchingIds().remove(getUserId());
			}
			zeppaEvent.getUsersGoingIds().add(getUserId());

		} else {
			zeppaEvent.getUsersGoingIds().remove(getUserId());
		}

		new AsyncTask<Object, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Object... params) {
				boolean success = false;
				ZeppaEvent event = (ZeppaEvent) params[0];
				ZeppaEventSingleton eventSingleton = ZeppaEventSingleton
						.getInstance();

				if (didJoin)
					success = eventSingleton.joinZeppaEvent(
							getApplicationContext(), getCredential(), event);
				else
					success = eventSingleton.leaveZeppaEvent(
							getApplicationContext(), getCredential(), event);
				return success;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if (result) {// Operation executed successfully
					// Fuck Yeah.
				} else {
					Toast.makeText(getBaseContext(), "Error Occured Joining",
							Toast.LENGTH_SHORT).show();
					if (didJoin) {
						if (wasWatching) {
							zeppaEvent.getUsersWatchingIds().add(getUserId());
						}
						zeppaEvent.getUsersGoingIds().remove(getUserId());
					} else {
						zeppaEvent.getUsersGoingIds().add(getUserId());
					}
				}

			}

		}.execute(params);

		invalidateOptionsMenu();
	}

	private void getHostInAsync(final Long hostId) {

		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				boolean success = false;
				Zeppauserendpoint.Builder endpointBuilder = new Zeppauserendpoint.Builder(
						AndroidHttp.newCompatibleTransport(),
						new JacksonFactory(),
						((ZeppaApplication) getApplication())
								.getGoogleAccountCredential());
				endpointBuilder = CloudEndpointUtils
						.updateBuilder(endpointBuilder);

				Zeppauserendpoint userEndpoint = endpointBuilder.build();

				try {
					GetZeppaUser getUser = userEndpoint.getZeppaUser(hostId);
					ZeppaUser hostUser = getUser.execute();
					if (hostUser != null) {
						host = hostUser;
						success = true;
					}

				} catch (IOException e) {
					e.printStackTrace();
				}

				return success;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if (result) {
					setHostInfo();
				} else {

					Toast.makeText(getApplicationContext(),
							"Error Loading Host", Toast.LENGTH_SHORT).show();
					onBackPressed();
				}
			}

		}.execute();

	}

	private void getNSetViaInAsync() {
		final Long originalEventId = zeppaEvent.getOriginalEventId();
		if (originalEventId == null || originalEventId.longValue() < 0) {
			return;
		}
		viaName.setText("loading...");
		new AsyncTask<Void, Void, ZeppaUser>() {

			@Override
			protected ZeppaUser doInBackground(Void... params) {
				ZeppaEventSingleton eventSingleton = ZeppaEventSingleton
						.getInstance();
				originalEvent = eventSingleton.findOrFetchEventById(
						originalEventId, getCredential());
				if (originalEvent == null) {
					return null;
				}
				return ZeppaUserSingleton.getInstance().getOrFetchZeppaUser(
						originalEvent.getHostId(), getCredential());
			}

			@Override
			protected void onPostExecute(ZeppaUser result) {
				super.onPostExecute(result);
				if (result != null) {
					viaName.setText("via " + result.getDisplayName());
					viaName.setOnClickListener(getThis());
				} else {
					viaName.setText("Error occured");
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
							getApplicationContext(),
							"Error Occured Loading " + user.getGivenName()
									+ "\'s Picture", Toast.LENGTH_SHORT).show();
				}
			}

		}.execute();
	}
	
	private void raiseRepostDialog() {
		ZeppaEventSingleton eventSingleton = ZeppaEventSingleton.getInstance();
		ZeppaUserSingleton userSingleton = ZeppaUserSingleton.getInstance();

		if (zeppaEvent.getHostId().equals(userSingleton.getUserId())) {
			if (zeppaEvent.getReposts() == null
					|| zeppaEvent.getReposts().isEmpty()) {
				Toast.makeText(this, "Nobody has reposted", Toast.LENGTH_SHORT)
						.show();
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(zeppaEvent.getTitle() + " reposts");

				RepostsListAdapter adapter = new RepostsListAdapter(this,
						zeppaEvent.getKey().getId());

				builder.setAdapter(adapter, adapter);
				builder.setNeutralButton(R.string.dismiss, adapter);

				builder.show();
			}

		} else {

			Long repostId = eventSingleton.getMyRepostId(zeppaEvent.getKey()
					.getId());
			if (repostId > 0) { // Did repost
				Intent toRepost = new Intent();
				toRepost.putExtra(Constants.INTENT_ZEPPA_EVENT_ID, repostId);
				startActivity(toRepost);
				overridePendingTransition(R.anim.slide_left_in,
						R.anim.slide_left_out);

			} else { // Did not repost yet
				RepostDialogFragment repostDialog = new RepostDialogFragment();
				Bundle args = new Bundle();
				args.putLong(Constants.INTENT_ZEPPA_EVENT_ID, zeppaEvent.getKey().getId());
				repostDialog.show(getSupportFragmentManager(), "RepostDialog");
			}
		}

	}	

	
}
