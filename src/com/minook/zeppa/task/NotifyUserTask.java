package com.minook.zeppa.task;

import java.io.IOException;

import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.zeppanotificationendpoint.Zeppanotificationendpoint;
import com.minook.zeppa.zeppanotificationendpoint.model.ZeppaNotification;

public abstract class NotifyUserTask extends AsyncTask<Void, Void, Boolean>{

	protected GoogleAccountCredential credential;	
	protected HttpTransport transport = AndroidHttp.newCompatibleTransport();
	protected JsonFactory factory = AndroidJsonFactory.getDefaultInstance();
	
	
	
	public NotifyUserTask(GoogleAccountCredential credential){
		this.credential = credential;
	}
	
	@Override
	protected abstract Boolean doInBackground(Void... params);

	/**
	 * NOT THREAD SAFE</p>
	 * This method sends a notication and notifies all the users registered devices
	 * @param notification to send
	 * @return true if successfully sent
	 */
	protected boolean insertNotificationObject(ZeppaNotification notification){
		boolean success = false;
		
		Zeppanotificationendpoint.Builder builder = new Zeppanotificationendpoint.Builder(transport, factory, credential);
		builder = CloudEndpointUtils.updateBuilder(builder);
		Zeppanotificationendpoint endpoint = builder.build();
		
		try {
			endpoint.insertZeppaNotification(notification).execute();
			success = true;
		} catch (IOException e){
			e.printStackTrace();
		}
		
		
		return success;
	}
	
}
	