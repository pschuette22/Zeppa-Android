package com.minook.zeppa.activity;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.minook.zeppa.R;
import com.minook.zeppa.photoendpoint.model.Photo;

public abstract class AbstractAccountBaseActivity extends
		AuthenticatedFragmentActivity implements OnClickListener {

	private static final String TAG = AbstractAccountBaseActivity.class
			.getName();

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

	protected Photo displayPhoto;
	private static final int REQ_SELECT_PHOTO = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().hide();

		setContentView(R.layout.activity_account);

		connectionProgress = new ProgressDialog(this);
		connectionProgress.setTitle("Loading Information");
		connectionProgress.setMessage("One Moment Please...");
		connectionProgress.setIndeterminate(true);

		// find all view elements
		confirmButton = (Button) findViewById(R.id.accountactivity_confirm);
		confirmButton.setOnClickListener(this);
		cancelButton = (Button) findViewById(R.id.accountactivity_cancel);
		cancelButton.setOnClickListener(this);

		givenNameField = (EditText) findViewById(R.id.accountactivity_givenname);
		familyNameField = (EditText) findViewById(R.id.accountactivity_familyname);
		userImage = (ImageView) findViewById(R.id.accountactivity_image);
		userImage.setOnClickListener(this);

		emailField = (TextView) findViewById(R.id.accountactivity_email);
		numberField = (TextView) findViewById(R.id.accountactivity_phone);

	}

	@Override
	public void onClick(View v) {
//		if (v.getId() == R.id.accountactivity_image) {
//			Intent photoPicker = new Intent(Intent.ACTION_PICK);
//			photoPicker.setType("image/*");
//			startActivityForResult(photoPicker, REQ_SELECT_PHOTO);
//		}
	}

	protected abstract void setInfo();

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {

			switch (requestCode) {

//			case REQ_SELECT_PHOTO:
//
//				Uri uri = data.getData();
//				userImage.setImageURI(uri);
//				
//
//				break;

			}
		}

	}


	protected void uploadImage(Bitmap map) throws ClientProtocolException, IOException {
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 10000);
		
		HttpGet httpGet = new HttpGet("http://zeppamobile.com/upload");
		HttpResponse response = httpClient.execute(httpGet);
		
		
		HttpPost post = new HttpPost("http://zeppamobile.com/upload");
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();

		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		
//		MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
//		HttpEntity entity = new HttpEntity(null,);		
//		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		
		
		
		
	}
	

}
