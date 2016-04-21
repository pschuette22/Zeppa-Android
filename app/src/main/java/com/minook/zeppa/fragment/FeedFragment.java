package com.minook.zeppa.fragment;

/**
 * @author DrunkWithFunk21
 *	
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.R;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.adapter.eventlistadapter.FeedListAdapter;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.singleton.ZeppaEventSingleton.OnZeppaEventLoadListener;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class FeedFragment extends Fragment implements OnRefreshListener,
		OnZeppaEventLoadListener {


	private View layout;
	private PullToRefreshLayout pullToRefreshLayout;
	private ListView feedList;
	// Adapter for feed events
	private FeedListAdapter flAdapter;

	private OnScrollListener mScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			flAdapter.onScrollStart();

		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

			flAdapter.onScrollStop();

			if (totalItemCount > 3 && (firstVisibleItem + visibleItemCount) == totalItemCount) {
				ZeppaEventSingleton.getInstance().fetchMoreEvents(
						(ZeppaApplication) getActivity().getApplication(),
						((AuthenticatedFragmentActivity) getActivity())
								.getGoogleAccountCredential());
			}

		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		flAdapter = new FeedListAdapter(
				(AuthenticatedFragmentActivity) getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		layout = inflater.inflate(R.layout.fragment_feed, container, false);

		feedList = (ListView) layout.findViewById(R.id.feedListView);
		pullToRefreshLayout = (PullToRefreshLayout) layout
				.findViewById(R.id.feedfragment_ptr);

		ActionBarPullToRefresh.from(getActivity())
				.options(Options.create().scrollDistance(.4f).build())
				.allChildrenArePullable().listener(this)
				.setup(pullToRefreshLayout);

		ZeppaEventSingleton.getInstance().registerEventLoadListener(this);
		return layout;
	}
	
	

	// @Override
	// public void onResume() {
	// super.onResume();
	// flAdapter.notifyDataSetChanged();
	//
	// }

	@Override
	public void onStart() {
		feedList.setOnScrollListener(mScrollListener);

//		if (!ZeppaEventSingleton.getInstance().hasLoadedInitial() || ZeppaEventSingleton.getInstance().isLoadingEvents()) {			
//			pullToRefreshLayout.setRefreshing(true);
//		}

		feedList.setAdapter(flAdapter);
		feedList.setOnItemClickListener(flAdapter);
		super.onStart();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if (!ZeppaEventSingleton.getInstance().hasLoadedInitial() || ZeppaEventSingleton.getInstance().isLoadingEvents()) {
			pullToRefreshLayout.setRefreshing(true);
			
		}

		flAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onPause() {
		if(pullToRefreshLayout.isRefreshing()){
			pullToRefreshLayout.setRefreshing(false);
		}
		super.onPause();
	}

	@Override
	public void onDestroyView() {

		pullToRefreshLayout.removeView(feedList);
		ZeppaEventSingleton.getInstance().unregisterEventLoadListener(this);

		super.onDestroyView();
	}

	@Override
	public void onRefreshStarted(View view) {
		try {
			GoogleAccountCredential credential = ((AuthenticatedFragmentActivity) getActivity())
					.getGoogleAccountCredential();
			ZeppaEventSingleton.getInstance().fetchNewEvents(
					(ZeppaApplication) getActivity().getApplication(),
					credential);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onZeppaEventsLoaded() {

		pullToRefreshLayout.setRefreshing(false);
		flAdapter.notifyDataSetChanged();

	}

}
