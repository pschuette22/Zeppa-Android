package com.minook.zeppa.activity;

import android.view.View;

import com.minook.zeppa.mediator.MyZeppaUserMediator;
import com.minook.zeppa.singleton.ZeppaUserSingleton;


public class EditAccountActivity extends AbstractAccountBaseActivity {


	@Override
	protected void onStart() {
		super.onStart();
		setInfo();
	}

	@Override
	public void onClick(View v) {
		
		
	}

	@Override
	protected void setInfo() {
		// TODO Auto-generated method stub
		MyZeppaUserMediator mediator = ZeppaUserSingleton.getInstance().getUserMediator();
		
		
	}

	
	
}
