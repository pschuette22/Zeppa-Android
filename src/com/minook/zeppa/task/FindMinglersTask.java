package com.minook.zeppa.task;

import java.io.IOException;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.utils.Utils;
import com.minook.zeppa.zeppauserinfoendpoint.Zeppauserinfoendpoint;
import com.minook.zeppa.zeppauserinfoendpoint.Zeppauserinfoendpoint.ListZeppaUserInfo;
import com.minook.zeppa.zeppauserinfoendpoint.model.CollectionResponseZeppaUserInfo;
import com.minook.zeppa.zeppauserinfoendpoint.model.ZeppaUserInfo;

public class FindMinglersTask extends AsyncTask<Void, Void, Void> {

	private final String TAG = getClass().getName();
	private Context context;
	private GoogleAccountCredential credential;

	// private final int finderThreadCount = 5;

	public FindMinglersTask(Context context, GoogleAccountCredential credential) {
		this.context = context;
		this.credential = credential;

	}

	@Override
	protected Void doInBackground(Void... params) {

		// Initialize reused objects
		ContentResolver resolver = context.getContentResolver();
		StringBuilder builder = new StringBuilder();

		Cursor cursor = null;

		/*
		 * Query by numbers
		 */

		cursor = resolver.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null, null);

		if (cursor.moveToFirst()) {

			do {
				String number = getFormattedNumber(cursor);
				if (!numberIsRecognized(number)) {

					if (builder.length() == 0) {
						builder.append("(primaryUnformattedNumber == ");
						builder.append("'");
						builder.append(number);
						builder.append("'");
					} else {

						if (builder.length() + number.length() > 1960 || cursor.isLast()) {
							cursor.moveToPrevious();
							builder.append(")");

							String query = builder.toString();
							Log.d(TAG, "Number Query: " + query);
							executeQuery(query);

							builder = new StringBuilder();

						} else {
							builder.append(" || primaryUnformattedNumber == ");
							builder.append("'");
							builder.append(number);
							builder.append("'");

						}

					}

				}
			} while (!cursor.isLast() && cursor.moveToNext());

//			if (builder.length() > 0) {
//				executeQuery(builder.toString());
//			}

		}

		builder = new StringBuilder(); // make sure it is reset
		
		/*
		 * Iterate through all GMail addresses saved to phone and check if they
		 * exist in zeppa.
		 */
		cursor = resolver.query(
				ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, null,
				null, null);

		if (cursor.moveToFirst()) {

			do {
				String email = getEmail(cursor);
				if (!emailIsRecognized(email)) {
					if (builder.length() == 0) {
						builder.append("(googleAccountEmail == ");
						builder.append("'");
						builder.append(email);
						builder.append("'");
					} else {

						if (builder.length() + email.length() > 1960 || cursor.isLast()) {
							cursor.moveToPrevious();
							builder.append(")");

							String query = builder.toString();
							Log.d(TAG, "Email Query: " + query);
							executeQuery(query);

							builder = new StringBuilder();

						} else {
							builder.append(" || googleAccountEmail == ");
							builder.append("'");
							builder.append(email);
							builder.append("'");

						}

					}

				}

			} while (!cursor.isLast() && cursor.moveToNext());

//			if (builder.length() > 0) {
//				executeQuery(builder.toString());
//			}

		}

		cursor.close();

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		Log.d(TAG, "Executed Find Minglers Task");
		super.onPostExecute(result);
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
	}

	private void executeQuery(String filter) {

		Zeppauserinfoendpoint.Builder endpointBuilder = new Zeppauserinfoendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				AndroidJsonFactory.getDefaultInstance(), credential);
		endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
		Zeppauserinfoendpoint endpoint = endpointBuilder.build();

		int exceptionCount = 0;
		String cursor = null;

		do {
			try {
				ListZeppaUserInfo listUserInfo = endpoint.listZeppaUserInfo();
				listUserInfo.setFilter(filter);
				listUserInfo.setCursor(cursor);
				listUserInfo.setLimit(25);
				CollectionResponseZeppaUserInfo result = listUserInfo.execute();

				if (result != null && result.getItems() != null
						&& !result.getItems().isEmpty()) {
					addUserInfo(result.getItems());
				}

				cursor = result.getNextPageToken();
				filter = null;

			} catch (IOException e) {
				e.printStackTrace();
				exceptionCount++;
			}

		} while (cursor != null && exceptionCount < 3);
	}

	private String getFormattedNumber(Cursor cursor) {
		String number = cursor.getString(cursor
				.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

		return Utils.make10DigitNumber(number);
	}

	private String getEmail(Cursor cursor) {
		return cursor
				.getString(cursor
						.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
	}

	/**
	 * Checks to see if this number is already associated with a held user
	 * 
	 * @param phoneNumber
	 * @return True if recognized
	 */
	private synchronized boolean numberIsRecognized(String phoneNumber) {
		return ZeppaUserSingleton.getInstance().numberIsRecognized(phoneNumber);
	}

	/**
	 * Checks to see if this email is already associated with a held user
	 * 
	 * @param email
	 * @return True if recognized
	 */
	private synchronized boolean emailIsRecognized(String email) {
		return ZeppaUserSingleton.getInstance().emailIsRecognized(email);
	}

	/**
	 * This creates a mediator for a new user instance and adds them to the
	 * 
	 * @param userInfo
	 */
	private synchronized void addUserInfo(List<ZeppaUserInfo> userInfoList) {
		for (int i = 0; i < userInfoList.size(); i++) {
			ZeppaUserInfo userInfo = userInfoList.get(i);
			if (!numberIsRecognized(userInfo.getPrimaryUnformatedNumber())
					&& !emailIsRecognized(userInfo.getGoogleAccountEmail())) {
				ZeppaUserSingleton.getInstance().addDefaultZeppaUserMediator(
						userInfo, null);
			}

		}
	}

}

//

