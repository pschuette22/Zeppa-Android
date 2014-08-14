package com.minook.zeppa;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import com.minook.zeppa.zeppanotificationendpoint.model.ZeppaNotification;

public class Constants {

	// Global Enumerators
	public static enum EventScope {
		PUBLIC, // Open to unlimited sharing
		PRIVATE, // Host only sharing
		CASUAL, // Friends of host can share
		NO_REPOST
	}

	public static enum ConflictionStatus {
		NO_CONFLICTION, PARTIAL_CONFLICTION, COMPLETE_CONFLICTION, ATTENDING
	}

	public static enum LoadType {
		HOSTED_EVENTS, JOINED_EVENT, WATCHING_EVENT, HELD_EVENTS, NOTIFICATIONS, EVENT_TAGS, FRIENDS, EVENT_REFRESH
	}

	public static enum EventUpdateType {
		SIMPLE_UPDATE, JOINED, LEAVE, WATCH, DELETE, STOP_WATCHING
	}

	public static enum NotificationType {
		FRIEND_REQUEST, FRIEND_ACCEPTED,

		EVENT_RECCOMENDATION, DIRECT_INVITE,

		COMMENT_ON_POST,

		EVENT_CANCELED, EVENT_UPDATED,

		FRIEND_JOINED_EVENT,

		USER_LEAVING, FIND_TIME, TIME_FOUND
	}
	
	public static enum UserDataRelationship {
		IS_USER,
		MINGLING,
		SENT_REQUEST,
		PENDING_REQUEST,
		NOT_CONNECTED,
		NOT_USING,
		DID_EXTEND_BID,
		UNKNOWN
		
	}

	/*
	 * Application - Wide constants
	 */
	public final static double APP_VERSION = 1.0;

	/*
	 * Debug variables
	 */
	public final static boolean IS_CONNECTED = true; 
	

	// Internal Database Constants
	public static final String DB_NAME = "Zeppa_Internal_Database";
	public static final int DB_VERSION = 1;

	/*
	 * ---------------- Preferences -------------------
	 */ 
	// Calendar Objects
	public final static String CAL_NAME_INTERNAL = "Local Zeppa Calendar";
	public final static String CAL_NAME_DISPLAY = "Zeppa Events";

	// Preferences
	public final static String SHARED_PREFS = "Zeppa Shared Preferences";
	public final static String PREFS_VERSION = "Preference Version Code";

	// Account Preferences
	public final static String GOOGLE_ACCOUNT = "Account Email Address";
	public final static String USER_ID = "Zeppa User Id";
	public final static String ZEPPA_INTERNAL_CALENDAR_ID = "Zeppa Internal Calendar Id";
	public final static String ZEPPA_GOOGLE_CALENDAR_ID = "Zeppa Google Calendar Id";
	
	// System Preferences
	public final static String SHOW_TUTORIAL = "Has Seen Initial Tutorial";
	public final static String IS_12HR_FORMAT = "Used 12-hr time format";

	// Notification Preferences
	public final static String PUSH_NOTIFICAIONS = "Send Push Notifications";
	public final static String PN_EVENT_RECCOMENDATION = "Notificaiton With Event Reccomendation";
	public final static String PN_FRIEND_REQUEST = "Notification @ Friend Request";
	public final static String PN_FRIEND_ACCEPT = "Notiification @ Friend Accept";
	public final static String PN_FRIEND_JOINS = "Notification When Friend Joins Event You Joined";
	public final static String PN_EVENT_JOINED = "Notification When Someone Joins Your Event";
	public final static String PN_EVENT_LEFT = "Notification When Friend Leaves your Event";
	public final static String PN_SOUND_ON = "Ring When Received Notification";
	public final static String PN_VIBRARTE_ON = "Vibrate When Received Notification";

	/*
	 * App Engine constants Verify it is safe to hold them here/ considered best
	 * practice
	 */

	public final static String PROJECT_NUMBER = "587859844920";
	
	public final static String REDIRECT_URL = "urn:ietf:wg:oauth:2.0:oob";
	public final static String APP_ENGINE_CLIENT_ID = "587859844920-jiqoh8rn4j8d0941vunu4jfdcl2huv4l.apps.googleusercontent.com";
	public final static String APP_ENGINE_CLIENT_SECRET = "C0L8jW9hLtobNSjIyp1J52gk";

	public final static String APP_ENGINE_AUDIENCE_CODE = "server:client_id:"
			+ APP_ENGINE_CLIENT_ID;
	
	
	/*
	 * Scopes
	 * 
	 * List of the scopes needed for the authentication client
	 */
	
