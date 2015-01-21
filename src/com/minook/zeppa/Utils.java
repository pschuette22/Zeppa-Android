package com.minook.zeppa;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.zeppanotificationendpoint.model.ZeppaNotification;

public class Utils {
	private static final String TAG = "ZeppaUtils";
	
	
	/*
	 * encoding methods
	 */
	public static String encodeListString(List<String> tempList) {
		StringBuilder stringbuilder = new StringBuilder();
		stringbuilder.append(tempList.get(0));
		if (tempList.size() > 1) {
			for (int i = 1; i < tempList.size(); i++) {
				stringbuilder.append(",");
				stringbuilder.append(tempList.get(i).trim());
			}
		}

		return stringbuilder.toString();
	}

	public static String encodeListLong(ArrayList<Long> longList) {
		StringBuilder stringbuilder = new StringBuilder();
		stringbuilder.append(longList.get(0));
		if (longList.size() > 1) {
			for (int i = 0; i < longList.size(); i++) {
				stringbuilder.append(",");
				stringbuilder.append(longList.get(i));
			}
		}

		return stringbuilder.toString();
	}

	/*
	 * constant methods
	 */

	public static String getDisplayDatesString(long startMillis, long endMillis) {

		StringBuilder result = new StringBuilder();

		// most events
		result.append(getDayString(startMillis));
		result.append(" ");
		result.append(getDisplayTimeString(startMillis));
		result.append(" - ");

		if ((startMillis - endMillis) >= (24 * 60 * 60 * 1000)) {
			result.append(getDateString(endMillis));
			result.append(" ");
		}

		result.append(getDisplayTimeString(endMillis));

		return result.toString();
	}

	public static String getDisplayDateString(long dateMillis) {
		StringBuilder builder = new StringBuilder();

		builder.append(getDayString(dateMillis));
		builder.append(" ");
		builder.append(getDisplayTimeString(dateMillis));
		return builder.toString();
	}

	private static String getDisplayTimeString(long dateMillis) {

		StringBuilder builder = new StringBuilder();
		// TODO: check prefs if they want 24 hr clock

		long timeBetween = (System.currentTimeMillis() - dateMillis);
		if (timeBetween >= 0) {
			if (timeBetween < (1000 * 60)) {
				builder.append("Just now");
			} else if (timeBetween < (3000 * 60)) { // less than 3 minute ago
				builder.append("A few moments ago");
			} else if (timeBetween < (30 * 1000 * 60)) {
				builder.append((timeBetween / (1000 * 60)) + " minutes ago");
			} else {
				builder.append(getTimeAsString(dateMillis));
			}
		} else {
			timeBetween *= -1;
			if (timeBetween < (3 * 1000 * 60)) { // in within 3 minutes
				builder.append("Right Now");
			} else if (timeBetween < (30 * 1000 * 60)) {
				builder.append((timeBetween / (1000 * 60)) + " minutes");
			} else {
				builder.append(getTimeAsString(dateMillis));
			}

		}

		return builder.toString();
	}

	private static String getTimeAsString(long dateMillis) {
		StringBuilder builder = new StringBuilder();

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(dateMillis);
		boolean isAm = true;
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		if (hour > 12) {
			hour -= 12;
			isAm = false;
		} else if (hour == 12) {
			isAm = false;
		}

		if (hour == 0) {
			hour = 12;
		}

		builder.append(hour);
		builder.append(":");

		int minute = cal.get(Calendar.MINUTE);
		builder.append(minute < 10 ? "0" + minute : minute);

		builder.append(isAm ? "am" : "pm");

		return builder.toString();
	}

	// TODO: make this more accurate

	private static String getDayString(long dateMillis) {
		StringBuilder builder = new StringBuilder();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(dateMillis);
		long currentTime = System.currentTimeMillis();
		Calendar today = Calendar.getInstance();
		
		if (cal.get(Calendar.DATE) == today.get(Calendar.DATE)) {
			builder.append("");
		
		} else if (currentTime > dateMillis) {
			// Date is in the past
			if ((currentTime - dateMillis) < (1000 * 60 * 60 * 2)) {
				builder.append("Yesterday");
			} else if ((currentTime - dateMillis) < (1000 * 60 * 60 * 7)) {
				builder.append("Last");
				builder.append(" ");
				builder.append(cal.getDisplayName(Calendar.DAY_OF_WEEK,
						Calendar.LONG, Locale.getDefault()));
			} else {
				builder.append(getDateString(dateMillis));
			}

		} else {
			Calendar tomorrow = Calendar.getInstance();
			tomorrow.add(Calendar.DATE, 1);

			if (cal.get(Calendar.DATE) == tomorrow.get(Calendar.DATE)) {
				builder.append("Tomorrow");
			} else if ((dateMillis - currentTime) < (1000 * 60 * 60 * 24 * 7)) {
				builder.append(cal.getDisplayName(Calendar.DAY_OF_WEEK,
						Calendar.LONG, Locale.getDefault()));
			} else {
				builder.append(getDateString(dateMillis));
			}

		}

		return builder.toString();
	}

