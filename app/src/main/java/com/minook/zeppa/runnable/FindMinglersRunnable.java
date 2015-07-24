package com.minook.zeppa.runnable;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.appspot.zeppa_cloud_1821.zeppauserinfoendpoint.Zeppauserinfoendpoint;
import com.appspot.zeppa_cloud_1821.zeppauserinfoendpoint.Zeppauserinfoendpoint.ListZeppaUserInfo;
import com.appspot.zeppa_cloud_1821.zeppauserinfoendpoint.model.CollectionResponseZeppaUserInfo;
import com.appspot.zeppa_cloud_1821.zeppauserinfoendpoint.model.ZeppaUserInfo;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.Utils;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.adapter.MinglerFinderAdapter;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FindMinglersRunnable extends BaseRunnable {

	private List<String> recognizedEmails;
	private List<String> recognizedNumbers;
	private MinglerFinderAdapter finderAdapter;

	public FindMinglersRunnable(ZeppaApplication application,
			GoogleAccountCredential credential,
			MinglerFinderAdapter finderAdapter) {
		super(application, credential);
		recognizedEmails = ZeppaUserSingleton.getInstance()
				.getRecognizedEmails();
		recognizedNumbers = ZeppaUserSingleton.getInstance()
				.getRecognizedNumbers();
		this.finderAdapter = finderAdapter;
	}

	@Override
	public void run() {
		// Initialize reused objects
		ContentResolver resolver = application.getContentResolver();
		StringBuilder builder = new StringBuilder();

		/*
		 * Query by numbers
		 */

		Cursor numberCursor = resolver.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null, null);

		if (numberCursor.moveToFirst()) {

			do {
				try {
					String number = getFormattedNumber(numberCursor);

					if (!numberIsRecognized(number)) {

						if (builder.length() == 0) {
							builder.append("(primaryUnformattedNumber == ");
							builder.append("'");
							builder.append(number);
							builder.append("'");
						} else {

							if (builder.length() + number.length() > 1960
									|| numberCursor.isLast()) {
								numberCursor.moveToPrevious();
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
				} catch (IndexOutOfBoundsException e) {
					// Dont do anything
				}

			} while (!numberCursor.isLast() && numberCursor.moveToNext());

		}

		numberCursor.close();

		builder = new StringBuilder(); // make sure it is reset

		/*
		 * Iterate through all GMail addresses saved to phone and check if they
		 * exist in zeppa.
		 */
		Cursor emailCursor = resolver.query(
				ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, null,
				null, null);

		if (emailCursor.moveToFirst()) {

			do {
				String email = getEmail(emailCursor);
				if (!emailIsRecognized(email)) {
					if (builder.length() == 0) {
						builder.append("(googleAccountEmail == ");
						builder.append("'");
						builder.append(email);
						builder.append("'");
					} else {

						if (builder.length() + email.length() > 1960
								|| emailCursor.isLast()) {
							emailCursor.moveToPrevious();
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

			} while (!emailCursor.isLast() && emailCursor.moveToNext());

		}

		emailCursor.close();

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

				List<ZeppaUserInfo> uniqueInfoItems = new ArrayList<ZeppaUserInfo>();
				while (iterator.hasNext()) {
					ZeppaUserInfo info = iterator.next();
					recognizedEmails.add(info.getGoogleAccountEmail());

					if (info.getPrimaryUnformattedNumber() != null
							&& !info.getPrimaryUnformattedNumber().isEmpty()) {
						recognizedNumbers.add(info
								.getPrimaryUnformattedNumber());
					}

					uniqueInfoItems.add(info);
				}

				addMediatorsOnUIThread(uniqueInfoItems);

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
	private boolean numberIsRecognized(String phoneNumber) {
		Iterator<String> iterator = recognizedNumbers.iterator();

		while (iterator.hasNext()) {
			if (iterator.next().equalsIgnoreCase(phoneNumber)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks to see if this email is already associated with a held user
	 * 
	 * @param email
	 * @return True if recognized
	 */
	private boolean emailIsRecognized(String email) {
		Iterator<String> iterator = recognizedEmails.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().equalsIgnoreCase(email)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds loaded DefaultUserInfoMediators to singleton data on UI thread
	 * 
	 *            , List of mediators to be added
	 */
	private void addMediatorsOnUIThread(final List<ZeppaUserInfo> userInfoList) {
		try {
			application.getCurrentActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					ZeppaUserSingleton singleton = ZeppaUserSingleton
							.getInstance();

					Iterator<ZeppaUserInfo> iterator = userInfoList.iterator();
					while (iterator.hasNext()) {
						singleton.addDefaultZeppaUserMediator(iterator.next(),
								null);

					}

					finderAdapter.notifyDataSetChanged();
				}

			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
