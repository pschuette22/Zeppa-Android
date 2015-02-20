package com.minook.zeppa.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.minook.zeppa.Constants;
import com.minook.zeppa.PrefsManager;
import com.minook.zeppa.R;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.activity.MainActivity;
import com.minook.zeppa.runnable.DeleteAccountRunnable;
import com.minook.zeppa.runnable.ThreadManager;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.pschuette.android.calendarlibrary.CalendarData;
import com.pschuette.android.calendarlibrary.CalendarSyncStateAdapter.OnSyncStateChangedListener;
import com.pschuette.android.calendarlibrary.CalendarSyncStateView;

public class SettingsFragment extends Fragment implements OnClickListener,
		OnCheckedChangeListener, OnSyncStateChangedListener {

	private View layout;
	
	// Section 1
	private Switch sendNotificationsSwitch;
	private Switch ringOnNotificationsSwitch;
	private Switch vibrateOnNotificationsSwitch;
	private LinearLayout notificationsSettings;
	
	// Section 2
	private TextView manageIndividualNotifications;
	private LinearLayout individualNotifictionsHolder;
	private Switch notifOnMingleRequestSwitch;
	private Switch notifOnMingleAcceptSwitch;
	private Switch notifOnEventReccomendationSwitch;
	private Switch notifOnEventInviteSwitch;
	private Switch notifOnEventCommentSwitch;
	private Switch notifOnUserJoinSwitch;
	private Switch notifOnUserLeftSwitch;
	private Switch notifOnEventCanceledSwitch;
	
	// Section 3
	private TextView manageSyncedCalendars;
	private LinearLayout calendarSyncHolder;
	private CalendarSyncStateView calendarSyncStateView;
	
	// Section 4
	private TextView logoutButton;
	private TextView deleteButton;
	
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		layout = inflater.inflate(R.layout.fragment_settings, container, false);

		sendNotificationsSwitch = (Switch) layout
				.findViewById(R.id.settingsfragment_notifications);
		boolean sendPushNotifs = PrefsManager.getUserPreference(getActivity()
				.getApplication(), Constants.PUSH_NOTIFICATIONS);
		sendNotificationsSwitch.setChecked(sendPushNotifs);
		sendNotificationsSwitch.setOnCheckedChangeListener(this);

		ringOnNotificationsSwitch = (Switch) layout
				.findViewById(R.id.settingsfragment_ring);
		ringOnNotificationsSwitch.setChecked(PrefsManager.getUserPreference(
				getActivity().getApplication(), Constants.PN_SOUND_ON));
		ringOnNotificationsSwitch.setOnCheckedChangeListener(this);

		vibrateOnNotificationsSwitch = (Switch) layout
				.findViewById(R.id.settingsfragment_vibrate);
		vibrateOnNotificationsSwitch.setChecked(PrefsManager.getUserPreference(
				getActivity().getApplication(), Constants.PN_VIBRARTE_ON));
		vibrateOnNotificationsSwitch.setOnCheckedChangeListener(this);
		
		notificationsSettings = (LinearLayout) layout.findViewById(R.id.settingsfragment_notificationssettings);
		if(PrefsManager.getUserPreference(getActivity().getApplication(), Constants.PUSH_NOTIFICATIONS)){
			notificationsSettings.setVisibility(View.VISIBLE);
		} else {
			notificationsSettings.setVisibility(View.GONE);
		}
		
		manageIndividualNotifications = (TextView) layout.findViewById(R.id.settingsfragment_notificationsbytype);
		manageIndividualNotifications.setOnClickListener(this);
		
		individualNotifictionsHolder = (LinearLayout) layout.findViewById(R.id.settingsfragment_notificationsbytypeholder);
		individualNotifictionsHolder.setVisibility(View.GONE);
		
		notifOnMingleRequestSwitch = (Switch) layout
				.findViewById(R.id.settingsfragment_minglerequest);
		notifOnMingleRequestSwitch.setChecked(PrefsManager.getUserPreference(
				getActivity().getApplication(), Constants.PN_MINGLE_REQUEST));
		notifOnMingleRequestSwitch.setOnCheckedChangeListener(this);

		notifOnMingleAcceptSwitch = (Switch) layout
				.findViewById(R.id.settingsfragment_mingleconfirm);
		notifOnMingleAcceptSwitch.setChecked(PrefsManager.getUserPreference(
				getActivity().getApplication(), Constants.PN_MINGLE_ACCEPT));
		notifOnMingleAcceptSwitch.setOnCheckedChangeListener(this);

		notifOnEventReccomendationSwitch = (Switch) layout
				.findViewById(R.id.settingsfragment_eventrecommendation);
		notifOnEventReccomendationSwitch.setChecked(PrefsManager
				.getUserPreference(getActivity().getApplication(),
						Constants.PN_EVENT_RECOMMENDATION));
		notifOnEventReccomendationSwitch.setOnCheckedChangeListener(this);

		notifOnEventInviteSwitch = (Switch) layout
				.findViewById(R.id.settingsfragment_eventinvited);
		notifOnEventInviteSwitch.setChecked(PrefsManager.getUserPreference(
				getActivity().getApplication(), Constants.PN_EVENT_INVITATION));
		notifOnEventInviteSwitch.setOnCheckedChangeListener(this);

		notifOnEventCommentSwitch = (Switch) layout
				.findViewById(R.id.settingsfragment_comments);
		notifOnEventCommentSwitch.setChecked(PrefsManager.getUserPreference(
				getActivity().getApplication(), Constants.PN_EVENT_COMMENT));
		notifOnEventCommentSwitch.setOnCheckedChangeListener(this);

		notifOnUserJoinSwitch = (Switch) layout
				.findViewById(R.id.settingsfragment_eventjoin);
		notifOnUserJoinSwitch.setChecked(PrefsManager.getUserPreference(
				getActivity().getApplication(), Constants.PN_EVENT_JOINED));
		notifOnUserJoinSwitch.setOnCheckedChangeListener(this);

		notifOnUserLeftSwitch = (Switch) layout
				.findViewById(R.id.settingsfragment_eventleave);
		notifOnUserLeftSwitch.setChecked(PrefsManager.getUserPreference(
				getActivity().getApplication(), Constants.PN_EVENT_LEFT));
		notifOnUserLeftSwitch.setOnCheckedChangeListener(this);

		notifOnEventCanceledSwitch = (Switch) layout
				.findViewById(R.id.settingsfragment_cancel);
		notifOnEventCanceledSwitch.setChecked(PrefsManager.getUserPreference(
				getActivity().getApplication(), Constants.PN_EVENT_CANCELED));
		notifOnEventCanceledSwitch.setOnCheckedChangeListener(this);

		manageSyncedCalendars = (TextView) layout.findViewById(R.id.settingsfragment_syncedcalendars);
		manageSyncedCalendars.setOnClickListener(this);
		
		calendarSyncHolder = (LinearLayout) layout
				.findViewById(R.id.settingsfragment_synccalendarsholder);

		calendarSyncStateView = new CalendarSyncStateView(getActivity(), this);
		calendarSyncHolder.addView(calendarSyncStateView);
		
		calendarSyncHolder.setVisibility(View.GONE);

		logoutButton = (TextView) layout
				.findViewById(R.id.settingsfragment_logout);
		logoutButton.setOnClickListener(this);

		deleteButton = (TextView) layout
				.findViewById(R.id.settingsfragment_delete);
		deleteButton.setOnClickListener(this);

		return layout;
	}

	@Override
	public void onResume() {
		super.onResume();
		MainActivity activity = (MainActivity) getActivity();
		activity.setNavigationItem(Constants.NAVIGATION_SETTINGS_INDEX);
		activity.toolbar.getMenu().clear();
		activity.toolbar.setTitle(R.string.settings);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.settingsfragment_notificationsbytype:
			if(individualNotifictionsHolder.getVisibility() == View.VISIBLE){
				individualNotifictionsHolder.setVisibility(View.GONE);
			} else {
				individualNotifictionsHolder.setVisibility(View.VISIBLE);
			}
			break;
			
		case R.id.settingsfragment_syncedcalendars:
			if(calendarSyncHolder.getVisibility() == View.VISIBLE){
				calendarSyncHolder.setVisibility(View.GONE);
			} else {
				calendarSyncHolder.setVisibility(View.VISIBLE);
			}
			
			break;
		
		case R.id.settingsfragment_logout:
			raiseLogoutDialog();
			break;

		case R.id.settingsfragment_delete:
			raiseDeleteAccountDialog();
			break;
		}

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.settingsfragment_notifications:
			PrefsManager.setUserPreference(getActivity().getApplication(),
					Constants.PUSH_NOTIFICATIONS, isChecked);
			if(isChecked){
				notificationsSettings.setVisibility(View.VISIBLE);
			} else {
				notificationsSettings.setVisibility(View.GONE);
			}
			
			break;

		case R.id.settingsfragment_ring:
			PrefsManager.setUserPreference(getActivity().getApplication(),
					Constants.PN_SOUND_ON, isChecked);
			break;

		case R.id.settingsfragment_vibrate:
			PrefsManager.setUserPreference(getActivity().getApplication(),
					Constants.PN_VIBRARTE_ON, isChecked);
			break;

		case R.id.settingsfragment_minglerequest:
			PrefsManager.setUserPreference(getActivity().getApplication(),
					Constants.PN_MINGLE_REQUEST, isChecked);
			break;

		case R.id.settingsfragment_mingleconfirm:
			PrefsManager.setUserPreference(getActivity().getApplication(),
					Constants.PN_MINGLE_ACCEPT, isChecked);
			break;

		case R.id.settingsfragment_eventrecommendation:
			PrefsManager.setUserPreference(getActivity().getApplication(),
					Constants.PN_EVENT_RECOMMENDATION, isChecked);
			break;

		case R.id.settingsfragment_eventinvited:
			PrefsManager.setUserPreference(getActivity().getApplication(),
					Constants.PN_EVENT_INVITATION, isChecked);
			break;

		case R.id.settingsfragment_comments:
			PrefsManager.setUserPreference(getActivity().getApplication(),
					Constants.PN_EVENT_COMMENT, isChecked);
			break;

		case R.id.settingsfragment_eventjoin:
			PrefsManager.setUserPreference(getActivity().getApplication(),
					Constants.PN_EVENT_JOINED, isChecked);
			break;

		case R.id.settingsfragment_eventleave:
			PrefsManager.setUserPreference(getActivity().getApplication(),
					Constants.PN_EVENT_LEFT, isChecked);
			break;

		case R.id.settingsfragment_cancel:
			PrefsManager.setUserPreference(getActivity().getApplication(),
					Constants.PN_EVENT_CANCELED, isChecked);
			break;

		}

	}

	@Override
	public void onSyncStateChanged(final CalendarData calData,
			final Switch switchView) {
		if (calData.getName().equals("Zeppa") && !calData.isSynced()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Are you sure?");
			builder.setMessage("Unsyncing the Zeppa Calendar will cause Zeppa to not work properly. Are you sure you want to do this?");
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					if (which == DialogInterface.BUTTON_POSITIVE) {
						calData.changeSyncState(getActivity(), true);
						switchView.setChecked(true);
					}
				}
			};

			builder.setNegativeButton("Unsync", dialogClickListener);
			builder.setPositiveButton("Sync Calendar", dialogClickListener);
			builder.show();
		}
	}

	private DialogInterface.OnClickListener deleteDialogListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();

			if (which == DialogInterface.BUTTON_POSITIVE) {

				ThreadManager.execute(new DeleteAccountRunnable(
						(ZeppaApplication) getActivity().getApplication(),
						((AuthenticatedFragmentActivity) getActivity())
								.getGoogleAccountCredential(),
						ZeppaUserSingleton.getInstance().getUserId()
								.longValue()));

				((AuthenticatedFragmentActivity) getActivity()).logout();

			}

		}
	};

	private void raiseDeleteAccountDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Delete Account?");
		builder.setMessage("You can't undo this! Be really really sure you want to delete your account on Zeppa and all information associated with it. Operation may take several moments, please do not close application mid deletion");
		builder.setPositiveButton("Delete Account", deleteDialogListener);
		builder.setNegativeButton("Dismiss", deleteDialogListener);
		builder.show();

	}

	private DialogInterface.OnClickListener logoutDialogListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();

			if (which == DialogInterface.BUTTON_POSITIVE) {

				((AuthenticatedFragmentActivity) getActivity()).logout();
			}

		}
	};

	private void raiseLogoutDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Logout?");
		builder.setMessage("You may miss a bunch of fun activities");
		builder.setPositiveButton("Logout", logoutDialogListener);
		builder.setNegativeButton("Dismiss", logoutDialogListener);
		builder.show();
	}

}
