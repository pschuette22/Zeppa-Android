package com.minook.zeppa.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Utils {
	private static final String TAG = "ZeppaUtils";
	
	/**
	 * This method takes an unformatted 10-digit string for a US phone number</p>
	 * and returns it in expected format.
	 * 
	 * @param unformatedNumber	10 digit unformated phone number
	 * @return formatedString 	number formatted like (123) 456-7890
	 */
	public static String formatPhoneNumber(String unformatedNumber) {

		Log.d(TAG, "Unformatted number: " + unformatedNumber);
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

		return builder.toString(); // Number in form: (123) 456-7890
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
}
