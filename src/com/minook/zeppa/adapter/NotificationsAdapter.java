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
import android.widget.TextView;
import android.widget.Toast;

import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.Utils;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.activity.AbstractEventViewActivity;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.activity.DefaultEventViewActivity;
import com.minook.zeppa.activity.MinglerActivity;
import com.minook.zeppa.activity.MyEventViewActivity;
import com.minook.zeppa.activity.StartMinglingActivity;
import com.minook.zeppa.mediator.AbstractZeppaUserMediator;
import com.minook.zeppa.runnable.RemoveNotificationRunnable;
import com.minook.zeppa.runnable.ThreadManager;
import com.minook.zeppa.singleton.NotificationSingleton;
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
		ZeppaNotification notification = getItem(position);

		AbstractZeppaUserMediator senderMediator = ZeppaUserSingleton
				.getInstance().getAbstractUserMediatorById(
						notification.getSenderId().longValue());

		if (senderMediator == null) {
			Log.wtf("TAG", "No User Found for received Notification");
		}

		if (convertView == null) {
			convertView = activity.getLayoutInflater().inflate(
					R.layout.view_notification_item, parent, false);
		}

		ImageView userImage = (ImageView) convertView
				.findViewById(R.id.notificationitem_userimage);
		TextView text = (TextView) convertView
				.findViewById(R.id.notificationitem_text);
		TextView date = (TextView) convertView
				.findViewById(R.id.notificationitem_date);

		senderMediator.setImageWhenReady(userImage);
		text.setText(NotificationSingleton.getInstance()
				.getNotificationMessage(notification));

		date.setText(Utils.getDisplayDateString(notification.getCreated()
				.longValue()));

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
			intent = new Intent(activity, AbstractEventViewActivity.class);
			intent.putExtra(Constants.INTENT_ZEPPA_EVENT_ID,
					notification.getEventId());
			activity.startActivity(intent);
			activity.overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);
			break;

		case 5: // Event Canceled
			// TODO: delete notification and notify Dataset changed
			Toast toast = Toast.makeText(activity, "Event Was Canceled...",
					Toast.LENGTH_LONG);
			toast.show();
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
