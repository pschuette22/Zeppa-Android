package com.minook.zeppa.adapters.eventadapter;

import android.os.AsyncTask;
import android.os.DropBoxManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.minook.zeppa.R;
import com.minook.zeppa.activities.AuthenticatedFragmentActivity;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUser;

public class FriendEventsAdapter extends EventLayoutAdapter{

	private ZeppaUser friend;
	
	public FriendEventsAdapter(ZeppaUser friend, AuthenticatedFragmentActivity activity,
			LinearLayout eventHolder) {
		super(activity, eventHolder);
		
		this.friend = friend;
		events = ZeppaEventSingleton.getInstance().getEventsFor(friend.getKey().getId().longValue());

		drawEvents();
		loadEventsInAsync();
	
	}
	
	
	
	
	@Override
	protected View getLoaderView() {
		View loaderView = super.getLoaderView();
		TextView text = (TextView) loaderView.findViewById(R.id.loaderview_text);
		text.setText("Finding some of " + friend.getGivenName() + "'s events" );
		return loaderView;
	}




	public void loadEventsInAsync(){
		
		eventHolder.addView(getLoaderView(), 0);
		
		new AsyncTask<Void, Void, Boolean>(){

			
			@Override
			protected Boolean doInBackground(Void... params) {
				return ZeppaEventSingleton.getInstance().fetchEventsFor(activity, friend.getKey().getId(), getCredential());
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);

				if(result){
					notifyDataSetChanged();

				} else {
					Toast.makeText(activity, "Error On Load", Toast.LENGTH_SHORT).show();
					
				}
				
			
			}
			
			
			
		}.execute();
		
		
	}
	
	
}
