package com.minook.zeppa.activity;

import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.adapter.MinglerListAdapter;
import com.minook.zeppa.adapter.eventlistadapter.MinglerEventsAdapter;
import com.minook.zeppa.adapter.tagadapter.MinglerTagAdapter;
import com.minook.zeppa.mediator.DefaultUserInfoMediator;
import com.minook.zeppa.mediator.DefaultZeppaEventMediator.OnMinglerRelationshipsLoadedListener;
import com.minook.zeppa.runnable.FetchDefaultTagsForUserRunnable;
import com.minook.zeppa.runnable.FetchEventsForMinglerRunnable;
import com.minook.zeppa.runnable.FetchMutualMingersRunnable;
import com.minook.zeppa.runnable.ThreadManager;
import com.minook.zeppa.singleton.EventTagSingleton;
import com.minook.zeppa.singleton.EventTagSingleton.OnTagLoadListener;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.singleton.ZeppaEventSingleton.OnZeppaEventLoadListener;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

public class MinglerActivity extends AuthenticatedFragmentActivity implements
		OnClickListener, OnMinglerRelationshipsLoadedListener,
		OnTagLoadListener, OnZeppaEventLoadListener, OnRefreshListener {

	// private static final String TAG = "UserActivity";

	private long userId;
	private ImageView userImage;
	private TextView userName;
	private TextView userPhoneNumber;
	private TextView userGmail;
	private TextView mutualMinglers;

	private LinearLayout eventHolder;
	private LinearLayout tagHolder;
	private PullToRefreshLayout pullToRefreshLayout;

	private MinglerTagAdapter tagAdapter;
	private MinglerEventsAdapter friendEventsAdapter;
	private DefaultUserInfoMediator userMediator;

	private boolean isUpdating;
	private boolean isUpdatingMinglerRelationships;
	private boolean isUpdatingMinglerEvents;
	private boolean isUpdatingMinglerTags;
	private boolean didInitialFetch;

	/*
	 * ------------- Override Methods --------------
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_mingler);
		isUpdating = false;
		isUpdatingMinglerRelationships = false;
		isUpdatingMinglerEvents = false;
		isUpdatingMinglerTags = false;
		didInitialFetch = false;

		if (userMediator == null) {
			try {
				Long passedUserId = getIntent().getExtras().getLong(
						Constants.INTENT_ZEPPA_USER_ID);

				if (passedUserId != null && passedUserId.longValue() > 0) {
					userId = passedUserId.longValue();
				}
				
				userMediator = (DefaultUserInfoMediator) ZeppaUserSingleton
						.getInstance().getAbstractUserMediatorById(userId);

			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(this, "Error fetching Mingler" , Toast.LENGTH_SHORT).show();
				onBackPressed();
			}

			

		}
		// find UI Elements and hold
		userImage = (ImageView) findViewById(R.id.useractivity_image);
		userName = (TextView) findViewById(R.id.useractivity_username);
		userPhoneNumber = (TextView) findViewById(R.id.useractivity_phonenumber);
		userGmail = (TextView) findViewById(R.id.useractivity_email);
		mutualMinglers = (TextView) findViewById(R.id.useractivity_mutualfriends);
		// Set loading mutual minglers test to loading for the time being
		mutualMinglers.setText("Loading...");

		eventHolder = (LinearLayout) findViewById(R.id.useractivity_eventholder);
		tagHolder = (LinearLayout) findViewById(R.id.useractivity_tagholder);

		// set Listeners
		mutualMinglers.setOnClickListener(this);
		userPhoneNumber.setOnClickListener(this);
		userGmail.setOnClickListener(this);

		// action bar
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(userMediator.getGivenName() + "'s Profile");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);

		// Set up pull to refresh capability
		pullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.mingleractivity_ptr);
		ActionBarPullToRefresh.from(this)
				.options(Options.create().scrollDistance(.4f).build())
				.allChildrenArePullable().listener(this)
				.setup(pullToRefreshLayout);

	}

	@Override
	protected void onStart() {
		super.onStart();

		tagAdapter = new MinglerTagAdapter(this, tagHolder,
				userMediator.getUserId(), null);
		friendEventsAdapter = new MinglerEventsAdapter(userMediator, this,
				eventHolder);

		setUserInfo();
		ZeppaEventSingleton.getInstance().registerEventLoadListener(this);

		tagAdapter.drawTags();
		try {
			friendEventsAdapter.drawEvents();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

//	@Override
//	protected void onResume() {
//		super.onResume();
//		
//	}

	@Override
	public void onConnected(Bundle connectionHint) {
		super.onConnected(connectionHint);
		// TODO: update UI
		if (!didInitialFetch) {
			didInitialFetch = true;
			fetchUpdatedInfo();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_minglerpage, menu);

		try {
			userMediator.getPrimaryPhoneNumber();

		} catch (NullPointerException e) {
			menu.findItem(R.id.action_textmingler).setVisible(false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;

		case R.id.action_unmingle:
			GoogleAccountCredential credential = getGoogleAccountCredential();

			ZeppaEventSingleton.getInstance().removeMediatorsForUser(
					userMediator.getUserId().longValue());
			ZeppaEventSingleton.getInstance().notifyObservers();
			EventTagSingleton.getInstance().removeEventTagsForUser(
					userMediator.getUserId().longValue());
			userMediator.removeRelationship(
					(ZeppaApplication) getApplication(), credential);
			ZeppaUserSingleton.getInstance().notifyObservers();
			onBackPressed();

			return true;

		case R.id.action_textmingler:
			userMediator.sendTextMessage(this);
			return true;

		case R.id.action_emailmingler:
			userMediator.sendEmail(this, null);
			return true;

		}

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
		case R.id.useractivity_mutualfriends:
			showMutualMinglers();
			break;

		}

	}

	@Override
	public void onMinglerRelationshipsLoaded() {
		// Ensure the mutual mingler text is visible if it werent before
		mutualMinglers.setVisibility(View.VISIBLE);
		// Set list of current mutual minglers and remove this user
		List<Long> minglingIds = userMediator.getMinglingWithIds();
		minglingIds.remove(userMediator.getUserId());
		List<DefaultUserInfoMediator> mutualMingerMediators = ZeppaUserSingleton
				.getInstance().getMinglersFrom(minglingIds);

		// Set the text of the mutual minglers
		if (mutualMingerMediators.isEmpty()) {
			mutualMinglers.setText("No Mutual Mingers");
		} else if (mutualMingerMediators.size() == 1) {
			mutualMinglers.setText("You both mingle with "
					+ mutualMingerMediators.get(0).getDisplayName());
		} else {
			mutualMinglers.setText(mutualMingerMediators.size()
					+ " Mutual Minglers");
		}

		// not longer updating mingler relationships
		isUpdatingMinglerRelationships = false;
		onEntityTypeUpdateFinished();
	}

	@Override
	public void onErrorLoadingMinglerRelationships() {
		Toast.makeText(this, "Error Loading Minglers", Toast.LENGTH_SHORT)
				.show();
		mutualMinglers.setVisibility(View.GONE);

		isUpdatingMinglerRelationships = false;
		onEntityTypeUpdateFinished();

	}

	@Override
	public void onTagsLoaded() {
		tagAdapter.notifyDataSetChanged();
		tagAdapter.drawTags();
		isUpdatingMinglerTags = false;
		onEntityTypeUpdateFinished();

	}

	@Override
	public void onRefreshStarted(View view) {
		fetchUpdatedInfo();

	}

	@Override
	public void onErrorLoadingTags() {
		Toast.makeText(this, "Error loading tags", Toast.LENGTH_SHORT).show();
		isUpdatingMinglerTags = false;
		onEntityTypeUpdateFinished();
	}

	@Override
	public void onZeppaEventsLoaded() {
		friendEventsAdapter.notifyDataSetChanged();
		try {
			friendEventsAdapter.drawEvents();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		isUpdatingMinglerEvents = false;
		onEntityTypeUpdateFinished();

	}

	public long getMinglerId(){
		try {
			return userMediator.getUserId().longValue();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	private void onEntityTypeUpdateFinished() {

		if (isUpdatingMinglerEvents || isUpdatingMinglerRelationships
				|| isUpdatingMinglerTags) {
			return;
		} else {
			pullToRefreshLayout.setRefreshing(false);
			isUpdating = false;
		}

	}

	private void fetchUpdatedInfo() {

		if (!isConnected() || isUpdating)
			return;

		pullToRefreshLayout.setRefreshing(true);

		isUpdatingMinglerRelationships = true;
		ThreadManager.execute(new FetchMutualMingersRunnable(
				(ZeppaApplication) getApplication(),
				getGoogleAccountCredential(), ZeppaUserSingleton.getInstance()
						.getUserId().longValue(), userMediator, this));

		isUpdatingMinglerTags = true;
		ThreadManager.execute(new FetchDefaultTagsForUserRunnable(
				(ZeppaApplication) getApplication(),
				getGoogleAccountCredential(), ZeppaUserSingleton.getInstance()
						.getUserId().longValue(), userMediator.getUserId()
						.longValue(), this));

		// Update Mingler Events
		isUpdatingMinglerEvents = true;
		ThreadManager.execute(new FetchEventsForMinglerRunnable(
				(ZeppaApplication) getApplication(),
				getGoogleAccountCredential(), ZeppaUserSingleton.getInstance()
						.getUserId().longValue(), userMediator.getUserId()
						.longValue()));

	}

	/**
	 * populates fields with the most up to date info available
	 * 
	 */
	private void setUserInfo() {

		userName.setText(userMediator.getDisplayName());
		userMediator.setImageWhenReady(userImage);

		try {
			String phoneNumberText = userMediator.getPrimaryPhoneNumber();
			userPhoneNumber.setText(phoneNumberText);

		} catch (NullPointerException e) {
			userPhoneNumber.setVisibility(View.GONE);

		}

		userGmail.setText(userMediator.getGmail());

	}

	private void showMutualMinglers() {
		List<Long> minglingIds = userMediator.getMinglingWithIds();
		List<DefaultUserInfoMediator> mutualMingerMediators = ZeppaUserSingleton
				.getInstance().getMinglersFrom(minglingIds);
		if (mutualMingerMediators.isEmpty()) {
			Toast.makeText(this, "No mutual minglers", Toast.LENGTH_SHORT)
					.show();
		} else {
			MinglerListAdapter mutualMinglerListAdapter = new MinglerListAdapter(
					this, mutualMingerMediators);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			if (mutualMingerMediators.size() == 1) {
				builder.setTitle("1 mutual mingler");
			} else {
				builder.setTitle(mutualMingerMediators.size()
						+ " mutual minglers");
			}
			ListView view = new ListView(this);
			view.setAdapter(mutualMinglerListAdapter);
			view.setOnItemClickListener(mutualMinglerListAdapter);
			builder.setView(view);
			builder.setNegativeButton("Dismiss",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

						}
					});
			builder.show();
		}

	}

}
