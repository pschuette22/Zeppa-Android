package com.minook.zeppa.fragments;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.R;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.activities.AuthenticatedFragmentActivity;
import com.minook.zeppa.adapters.eventadapter.WatchListAdapter;
import com.minook.zeppa.singleton.ZeppaEventSingleton;

public class WatchingFragment extends Fragment implements OnRefreshListener {

	// Private
	private View layout;
	private ListView watchingList;
	private PullToRefreshLayout pullToRefreshLayout;
	private WatchListAdapter wlAdapter;

	// Constants

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(wlAdapter == null)
			wlAdapter = new WatchListAdapter((AuthenticatedFragmentActivity) getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		layout = inflater.inflate(R.layout.fragment_watching, null, false);
		pullToRefreshLayout = (PullToRefreshLayout) layout
				.findViewById(R.id.watchingfragment_ptr);

		ActionBarPullToRefresh.from(getActivity())
				.options(Options.create().scrollDistance(.4f).build())
				.allChildrenArePullable().listener(this)
				.setup(pullToRefreshLayout);

		watchingList = (ListView) layout.findViewById(R.id.watchingListView);

		watchingList.setAdapter(wlAdapter);

		return layout;
	}

	@Override
	public void onRefreshStarted(View view) {
		findUnseenEvents((ZeppaApplication) getActivity().getApplication());
	}
	
	@Override
	public void onResume() {
		super.onResume();
		wlAdapter.notifyIfDataChanged();
	}
	
	/*
	 * --------------- Public Methods ---------------------
	 */
	

	public void onRefreshFinished() {
		pullToRefreshLayout.setRefreshComplete();
		wlAdapter.notifyDataSetChanged();
	}

	public void findUnseenEvents(final ZeppaApplication application) {

		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				return ZeppaEventSingleton.getInstance().loadNewEvents(
						getCredential());
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				onRefreshFinished();
			}

		}.execute();

	}
	
	/*
	 * --------------- Private Methods ---------------------
	 */

	private GoogleAccountCredential getCredential() {
		return ((ZeppaApplication) getActivity().getApplication())
				.getGoogleAccountCredential();
	}

	/*
	 * ----------------- MY METHODS ----------------------- NOTES:
	 */
	
}