package com.minook.zeppa.runnable;

import java.io.IOException;
import java.util.Iterator;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.Utils;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppauserinfoendpoint.Zeppauserinfoendpoint;
import com.minook.zeppa.zeppauserinfoendpoint.Zeppauserinfoendpoint.ListZeppaUserInfo;
import com.minook.zeppa.zeppauserinfoendpoint.model.CollectionResponseZeppaUserInfo;
import com.minook.zeppa.zeppauserinfoendpoint.model.ZeppaUserInfo;

public class FindMinglersRunnable extends BaseRunnable {


	public FindMinglersRunnable(ZeppaApplication application,
			GoogleAccountCredential credential) {
		super(application, credential);
	}

	@Override
	public void run() {
		// Initialize reused objects
		ContentResolver resolver = application.getContentResolver();
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

						if (builder.length() + number.length() > 1960
								|| cursor.isLast()) {
							cursor.moveToPrevious();
							builder.append(")");

							String query = builder.toString();

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

						if (builder.length() + email.length() > 1960
								|| cursor.isLast()) {
							cursor.moveToPrevious();
							builder.append(")");

							String query = builder.toString();
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

		}

		cursor.close();

		try {
			application.getCurrentActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					ZeppaUserSingleton.getInstance().notifyObservers();
				}

			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void executeQuery(String filter) {

		boolean valuesDidChange = false;
		Zeppauserinfoendpoint endpoint = buildUserInfoEndpoint();

		String cursor = null;

		try {
			ListZeppaUserInfo listUserInfo = endpoint.listZeppaUserInfo();
			listUserInfo.setFilter(filter);
			listUserInfo.setCursor(cursor);
			CollectionResponseZeppaUserInfo result = listUserInfo.execute();

			if (result != null && result.getItems() != null
					&& !result.getItems().isEmpty()) {

				Iterator<ZeppaUserInfo> iterator = result.getItems().iterator();
				while (iterator.hasNext()) {

					ZeppaUserSingleton.getInstance()
							.addDefaultZeppaUserMediator(iterator.next(), null);

				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private String getFormattedNumber(Cursor cursor) {
		String number = cursor.getString(cursor
				.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

		return Utils.make11DigitNumber(number);
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

}

//

