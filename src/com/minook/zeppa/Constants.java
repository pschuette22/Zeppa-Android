package com.minook.zeppa;


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
		UNKNOWN
		
	}

	/*
	 * Application - Wide constants
	 */
	public static final double APP_VERSION = 1.0;

	/*
	 * Debug variables
	 */
	public static final boolean IS_CONNECTED = true; 
	

	// Internal Database Constants
	public static final String DB_NAME = "Zeppa_Internal_Database";
	public static final int DB_VERSION = 1;

	/*
	 * ---------------- Preferences -------------------
	 */ 
	// Calendar Objects
	public static final String CAL_NAME_INTERNAL = "Local Zeppa Calendar";
	public static final String CAL_NAME_DISPLAY = "Zeppa Events";

	// Preferences
	public static final String SHARED_PREFS = "Zeppa Shared Preferences";
	public static final String PREFS_VERSION = "Preference Version Code";

	// Account Preferences
	public static final String GOOGLE_ACCOUNT = "Account Email Address";
	public static final String USER_ID = "Zeppa User Id";
	public static final String ZEPPA_INTERNAL_CALENDAR_ID = "Zeppa Internal Calendar Id";
	public static final String ZEPPA_GOOGLE_CALENDAR_ID = "Zeppa Google Calendar Id";
	
	// System Preferences
	public static final String SHOW_TUTORIAL = "Has Seen Initial Tutorial";
	public static final String IS_12HR_FORMAT = "Used 12-hr time format";

	// Notification Preferences
	public static final String PUSH_NOTIFICAIONS = "Send Push Notifications";
	public static final String PN_EVENT_RECCOMENDATION = "Notificaiton With Event Reccomendation";
	public static final String PN_FRIEND_REQUEST = "Notification @ Friend Request";
	public static final String PN_FRIEND_ACCEPT = "Notiification @ Friend Accept";
	public static final String PN_FRIEND_JOINS = "Notification When Friend Joins Event You Joined";
	public static final String PN_EVENT_JOINED = "Notification When Someone Joins Your Event";
	public static final String PN_EVENT_LEFT = "Notification When Friend Leaves your Event";
	public static final String PN_SOUND_ON = "Ring When Received Notification";
	public static final String PN_VIBRARTE_ON = "Vibrate When Received Notification";

	/*
	 * App Engine constants Verify it is safe to hold them here/ considered best
	 * practice
	 */

	public static final String PROJECT_NUMBER = "587859844920";
	
	public static final String WEB_CLIENT_ID = "587859844920-jiqoh8rn4j8d0941vunu4jfdcl2huv4l.apps.googleusercontent.com";
	public static final String ANDROID_CLIENT_ID = "587859844920-nntv7duprkooi09urrsigsvia4kcu6s5.apps.googleusercontent.com";
	public static final String ANDROID_AUDIENCE = "server:client_id:" + WEB_CLIENT_ID;
	public static final String EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email";
	public static final String API_CLIENT_SCOPES = "oauth2:server:client_id:" + WEB_CLIENT_ID + ":api_scope:" + EMAIL_SCOPE;
	
	
	/*
	 * Scopes
	 * 
	 * List of the scopes needed for the authentication client
	 */

	

	
	/*
	 * Navigation items are for labeling back stack instances When back button
	 * is pressed, this makes sure that correct nav item is selected
	 */
	public static final String NAVIGATION_HOME = "NavigationHome";
	public static final String NAVIGATION_CONTACTS = "NavigationContacts";
	public static final String NAVIGATION_ACTIVITY = "NavigationActivity";
	public static final String NAVIGATION_ACCOUNT = "NavigationMyAccount";
	public static final String NAVIGATION_FEEDBACK = "NavigationSendFeedback";
	public static final String NAVIGATION_INVITE = "NavigationInvite";
	public static final String NAVIGATION_SETTINGS = "NavigationSettings";
	
	
	public static final int NAVIGATION_ACCOUNT_INDEX = 0;
	public static final int NAVIGATION_HOME_INDEX = 1;
	public static final int NAVIGATION_CONTACTS_INDEX = 2;
	public static final int NAVIGATION_FEEDBACK_INDEX = 3;
	public static final int NAVIGATION_SETTINGS_INDEX = 4;

	/*
	 * Constants passed VIA intents
	 */
	public static final String INTENT_BACKEND_CONNECTED = "Backend Connected";
	public static final String INTENT_EVENT_ENDTIME = "New Event End Time";
	public static final String INTENT_EVENT_STARTTIME = "New Event Start Time";
	public static final String INTENT_NOTIFICATIONS = "Launch Notifications";
	public static final String INTENT_ZEPPA_EVENT = "Zeppa Event Passed";
	public static final String INTENT_ZEPPA_EVENT_ID = "Zeppa Event Id Passed";
	public static final String INTENT_ZEPPA_USER = "Zeppa User Passed";
	public static final String INTENT_ZEPPA_USER_ID = "Zeppa User Id Passed";

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
	
	

}
