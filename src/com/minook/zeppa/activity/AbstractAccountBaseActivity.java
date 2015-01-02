package com.minook.zeppa.activity;

import java.io.IOException;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.utils.Utils;

public abstract class AbstractAccountBaseActivity extends
		AuthenticatedFragmentActivity implements OnClickListener {

	// UI Elements
	protected EditText givenNameField;
	protected EditText familyNameField;
	protected TextView emailField;
	protected TextView numberField;
	protected ImageView userImage;
	protected Drawable loadedImage;
	protected Button confirmButton;
	protected Button cancelButton;

	// Connection Progress
	protected ProgressDialog connectionProgress;

	// User Info
	protected String profileId;
	protected String givenName;
	protected String familyName;
	protected String imageUrl;
	protected String userGmail;
	protected String userPhoneNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().hide();

		setContentView(R.layout.activity_newuser);

		connectionProgress = new ProgressDialog(this);
		connectionProgress.setTitle("Loading Information");
		connectionProgress.setMessage("One Moment Please...");
		connectionProgress.setIndeterminate(true);

		// find all view elements
		confirmButton = (Button) findViewById(R.id.newuseractivity_create);
		confirmButton.setOnClickListener(this);
		cancelButton = (Button) findViewById(R.id.newuseractivity_cancel);
		cancelButton.setOnClickListener(this);

		givenNameField = (EditText) findViewById(R.id.newuseractivty_givenname);
		familyNameField = (EditText) findViewById(R.id.newuseractivty_familyname);
		userImage = (ImageView) findViewById(R.id.newuseractivity_image);
		userImage.setOnClickListener(this);

		emailField = (TextView) findViewById(R.id.newuseractivity_email);
		numberField = (TextView) findViewById(R.id.newuseractivity_phone);

	}


	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.newuseractivity_image) {
			selectImage();
		}
	}

	protected void setInfo() {
		String accountEmail = getSharedPreferences(Constants.SHARED_PREFS,
				Context.MODE_PRIVATE).getString(Constants.LOGGED_IN_ACCOUNT, null);

		if (accountEmail == null || accountEmail.isEmpty()) {
			Log.wtf("TAG", "In Account Activity without account specified");
			logout();
		} else {
			userGmail = accountEmail;
			emailField.setText(userGmail);
		}

		TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		String numberString = tMgr.getLine1Number();

		if (numberString == null) {
			numberField.setVisibility(View.GONE);
		} else {
			Log.d("TAG", "Number: " + numberString);
			userPhoneNumber = numberString;
			numberField.setText(Utils.formatPhoneNumber(userPhoneNumber));
		}

	}

	protected void selectImage() {

	}

	protected void setImage(Bitmap imageBitmap) {
		userImage.setImageBitmap(imageBitmap);
	}

	protected void loadAndSetImageInAsync(String imageUrl) {
		if (imageUrl.endsWith("?sz=50")) {
			imageUrl.substring(0, (imageUrl.length() - 6));
		}

		Object[] params = { imageUrl };
		new AsyncTask<Object, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(Object... params) {
				try {
					return Utils.loadImageBitmapFromUrl((String) params[0]);
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				super.onPostExecute(result);
				if (result == null) {
					Toast.makeText(getApplicationContext(),
							"Error Loading picture", Toast.LENGTH_SHORT).show();
				} else {
					setImage(result);
				}

			}

		}.execute(params);

	}

		

}
