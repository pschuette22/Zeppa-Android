package com.minook.zeppa.task;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import android.os.AsyncTask;

public abstract class ZeppaEndpointTask extends AsyncTask<Void, Void, Boolean>{

	
	protected GoogleAccountCredential credential;
	protected HttpTransport transport = AndroidHttp.newCompatibleTransport();
	protected GsonFactory factory = GsonFactory.getDefaultInstance();
	
	public ZeppaEndpointTask(GoogleAccountCredential credential) {
		this.credential = credential;
	}

}
