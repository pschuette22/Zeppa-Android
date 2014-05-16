package com.minook.zeppa.fragmentdialogs;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.activities.AuthenticatedFragmentActivity;
import com.minook.zeppa.eventtagendpoint.model.EventTag;
import com.minook.zeppa.singleton.EventTagSingleton;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUser;

public class RepostDialogFragment extends DialogFragment implements
		OnClickListener {

	private AuthenticatedFragmentActivity activity;
	private ZeppaEvent originalEvent;
	private RepostDialogAdapter adapter;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreateDialog(savedInstanceState);
		Long eventId = getArguments().getLong(Constants.INTENT_ZEPPA_EVENT_ID);
		originalEvent = ZeppaEventSingleton.getInstance().getEventById(eventId.longValue());

		activity = (AuthenticatedFragmentActivity) getActivity();
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);

		builder.setTitle("Repost " + originalEvent.getTitle());
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

	private GoogleAccountCredential getCredential() {
		return ((ZeppaApplication) activity.getApplication())
				.getGoogleAccountCredential();
	}

	private void repostEvent() {

		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				return ZeppaEventSingleton.getInstance().repostedEvent(
						getActivity().getApplicationContext(), getCredential(),
						originalEvent, adapter.getTagIds(),
						adapter.getSelectedUsers());
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if (result) {
					Toast.makeText(activity, "Posted!", Toast.LENGTH_SHORT)
							.show();
					// TODO: notify data set changed
				} else {
					Toast.makeText(activity, "Error Reposting Event",
							Toast.LENGTH_SHORT).show();
				}
			}

		}.execute();

	}

	private class RepostDialogAdapter extends BaseAdapter implements
			android.view.View.OnClickListener {

		private ArrayList<ZeppaUser> friends;
		private ArrayList<Long> inviteIds;
		private ArrayList<Long> tagIds;
		private LinearLayout tagHolder;

		RepostDialogAdapter() {
			friends = new ArrayList<ZeppaUser>();
			friends.addAll(ZeppaUserSingleton.getInstance().getFriends());
			friends.remove(originalEvent.getHostId());
			inviteIds = new ArrayList<Long>();
		}

		public List<Long> getSelectedUsers() {
			return inviteIds;
		}

		public List<Long> getTagIds() {
			return tagIds;
		}

		@Override
		public int getCount() {
			return (friends.size() + 2);
		}

		@Override
		public Object getItem(int position) {
			if (position == 0) {
				return tagHolder;
			}
			return friends.get(position - 2);
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

				ZeppaUser user = (ZeppaUser) getItem(position);

				CheckBox inviteState = (CheckBox) convertView
						.findViewById(R.id.inviteitem_checkbox);

				inviteState.setClickable(false);

				TextView userName = (TextView) convertView
						.findViewById(R.id.inviteitem_contactname);
				ImageView userImage = (ImageView) convertView
						.findViewById(R.id.inviteitem_contactimage);

				userName.setText(user.getDisplayName());

				setImageInAsync(userImage, user);
				convertView.setOnClickListener(this);
				convertView.setTag(user);

				return convertView;
			}

		}

		@Override
		public void onClick(View v) {
			CheckBox inviteState = (CheckBox) v
					.findViewById(R.id.inviteitem_checkbox);

			ZeppaUser user = (ZeppaUser) v.getTag();
			if (inviteState.isChecked()) {
				inviteIds.remove(user.getKey().getId());
				inviteState.setChecked(false);
			} else {
				inviteIds.add(user.getKey().getId());
				inviteState.setChecked(true);
			}

		}

		private void setImageInAsync(final ImageView imageView,
				final ZeppaUser user) {

			new AsyncTask<Void, Void, Drawable>() {

				@Override
				protected Drawable doInBackground(Void... params) {
					return ZeppaUserSingleton.getInstance().getUserImage(user);
				}

				@Override
				protected void onPostExecute(Drawable result) {
					super.onPostExecute(result);
					if (result != null) {
						imageView.setImageDrawable(result);
					}
				}

			}.execute();

		}

		private void showUserTags(int width) {
			List<EventTag> userTags = EventTagSingleton.getInstance().getTags();

			LayoutInflater inflater = activity.getLayoutInflater();
			LinearLayout currentLine = (LinearLayout) inflater.inflate(
					R.layout.view_tag_line, null, false);

			int lineWidth = width;
			tagHolder.addView(currentLine);
			currentLine.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.EXACTLY);
			int tagsWidth = 0;

			for (int i = 0; i < userTags.size(); i++) {
				final EventTag tag = userTags.get(i);
				View tagView = (View) inflater.inflate(
						R.layout.view_tag_others, null, false);

				final CheckedTextView tagViewText = (CheckedTextView) tagView
						.findViewById(R.id.tagview_tagtext);
				tagViewText.setText("#" + tag.getTagText());
				tagViewText.setTag(tag);

				tagView.measure(MeasureSpec.UNSPECIFIED,
						MeasureSpec.UNSPECIFIED);
				int tagWidth = tagView.getMeasuredWidth();

				if ((lineWidth - tagsWidth) < tagWidth) {
					currentLine = (LinearLayout) inflater.inflate(
							R.layout.view_tag_line, null, false);
					tagHolder.addView(currentLine);
					tagsWidth = 0;
				}

				currentLine.addView(tagView);

				tagsWidth += tagWidth;

				tagViewText
						.setOnClickListener(new android.view.View.OnClickListener() {

							@Override
							public void onClick(View v) {
								// raise a dialog for this
								if (tagViewText.isChecked()) {
									tagIds.remove(tag.getKey().getId());
									tagViewText.setChecked(false);
								} else {
									tagIds.add(tag.getKey().getId());
									tagViewText.setChecked(true);
								}

							}

						});

			}

		}

	}

}
