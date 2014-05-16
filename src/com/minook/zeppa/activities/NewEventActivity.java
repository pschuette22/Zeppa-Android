package com.minook.zeppa.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.adapters.ContactListAdapter;
import com.minook.zeppa.adapters.tagadapter.NewEventTagAdapter;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUser;

public class NewEventActivity extends AuthenticatedFragmentActivity implements
		OnClickListener, TimePickerDialog.OnTimeSetListener,
		DatePickerDialog.OnDateSetListener {

	/*
	 * -------------- Activity Pieces -------------------
	 */

	private final String TAG = "NewEventActivity";

	// FINAL
	private final static String PICKER_24HR_ARG = "Picker is 24hr Format";
	private final static String PICKER_HOUR = "Picker Hour Argument";
	private final static String PICKER_MINUTE = "Picker Minute Argument";
	private final static String PICKER_DAY = "Picker Minute Argument";
	private final static String PICKER_MONTH = "Picker Month Argument";
	private final static String PICKER_YEAR = "Picker Year Argument";

	// UI Variables

	private Button cancelButton;
	private Button doneButton;

	private EditText titleField;
	private EditText descriptionField;
	private EditText shortLocationField;

	private Spinner eventScopeField;
	private TextView startDateField;
	private TextView startTimeField;
	private TextView endDateField;
	private TextView endTimeField;

	private TextView addInvitesField;
	private EditText newTagTextField;

	private ImageView addNewTagField;
	private ImageView addLocationField;

	// Invisible Event Object Variables
	private Calendar startCalendar;
	private Calendar endCalendar;
	private String longLocation;
	private NewEventTagAdapter tagAdapter;
	private List<Long> invitedFriendIds;

	// Picker Variables:
	private boolean is12HourFormat;
	private boolean isStartCalendar;

	/*
	 * --------------- Override Methods ---------------- NOTES:
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		getActionBar().hide();

		setContentView(R.layout.activity_newevent);

		// Get UI elemets
		titleField = (EditText) findViewById(R.id.neweventactivity_title);
		descriptionField = (EditText) findViewById(R.id.neweventactivity_description);
		shortLocationField = (EditText) findViewById(R.id.neweventactivity_shortlocation);
		startDateField = (TextView) findViewById(R.id.neweventactivity_startdate);
		startTimeField = (TextView) findViewById(R.id.neweventactivity_starttime);
		endDateField = (TextView) findViewById(R.id.neweventactivity_enddate);
		endTimeField = (TextView) findViewById(R.id.neweventactivity_endtime);
		doneButton = (Button) findViewById(R.id.neweventactivity_create);
		cancelButton = (Button) findViewById(R.id.neweventactivity_cancel);

		newTagTextField = (EditText) findViewById(R.id.neweventactivity_tagtext);
		addNewTagField = (ImageView) findViewById(R.id.neweventactivity_addnewtag);
		addInvitesField = (TextView) findViewById(R.id.neweventactivity_addinvites);

		addLocationField = (ImageView) findViewById(R.id.neweventactivity_exactlocation);

		eventScopeField = (Spinner) findViewById(R.id.neweventactivity_eventscope);
		startCalendar = new GregorianCalendar();
		endCalendar = new GregorianCalendar();

		
		LinearLayout tagHolder = (LinearLayout)findViewById(R.id.neweventactivity_taglineholder);
		tagAdapter = new NewEventTagAdapter(this, tagHolder);
		invitedFriendIds = new ArrayList<Long>();

		// Initial Settings
		SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFS,
				MODE_PRIVATE);

		is12HourFormat = prefs.getBoolean(Constants.IS_12HR_FORMAT, true);

		// Set Default Times

		setDefaultCalendarTimes(
				getIntent().getExtras().getLong(
						Constants.INTENT_EVENT_STARTTIME), getIntent()
						.getExtras().getLong(Constants.INTENT_EVENT_ENDTIME));

		startDateField.setOnClickListener(this);
		startTimeField.setOnClickListener(this);
		endDateField.setOnClickListener(this);
		endTimeField.setOnClickListener(this);

		// Set Scope Spinner
		String[] spinnerArray = getResources().getStringArray(
				R.array.event_types);
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_dropdown_item,
				spinnerArray);
		eventScopeField.setAdapter(spinnerArrayAdapter);

		// Set Button Listeners
		cancelButton.setOnClickListener(this);
		doneButton.setOnClickListener(this);
		addNewTagField.setOnClickListener(this);
		addInvitesField.setOnClickListener(this);
		addLocationField.setOnClickListener(this);


		return;
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	// This handles when a user types in the description field

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.neweventactivity_cancel:
			onBackPressed();
			break;

		case R.id.neweventactivity_create:
			startEventInAcync();
			break;

		case R.id.neweventactivity_addinvites:
			showInviteList();
			break;

		case R.id.neweventactivity_addnewtag:
			String newTagText = newTagTextField.getText().toString();
			if(tagAdapter.createdTagInAsync(newTagText)){
				newTagTextField.clearComposingText();
			}

			break;
		case R.id.neweventactivity_exactlocation:
			AlertDialog.Builder addressDialog = new AlertDialog.Builder(this);
			final EditText addressField = new EditText(this);
			if (longLocation == null || longLocation.isEmpty()) {
				addressField.setHint(R.string.location_long);
			} else {
				addressField.setText(longLocation);
			}
			addressDialog.setView(addressField);
			addressDialog.setPositiveButton(R.string.done,
					new Dialog.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							longLocation = addressField.getText().toString();
							dialog.dismiss();
						}

					});
			addressDialog.setTitle(R.string.location_long_title);
			addressDialog.create().show();

			break;

		case R.id.neweventactivity_startdate:
			isStartCalendar = true;
			DialogFragment startDateFragment = new DatePickerFragment();
			Bundle args1 = new Bundle();

			args1.putInt(PICKER_DAY, startCalendar.get(Calendar.DAY_OF_MONTH));
			args1.putInt(PICKER_MONTH, startCalendar.get(Calendar.MONTH));
			args1.putInt(PICKER_YEAR, startCalendar.get(Calendar.YEAR));

			startDateFragment.setArguments(args1);
			startDateFragment.show(getSupportFragmentManager(),
					"startDatePicker");
			break;

		case R.id.neweventactivity_starttime:
			isStartCalendar = true;
			DialogFragment startTimeFragment = new TimePickerFragment();
			Bundle args2 = new Bundle();
			args2.putInt(PICKER_MINUTE, startCalendar.get(Calendar.MINUTE));
			args2.putInt(PICKER_HOUR, startCalendar.get(Calendar.HOUR_OF_DAY));
			args2.putBoolean(PICKER_24HR_ARG, !is12HourFormat);

			startTimeFragment.setArguments(args2);
			startTimeFragment.show(getSupportFragmentManager(),
					"startTimePicker");
			break;

		case R.id.neweventactivity_enddate:
			isStartCalendar = false;
			DialogFragment endDateFragment = new DatePickerFragment();
			Bundle args3 = new Bundle();

			args3.putInt(PICKER_DAY, endCalendar.get(Calendar.DAY_OF_MONTH));
			args3.putInt(PICKER_MONTH, endCalendar.get(Calendar.MONTH));
			args3.putInt(PICKER_YEAR, endCalendar.get(Calendar.YEAR));

			endDateFragment.setArguments(args3);
			endDateFragment.show(getSupportFragmentManager(), "endDatePicker");
			break;

		case R.id.neweventactivity_endtime:
			isStartCalendar = false;
			DialogFragment endTimeFragment = new TimePickerFragment();
			Bundle args4 = new Bundle();
			args4.putInt(PICKER_MINUTE, endCalendar.get(Calendar.MINUTE));
			args4.putInt(PICKER_HOUR, endCalendar.get(Calendar.HOUR_OF_DAY));
			args4.putBoolean(PICKER_24HR_ARG, !is12HourFormat);

			endTimeFragment.setArguments(args4);
			endTimeFragment.show(getSupportFragmentManager(), "endTimePicker");
			break;
		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.hold, R.anim.slide_down_out);
	}

	/*
	 * ---------------- Public Methods --------------------
	 */

	

	/*
	 * -------------- Private Methods --------------------
	 */
	
	private void startEventInAcync() {

		// TODO: show progress dialog
		final ZeppaUserSingleton userSingleton = ZeppaUserSingleton
				.getInstance();
		final ZeppaEventSingleton eventSingleton = ZeppaEventSingleton
				.getInstance();

		ZeppaEvent event = eventSingleton.newEventInstance();

		event.setTitle(titleField.getText().toString());
		event.setHostId(userSingleton.getUserId());
		event.setEventScope(eventScopeField.getSelectedItemPosition());
		event.setDescription(descriptionField.getText().toString());
		event.setShortLocation(shortLocationField.getText().toString());
		if (longLocation != null)
			event.setExactLocation(longLocation); // May be null
		event.setStart(startCalendar.getTimeInMillis());
		event.setEnd(endCalendar.getTimeInMillis());

		if (isValidEvent(event)) {
			List<Long> tagIds = tagAdapter.getSelectedTagIds();

			event.setTagIds(tagIds);
			event.setUsersInvited(invitedFriendIds);

			ZeppaEvent[] params = { event };

			final ProgressDialog progressDialog = new ProgressDialog(this);
			progressDialog.setTitle(R.string.posting_event);
			progressDialog.setMessage(getResources().getText(
					R.string.one_moment));
			progressDialog.setCancelable(false);
			progressDialog.show();

			new AsyncTask<ZeppaEvent, Void, Boolean>() {

				@Override
				protected Boolean doInBackground(ZeppaEvent... params) {
					ZeppaEvent event = params[0];
					return eventSingleton.createZeppaEvent(
							getApplicationContext(), getCredential(), event,
							getContentResolver());
				}

				@Override
				protected void onPostExecute(Boolean success) {
					super.onPostExecute(success);

					progressDialog.dismiss();
					if (success) {
						
						onBackPressed();
					} else {
						Toast.makeText(((ZeppaApplication) getApplication()),
								"Error Posting Event", Toast.LENGTH_LONG)
								.show();
					}

				}

			}.execute(params);
		}

	}

	private void showInviteList() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle(R.string.invite_list);
		dialogBuilder.setNeutralButton(R.string.dismiss,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}

				});

		ListView inviteList = new ListView(this);

		InviteAdapter adapter = new InviteAdapter();
		inviteList.setAdapter(adapter);

		dialogBuilder.setView(inviteList);

		Dialog dialog = dialogBuilder.show();

		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				showInviteViews();
			}

		});

	}

	private void showInviteViews() {
		ZeppaUserSingleton userSingleton = ZeppaUserSingleton.getInstance();
		LinearLayout invitesHolder = (LinearLayout) findViewById(R.id.neweventactivity_invitesholder);
		ArrayList<ZeppaUser> changeChecker = new ArrayList<ZeppaUser>();
		if (invitesHolder.getChildCount() > 0) {
			for (int i = 0; i < invitesHolder.getChildCount(); i++) {
				ZeppaUser zeppaUser = (ZeppaUser) invitesHolder.getChildAt(i)
						.getTag();
				changeChecker.add(zeppaUser);
			}
		}

		if (!invitedFriendIds.isEmpty()) {
			ArrayList<ZeppaUser> attending = new ArrayList<ZeppaUser>();
			for (Long friendId : invitedFriendIds) {
				attending.add(userSingleton.getUserById(friendId));
			}

			if (!changeChecker.isEmpty()
					&& (attending.size() == changeChecker.size())) {

				for (ZeppaUser user : attending) {
					changeChecker.remove(user);
				}

				if (changeChecker.isEmpty()) {
					return;
				}

			}
			invitesHolder.removeAllViews();

			Collections.sort(attending, Constants.USER_COMPARATOR);
			ContactListAdapter contactsAdapter = new ContactListAdapter(this,
					attending);
			View v = null;
			for (int i = 0; i < contactsAdapter.getCount(); i++) {
				v = contactsAdapter.getView(i, v, null);
				invitesHolder.addView(v);
				v.setOnClickListener(contactsAdapter);
			}
		} else {
			invitesHolder.removeAllViews();
		}

	}


	private String dateToString(Calendar cal) {

		Calendar instance = Calendar.getInstance();
		int instanceInt = getDateInt(instance);
		int calInt = getDateInt(cal);

		if (calInt == instanceInt) {
			return getResources().getString(R.string.today);
		}

		instance.add(Calendar.DATE, 1);
		if (getDateInt(instance) == calInt) {
			return getResources().getString(R.string.tomorrow);
		}

		instance.add(Calendar.DATE, -1);

		StringBuilder date = new StringBuilder();
		Locale dl = Locale.getDefault();

		date.append(cal
				.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, dl)
				+ ", ");
		date.append(cal.getDisplayName(Calendar.MONTH, Calendar.LONG, dl) + " ");
		date.append(cal.get(Calendar.DAY_OF_MONTH));

		int year = cal.get(Calendar.YEAR);
		if (year > instance.get(Calendar.YEAR)) {
			date.append(" - " + year);
		}

		return date.toString();
	}

	private String timeToString(Calendar cal) {

		StringBuilder time = new StringBuilder();
		Locale dl = Locale.getDefault();

		int hour = cal.get(Calendar.HOUR_OF_DAY);

		if (is12HourFormat && hour > 12) {
			hour -= 12;
		} else if (is12HourFormat && hour == 0) {
			hour = 12;
		}

		int minute = cal.get(Calendar.MINUTE);
		time.append(hour + ":");
		time.append((minute >= 10 ? minute + "" : "0" + minute) + " ");
		if (is12HourFormat)
			time.append(cal.getDisplayName(Calendar.AM_PM, Calendar.SHORT, dl));

		return time.toString();
	}

	private int getDateInt(Calendar cal) {
		// yyyymmdd
		int dateInt = 0;
		dateInt += (cal.get(Calendar.YEAR) * 10000);
		dateInt += (cal.get(Calendar.MONTH) * 100);
		dateInt += (cal.get(Calendar.DAY_OF_MONTH));

		return dateInt;
	}

	

	private void setDefaultCalendarTimes(long startMillis, long endMillis) {
		Calendar cal = Calendar.getInstance();
		if (startMillis < 0) {
			cal.add(Calendar.SECOND, (cal.get(Calendar.SECOND) * -1));
			int buffer = cal.get(Calendar.MINUTE) % 5;
			cal.add(Calendar.MINUTE, (5 - buffer));
		} else {
			cal.setTimeInMillis(startMillis);
		}

		startCalendar.setTime(cal.getTime());
		String startDateString = dateToString(cal);
		String startTimeString = timeToString(cal);

		cal.add(Calendar.HOUR, 1);

		endCalendar.setTime(cal.getTime());
		
		String endDateString = dateToString(cal);
		String endTimeString = timeToString(cal);

		startDateField.setText(startDateString);
		startTimeField.setText(startTimeString);
		endDateField.setText(endDateString);
		endTimeField.setText(endTimeString);
		
	}

	private boolean isValidTime(long startMillis, long endMillis) {
		long currentTime = System.currentTimeMillis();
		if (startMillis > endMillis) // start after end
			return false;
		else if (endMillis < currentTime) // ended already
			return false;
		else
			return true;
	}

	private boolean isValidEvent(ZeppaEvent event) {
		AlertDialog.Builder error;

		if (event.getTitle().isEmpty()) { // Throw no title error
			error = new AlertDialog.Builder(this);
			error.setTitle(R.string.error_newevent);
			error.setMessage(R.string.error_newevent_title);
			error.setNeutralButton(R.string.dismiss,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			error.show();

			return false;
		} else if (event.getShortLocation().isEmpty()
				&& event.getExactLocation().isEmpty()) { // Throw no location
															// error
			error = new AlertDialog.Builder(this);
			error.setTitle(R.string.error_newevent);
			error.setMessage(R.string.error_newevent_location);
			error.setNeutralButton(R.string.dismiss,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			error.show();

			return false;
		} else if (!isValidTime(event.getStart().longValue(), event.getEnd()
				.longValue())) { // Throw bad time error

			error = new AlertDialog.Builder(this);
			error.setTitle(R.string.error_newevent);
			error.setMessage(R.string.error_newevent_time);
			error.setNeutralButton(R.string.dismiss,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			error.show();

			return false;
		}

		return true;
	}

	/*
	 * -------------- Private Classes -------------- NOTES:
	 */

	private class InviteAdapter extends BaseAdapter implements OnClickListener {

		private List<ZeppaUser> friends;

		public InviteAdapter() {
			this.friends = ZeppaUserSingleton.getInstance().getFriends();
		}

		@Override
		public int getCount() {
			// TODO Hook up to contacts array list.
			return friends.size();
		}

		@Override
		public ZeppaUser getItem(int position) {
			// TODO Auto-generated method stub
			return friends.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.view_invitelist_item, null, false);
			}

			ZeppaUser user = getItem(position);

			CheckBox inviteState = (CheckBox) convertView
					.findViewById(R.id.inviteitem_checkbox);
			inviteState.setChecked(invitedFriendIds.contains(user.getKey()
					.getId()));
			inviteState.setClickable(false);

			TextView userName = (TextView) convertView
					.findViewById(R.id.inviteitem_contactname);
			ImageView userImage = (ImageView) convertView
					.findViewById(R.id.inviteitem_contactimage);

			userName.setText(user.getDisplayName());

			setImageInAsync(userImage, user);
			convertView.setOnClickListener(this);
			convertView.setTag(user);

			return convertView;
		}

		@Override
		public void onClick(View v) {
			CheckBox inviteState = (CheckBox) v
					.findViewById(R.id.inviteitem_checkbox);

			ZeppaUser user = (ZeppaUser) v.getTag();
			if (inviteState.isChecked()) {
				invitedFriendIds.remove(user.getKey().getId());
				inviteState.setChecked(false);
			} else {
				invitedFriendIds.add(user.getKey().getId());
				inviteState.setChecked(true);
			}

		}

		private void setImageInAsync(final ImageView imageView,
				final ZeppaUser user) {

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

	// Time Picker Class -> Result Method
	public static class TimePickerFragment extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker
			boolean is24HourFormat = getArguments().getBoolean(PICKER_24HR_ARG);
			int hour = getArguments().getInt(PICKER_HOUR);
			int minute = getArguments().getInt(PICKER_MINUTE);

			Log.d("TAG", "args = " + hour + ":" + minute
					+ (is24HourFormat ? " 24HR" : " 12HR"));

			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(),
					(NewEventActivity) getActivity(), hour, minute,
					is24HourFormat);
		}

	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

		if (isStartCalendar) {
			startCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
			startCalendar.set(Calendar.MINUTE, minute);
			startTimeField.setText(timeToString(startCalendar));
			if (endCalendar.before(startCalendar)) {
				endCalendar.set(Calendar.HOUR_OF_DAY, (hourOfDay + 1));
				endCalendar.set(Calendar.MINUTE, minute);
				endTimeField.setText(timeToString(endCalendar));
				endDateField.setText(dateToString(endCalendar));
			}

		} else {
			endCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
			endCalendar.set(Calendar.MINUTE, minute);
			endTimeField.setText(timeToString(endCalendar));
			if (endCalendar.before(startCalendar)) {
				startCalendar.set(Calendar.HOUR_OF_DAY, (hourOfDay - 1));
				startCalendar.set(Calendar.MINUTE, minute);
				startTimeField.setText(timeToString(startCalendar));
				startDateField.setText(dateToString(startCalendar));

			}

		}

	}

	// Date Picker Class -> Result Method
	public static class DatePickerFragment extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			int year = getArguments().getInt(PICKER_YEAR);
			int month = getArguments().getInt(PICKER_MONTH);
			int day = getArguments().getInt(PICKER_DAY);

			return new DatePickerDialog(getActivity(),
					(NewEventActivity) getActivity(), year, month, day);
		}

	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		if (isStartCalendar) {
			startCalendar.set(Calendar.YEAR, year);
			startCalendar.set(Calendar.MONTH, monthOfYear);
			startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			startDateField.setText(dateToString(startCalendar));
			if (endCalendar.before(startCalendar)) {
				endCalendar.set(Calendar.YEAR, year);
				endCalendar.set(Calendar.MONTH, monthOfYear);
				endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

				if (endCalendar.before(startCalendar)) {
					endCalendar.set(Calendar.HOUR_OF_DAY,
							startCalendar.get(Calendar.HOUR_OF_DAY) + 1);
				}

				endTimeField.setText(timeToString(endCalendar));
				endDateField.setText(dateToString(endCalendar));
			}

		} else {
			endCalendar.set(Calendar.YEAR, year);
			endCalendar.set(Calendar.MONTH, monthOfYear);
			endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			endDateField.setText(dateToString(endCalendar));
			if (endCalendar.before(startCalendar)) {
				startCalendar.set(Calendar.YEAR, year);
				startCalendar.set(Calendar.MONTH, monthOfYear);
				startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

				if (endCalendar.before(startCalendar)) {
					startCalendar.set(Calendar.HOUR_OF_DAY,
							endCalendar.get(Calendar.HOUR_OF_DAY) - 1);
				}

				startTimeField.setText(timeToString(startCalendar));
				startDateField.setText(dateToString(startCalendar));
			}

		}

	}

}
