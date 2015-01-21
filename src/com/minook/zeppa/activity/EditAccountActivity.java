package com.minook.zeppa.activity;

import android.view.MenuItem;
import android.view.View;

import com.minook.zeppa.R;
import com.minook.zeppa.mediator.MyZeppaUserMediator;
import com.minook.zeppa.singleton.ZeppaUserSingleton;


public class EditAccountActivity extends AbstractAccountBaseActivity {

	private MyZeppaUserMediator myMediator;
	

	@Override
	protected void onResume() {
		super.onResume();
		myMediator = ZeppaUserSingleton.getInstance().getUserMediator();
		myMediator.setContext(this);
		setInfo();
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		
		switch(item.getItemId()){
		case android.R.id.home:
			onBackPressed();
			return true;
		
		}
		
		
		return false;
	}



	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		switch (v.getId()){
		
		case R.id.accountactivity_cancel:
			onBackPressed();
			break;
		
		case R.id.accountactivity_confirm:
			updateAccount();
			break;
		
		
			
		}
		
		
	}

	@Override
	protected void setInfo() {
		givenNameField.setText(myMediator.getGivenName());
		familyNameField.setText(myMediator.getFamilyName());
		myMediator.setImageWhenReady(userImage);
		numberField.setText(myMediator.getPrimaryPhoneNumber());
		emailField.setText(myMediator.getGmail());
		myMediator.setImageWhenReady(userImage);
		
	}

	private void updateAccount(){
		
	}
	
	
}
