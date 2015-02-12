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
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.adapter.NotificationsAdapter;
import com.minook.zeppa.singleton.NotificationSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.singleton.NotificationSingleton.NotificationLoadListener;

public class ActivityFragment extends Fragment implements OnRefreshListener,
		NotificationLoadListener {

	private View layout;
	private View loaderView;
	private PullToRefreshLayout pullToRefreshLayout;
	private ListView activityList;
	private NotificationsAdapter notifictionsAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		NotificationSingleton.getInstance().registerOnLoadListener(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		layout = inflater.inflate(R.layout.fragment_activity, container, false);
		activityList = (ListView) layout.findViewById(R.id.activityListView);
		pullToRefreshLayout = (PullToRefreshLayout) layout
				.findViewById(R.id.activityfragment_ptr);

		if (!NotificationSingleton.getInstance().hasLoadedInitial()) {
			loaderView = Utils.makeLoaderView(
					(AuthenticatedFragmentActivity) getActivity(),
					"Loading Notifications...");
			activityList.addHeaderView(loaderView);
		}

		notifictionsAdapter = new NotificationsAdapter(
				(AuthenticatedFragmentActivity) getActivity());
		activityList.setAdapter(notifictionsAdapter);
		activityList.setOnItemClickListener(notifictionsAdapter);

		ActionBarPullToRefresh.from(getActivity())
				.options(Options.create().scrollDistance(.4f).build())
				.allChildrenArePullable().listener(this)
				.setup(pullToRefreshLayout);

		NotificationSingleton.getInstance().registerOnLoadListener(this);

		return layout;
	}

	@Override
	public void onResume() {
		super.onResume();
		notifictionsAdapter.notifyDataSetChanged();

	}

	@Override
	public void onDestroyView() {
		NotificationSingleton.getInstance().unregisterOnLoadListener(this);
		super.onDestroyView();
	}

	@Override
	public void onRefreshStarted(View view) {
		try {

			NotificationSingleton.getInstance().fetchNotifications(
					(ZeppaApplication) getActivity().getApplication(),
					((AuthenticatedFragmentActivity) getActivity())
							.getGoogleAccountCredential(),
					ZeppaUserSingleton.getInstance().getUserId().longValue());

		} catch (Exception e) {
			pullToRefreshLayout.setRefreshing(false);
		}
	}

	@Override
	public void onNotificationsLoaded() {

		try {
			pullToRefreshLayout.setRefreshing(false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (loaderView != null) {
			activityList.removeHeaderView(loaderView);
			loaderView = null;
		}

		notifictionsAdapter.notifyDataSetChanged();
	}

}
