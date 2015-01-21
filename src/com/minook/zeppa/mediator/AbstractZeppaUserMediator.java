package com.minook.zeppa.mediator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.minook.zeppa.Utils;

public abstract class AbstractZeppaUserMediator extends AbstractMediator implements Comparable<AbstractZeppaUserMediator>{

	protected long lastUpdateTimeInMillis;

	protected List<ImageView> setOnImageLoad;
	protected Bitmap userImage;

	private boolean loadingImage = false;
	private boolean didLoadImage = false;

	public AbstractZeppaUserMediator() {
		this.lastUpdateTimeInMillis = System.currentTimeMillis();
		this.setOnImageLoad = new ArrayList<ImageView>();
	}

	public abstract String getGivenName();

	public abstract String getFamilyName();

	public abstract String getDisplayName();

	public abstract String getUnformattedPhoneNumber();

	public abstract String getGmail();

	protected abstract String getImageUrl();

	public abstract Long getUserId();

	public String getPrimaryPhoneNumber() throws NullPointerException {
		return Utils.formatPhoneNumber(getUnformattedPhoneNumber());
	}

	/**
	 * This method sets the user image if it is loaded and held</p> If image has
	 * not yet loaded and image is not already waiting,</p> It adds the image to
	 * a list of images that will be set when load finishes
	 * 
	 * @param image
	 */

	public void setImageWhenReady(ImageView image) {
		try {
			if (image != null) {
				if (didLoadImage) {
					image.setImageBitmap(userImage);
				} else if (!setOnImageLoad.contains(image)) {
					setOnImageLoad.add(image);
				}
			} else {
				if (!loadingImage) {
					loadImageInAsync(getImageUrl());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method loads an image in async and sets waiting views once loaded
	 * 
	 * @param imageUrl
	 *            , url of the requested image
	 */
	protected void loadImageInAsync(String imageUrl) {

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
				// TODO Auto-generated method stub
				super.onPostExecute(result);

				if (result != null) {
					userImage = result;
					didLoadImage = true;
					onImageLoad();
				} else {
					loadingImage = false;
				}
			}

		}.execute(params);
	}

	/**
	 * This method sets the user image to imageviews waiting for the load.</p>
	 * It will only be called after the image is loaded from loadUserImage.
	 */
	private void onImageLoad() {
		Iterator<ImageView> iterator = setOnImageLoad.iterator();
		while (iterator.hasNext()) {
			try {
				iterator.next().setImageBitmap(userImage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public int compareTo(AbstractZeppaUserMediator another) {

		return getDisplayName().compareTo(another.getDisplayName());
	}
	
	

}
