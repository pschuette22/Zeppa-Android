package com.minook.zeppa.fragmentdialog;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.adapter.InviteListAdapter;
import com.minook.zeppa.adapter.tagadapter.CreateEventTagAdapter;
import com.minook.zeppa.mediator.DefaultZeppaEventMediator;
import com.minook.zeppa.singleton.EventTagSingleton;
import com.minook.zeppa.singleton.ZeppaEventSingleton;

public class RepostDialogFragment extends DialogFragment implements
		OnClickListener {

	private AuthenticatedFragmentActivity activity;
	private DefaultZeppaEventMediator eventManager;
	private RepostDialogAdapter adapter;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreateDialog(savedInstanceState);
		Long eventId = getArguments().getLong(Constants.INTENT_ZEPPA_EVENT_ID);

		eventManager = (DefaultZeppaEventMediator) ZeppaEventSingleton
				.getInstance().getEventById(eventId.longValue());

		activity = (AuthenticatedFragmentActivity) getActivity();
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);

		builder.setTitle("Repost " + eventManager.getTitle());
		final ListView list = new ListView(activity);
		adapter = new RepostDialogAdapter();
		list.setAdapter(adapter);

		builder.setView(list);
		builder.setPositiveButton(R.string.done, this);
		builder.setNegativeButton(R.string.cancel, this);

		return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

		if (which == DialogInterface.BUTTON_POSITIVE) {
			repostEvent();
		}
		dialog.dismiss();
	}

	private void repostEvent() {

		// new AsyncTask<Void, Void, Boolean>() {
		//
		// @Override
		// protected Boolean doInBackground(Void... params) {
		// return ZeppaEventSingleton.getInstance().repostedEvent(
		// getActivity().getApplicationContext(), getCredential(),
		// originalEvent, adapter.getTagIds(),
		// adapter.getSelectedUsers());
		// }
		//
		// @Override
		// protected void onPostExecute(Boolean result) {
		// super.onPostExecute(result);
		// if (result) {
		// Toast.makeText(activity, "Posted!", Toast.LENGTH_SHORT)
		// .show();
		// // TODO: notify data set changed
		// } else {
		// Toast.makeText(activity, "Error Reposting Event",
		// Toast.LENGTH_SHORT).show();
		// }
		// }
		//
		// }.execute();

	}

	private class RepostDialogAdapter extends BaseAdapter {

		private CreateEventTagAdapter tagAdapter;
		private InviteListAdapter inviteAdapter;
		private LinearLayout tagHolder;

		RepostDialogAdapter() {
			tagAdapter = new CreateEventTagAdapter(activity, tagHolder,
					EventTagSingleton.getInstance().getTags());
			inviteAdapter = new InviteListAdapter(activity);

		}

		public List<Long> getSelectedUsers() {
			return inviteAdapter.getInvitedUserIds();
		}

		public List<Long> getTagIds() {
			return tagAdapter.getSelectedTagIds();
		}

		@Override
		public int getCount() {
			return (2 + inviteAdapter.getCount());
		}

		@Override
		public Object getItem(int position) {
			if (position == 0) {
				return tagHolder;
			}
			return inviteAdapter.getItem(position - 2);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (position == 0) {
				tagHolder = new LinearLayout(activity);
				tagHolder.setOrientation(LinearLayout.VERTICAL);
				showUserTags(parent.getWidth());

				return tagHolder;
			} else if (position == 1) {
				View sectionHeader = activity.getLayoutInflater().inflate(
						R.layout.view_sectionheader, null, false);
				((TextView) sectionHeader.findViewById(R.id.sectionheader_text))
						.setText("Invites");
				return sectionHeader;
			} else {
				convertView = activity.getLayoutInflater().inflate(
						R.layout.view_invitelist_item, null, false);

				inviteAdapter.getView(position - 2, convertView, parent);

				return convertView;
			}

		}

		private void showUserTags(int width) {

			tagAdapter.drawTags();

		}

	}

}
