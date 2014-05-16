package com.minook.zeppa.activities;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.adapters.eventadapter.EventListAdapter;
import com.minook.zeppa.adapters.tagadapter.FriendTagAdapter;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUser;

public class UserActivity extends AuthenticatedFragmentActivity implements
		OnClickListener {

	private ZeppaUser zeppaUser;

	private ImageView userImage;
	private TextView userName;
	private Button mutualFriendsButton;

	private LinearLayout eventHolder;

	private FriendTagAdapter tagAdapter;
	private EventListAdapter eventAdapter;

	/*
	 * ------------- Override Methods --------------
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_user);

		long userId = getIntent().getExtras().getLong(
				Constants.INTENT_ZEPPA_USER_ID);

		zeppaUser = ZeppaUserSingleton.getInstance().getUserById(userId);
		if (zeppaUser == null) {
			loadUserInAsync(userId);
		} else {
			setUserInfo();
		}

		// find UI Elements and hold
		userImage = (ImageView) findViewById(R.id.useractivity_image);
		userName = (TextView) findViewById(R.id.useractivity_username);
		mutualFriendsButton = (Button) findViewById(R.id.useractivity_mutualfriends);
		eventHolder = (LinearLayout) findViewById(R.id.useractivity_eventholder);

		// set Listeners
		mutualFriendsButton.setOnClickListener(this);
		

		// action bar
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);


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

	private Long getUserId() {
		return ZeppaUserSingleton.getInstance().getUserId();
	}

	private void loadUserInAsync(long userId) {

		ProgressDialog loadDialog = new ProgressDialog(this);
		loadDialog.setTitle("Loading User");
		loadDialog.setIndeterminate(true);
		loadDialog.setCanceledOnTouchOutside(false);
		loadDialog.setButton(Dialog.BUTTON_NEGATIVE, getResources().getString(R.string.cancel), new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}

			
		});
		
		
		Object[] params = {userId, loadDialog};
		
		final AsyncTask<Object, Void, ZeppaUser> fetchTask = new AsyncTask<Object, Void, ZeppaUser>(){

			private ProgressDialog loadDialog;
			@Override
			protected ZeppaUser doInBackground(Object... params) {
				long userId = (long) params[0];
				return ZeppaUserSingleton.getInstance().getOrFetchZeppaUser(Long.valueOf(userId), getCredential());
			}

			@Override
			protected void onPostExecute(ZeppaUser result) {
				super.onPostExecute(result);
				if(loadDialog.isShowing())
					loadDialog.dismiss();
				if(result != null){
					zeppaUser = result;
					setUserInfo();
					
				} else {
					Toast.makeText(getApplicationContext(), "Error occured", Toast.LENGTH_SHORT).show();
					onBackPressed();
				}
			}
			
		};
		
		fetchTask.execute(params);
		loadDialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				fetchTask.cancel(true);
				Toast.makeText(getBaseContext(), "Canceled Operation", Toast.LENGTH_SHORT).show();
				onBackPressed();
			}
		});
		
		loadDialog.show();
		
	}

	private void setUserInfo() {

		final ZeppaUserSingleton userSingleton = ZeppaUserSingleton
				.getInstance();
		new AsyncTask<Void, Void, Drawable>() {

			@Override
			protected Drawable doInBackground(Void... params) {
				return userSingleton.getUserImage(zeppaUser);
			}

			@Override
			protected void onPostExecute(Drawable result) {
				super.onPostExecute(result);
				if (result != null) {
					userImage.setImageDrawable(result);
				} else {
					Toast.makeText(getApplicationContext(),
							"Error Loaded Picture", Toast.LENGTH_SHORT).show();
				}
			}

		}.execute();

		userName.setText(zeppaUser.getDisplayName());

		int mutualFriendCount = userSingleton.getFriendsFrom(
				zeppaUser.getContactIds()).size();
		mutualFriendsButton.setText(mutualFriendCount + " Mutual Friends");
	
		LinearLayout tagHolder = (LinearLayout) findViewById(R.id.useractivity_tagholder);
		tagAdapter = new FriendTagAdapter(this, tagHolder, zeppaUser);
		tagAdapter.drawTags();
	}

	/*
	 * Called on refresh and when opened Will show what info the
	 * ((ZeppaApplication) getApplication()) is holding but still will check to
	 * see if anything is missing.
	 */

}
