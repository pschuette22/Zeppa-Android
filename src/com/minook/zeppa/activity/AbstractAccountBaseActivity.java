package com.minook.zeppa.activity;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.Utils;
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

	private Bitmap imageBitmap;

	private static final int REQ_SELECT_PHOTO = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().hide();

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
			// AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// builder.setTitle("To Be Fixed Soon");
			// builder.setMessage("I haven't put in the ability to store images on the Zeppa backend yet. So, for now, you must set your Google Account (gmail) picture then click 'Update Image' below and your image will update");
			//
			// DialogInterface.OnClickListener listener = new
			// DialogInterface.OnClickListener() {
			//
			// @Override
			// public void onClick(DialogInterface dialog, int which) {
			// if (which == DialogInterface.BUTTON_POSITIVE) {
			// // Set current Google Account
			// Person currentPerson = Plus.PeopleApi
			// .getCurrentPerson(apiClient);
			//
			// if (currentPerson.getImage().isDataValid()) {
			// imageUrl = currentPerson.getImage().getUrl();
			// imageUrl = trimSizingFromURL(imageUrl);
			// loadAndSetImageInAsync(imageUrl);
			// }
			//
			// }
			//
			// dialog.dismiss();
			// }
			// };
			//
			// builder.setPositiveButton("Update Image", listener);
			// builder.setNegativeButton("Dismiss", listener);
			//
			// builder.show();

			Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
			photoPickerIntent.setType("image/*");
			startActivityForResult(photoPickerIntent, REQ_SELECT_PHOTO);

		}
	}

	protected abstract void setInfo();

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {

			switch (requestCode) {

			case REQ_SELECT_PHOTO:

				Uri selectedImage = data.getData();
				InputStream imageStream;
				try {
					imageStream = getContentResolver().openInputStream(
							selectedImage);
					imageBitmap = BitmapFactory.decodeStream(imageStream);
					setUserImage();

					new UploadImageTask(imageBitmap).execute();

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(this, "Image Not Found!", Toast.LENGTH_SHORT)
							.show();
				}

				break;

			}
		}

	}

	protected void setUserImage() {
		if (imageBitmap == null && imageUrl != null && !imageUrl.isEmpty()) {
			loadAndSetImageInAsync(imageUrl);
		} else if (imageBitmap != null) {
			userImage.setImageBitmap(imageBitmap);
			userImage.invalidate();
		}
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
					imageBitmap = result;
					setUserImage();
				}

			}

		}.execute(params);
	}

	private String trimSizingFromURL(String imageUrl) {

		if (imageUrl.contains("?sz=")) {
			imageUrl = imageUrl.substring(0, imageUrl.lastIndexOf("?sz="));
		}

		return imageUrl;
	}

	private class UploadImageTask extends AsyncTask<Void, Void, String> {

		private Bitmap map;

		public UploadImageTask(Bitmap map) {
			this.map = map;
		}

		@Override
		protected String doInBackground(Void... params) {

			String result = null;
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost("http://" + Constants.APP_ENGINE_CLIENT_ID + "/upload");
			
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			map.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			byte[] byteArray = stream.toByteArray();
			MultipartEntityBuilder entityBuilder = MultipartEntityBuilder
					.create();

			try {
				entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
				entityBuilder.addBinaryBody("file", byteArray);
				
				postRequest.setEntity(entityBuilder.build());
				
				HttpResponse response = httpClient.execute(postRequest);

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (result != null && !result.isEmpty()) {
				imageUrl = result;
				// TODO: indicate that image is being served
			} else {
				// TODO: indicate unsuccessful
			}

		}

	}

}
