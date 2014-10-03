package com.minook.zeppa.task;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.zeppanotificationendpoint.model.ZeppaNotification;

import android.os.AsyncTask;

public abstract class NotifyUserTask extends AsyncTask<Object, Void, Boolean>{

	protected GoogleAccountCredential credential;
	
	public NotifyUserTask(GoogleAccountCredential credential){
		this.credential = credential;
	}
	
	@Override
	protected abstract Boolean doInBackground(Object... params);

	protected boolean insertNotificationObject(ZeppaNotification notification){
		boolean success = false;
		
		
		
		return success;
	}
	
}
	