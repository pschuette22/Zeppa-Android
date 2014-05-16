package com.minook.zeppa.adapters;

import java.util.Date;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.activities.EventViewActivity;
import com.minook.zeppa.activities.MainActivity;
import com.minook.zeppa.activities.NewFriendsActivity;
import com.minook.zeppa.activities.UserActivity;
import com.minook.zeppa.singleton.NotificationSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppanotificationendpoint.model.ZeppaNotification;
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUser;

public class NotificationsAdapter extends BaseAdapter implements
		OnItemClickListener {

	private MainActivity activity;

	public NotificationsAdapter(MainActivity activity) {
		this.activity = activity;
	}

	@Override
	public int getCount() {
		return NotificationSingleton.getInstance().getNotifications().size();
	}

	@Override
	public ZeppaNotification getItem(int position) {
		return NotificationSingleton.getInstance().getNotifications()
				.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ZeppaNotification notification = getItem(position);
		if (convertView == null) {
			convertView = activity.getLayoutInflater().inflate(
					R.layout.view_notification_item, null, false);
		}

		ImageView userImage = (ImageView) convertView
				.findViewById(R.id.notificationitem_userimage);
		TextView text = (TextView) convertView
				.findViewById(R.id.notificationitem_text);
		TextView date = (TextView) convertView
				.findViewById(R.id.notificationitem_date);

		setImageInAsync(userImage, notification.getFromUserId());
		text.setText(notification.getExtraMessage());
		date.setText(Constants.getDisplayDateString(notification.getSentDate()));

		return convertView;
	}

	private GoogleAccountCredential getCredential() {
		return ((ZeppaApplication) activity.getApplication())
				.getGoogleAccountCredential();
	}

	private void setImageInAsync(final ImageView imageView, final Long userId) {
		new AsyncTask<Void, Void, Drawable>() {

			@Override
			protected Drawable doInBackground(Void... params) {
				ZeppaUserSingleton userSingleton = ZeppaUserSingleton
						.getInstance();
				ZeppaUser user = userSingleton.getOrFetchZeppaUser(userId,
						getCredential());
				return userSingleton.getUserImage(user);
			}

			@Override
			protected void onPostExecute(Drawable result) {
				super.onPostExecute(result);
				if (result != null) {
					imageView.setImageDrawable(result);
				}
			}

		}.execute();

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		ZeppaNotification notification = getItem(position);
		int type = notification.getNotificationOrdinal();
		Intent intent = null;
		switch (type) {
		
		case 0: // FriendRequest;
			intent = new Intent(activity, NewFriendsActivity.class);
			activity.overridePendingTransition(R.anim.slide_up_in, R.anim.hold);
			activity.startActivity(intent);
			break;
		case 1: // FriendAccepted
			intent = new Intent(activity, UserActivity.class);
			intent.putExtra(Constants.INTENT_ZEPPA_USER_ID,
					notification.getFromUserId());
			activity.startActivity(intent);

			activity.overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);
			break;
		case 2: // Event Reccomendation
			intent = new Intent(activity, EventViewActivity.class);
			intent.putExtra(Constants.INTENT_ZEPPA_EVENT_ID,
					notification.getEventId());
			activity.startActivity(intent);
			activity.overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);
			break;
		case 3: // Direct Invite, Implement Later
			intent = new Intent(activity, EventViewActivity.class);
			intent.putExtra(Constants.INTENT_ZEPPA_EVENT_ID,
					notification.getEventId());
			activity.startActivity(intent);
			activity.overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);

			break;
		case 4: // Post Comment
			intent = new Intent(activity, EventViewActivity.class);
			intent.putExtra(Constants.INTENT_ZEPPA_EVENT_ID,
					notification.getEventId());
			activity.startActivity(intent);
			activity.overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);
			break;

		case 5: // Event Canceled
			Toast toast = Toast.makeText(activity, "Event Was Canceled...",
					Toast.LENGTH_LONG);
			toast.show();
			// Perhaps send them to the calendar and show the opened time slot?
			break;

		case 6: // Event Updated
			intent = new Intent(activity, EventViewActivity.class);
			intent.putExtra(Constants.INTENT_ZEPPA_EVENT_ID,
					notification.getEventId());
			activity.startActivity(intent);

			activity.overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);
			break;
		case 7: // Friend Joined Event
			intent = new Intent(activity, EventViewActivity.class);
			intent.putExtra(Constants.INTENT_ZEPPA_EVENT_ID,
					notification.getEventId());
			activity.startActivity(intent);
			activity.overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);
			break;
		case 8: // User Left Your Event
			intent = new Intent(activity, EventViewActivity.class);
			intent.putExtra(Constants.INTENT_ZEPPA_EVENT_ID,
					notification.getEventId());
			activity.startActivity(intent);
			activity.overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);
			break;
		case 9: // Someone Wants to find a time, Implement Later

			break;
		case 10: // Time was found

			break;

		case 11: // Event Reposted
			intent = new Intent(activity, EventViewActivity.class);
			intent.putExtra(Constants.INTENT_ZEPPA_EVENT_ID,
					notification.getEventId());
			activity.startActivity(intent);
			activity.overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);
			break;
		}

	}

}
