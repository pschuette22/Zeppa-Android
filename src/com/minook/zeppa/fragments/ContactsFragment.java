package com.minook.zeppa.fragments;

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
import android.widget.ListView;
import android.widget.TextView;

import com.minook.zeppa.R;
import com.minook.zeppa.activities.MainActivity;
import com.minook.zeppa.activities.NewFriendsActivity;
import com.minook.zeppa.adapters.ContactListAdapter;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUser;

public class ContactsFragment extends Fragment {

	// ----------- Global Variables Bank ------------- \\
	// Private
	private View layout;
	private ListView contactList;
	private View loaderView;
	private ContactListAdapter adapter;

	// Constant

	// Debug

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		((MainActivity) getActivity()).setCurrentFragment(this);

		layout = inflater.inflate(R.layout.fragment_contacts, container, false);
		contactList = (ListView) layout.findViewById(R.id.contactsListView);

		List<ZeppaUser> friends = ZeppaUserSingleton.getInstance().getFriends();
		adapter = new ContactListAdapter(getActivity(), friends);

		contactList.setAdapter(adapter);
		contactList.setOnItemClickListener(adapter);

		if (!ZeppaUserSingleton.getInstance().hasLoadedFriends()) {
			loaderView = (View) layout.findViewById(R.id.contacts_loaderview);
			((TextView) loaderView.findViewById(R.id.loaderview_text))
					.setText("Loading Friends...");
			ZeppaUserSingleton.getInstance().setContactsLoader(loaderView);
			loaderView.setVisibility(View.VISIBLE);

		}

		setHasOptionsMenu(true);

		return layout;
	}

	@Override
	public void onResume() {
		super.onResume();
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setTitle(R.string.app_name);

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_contacts, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
		case R.id.action_addcontact:
			Intent toAddFriends = new Intent(getActivity()
					.getApplicationContext(), NewFriendsActivity.class);
			startActivity(toAddFriends);
			getActivity().overridePendingTransition(R.anim.slide_up_in,
					R.anim.hold);

			break;

		case R.id.action_refresh:

			break;

		case R.id.action_help:

			break;

		}

		return true;
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
