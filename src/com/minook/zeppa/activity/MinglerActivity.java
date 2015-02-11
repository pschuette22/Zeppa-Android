package com.minook.zeppa.activity;

import java.util.List;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
		OnTagLoadListener, OnZeppaEventLoadListener {

	private static final String TAG = "UserActivity";

	private long userId;
	private ImageView userImage;
	private TextView userName;
	private TextView userPhoneNumber;
	private TextView userGmail;
	private TextView mutualMinglers;

	private LinearLayout eventHolder;
	private LinearLayout tagHolder;

	private MinglerTagAdapter tagAdapter;
	private MinglerEventsAdapter friendEventsAdapter;

	private DefaultUserInfoMediator userMediator;

	/*
	 * ------------- Override Methods --------------
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_mingler);

		userId = getIntent().getExtras()
				.getLong(Constants.INTENT_ZEPPA_USER_ID);

		// find UI Elements and hold
		userImage = (ImageView) findViewById(R.id.useractivity_image);
		userName = (TextView) findViewById(R.id.useractivity_username);
		userPhoneNumber = (TextView) findViewById(R.id.useractivity_phonenumber);
		userGmail = (TextView) findViewById(R.id.useractivity_email);
		mutualMinglers = (TextView) findViewById(R.id.useractivity_mutualfriends);
		eventHolder = (LinearLayout) findViewById(R.id.useractivity_eventholder);
		tagHolder = (LinearLayout) findViewById(R.id.useractivity_tagholder);

		// set Listeners
		mutualMinglers.setOnClickListener(this);
		userPhoneNumber.setOnClickListener(this);
		userGmail.setOnClickListener(this);

		// action bar
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(userMediator.getGivenName() + "'s Profile");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);

	}

	@Override
	protected void onStart() {
		super.onStart();

		userMediator = (DefaultUserInfoMediator) ZeppaUserSingleton
				.getInstance().getAbstractUserMediatorById(userId);

		tagAdapter = new MinglerTagAdapter(this, tagHolder,
				userMediator.getUserId(), null);
		friendEventsAdapter = new MinglerEventsAdapter(userMediator, this,
				eventHolder);

		setUserInfo();
		mutualMinglers.setText("Loading...");
	}

	@Override
	protected void onResume() {
		super.onResume();
		tagAdapter.drawTags();
		try {
			friendEventsAdapter.drawEvents();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		super.onConnected(connectionHint);
		// TODO: update UI
		ThreadManager.execute(new FetchMutualMingersRunnable(
				(ZeppaApplication) getApplication(),
				getGoogleAccountCredential(), ZeppaUserSingleton.getInstance()
						.getUserId().longValue(), userMediator, this));

		ThreadManager.execute(new FetchDefaultTagsForUserRunnable(
				(ZeppaApplication) getApplication(),
				getGoogleAccountCredential(), ZeppaUserSingleton.getInstance()
						.getUserId().longValue(), userMediator.getUserId()
						.longValue(), this));

		ThreadManager.execute(new FetchEventsForMinglerRunnable(
				(ZeppaApplication) getApplication(),
				getGoogleAccountCredential(), ZeppaUserSingleton.getInstance()
						.getUserId().longValue(), userMediator.getUserId()
						.longValue()));
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
		mutualMinglers.setVisibility(View.VISIBLE);
		List<Long> minglingIds = userMediator.getMinglingWithIds();
		minglingIds.remove(userMediator.getUserId());

		List<DefaultUserInfoMediator> mutualMingerMediators = ZeppaUserSingleton
				.getInstance().getMinglersFrom(minglingIds);
		if (mutualMingerMediators.isEmpty()) {
			mutualMinglers.setText("No Mutual Mingers");
		} else if (mutualMingerMediators.size() == 1) {
			mutualMinglers.setText("You both mingle with "
					+ mutualMingerMediators.get(0).getDisplayName());
		} else {
			mutualMinglers.setText(mutualMingerMediators.size()
					+ " Mutual Minglers");
		}
	}

	@Override
	public void onErrorLoadingMinglerRelationships() {
		Toast.makeText(this, "Error Loading Minglers", Toast.LENGTH_SHORT)
				.show();
		mutualMinglers.setVisibility(View.GONE);

	}

	@Override
	public void onTagsLoaded() {
		tagAdapter.notifyDataSetChanged();
		tagAdapter.drawTags();

	}

	@Override
	public void onErrorLoadingTags() {
		Toast.makeText(this, "Error loading tags", Toast.LENGTH_SHORT).show();

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

	@Override
	public void onZeppaEventsLoaded() {
		friendEventsAdapter.notifyDataSetChanged();
		try {
			friendEventsAdapter.drawEvents();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
