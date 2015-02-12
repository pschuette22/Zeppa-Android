package com.minook.zeppa.fragment;

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
import com.minook.zeppa.Utils;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.adapter.eventlistadapter.AgendaListAdapter;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.singleton.ZeppaEventSingleton.OnZeppaEventLoadListener;

public class AgendaFragment extends Fragment implements OnRefreshListener,
		OnZeppaEventLoadListener {

	// Private
	private View layout;
	private ListView agendaList;
	private PullToRefreshLayout pullToRefreshLayout;
	private AgendaListAdapter alAdapter;
	private View loaderView;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ZeppaEventSingleton.getInstance().registerEventLoadListener(this);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		layout = inflater.inflate(R.layout.fragment_watching, container, false);

		agendaList = (ListView) layout.findViewById(R.id.watchingListView);
		pullToRefreshLayout = (PullToRefreshLayout) layout
				.findViewById(R.id.watchingfragment_ptr);

		alAdapter = new AgendaListAdapter(
				(AuthenticatedFragmentActivity) getActivity());


		ActionBarPullToRefresh.from(getActivity())
				.options(Options.create().scrollDistance(.4f).build())
				.allChildrenArePullable().listener(this)
				.setup(pullToRefreshLayout);

		if (!ZeppaEventSingleton.getInstance().hasLoadedInitial()) {
			loaderView = Utils.makeLoaderView(
					(AuthenticatedFragmentActivity) getActivity(),
					"Finding Activities...");
			agendaList.addHeaderView(loaderView);
		}
		
		agendaList.setAdapter(alAdapter);
		agendaList.setOnItemClickListener(alAdapter);


		return layout;
	}

	@Override
	public void onRefreshStarted(View view) {

		pullToRefreshLayout.setRefreshComplete();
	}

	@Override
	public void onResume() {
		super.onResume();
		alAdapter.notifyDataSetChanged();
	}

	@Override
	public void onDestroy() {
		ZeppaEventSingleton.getInstance().unregisterEventLoadListener(this);
		super.onDestroy();
	}

	@Override
	public void onZeppaEventsLoaded() {
		
		if (loaderView != null) {
			loaderView.setVisibility(View.GONE);
			agendaList.removeHeaderView(loaderView);
			loaderView = null;
		}
		
		pullToRefreshLayout.setRefreshing(false);
		alAdapter.notifyDataSetChanged();

	}

	/*
	 * --------------- Private Methods ---------------------
	 */

}