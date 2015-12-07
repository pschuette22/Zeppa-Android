package com.minook.zeppa.runnable;

import android.widget.CheckedTextView;

import com.appspot.zeppa_cloud_1821.zeppaclientapi.Zeppaclientapi;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaUserToUserRelationship;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ApiClientHelper;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.mediator.DefaultUserInfoMediator;
import com.minook.zeppa.mediator.MyZeppaUserMediator;

import java.io.IOException;

public class RequestMingleRunnable extends BaseRunnable {

	private MyZeppaUserMediator mMediator;
	private DefaultUserInfoMediator dMediator;
	private CheckedTextView view;

	public RequestMingleRunnable(ZeppaApplication application,
			GoogleAccountCredential credential, MyZeppaUserMediator mMediator,
			DefaultUserInfoMediator dMediator, CheckedTextView view) {
		super(application, credential);
		this.mMediator = mMediator;
		this.dMediator = dMediator;
		this.view = view;
	}

	@Override
	public void run() {

		ApiClientHelper helper = new ApiClientHelper();
		Zeppaclientapi api = helper.buildClientEndpoint();

		ZeppaUserToUserRelationship relationship = createRelationship();

		try {

			relationship = api.insertZeppaUserToUserRelationship(credential.getToken(),
					relationship).execute();
			dMediator.setUserRelationship(relationship);

			try {

				application.getCurrentActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						view.setClickable(true);

					}
				});

			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (IOException | GoogleAuthException ie) {
			ie.printStackTrace();

			try {

				application.getCurrentActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						view.setChecked(false);
						view.setText("Request");
					}
				});

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private ZeppaUserToUserRelationship createRelationship() {
		ZeppaUserToUserRelationship relationship = new ZeppaUserToUserRelationship();

		relationship.setCreatorId(mMediator.getUserId());
		relationship.setSubjectId(dMediator.getUserId());
		relationship.setRelationshipType("PENDING_REQUEST");

		return relationship;
	}

}
