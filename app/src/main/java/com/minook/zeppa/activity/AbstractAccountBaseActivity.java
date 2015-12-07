package com.minook.zeppa.activity;

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

import com.appspot.zeppa_cloud_1821.zeppaclientapi.Zeppaclientapi;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.PhotoInfo;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ApiClientHelper;
import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class AbstractAccountBaseActivity extends
		AuthenticatedFragmentActivity implements OnClickListener {

//	private static final String TAG = AbstractAccountBaseActivity.class
//			.getName();

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
//	protected PhotoInfo displayPhoto;

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

					StringBuilder builder = new StringBuilder();
					for (int i = 0; i < userGmail.length(); i++) {
						char c = userGmail.charAt(i);
						if (c == '@') {
							break;
						} else if (Character.isDigit(c)
								|| Character.isLetter(c)) {
							builder.append(c);
						}
					}

					builder.append("-" + System.currentTimeMillis());

//					ProgressDialog postProgress = new ProgressDialog(getBaseContext());
//					postProgress.setIndeterminate(true);
//					postProgress.setTitle("Uploading Image");
//					postProgress.setMessage("One moment please...");
//					postProgress.setCancelable(false);
//					postProgress.show();
					
					new UploadImageTask(getGoogleAccountCredential(),
							imageBitmap, builder.toString()).execute();

				} catch (FileNotFoundException e) {
					e.printStackTrace();
					Toast.makeText(this, "Image Not Found!", Toast.LENGTH_SHORT)
							.show();
				}

				break;

			}
		}

	}

	protected void setUserImage() {
		if (imageBitmap != null) {
			userImage.setImageBitmap(imageBitmap);
			userImage.invalidate();

		} else if (imageUrl != null && !imageUrl.isEmpty()) {
			loadAndSetImageInAsync(imageUrl);
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

//	private String trimSizingFromURL(String imageUrl) {
//
//		if (imageUrl.contains("?sz=")) {
//			imageUrl = imageUrl.substring(0, imageUrl.lastIndexOf("?sz="));
//		}
//
//		return imageUrl;
//	}

	private class UploadImageTask extends AsyncTask<Void, Void, String> {

		private String imageTitle;
		private Bitmap bitmap;
		private GoogleAccountCredential credential;
//		private ProgressDialog postProgress;

		public UploadImageTask(GoogleAccountCredential credential, /*ProgressDialog postProgress,*/
				Bitmap bitmap, String imageTitle) {
			this.credential = credential;
			this.bitmap = bitmap;
			this.imageTitle = imageTitle;
//			this.postProgress = postProgress;
		}

//		@SuppressWarnings("deprecation")
		@Override
		protected String doInBackground(Void... params) {

			String result = null;
			try {

				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(Constants.IMAGEUPLOAD_URL);
				HttpResponse response = client.execute(get);

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(
								response.getEntity().getContent(), "UTF-8"));
				String sResponse;
				StringBuilder s = new StringBuilder();
				while ((sResponse = reader.readLine()) != null) {
					s = s.append(sResponse);
				}

				String uploadUrl = s.toString();

				HttpPost postRequest = new HttpPost(uploadUrl);
				MultipartEntityBuilder builder = MultipartEntityBuilder
						.create();
				builder.setBoundary(Constants.MULTIPARTENTITY_BOUNDRY);
				builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
						stream)) {
					byte[] byteArray = stream.toByteArray();

					// builder.addTextBody("name", imageTitle);
					// FormBodyPart part = new FormBodyPart("data", body)

                    builder.addBinaryBody("data", byteArray, ContentType.create("image/jpeg"), null);

					postRequest.setEntity(builder.build());

					HttpResponse response1 = client.execute(postRequest);

					InputStream instream = response1.getEntity().getContent();
					String json = convertStreamToString(instream);

					JSONObject object = new JSONObject(json);
					String blobKey = object.getString("blobKey");
					String servingUrl = object.getString("servingUrl");

					if (servingUrl != null && !servingUrl.isEmpty()) {
						PhotoInfo photo = new PhotoInfo();
						photo.setBlobKey(blobKey);
						photo.setUrl(servingUrl);
						photo.setOwnerEmail(userGmail);

						// Build the api endpoint class
						ApiClientHelper helper = new ApiClientHelper();
						Zeppaclientapi api = helper.buildClientEndpoint();

						// Insert photo info
						photo = api.insertPhotoInfo(credential.getToken(), photo).execute();

						result = photo.getUrl();
					}

				} else {
					//
				}

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
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

//			postProgress.dismiss();
			
			if (result != null && !result.isEmpty()) {
				imageUrl = result;
				Toast.makeText(AbstractAccountBaseActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();

			} else {
				Toast.makeText(AbstractAccountBaseActivity.this, "Error Uploading Image", Toast.LENGTH_SHORT).show();
				imageBitmap = null;
				setUserImage();
			}

		}

		private String convertStreamToString(InputStream is) {

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();

			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return sb.toString();
		}

	}

}
