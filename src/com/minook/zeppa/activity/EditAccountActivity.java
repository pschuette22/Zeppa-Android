package com.minook.zeppa.activity;

import android.view.View;

import com.minook.zeppa.mediator.MyZeppaUserMediator;
import com.minook.zeppa.singleton.ZeppaUserSingleton;


public class EditAccountActivity extends AbstractAccountBaseActivity {

	private MyZeppaUserMediator myMediator;
	

	@Override
	protected void onResume() {
		super.onResume();
		myMediator = ZeppaUserSingleton.getInstance().getUserMediator();
		myMediator.setContext(this);
		
	}
	
	

	@Override
	protected void onStart() {
		super.onStart();
		setInfo();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		
		}
		
	}

	@Override
	protected void setInfo() {
		givenNameField.setText(myMediator.getGivenName());
		familyNameField.setText(myMediator.getFamilyName());
		myMediator.setImageWhenReady(userImage);
		
	}

	
	
}
