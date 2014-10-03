package com.minook.zeppa.mediator;

import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.activity.MainActivity;
import com.minook.zeppa.activity.MyEventViewActivity;
import com.minook.zeppa.singleton.EventTagSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;


public class MyZeppaEventMediator extends AbstractZeppaEventMediator{

	
	protected boolean deletingEvent;
	protected boolean editingEvent;
	
	
	public MyZeppaEventMediator(ZeppaEvent event) {
		super(event);
		conflictStatus = ConflictStatus.ATTENDING;
		deletingEvent = false;
		editingEvent = false;
	}	
	
	
	@Override
	public boolean isAgendaEvent() {
		return true;
	}

	
	


	@Override
	protected void setHostInfo(View view) {
		MyZeppaUserMediator myMediator = ZeppaUserSingleton.getInstance().getUserMediator();
		
		TextView hostName = (TextView) view
				.findViewById(R.id.eventview_hostname);
				
		hostName.setText(myMediator.getDisplayName());
		ImageView hostImage = (ImageView) view
				.findViewById(R.id.eventview_hostimage);
		myMediator.setImageWhenReady(hostImage);
		
		((LinearLayout) view.findViewById(R.id.eventview_hostinfo)).setOnClickListener(this);
		
	}


	public List<MyEventTagMediator> getTagMediators(){
		return EventTagSingleton.getInstance().getMyTagsFrom(event.getTagIds());
	}
	
	public void deleteEvent(AuthenticatedFragmentActivity context){
		deletingEvent = true;
		// TODO: raise deleting dialog
		// then change params to object and pass dialog
		// or set them as global params
		AuthenticatedFragmentActivity[] params = {context};
		new DeleteEventTask().execute(params);
		
	}
	
	private class DeleteEventTask extends AsyncTask<AuthenticatedFragmentActivity, Void, Boolean>{

		private AuthenticatedFragmentActivity context;
		
		@Override
		protected Boolean doInBackground(AuthenticatedFragmentActivity... params) {
			// TODO Auto-generated method stub
			context = params[0];
			
			// set object as deleted so nothing can be added to it
			// delete comments
			// delete relationships
			
			return null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			
			deletingEvent = false;
			if(result){
				context.onBackPressed();
			} else {
				
			}
			
		}
		
		
		
	}
	

	/**
	 * 
	 * 
	 */
	@Override
	public void convertView(AuthenticatedFragmentActivity context, View convertView) {
		super.convertView(context, convertView);
		((LinearLayout) convertView.findViewById(R.id.eventview_buttonbar)).setVisibility(View.GONE);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()){
		case R.id.eventview_hostinfo:
			if(getContext() instanceof MainActivity){
				((MainActivity) getContext()).selectItem(0, true);
			}
			break;
		
		default:
			Intent toEventDetails  = new Intent (getContext(), MyEventViewActivity.class);
			toEventDetails.putExtra(Constants.INTENT_ZEPPA_EVENT_ID, getEventId());
			getContext().startActivity(toEventDetails);
			getContext().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
		}
		
	}


}
