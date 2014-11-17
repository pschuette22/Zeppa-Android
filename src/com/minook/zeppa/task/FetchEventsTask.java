package com.minook.zeppa.task;

import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.zeppaeventendpoint.Zeppaeventendpoint;

public abstract class FetchEventsTask extends AsyncTask<Void, Void, Void>{

	protected GoogleAccountCredential credential;
	protected HttpTransport transport = AndroidHttp.newCompatibleTransport();
	protected GsonFactory factory = GsonFactory.getDefaultInstance();
	protected Long userId;
	
	protected FetchEventsTask(GoogleAccountCredential credential, Long userId){
		this.credential = credential;
		this.userId = userId;
	}

	@Override
	protected abstract Void doInBackground(Void... params);
	
	protected Zeppaeventendpoint buildZeppaEventEndpoint(){
		Zeppaeventendpoint.Builder builder = new Zeppaeventendpoint.Builder(transport, factory, credential);
		builder = CloudEndpointUtils.updateBuilder(builder);
		return builder.build();
		
	}
	
	
	
	
}
