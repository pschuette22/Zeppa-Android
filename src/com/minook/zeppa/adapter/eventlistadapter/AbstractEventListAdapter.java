package com.minook.zeppa.adapter.eventlistadapter;

import java.io.IOException;
import java.util.List;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.R;
import com.minook.zeppa.Utils;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator;
import com.minook.zeppa.observer.OnLoadListener;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

public abstract class AbstractEventListAdapter extends BaseAdapter implements
		OnLoadListener {

	private final static String TAG = AbstractEventListAdapter.class.getName();

	protected AuthenticatedFragmentActivity activity;
	protected List<AbstractZeppaEventMediator> eventMediators;

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
		super();
		this.activity = activity;
		ZeppaEventSingleton.getInstance().registerObserver(this);
	}

	@Override
	public int getCount() {
		if (eventMediators == null) {
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

		AbstractZeppaEventMediator mediator = getItem(position);
		return mediator.getEventId().longValue();

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		AbstractZeppaEventMediator mediator = getItem(position);
		mediator.setContext(activity);

		if (convertView == null) {
			convertView = activity.getLayoutInflater().inflate(
					R.layout.view_eventlist_item, parent, false);
		}

		mediator.convertEventListItemView(activity, convertView);

		return convertView;

	}

	@Override
	public boolean didLoadInitial() {
		return ZeppaEventSingleton.getInstance().hasLoadedInitial();
	}

	@Override
	public void onFinishLoad() {
		verifyDatasetValid();
	}

	protected abstract List<AbstractZeppaEventMediator> getCurrentEventMediators();

	public void verifyDatasetValid() {
		if (didLoadInitial()) {
			List<AbstractZeppaEventMediator> currentMediators = getCurrentEventMediators();
			removeLoaderViewIfVisible();
			if (eventMediators != null
					&& eventMediators.containsAll(currentMediators)
					&& currentMediators.containsAll(eventMediators)) {
				Log.d(TAG, "Mediators up to date");
			} else {
				setEventMediators();
				notifyDataSetChanged();
			}

		}
	}

	protected abstract void setEventMediators();

	protected GoogleAccountCredential getCredential() throws IOException {
		return activity.getGoogleAccountCredential();
	}

	protected Long getUserId() {
		return ZeppaUserSingleton.getInstance().getUserId();
	}

	protected View getLoaderView() {
		return Utils.makeLoaderView(activity, "Finding Activities");
	}

	protected abstract void removeLoaderViewIfVisible();

}
