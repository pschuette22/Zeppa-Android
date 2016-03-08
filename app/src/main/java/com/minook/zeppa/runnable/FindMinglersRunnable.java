package com.minook.zeppa.runnable;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.appspot.zeppa_cloud_1821.zeppaclientapi.Zeppaclientapi;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.CollectionResponseZeppaUserInfo;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaUserInfo;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ApiClientHelper;
import com.minook.zeppa.Utils;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.adapter.MinglerFinderAdapter;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

import org.json.JSONArray;

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

		/*
		 * Query by numbers
		 */

		Cursor numberCursor = resolver.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null, null);

		if (numberCursor.moveToFirst()) {
			List<String> phoneNumberList = new ArrayList<String>();
			int characterCount = 0;
            int totalNumberCount=0;
			do {
				try {
					String number = getFormattedNumber(numberCursor);

					if (!numberIsRecognized(number) && !phoneNumberList.contains(number)) {
							phoneNumberList.add(number);
							characterCount+=number.length();
                            totalNumberCount++;

							if (characterCount > 1750
									|| numberCursor.isLast()) {


								executeQuery("listParam.contains(phoneNumber)", phoneNumberList);
								phoneNumberList.clear();
								characterCount=0;

							}


					}
				} catch (IndexOutOfBoundsException e) {
					// Dont do anything
				}

			} while (numberCursor.moveToNext());

		}

		numberCursor.close();

		/*
		 * Iterate through all GMail addresses saved to phone and check if they
		 * exist in zeppa.
		 */
		Cursor emailCursor = resolver.query(
				ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, null,
				null, null);

		if (emailCursor.moveToFirst()) {
			List<String> emailList = new ArrayList<String>();
			int characterCount=0;
            int totalEmailCount=0;
			do {
				String email = getEmail(emailCursor);
				if (!emailIsRecognized(email) && !emailList.contains(email)) {
						emailList.add(email);
						characterCount+=email.length();
                        totalEmailCount++;

						if (characterCount > 1750
								|| emailCursor.isLast()) {

							executeQuery("listParam.contains(authEmail)", emailList);
                            emailList.clear();
                            characterCount=0;

						}

				}

			} while (emailCursor.moveToNext());

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

	private void executeQuery(String filter, List<String> listArg) {

		ApiClientHelper helper = new ApiClientHelper();
		Zeppaclientapi api = helper.buildClientEndpoint();

		String cursor = null;
        JSONArray array = new JSONArray();
        for(String s: listArg){
            array.put(s);
        }


		try {

			Zeppaclientapi.ListZeppaUserInfo listUserInfo = api.listZeppaUserInfo(credential.getToken());
			listUserInfo.setFilter(filter);
			listUserInfo.setCursor(cursor);
			listUserInfo.setJsonArgs(array.toString());
			CollectionResponseZeppaUserInfo result = listUserInfo.execute();

			if (result != null && result.getItems() != null
					&& !result.getItems().isEmpty()) {

				Iterator<ZeppaUserInfo> iterator = result.getItems().iterator();

				List<ZeppaUserInfo> uniqueInfoItems = new ArrayList<ZeppaUserInfo>();
				while (iterator.hasNext()) {
					ZeppaUserInfo info = iterator.next();

					// If user id is unrecognized, add to list of users that should be added
					if (ZeppaUserSingleton.getInstance().getAbstractUserMediatorById(info.getKey().getParent().getId())==null) {
						uniqueInfoItems.add(info);
					}

				}

				// Add the unique users to the UI
				addMediatorsOnUIThread(uniqueInfoItems);

			}

		} catch (IOException | GoogleAuthException e) {
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
//		Iterator<String> iterator = recognizedNumbers.iterator();
//
//		while (iterator.hasNext()) {
//			if (iterator.next().equalsIgnoreCase(phoneNumber)) {
//				return true;
//			}
//		}
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
