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
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.R;
import com.minook.zeppa.Utils;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.adapter.eventlistadapter.FeedListAdapter;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.singleton.ZeppaEventSingleton.OnZeppaEventLoadListener;

public class FeedFragment extends Fragment implements OnRefreshListener,
		OnZeppaEventLoadListener {

	// Private

	private View layout;
	private View loaderView;
	private PullToRefreshLayout pullToRefreshLayout;
	private ListView feedList;
	private FeedListAdapter flAdapter;

	private OnScrollListener mScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if ((scrollState == OnScrollListener.SCROLL_STATE_IDLE)
					&& (feedList.getLastVisiblePosition() == flAdapter
							.getCount() - 1)) {

				ZeppaEventSingleton.getInstance().fetchMoreEvents(
						(ZeppaApplication) getActivity().getApplication(),
						((AuthenticatedFragmentActivity) getActivity())
								.getGoogleAccountCredential());

			}

		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

		}

	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		layout = inflater.inflate(R.layout.fragment_feed, container, false);

		feedList = (ListView) layout.findViewById(R.id.feedListView);
		
		feedList.setOnScrollListener(mScrollListener);
		
		pullToRefreshLayout = (PullToRefreshLayout) layout
				.findViewById(R.id.feedfragment_ptr);

		flAdapter = new FeedListAdapter(
				(AuthenticatedFragmentActivity) getActivity());

		ActionBarPullToRefresh.from(getActivity())
				.options(Options.create().scrollDistance(.4f).build())
				.allChildrenArePullable().listener(this)
				.setup(pullToRefreshLayout);

		if (!ZeppaEventSingleton.getInstance().hasLoadedInitial()) {
			loaderView = Utils.makeLoaderView(
					(AuthenticatedFragmentActivity) getActivity(),
					"Finding Activities...");
			feedList.addHeaderView(loaderView);
		}

		feedList.setAdapter(flAdapter);
		feedList.setOnItemClickListener(flAdapter);

		ZeppaEventSingleton.getInstance().registerEventLoadListener(this);
		return layout;
	}

	@Override
	public void onRefreshStarted(View view) {
		try {
			GoogleAccountCredential credential = ((AuthenticatedFragmentActivity) getActivity())
					.getGoogleAccountCredential();
			ZeppaEventSingleton.getInstance().fetchNewEvents(
					(ZeppaApplication) getActivity().getApplication(),
					credential, pullToRefreshLayout);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		flAdapter.notifyDataSetChanged();

	}

	@Override
	public void onZeppaEventsLoaded() {
		if (loaderView != null) {
			feedList.removeHeaderView(loaderView);
			loaderView = null;
		}

		pullToRefreshLayout.setRefreshing(false);

		flAdapter.notifyDataSetChanged();

	}

	

}
