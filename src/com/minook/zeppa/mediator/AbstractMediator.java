package com.minook.zeppa.mediator;

import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;

public abstract class AbstractMediator {

	private final String TAG = "AbstractMediator";
	private AuthenticatedFragmentActivity context;
	protected Long timeCreatedInMillis;
	protected Long lastRefreshTimeInMillis;

	public void convertView(AuthenticatedFragmentActivity context) {
		setContext(context);
		lastRefreshTimeInMillis = System.currentTimeMillis();
		timeCreatedInMillis = System.currentTimeMillis();
	}

	/**
	 * This method sets the current mediators context to null if it matches the
	 * calling context
	 */
	public void killContextIfMatching(AuthenticatedFragmentActivity context) {
		if (this.context == context) {
			this.context = null;
		}
	}

	public void setContext(AuthenticatedFragmentActivity context) {

		if (context != null && this.context == context) {
			Log.d(TAG, "Matching Context");
		} else {
			if (context.addHeldContext(this)) {
				Log.d(TAG, "Context already held");
			}
			this.context = context;

		}

	}

	protected AuthenticatedFragmentActivity getContext()
			throws NullPointerException {

		if (context == null) {
			throw new NullPointerException("Context for mediator is null");
		}

		return context;
	}
	
	protected GoogleAccountCredential getGoogleAccountCredential(){
		return context.getGoogleAccountCredential();
	}

}
