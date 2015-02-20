package com.minook.zeppa.fragment;

import java.io.IOException;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.activity.MainActivity;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppafeedbackendpoint.Zeppafeedbackendpoint;
import com.minook.zeppa.zeppafeedbackendpoint.model.ZeppaFeedback;

public class FeedbackFragment extends Fragment implements OnClickListener {

	// private variables:
	private View layout;
	private EditText feedbackSubjectField;
	private EditText feedbackTextField;
	private Button sendFeedback;
	private RatingBar ratingBar;

	private String feedbackSubjectString;
	private String feedbackTextString;

	// Debug variables
	private final String TAG = "FeedBack Fragment Tag";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		layout = inflater.inflate(R.layout.fragment_feedback, container, false);

		ratingBar = (RatingBar) layout.findViewById(R.id.feedback_ratingbar);
		ratingBar.setRating(5);

		feedbackSubjectField = (EditText) layout
				.findViewById(R.id.feedback_edittext_subject);
		feedbackTextField = (EditText) layout
				.findViewById(R.id.feedback_edittext_main);
		sendFeedback = (Button) layout.findViewById(R.id.feedback_button_send);
		sendFeedback.setOnClickListener(this);

		return layout;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.feedback_button_send:

			if (fieldsAreOk()) {
				new PostFeedbackInAsync().execute();

			}

			break;
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		MainActivity activity = (MainActivity) getActivity();
		activity.toolbar.getMenu().clear();
		activity.setNavigationItem(Constants.NAVIGATION_FEEDBACK_INDEX);
		activity.toolbar.setTitle(R.string.feedback);
	}

	private boolean fieldsAreOk() {

		feedbackSubjectString = feedbackSubjectField.getText().toString()
				.trim();
		feedbackTextString = feedbackTextField.getText().toString().trim();

		if (!feedbackSubjectString.isEmpty() && !feedbackTextString.isEmpty()) {
			feedbackSubjectField.setText("");
			feedbackTextField.setText("");
			return true;
		} else {

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.insufficientfields_title);
			builder.setMessage(R.string.insufficientfields_message);
			builder.setPositiveButton(R.string.got_it,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

						}

					});

			builder.create().show();

			return false;
		}
	}

	private class PostFeedbackInAsync extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			GoogleAccountCredential credential = ((AuthenticatedFragmentActivity) getActivity())
					.getGoogleAccountCredential();
			Zeppafeedbackendpoint.Builder endpointBuilder = new Zeppafeedbackendpoint.Builder(
					AndroidHttp.newCompatibleTransport(),
					GsonFactory.getDefaultInstance(), credential);
			endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);

			Zeppafeedbackendpoint feedbackEndpoint = endpointBuilder.build();
			boolean success = false;
			try {
				Log.d(TAG, "Executing insert Feedback Object operation");
				double rating = ratingBar.getRating();

				ZeppaFeedback feedback = new ZeppaFeedback();
				feedback.setUserId(ZeppaUserSingleton.getInstance().getUserId());
				feedback.setSubject(feedbackSubjectString);
				feedback.setFeedback(feedbackTextString);
				feedback.setRating(rating);
				feedback.setReleaseCode(Constants.APP_RELEASE_CODE);
				feedback.setDeviceType("ANDROID");

				feedbackEndpoint.insertZeppaFeedback(feedback).execute();

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
				Toast.makeText(getActivity(), "Thanks for the Feedback!",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getActivity(), "Error sending feeback",
						Toast.LENGTH_SHORT).show();

			}

		}

	}

}
