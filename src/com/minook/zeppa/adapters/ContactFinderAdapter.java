package com.minook.zeppa.adapters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.R;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppauserendpoint.Zeppauserendpoint;
import com.minook.zeppa.zeppauserendpoint.Zeppauserendpoint.RequestContactConnection;
import com.minook.zeppa.zeppauserendpoint.Zeppauserendpoint.RespondToContactRequest;
import com.minook.zeppa.zeppauserendpoint.Zeppauserendpoint.RevokeContactRequest;
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUser;

public class ContactFinderAdapter extends BaseAdapter implements
		OnClickListener {

	private Context context;
	private LayoutInflater inflater;
	private ZeppaUser user;

	private List<ZeppaUser> pendingList;
	private List<ZeppaUser> potentialList;

	public ContactFinderAdapter(Context context) {
		this.context = context;
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.user = ZeppaUserSingleton.getInstance().getUser();

		potentialList = new ArrayList<ZeppaUser>();
		pendingList = new ArrayList<ZeppaUser>();

	}

	@Override
	public void notifyDataSetChanged() {
		List<ZeppaUser> contacts = ZeppaUserSingleton.getInstance()
				.getHeldUsers();
		if (!contacts.isEmpty()) {
			List<Long> pendingIds = user.getFriendRequestIds();

			if (pendingIds == null) {
				potentialList.addAll(contacts);
			} else {
				for (ZeppaUser contact : contacts) {
					if (pendingIds.contains(contact.getKey().getId())) {
						pendingList.add(contact);
					} else {
						potentialList.add(contact);
					}
				}
			}
		}

		super.notifyDataSetChanged();
	}

	private boolean isPending(ZeppaUser contact) {
		return pendingList.contains(contact);
	}

	@Override
	public int getCount() { // size is the addition between pending and
		// possible connections
		return (pendingList.size() + potentialList.size() + 2);
	}

	@Override
	public ZeppaUser getItem(int position) {
		if (position == 0) {
			return null;
		} else if (position == (1 + pendingList.size())) {
			return null;
		} else if (position <= pendingList.size()) {
			return pendingList.get(position - 1);
		} else {
			return potentialList.get(position - (2 + pendingList.size()));
		}
	}

	@Override
	public long getItemId(int position) {
		ZeppaUser item = getItem(position);
		if (item == null)
			return Long.valueOf(-1);
		return item.getKey().getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ZeppaUser user = getItem(position);

		if (user == null) {
			convertView = inflater.inflate(R.layout.view_sectionheader, null,
					false);
			TextView sectionText = (TextView) convertView
					.findViewById(R.id.sectionheader_text);
			sectionText.setText(getHeader(position));
			convertView.setClickable(false);
			return convertView;
		} else {

			convertView.setTag(user);

			String name = user.getDisplayName();
			ImageView userImage = null;
			TextView nameText = null;

			if (isPending(user)) {
				convertView = inflater.inflate(
						R.layout.view_addcontact_itemrespond, null, false);
				// convertView.setClickable(true);
				convertView.setClickable(true);

				userImage = (ImageView) convertView
						.findViewById(R.id.newcontact_responditem_picture);
				nameText = (TextView) convertView
						.findViewById(R.id.newcontact_responditem_name);

				convertView.findViewById(R.id.newcontact_confirm_button)
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View view) {

								respondToInviteInAsync(user, true);

								Toast.makeText(
										context,
										"Connected with "
												+ user.getDisplayName(),
										Toast.LENGTH_SHORT).show();
							}
						});

				convertView.findViewById(R.id.newcontact_deny_button)
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View view) {

								respondToInviteInAsync(user, false);
							}
						});

			} else {
				convertView = inflater.inflate(
						R.layout.view_addcontact_itemsend, null, false);
				// convertView.setClickable(true);
				convertView.setEnabled(true);

				userImage = (ImageView) convertView
						.findViewById(R.id.newcontact_senditem_picture);
				nameText = (TextView) convertView
						.findViewById(R.id.newcontact_senditem_name);

				final CheckBox requestButton = (CheckBox) convertView
						.findViewById(R.id.newcontact_senditem_button);

				requestButton.setChecked(user.getFriendRequestIds() != null
						&& user.getFriendRequestIds().contains(
								ZeppaUserSingleton.getInstance().getUserId()));

				requestButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {

						if (requestButton.isChecked()) {
							requestConnectionInAsync(user, requestButton);

						} else {
							revokeRequestInAsync(user, requestButton);

						}
					}
				});

			}

			nameText.setText(name);
			setImageInAsync(userImage, user);
			convertView.setClickable(false);

			convertView.setOnClickListener(this);

			return convertView;
		}
	}

	@Override
	public void onClick(View view) {

		ZeppaUser user = (ZeppaUser) ((View) view.getParent()).getTag();

		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		View contentView = inflater.inflate(
				R.layout.view_possiblecontact_inspect, null, false);

		ImageView imageView = (ImageView) contentView
				.findViewById(R.id.possiblecontactview_image);
		((TextView) contentView.findViewById(R.id.possiblecontactview_name))
				.setText(user.getDisplayName());
		((TextView) contentView.findViewById(R.id.possiblecontactview_email))
				.setText(user.getEmail());
		((TextView) contentView.findViewById(R.id.possiblecontactview_phone))
				.setText(user.getPhoneNumber());

		builder.setView(contentView);
		builder.setAdapter(null, dialogOptionsListener(user));

		builder.show();

		setImageInAsync(imageView, user);

	}

	/*
	 * Private methods
	 */

	private String getHeader(int position) {
		Resources res = context.getResources();
		if (position == 0) {
			return res.getString(R.string.pending_requests);
		} else {
			return res.getString(R.string.contacts_using);
		}
	}

	private DialogInterface.OnClickListener dialogOptionsListener(
			final ZeppaUser user) {

		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				if (which == DialogInterface.BUTTON_NEGATIVE) {
					dialog.dismiss();
				} else {

				}
			}
		};

		return listener;
	}

	/*
	 * AsyncTasks
	 */

	private void setImageInAsync(final ImageView userImage, final ZeppaUser user) {
		new AsyncTask<Void, Void, Drawable>() {

			@Override
			protected Drawable doInBackground(Void... params) {
				return ZeppaUserSingleton.getInstance().getUserImage(user);
			}

			@Override
			protected void onPostExecute(Drawable result) {
				super.onPostExecute(result);
				if (result != null) {
					userImage.setImageDrawable(result);
				}
			}

		}.execute();

	}

	private void respondToInviteInAsync(final ZeppaUser askingUser,
			final boolean didConfirm) {

		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				boolean success = false;
				Zeppauserendpoint.Builder endpointBuilder = new Zeppauserendpoint.Builder(
						AndroidHttp.newCompatibleTransport(),
						new JacksonFactory(),
						((ZeppaApplication) context.getApplicationContext())
								.getGoogleAccountCredential());

				endpointBuilder = CloudEndpointUtils
						.updateBuilder(endpointBuilder);
				Zeppauserendpoint endpoint = endpointBuilder.build();

				try {
					RespondToContactRequest responseTask = endpoint
							.respondToContactRequest(ZeppaUserSingleton
									.getInstance().getUserId(), askingUser
									.getKey().getId(), didConfirm);
					responseTask.execute();

					ZeppaUserSingleton.getInstance().getUser()
							.getFriendRequestIds()
							.remove(askingUser.getKey().getId());

					if (didConfirm) {
						ZeppaUserSingleton.getInstance().addFriendZeppaUser(
								askingUser);
					}

					success = true;
				} catch (IOException e) {
					e.printStackTrace();
				}

				return success;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);

				if (result) {
					// Yay, nothing went wrong
				} else {
					Toast errorToast = Toast
							.makeText(context, "Connect Error: Check Network!",
									Toast.LENGTH_SHORT);
					errorToast.show();
				}
			}

		}.execute();

	}

	private void requestConnectionInAsync(ZeppaUser zeppaUser,
			final CheckBox button) {

		ZeppaUser[] params = { zeppaUser };
		new AsyncTask<ZeppaUser, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(ZeppaUser... params) {
				boolean success = false;
				ZeppaUser zeppaUser = params[0];
				Zeppauserendpoint.Builder endpointBuilder = new Zeppauserendpoint.Builder(
						AndroidHttp.newCompatibleTransport(),
						new JacksonFactory(),
						((ZeppaApplication) context.getApplicationContext())
								.getGoogleAccountCredential());
				endpointBuilder = CloudEndpointUtils
						.updateBuilder(endpointBuilder);
				Zeppauserendpoint endpoint = endpointBuilder.build();

				try {
					RequestContactConnection requestConnection = endpoint
							.requestContactConnection(ZeppaUserSingleton
									.getInstance().getUserId(), zeppaUser
									.getKey().getId());

					requestConnection.execute();
					success = true;

				} catch (IOException e) {
					e.printStackTrace();
				}

				return success;
			}

			@Override
			protected void onPostExecute(Boolean success) {
				super.onPostExecute(success);

				button.setEnabled(true);
				if (success) {
					button.setChecked(true);
					// yay
				} else {
					button.setChecked(false);
					Toast errorToast = Toast.makeText(context,
							"Network Error: Check Connection!",
							Toast.LENGTH_LONG);
					errorToast.show();
				}

			}

		}.execute(params);

	}

	private void revokeRequestInAsync(final ZeppaUser zeppaUser,
			final CheckBox button) {

		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				Boolean success = false;
				Zeppauserendpoint.Builder endpointBuilder = new Zeppauserendpoint.Builder(
						AndroidHttp.newCompatibleTransport(),
						new JacksonFactory(),
						((ZeppaApplication) context.getApplicationContext())
								.getGoogleAccountCredential());
				endpointBuilder = CloudEndpointUtils
						.updateBuilder(endpointBuilder);
				Zeppauserendpoint endpoint = endpointBuilder.build();

				try {

					RevokeContactRequest revokeRequest = endpoint
							.revokeContactRequest(ZeppaUserSingleton
									.getInstance().getUserId(), zeppaUser
									.getKey().getId());
					revokeRequest.execute();

					success = true;
					zeppaUser.getFriendRequestIds().remove(
							ZeppaUserSingleton.getInstance().getUserId());

				} catch (IOException e) {
					e.printStackTrace();
				}

				return success;
			}

			@Override
			protected void onPostExecute(Boolean success) {
				super.onPostExecute(success);

				button.setEnabled(true);
				if (success) {
					button.setChecked(false);
				} else {
					button.setChecked(true);
					Toast errorToast = new Toast(context);
					errorToast.setDuration(Toast.LENGTH_LONG);
					errorToast.setText("Network Error: Check Connection!");
				}

			}

		}.execute();
	}

}
