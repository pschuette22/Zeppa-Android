package com.minook.zeppa.adapter.eventlistadapter;

import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.minook.zeppa.R;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.mediator.AbstractZeppaEventMediator;
import com.minook.zeppa.singleton.ZeppaEventSingleton;

public class FeedListAdapter extends AbstractEventListAdapter {

	protected ListView listView;

	public FeedListAdapter(AuthenticatedFragmentActivity activity, ListView list) {
		super(activity);
		this.listView = list;

		if (ZeppaEventSingleton.getInstance().hasLoadedInitial()) {
			setEventMediators();
		} else {
			loaderView = getLoaderView();
			listView.addHeaderView(loaderView);
		}

	}

	@Override
	protected List<AbstractZeppaEventMediator> getCurrentEventMediators() {
		// TODO Auto-generated method stub
		return ZeppaEventSingleton.getInstance().getEventMediators();
	}

	@Override
	protected void setEventMediators() {
		eventMediators = ZeppaEventSingleton.getInstance().getEventMediators();
	}

	@Override
	protected void removeLoaderViewIfVisible() {
		if (loaderView != null && loaderView.getVisibility() == View.VISIBLE) {
			listView.removeHeaderView(loaderView);
			loaderView = null;
		}

	}

	public void fetchNewEventsInAsync(PullToRefreshLayout refreshLayout) {
		Object[] params = { refreshLayout };

		new AsyncTask<Object, Void, Boolean>() {

			private PullToRefreshLayout refreshLayout;

			@Override
			protected Boolean doInBackground(Object... params) {
				// try {
				refreshLayout = (PullToRefreshLayout) params[0];
				// TODO: add method to load in newly created events
				return Boolean.TRUE;
				// } catch (IOException e) {
				// e.printStackTrace();
				// }
				//
				// return Boolean.FALSE;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				refreshLayout.setRefreshComplete();

				if (result) {
					// Notify Dataset Changed
				} else {
					// Notify user error occured
				}

			}

		}.execute(params);

	}

	public void fetchMoreEventsInAsync(ListView list) {

		View loaderView = activity.getLayoutInflater().inflate(
				R.layout.view_loaderview, listView, false);
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

				// list = (ListView) params[0];
				// loaderView = (View) params[1];
				//
				// try {
				// return ZeppaEventSingleton.getInstance().loadNewEvents(
				// getCredential());
				// } catch (IOException e) {
				// return Boolean.FALSE;
				// }

				return Boolean.FALSE;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				list.removeHeaderView(loaderView);
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

}