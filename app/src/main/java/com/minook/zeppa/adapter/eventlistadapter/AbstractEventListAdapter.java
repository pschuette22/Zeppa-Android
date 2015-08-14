package com.minook.zeppa.adapter.eventlistadapter;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator;
import com.minook.zeppa.mediator.DefaultZeppaEventMediator;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

import java.io.IOException;
import java.util.List;

public abstract class AbstractEventListAdapter extends BaseAdapter implements
		OnItemClickListener, OnClickListener {

//	private final static String TAG = AbstractEventListAdapter.class.getName();

	protected AuthenticatedFragmentActivity activity;
	protected List<AbstractZeppaEventMediator> eventMediators;

	protected long lastDataUpdateTime;

	/**
	 * EventListAdapter This class adapts a list of ZeppaEvents in the singleton
	 * to a list of views It also handles generating said views
	 * 
	 * @param AuthenticatedFragmentActvity
	 *            activity Calling authenticated activity
	 * 
	 * */

	public AbstractEventListAdapter(AuthenticatedFragmentActivity activity) {
		super();
		this.activity = activity;
		this.lastDataUpdateTime = -1;
	}

	@Override
	public int getCount() {
		if (eventMediators == null) {
			return 0;
		}

		return eventMediators.size();
	}

	@Override
	public AbstractZeppaEventMediator getItem(int position) {
		return eventMediators.get(position);
	}

	@Override
	public long getItemId(int position) {

		return getItem(position).getEventId().longValue();

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		AbstractZeppaEventMediator mediator = getItem(position);

		if (convertView == null) {
			convertView = activity.getLayoutInflater().inflate(
					R.layout.view_eventlist_item, parent, false);
		}

		convertView = mediator.convertEventListItemView(activity, convertView);
		convertView.setOnClickListener(this);

		if (mediator instanceof DefaultZeppaEventMediator) {
			View quickActionBar = convertView
					.findViewById(R.id.eventview_quickactionbar);
			quickActionBar.findViewById(R.id.quickaction_join)
					.setOnClickListener(this);
			quickActionBar.findViewById(R.id.quickaction_text)
					.setOnClickListener(this);
			quickActionBar.findViewById(R.id.quickaction_watch)
					.setOnClickListener(this);

		}

		return convertView;

	}

	@Override
	public void notifyDataSetChanged() {
		setEventMediators();
		super.notifyDataSetChanged();

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		AbstractZeppaEventMediator mediator = getItem(position);
		Intent intent = mediator.getToEventViewIntent(activity);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.slide_left_in,
				R.anim.slide_left_out);

	}

	protected abstract List<AbstractZeppaEventMediator> getCurrentEventMediators();

	protected abstract void setEventMediators();

	protected GoogleAccountCredential getCredential() throws IOException {
		return activity.getGoogleAccountCredential();
	}

	protected Long getUserId() {
		return ZeppaUserSingleton.getInstance().getUserId();
	}

	@Override
	public void onClick(View v) {

		AbstractZeppaEventMediator mediator = (AbstractZeppaEventMediator) v
				.getTag();

		if (v.getId() == R.id.eventview) {
			mediator.launchIntoEventView(activity);
		} else if (mediator instanceof DefaultZeppaEventMediator) {

			switch (v.getId()) {

			case R.id.quickaction_join:
				((DefaultZeppaEventMediator) mediator)
						.onJoinButtonClicked(activity);
				ZeppaEventSingleton.getInstance().notifyObservers();
				break;

			case R.id.quickaction_text:
				((DefaultZeppaEventMediator) mediator)
						.onTextButtonClicked(activity);
				break;

			case R.id.quickaction_watch:
				((DefaultZeppaEventMediator) mediator)
						.onWatchButtonClicked(activity);
				ZeppaEventSingleton.getInstance().notifyObservers();

				break;
			}

		}

	}

}
