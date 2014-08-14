package com.minook.zeppa.contentprovider;

import android.database.Cursor;

import com.minook.zeppa.Constants.UserDataRelationship;
import com.minook.zeppa.contentprovider.ZeppaContract.UserInfoContract;
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUser;
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUserInfo;
import com.minook.zeppa.zeppausertouserrelationshipendpoint.model.ZeppaUserToUserRelationship;


/**
 * This class acts as the median between the local database, cloud data, and runtime data
 * 
 * @author DrunkWithFunk21
 *
 */
public class ZeppaUserData {
	
	
	public long localId;
	public long zeppaId;
	public String accountName;
	public boolean isSelf;
	public String givenName;
	public String familyName;
	public String imageUrl;
	public String phoneNumber;
	public long contactsId;
	public UserDataRelationship dataRelationship;
	public long relationshipId;
	
	/**
	 * creates a new blank instance, no null values
	 * @return instance
	 */
	public static ZeppaUserData newInstance(){
		ZeppaUserData data = new ZeppaUserData();
		data.localId = -1;
		data.zeppaId = -1;
		data.isSelf = false;
		data.relationshipId = -1;
		data.givenName = "";
		data.familyName = "";
		data.imageUrl = "";
		data.accountName = "";
		data.phoneNumber = "";
		data.dataRelationship = UserDataRelationship.UNKNOWN;
		return data;
	}
	
	/**
	 * Generate all UserData from local db.</p>
	 * Automatically sets self to false
	 * 
	 * @param cursor
	 * @return userData for held user
	 */
	public static ZeppaUserData generateFromCursor(Cursor cursor){
		ZeppaUserData data = new ZeppaUserData();
		data.localId = cursor.getLong(UserInfoContract.INDEX_LOCAL_ID);
		data.zeppaId = cursor.getLong(UserInfoContract.INDEX_ZEPPA_ID);
		data.accountName = cursor.getString(UserInfoContract.INDEX_GOOGLE_ACCOUNT); // user Gmail address
		data.isSelf = false;
		data.givenName = cursor.getString(UserInfoContract.INDEX_GIVEN_NAME);
		data.familyName = cursor.getString(UserInfoContract.INDEX_FAMILY_NAME);
		data.imageUrl = cursor.getString(UserInfoContract.INDEX_IMAGE_URL);
		data.phoneNumber = cursor.getString(UserInfoContract.INDEX_PHONE_NUMBER);
		data.contactsId = cursor.getLong(UserInfoContract.INDEX_CONTACTS_ID); // link to the Contacts contract
		data.dataRelationship = UserDataRelationship.values()[cursor.getInt(UserInfoContract.INDEX_USER_RELATIONSHIP)];
		data.relationshipId = cursor.getLong(UserInfoContract.INDEX_ZEPPA_RELATIONSHIP_ID);

		return data;
	
	}
	
	/**
	 *  creates an instance of ZeppaUserData from a retrieved object and relationship object
	 * @param userInfo		retrieved ZeppaUserInfo object
	 * @param relationship	retrieved ZeppaUserToUserRelationship object
	 * @return data			resulting data
	 */
	public static ZeppaUserData generateFromRetrieved(ZeppaUserInfo userInfo, ZeppaUserToUserRelationship relationship) throws NullPointerException{
		if(userInfo == null){
			throw new NullPointerException("tried generating ZeppaUserData from null ZeppaUserInfo");
		}
		ZeppaUserData data = new ZeppaUserData();
		data.localId = -1;
		data.zeppaId = userInfo.getKey().getId().longValue();
		data.accountName = userInfo.getGoogleAccountEmail();
		data.isSelf = false;
		data.givenName = userInfo.getGivenName();
		data.familyName = userInfo.getFamilyName();
		data.imageUrl = userInfo.getImageUrl();
		data.phoneNumber = userInfo.getPrimaryUnformatedNumber();
		data.contactsId = -1;
		if(relationship == null ){
			data.dataRelationship = UserDataRelationship.NOT_CONNECTED;
			data.relationshipId = -1;
		} else {
			switch(relationship.getRelationshipType().intValue()){
			case 0:
				data.dataRelationship = UserDataRelationship.MINGLING;
				break;
			case 1:
				if(data.zeppaId == relationship.getConnectionRequesterId().longValue()){
					data.dataRelationship = UserDataRelationship.PENDING_REQUEST;
				} else {
					data.dataRelationship = UserDataRelationship.SENT_REQUEST;
				}
				break;
			default:
				data.dataRelationship = UserDataRelationship.UNKNOWN; // should never happen
			}
			
			data.relationshipId = relationship.getKey().getId();
		}
		
		return data;
	}
	
	/**
	 * Create the ZeppaUserData class for this user;
	 * @param user	Retrieved ZeppaUser object
	 * @return data 
	 */
	public static ZeppaUserData generateMyData(ZeppaUser user){
		ZeppaUserInfo info = user.getUserInfo();
		ZeppaUserData data = new ZeppaUserData();
		
		data.localId = -1;
		data.zeppaId = user.getKey().getId().longValue();
		data.accountName = info.getGoogleAccountEmail();
		data.isSelf = true;
		data.givenName = info.getGivenName();
		data.familyName = info.getFamilyName();
		data.imageUrl = info.getImageUrl();
		data.phoneNumber = info.getPrimaryUnformatedNumber();
		data.contactsId = -1;
		data.relationshipId = -1;
		data.dataRelationship = UserDataRelationship.IS_USER;
		
		return data;
	}	
	
}
