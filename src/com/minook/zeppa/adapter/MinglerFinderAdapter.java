package com.minook.zeppa.adapter;

import java.util.List;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.minook.zeppa.R;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.mediator.DefaultUserInfoMediator;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

public class MinglerFinderAdapter extends BaseAdapter implements
		OnClickListener {

	private AuthenticatedFragmentActivity context;

	private List<DefaultUserInfoMediator> possibleConnectionMediators;
	private List<DefaultUserInfoMediator> pendingRequestMediators;

	public MinglerFinderAdapter(AuthenticatedFragmentActivity context) {
		this.context = context;
		setMediators();

	}

	@Override
	public void notifyDataSetChanged() {

		setMediators();
		super.notifyDataSetChanged();

	}

	@Override
	public int getCount() {
		return possibleConnectionMediators.size()
				+ pendingRequestMediators.size();
	}

	@Override
	public DefaultUserInfoMediator getItem(int position) {
		int pending = pendingRequestMediators.size();
		if (position >= pending) {
			return possibleConnectionMediators.get(position - pending);
		} else {
			return pendingRequestMediators.get(position);
		}
	}

	@Override
	public long getItemId(int position) {

		return getItem(position).getUserId().longValue();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		DefaultUserInfoMediator mediator = getItem(position);

		if (mediator.getUserRelationship() == null
				|| mediator.getUserRelationship().getCreatorId().longValue() == ZeppaUserSingleton
						.getInstance().getUserId().longValue()) {

			convertView = context.getLayoutInflater().inflate(
					R.layout.view_addcontact_itemsend, parent, false);
			convertView = mediator
					.convertRequestConnectListItemView(convertView);
			convertView.findViewById(R.id.newcontact_senditem_button)
					.setOnClickListener(this);

		} else {

			convertView = context.getLayoutInflater().inflate(
					R.layout.view_addcontact_itemrespond, parent, false);
			mediator.convertRespondConnectListItemView(context, convertView);
			convertView.findViewById(R.id.newcontact_confirm_button)
					.setOnClickListener(this);
			convertView.findViewById(R.id.newcontact_deny_button)
					.setOnClickListener(this);

		}

		ImageView image = (ImageView) convertView
				.findViewById(R.id.newcontact_picture);
		TextView nameText = (TextView) convertView
				.findViewById(R.id.newcontact_name);

		mediator.setImageWhenReady(image);
		nameText.setText(mediator.getDisplayName());

		return convertView;
	}

	/*
	 * Private methods
	 */

	private void setMediators() {
		possibleConnectionMediators = ZeppaUserSingleton.getInstance()
				.getPossibleFriendInfoMediators();
		pendingRequestMediators = ZeppaUserSingleton.getInstance()
				.getPendingFriendRequests();

	}

	@Override
	public void onClick(View v) {

		DefaultUserInfoMediator mediator = (DefaultUserInfoMediator) v.getTag();

		switch (v.getId()) {
		case R.id.newcontact_confirm_button:
			mediator.acceptMingleRequest(
					(ZeppaApplication) context.getApplication(),
					context.getGoogleAccountCredential());

			break;
		case R.id.newcontact_deny_button:
			mediator.removeRelationship(
					(ZeppaApplication) context.getApplication(),
					context.getGoogleAccountCredential());
			break;
		case R.id.newcontact_senditem_button:

			if (mediator.getUserRelationship() == null) {
				mediator.sendMingleRequest(
						(ZeppaApplication) context.getApplication(),
						context.getGoogleAccountCredential());
			} else {
				mediator.removeRelationship(
						(ZeppaApplication) context.getApplication(),
						context.getGoogleAccountCredential());
			} 

			break;
		}

		ZeppaUserSingleton.getInstance().notifyObservers();

	}

}
