package com.minook.zeppa.mediator;

import java.util.List;

import android.os.AsyncTask;
import android.view.View;

import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;


public class MyZeppaEventMediator extends AbstractZeppaEventMediator{

	
	protected List<MyEventTagMediator> tagManagers;
	
	public MyZeppaEventMediator(ZeppaEvent event) {
		super(event);
		hostManager = ZeppaUserSingleton.getInstance().getUserMediator();
		
		conflictStatus = ConflictStatus.ATTENDING;

	}	
	
	
	@Override
	public boolean isInterestingEvent() {
		return true;
	}



	public void deleteEvent(){
		
	}
	
	private class DeleteEventTask extends AsyncTask<Void, Void, Boolean>{

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	private class DeleteRelationshipsTask extends AsyncTask<Void, Void, Boolean>{

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

	/**
	 * 
	 * 
	 */
	@Override
	public void convertView(View convertView) {

		
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}


}
