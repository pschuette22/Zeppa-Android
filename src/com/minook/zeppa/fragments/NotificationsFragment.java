package com.minook.zeppa.fragments;


import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.minook.zeppa.R;
import com.minook.zeppa.activities.MainActivity;
import com.minook.zeppa.adapters.NotificationsAdapter;
import com.minook.zeppa.singleton.NotificationSingleton;

public class NotificationsFragment extends Fragment implements
		OnRefreshListener {

	/*
	 * -------------------- Private Variables ----------------------
	 */

	private View layout;
	private ListView list;
	private View loaderView;
	private PullToRefreshLayout pullToRefreshLayout;
	private NotificationsAdapter adapter;

	/*
	 * --------------------- Override Methods ---------------------
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		layout = inflater.inflate(R.layout.fragment_notifications, container,
				false);
		list = (ListView) layout.findViewById(R.id.notificationsListView);
		pullToRefreshLayout = (PullToRefreshLayout) layout
				.findViewById(R.id.notificationsfragment_ptr);

		ActionBarPullToRefresh.from(getActivity())
				.options(Options.create().scrollDistance(.4f).build())
				.allChildrenArePullable().listener(this)
				.setup(pullToRefreshLayout);

		adapter = new NotificationsAdapter(((MainActivity) getActivity()));
		list.setAdapter(adapter);
		list.setOnItemClickListener(adapter);

		if (!NotificationSingleton.getInstance().hasLoadedInitial()) {
			loaderView = (View) layout
					.findViewById(R.id.notifications_loaderview);
			loaderView.setVisibility(View.GONE);

		}

		return layout;
	}

	@Override
	public void onResume() {
		super.onResume();
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setTitle(R.string.notifications);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

	}

	@Override
	public void onRefreshStarted(View view) {
		// TODO get new notifications
		// check if there are any new
		boolean didFindNew = false;
		if (didFindNew) {
			adapter.notifyDataSetChanged();
		} else {
			Toast noneNew = Toast.makeText(getActivity(), "Up To Date!",
					Toast.LENGTH_LONG);
			noneNew.show();
		}

	}

	/*
	 * ------------------- Public Methods --------------------
	 */

}
