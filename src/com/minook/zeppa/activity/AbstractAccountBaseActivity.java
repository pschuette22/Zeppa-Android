package com.minook.zeppa.activity;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.Utils;
import com.minook.zeppa.photoendpoint.model.Photo;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

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
		if (v.getId() == R.id.accountactivity_image) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("To Be Fixed Soon");
			builder.setMessage("I haven't put in the ability to store images on the Zeppa backend yet. So, for now, you must set your Google Account (gmail) picture then click 'Update Image' below and your image will update");

			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (which == DialogInterface.BUTTON_POSITIVE) {
						// Set current Google Account
						Person currentPerson = Plus.PeopleApi
								.getCurrentPerson(apiClient);

						if (currentPerson.getImage().isDataValid()) {
							imageUrl = currentPerson.getImage().getUrl();
							imageUrl = trimSizingFromURL(imageUrl);
							loadAndSetImageInAsync(imageUrl);
						}

					}

					dialog.dismiss();
				}
			};

			builder.setPositiveButton("Update Image", listener);
			builder.setNegativeButton("Dismiss", listener);

			builder.show();

		}
	}

	protected abstract void setInfo();

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {

			switch (requestCode) {

			case REQ_SELECT_PHOTO:

				Uri uri = data.getData();
				userImage.setImageURI(uri);
				try {
					Bitmap map = MediaStore.Images.Media.getBitmap(
							this.getContentResolver(), uri);
					uploadImage(map);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				break;

			}
		}

	}

	protected void uploadImage(Bitmap map) throws ClientProtocolException,
			IOException {

		HttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost(Constants.APP_ENGINE_UPLOAD_URL);

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addPart("title", new StringBody("Profile Picture"));
		builder.addPart("ownerId", new StringBody(""
				+ ZeppaUserSingleton.getInstance().getUserId()));

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			map.compress(CompressFormat.JPEG, 100, bos);

		} catch (Exception e) {

			builder.addPart("image", null);
		}

		// MultipartEntity entity = new
		// MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		// HttpEntity entity = new HttpEntity(null,);
		// MultipartEntityBuilder builder = MultipartEntityBuilder.create();

	}

	protected void loadAndSetImageInAsync(String imageUrl) {
		Object[] params = { imageUrl };
		new AsyncTask<Object, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(Object... params) {
				String imageUrl = (String) params[0];
				try {
					return Utils.loadImageBitmapFromUrl(imageUrl);
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				super.onPostExecute(result);

				if (result != null) {
					userImage.setImageBitmap(result);
					userImage.invalidate();
				}

			}

		}.execute(params);
	}
	
	private String trimSizingFromURL(String imageUrl){
		
		if (imageUrl.contains("?sz=")){
			imageUrl = imageUrl.substring(0, imageUrl.lastIndexOf("?sz="));
		}
		
		return imageUrl;
	}

}
