package com.minook.zeppa.adapters.eventadapter;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.Constants;
import com.minook.zeppa.Constants.ConflictionStatus;
import com.minook.zeppa.R;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.activities.AuthenticatedFragmentActivity;
import com.minook.zeppa.activities.EventViewActivity;
import com.minook.zeppa.activities.MyEventViewActivity;
import com.minook.zeppa.activities.UserActivity;
import com.minook.zeppa.adapters.ContactListAdapter;
import com.minook.zeppa.adapters.RepostsListAdapter;
import com.minook.zeppa.fragmentdialogs.RepostDialogFragment;
import com.minook.zeppa.singleton.ZeppaEventSingleton;
import com.minook.zeppa.singleton.ZeppaUserSingleton;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;
import com.minook.zeppa.zeppauserendpoint.model.ZeppaUser;

public class EventListAdapter extends BaseAdapter {

	protected AuthenticatedFragmentActivity activity;
	protected List<ZeppaEvent> events;
	protected boolean initialLoaded;

	public EventListAdapter(AuthenticatedFragmentActivity activity) {
		this.activity = activity;
		events = new ArrayList<ZeppaEvent>();
		initialLoaded = ZeppaEventSingleton.getInstance().hasLoadedInitial();

	}

	@Override
	public int getCount() {
		if (!initialLoaded) {
			return 1; // Loader view
		} else {
			return events.size();
		}
	}

	@Override
	public ZeppaEvent getItem(int position) {
		if (!initialLoaded || events.isEmpty())
			return null;
		else
			return events.get(position);
	}

