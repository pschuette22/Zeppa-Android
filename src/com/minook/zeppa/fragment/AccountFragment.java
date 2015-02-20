package com.minook.zeppa.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
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
	private TextView addNewTag;
	private EditText newTagText;

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

		newTagText = (EditText) layout
				.findViewById(R.id.accountfragment_tagtext);
		addNewTag = (TextView) layout
				.findViewById(R.id.accountfragment_addnewtag);
		addNewTag.setOnClickListener(this);

		LinearLayout tagHolder = (LinearLayout) layout
				.findViewById(R.id.accountfragment_tagholder);

		tagAdapter = new MyTagAdapter(
				(AuthenticatedFragmentActivity) getActivity(), tagHolder, null);
		tagAdapter.drawTags();

		eventAdapter = new MyEventsAdapter(
				(AuthenticatedFragmentActivity) getActivity(), eventHolder);

		try {
			eventAdapter.drawEvents();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return layout;
	}

	@Override
	public void onResume() {
		super.onResume();

		MainActivity activity = (MainActivity) getActivity();
		activity.toolbar.getMenu().clear();
		activity.toolbar.inflateMenu(R.menu.menu_account);
		activity.setNavigationItem(Constants.NAVIGATION_ACCOUNT_INDEX);
		activity.toolbar.setTitle(R.string.my_profile);
		
		
		userMediator.setImageWhenReady(userImage);
		displayName.setText(userMediator.getDisplayName());

		try {
			phoneNumber.setText(userMediator.getPrimaryPhoneNumber());
		} catch (NullPointerException e) {
			phoneNumber.setVisibility(View.GONE);
		}
		emailAddress.setText(userMediator.getGmail());

		// Handle tag stuff

		if (!tagAdapter.tagsAreCurrent()) {
			tagAdapter.notifyDataSetChanged();
			tagAdapter.drawTags();
		}

		// Events list

		if (!eventAdapter.isUpToDate()) {
			eventAdapter.notifyDataSetChanged();
			try {
				eventAdapter.drawEvents();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.accountfragment_addnewtag:

			if (newTagText.isEnabled()) {
				tagAdapter.createTagInAsync(newTagText);
			}

			break;

		}

	}


}
