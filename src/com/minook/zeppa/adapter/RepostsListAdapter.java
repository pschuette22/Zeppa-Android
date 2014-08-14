package com.minook.zeppa.adapter;

import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.mediator.MyZeppaEventMediator;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;

public class RepostsListAdapter extends BaseAdapter implements OnClickListener {

	private Activity activity;
	private ListView list;
	private View loadView;
	private MyZeppaEventMediator eventManager;
	private List<ZeppaEvent> reposts;
	private boolean didFinishFetch;

	
	public RepostsListAdapter(FragmentActivity activity, GoogleAccountCredential credential, MyZeppaEventMediator eventManager) {
		didFinishFetch = false;
		this.activity = activity;
		this.eventManager = eventManager;
//		this.reposts = ZeppaEventSingleton.getInstance().getAndFetchRepostsForEvent(activity, event, credential, this);
		
		
	}

	@Override
	public int getCount() {
		return reposts.size();
	}

	@Override
	public ZeppaEvent getItem(int position) {
		return reposts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getKey().getId().longValue();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {



		return convertView;
	}

	@Override
	public void onClick(DialogInterface dialog, int position) {
		if (position == DialogInterface.BUTTON_NEUTRAL) {
			dialog.dismiss();
		} else {

			ZeppaEvent event = getItem(position);
			Intent toRepost = new Intent();
			toRepost.putExtra(Constants.INTENT_ZEPPA_EVENT_ID, event.getKey()
					.getId());
			activity.startActivity(toRepost);
			activity.overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);

		}
	}

}
