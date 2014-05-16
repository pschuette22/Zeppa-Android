package com.minook.zeppa.fragments;

/**
 * @author DrunkWithFunk21
 *	
 */

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.minook.zeppa.R;
import com.minook.zeppa.activities.AuthenticatedFragmentActivity;
import com.minook.zeppa.adapters.eventadapter.FeedListAdapter;

public class FeedFragment extends Fragment implements OnRefreshListener {

	// Private

	private View layout;
	private PullToRefreshLayout pullToRefreshLayout;
	private ListView feedList;
	private FeedListAdapter flAdapter;

	// Constants

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (flAdapter == null)
			flAdapter = new FeedListAdapter((AuthenticatedFragmentActivity) getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		layout = inflater.inflate(R.layout.fragment_feed, null, false);
		feedList = (ListView) layout.findViewById(R.id.feedListView);
		pullToRefreshLayout = (PullToRefreshLayout) layout
				.findViewById(R.id.feedfragment_ptr);

		ActionBarPullToRefresh.from(getActivity())
				.options(Options.create().scrollDistance(.4f).build())
				.allChildrenArePullable().listener(this)
				.setup(pullToRefreshLayout);

		feedList.setAdapter(flAdapter);

		return layout;
	}

	@Override
	public void onResume() {
		super.onResume();
		flAdapter.notifyIfDataChanged();
	}

	@Override
	public void onRefreshStarted(View view) {
		flAdapter.fetchNewEventsInAsync(pullToRefreshLayout);
	}

	/*
	 * --------------- Public Methods ---------------------
	 */


}
