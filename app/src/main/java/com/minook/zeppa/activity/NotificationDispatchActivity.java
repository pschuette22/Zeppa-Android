package com.minook.zeppa.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.appspot.zeppa_cloud_1821.zeppanotificationendpoint.model.ZeppaNotification;
import com.minook.zeppa.Constants;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator;
import com.minook.zeppa.singleton.NotificationSingleton;
import com.minook.zeppa.singleton.ZeppaEventSingleton;

public class NotificationDispatchActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ZeppaApplication application = (ZeppaApplication) getApplication();

		try {
			if (application.getCurrentActivity() != null) {

				long notificationId = getIntent().getLongExtra(
						Constants.EXTRA_NOTIFICATIONID, -1);

				if (notificationId > 0) {
					ZeppaNotification notification = NotificationSingleton
							.getInstance().getNotificationById(notificationId);
					// TODO: navigate to appropriate activity

					int notifType = NotificationSingleton.getInstance()
							.getNotificationTypeOrder(notification);

					if (notifType == 0) {
						if (application.getCurrentActivity() instanceof StartMinglingActivity) {
							// dont do anything
						} else {
							launchStartMinglingActivity();
						}
					} else if (notifType == 1) {
						if (application.getCurrentActivity() instanceof MinglerActivity) {
							MinglerActivity activity = (MinglerActivity) application
									.getCurrentActivity();

							if (notification.getSenderId().longValue() == activity
									.getMinglerId()) {
								// already looking at profile
							} else {
								launchMinglerProfileActivity(notification
										.getSenderId());
							}
						} else {
							launchMinglerProfileActivity(notification
									.getSenderId());
						}
					} else if (notifType == 5) {
						// dont do anything
					} else if (notifType > 1) {

						AbstractZeppaEventMediator event = ZeppaEventSingleton
								.getInstance().getEventById(
										notification.getSenderId());

						if (application.getCurrentActivity() instanceof AbstractEventViewActivity) {

						} else {
							event.launchIntoEventView(this);
						}

					}

					return;
				}

			}

			Intent toLogin = new Intent(this, LoginActivity.class);
			toLogin.putExtra(Constants.INTENT_NOTIFICATIONS, true);
			startActivity(toLogin);
			finish();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * This method starts StartMinglingActivity
	 */
	private void launchStartMinglingActivity() {
		Intent startMinglingActivity = new Intent(this,
				StartMinglingActivity.class);
		startActivity(startMinglingActivity);
	}

	/**
	 * This method starts MinglerActivity for the provided minglerId
	 * 
	 * @param minglerId
	 */
	private void launchMinglerProfileActivity(long minglerId) {

		Intent minglerIntent = new Intent(this, MinglerActivity.class);
		minglerIntent.putExtra(Constants.INTENT_ZEPPA_USER_ID, minglerId);
		startActivity(minglerIntent);

	}

}
