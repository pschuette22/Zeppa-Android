package com.minook.zeppa.activities;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAuthIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.database.PreferencesHelper;
import com.minook.zeppa.zeppauserendpoint.Zeppauserendpoint;
import com.minook.zeppa.zeppauserendpoint.Zeppauserendpoint.FetchMatchingUser;
import com.minook.zeppa.zeppauserendpoint.Zeppauserendpoint.GetZeppaUser;
import com.minook.zeppa.zeppauserendpoint.Zeppauserendpoint.InsertZeppaUser;
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUser;

public class LoginActivity extends Activity implements OnClickListener,
		ConnectionCallbacks, OnConnectionFailedListener {

	/*
	 * This class handles user launching the app and ensuring that they have
	 * connection to the backend/ authentication Reference:
	 * 
	 * For plus client:
	 * https://developers.google.com/+/mobile/android/getting-started
	 * https://developers.google.com/+/mobile/android/sign-in
	 * 
	 * implements: PlusClient.ConnectionCallbacks,
	 * PlusClient.OnConnectionFailedListener,
	 * PlusClient.OnMomentsLoadedListener,
	 * 
	 * For GoogleAuthUtil
	 * http://developer.android.com/reference/com/google/android
	 * /gms/auth/GoogleAuthUtil.html
	 * http://developer.android.com/google/play-services/auth.html#obtain
	 */

	// ----------- Global Variables Bank ------------- \\
	// Debug
	private String TAG = "LoginActivity";

	// Private

	private ProgressDialog connectionProgress;
	private PlusClient plusClient;
	private ConnectionResult connectionResult;

	// Request Codes
	private static final int REQUEST_ACCOUNT_PICKER = 2;
	private static final int REQUEST_RECOVER_AUTH = 3;
	private static final int REQUEST_CODE_RESOLVE_ERR = 8000;
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	// Actions
	private final String ACTION_ADD = "http://schemas.google.com/AddActivity";

	/*
	 * ------------------- Override methods ----------------------- NOTES:
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		findViewById(R.id.sign_in_button).setOnClickListener(this);

		if (Constants.IS_CONNECTED) {

			plusClient = new PlusClient.Builder(getApplicationContext(), this,
					this).setActions(ACTION_ADD).setScopes(Scopes.PLUS_LOGIN)
					.build();

			connectionProgress = new ProgressDialog(this);
			connectionProgress.setTitle("Contacting Google+");
			connectionProgress.setMessage("Signing in...");
			connectionProgress.setCancelable(false);

			SharedPreferences prefs = getSharedPreferences(
					Constants.SHARED_PREFS, MODE_PRIVATE);
			String email = prefs.getString(Constants.EMAIL_ADDRESS, null);
			if (email != null && !email.isEmpty()) {

				plusClient.connect();
				connectionProgress.show();
			}

		} else { // Local Run

			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
			dialogBuilder.setTitle("Sign in with account name");
			final EditText textView = new EditText(this);

			dialogBuilder.setView(textView);
			dialogBuilder.setPositiveButton("Login",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which == DialogInterface.BUTTON_POSITIVE) {
								launchNLaunchDebug(textView.getText()
										.toString());
							}
							dialog.dismiss();
						}
					});
			dialogBuilder.show();

		}

	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sign_in_button:

			if (Constants.IS_CONNECTED) {
				if (checkPlayServices()) {
					if (!plusClient.isConnecting() && !plusClient.isConnected()) {
						if (connectionResult == null) {
							connectionProgress.show();
							plusClient.connect();
						} else {
							try {
								connectionResult.startResolutionForResult(this,
										REQUEST_CODE_RESOLVE_ERR);
							} catch (SendIntentException ex) {
								connectionResult = null;
								plusClient.connect();
							}

						}
					} else if (plusClient.isConnected()) {
						GoogleAccountCredential credential = setMyAccountCredential(plusClient.getAccountName());
						loadAndLaunch(credential);
					}
				}
			} else {
				AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
						this);
				dialogBuilder.setTitle("Sign in with account name");
				final EditText textView = new EditText(this);
				dialogBuilder.setView(textView);
				dialogBuilder.setPositiveButton("Login",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (which == DialogInterface.BUTTON_POSITIVE) {
									launchNLaunchDebug(textView.getText()
											.toString());
								}
								dialog.dismiss();
							}
						});

				dialogBuilder.show();
			}
			break;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.menu_login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
		case R.id.action_launchthrough:
			if (!Constants.IS_CONNECTED) {
				Intent launchMain = new Intent(getApplicationContext(),
						MainActivity.class);
				startActivity(launchMain);
				finish();
			}
			break;

		}

		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case REQUEST_CODE_RESOLVE_ERR:
			Log.d(TAG, "Got Result for Failed Connection Resolution: "
					+ resultCode);

			if (resultCode == RESULT_OK) {
				Log.d(TAG, "Result OK, retrying connection");
				connectionResult = null;
				plusClient.connect();
			}

			break;

		case REQUEST_RECOVER_AUTH:

			break;

		}

	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {

		if (connectionProgress.isShowing()) {
			connectionProgress.dismiss();
		}

		if (result.hasResolution()) {
			try {
				result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
			} catch (SendIntentException ex) {
				Log.d(TAG, "Caught SendIntentException");
				plusClient.connect();
			}
		} else {
			Toast.makeText(getApplicationContext(), "G+ Connection Failed",
					Toast.LENGTH_SHORT).show();
		}
		connectionResult = result;

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		if (connectionProgress.isShowing())
			connectionProgress.dismiss();

		GoogleAccountCredential credential = setMyAccountCredential(plusClient.getAccountName());

		if (checkPlayServices()) {
			loadAndLaunch(credential);
		} else {
			Log.d(TAG, "Play Services Check Failed..");
		}
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Account Disconnected", Toast.LENGTH_SHORT).show();
	}

	/*
	 * ---------------------- My Methods ---------------------------- NOTES:
	 */

	private void launchNLaunchDebug(String accountName) {
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setIndeterminate(true);
		dialog.setTitle("Loading/Creating Debug Account");
		dialog.setMessage("Hold your horses home boy");
		String[] params = { accountName };
		new AsyncTask<String, Void, ZeppaUser>() {

			@Override
			protected ZeppaUser doInBackground(String... params) {
				ZeppaUser zeppaUser = null;
				String accountName = params[0];

				try {
					Zeppauserendpoint.Builder endpointBuilder = new Zeppauserendpoint.Builder(
							AndroidHttp.newCompatibleTransport(),
							new JacksonFactory(), null);
					endpointBuilder = CloudEndpointUtils
							.updateBuilder(endpointBuilder);
					Zeppauserendpoint userEndpoint = endpointBuilder.build();

					FetchMatchingUser fetchMatch = userEndpoint
							.fetchMatchingUser(accountName);
					zeppaUser = fetchMatch.execute();

					if (zeppaUser == null) {
						zeppaUser = newDebugInstanceFor(accountName);

						try {
							InsertZeppaUser insertUser = userEndpoint
									.insertZeppaUser(zeppaUser);
							zeppaUser = insertUser.execute();

						} catch (IOException e) {
							e.printStackTrace();
							return null;
						}

					}

				} catch (IOException ex) {
					ex.printStackTrace();
				}

				return zeppaUser;
			}

			@Override
			protected void onPostExecute(ZeppaUser result) {
				super.onPostExecute(result);

				if (result == null) {
					Toast.makeText(getApplicationContext(),
							"Backend Error, Try Again Later",
							Toast.LENGTH_SHORT).show();
				} else {
					((ZeppaApplication) getApplication()).initialize(result);

					Intent launchMain = new Intent(getApplicationContext(),
							MainActivity.class);
					launchMain.putExtra(Constants.INTENT_NOTIFICATIONS, false);
					startActivity(launchMain);
					finish();
				}

			}

		}.execute(params);

	}

	private ZeppaUser newUserInstanceFor(Context context, Person currentPerson) {
		ZeppaUser zeppaUser = new ZeppaUser();

		if (currentPerson.getName().hasGivenName()) {
			zeppaUser.setGivenName(currentPerson.getName().getGivenName());
		} else if (currentPerson.hasNickname()) {
			zeppaUser.setGivenName(currentPerson.getNickname());
		} else {
			zeppaUser.setGivenName(currentPerson.getDisplayName());
		}

		if (currentPerson.getName().hasFamilyName()) {
			zeppaUser.setFamilyName(currentPerson.getName().getFamilyName());
		} else {
			zeppaUser.setFamilyName(""); // empty string so
											// doesnt retun null
		}

		String imgUrl = currentPerson.getImage().getUrl();
		// TODO: verify this works correctly
		if (imgUrl.endsWith("?sz=50"))
			imgUrl = imgUrl.substring(0, (imgUrl.length()) - 6);
		zeppaUser.setImageUrl(imgUrl);

		zeppaUser.setProfileId(currentPerson.getId());
		String accountName = ((ZeppaApplication) context
				.getApplicationContext()).getAccountName();
		zeppaUser.setEmail(accountName);
		zeppaUser.setPhoneNumber(getPhoneNumber(context));

		zeppaUser.setDateJoined(Long.valueOf(System.currentTimeMillis()));
		zeppaUser.setLastEdited(Long.valueOf(System.currentTimeMillis()));
		zeppaUser.setContactIds(new ArrayList<Long>());
		zeppaUser.setNewTagFollowerIds(new ArrayList<Long>());
		zeppaUser.setBlockedUserIds(new ArrayList<Long>());
		zeppaUser.setFriendRequestIds(new ArrayList<Long>());
		zeppaUser.setDeviceRegistrationIds(new ArrayList<String>());

		return zeppaUser;
	}

	private ZeppaUser newDebugInstanceFor(String accountName) {
		ZeppaUser zeppaUser = new ZeppaUser();
		zeppaUser.setGivenName(accountName);
		zeppaUser.setImageUrl(null);

		zeppaUser.setProfileId(accountName);
		zeppaUser.setEmail(accountName);
		zeppaUser.setPhoneNumber(null);

		zeppaUser.setDateJoined(Long.valueOf(System.currentTimeMillis()));
		zeppaUser.setLastEdited(Long.valueOf(System.currentTimeMillis()));
		zeppaUser.setContactIds(new ArrayList<Long>());
		zeppaUser.setNewTagFollowerIds(new ArrayList<Long>());
		zeppaUser.setBlockedUserIds(new ArrayList<Long>());
		zeppaUser.setFriendRequestIds(new ArrayList<Long>());
		zeppaUser.setDeviceRegistrationIds(new ArrayList<String>());

		return zeppaUser;
	}

	private String getPhoneNumber(Context context) {
		TelephonyManager tMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tMgr.getLine1Number();
	}

	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	private GoogleAccountCredential setMyAccountCredential(String accountName) {

		GoogleAccountCredential credential = GoogleAccountCredential
				.usingAudience(getApplicationContext(),
						Constants.APP_ENGINE_AUDIENCE_CODE);

		SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFS,
				MODE_PRIVATE);
		String email = prefs.getString(Constants.EMAIL_ADDRESS, null);

		if (email == null || !email.equals(accountName)) {
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString(Constants.EMAIL_ADDRESS, accountName).apply();
			editor.commit();
		}

		credential.setSelectedAccountName(accountName);
		((ZeppaApplication) getApplication()).setCredential(credential);
		return credential;

	}

	private void loadAndLaunch(GoogleAccountCredential credential) {

		// TODO: set this as the only dialog that is shown
		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(R.string.getting_details);
		progressDialog.setMessage("One Moment, Please...");
		progressDialog.setCancelable(false);
		progressDialog.show();
		
		Object[] params = {getApplicationContext(), credential};

		new AsyncTask<Object, Void, ZeppaUser>() {

			@Override
			protected ZeppaUser doInBackground(Object... params) {

				Context context = (Context) params[0];
				GoogleAccountCredential credential = (GoogleAccountCredential) params[1];
				ZeppaUser zeppaUser = null;

				Zeppauserendpoint.Builder endpointBuilder = new Zeppauserendpoint.Builder(
						AndroidHttp.newCompatibleTransport(),
						new JacksonFactory(), credential);
				endpointBuilder = CloudEndpointUtils
						.updateBuilder(endpointBuilder);

				Zeppauserendpoint userEndpoint = endpointBuilder.build();
				try {

					Person currentPerson = plusClient.getCurrentPerson();

					// This is probably a reeeeallllly shitty way of doing this...
					Long appEngineId = PreferencesHelper.getHeldUserIdForAccountWithoutInitializing(context,
									credential.getSelectedAccountName());
					
					if (appEngineId.longValue() > 0) {

						GetZeppaUser fetchUserById = userEndpoint.getZeppaUser(appEngineId);
						zeppaUser = fetchUserById.execute();
						
					} else {

						FetchMatchingUser fetchUserByPlusId = userEndpoint
								.fetchMatchingUser(currentPerson.getId());
						zeppaUser = fetchUserByPlusId.execute();
					}

					if (zeppaUser == null) {

						zeppaUser = newUserInstanceFor(getApplicationContext(),
								currentPerson);

						try {
							InsertZeppaUser insertUser = userEndpoint
									.insertZeppaUser(zeppaUser);
							zeppaUser = insertUser.execute();

						} catch (IOException e) {
							e.printStackTrace();
							return null;
						}

					}

				} catch (UserRecoverableAuthIOException authIOEx) {
					Log.d(TAG, "Recoverable Exception Cause: "
							+ authIOEx.getCause().getMessage());
					authIOEx.printStackTrace();
					startActivityForResult(authIOEx.getIntent(),
							REQUEST_RECOVER_AUTH);
					return null;
				}

				catch (GoogleAuthIOException authEx) {
					// TODO Auto-generated catch block

					Log.d(TAG, "GoogleAuthIOException Cause: "
							+ authEx.getCause().getLocalizedMessage() + ": "
							+ authEx.getCause().hashCode());

					authEx.printStackTrace();

					return null;
				} catch (IOException ioEx) {
					Log.d(TAG, "IOException caught");
					ioEx.printStackTrace();
					return null;
				}

				return zeppaUser;
			}

			@Override
			protected void onPostExecute(ZeppaUser result) {
				super.onPostExecute(result);
				Log.d(TAG, "post execute called");
				if (result == null) {
					Toast.makeText(getApplicationContext(), "Connection Error",
							Toast.LENGTH_SHORT).show();
				} else { // Launch Main
					((ZeppaApplication) getApplication()).initialize(result);

					Intent launchMain = new Intent(getApplicationContext(),
							MainActivity.class);
					launchMain.putExtra(Constants.INTENT_NOTIFICATIONS, false);
					startActivity(launchMain);
					finish();
				}
				progressDialog.dismiss();

			}

		}.execute(params);

	}
}
