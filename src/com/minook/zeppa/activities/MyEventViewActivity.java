package com.minook.zeppa.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.minook.zeppa.R;
import com.minook.zeppa.adapters.RepostsListAdapter;
import com.minook.zeppa.adapters.tagadapter.MyEventTagAdapter;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUser;

public class MyEventViewActivity extends EventViewActivity{

	

	
	
	@Override
	protected boolean isMyEvent(){
		return true;
	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.menu_event_hosted, menu);
		
		return true;
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;

			/*
			 * Host Controls
			 */
		case R.id.menu_event_cancel:
			raiseCancelDialog();
			break;
		case R.id.menu_event_seereposts:
			raiseSeeRepostsDialog();
			break;

		case R.id.menu_event_massemail:
			massEmailJoined();
			break;

		case R.id.menu_event_masstext:
			massTextJoined();
			break;

		case R.id.menu_event_directions:
			String location = (zeppaEvent.getExactLocation() != null ? zeppaEvent
					.getExactLocation() : zeppaEvent.getShortLocation());
			Intent toDirections = new Intent(
					android.content.Intent.ACTION_VIEW,
					Uri.parse("http://maps.google.com/maps?f=d&daddr="
							+ location));
			startActivity(toDirections);
			return true;

		}

		return false;

	}
	
	
	
	
	@Override
	protected void handleConflictIndicator(Long userId, boolean isOwnEvent,
			ZeppaEventSingleton eventSingleton) {
//		super.handleConflictIndicator(userId, isOwnEvent, eventSingleton);
		conflictIndicator.setImageDrawable(getResources()
				.getDrawable(R.drawable.small_circle_blue));
	
	}



	@Override
	protected void setTags() {
//		super.setTags();
		LinearLayout tagholder = (LinearLayout) findViewById(R.id.eventactivity_tagholder);
		tagAdapter = new MyEventTagAdapter(this, tagholder, zeppaEvent);
		tagAdapter.drawTags();
	
	}
	
	
	/*
	 * Action Methods
	 */
	private void raiseCancelDialog() {
		ZeppaUserSingleton userSingleton = ZeppaUserSingleton.getInstance();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Cancel " + zeppaEvent.getTitle() + "?");
		StringBuilder messageBuilder = new StringBuilder();

		messageBuilder.append("Are you sure you want to delete ");
		messageBuilder.append(zeppaEvent.getTitle());
		messageBuilder.append("? This cannot be undone..");

		if (zeppaEvent.getUsersGoingIds() != null) {
			int joinCount = zeppaEvent.getUsersGoingIds().size();
			if (joinCount > 1) {
				messageBuilder.append(" " + joinCount
						+ " friends will be notified");
			} else if (joinCount == 1) {
				ZeppaUser loneAttender = userSingleton.getUserById(zeppaEvent
						.getUsersGoingIds().get(0));
				messageBuilder.append(" " + loneAttender.getDisplayName()
						+ " will be notified");
			}

		}
		builder.setMessage(messageBuilder.toString());

		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == DialogInterface.BUTTON_POSITIVE) {

					ZeppaEvent[] params = { zeppaEvent };
					new AsyncTask<ZeppaEvent, Void, Boolean>() {

						Context context;

						@Override
						protected Boolean doInBackground(ZeppaEvent... params) {

							ZeppaEvent event = params[0];
							ZeppaEventSingleton eventSingleton = ZeppaEventSingleton
									.getInstance();
							return eventSingleton.deleteZeppaEvent(
									getApplicationContext(), getCredential(),
									event, getContentResolver());
						}

						@Override
						protected void onPostExecute(Boolean result) {
							super.onPostExecute(result);
							if (result) {
								Toast.makeText(context,
										"Deleted " + zeppaEvent.getTitle(),
										Toast.LENGTH_SHORT).show();

							} else {
								Toast.makeText(context,
										"Error Deleting " + context,
										Toast.LENGTH_SHORT).show();
							}
						}

					}.execute(params);
					onBackPressed();
				}
				dialog.dismiss();

			}
		};

		builder.setPositiveButton("Cancel Event", listener);
		builder.setNegativeButton("Nevermind", listener);

		builder.show();
	}

	private void raiseSeeRepostsDialog() {
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
	}

	private void massEmailJoined() {

	}

	private void massTextJoined() {

	}

	
}
