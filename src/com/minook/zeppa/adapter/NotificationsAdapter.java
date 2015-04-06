package com.minook.zeppa.adapter;

import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.Utils;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.activity.DefaultEventViewActivity;
import com.minook.zeppa.activity.MinglerActivity;
import com.minook.zeppa.activity.MyEventViewActivity;
import com.minook.zeppa.activity.StartMinglingActivity;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator;
import com.minook.zeppa.mediator.AbstractZeppaUserMediator;
import com.minook.zeppa.mediator.MyZeppaEventMediator;
import com.minook.zeppa.runnable.RemoveNotificationRunnable;
import com.minook.zeppa.runnable.ThreadManager;
import com.minook.zeppa.runnable.UpdateNotificationRunnable;
import com.minook.zeppa.singleton.NotificationSingleton;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppanotificationendpoint.model.ZeppaNotification;

public class NotificationsAdapter extends BaseAdapter implements
		OnItemClickListener {

	private AuthenticatedFragmentActivity activity;
	private List<ZeppaNotification> notifications;

	public NotificationsAdapter(AuthenticatedFragmentActivity activity) {

		this.activity = activity;
		notifications = NotificationSingleton.getInstance().getNotifications();
		Collections.sort(notifications, Utils.NOTIFICAITON_COMPARATOR);

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

		if (convertView == null) {
			convertView = activity.getLayoutInflater().inflate(
					R.layout.view_notification_item, parent, false);
		}

		ZeppaNotification notification;
		try {
			notification = getItem(position);

		} catch (Exception e) {
			convertView.setVisibility(View.GONE);
			return convertView;
		}

		AbstractZeppaUserMediator senderMediator = ZeppaUserSingleton
				.getInstance().getAbstractUserMediatorById(
						notification.getSenderId().longValue());

		if (senderMediator == null) {
			Log.wtf("TAG", "No User Found for received Notification");
			convertView.setVisibility(View.GONE);
		} else {
			convertView.setVisibility(View.VISIBLE);

			RelativeLayout background = (RelativeLayout) convertView
					.findViewById(R.id.notificationitem_background);
			if (notification.getHasSeen() != null && notification.getHasSeen()) {
				background
						.setBackgroundResource(R.drawable.background_notification_seen);
			} else {
				background
						.setBackgroundResource(R.drawable.background_notification_unseen);
			}

			ImageView userImage = (ImageView) convertView
					.findViewById(R.id.notificationitem_userimage);
			TextView text = (TextView) convertView
					.findViewById(R.id.notificationitem_text);
			TextView date = (TextView) convertView
					.findViewById(R.id.notificationitem_date);

			senderMediator.setImageWhenReady(userImage);
			try {
				text.setText(NotificationSingleton.getInstance()
						.getNotificationMessage(notification));

			} catch (StringIndexOutOfBoundsException e) {
				text.setText("Error Occured");
			}
			try {
				date.setText(Utils.getDisplayDateString(notification
						.getCreated().longValue()));
			} catch (StringIndexOutOfBoundsException e) {
				date.setText("Error Occured");
			}

		}

		return convertView;
	}

	@Override
	public void notifyDataSetChanged() {

		notifications = NotificationSingleton.getInstance().getNotifications();
		Collections.sort(notifications, Utils.NOTIFICAITON_COMPARATOR);

		super.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		ZeppaNotification notification = getItem(position);

		notification.setHasSeen(Boolean.TRUE);
		UpdateNotificationRunnable updateRunnable = new UpdateNotificationRunnable(
				(ZeppaApplication) activity.getApplication(),
				activity.getGoogleAccountCredential(), notification);
		ThreadManager.execute(updateRunnable);

		Intent intent = null;
		switch (NotificationSingleton.getInstance().getNotificationTypeOrder(
				notification)) {

		case 0: // Mingle Request;
			intent = new Intent(activity, StartMinglingActivity.class);
			activity.overridePendingTransition(R.anim.slide_up_in, R.anim.hold);
			activity.startActivity(intent);
			ThreadManager.execute(new RemoveNotificationRunnable(
					(ZeppaApplication) activity.getApplication(), activity
							.getGoogleAccountCredential(), notification.getId()
							.longValue()));

			break;
		case 1: // Mingle Accepted
			intent = new Intent(activity, MinglerActivity.class);
			intent.putExtra(Constants.INTENT_ZEPPA_USER_ID,
					notification.getSenderId());
			activity.startActivity(intent);

			activity.overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);
			ThreadManager.execute(new RemoveNotificationRunnable(
					(ZeppaApplication) activity.getApplication(), activity
							.getGoogleAccountCredential(), notification.getId()
							.longValue()));

			break;
		case 2: // Event Recommendation
			intent = new Intent(activity, DefaultEventViewActivity.class);
			intent.putExtra(Constants.INTENT_ZEPPA_EVENT_ID,
					notification.getEventId());
			activity.startActivity(intent);
			activity.overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);
			break;
		case 3: // Direct Invite, Implement Later
			intent = new Intent(activity, DefaultEventViewActivity.class);
			intent.putExtra(Constants.INTENT_ZEPPA_EVENT_ID,
					notification.getEventId());
			activity.startActivity(intent);
			activity.overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);

			break;
		case 4: // Post Comment
			// TODO: Determine if this is my event or another
			AbstractZeppaEventMediator mediator = ZeppaEventSingleton
					.getInstance().getEventById(
							notification.getEventId().longValue());

			intent = new Intent(
					activity,
					(mediator instanceof MyZeppaEventMediator) ? MyEventViewActivity.class
							: DefaultEventViewActivity.class);

			intent.putExtra(Constants.INTENT_ZEPPA_EVENT_ID,
					notification.getEventId());
			activity.startActivity(intent);
			activity.overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);
			break;

		case 5: // Event Canceled
			// TODO: delete notification and notify Dataset changed
			// Perhaps send them to the calendar and show the opened time slot?
			break;

		case 6: // Event Updated (Unimplemented for now)
			intent = new Intent(activity, DefaultEventViewActivity.class);
			intent.putExtra(Constants.INTENT_ZEPPA_EVENT_ID,
					notification.getEventId());
			activity.startActivity(intent);

			activity.overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);
			break;
		case 7: // Friend Joined Event
			intent = new Intent(activity, MyEventViewActivity.class);
			intent.putExtra(Constants.INTENT_ZEPPA_EVENT_ID,
					notification.getEventId());
			activity.startActivity(intent);
			activity.overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);
			break;
		case 8: // User Left Event
			intent = new Intent(activity, MyEventViewActivity.class);
			intent.putExtra(Constants.INTENT_ZEPPA_EVENT_ID,
					notification.getEventId());
			activity.startActivity(intent);
			activity.overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);
			break;

		}

	}

}
