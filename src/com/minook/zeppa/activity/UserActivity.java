package com.minook.zeppa.activity;

import android.app.ActionBar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.adapter.eventlistadapter.FriendEventsAdapter;
import com.minook.zeppa.adapter.tagadapter.FriendTagAdapter;
import com.minook.zeppa.mediator.DefaultUserInfoMediator;
import com.minook.zeppa.singleton.EventTagSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

public class UserActivity extends AuthenticatedFragmentActivity implements
		OnClickListener {

	private static final String TAG = "UserActivity";

	private ImageView userImage;
	private TextView userName;
	private TextView userPhoneNumber;
	private TextView userGmail;
	private TextView mutualFriends;

	private LinearLayout eventHolder;
	private View eventLoaderView;
	private LinearLayout tagHolder;
	private View tagLoaderView;

	private FriendTagAdapter tagAdapter;
	private FriendEventsAdapter friendEventsAdapter;

	private DefaultUserInfoMediator userMediator;

	/*
	 * ------------- Override Methods --------------
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_user);

		long userId = getIntent().getExtras().getLong(
				Constants.INTENT_ZEPPA_USER_ID);

		userMediator = ZeppaUserSingleton.getInstance().getUserFor(userId);

		// find UI Elements and hold
		userImage = (ImageView) findViewById(R.id.useractivity_image);
		userName = (TextView) findViewById(R.id.useractivity_username);
		userPhoneNumber = (TextView) findViewById(R.id.useractivity_phonenumber);
		userGmail = (TextView) findViewById(R.id.useractivity_email);
		mutualFriends = (TextView) findViewById(R.id.useractivity_mutualfriends);
		eventHolder = (LinearLayout) findViewById(R.id.useractivity_eventholder);
		tagHolder = (LinearLayout) findViewById(R.id.useractivity_tagholder);

		// set Listeners
		mutualFriends.setOnClickListener(this);
		userPhoneNumber.setOnClickListener(this);
		userGmail.setOnClickListener(this);

		// action bar
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);

		tagAdapter = new FriendTagAdapter(this, tagHolder,
				userMediator.getUserId());
		friendEventsAdapter = new FriendEventsAdapter(userMediator, this,
				eventHolder);

	}

	@Override
	protected void onStart() {
		super.onStart();

		userMediator.setContext(this);
		setUserInfo();
	}

	@Override
	protected void onResume() {
		super.onResume();
		tagAdapter.drawTags();
		friendEventsAdapter.drawEvents();
	}
	
	
	@Override
	public void onConnected(Bundle connectionHint) {
		super.onConnected(connectionHint);
		updateEventTags();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
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
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		}

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

		// TODO: figure out mutual minglers
		mutualFriends.setText("XXX Mutual Minglers");
	}

	private void updateEventTags() {

		new AsyncTask<Object, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Object... params) {

				return Boolean.valueOf(EventTagSingleton.getInstance()
						.fetchEventTagsForUserWithBlocking(userMediator.getUserId(),
								getGoogleAccountCredential()));
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);

				if (result) {
					tagAdapter.notifyDataSetChanged();
				}

				Log.d(TAG, "Did update all tags");

			}

		}.execute();

	}

}
