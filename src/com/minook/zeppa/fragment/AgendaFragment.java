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
//	private View loaderView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ZeppaEventSingleton.getInstance().registerEventLoadListener(this);

		alAdapter = new AgendaListAdapter(
				(AuthenticatedFragmentActivity) getActivity());

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		layout = inflater.inflate(R.layout.fragment_agenda, container, false);

		agendaList = (ListView) layout.findViewById(R.id.agendaListView);
		pullToRefreshLayout = (PullToRefreshLayout) layout
				.findViewById(R.id.agendafragment_ptr);

		ActionBarPullToRefresh.from(getActivity())
				.options(Options.create().scrollDistance(.4f).build())
				.allChildrenArePullable().listener(this)
				.setup(pullToRefreshLayout);

		return layout;
	}

	@Override
	public void onStart() {
		super.onStart();

		if (!ZeppaEventSingleton.getInstance().hasLoadedInitial()
				|| ZeppaEventSingleton.getInstance().isLoadingEvents()) {
//			loaderView = Utils.makeLoaderView(
//					(AuthenticatedFragmentActivity) getActivity(),
//					"Finding Activities...");
//			agendaList.addHeaderView(loaderView);
			
			pullToRefreshLayout.setRefreshing(true);
			
		}

		agendaList.setAdapter(alAdapter);
		agendaList.setOnItemClickListener(alAdapter);

	}

	@Override
	public void onDestroyView() {

		pullToRefreshLayout.removeView(agendaList);

		super.onDestroyView();
	}

	@Override
	public void onRefreshStarted(View view) {
		alAdapter.notifyDataSetChanged();
		pullToRefreshLayout.setRefreshComplete();
	}

//	@Override
//	public void onResume() {
//		super.onResume();
//		
//		if(alAdapter != null)
//			alAdapter.notifyDataSetChanged();
//	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if (!ZeppaEventSingleton.getInstance().hasLoadedInitial() || ZeppaEventSingleton.getInstance().isLoadingEvents()) {
			pullToRefreshLayout.setRefreshing(true);
			
		}
	}
	
	@Override
	public void onPause() {
		if(pullToRefreshLayout.isRefreshing()){
			pullToRefreshLayout.setRefreshing(false);
		}
		super.onPause();
	}


	@Override
	public void onZeppaEventsLoaded() {

//		if (loaderView != null) {
//			loaderView.setVisibility(View.GONE);
//			agendaList.removeHeaderView(loaderView);
//			loaderView = null;
//		}

		pullToRefreshLayout.setRefreshing(false);
		alAdapter.notifyDataSetChanged();

	}

	/*
	 * --------------- Private Methods ---------------------
	 */

}