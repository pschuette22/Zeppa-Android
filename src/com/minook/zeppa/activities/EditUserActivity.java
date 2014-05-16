package com.minook.zeppa.activities;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.minook.zeppa.zeppauserendpoint.model.ZeppaUser;

public class EditUserActivity extends AuthenticatedFragmentActivity {

	// user object, null if new account
	private ZeppaUser 	user;
	
	// UI Elements
	private EditText 	givenNameField;
	private EditText 	familyNameField;
	private TextView 	emailField;
	private TextView	numberField;
	
	private ImageView 	userImage;
	private String 		imageUrl;
	
	private Button 		createButton;
	private Button		cancelButton;
	
	
	
	
	
}
