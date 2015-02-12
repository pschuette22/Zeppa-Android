package com.minook.zeppa.fragment;

import java.util.List;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.activity.MainActivity;
import com.minook.zeppa.activity.StartMinglingActivity;
import com.minook.zeppa.adapter.MinglerListAdapter;
import com.minook.zeppa.mediator.DefaultUserInfoMediator;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton.OnMinglersLoadListener;

public class MinglersFragment extends Fragment implements OnMinglersLoadListener, OnItemClickListener {

	// ----------- Global Variables Bank ------------- \\
	// Private
	private View layout;
	private ListView contactList;
	private MinglerListAdapter adapter;

	// Constant

	// Debug

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		layout = inflater.inflate(R.layout.fragment_minglers, container, false);
		contactList = (ListView) layout.findViewById(R.id.contactsListView);

		setHasOptionsMenu(true);

		return layout;
	}

	@Override
	public void onResume() {
		super.onResume();
		((MainActivity) getActivity()).setNavigationItem(Constants.NAVIGATION_MINGLERS_INDEX);

		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setTitle(R.string.minglers );
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		List<DefaultUserInfoMediator> friendInfoManagers = ZeppaUserSingleton
				.getInstance().getMinglerMediators();
		adapter = new MinglerListAdapter((AuthenticatedFragmentActivity) getActivity(), friendInfoManagers);
		contactList.setAdapter(adapter);
		contactList.setOnItemClickListener(this);

		ZeppaUserSingleton.getInstance().registerLoadListener(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		ZeppaUserSingleton.getInstance().registerLoadListener(this);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_minglers, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
		case R.id.action_addcontact:
			Intent toAddFriends = new Intent(getActivity()
					.getApplicationContext(), StartMinglingActivity.class);
			startActivity(toAddFriends);
			getActivity().overridePendingTransition(R.anim.slide_up_in,
					R.anim.hold);

			return true;


		}

		return false;
	}

	@Override
	public void onMinglersLoaded() {
		adapter.notifyDataSetChanged();
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = adapter.getItem(position).getToUserIntent(getActivity());
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
		
	}

	/*
	 * ----------------- MY METHODS ----------------------- NOTES:
	 */

	// private void refreshContacts(){
	//
	//
	//
	// }

}
