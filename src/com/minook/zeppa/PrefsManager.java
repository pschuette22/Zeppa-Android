package com.minook.zeppa;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsManager {

	private static SharedPreferences getSharedPreferences(Context context) {
		return context.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
	}

	public static String getLoggedInEmail(Context context) {
		return getSharedPreferences(context).getString(
				Constants.LOGGED_IN_ACCOUNT, null);
	}

	public static boolean setLoggedInAccountEmail(Context context, String email) {
		return getSharedPreferences(context).edit()
				.putString(Constants.LOGGED_IN_ACCOUNT, email).commit();
	}

	public static Long getLoggedInUserId(Context context) {
		return getSharedPreferences(context).getLong(
				Constants.LOGGED_IN_USER_ID, -1);
	}

	public static boolean setLoggedInUserId(Context context, Long userId) {
		return getSharedPreferences(context).edit()
				.putLong(Constants.LOGGED_IN_USER_ID, userId).commit();
	}

	public static boolean getUserPreference(Context context,
			String preferenceConstant) {
		String loggedInEmail = getLoggedInEmail(context);
		if (loggedInEmail == null || loggedInEmail.isEmpty()) {
			return false;
		} else {
			return getSharedPreferences(context).getBoolean(
					loggedInEmail + preferenceConstant, false);
		}
	}

	public static boolean setUserPreference(Context context,
			String preferenceConstant, boolean value) {
		String loggedInEmail = getLoggedInEmail(context);
		if (loggedInEmail == null || loggedInEmail.isEmpty()) {
			return false;
		} else {
			return getSharedPreferences(context).edit()
					.putBoolean(loggedInEmail + preferenceConstant, value)
					.commit();
		}
	}

	public static boolean containsUserPreference(Context context,
			String preferenceConstant) {
		String loggedInEmail = getLoggedInEmail(context);
		if (loggedInEmail == null || loggedInEmail.isEmpty()) {
			return false;
		} else {
			return getSharedPreferences(context).contains(
					loggedInEmail + preferenceConstant);
		}
	}

	public static void setBaseNotificationPreferences(Context context) {

		/*
		 * Set Push Notifications
		 * */
		if (!containsUserPreference(context, Constants.PUSH_NOTIFICATIONS)) {
			setUserPreference(context, Constants.PUSH_NOTIFICATIONS, true);
		}

		if (!containsUserPreference(context, Constants.PN_SOUND_ON)) {
			setUserPreference(context, Constants.PN_SOUND_ON, true);
		}

		if (!containsUserPreference(context, Constants.PN_VIBRARTE_ON)) {
			setUserPreference(context, Constants.PN_VIBRARTE_ON, true);
		}

		if (!containsUserPreference(context, Constants.PN_MINGLE_ACCEPT)) {
			setUserPreference(context, Constants.PN_MINGLE_ACCEPT, true);
		}

		if (!containsUserPreference(context, Constants.PN_MINGLE_REQUEST)) {
			setUserPreference(context, Constants.PN_MINGLE_REQUEST, true);
		}

		if (!containsUserPreference(context, Constants.PN_EVENT_RECOMMENDATION)) {
			setUserPreference(context, Constants.PN_EVENT_RECOMMENDATION, true);
		}

		if (!containsUserPreference(context, Constants.PN_EVENT_INVITATION)) {
			setUserPreference(context, Constants.PN_EVENT_INVITATION, true);
		}

		if (!containsUserPreference(context, Constants.PN_EVENT_COMMENT)) {
			setUserPreference(context, Constants.PN_EVENT_COMMENT, true);
		}

		if (!containsUserPreference(context, Constants.PN_EVENT_JOINED)) {
			setUserPreference(context, Constants.PN_EVENT_JOINED, true);
		}

		if (!containsUserPreference(context, Constants.PN_EVENT_LEFT)) {
			setUserPreference(context, Constants.PN_EVENT_LEFT, true);
		}

		if (!containsUserPreference(context, Constants.PN_EVENT_CANCELED)) {
			setUserPreference(context, Constants.PN_EVENT_CANCELED, true);
		}

		/*
		 * Set Showcase Preferences 
		 */
		if(!containsUserPreference(context, Constants.SC_NAVIGATIONBAR)) {
			setUserPreference(context, Constants.SC_NAVIGATIONBAR, false);
		}
		
	}

}
