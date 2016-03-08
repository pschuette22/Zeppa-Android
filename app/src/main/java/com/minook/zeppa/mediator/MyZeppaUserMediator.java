package com.minook.zeppa.mediator;


import com.appspot.zeppa_cloud_1821.zeppaclientapi.Zeppaclientapi;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaUser;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaUserInfo;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ApiClientHelper;
import com.minook.zeppa.Utils;

import java.io.IOException;

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
	 * @param user
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

	/**
	 * Get the Auth email for this user
	 * @return
	 */
	public String getGmail() {
		return user.getAuthEmail();
	}

	/**
	 * Get the untouched phone number for this user
	 * @return
	 * @throws NullPointerException
	 */
	public String getPhoneNumber() throws NullPointerException {

		String phoneNumber = user.getPhoneNumber();
		if (phoneNumber == null || phoneNumber.isEmpty()) {
			throw new NullPointerException();
		}

		return Utils.formatPhoneNumber(phoneNumber);
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
		ApiClientHelper helper = new ApiClientHelper();
		Zeppaclientapi api = helper.buildClientEndpoint();

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
			userCopy.setPhoneNumber(primaryUnformattedNumber);
		}

		userCopy.setUserInfo(info);
		try {
			userCopy = api.updateZeppaUser(credential.getToken(), userCopy).execute();
			user = userCopy;
			success = true;
			loadImageInAsync(user.getUserInfo().getImageUrl());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GoogleAuthException ex) {
			ex.printStackTrace();
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
