package com.minook.zeppa.activity;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
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

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.adapter.InviteListAdapter;
import com.minook.zeppa.adapter.tagadapter.CreateEventTagAdapter;
import com.minook.zeppa.mediator.MyZeppaEventMediator;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.utils.GCalUtils;
import com.minook.zeppa.utils.Utils;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;

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

	private Spinner eventPrivacyField;
	private CheckBox guestsCanInviteField;

	private TextView startDateField;
	private TextView startTimeField;
	private TextView endDateField;
	private TextView endTimeField;

	private TextView addInvitesField;
	private EditText newTagTextField;

	private ImageView addNewTagField;
	private ImageView addLocationField;

	private LinearLayout invitesHolder;

	// Invisible Event Object Variables
	private Calendar startCalendar;
	private Calendar endCalendar;
	private String longLocation;
	private CreateEventTagAdapter tagAdapter;
	private InviteListAdapter invitesAdapter;

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
		invitesHolder = (LinearLayout) findViewById(R.id.neweventactivity_invitesholder);

		newTagTextField = (EditText) findViewById(R.id.neweventactivity_tagtext);
		addNewTagField = (ImageView) findViewById(R.id.neweventactivity_addnewtag);
		addInvitesField = (TextView) findViewById(R.id.neweventactivity_addinvites);

		addLocationField = (ImageView) findViewById(R.id.neweventactivity_exactlocation);

		eventPrivacyField = (Spinner) findViewById(R.id.neweventactivity_eventscope);
		startCalendar = new GregorianCalendar();
		endCalendar = new GregorianCalendar();

		LinearLayout tagHolder = (LinearLayout) findViewById(R.id.neweventactivity_taglineholder);
		tagAdapter = new CreateEventTagAdapter(this, tagHolder);
		invitesAdapter = new InviteListAdapter(this);

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
		eventPrivacyField.setAdapter(spinnerArrayAdapter);

		// Set Button Listeners
		cancelButton.setOnClickListener(this);
		doneButton.setOnClickListener(this);
		addNewTagField.setOnClickListener(this);
		addInvitesField.setOnClickListener(this);
		addLocationField.setOnClickListener(this);

	}

	@Override
	protected void onResume() {
		super.onResume();
		tagAdapter.drawTags();
	}

	// This handles when a user types in the description field

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.neweventactivity_cancel:
			onBackPressed();
			break;

		case R.id.neweventactivity_create:
			startEventInAsync();
			break;

		case R.id.neweventactivity_addinvites:
			showInviteList();
			break;

		case R.id.neweventactivity_addnewtag:
			if (newTagTextField.isEnabled()) {
				tagAdapter.createTagInAsync(newTagTextField);
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

	private void startEventInAsync() {

		ZeppaEvent event = new ZeppaEvent();

		event.setHostId(ZeppaUserSingleton.getInstance().getUserId());
		event.setTitle(titleField.getText().toString());
		event.setDescription(descriptionField.getText().toString());

		event.setPrivacy(Utils.getPrivacy(eventPrivacyField
				.getSelectedItemPosition()));
		event.setDisplayLocation(shortLocationField.getText().toString());
		if (longLocation != null && !longLocation.isEmpty())
			event.setMapsLocation(longLocation); // May be null
		event.setStart(startCalendar.getTimeInMillis());
		event.setEnd(endCalendar.getTimeInMillis());
		event.setTagIds(tagAdapter.getSelectedTagIds());
		event.setInvitedUserIds(invitesAdapter.getInvitedUserIds());
		event.setGuestsMayInvite(Boolean.TRUE);

		if (isValidEvent(event)) {

			ProgressDialog progressDialog = new ProgressDialog(this);
			progressDialog.setTitle(R.string.posting_event);
			progressDialog.setMessage(getResources().getText(
					R.string.one_moment));
			progressDialog.setCancelable(false);
			progressDialog.show();

			Object[] params = { event, progressDialog };

			new AsyncTask<Object, Void, ZeppaEvent>() {

				@Override
				protected ZeppaEvent doInBackground(Object... params) {
					ZeppaEvent event = (ZeppaEvent) params[0];
					ProgressDialog dialog = (ProgressDialog) params[1];
					try {
						// TODO: update this so user specifies if invites are
						// available
						
						Context context = getBaseContext();
						GoogleAccountCredential credential = getGoogleCalendarCredential();
						
						event = GCalUtils.putZeppaEventInCalendar(
								context, event,
								credential);

						event = ZeppaEventSingleton.getInstance()
								.createZeppaEventWithBlocking(
										getGoogleAccountCredential(), event);

						MyZeppaEventMediator myMediator = new MyZeppaEventMediator(
								event);

						ZeppaEventSingleton.getInstance().addMediator(
								myMediator);

					} catch (IOException e) {
						e.printStackTrace();
						
						if(event.getGoogleCalendarEventId() != null){
							try {
								GCalUtils.deleteZeppaEventInCal(event, getGoogleCalendarCredential());
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
						
						event = null;
					}

					dialog.dismiss();

					return event;
				}

				@Override
				protected void onPostExecute(ZeppaEvent result) {
					super.onPostExecute(result);

					if (result != null) {
						onBackPressed();
					} else {
						Toast.makeText(((ZeppaApplication) getApplication()),
								"Error Posting", Toast.LENGTH_LONG)
								.show();
					}

				}

			}.execute(params);
		}

	}

	private void showInviteList() {
		if (ZeppaUserSingleton.getInstance().hasLoadedInitial()) {
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

			inviteList.setAdapter(invitesAdapter);

			dialogBuilder.setView(inviteList);

			Dialog dialog = dialogBuilder.show();

			dialog.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {

					showInviteViews();
				}

			});

		} else {
			Toast.makeText(this, "Loading friends...", Toast.LENGTH_SHORT)
					.show();
		}

	}

	private void showInviteViews() {

		if (invitesAdapter.getCount() > 0) {
			invitesHolder.removeAllViews();

		}

	}

	// private void updateInviteViews(){
	// List<Long> invitedUserIds = invitesAdapter.getInvitedUserManagerIds();
	//
	// for(int i = invitesHolder.getChildCount(); i >=0 ; i--){
	// View inviteView = invitesHolder.getChildAt(i);
	// Long viewUserId = (Long) inviteView.getTag();
	//
	// if(!invitedUserIds.remove(viewUserId)){
	// invitesHolder.removeViewAt(i);
	//
	// }
	//
	// }
	//
	// }

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
		if (startMillis >= endMillis) // start after end
			return false;
		else if (endMillis < currentTime) // ended already
			return false;
		else
			return true;
	}

	private boolean isValidEvent(ZeppaEvent event) {
		// TODO: build a check in here to make sure fields are correct
		return true;
	}

	/*
	 * -------------- Private Classes -------------- NOTES:
	 */

	// Time Picker Class -> Result Method
	public static class TimePickerFragment extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker
			boolean is24HourFormat = getArguments().getBoolean(PICKER_24HR_ARG);
			int hour = getArguments().getInt(PICKER_HOUR);
			int minute = getArguments().getInt(PICKER_MINUTE);

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

			DatePickerDialog picker = new DatePickerDialog(getActivity(),
					(NewEventActivity) getActivity(), year, month, day);

			return picker;
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
