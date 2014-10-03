package com.minook.zeppa.adapter.eventlistadapter;

import java.io.IOException;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator;
import com.minook.zeppa.observer.OnLoadListener;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.utils.Utils;

public abstract class AbstractEventListAdapter extends BaseAdapter implements OnLoadListener{

	protected AuthenticatedFragmentActivity activity;
	protected List<AbstractZeppaEventMediator> eventMediators;
	
	protected boolean initialDidLoad;
	protected View loaderView;


	/**
	 * EventListAdapter This class adapts a list of ZeppaEvents in the singleton
	 * to a list of views It also handles generating said views
	 * 
	 * @param AuthenticatedFragmentActvity
	 *            activity Calling authenticated activity
	 * 
	 * */

	public AbstractEventListAdapter(AuthenticatedFragmentActivity activity) {
		this.activity = activity;
	}

	
	@Override
	public int getCount() {
		if(eventMediators == null){
			return 0; // check for some sort of error?
		}
		
		return eventMediators.size();
	}

	@Override
	public AbstractZeppaEventMediator getItem(int position) {
		return eventMediators.get(position);
	}

	@Override
	public long getItemId(int position) {

		AbstractZeppaEventMediator manager = getItem(position);
		return manager.getEventId().longValue();

	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		AbstractZeppaEventMediator manager = getItem(position);

		if (convertView == null) {
			convertView = activity.getLayoutInflater().inflate(R.layout.view_eventlist_item, parent, false);
		} 
		
		manager.convertView(activity, convertView);
		
		return convertView;

	}

	public abstract void verifyDatasetValid();
	
	protected abstract void setEventMediators();
	
	protected GoogleAccountCredential getCredential() throws IOException{
		return activity.getGoogleAccountCredential();
	}

	protected Long getUserId() {
		return ZeppaUserSingleton.getInstance().getUserId();
	}

	
	protected View getLoaderView(){
		return Utils.makeLoaderView(activity, "Finding Activities");
	}

}
