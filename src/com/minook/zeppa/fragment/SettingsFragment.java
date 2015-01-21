package com.minook.zeppa.fragment;

import java.io.IOException;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.minook.zeppa.CloudEndpointUtils;
import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.activity.MainActivity;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppauserendpoint.Zeppauserendpoint;

public class SettingsFragment extends Fragment implements OnClickListener {

	View layout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		layout = inflater.inflate(R.layout.fragment_settings, container, false);

		Button deleteButton = (Button) layout
				.findViewById(R.id.settingsfragment_delete);
		deleteButton.setOnClickListener(this);

		return layout;
	}

	@Override
	public void onResume() {
		super.onResume();
		((MainActivity) getActivity())
				.setNavigationItem(Constants.NAVIGATION_SETTINGS_INDEX);

		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setTitle(R.string.settings);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.settingsfragment_delete:
			raiseDeleteAccountDialog();
			break;
		}

	}

	private DialogInterface.OnClickListener deleteDialogListener = new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();

			if(which == DialogInterface.BUTTON_POSITIVE){
				ProgressDialog progress = new ProgressDialog(getActivity());
				progress.setCancelable(false);
				progress.setTitle("Deleting Account...");
				progress.setMessage("Please do not close Zeppa until deletion is complete");
				progress.show();
				new DeleteAccountTask(((AuthenticatedFragmentActivity) getActivity()).getGoogleAccountCredential(), progress).execute();
				
			}			
			
		}
	};
	
	private void raiseDeleteAccountDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Delete Account?");
		builder.setMessage("You can't undo this!!! Be really really sure you want to delete your account on Zeppa and all information associated with it. Operation may take several moments, please do not close application mid deletion");
		builder.setPositiveButton("DeleteAccount", deleteDialogListener);
		builder.setNegativeButton("Dismiss", deleteDialogListener);
		// Create the click listener for the dialog buttons
		builder.show();
		
	}

	private class DeleteAccountTask extends AsyncTask<Void, Void, Boolean> {

		private GoogleAccountCredential credential;
		private ProgressDialog progress;

		public DeleteAccountTask(GoogleAccountCredential credential,
				ProgressDialog progress) {
			this.credential = credential;
			this.progress = progress;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			Boolean success = Boolean.FALSE;

			Zeppauserendpoint.Builder builder = new Zeppauserendpoint.Builder(
					AndroidHttp.newCompatibleTransport(),
					GsonFactory.getDefaultInstance(), credential);
			builder = CloudEndpointUtils.updateBuilder(builder);
			Zeppauserendpoint endpoint = builder.build();

			try {
				endpoint.removeZeppaUser(ZeppaUserSingleton.getInstance()
						.getUserId()).execute();

				// TODO: clear literally everything out of memory and
				// preferences

				success = Boolean.TRUE;
			} catch (IOException e) {
				e.printStackTrace();
			}

			return success;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			progress.dismiss();
			((AuthenticatedFragmentActivity) getActivity()).logout();
		}

	}

}
