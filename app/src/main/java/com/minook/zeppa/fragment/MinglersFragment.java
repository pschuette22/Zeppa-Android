package com.minook.zeppa.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.activity.MainActivity;
import com.minook.zeppa.activity.StartMinglingActivity;
import com.minook.zeppa.adapter.MinglerListAdapter;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton.OnMinglersLoadListener;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class MinglersFragment extends Fragment implements OnClickListener,
		OnMinglersLoadListener, OnItemClickListener, OnRefreshListener {

	// ----------- Global Variables Bank ------------- \\
	// Private
	private View layout;
	private ListView contactList;
	private MinglerListAdapter adapter;
	private ImageButton addMinglers;
	private PullToRefreshLayout pullToRefreshLayout;
//	private View loaderView;
	
	public MinglersFragment(){
		
		
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		adapter = new MinglerListAdapter(
				(AuthenticatedFragmentActivity) getActivity(),
				null);
		
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		layout = inflater.inflate(R.layout.fragment_minglers, container, false);
		contactList = (ListView) layout.findViewById(R.id.minglers_listview);
		addMinglers = (ImageButton) layout.findViewById(R.id.minglers_add);
		addMinglers.setOnClickListener(this);

		pullToRefreshLayout = (PullToRefreshLayout) layout
				.findViewById(R.id.minglersfragment_ptr);

		ActionBarPullToRefresh.from(getActivity())
				.options(Options.create().scrollDistance(.4f).build())
				.allChildrenArePullable().listener(this)
				.setup(pullToRefreshLayout);
		

		
		contactList.setAdapter(adapter);
		contactList.setOnItemClickListener(this);
		
		ZeppaUserSingleton.getInstance().registerLoadListener(this);

		
		return layout;
	}

	

	@Override
	public void onResume() {
		super.onResume();

		MainActivity activity = (MainActivity) getActivity();
		activity.toolbar.getMenu().clear();
		activity.setNavigationItem(Constants.NAVIGATION_MINGLERS_INDEX);
		activity.toolbar.setTitle(R.string.minglers);
	}
	
	
	

	@Override
	public void onStart() {
		super.onStart();
		
		// Show the view as loading if initial load has not finished
		if(!ZeppaUserSingleton.getInstance().hasLoadedInitial()){
			pullToRefreshLayout.setRefreshing(true);
		}
		
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_minglers, menu);
	}


	@Override
	public void onMinglersLoaded() {
		pullToRefreshLayout.setRefreshing(false);
		adapter.notifyDataSetChanged();

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = adapter.getItem(position)
				.getToUserIntent(getActivity());
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.slide_left_in,
				R.anim.slide_left_out);

	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.minglers_add){
			Intent toAddFriends = new Intent(getActivity()
					.getApplicationContext(), StartMinglingActivity.class);
			startActivity(toAddFriends);
			getActivity().overridePendingTransition(R.anim.slide_up_in,
					R.anim.hold);
		}
		
	}


	@Override
	public void onRefreshStarted(View view) {
		// TODO Auto-generated method stub
		
	}


}
