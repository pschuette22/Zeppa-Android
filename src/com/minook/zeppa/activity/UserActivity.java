package com.minook.zeppa.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.adapter.eventlistadapter.FriendEventsAdapter;
import com.minook.zeppa.adapter.tagadapter.FriendTagAdapter;
import com.minook.zeppa.mediator.DefaultUserInfoMediator;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

public class UserActivity extends AuthenticatedFragmentActivity implements
		OnClickListener {

	private ImageView userImage;
	private TextView userName;
	private Button mutualFriendsButton;

	private LinearLayout eventHolder;
	private LinearLayout tagHolder;

	private FriendTagAdapter tagAdapter;
	private FriendEventsAdapter friendEventsAdapter;

	private DefaultUserInfoMediator userManager;

	/*
	 * ------------- Override Methods --------------
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_user);

		long userId = getIntent().getExtras().getLong(
				Constants.INTENT_ZEPPA_USER_ID);

		userManager = ZeppaUserSingleton.getInstance().getUserFor(userId);

		// find UI Elements and hold
		userImage = (ImageView) findViewById(R.id.useractivity_image);
		userName = (TextView) findViewById(R.id.useractivity_username);
		mutualFriendsButton = (Button) findViewById(R.id.useractivity_mutualfriends);
		eventHolder = (LinearLayout) findViewById(R.id.useractivity_eventholder);
		tagHolder = (LinearLayout) findViewById(R.id.useractivity_tagholder);
		
		// set Listeners
		mutualFriendsButton.setOnClickListener(this);

		// action bar
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);

		setUserInfo();
		
		tagAdapter = new FriendTagAdapter(this, tagHolder, userManager.getEventTagManagers());
		tagAdapter.drawTags();
		
		friendEventsAdapter = new FriendEventsAdapter(userManager, this, eventHolder);
		friendEventsAdapter.drawEvents();
	}

	@Override
	protected void onResume() {
		super.onResume();

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

	/*
	 * ---------------- Private Methods ------------------
	 */

	private void setUserInfo() {

		userName.setText(userManager.getDisplayName());

		userManager.setImageWhenReady(userImage);
		int mutualFriendCount = 0; // TODO: count relationships
		
		mutualFriendsButton.setText(mutualFriendCount + " Mutual Friends");

		LinearLayout tagHolder = (LinearLayout) findViewById(R.id.useractivity_tagholder);
		tagAdapter = new FriendTagAdapter(this, tagHolder, userManager.getEventTagManagers());
		tagAdapter.drawTags();
	}

	/*
	 * Called on refresh and when opened Will show what info the
	 * ((ZeppaApplication) getApplication()) is holding but still will check to
	 * see if anything is missing.
	 */

}
