package com.minook.zeppa.fragmentdialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.R;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.adapters.ContactListAdapter;
import com.minook.zeppa.eventtagendpoint.model.EventTag;
import com.minook.zeppa.singleton.EventTagSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

public class TagInfoDialogFragment extends DialogFragment implements
		OnClickListener {

	private EventTag tag;

	public void init(EventTag tag) {
		this.tag = tag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreateDialog(savedInstanceState);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("#" + tag.getTagText() + " Info");

		if (tag.getUsersFollowingIds() != null) {
			ContactListAdapter adapter = new ContactListAdapter(getActivity(),
					ZeppaUserSingleton.getInstance().getFriendsFrom(
							tag.getUsersFollowingIds()));
			builder.setAdapter(adapter, adapter);
		}

		builder.setNegativeButton(R.string.cancel, this);
		builder.setPositiveButton(R.string.delete, this);

		return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

		if (which == DialogInterface.BUTTON_NEGATIVE) {
			dialog.dismiss();
		} else if (which == DialogInterface.BUTTON_POSITIVE) {

			EventTag[] params = { tag };
			new AsyncTask<EventTag, Void, Boolean>() {

				@Override
				protected Boolean doInBackground(EventTag... params) {
					return EventTagSingleton.getInstance().deleteEventTag(tag,
							getCredential());
				}

				@Override
				protected void onPostExecute(Boolean result) {
					super.onPostExecute(result);
					if (result) {
						// Yay!
					} else {
						Toast.makeText(getActivity(),
								"Error Deleting #" + tag.getTagText(),
								Toast.LENGTH_SHORT).show();
					}
				}

			}.execute(params);

			dialog.dismiss();
		}

	}

	private GoogleAccountCredential getCredential() {
		return ((ZeppaApplication) getActivity().getApplication())
				.getGoogleAccountCredential();
	}

}
