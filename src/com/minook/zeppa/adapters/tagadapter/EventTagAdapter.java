package com.minook.zeppa.adapters.tagadapter;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.eventtagendpoint.Eventtagendpoint;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUser;

public class EventTagAdapter extends FriendTagAdapter {

	public EventTagAdapter(FragmentActivity activity, LinearLayout layout,
			ZeppaUser user, ZeppaEvent event) {
		super(activity, layout, user);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return super.getView(position, convertView, parent);
	}

	@Override
	protected void loadTagsInAsync() {

		new AsyncTask<Void, Void, Boolean>(){

			@Override
			protected Boolean doInBackground(Void... params) {
				Eventtagendpoint.Builder endpointBuilder = new Eventtagendpoint.Builder(
						AndroidHttp.newCompatibleTransport(),
						new JacksonFactory(), getGoogleAccountCredential());
				
				endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
				
				Eventtagendpoint tagEndpoint = endpointBuilder.build();
					
//					GetEventTagsForEvent getTagsForEvent = tagEndpoint.GetEvent
					
					
				
				
				return null;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if(result){
					drawTags();
				} else {
					
				}
			}
			
			
			
		}.execute();
		
		
	}

}