	private final static String SCOPE_PLUS = "https://www.googleapis.com/auth/plus.login";
	private final static String SCOPE_CALENDAR = "https://www.googleapis.com/auth/calendar";
	// Publicly accessed scopes
	public final static String SCOPES = SCOPE_PLUS + "oauth2:server:client_id:" + APP_ENGINE_CLIENT_ID +":api_scope:" + SCOPE_CALENDAR;
	
	

	
	/*
	 * Navigation items are for labeling back stack instances When back button
	 * is pressed, this makes sure that correct nav item is selected
	 */
	public final static String NAVIGATION_HOME = "NavigationHome";
	public final static String NAVIGATION_CONTACTS = "NavigationContacts";
	public final static String NAVIGATION_ACTIVITY = "NavigationActivity";
	public final static String NAVIGATION_ACCOUNT = "NavigationMyAccount";
	public final static String NAVIGATION_FEEDBACK = "NavigationSendFeedback";
	public final static String NAVIGATION_INVITE = "NavigationInvite";

	/*
	 * Constants passed VIA intents
	 */
	public final static String INTENT_BACKEND_CONNECTED = "Backend Connected";
	public final static String INTENT_EVENT_ENDTIME = "New Event End Time";
	public final static String INTENT_EVENT_STARTTIME = "New Event Start Time";
	public final static String INTENT_NOTIFICATIONS = "Launch Notifications";
	public final static String INTENT_ZEPPA_EVENT = "Zeppa Event Passed";
	public final static String INTENT_ZEPPA_EVENT_ID = "Zeppa Event Id Passed";
	public final static String INTENT_ZEPPA_USER = "Zeppa User Passed";
	public final static String INTENT_ZEPPA_USER_ID = "Zeppa User Id Passed";

	/*
	 * Query limit constants
	 * 
	 * THIS MUST STAY CONSISTENT WITH THE ENDPOINT CONSTANTS
	 * 
	 */
	
	public static final int QUERY_LIMIT_EVENT = 10;
	public static final int QUERY_LIMIT_USER = 10;
	public static final int QUERY_LIMIT_TAG = 25;
	public static final int QUERY_LIMIT_COMMENT = 10;
	public static final int QUERY_LIMIT_NOTIFICATIONS = 10;
	public static final int QUERY_LIMIT_RELATIONSHIP = 20;
	
	/*
	 * encoding methods
	 */
	public static String encodeListString(List<String> tempList) {
		StringBuilder stringbuilder = new StringBuilder();
		stringbuilder.append(tempList.get(0));
		if (tempList.size() > 1) {
			for (int i = 1; i < tempList.size(); i++) {
				stringbuilder.append(",");
				stringbuilder.append(tempList.get(i));
			}
		}

		return stringbuilder.toString();
	}

	public static String encodeListLong(ArrayList<Long> longList) {
		StringBuilder stringbuilder = new StringBuilder();
		stringbuilder.append(longList.get(0));
		if (longList.size() > 1) {
			for (int i = 0; i < longList.size(); i++) {
				stringbuilder.append(",");
				stringbuilder.append(longList.get(i));
			}
		}

		return stringbuilder.toString();
	}

	/*
	 * constant methods
	 */

	public static String getDisplayDatesString(long startMillis, long endMillis) {

		StringBuilder result = new StringBuilder();

		// most events
		result.append(getDayString(startMillis));
		result.append(" ");
		result.append(getDisplayTimeString(startMillis));
		result.append(" - ");

		if ((startMillis - endMillis) >= (24 * 60 * 60 * 1000)) {
			result.append(getDateString(endMillis));
			result.append(" ");
		}

		result.append(getDisplayTimeString(endMillis));

		return result.toString();
	}

	public static String getDisplayDateString(long dateMillis) {
		StringBuilder builder = new StringBuilder();

		builder.append(getDayString(dateMillis));
		builder.append(" ");
		builder.append(getDisplayTimeString(dateMillis));
		return builder.toString();
	}

	private static String getDisplayTimeString(long dateMillis) {

		StringBuilder builder = new StringBuilder();
		// TODO: check prefs if they want 24 hr clock

		long timeBetween = (System.currentTimeMillis() - dateMillis);
		if (timeBetween >= 0) {
			if (timeBetween < (1000 * 60)) {
				builder.append("Just now");
			} else if (timeBetween < (3000 * 60)) { // less than 3 minute ago
				builder.append("A few moments ago");
			} else if (timeBetween < (30 * 1000 * 60)) {
				builder.append((timeBetween / (1000 * 60)) + " minutes ago");
			} else {
				builder.append(getTimeAsString(dateMillis));
			}
		} else {
			timeBetween *= -1;
			if (timeBetween < (3 * 1000 * 60)) { // in within 3 minutes
				builder.append("Right Now");
			} else if (timeBetween < (30 * 1000 * 60)) {
				builder.append((timeBetween / (1000 * 60)) + " minutes");
			} else {
				builder.append(getTimeAsString(dateMillis));
			}

		}

		return builder.toString();
	}

