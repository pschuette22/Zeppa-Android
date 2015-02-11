package com.minook.zeppa;



public class Constants {
	
	/*
	 * Software versions. Update with release
	 * Current release: 1.0.0
	 * */
	
	public static final int VERSION_CODE = 1;
	public static final int UPDATE_CODE = 0;
	public static final int BUGFIX_CODE = 0;
	public static final String APP_RELEASE_CODE = VERSION_CODE + "." + UPDATE_CODE +"." + BUGFIX_CODE;
	


	public static enum NotificationType {
		FRIEND_REQUEST, FRIEND_ACCEPTED,

		EVENT_RECCOMENDATION, DIRECT_INVITE,

		COMMENT_ON_POST,

		EVENT_CANCELED, EVENT_UPDATED,

		FRIEND_JOINED_EVENT,

		USER_LEAVING, FIND_TIME, TIME_FOUND
	}


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
	public static final String LOGGED_IN_ACCOUNT = "Account Email Address";
	public static final String LOGGED_IN_USER_ID = "Account User Id";
	

	// Notification Preferences
	public static final String PUSH_NOTIFICATIONS = "-PushNotifications";
	public static final String PN_SOUND_ON = "-RingOnNotifications";
	public static final String PN_VIBRARTE_ON = "-VibrateOnNotifications";
	
	public static final String PN_MINGLE_REQUEST = "-MingleRequestNotification";
	public static final String PN_MINGLE_ACCEPT = "-MingleAcceptNotification";
	
	public static final String PN_EVENT_RECOMMENDATION = "-EventReccomendationNotification";
	public static final String PN_EVENT_INVITATION = "-EventInviteNotification";
	public static final String PN_EVENT_COMMENT = "-EventCommentNotification";
	public static final String PN_EVENT_JOINED = "-EventJoinNotification";
	public static final String PN_EVENT_LEFT = "-EventLeaveNotification";
	public static final String PN_EVENT_CANCELED ="-EventCanceledNotification";


	
	/*
	 * App Engine constants Verify it is safe to hold them here/ considered best
	 * practice
	 */

	public static final String PROJECT_NUMBER = "587859844920";
	public static final String PROJECT_ID = "zeppa-cloud-1821";
	
	public static final String APP_ENGINE_CLIENT_ID = "587859844920.project.googleusercontent.com";
	public static final String WEB_CLIENT_ID = "587859844920-jiqoh8rn4j8d0941vunu4jfdcl2huv4l.apps.googleusercontent.com";
	public static final String ANDROID_AUDIENCE = "server:client_id:" + WEB_CLIENT_ID;
	public static final String APP_ENGINE_UPLOAD_URL = "http://1-dot-zeppa-cloud-1821.appspot.com/upload";
	public static final String APP_ENGINE_SERVE_URL = "http://1-dot-zeppa-cloud-1821.appspot.com/serve";
	
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
	public static final int NAVIGATION_MINGLERS_INDEX = 2;
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
