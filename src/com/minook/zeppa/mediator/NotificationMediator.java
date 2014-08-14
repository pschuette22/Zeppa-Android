package com.minook.zeppa.mediator;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.activity.NewFriendsActivity;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppanotificationendpoint.model.ZeppaNotification;

public class NotificationMediator implements OnClickListener{

	private ZeppaNotification notification;
	private Activity activity;
	       
	public NotificationMediator(ZeppaNotification notification) {
		this.notification = notification;
	}

	public View convertView(Activity activity, View convertView) throws NullPointerException{
		this.activity = activity;
		AbstractZeppaUserMediator userMediator = ZeppaUserSingleton.getInstance().getUserFor(notification.getFromUserId());
		ImageView image = (ImageView) convertView.findViewById(R.id.notificationitem_userimage);
		userMediator.setImageWhenReady(image);
		
		TextView message = (TextView) convertView.findViewById(R.id.notificationitem_text);
		message.setText(notification.getExtraMessage());
		
		TextView date = (TextView) convertView.findViewById(R.id.notificationitem_date);
		date.setText(Constants.getDisplayDateString(notification.getSentDate().longValue()));
				
		convertView.setOnClickListener(this);
		return convertView;
	}

	@Override
	public void onClick(View v) {
		switch(notification.getNotificationOrdinal().intValue()){
		case 0:
			Intent toRequests = new Intent(activity, NewFriendsActivity.class);
			activity.startActivity(toRequests);
			activity.overridePendingTransition(R.anim.slide_up_in, R.anim.hold);
			
			break;
		
		
		}
		
	}
	
}
