package com.minook.zeppa.fragment;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.activity.MainActivity;
import com.minook.zeppa.adapter.eventlistadapter.MyEventsAdapter;
import com.minook.zeppa.adapter.tagadapter.MyTagAdapter;
import com.minook.zeppa.mediator.MyZeppaUserMediator;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

public class AccountFragment extends Fragment implements OnClickListener {

	/*
	 * ------------ Private Variables -----------------
	 */
	private MyZeppaUserMediator userMediator;
	private MyEventsAdapter eventAdapter;
	private MyTagAdapter tagAdapter;

	private View layout;
	private ImageView userImage;
	private TextView displayName;
	private TextView phoneNumber;
	private TextView emailAddress;
	private LinearLayout eventHolder;

	/*
	 * ----------- Override Methods -----------------
	 */
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		userMediator = ZeppaUserSingleton.getInstance().getUserMediator();
		layout = inflater.inflate(R.layout.fragment_account, container, false);

		userImage = (ImageView) layout.findViewById(R.id.accountfragment_image);
		displayName = (TextView) layout.findViewById(R.id.accountfragment_name);
		phoneNumber = (TextView) layout
				.findViewById(R.id.accountfragment_phonenumber);
		emailAddress = (TextView) layout
				.findViewById(R.id.accountfragment_emailaddress);

		eventHolder = (LinearLayout) layout
				.findViewById(R.id.accountfragment_eventholder);

		userMediator.setImageWhenReady(userImage);
		displayName.setText(userMediator.getDisplayName());
		phoneNumber.setText(userMediator.getPrimaryPhoneNumber());
		emailAddress.setText(userMediator.getGmail());

		// Handle tag stuff
		LinearLayout tagHolder = (LinearLayout) layout
				.findViewById(R.id.accountfragment_tagholder);
		tagAdapter = new MyTagAdapter(
				(AuthenticatedFragmentActivity) getActivity(), tagHolder, null);
		tagAdapter.drawTags();
		
		// Events list
		
		eventAdapter = new MyEventsAdapter((AuthenticatedFragmentActivity) getActivity(), eventHolder);
		eventAdapter.drawEvents();

		return layout;
	}
	
	
	

	@Override
	public void onResume() {
		super.onResume();
		
		((MainActivity) getActivity()).setNavigationItem(Constants.NAVIGATION_ACCOUNT_INDEX);
	
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setTitle(R.string.my_profile);
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
	

		}

	}

}
