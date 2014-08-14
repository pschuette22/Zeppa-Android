package com.minook.zeppa.adapter;

import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.activity.UserActivity;
import com.minook.zeppa.mediator.DefaultUserInfoMediator;
import com.minook.zeppa.observer.OnLoadListener;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUser;

public class ContactListAdapter extends BaseAdapter implements
		ListView.OnItemClickListener, DialogInterface.OnClickListener,
		OnClickListener, OnLoadListener {

	private Activity activity;
	private List<DefaultUserInfoMediator> infoManagers;
	
	public ContactListAdapter(Activity activity, List<DefaultUserInfoMediator> friendInfoManagers) {
		this.activity = activity;
		
		
		
		this.infoManagers = ZeppaUserSingleton.getInstance().getFriendInfoManagers();
	}

	@Override
	public int getCount() {
		return infoManagers.size();
	}

	@Override
	public DefaultUserInfoMediator getItem(int position) {
		return infoManagers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getUserId().longValue();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = activity.getLayoutInflater().inflate(
					R.layout.view_basicuseritem, parent, false);
		}
		DefaultUserInfoMediator infoManager = getItem(position);

		convertView = infoManager.convertFriendListItemView(convertView);  

		return convertView;
	}
	
	/*
	 * Load listener methods
	 */
	@Override
	public boolean didLoadInitial() {
		return ZeppaUserSingleton.getInstance().hasLoadedInitial();
	}

	@Override
	public void onFinishLoad() {
		notifyDataSetChanged();
	}
	

	@Override
	public void onClick(DialogInterface dialog, int which) {

		if (which == DialogInterface.BUTTON_NEGATIVE) {
			dialog.dismiss();
		} else {

			DefaultUserInfoMediator infoManager = getItem(which);

			Intent toContact = infoManager.getToUserIntent(activity);
			activity.startActivity(toContact);
			activity.overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);
			dialog.dismiss();
		}
	}

	@Override
	public void onClick(View v) {
		ZeppaUser user = (ZeppaUser) v.getTag();

		Intent toContact = new Intent(activity, UserActivity.class);
		toContact.putExtra(Constants.INTENT_ZEPPA_USER_ID, user.getKey()
				.getId());
		activity.startActivity(toContact);
		activity.overridePendingTransition(R.anim.slide_left_in,
				R.anim.slide_left_out);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		DefaultUserInfoMediator infoManager = getItem(position);
		Intent toUserIntent = infoManager.getToUserIntent(activity);
		activity.overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
		activity.startActivity(toUserIntent);
		
		
	}

}