	private static String getTimeAsString(long dateMillis) {
		StringBuilder builder = new StringBuilder();

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(dateMillis);
		boolean isAm = true;
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		if (hour > 12) {
			hour -= 12;
			isAm = false;
		} else if (hour == 12) {
			isAm = false;
		}

		if (hour == 0) {
			hour = 12;
		}

		builder.append(hour);
		builder.append(":");

		int minute = cal.get(Calendar.MINUTE);
		builder.append(minute < 10 ? "0" + minute : minute);

		builder.append(isAm ? "am" : "pm");

		return builder.toString();
	}

	// TODO: make this more accurate

	private static String getDayString(long dateMillis) {
		StringBuilder builder = new StringBuilder();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(dateMillis);
		long currentTime = System.currentTimeMillis();
		Calendar today = Calendar.getInstance();
		
		if (cal.get(Calendar.DATE) == today.get(Calendar.DATE)) {
			builder.append("");
		
		} else if (currentTime > dateMillis) {
			// Date is in the past
			if ((currentTime - dateMillis) < (1000 * 60 * 60 * 2)) {
				builder.append("Yesterday");
			} else if ((currentTime - dateMillis) < (1000 * 60 * 60 * 7)) {
				builder.append("Last");
				builder.append(" ");
				builder.append(cal.getDisplayName(Calendar.DAY_OF_WEEK,
						Calendar.LONG, Locale.getDefault()));
			} else {
				builder.append(getDateString(dateMillis));
			}

		} else {
			Calendar tomorrow = Calendar.getInstance();
			tomorrow.add(Calendar.DATE, 1);

			if (cal.get(Calendar.DATE) == tomorrow.get(Calendar.DATE)) {
				builder.append("Tomorrow");
			} else if ((dateMillis - currentTime) < (1000 * 60 * 60 * 24 * 7)) {
				builder.append(cal.getDisplayName(Calendar.DAY_OF_WEEK,
						Calendar.LONG, Locale.getDefault()));
			} else {
				builder.append(getDateString(dateMillis));
			}

		}

		return builder.toString();
	}

	private static String getDateString(long dateMillis) {
		StringBuilder builder = new StringBuilder();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(dateMillis);
		// TODO: check if they want MM/DD or DD/MM

		builder.append((cal.get(Calendar.MONTH) + 1));
		builder.append("/");
		builder.append(cal.get(Calendar.DAY_OF_MONTH));

		builder.append("/");
		builder.append(cal.get(Calendar.YEAR));

		return builder.toString();
	}
	
	
	/*
	 * max call sizes for each item.. do this if i have an issue with query sizes
	 * 
	 * */
	
	private static final int MAX_TRANSACTION_BYTES = 500; // bytes
	private static final int LONG_BYTES = 8; // bytes
	private static final int BOOLEAN_BYTES = 1; // bytes
	
	public static final int ENUM_BYTES = 10;
	
	// defines the expected size of a user to user relationship;

	/*
	 * Comparators
	 */

//	public static final Comparator<ZeppaUser> USER_COMPARATOR = new Comparator<ZeppaUser>() {
//
//		@Override
//		public int compare(ZeppaUser lhs, ZeppaUser rhs) {
//			// TODO Auto-generated method stub
//			return (lhs.getGivenName() + " " + lhs.getFamilyName()).compareToIgnoreCase(
//					rhs.getGivenName() + " " + rhs.getFamilyName());
//		}
//
//	};
//
//	public static final Comparator<ZeppaUser> USER_FINDER_COMPARATOR = new Comparator<ZeppaUser>() {
//
//		@Override
//		public int compare(ZeppaUser lhs, ZeppaUser rhs) {
//			List<Long> requestIds = ZeppaUserSingleton.getInstance().getUser()
//					.getFriendRequestIds();
//			boolean lRequested = requestIds.contains(lhs.getKey().getId());
//			boolean rRequested = requestIds.contains(rhs.getKey().getId());
//			if (lRequested && !rRequested) {
//				return 1;
//			} else if (!lRequested && rRequested) {
//				return -1;
//			} else {
//				return (lhs.getGivenName() + " " + lhs.getFamilyName()).compareToIgnoreCase(
//						rhs.getGivenName() + " " + rhs.getFamilyName());
//			}
//		}
//
//	};
//
//	public static final Comparator<ZeppaEvent> EVENT_COMPARATOR = new Comparator<ZeppaEvent>() {
//
//		@Override
//		public int compare(ZeppaEvent lhs, ZeppaEvent rhs) {
//			// TODO Auto-generated method stub
//			return ((int) (((long) lhs.getStart()) - ((long) rhs.getStart())));
//
//		}
//
//	};

	public static final Comparator<ZeppaNotification> NOTIFICAITON_COMPARATOR = new Comparator<ZeppaNotification>() {

		@Override
		public int compare(ZeppaNotification lhs, ZeppaNotification rhs) {

			return ((int) ((lhs.getSentDate().longValue()) - (rhs.getSentDate()
					.longValue())));

		}

	};

}
