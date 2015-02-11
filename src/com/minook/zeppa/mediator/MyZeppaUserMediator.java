package com.minook.zeppa.mediator;

import java.io.IOException;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.zeppauserendpoint.Zeppauserendpoint;
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUser;
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUserInfo;

/**
 * 
 * @author DrunkWithFunk21
 * 
 *         There should only be a single instance of this class This holds the
 *         ZeppaUserObject of the user using the application
 * 
 */
public class MyZeppaUserMediator extends AbstractZeppaUserMediator {

	private ZeppaUser user;

	/**
	 * Constructs a new instance of MyZeppaUserManager</p> Should be only one
	 * instance per session
	 * 
	 * @param zeppaUser
	 *            object for user
	 */
	public MyZeppaUserMediator(ZeppaUser user) {
		super();
		this.user = user;
		loadImageInAsync(user.getUserInfo().getImageUrl());
	}

	@Override
	public String getGivenName() {
		// TODO Auto-generated method stub
		return user.getUserInfo().getGivenName();
	}

	@Override
	public String getFamilyName() {
		// TODO Auto-generated method stub
		return user.getUserInfo().getFamilyName();
	}

	@Override
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return getGivenName() + " " + getFamilyName();
	}

	@Override
	public Long getUserId() {
		// TODO Auto-generated method stub
		return user.getKey().getId();
	}

	@Override
	public String getGmail() {
		return user.getUserInfo().getGoogleAccountEmail();
	}

	@Override
	public String getUnformattedPhoneNumber() throws NullPointerException {

		String phoneNumber = user.getUserInfo().getPrimaryUnformattedNumber();
		if (phoneNumber == null || phoneNumber.isEmpty()) {
			throw new NullPointerException();
		}

		return phoneNumber;
	}

	@Override
	protected String getImageUrl() {
		// TODO Auto-generated method stub
		return user.getUserInfo().getImageUrl();
	}
	
	public String getZeppaCalendarId() {
		return user.getZeppaCalendarId();
	}

	public ZeppaUserInfo getUserInfo() {
		return user.getUserInfo();
	}

	public boolean updateUserInfoWithBlocking(
			GoogleAccountCredential credential, String givenName,
			String familyName, String imageUrl, String primaryUnformattedNumber) {
		boolean success = false;
		Zeppauserendpoint.Builder builder = new Zeppauserendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				GsonFactory.getDefaultInstance(), credential);
		builder = CloudEndpointUtils.updateBuilder(builder);
		Zeppauserendpoint endpoint = builder.build();

		ZeppaUser userCopy = user.clone();
		
		ZeppaUserInfo info = userCopy.getUserInfo();
		if (isValidString(givenName)) {
			info.setGivenName(givenName);
		}

		if (isValidString(familyName)) {
			info.setFamilyName(familyName);
		}

		if (isValidString(imageUrl)) {
			info.setImageUrl(imageUrl);
		}
		
		if(isValidString(primaryUnformattedNumber)){
			info.setPrimaryUnformattedNumber(primaryUnformattedNumber);
		}

		userCopy.setUserInfo(info);
		try {
			userCopy = endpoint.updateZeppaUser(userCopy).execute();
			user = userCopy;
			success = true;
			loadImageInAsync(user.getUserInfo().getImageUrl());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return success;
	}

	public boolean isValidString(String s) {
		if (s == null || s.isEmpty()) {
			return false;
		} else {
			
			return true;
		}
	}

}
