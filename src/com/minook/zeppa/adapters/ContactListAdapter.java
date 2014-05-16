package com.minook.zeppa.adapters;

import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.activities.UserActivity;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUser;

public class ContactListAdapter extends BaseAdapter implements
		ListView.OnItemClickListener, DialogInterface.OnClickListener,
		OnClickListener {

	private Activity activity;
	private List<ZeppaUser> zeppaUsers;

	public ContactListAdapter(Activity activity, List<ZeppaUser> zeppaUsers) {
		this.activity = activity;
		this.zeppaUsers = zeppaUsers;
	}

	@Override
	public int getCount() {
		return zeppaUsers.size();
	}

	@Override
	public ZeppaUser getItem(int position) {
		return zeppaUsers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = activity.getLayoutInflater().inflate(
					R.layout.view_basicuseritem, parent, false);
		}
		ZeppaUser user = getItem(position);

		convertView.setTag(user);
		TextView contactName = (TextView) convertView
				.findViewById(R.id.contacts_listitem_name);
		ImageView contactImage = (ImageView) convertView
				.findViewById(R.id.contacts_listitem_picture);

		contactName.setText(user.getDisplayName());
		setImageInAsync(contactImage, user);

		return convertView;
	}

	private void setImageInAsync(final ImageView imageView,
			final ZeppaUser zeppaUser) {

		new AsyncTask<Void, Void, Drawable>() {

			@Override
			protected Drawable doInBackground(Void... params) {
				return ZeppaUserSingleton.getInstance().getUserImage(zeppaUser);

			}

			@Override
			protected void onPostExecute(Drawable result) {
				super.onPostExecute(result);
				if (result != null)
					imageView.setImageDrawable(result);
			}

		}.execute();

	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View contactItem,
			int position, long arg3) {

		ZeppaUser user = getItem(position);

		Intent toContact = new Intent(activity, UserActivity.class);
		toContact.putExtra(Constants.INTENT_ZEPPA_USER_ID, user.getKey()
				.getId());
		activity.startActivity(toContact);
		activity.overridePendingTransition(R.anim.slide_left_in,
				R.anim.slide_left_out);

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

		if (which == DialogInterface.BUTTON_NEGATIVE) {
			dialog.dismiss();
		} else {

			ZeppaUser user = getItem(which);

			Intent toContact = new Intent(activity, UserActivity.class);
			toContact.putExtra(Constants.INTENT_ZEPPA_USER_ID, user.getKey()
					.getId());
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

}