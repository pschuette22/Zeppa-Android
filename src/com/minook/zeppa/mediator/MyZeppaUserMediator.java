package com.minook.zeppa.mediator;

import com.minook.zeppa.zeppauserendpoint.model.ZeppaUser;

/**
 * 
 * @author DrunkWithFunk21
 *
 * There should only be a single instance of this class
 * This holds the ZeppaUserObject of the user using the application
 *
 */
public class MyZeppaUserMediator extends AbstractZeppaUserMediator{

	
	private ZeppaUser user;
	
	
	/**
	 * Constructs a new instance of MyZeppaUserManager</p>
	 * Should be only one instance per session
	 * 
	 * @param zeppaUser object for user
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
	public String getUnformattedPhoneNumber() throws NullPointerException{
		
		String phoneNumber = user.getUserInfo().getPrimaryUnformatedNumber();
		if(phoneNumber == null || phoneNumber.isEmpty()){
			throw new NullPointerException();
		}
		
		return phoneNumber;
	}

	public String getZeppaCalendarId(){
		return user.getZeppaCalendarId();
	}

	
	
	
}
