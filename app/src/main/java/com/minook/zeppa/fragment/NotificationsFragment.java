package com.minook.zeppa.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.minook.zeppa.R;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.adapter.NotificationsAdapter;
import com.minook.zeppa.singleton.NotificationSingleton;
import com.minook.zeppa.singleton.NotificationSingleton.NotificationLoadListener;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class NotificationsFragment extends Fragment implements
		OnRefreshListener, NotificationLoadListener {

	private View layout;
//	private View loaderView;
	private PullToRefreshLayout pullToRefreshLayout;
	private ListView activityList;
	private NotificationsAdapter notificationsAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		notificationsAdapter = new NotificationsAdapter(
				(AuthenticatedFragmentActivity) getActivity());

	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		layout = inflater.inflate(R.layout.fragment_activity, container, false);
		activityList = (ListView) layout.findViewById(R.id.activityListView);
		pullToRefreshLayout = (PullToRefreshLayout) layout
				.findViewById(R.id.activityfragment_ptr);


		activityList.setAdapter(notificationsAdapter);
		activityList.setOnItemClickListener(notificationsAdapter);

		ActionBarPullToRefresh.from(getActivity())
				.options(Options.create().scrollDistance(.4f).build())
				.allChildrenArePullable().listener(this)
				.setup(pullToRefreshLayout);
		
		NotificationSingleton.getInstance().registerOnLoadListener(this);
		
		return layout;
	}

	@Override
	public void onStart() {

		if (!NotificationSingleton.getInstance().hasLoadedInitial()) {
			pullToRefreshLayout.setRefreshing(true);
		}

		super.onStart();
	}

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
	public void onDestroyView() {
		pullToRefreshLayout.removeView(activityList);
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
			Toast.makeText(getActivity(), "Error Fetching Notifications", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onNotificationsLoaded() {

		try {
			if(pullToRefreshLayout != null)
				pullToRefreshLayout.setRefreshing(false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(notificationsAdapter != null)
			notificationsAdapter.notifyDataSetChanged();
	}

	@Override
	public void onNotificationDataChanged() {
		if(notificationsAdapter != null)
			notificationsAdapter.notifyDataSetChanged();

	}

}
