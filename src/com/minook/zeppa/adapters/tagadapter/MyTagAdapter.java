package com.minook.zeppa.adapters.tagadapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.R;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.eventtagendpoint.model.EventTag;
import com.minook.zeppa.singleton.EventTagSingleton;

public class MyTagAdapter extends TagAdapter {

	public MyTagAdapter(FragmentActivity activity, LinearLayout tagHolder) {
		super(activity, tagHolder);

		EventTagSingleton singleton = EventTagSingleton.getInstance();
		if(singleton.hasLoadedTags()){
			tags = EventTagSingleton.getInstance().getTags();
		} else {
			singleton.setWaitingAdapter(this);
		}
				
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		super.getView(position, convertView, parent);

			final EventTag eventTag = getItem(position);

			convertView = inflater.inflate(R.layout.view_tag_others, null,
					false);
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
							activity);
					dialogBuilder.setTitle(eventTag.getTagText());
					dialogBuilder.setItems(R.array.mytag_options,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									switch (which) {
									case 0: // followers

										break;
									case 1: // usage

										break;

									case 2: // delete

										deleteTagInAsync(eventTag);
										dialog.dismiss();
										break;
									}

								}
							});

					dialogBuilder.create().show();

				}

			});

			CheckedTextView tagText = (CheckedTextView) convertView
					.findViewById(R.id.tagview_mytagtext);
			tagText.setText(eventTag.getTagText());
			views.add(position, convertView); // Keep view array synced
		

		return convertView;

	}

	private void deleteTagInAsync(EventTag tag) {

		GoogleAccountCredential credential = ((ZeppaApplication) activity
				.getApplication()).getGoogleAccountCredential();
		Object[] params = { credential, tag };

		new AsyncTask<Object, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Object... params) {
				GoogleAccountCredential credential = (GoogleAccountCredential) params[0];
				EventTag tag = (EventTag) params[1];

				return EventTagSingleton.getInstance().deleteEventTag(tag,
						credential);

			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);

				if (result) {
					// deleted successfully
				} else {
					Toast.makeText(activity, R.string.error_deletetag,
							Toast.LENGTH_SHORT).show();
				}

			}

		}.execute(params);
	}

	public View addTagAtRuntime(EventTag tag, GoogleAccountCredential credential) {
		EventTagSingleton.getInstance().insertEventTag(tag, credential);

		return getView(tags.size(), null, null);
	}

}
