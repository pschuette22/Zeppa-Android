package com.minook.zeppa.mediator;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaEvent;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.activity.MyEventViewActivity;
import com.minook.zeppa.runnable.RemoveEventRunnable;
import com.minook.zeppa.runnable.ThreadManager;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

public class MyZeppaEventMediator extends AbstractZeppaEventMediator {


	public MyZeppaEventMediator(ZeppaEvent event) {
		super(event);
		conflictStatus = ConflictStatus.ATTENDING;

	}

	@Override
	public boolean isAgendaEvent() {
		return true;
	}

	@Override
	protected void setHostInfo(View view) {
		MyZeppaUserMediator myMediator = ZeppaUserSingleton.getInstance()
				.getUserMediator();

		TextView hostName = (TextView) view
				.findViewById(R.id.eventview_hostname);

		hostName.setText(myMediator.getDisplayName());
		ImageView hostImage = (ImageView) view
				.findViewById(R.id.eventview_hostimage);
		myMediator.setImageWhenReady(hostImage);

	}


	public void deleteEvent(ZeppaApplication application, GoogleAccountCredential credential) {
		
		ThreadManager.execute(new RemoveEventRunnable(application, credential, getEventId().longValue()));
		ZeppaEventSingleton.getInstance().removeMediator(this);
	}
	
	

	@Override
	public void setConflictIndicator(Context context, ImageView image) {
		image.setImageResource(R.drawable.conflict_blue);
		image.setVisibility(View.VISIBLE);
	}



	/**
	 * 
	 * 
	 */


	@Override
	public View convertQuickActionBar(Context context, View barView) {
		barView.setVisibility(View.GONE);	
		return barView;
	}

	
	@Override
	public boolean isHostedByCurrentUser() {
		return true;
	}
	
	
	@Override
	public void launchIntoEventView(Context context) {
		Intent intent = new Intent(context, MyEventViewActivity.class);
		intent.putExtra(Constants.INTENT_ZEPPA_EVENT_ID, getEventId());
		context.startActivity(intent);
		if (context instanceof AuthenticatedFragmentActivity) {
			((AuthenticatedFragmentActivity) context)
					.overridePendingTransition(R.anim.slide_left_in,
							R.anim.slide_left_out);
		}
	}

	@Override
	public Intent getToEventViewIntent(Context context) {
		Intent intent = new Intent(context, MyEventViewActivity.class);
		intent.putExtra(Constants.INTENT_ZEPPA_EVENT_ID, getEventId());
		return intent;
	}



}