	@Override
	public long getItemId(int position) {
		if (!initialLoaded || events.isEmpty()) {
			return -1;
		} else {
			ZeppaEvent event = getItem(position);
			return event.getKey().getId().longValue();
		}
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (!initialLoaded) {
			View loaderView = activity.getLayoutInflater().inflate(
					R.layout.view_loaderview, null, false);
			TextView loadText = (TextView) loaderView
					.findViewById(R.id.loaderview_text);
			loadText.setText(R.string.loading);
			return loaderView;
		} else if(!events.isEmpty()) {

			final ZeppaEvent event = getItem(position);
			final ZeppaUser host = getHost(event);

			if (convertView == null) {
				convertView = activity.getLayoutInflater().inflate(
						R.layout.view_eventlist_item, parent, false);

			}

			ImageView hostImage = (ImageView) convertView
					.findViewById(R.id.eventview_hostimage);
			TextView hostName = (TextView) convertView
					.findViewById(R.id.eventview_hostname);
			TextView viaName = (TextView) convertView
					.findViewById(R.id.eventview_via);
			TextView title = (TextView) convertView
					.findViewById(R.id.eventview_eventtitle);
			TextView description = (TextView) convertView
					.findViewById(R.id.eventview_description);
			TextView time = (TextView) convertView
					.findViewById(R.id.eventview_eventtime);
			String location = (!event.getShortLocation().isEmpty() ? event
					.getShortLocation() : event.getExactLocation());

			hostName.setText(host.getDisplayName());
			getNSetViaInAsync(viaName, event.getOriginalEventId());
			title.setText(event.getTitle());
			time.setText(Constants.getDisplayDatesString(event.getStart()
					.longValue(), event.getEnd().longValue())
					+ ", " + location);
			description.setText(event.getDescription());
			getNSetImageInAsync(hostImage, host);

			if (!activity.getClass().equals(UserActivity.class)) {

				OnClickListener toHostListener = new OnClickListener() {

					@Override
					public void onClick(View v) {
						ZeppaEvent event = getItem(position);
						ZeppaUser host = getHost(event);
						boolean isUserEvent = isUserEvent(host);

						if (!isUserEvent) {
							viewUserProfile(host);
						} else {
							// Go to account fragment
						}

					}
				};
				hostImage.setOnClickListener(toHostListener);
				hostName.setOnClickListener(toHostListener);

			}

			/*
			 * Handle the Action Items
			 */
			handleWatchButton(host, event, convertView);
			handleRepostButton(host, event, convertView);
			handleJoinButton(host, event, convertView);
			handleConflictIndicator(host, event, convertView);

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent launchEvent;

					if (ZeppaUserSingleton.getInstance().isUser(host)) {
						launchEvent = new Intent(activity,
								MyEventViewActivity.class);
					} else {
						launchEvent = new Intent(activity,
								EventViewActivity.class);
					}

					launchEvent.putExtra(Constants.INTENT_ZEPPA_EVENT_ID, event
							.getKey().getId());
					activity.startActivity(launchEvent);
					activity.overridePendingTransition(R.anim.slide_left_in,
							R.anim.slide_left_out);
				}
			});

		}
		return convertView;
	}

	@Override
	public void notifyDataSetChanged() {
		initialLoaded = ZeppaEventSingleton.getInstance().hasLoadedInitial();
		super.notifyDataSetChanged();

	}

	/*
	 * Private Methods
	 */

	protected ZeppaUser getHost(ZeppaEvent event) {
		return ZeppaUserSingleton.getInstance().getUserById(event.getHostId().longValue());
	}

	protected boolean isUserEvent(ZeppaUser host) {
		return ZeppaUserSingleton.getInstance().getUser().equals(host);
	}

	protected GoogleAccountCredential getCredential() {
		return ((ZeppaApplication) activity.getApplication())
				.getGoogleAccountCredential();
	}

	protected String getAccountName() {
		return getCredential().getSelectedAccountName();
	}

	protected Long getUserId() {
		return ZeppaUserSingleton.getInstance().getUserId();
	}

	/*
	 * Action Button Handlers
	 */

	private void handleWatchButton(final ZeppaUser host,
			final ZeppaEvent event, View convertView) {
		final Button watchButton = (Button) convertView
				.findViewById(R.id.eventview_watchbutton);

		if (isUserEvent(host)) {

			watchButton.setText("Cancel");
			watchButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							activity);
					builder.setTitle("Cancel " + event.getTitle() + "?");
					StringBuilder messageBuilder = new StringBuilder();

					messageBuilder.append("Are you sure you want to delete ");
					messageBuilder.append(event.getTitle());
					messageBuilder.append("? This cannot be undone..");

					if (event.getUsersGoingIds() != null) {
						int joinCount = event.getUsersGoingIds().size();
						if (joinCount > 1) {
							messageBuilder.append(" " + joinCount
									+ " friends will be notified");
						} else if (joinCount == 1) {
							ZeppaUser loneAttender = ZeppaUserSingleton
									.getInstance().getUserById(
											event.getUsersGoingIds().get(0));
							messageBuilder.append(" "
									+ loneAttender.getDisplayName()
									+ " will be notified");
						}

					}
					builder.setMessage(messageBuilder.toString());

					DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which == DialogInterface.BUTTON_POSITIVE) {
								new AsyncTask<Void, Void, Boolean>() {

									@Override
									protected Boolean doInBackground(
											Void... params) {
										return ZeppaEventSingleton
												.getInstance()
												.deleteZeppaEvent(
														activity,
														getCredential(),
														event,
														activity.getContentResolver());

									}

									@Override
									protected void onPostExecute(Boolean result) {
										super.onPostExecute(result);
										if (result) {
											Toast.makeText(
													((ZeppaApplication) activity
															.getApplication()),
													"Deleted "
															+ event.getTitle(),
													Toast.LENGTH_SHORT).show();
											notifyDataSetChanged();
										} else {
											Toast.makeText(
													((ZeppaApplication) activity
															.getApplication()),
													"Error Deleting "
															+ event.getTitle(),
													Toast.LENGTH_SHORT).show();
										}
									}

								}.execute();
							}
							dialog.dismiss();

						}
					};

					builder.setPositiveButton("Cancel Event", listener);
					builder.setNegativeButton("Nevermind", listener);

					builder.show();
				}

			});

		} else {

			final Long userId = ZeppaUserSingleton.getInstance().getUserId();
			if (event.getUsersGoingIds() != null
					&& event.getUsersGoingIds().contains(userId)) {
				watchButton.setEnabled(false);
			} else {
				watchButton.setEnabled(true);
				if (event.getUsersWatchingIds() != null
						&& event.getUsersWatchingIds().contains(userId)) {
					watchButton.setText("Stop Watching");
				} else {
					watchButton.setText("Watch");

					watchButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {

							if (event.getUsersWatchingIds() != null
									&& event.getUsersWatchingIds().contains(
											userId)) {
								stopWatchingEventInAsyc(event);
							} else {
								watchEventInAsync(event);
							}
						}

					});
				}
			}

		}
	}

	private void handleRepostButton(final ZeppaUser host,
			final ZeppaEvent event, View convertView) {
		final Button repostButton = (Button) convertView
				.findViewById(R.id.eventview_joinrepostbutton);

		if (isUserEvent(host)) {
			repostButton.setVisibility(View.VISIBLE);

			if (event.getEventScope() == (0)) {
				repostButton.setText("See Reposts");
				repostButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (event.getReposts() == null
								|| event.getReposts().isEmpty()) {
							Toast.makeText(activity, "Nobody has reposted",
									Toast.LENGTH_SHORT).show();
						} else {
							AlertDialog.Builder builder = new AlertDialog.Builder(
									activity);
							builder.setTitle(event.getTitle() + " reposts");

							RepostsListAdapter adapter = new RepostsListAdapter(
									activity, event.getKey().getId());

							builder.setAdapter(adapter, adapter);
							builder.setNeutralButton(R.string.dismiss, adapter);

							builder.show();
						}

					}
				});
			} else {
				repostButton.setVisibility(View.GONE);
			}
		} else {
			final Long repostId = ZeppaEventSingleton.getInstance()
					.getMyRepostId(event.getKey().getId());
			if (repostId > 0) { // Did Repost
				repostButton.setVisibility(View.VISIBLE);

				repostButton.setText("My Repost");
				repostButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent toRepost = new Intent();
						toRepost.putExtra(Constants.INTENT_ZEPPA_EVENT_ID,
								repostId);
						activity.startActivity(toRepost);
						activity.overridePendingTransition(
								R.anim.slide_left_in, R.anim.slide_left_out);
					}
				});
			} else {
				if (event.getEventScope() == (0)) {
					repostButton.setVisibility(View.VISIBLE);

					repostButton.setText("Repost");
					repostButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							RepostDialogFragment repostDialog = new RepostDialogFragment();
							Bundle args = new Bundle();
							args.putLong(Constants.INTENT_ZEPPA_EVENT_ID, event
									.getKey().getId());
							repostDialog.show(
									activity.getSupportFragmentManager(),
									"RepostDialog");
						}

					});
				} else {
					repostButton.setVisibility(View.GONE);
				}

			}

		}

	}

	private void handleJoinButton(final ZeppaUser host, final ZeppaEvent event,
			View convertView) {
		final Button joinButton = (Button) convertView
				.findViewById(R.id.eventview_joinbutton);

		if (isUserEvent(host)) {
			joinButton.setText("Who's Going");
			joinButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showGuestList(event);
				}

			});

		} else {
			final Long userId = ZeppaUserSingleton.getInstance().getUserId();

			if (event.getUsersGoingIds() != null
					&& event.getUsersGoingIds().contains(userId)) {
				joinButton.setText("Leave");
			} else {
				joinButton.setText("Join");
			}

			joinButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (event.getUsersGoingIds() != null
							&& event.getUsersGoingIds().contains(userId)) {
						leaveEventInAsync(event);
					} else {
						joinEventInAsync(event);
					}
				}

			});
		}
	}

	private void handleConflictIndicator(final ZeppaUser host,
			final ZeppaEvent event, View convertView) {
		ImageView conflictIndicator = (ImageView) convertView
				.findViewById(R.id.eventview_conflictionindicator);

		Resources res = activity.getResources();
		if (isUserEvent(host)
				|| (event.getUsersGoingIds() != null && event
						.getUsersGoingIds().contains(
								ZeppaUserSingleton.getInstance().getUserId()))) {
			conflictIndicator.setImageDrawable(res
					.getDrawable(R.drawable.small_circle_blue));
		} else {
			ConflictionStatus status = ZeppaEventSingleton.getInstance()
					.getConflictionStatus(activity, event, getAccountName(),
							activity.getContentResolver());

			switch (status.ordinal()) {
			case 0: // No conflict
				conflictIndicator.setImageDrawable(res
						.getDrawable(R.drawable.small_circle_green));
				break;
			case 1: // Partial Conflict
				conflictIndicator.setImageDrawable(res
						.getDrawable(R.drawable.small_circle_yellow));
				break;
			case 2: // Complete conflict
				conflictIndicator.setImageDrawable(res
						.getDrawable(R.drawable.small_circle_red));
				break;

			case 3: // Going
				conflictIndicator.setImageDrawable(res
						.getDrawable(R.drawable.small_circle_blue));
				break;

			}
		}

		conflictIndicator.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showConfliction(event);
			}
		});
	}

	/*
	 * Get-n-set Async Tasks
	 */

	private void getNSetImageInAsync(final ImageView hostImageView,
			final ZeppaUser host) {
		new AsyncTask<Void, Void, Drawable>() {

			@Override
			protected Drawable doInBackground(Void... params) {
				return ZeppaUserSingleton.getInstance().getUserImage(host);
			}

			@Override
			protected void onPostExecute(Drawable result) {
				super.onPostExecute(result);
				if (result != null)
					hostImageView.setImageDrawable(result);
			}

		}.execute();

	}

	private void getNSetViaInAsync(final TextView text, final Long id) {
		if (id == null || id < 1) {
			text.setVisibility(View.GONE);

		} else {
			text.setVisibility(View.VISIBLE);
			text.setText("Loading...");
			new AsyncTask<Void, Void, ZeppaUser>() {

				@Override
				protected ZeppaUser doInBackground(Void... params) {
					return ZeppaUserSingleton.getInstance().getUserById(id);
				}

				@Override
				protected void onPostExecute(final ZeppaUser result) {
					super.onPostExecute(result);
					if (result != null) {
						text.setText("via " + result.getDisplayName());
						text.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								viewUserProfile(result);
							}
						});

					}
				}

			}.execute();
		}

	}

	/*
	 * Join Handler Async Tasks
	 */

	private void joinEventInAsync(final ZeppaEvent event) {

		if (event.getUsersGoingIds() == null) {
			event.setUsersGoingIds(new ArrayList<Long>());
		}

		event.getUsersGoingIds().add(
				ZeppaUserSingleton.getInstance().getUserId());

		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				return ZeppaEventSingleton.getInstance().joinZeppaEvent(
						activity, getCredential(), event);
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if (result) {
					// Yay
				} else {
					Toast.makeText(
							((ZeppaApplication) activity.getApplication()),
							"Error Joining " + event.getTitle(),
							Toast.LENGTH_SHORT).show();
				}
			}

		}.execute();

		notifyDataSetChanged();
	}

	private void leaveEventInAsync(final ZeppaEvent event) {

		event.getUsersGoingIds().remove(getUserId());

		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				return ZeppaEventSingleton.getInstance().leaveZeppaEvent(
						activity, getCredential(), event);
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if (result) {
					// Yay
				} else {
					Toast.makeText(
							((ZeppaApplication) activity.getApplication()),
							"Error Joining " + event.getTitle(),
							Toast.LENGTH_SHORT).show();
					event.getUsersGoingIds().add(getUserId());
					notifyDataSetChanged();
				}
			}

		}.execute();

		notifyDataSetChanged();
	}

	/*
	 * Watching Handler Async Tasks
	 */
	private void watchEventInAsync(final ZeppaEvent event) {

		if (event.getUsersWatchingIds() == null) {
			event.setUsersWatchingIds(new ArrayList<Long>());
		}

		event.getUsersWatchingIds().add(getUserId());

		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {

				return ZeppaEventSingleton.getInstance().watchZeppaEvent(
						getCredential(), event);
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if (result) {
					// Yay
				} else {
					Toast.makeText(
							((ZeppaApplication) activity.getApplication()),
							"Error Watching " + event.getTitle(),
							Toast.LENGTH_SHORT).show();
					event.getUsersWatchingIds().remove(getUserId());
				}
			}

		}.execute();
		notifyDataSetChanged();
	}

	private void stopWatchingEventInAsyc(final ZeppaEvent event) {

		event.getUsersWatchingIds().remove(getUserId());

		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				return ZeppaEventSingleton.getInstance()
						.stopWatchingZeppaEvent(getCredential(), event);
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if (result) {
					// Yay
				} else {
					Toast.makeText(
							((ZeppaApplication) activity.getApplication()),
							"Error Joining " + event.getTitle(),
							Toast.LENGTH_SHORT).show();
					event.getUsersWatchingIds().add(getUserId());
				}
			}

		}.execute();
		notifyDataSetChanged();
	}

	private void viewUserProfile(ZeppaUser user) {

		Intent viewUser = new Intent(activity, UserActivity.class);
		viewUser.putExtra(Constants.INTENT_ZEPPA_USER_ID, user.getKey().getId());
		activity.startActivity(viewUser);
		activity.overridePendingTransition(R.anim.slide_left_in,
				R.anim.slide_left_out);

	}

	private void showConfliction(ZeppaEvent event) {
		ConflictionStatus status = ConflictionStatus.NO_CONFLICTION;

		ZeppaEventSingleton eventSingleton = ZeppaEventSingleton.getInstance();

		if (status.equals(ConflictionStatus.NO_CONFLICTION)) {
			Toast noConflict = Toast.makeText(activity, "You're available!",
					Toast.LENGTH_SHORT);
			noConflict.show();

		} else if (status.equals(ConflictionStatus.PARTIAL_CONFLICTION)) {
			Dialog conflictionDialog = eventSingleton.getConflictionDialog(
					event, activity);
			conflictionDialog.show();

		} else if (status.equals(ConflictionStatus.COMPLETE_CONFLICTION)) {
			Dialog conflictionDialog = eventSingleton.getConflictionDialog(
					event, activity);
			conflictionDialog.show();

		} else { // status.equals(ConflictStatus.ALREADY_GOING)
			Toast.makeText(activity, "You're already going!",
					Toast.LENGTH_SHORT).show();

		}

	}

	private void showGuestList(ZeppaEvent event) {
		if (event.getUsersGoingIds() == null) {
			Toast.makeText(activity, "Nobody else is going yet",
					Toast.LENGTH_SHORT).show();
		} else {
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
					activity);
			dialogBuilder.setTitle("Guest List");
			ListView content = new ListView(activity);
			ContactListAdapter adapter = new ContactListAdapter(activity,
					ZeppaUserSingleton.getInstance().getFriendsFrom(
							event.getUsersGoingIds()));
			content.setAdapter(adapter);
			content.setOnItemClickListener(adapter);
			dialogBuilder.setView(content);
			dialogBuilder.setNeutralButton(R.string.dismiss,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

						}
					});

			dialogBuilder.show();
		}
	}

	protected GoogleAccountCredential getGoogleAccountCredential() {
		return ((ZeppaApplication) activity.getApplication())
				.getGoogleAccountCredential();
	}

	public void fetchNewEventsInAsync(PullToRefreshLayout refreshLayout) {
		Object[] params = { refreshLayout };

		new AsyncTask<Object, Void, Boolean>() {

			private PullToRefreshLayout refreshLayout;

			@Override
			protected Boolean doInBackground(Object... params) {
				refreshLayout = (PullToRefreshLayout) params[0];
				return ZeppaEventSingleton.getInstance().loadNewEvents(
						getCredential());
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				refreshLayout.setRefreshComplete();
				if (result) {
					// Whoo Hoo
					notifyDataSetChanged();
				} else {
					Toast.makeText(activity, "Error Refreshing",
							Toast.LENGTH_SHORT).show();
				}
			}

		}.execute(params);

	}

	public void fetchMoreEventsInAsync(ListView list) {

		View loaderView = activity.getLayoutInflater().inflate(
				R.layout.view_loaderview, null, false);
		TextView loaderText = (TextView) loaderView
				.findViewById(R.id.loaderview_text);
		loaderText.setText("Finding things to do...");
		list.addFooterView(loaderView);

		Object[] params = { list, loaderView };

		new AsyncTask<Object, Void, Boolean>() {

			private ListView list;
			private View loaderView;

			@Override
			protected Boolean doInBackground(Object... params) {

				list = (ListView) params[0];
				loaderView = (View) params[1];

				return ZeppaEventSingleton.getInstance().loadNewEvents(
						getCredential());
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				list.removeFooterView(loaderView);
				if (result) {
					// Yay
					notifyDataSetChanged();
				} else {
					Toast.makeText(activity, "Error Loading Events",
							Toast.LENGTH_SHORT).show();
				}
			}

		}.execute(params);

	}
	
	public void notifyIfDataChanged(){
		// This will be called within each of the subclasses
	}

}
