package com.minook.zeppa.contentprovider;

import android.net.Uri;
import android.provider.BaseColumns;

public class ZeppaContract {

	public static final String AUTHORITY = "com.minook.zeppa";
	public static final Uri ZEPPAEVENT_URI = Uri
			.parse("content://" + AUTHORITY);

	/*
	 * Columns for all Local Zeppa objects
	 */
	public static interface CommonColumns extends BaseColumns {
		public static final String ZEPPA_ID = "ZeppaDatabaseId";
		public static final String LAST_UPDATE = "LastUpdateTime";
		
		public static final int INDEX_LOCAL_ID = 0;
		public static final int INDEX_ZEPPA_ID = 1;
		public static final int INDEX_LAST_UPDATE = 2;
	}

	public static final class UserInfoContract implements CommonColumns {
		public static final String GOOGLE_ACCOUNT = "GoogleAccount";
		public static final String GIVEN_NAME = "GivenName";
		public static final String FAMILY_NAME = "FamilyName";
		public static final String IMAGE_URL = "ImageUrl";
		public static final String PHONE_NUMBER = "UnformattedPhoneNumber";
		public static final String CONTACTS_ID = "DeviceContactsId";
		public static final String USER_RELATIONSHIP = "RelationshipToUser";
		public static final String ZEPPA_RELATIONSHIP_ID = "ZeppaUserToUserRelationshipId";
		
		public static final String[] PROJECTION = {
			_ID,
			ZEPPA_ID,
			LAST_UPDATE,
			GOOGLE_ACCOUNT,
			GIVEN_NAME,
			FAMILY_NAME,
			IMAGE_URL,
			PHONE_NUMBER,
			CONTACTS_ID,
			USER_RELATIONSHIP,
			ZEPPA_RELATIONSHIP_ID
		};
		
		public static final int INDEX_GOOGLE_ACCOUNT = 3;
		public static final int INDEX_GIVEN_NAME = 4;
		public static final int INDEX_FAMILY_NAME = 5;
		public static final int INDEX_IMAGE_URL = 6;
		public static final int INDEX_PHONE_NUMBER = 7;
		public static final int INDEX_CONTACTS_ID = 8;
		public static final int INDEX_USER_RELATIONSHIP = 9;
		public static final int INDEX_ZEPPA_RELATIONSHIP_ID = 10;
		
	}

	public static final class ZeppaEventContract implements CommonColumns {
		public static final String LOCAL_EVENT_ID = "LocalEventID";
		public static final String HOST_ID  = "ZeppaHostID";
		public static final String ZEPPA_RELATIONSHIP_ID = "ZeppaUserToEventRelationship";
		public static final String ORIGINAL_EVENT_ID = "OriginalEventID";
		public static final String REPOSTED_EVENT_ID = "RepostedFromEventID";
		public static final String UNEXACT_LOCATION = "ShortLocation";
		public static final String TAG_ID1 = "EventTagID1";
		public static final String TAG_ID2 = "EventTagID2";
		public static final String TAG_ID3 = "EventTagID3";
		public static final String TAG_ID4 = "EventTagID4";
		public static final String TAG_ID5 = "EventTagID5";
		public static final String TAG_ID6 = "EventTagID6";
		public static final String EVENT_SCOPE = "EventScope";
		
		public static final String[] PROJECTION = {
			_ID,
			ZEPPA_ID, 
			LAST_UPDATE,
			LOCAL_EVENT_ID,
			HOST_ID,
			ZEPPA_RELATIONSHIP_ID,
			ORIGINAL_EVENT_ID,
			REPOSTED_EVENT_ID,
			UNEXACT_LOCATION,
			TAG_ID1,
			TAG_ID2,
			TAG_ID3,
			TAG_ID4,
			TAG_ID5,
			TAG_ID6,
			EVENT_SCOPE
		};
		
		public static final int INDEX_LOCAL_EVENT_ID = 3;
		public static final int INDEX_HOST_ID = 4;
		public static final int INDEX_ZEPPA_RELATIONSHIP_ID = 5;
		public static final int INDEX_ORIGINAL_EVENT_ID = 6;
		public static final int INDEX_REPOSTED_EVENT_ID = 7;
		public static final int INDEX_UNEXACT_LOCATION = 8;
		public static final int INDEX_TAG_ID1 = 9;
		public static final int INDEX_TAG_ID2 = 10;
		public static final int INDEX_TAG_ID3 = 11;
		public static final int INDEX_TAG_ID4 = 12;
		public static final int INDEX_TAG_ID5 = 13;
		public static final int INDEX_TAG_ID6 = 14;
		public static final int INDEX_EVENT_SCOPE = 15;
		 
	}

	public static final class EventTagContract implements CommonColumns {
		public static final String OWNER_ID = "OwnerZeppaID";
		public static final String TEXT = "EventTagText";
		public static final String CREATED = "DateTagCreated";
		public static final String RELATIONSHIP_ID = "UserFollowID";
		
		public static final String[] PROJECTION = {
			_ID,
			ZEPPA_ID,
			LAST_UPDATE,
			OWNER_ID,
			TEXT,
			CREATED,
			RELATIONSHIP_ID
		};
		
		public static final int INDEX_OWNER_ID = 3;
		public static final int INDEX_TEXT = 4;
		public static final int INDEX_CREATED = 5;
		public static final int INDEX_RELATIONSHIP_ID = 6;
		
	}

	public static final class ActivityContract implements CommonColumns {
		public static final String FROM_USER_ID = "FromZeppaUserID";
		public static final String TIME_SENT = "ActivityTime";
		public static final String MESSAGE = "ActivtyMessage";
		public static final String ACTIVITY_TYPE = "ActivtyType";
		public static final String EVENT_ID = "ZeppaEventID";
		public static final String HAS_SEEN = "UserHasSeen";
		public static final String EXPIRATION = "RelevanceExpiration";
		
		public static final String[] PROJECTION = {
			_ID,
			ZEPPA_ID,
			LAST_UPDATE,
			FROM_USER_ID,
			TIME_SENT,
			MESSAGE,
			ACTIVITY_TYPE,
			EVENT_ID,
			HAS_SEEN,
			EXPIRATION
		};
		
		public static final int INDEX_FROM_USER_ID = 3;
		public static final int INDEX_TIME_SENT = 4;
		public static final int INDEX_MESSAGE = 5;
		public static final int INDEX_ACTIVITY_TYPE = 6;
		public static final int INDEX_EVENT_ID = 7;
		public static final int INDEX_HAS_SEEN = 8;
		public static final int INDEX_EXPIRATION = 9;
		
	}
	
	

}
