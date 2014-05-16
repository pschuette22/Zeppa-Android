package com.minook.zeppa.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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

import com.minook.zeppa.R;
import com.minook.zeppa.activities.MainActivity;
import com.minook.zeppa.activities.NewEventActivity;
import com.minook.zeppa.adapters.eventadapter.EventListAdapter;
import com.minook.zeppa.adapters.tagadapter.MyTagAdapter;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUser;

public class AccountFragment extends Fragment implements OnClickListener {

	/*
	 * ------------ Private Variables -----------------
	 */
	private ZeppaUser zeppaUser;
	private EventListAdapter adapter;
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
		zeppaUser = ZeppaUserSingleton.getInstance().getUser();


		layout = inflater.inflate(R.layout.fragment_account, container, false);

		userImage = (ImageView) layout.findViewById(R.id.accountfragment_image);
		firstName = (EditText) layout
				.findViewById(R.id.accountfragment_firstname);
		lastName = (EditText) layout
				.findViewById(R.id.accountfragment_lastname);
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
		
		
		firstName.setText(zeppaUser.getGivenName());
		lastName.setText(zeppaUser.getFamilyName());
		phoneNumber.setText(zeppaUser.getPhoneNumber());
		emailAddress.setText(zeppaUser.getEmail());

		addTag.setOnClickListener(this);
		addEvent.setOnClickListener(this);

		setImageInAsync(userImage, zeppaUser);

		
		// Handle tag stuff
		LinearLayout tagHolder = (LinearLayout) layout
				.findViewById(R.id.accountfragment_tagholder);
		tagAdapter = new MyTagAdapter(getActivity(), tagHolder);

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
			startActivity(toNewEvent);
			getActivity().overridePendingTransition(R.anim.slide_up_in,
					R.anim.hold);

			break;

		}

	}

	/*
	 * ------------- Private Methods ---------------
	 */

	private void drawEvents() {

		for (int i = 0; i < adapter.getCount(); i++) {
			eventHolder.addView(adapter.getView(i, null, null));
		}

	}

	private void setImageInAsync(final ImageView imageView, final ZeppaUser user) {

		new AsyncTask<Void, Void, Drawable>() {

			@Override
			protected Drawable doInBackground(Void... params) {
				return ZeppaUserSingleton.getInstance().getUserImage(user);
			}

			@Override
			protected void onPostExecute(Drawable result) {
				super.onPostExecute(result);
				if (result != null) {
					imageView.setImageDrawable(result);
				}
			}

		}.execute();

	}


}
