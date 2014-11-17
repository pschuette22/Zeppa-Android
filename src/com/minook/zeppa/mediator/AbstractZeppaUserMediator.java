package com.minook.zeppa.mediator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.minook.zeppa.utils.Utils;


public abstract class AbstractZeppaUserMediator extends AbstractMediator{

	protected long lastUpdateTimeInMillis;

	
	protected List<ImageView> setOnImageLoad;
	protected Bitmap userImage;

	public AbstractZeppaUserMediator() {
		this.lastUpdateTimeInMillis = System.currentTimeMillis();
		this.setOnImageLoad = new ArrayList<ImageView>();
	}


	public abstract String getGivenName();
	public abstract String getFamilyName();
	public abstract String getDisplayName();
	public abstract String getUnformattedPhoneNumber();
	public abstract String getGmail();
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
		if (userImage != null) {
			image.setImageBitmap(userImage);
		} else if (!setOnImageLoad.contains(image)) {
			setOnImageLoad.add(image);
		}
	}
	
	
	/**
	 * This method loads an image in async and sets waiting views once loaded
	 * @param imageUrl, url of the requested image
	 */
	protected void loadImageInAsync(String imageUrl) {
		
		Object[] params = { imageUrl };
		new AsyncTask<Object, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(Object... params) {
				String url = (String) params[0];
				try {
					return Utils.loadImageBitmapFromUrl(url);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null; // TODO: make it try to load again later
				}
				
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);

				if (result != null) {
					userImage = result;
					onImageLoad();
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
	
}