	private static String getDateString(long dateMillis) {
		StringBuilder builder = new StringBuilder();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(dateMillis);
		// TODO: check if they want MM/DD or DD/MM

		builder.append((cal.get(Calendar.MONTH) + 1));
		builder.append("/");
		builder.append(cal.get(Calendar.DAY_OF_MONTH));

		builder.append("/");
		builder.append(cal.get(Calendar.YEAR));

		return builder.toString();
	}
	
	
	public static String getPrivacy(int position){
		switch (position){
		case 0:
			return "CASUAL";
			
		case 1:
			return "PUBLIC";
			
		case 2:
			return "PRIVATE";

		default:
			return "CASUAL";
		}
	}
	
	
	/*
	 * Comparators
	 */

//	public static final Comparator<ZeppaUser> USER_COMPARATOR = new Comparator<ZeppaUser>() {
//
//		@Override
//		public int compare(ZeppaUser lhs, ZeppaUser rhs) {
//			// TODO Auto-generated method stub
//			return (lhs.getGivenName() + " " + lhs.getFamilyName()).compareToIgnoreCase(
//					rhs.getGivenName() + " " + rhs.getFamilyName());
//		}
//
//	};
//
//	public static final Comparator<ZeppaUser> USER_FINDER_COMPARATOR = new Comparator<ZeppaUser>() {
//
//		@Override
//		public int compare(ZeppaUser lhs, ZeppaUser rhs) {
//			List<Long> requestIds = ZeppaUserSingleton.getInstance().getUser()
//					.getFriendRequestIds();
//			boolean lRequested = requestIds.contains(lhs.getKey().getId());
//			boolean rRequested = requestIds.contains(rhs.getKey().getId());
//			if (lRequested && !rRequested) {
//				return 1;
//			} else if (!lRequested && rRequested) {
//				return -1;
//			} else {
//				return (lhs.getGivenName() + " " + lhs.getFamilyName()).compareToIgnoreCase(
//						rhs.getGivenName() + " " + rhs.getFamilyName());
//			}
//		}
//
//	};
//
//	public static final Comparator<ZeppaEvent> EVENT_COMPARATOR = new Comparator<ZeppaEvent>() {
//
//		@Override
//		public int compare(ZeppaEvent lhs, ZeppaEvent rhs) {
//			// TODO Auto-generated method stub
//			return ((int) (((long) lhs.getStart()) - ((long) rhs.getStart())));
//
//		}
//
//	};

	public static final Comparator<ZeppaNotification> NOTIFICAITON_COMPARATOR = new Comparator<ZeppaNotification>() {

		@Override
		public int compare(ZeppaNotification lhs, ZeppaNotification rhs) {

			return ((int) ((lhs.getCreated().longValue()) - (rhs.getCreated().longValue())));

		}

	};
	
	
	/**
	 * This method takes an unformatted 10-digit string for a US phone number</p>
	 * and returns it in expected format.
	 * 
	 * @param unformatedNumber	10 digit unformated phone number
	 * @return formatedString 	number formatted like (123) 456-7890
	 */
	public static String formatPhoneNumber(String unformatedNumber) {

		if (unformatedNumber == null || unformatedNumber.isEmpty()) {
			return null;
		}

		StringBuilder builder = new StringBuilder();

		builder.append("(");
		builder.append(unformatedNumber.substring(1, 4)); // area code
		builder.append(") ");
		builder.append(unformatedNumber.substring(4, 7)); // first Three
		builder.append("-");
		builder.append(unformatedNumber.substring(7)); // Last four

		return builder.toString(); // Number in form: (123)456-7890
	}
	
	public static String make10DigitNumber(String contactPhoneNumber){
		StringBuilder builder = new StringBuilder();
		
		if(contactPhoneNumber.charAt(0) == '1'){
			if(contactPhoneNumber.length() == 10){
				Log.d(TAG, "Aleady correctForm: " + contactPhoneNumber);
				return contactPhoneNumber;
			} else {
				contactPhoneNumber = contactPhoneNumber.substring(1);
			}
		}
		
		builder.append("1");
		for(int i = 0; i < contactPhoneNumber.length(); i++){
			char c = contactPhoneNumber.charAt(i);
			if(Character.isDigit(c)){
				builder.append(c);
			}
		}
		
		return builder.toString();
	}

	/**
	 * This method loads the bitmap of an image from its URL
	 * 
	 * @param url				URL of the image
	 * @return	bitmap			image as a bitmap
	 * @throws IOException		connection error
	 */
	public static Bitmap loadImageBitmapFromUrl(String url) throws IOException {

		HttpURLConnection connection = (HttpURLConnection) new URL(url)
				.openConnection();
		connection.connect();
		InputStream input = connection.getInputStream();

		return BitmapFactory.decodeStream(input);

	}
	
	public static View makeLoaderView(AuthenticatedFragmentActivity context, String text) {
		View loaderView = context.getLayoutInflater().inflate(
				R.layout.view_loaderview, null, false);
		((ProgressBar) loaderView.findViewById(R.id.loaderview_progressbar))
				.setIndeterminate(true);
		((TextView) loaderView.findViewById(R.id.loaderview_text))
				.setText(text);

		return loaderView;
	}
}
