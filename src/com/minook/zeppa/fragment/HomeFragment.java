package com.minook.zeppa.fragment;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;
import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.activity.MainActivity;
import com.minook.zeppa.activity.NewEventActivity;
import com.minook.zeppa.singleton.ZeppaEventSingleton;

public class HomeFragment extends Fragment {

	private ViewPager mainPager;
	private PagerSlidingTabStrip tabStrip;
	private ZeppaViewPagerAdapter zeppaPagerAdapter;

	private View layout;

	// Constant
	private final int NUM_PAGES = 3;

	/*
	 * -------------- OVERRIDE METHODS ------------------- NOTES:
	 */

	// Call to inflate the view:
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		// initialize dynamic variables:
		layout = inflater.inflate(R.layout.fragment_home, null, false);
		mainPager = (ViewPager) layout.findViewById(R.id.main_pager);

		FragmentManager manager = getChildFragmentManager();
		zeppaPagerAdapter = new ZeppaViewPagerAdapter(manager);
		mainPager.setAdapter(zeppaPagerAdapter);
		tabStrip = (PagerSlidingTabStrip) layout
				.findViewById(R.id.home_pager_tabs);

		tabStrip.setBackgroundColor(getResources().getColor(R.color.white));
		tabStrip.setIndicatorColor(getResources().getColor(R.color.teal));
		tabStrip.setViewPager(mainPager);
		tabStrip.setShouldExpand(true);

		if (savedInstanceState == null)
			setHasOptionsMenu(true);

		Log.d("TAG", "onCreateView, HomeFragment");
		return layout;

	}

	@Override
	public void onResume() {
		super.onResume();
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setTitle(R.string.app_name);
		((MainActivity) getActivity()).setCurrentFragment(this);
		ZeppaEventSingleton.getInstance().clearOldEvents();

	}

	// Created, now at show configuration changes:
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mainPager.setCurrentItem(1);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		// TODO: add logic in here handling whether or not there are
		// notifications
		inflater.inflate(R.menu.menu_home, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {

		case R.id.action_newevent:
			Intent toNewEvent = new Intent(getActivity(),
					NewEventActivity.class);

			long nullLong = -1;
			toNewEvent.putExtra(Constants.INTENT_EVENT_STARTTIME, nullLong);
			toNewEvent.putExtra(Constants.INTENT_EVENT_ENDTIME, nullLong);
			startActivity(toNewEvent);
			getActivity().overridePendingTransition(R.anim.slide_up_in,
					R.anim.hold);
			break;

		case R.id.action_refresh:
			zeppaPagerAdapter.startRefreshForCurrentPage();
			break;

		case R.id.action_settings:

			break;

		case R.id.action_signout:

			break;
		}

		return true;
	}

	/*
	 * ----------------- MY METHODS ----------------------- NOTES:
	 * 
	 * stop the trend of using a computer as a substitute for being active and
	 * social
	 */

	public boolean didHandleDayView() {

		if (mainPager.getCurrentItem() == 0) {
			// Currently on the calendar view
			return zeppaPagerAdapter.didHandleDayView();
		} else
			return false;
	}

	/*
	 * --------------- PRIVATE CLASSES --------------------- NOTES:
	 */

	private class ZeppaViewPagerAdapter extends FragmentPagerAdapter {

		private String[] tabOptions;

		private CalendarFragment calendarFragment;
		private FeedFragment feedFragment;
		private WatchingFragment watchingFragment;

		public ZeppaViewPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
			tabOptions = getResources()
					.getStringArray(R.array.home_tab_options);

		}

		public void startRefreshForCurrentPage() {
			switch (mainPager.getCurrentItem()) {
			case 0:

				break;
			case 1:

				break;
			case 2:

				break;

			}
		}

		public boolean didHandleDayView() {
			if (calendarFragment != null) {
				return calendarFragment.didHandleDayView();
			} else {
				return false;
			}
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			Log.d("TAG", "Destroying item at position: " + position);
			super.destroyItem(container, position, object);
		}

		@Override
		public Fragment getItem(int pos) {

			switch (pos) {
			case 0:
				if (calendarFragment == null)
					calendarFragment = new CalendarFragment();
				return calendarFragment;
			case 1:
				if (feedFragment == null)
					feedFragment = new FeedFragment();
				return feedFragment;
			case 2:
				if (watchingFragment == null)
					watchingFragment = new WatchingFragment();
				return watchingFragment;
			}

			return null;
		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return tabOptions[position];
		}

	}
}
