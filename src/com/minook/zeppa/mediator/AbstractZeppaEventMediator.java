package com.minook.zeppa.mediator;

import java.util.List;

import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;
import com.minook.zeppa.zeppaeventtouserrelationshipendpoint.model.ZeppaEventToUserRelationship;

public abstract class AbstractZeppaEventMediator implements OnClickListener{
	
	public enum ConflictStatus{
		ATTENDING,
		NONE,
		PARTIAL,
		COMPLETE,
		UNKNOWN
	}
	
	
	protected ZeppaEvent event;
	protected AbstractZeppaUserMediator hostManager;
	protected AbstractZeppaUserMediator viaManager;
	protected AbstractZeppaUserMediator originalHostManager;
	protected AbstractZeppaEventMediator repostManager;
	protected List<ZeppaEventToUserRelationship> relationships;
	protected ConflictStatus conflictStatus;
		
	public AbstractZeppaEventMediator(ZeppaEvent event){
		this.event = event;	
		this.conflictStatus = ConflictStatus.UNKNOWN;
		
	}
	
	/**
	 * Pass in a ConvertView for a ZeppaEvent and set elements of this view 
	 */
	public void convertView(View convertView){
		TextView viaName = (TextView) convertView.findViewById(R.id.eventview_via);
		TextView title = (TextView) convertView
				.findViewById(R.id.eventview_eventtitle);
		TextView description = (TextView) convertView
				.findViewById(R.id.eventview_description);
		TextView placeAndTime = (TextView) convertView.findViewById(R.id.eventview_eventtime);

		title.setText(event.getTitle());
		placeAndTime.setText(Constants.getDisplayDatesString(event.getStart().longValue(), event.getEnd().longValue())
				+ ", " + getDisplayLocation());
		description.setText(event.getDescription());

		((Button) convertView.findViewById(R.id.eventview_buttonleft)).setOnClickListener(this);
		((Button) convertView.findViewById(R.id.eventview_buttonright)).setOnClickListener(this);

		if (isPublicEvent()) {
			((Button) convertView.findViewById(R.id.eventview_buttonmiddle)).setOnClickListener(this);
		} else {
			((Button) convertView.findViewById(R.id.eventview_buttonmiddle)).setVisibility(View.GONE);
		}

		convertView.setOnClickListener(this);

	}
		
	
	public Long getEventId(){
		return event.getKey().getId();
	}
	
	public abstract boolean isInterestingEvent();
	
	
	public String getTitle(){
		return event.getTitle();
	}
	
	public String getDescription(){
		return event.getDescription();
	}
	
	public String getDisplayLocation(){
		
		if(event.getDisplayLocation() != null){
			return event.getDisplayLocation();
		} else {
			return event.getMapsLocation();
		}
		
	}
	
	public Long getOriginalEventId(){
		return event.getOriginalEventId();
	}
	
	public Long getRepostedFromEventId(){
		return event.getRepostedFromEventId();
	}
	
	public String getTimeString(){
		return Constants.getDisplayDatesString(event.getStart().longValue(), event.getEnd().longValue());
	}
	
	public boolean doesMatchEventId(long eventId){
		return (eventId == event.getKey().getId().longValue());
	}
	
	public boolean hostIdDoesMatch(long hostId){
		return(event.getHostId().longValue() == hostId);
	}
	
	public void update(){
		new UpdateEventTask().execute();
	}
	
	public boolean isPublicEvent(){
		return event.getPrivacy().intValue() == 1;
	}
	
	public boolean eventIsOld(){
		long currentTime = System.currentTimeMillis();
		return (event.getEnd().longValue() <= currentTime);
	}
	
	
	private void getNSetViaInAsync(TextView viaText, Long viaUserId) {
//		if (viaUserId == null || viaUserId < 1) {
//			viaText.setVisibility(View.GONE);
//
//		} else if (viaUserId.longValue() == ZeppaUserSingleton.getInstance()
//				.getUserId().longValue()) {
//
//		} else {
//			viaText.setVisibility(View.VISIBLE);
//			viaText.setText("Loading...");
//			Object params = new Object[] { viaText, viaUserId };
//			new AsyncTask<Object, Void, ZeppaUser>() {
//				private TextView viaText;
//
//				@Override
//				protected ZeppaUser doInBackground(Object... params) {
//					viaText = (TextView) params[0];
//					Long userId = (Long) params[1];
//					return ZeppaUserSingleton.getInstance().getUserById(userId);
//				}
//
//				@Override
//				protected void onPostExecute(ZeppaUser result) {
//					super.onPostExecute(result);
////					viaUser = result;
////					if (result != null) {
////						viaText.setText("via " + result.getGivenName() + " "
////								+ result.getFamilyName());
////
////						viaText.setOnClickListener(new OnClickListener() {
////
////							@Override
////							public void onClick(View v) {
////								Intent toViaIntent = new Intent(activity,
////										UserActivity.class);
////								toViaIntent.putExtra(
////										Constants.INTENT_ZEPPA_USER_ID, viaUser
////												.getKey().getId());
////							}
////						});
////
////					}
//				}
//
//			}.execute(params);
//		}

	}

	
	protected void getNSetImageInAsync(ImageView hostImageView) {
//		Object[] params = { hostImageView };
//
//		new AsyncTask<Object, Void, Drawable>() {
//			private ImageView hostImageView;
//
//			@Override
//			protected Drawable doInBackground(Object... params) {
//				hostImageView = (ImageView) params[0];
//				return ZeppaUserSingleton.getInstance().getUserImage(host);
//			}
//
//			@Override
//			protected void onPostExecute(Drawable result) {
//				super.onPostExecute(result);
//				if (result != null)
//					hostImageView.setImageDrawable(result);
//			}
//
//		}.execute(params);

	}

	private void getNSetHostInAsync(Long hostId) {
//		host = ZeppaUserSingleton.getInstance().getUserById(hostId.longValue());
//
//		if (host != null) {
//			setHostInfo();
//		} else {
//			TextView hostName = (TextView) view
//					.findViewById(R.id.eventview_hostname);
//			hostName.setText("Loading...");
//			Object[] params = { hostId, getCredential() };
//
//			new AsyncTask<Object, Void, ZeppaUser>() {
//
//				@Override
//				protected ZeppaUser doInBackground(Object... params) {
//					Long userId = (Long) params[0];
//					GoogleAccountCredential credential = (GoogleAccountCredential) params[1];
//					return ZeppaUserSingleton.getInstance()
//							.getOrFetchZeppaUser(userId, credential);
//				}
//
//				@Override
//				protected void onPostExecute(ZeppaUser result) {
//					super.onPostExecute(result);
//					if (result != null) {
//						setHostInfo();
//					} else {
//						TextView hostName = (TextView) view
//								.findViewById(R.id.eventview_hostname);
//						hostName.setText("Error occured when loading");
//					}
//				}
//
//			}.execute(params);
//
//		}

	}

	private void setHostInfo() {
//		TextView hostName = (TextView) view
//				.findViewById(R.id.eventview_hostname);
//		hostName.setText(host.getGivenName() + " " + host.getFamilyName());
//		ImageView hostImage = (ImageView) view
//				.findViewById(R.id.eventview_hostimage);
//		getNSetImageInAsync(hostImage);
	}
	
	protected class UpdateEventTask extends AsyncTask<Void, Void, Boolean>{

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return null;
		}
		
		
	}
	
	
	
}
