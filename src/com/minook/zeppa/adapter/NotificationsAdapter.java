package com.minook.zeppa.adapter;

import java.io.IOException;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.activity.AbstractEventViewActivity;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.activity.StartMinglingActivity;
import com.minook.zeppa.activity.UserActivity;
import com.minook.zeppa.mediator.DefaultUserInfoMediator;
import com.minook.zeppa.observer.OnLoadListener;
import com.minook.zeppa.singleton.NotificationSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.utils.Utils;
import com.minook.zeppa.zeppanotificationendpoint.model.ZeppaNotification;

public class NotificationsAdapter extends BaseAdapter implements
		OnItemClickListener, OnLoadListener {

	private AuthenticatedFragmentActivity activity;
	private List<ZeppaNotification> notifications;
	private ListView notificationList;
	private View loaderView;

	public NotificationsAdapter(AuthenticatedFragmentActivity activity,
			ListView notificationList) {

		this.activity = activity;
		this.notificationList = notificationList;
		if (didLoadInitial()) {
			notifications = NotificationSingleton.getInstance()
					.getNotifications();
		} else {
			NotificationSingleton.getInstance().registerOnLoadListener(this);
			loaderView = (View) activity.getLayoutInflater().inflate(
					R.layout.view_loaderview, null, false);

			ProgressBar progress = (ProgressBar) loaderView
					.findViewById(R.id.loaderview_progressbar);
			progress.setIndeterminate(true);
			TextView text = (TextView) loaderView
					.findViewById(R.id.loaderview_text);
			text.setText("Loading...");

			notificationList.addHeaderView(loaderView);
		}

	}

	// public NotificationsAdapter(AuthenticatedFragmentActivity activity) {
	// this.activity = activity;
	// notifications = NotificationSingleton.getInstance().getNotifications();
	// }

	@Override
	public int getCount() {
		if (notifications == null) {
			return 0;
		} else {
			return notifications.size();
		}
	}

	@Override
	public ZeppaNotification getItem(int position) {
		return notifications.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getKey().getId().longValue();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ZeppaNotification notification = getItem(position);

		if (convertView != null) {
			convertView = activity.getLayoutInflater().inflate(
					R.layout.view_notification_item, parent, false);

			ImageView userImage = (ImageView) convertView
					.findViewById(R.id.notificationitem_userimage);
			TextView text = (TextView) convertView
					.findViewById(R.id.notificationitem_text);
			TextView date = (TextView) convertView
					.findViewById(R.id.notificationitem_date);

			setImageInAsync(userImage, notification.getSenderId());
			text.setText(notification.getExtraMessage());
			date.setText(Utils.getDisplayDateString(notification.getCreated()
					.longValue()));
		}

		return convertView;
	}

	@Override
	public void notifyDataSetChanged() {
		notifications = NotificationSingleton.getInstance().getNotifications();
		super.notifyDataSetChanged();
	}

	@Override
	public boolean didLoadInitial() {
		return NotificationSingleton.getInstance().hasLoadedInitial();
	}

	@Override
	public void onFinishLoad() {

		if (loaderView != null && loaderView.getVisibility() == View.VISIBLE) {
			loaderView.setVisibility(View.GONE);
		}

		notifyDataSetChanged();

	}

	private GoogleAccountCredential getCredential() throws IOException {
		return activity.getGoogleAccountCredential();
	}

	private void setImageInAsync(ImageView imageView, Long userId) {

		DefaultUserInfoMediator infoManager = ZeppaUserSingleton.getInstance()
				.getUserFor(userId);
		if (infoManager != null) {
			infoManager.setImageWhenReady(imageView);
		} else {
			// TODO: load user and set image.
		}
	}

	public void hasSeenUnseenNotifications() {

		new AsyncTask<Object, Void, Void>() {

			@Override
			protected Void doInBackground(Object... params) {

				for (ZeppaNotification notification : notifications) {
					if (!notification.getHasSeen()) {
						notification.setHasSeen(Boolean.TRUE);

						try {
							NotificationSingleton.getInstance()
									.markNotificationAsSeen(notification,
											getCredential());
						} catch (IOException e) {
							e.printStackTrace();
						}

					}
				}
				return null;
			}

		}.execute();

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		ZeppaNotification notification = getItem(position);

		String type = notification.getType();
		Intent intent = null;
		switch (notificationTypeOrder(type)) {

		case 0: // FriendRequest;
			intent = new Intent(activity, StartMinglingActivity.class);
			activity.overridePendingTransition(R.anim.slide_up_in, R.anim.hold);
			activity.startActivity(intent);
			break;
		case 1: // FriendAccepted
			intent = new Intent(activity, UserActivity.class);
			intent.putExtra(Constants.INTENT_ZEPPA_USER_ID,
					notification.getSenderId());
			activity.startActivity(intent);

			activity.overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);
			break;
		case 2: // Event Reccomendation
			intent = new Intent(activity, AbstractEventViewActivity.class);
			intent.putExtra(Constants.INTENT_ZEPPA_EVENT_ID,
					notification.getEventId());
			activity.startActivity(intent);
			activity.overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);
			break;
		case 3: // Direct Invite, Implement Later
			intent = new Intent(activity, AbstractEventViewActivity.class);
			intent.putExtra(Constants.INTENT_ZEPPA_EVENT_ID,
					notification.getEventId());
			activity.startActivity(intent);
			activity.overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);

			break;
		case 4: // Post Comment
			intent = new Intent(activity, AbstractEventViewActivity.class);
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
			intent = new Intent(activity, AbstractEventViewActivity.class);
			intent.putExtra(Constants.INTENT_ZEPPA_EVENT_ID,
					notification.getEventId());
			activity.startActivity(intent);

			activity.overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);
			break;
		case 7: // Friend Joined Event
			intent = new Intent(activity, AbstractEventViewActivity.class);
			intent.putExtra(Constants.INTENT_ZEPPA_EVENT_ID,
					notification.getEventId());
			activity.startActivity(intent);
			activity.overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);
			break;
		case 8: // User Left Your Event
			intent = new Intent(activity, AbstractEventViewActivity.class);
			intent.putExtra(Constants.INTENT_ZEPPA_EVENT_ID,
					notification.getEventId());
			activity.startActivity(intent);
			activity.overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);
			break;
		// case 9: // Someone Wants to find a time, Implement Later
		//
		// break;
		// case 10: // Time was found
		//
		// break;

		case 9: // Event Reposted
			intent = new Intent(activity, AbstractEventViewActivity.class);
			intent.putExtra(Constants.INTENT_ZEPPA_EVENT_ID,
					notification.getEventId());
			activity.startActivity(intent);
			activity.overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);
			break;
		}

	}

	private int notificationTypeOrder(String type) {
		if (type.equals("FRIEND_REQUEST")) {
			return 0;
		} else if (type.equals("FRIEND_ACCEPTED")) {
			return 1;
		} else if (type.equals("EVENT_RECCOMENDATION")) {
			return 2;
		} else if (type.equals("DIRECT_INVITE")) {
			return 3;
		} else if (type.equals("COMMENT_ON_POST")) {
			return 4;
		} else if (type.equals("EVENT_CANCELED")) {
			return 5;
		} else if (type.equals("EVENT_UPDATED")) {
			return 6;
		} else if (type.equals("USER_JOINED")) {
			return 7;
		} else if (type.equals("USER_LEAVING")) {
			return 8;
		} else if (type.equals("EVENT_REPOSTED")) {
			return 9;
		} else {
			return -1;
		}
	}

}
