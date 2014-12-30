package com.minook.zeppa.fragment;

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
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.adapter.eventlistadapter.FeedListAdapter;

public class FeedFragment extends Fragment implements OnRefreshListener {

	// Private

	private View layout;
	private PullToRefreshLayout pullToRefreshLayout;
	private ListView feedList;
	private FeedListAdapter flAdapter;

	// Constants

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		layout = inflater.inflate(R.layout.fragment_feed, container, false);

		feedList = (ListView) layout.findViewById(R.id.feedListView);
		pullToRefreshLayout = (PullToRefreshLayout) layout
				.findViewById(R.id.feedfragment_ptr);

		flAdapter = new FeedListAdapter(
				(AuthenticatedFragmentActivity) getActivity(), feedList);

		feedList.setAdapter(flAdapter);

		ActionBarPullToRefresh.from(getActivity())
				.options(Options.create().scrollDistance(.4f).build())
				.allChildrenArePullable().listener(this)
				.setup(pullToRefreshLayout);

		return layout;
	}

	@Override
	public void onRefreshStarted(View view) {
		flAdapter.fetchNewEventsInAsync(pullToRefreshLayout);
	}

	@Override
	public void onResume() {
		super.onResume();
		flAdapter.notifyDataSetChanged();

	}

	/*
	 * --------------- Public Methods ---------------------
	 */

}
