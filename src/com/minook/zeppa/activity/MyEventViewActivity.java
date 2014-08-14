package com.minook.zeppa.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.R;
import com.minook.zeppa.adapter.RepostsListAdapter;
import com.minook.zeppa.adapter.tagadapter.MyTagAdapter;
import com.minook.zeppa.mediator.MyZeppaEventMediator;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;


/**
 *	This class is for displaying an event view activity for an event the host owns
 * 
 * @author DrunkWithFunk21
 */
public class MyEventViewActivity extends AbstractEventViewActivity {


	private MyZeppaEventMediator eventManager;
	private MyTagAdapter tagAdapter;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		eventManager = (MyZeppaEventMediator) ZeppaEventSingleton.getInstance().getEventById(eventId);
//		tagAdapter = new MyTagAdapter(this, tagHolder, eventManager.getTagManagers());
		
	}

	@Override
	protected void setTags() {
//		tagAdapter.setTagManagers(eventManager.getTagManagers());
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// super.onCreateOptionsMenu(menu);
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
			raiseRepostDialog();
			break;

		case R.id.menu_event_massemail:
			massEmailJoined();
			break;

		case R.id.menu_event_masstext:
			massTextJoined();
			break;

		case R.id.menu_event_directions:
//			String location = (zeppaEvent.getExactLocation() != null ? zeppaEvent
//					.getExactLocation() : zeppaEvent.getShortLocation());
//			Intent toDirections = new Intent(
//					android.content.Intent.ACTION_VIEW,
//					Uri.parse("http://maps.google.com/maps?f=d&daddr="
//							+ location));
//			startActivity(toDirections);
			return true;

		}

		return false;

	}


	/*
	 * Action Methods
	 */
	private void raiseCancelDialog() {
		ZeppaUserSingleton userSingleton = ZeppaUserSingleton.getInstance();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Cancel " + eventManager.getTitle() + "?");
		StringBuilder messageBuilder = new StringBuilder();

		messageBuilder.append("Are you sure you want to delete ");
		messageBuilder.append(eventManager.getTitle());
		messageBuilder.append("? This cannot be undone..");

//		if (zeppaEvent.getUsersGoingIds() != null) {
//			int joinCount = zeppaEvent.getUsersGoingIds().size();
//			if (joinCount > 1) {
//				messageBuilder.append(" " + joinCount
//						+ " friends will be notified");
//			} else if (joinCount == 1) {
//				ZeppaUser loneAttender = userSingleton.getUserById(zeppaEvent
//						.getUsersGoingIds().get(0));
//				messageBuilder.append(" " + loneAttender.getGivenName()
//						+ " will be notified");
//			}
//
//		}
//		builder.setMessage(messageBuilder.toString());
//
//		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				if (which == DialogInterface.BUTTON_POSITIVE) {
//
//					ZeppaEvent[] params = { zeppaEvent };
//					new AsyncTask<ZeppaEvent, Void, Boolean>() {
//
//						Context context;
//
//						@Override
//						protected Boolean doInBackground(ZeppaEvent... params) {
//
//							ZeppaEvent event = params[0];
//							ZeppaEventSingleton eventSingleton = ZeppaEventSingleton
//									.getInstance();
//							return eventSingleton.deleteZeppaEvent(
//									getApplicationContext(), getCredential(),
//									event, getContentResolver());
//						}
//
//						@Override
//						protected void onPostExecute(Boolean result) {
//							super.onPostExecute(result);
//							if (result) {
//								Toast.makeText(context,
//										"Deleted " + zeppaEvent.getTitle(),
//										Toast.LENGTH_SHORT).show();
//
//							} else {
//								Toast.makeText(context,
//										"Error Deleting " + context,
//										Toast.LENGTH_SHORT).show();
//							}
//						}
//
//					}.execute(params);
//					onBackPressed();
//				}
//				dialog.dismiss();
//
//			}
//		};
//
//		builder.setPositiveButton("Cancel Event", listener);
//		builder.setNegativeButton("Nevermind", listener);
//
//		builder.show();
	}

	private void viewReposts(){

		try {
		GoogleAccountCredential credential = getGoogleAccountCredential();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(eventManager.getTitle() + " reposts");

		RepostsListAdapter adapter = new RepostsListAdapter(this,
				credential, eventManager);

		builder.setAdapter(adapter, adapter);
		builder.setNeutralButton(R.string.dismiss, adapter);

		builder.show();
		} catch (NullPointerException e){
			// Credential is null
			Toast.makeText(this, "Error Occured", Toast.LENGTH_SHORT).show();
		}
		
		
	}
	
	protected void raiseRepostDialog() {

		

	}

	private void massEmailJoined() {

//		if (zeppaEvent.getUsersGoingIds() == null) {
//			Toast.makeText(this, "Nobody has joined yet", Toast.LENGTH_SHORT)
//					.show();
//		} else {
//			List<ZeppaUser> friendsGoing = getFriendsGoingList();
//			String[] emailArray = new String[friendsGoing.size()];
//			for (int i = 0; i < friendsGoing.size(); i++) {
//				ZeppaUser friend = friendsGoing.get(i);
//				emailArray[i] = friend.getEmail();
//			}
//
//			Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
//			emailIntent.putExtra(Intent.EXTRA_EMAIL, emailArray);
//			emailIntent.putExtra(Intent.EXTRA_SUBJECT, zeppaEvent.getTitle());
//			emailIntent.setType("message/rfc822");
//			
//			startActivity(emailIntent);
//
//		}
	}

	private void massTextJoined() {
//		if (zeppaEvent.getUsersGoingIds() == null) {
//			Toast.makeText(this, "Nobody has joined yet", Toast.LENGTH_SHORT)
//					.show();
//		} else {
//			
//			List<ZeppaUser> friendsGoing = getFriendsGoingList();
//			StringBuilder builder = new StringBuilder();
//			
//			for(ZeppaUser friend: friendsGoing){
//				if(friend.getPhoneNumber() != null){
//					builder.append(friend.getPhoneNumber());
//					builder.append(";");
//				}
//			}
//			
//			String numbers = builder.toString();
//			
//			if(numbers.isEmpty()){
//				Toast.makeText(this, "No Listed Numbers", Toast.LENGTH_SHORT).show();
//			} else {
//				numbers.substring(0, (numbers.length() - 2));
//				
//				Intent sendIntent = new Intent(Intent.ACTION_VIEW);         
//				sendIntent.putExtra(Intent.EXTRA_PHONE_NUMBER, numbers);
//				sendIntent.putExtra("sms_body", "Regarding " + zeppaEvent.getTitle() + ", ");
//				sendIntent.setType("vnd.android-dir/mms-sms"); 
//			
//				startActivity(sendIntent);
//			}
//			
//		}

	}

	@Override
	protected void setEventInfo() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setHostInfo() {
		// TODO Auto-generated method stub
		
	}

}
