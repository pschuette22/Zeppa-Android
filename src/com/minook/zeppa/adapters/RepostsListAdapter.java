package com.minook.zeppa.adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUser;

public class RepostsListAdapter extends BaseAdapter implements OnClickListener {

	private Activity activity;
	private Long repostId;

	public RepostsListAdapter(FragmentActivity activity, Long repostId) {
		this.activity = activity;
		this.repostId = repostId;
	}

	@Override
	public int getCount() {
		return ZeppaEventSingleton.getInstance().getLocalReposts(repostId)
				.size();
	}

	@Override
	public ZeppaEvent getItem(int position) {
		// TODO Auto-generated method stub
		return ZeppaEventSingleton.getInstance().getLocalReposts(repostId)
				.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = activity.getLayoutInflater().inflate(
					R.layout.view_basicuseritem, parent, false);
		}
		ZeppaEvent event = getItem(position);
		ZeppaUser friend = ZeppaUserSingleton.getInstance().getUserById(
				event.getHostId());
		TextView titleText = (TextView) convertView
				.findViewById(R.id.contacts_listitem_name);
		ImageView imageView = (ImageView) convertView
				.findViewById(R.id.contacts_listitem_picture);
		int goingCount = (event.getUsersGoingIds() == null ? 0 : event
				.getUsersGoingIds().size());

		titleText.setText(friend.getDisplayName() + " (" + goingCount
				+ " joined)");
		setImageinAsync(friend, imageView);

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

	private void setImageinAsync(final ZeppaUser user, final ImageView image) {

		new AsyncTask<Void, Void, Drawable>() {

			@Override
			protected Drawable doInBackground(Void... params) {
				// TODO Auto-generated method stub
				return ZeppaUserSingleton.getInstance().getUserImage(user);
			}

			@Override
			protected void onPostExecute(Drawable result) {
				super.onPostExecute(result);
				if (result != null) {
					image.setImageDrawable(result);
				}
			}

		}.execute();

	}

}
