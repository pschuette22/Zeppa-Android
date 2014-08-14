package com.minook.zeppa.fragment;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.adapter.eventlistadapter.WatchListAdapter;

public class WatchingFragment extends Fragment implements OnRefreshListener {

	// Private
	View layout;
	ListView watchingList;
	PullToRefreshLayout pullToRefreshLayout;
	WatchListAdapter wlAdapter;

	// Constants

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d("TAG", "CreateView called on watching fragment");
		super.onCreateView(inflater, container, savedInstanceState);

		layout = inflater.inflate(R.layout.fragment_watching, null, false);
		
		
		watchingList = (ListView) layout
				.findViewById(R.id.watchingListView);
		pullToRefreshLayout = (PullToRefreshLayout) layout
				.findViewById(R.id.watchingfragment_ptr);	
		
		if (wlAdapter == null) {
			wlAdapter = new WatchListAdapter(
					(AuthenticatedFragmentActivity) getActivity(), watchingList);
		}
		
		wlAdapter.verifyDatasetValid();
		watchingList.setAdapter(wlAdapter);
		

		ActionBarPullToRefresh.from(getActivity())
				.options(Options.create().scrollDistance(.4f).build())
				.allChildrenArePullable().listener(this)
				.setup(pullToRefreshLayout);

		return layout;
	}
	

	@Override
	public void onRefreshStarted(View view) {
		wlAdapter.fetchNewEventsInAsync(pullToRefreshLayout);
	}

	/*
	 * --------------- Private Methods ---------------------
	 */

}