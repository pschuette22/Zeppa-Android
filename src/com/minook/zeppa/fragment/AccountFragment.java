package com.minook.zeppa.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.activity.MainActivity;
import com.minook.zeppa.activity.NewEventActivity;
import com.minook.zeppa.adapter.eventlistadapter.MyEventsAdapter;
import com.minook.zeppa.adapter.tagadapter.MyTagAdapter;
import com.minook.zeppa.mediator.MyZeppaUserMediator;
import com.minook.zeppa.singleton.EventTagSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

public class AccountFragment extends Fragment implements OnClickListener {

	/*
	 * ------------ Private Variables -----------------
	 */
	private MyZeppaUserMediator userManager;
	private MyEventsAdapter eventAdapter;
	private MyTagAdapter tagAdapter;

	private View layout;
	private ImageView userImage;
	private EditText firstName;
	private EditText lastName;
	private TextView phoneNumber;
	private TextView emailAddress;
	private LinearLayout eventHolder;
	private Button addTag;
	private Button addEvent;

	/*
	 * ----------- Override Methods -----------------
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		((MainActivity) getActivity()).setCurrentFragment(this);
		userManager = ZeppaUserSingleton.getInstance().getUserMediator();

		layout = inflater.inflate(R.layout.fragment_account, container, false);

		userImage = (ImageView) layout.findViewById(R.id.accountfragment_image);
		firstName = (EditText) layout
				.findViewById(R.id.accountfragment_firstname);
		phoneNumber = (TextView) layout
				.findViewById(R.id.accountfragment_phonenumber);
		emailAddress = (TextView) layout
				.findViewById(R.id.accountfragment_emailaddress);

		eventHolder = (LinearLayout) layout
				.findViewById(R.id.accountfragment_eventholder);
		addTag = (Button) layout
				.findViewById(R.id.accountfragment_newtagbutton);
		addEvent = (Button) layout
				.findViewById(R.id.accountfragment_neweventbutton);

		
		
//		 firstName.setText(zeppaUser.getGivenName());
//		 lastName.setText(zeppaUser.getFamilyName());
//		 phoneNumber.setText(zeppaUser.getPhoneNumber());
//		 emailAddress.setText(zeppaUser.getEmail());

		addTag.setOnClickListener(this);
		addEvent.setOnClickListener(this);

		// Handle tag stuff
		LinearLayout tagHolder = (LinearLayout) layout
				.findViewById(R.id.accountfragment_tagholder);
		tagAdapter = new MyTagAdapter(
				(AuthenticatedFragmentActivity) getActivity(), tagHolder,
				EventTagSingleton.getInstance().getTags());
		
		// Events list
		
		eventAdapter = new MyEventsAdapter((AuthenticatedFragmentActivity) getActivity(), eventHolder);


		return layout;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		tagAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.accountfragment_newtagbutton:
			final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
					getActivity());
			dialogBuilder.setTitle(R.string.create_tag);
			final EditText tagText = new EditText(getActivity());
			tagText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
			tagText.setHint(R.string.newtag);

			dialogBuilder.setTitle(R.string.create_tag);
			dialogBuilder.setView(tagText);

			dialogBuilder.setPositiveButton(R.string.create,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

						}

					});

			dialogBuilder.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});

			dialogBuilder.create().show();
			break;

		case R.id.accountfragment_neweventbutton:

			Intent toNewEvent = new Intent(getActivity(),
					NewEventActivity.class);

			long nullLong = -1;
			toNewEvent.putExtra(Constants.INTENT_EVENT_STARTTIME, nullLong);
			toNewEvent.putExtra(Constants.INTENT_EVENT_ENDTIME, nullLong);
			getActivity().startActivity(toNewEvent);
			getActivity().overridePendingTransition(R.anim.slide_up_in,
					R.anim.hold);

			break;

		}

	}

}
