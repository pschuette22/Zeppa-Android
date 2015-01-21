package com.minook.zeppa.mediator;

import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.activity.MainActivity;
import com.minook.zeppa.activity.MyEventViewActivity;
import com.minook.zeppa.singleton.EventTagSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;

public class MyZeppaEventMediator extends AbstractZeppaEventMediator {

	protected boolean deletingEvent;
	protected boolean editingEvent;

	public MyZeppaEventMediator(ZeppaEvent event) {
		super(event);
		conflictStatus = ConflictStatus.ATTENDING;
		deletingEvent = false;
		editingEvent = false;
	}

	@Override
	public boolean isAgendaEvent() {
		return true;
	}

	@Override
	protected void setHostInfo(View view) {
		MyZeppaUserMediator myMediator = ZeppaUserSingleton.getInstance()
				.getUserMediator();

		TextView hostName = (TextView) view
				.findViewById(R.id.eventview_hostname);

		hostName.setText(myMediator.getDisplayName());
		ImageView hostImage = (ImageView) view
				.findViewById(R.id.eventview_hostimage);
		myMediator.setImageWhenReady(hostImage);

		((LinearLayout) view.findViewById(R.id.eventview_hostinfo))
				.setOnClickListener(this);

	}

	public List<MyEventTagMediator> getTagMediators() {
		return EventTagSingleton.getInstance().getMyTagsFrom(getTagIds());
	}

	public void deleteEvent() {
		deletingEvent = true;
		// TODO: raise deleting dialog
		// then change params to object and pass dialog
		// or set them as global params
		new DeleteEventTask(getContext()).execute();

	}

	private class DeleteEventTask extends
			AsyncTask<Void, Void, Boolean> {

		private AuthenticatedFragmentActivity context;

		public DeleteEventTask(AuthenticatedFragmentActivity context){
			this.context = context;
		}
		
		@Override
		protected Boolean doInBackground(
				Void... params) {
			// TODO Auto-generated method stub
			// set object as deleted so nothing can be added to it
			// delete comments
			// delete relationships

			return null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			deletingEvent = false;
			if (result) {
				context.onBackPressed();
			} else {

			}

		}

	}

	/**
	 * 
	 * 
	 */
	@Override
	public void convertEventListItemView(AuthenticatedFragmentActivity context,
			View convertView) {
		super.convertEventListItemView(context, convertView);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.eventview_hostinfo:
			if (getContext() instanceof MainActivity) {
				((MainActivity) getContext()).selectItem(0, true);
			}
			break;
			
		case R.id.eventview:
			Intent toEventDetails = new Intent(getContext(),
					MyEventViewActivity.class);
			toEventDetails.putExtra(Constants.INTENT_ZEPPA_EVENT_ID,
					getEventId());
			getContext().startActivity(toEventDetails);
			getContext().overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_left_out);
			
			break;
		}

	}

	@Override
	public void convertQuickActionBar(View barView) {
		barView.setVisibility(View.GONE);

	}
	
	@Override
	public boolean isHostedByCurrentUser() {
		return true;
	}

}
