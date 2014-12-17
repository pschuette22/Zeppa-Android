package com.minook.zeppa.fragment;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
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

public class HomeFragment extends Fragment implements OnPageChangeListener{

	private ViewPager mainPager;
	private PagerSlidingTabStrip tabStrip;
	private ZeppaViewPagerAdapter zeppaPagerAdapter;

	private View layout;
	private int currentPage;

	// Constant
	private final int NUM_PAGES = 3;

	/*
	 * -------------- OVERRIDE METHODS ------------------- NOTES:
	 */

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		
		// initialize dynamic variables:
		layout = inflater.inflate(R.layout.fragment_home, container, false);
		mainPager = (ViewPager) layout.findViewById(R.id.main_pager);

		FragmentManager manager = getChildFragmentManager();
		zeppaPagerAdapter = new ZeppaViewPagerAdapter(manager);
		mainPager.setAdapter(zeppaPagerAdapter);
		mainPager.setOnPageChangeListener(this);
		tabStrip = (PagerSlidingTabStrip) layout
				.findViewById(R.id.home_pager_tabs);

		tabStrip.setBackgroundColor(getResources().getColor(R.color.white));
		tabStrip.setIndicatorColor(getResources().getColor(R.color.teal));
		tabStrip.setViewPager(mainPager);
		tabStrip.setShouldExpand(true);
		mainPager.setCurrentItem(1);

			
		

		Log.d("TAG", "onCreateView, HomeFragment");
		return layout;

	}

	@Override
	public void onResume() {
		super.onResume();
		((MainActivity) getActivity())
				.setNavigationItem(Constants.NAVIGATION_HOME_INDEX);

		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setTitle(R.string.home);

		setHasOptionsMenu(true);
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

			toNewEvent.putExtra(Constants.INTENT_EVENT_STARTTIME,
					Long.valueOf(-1));
			toNewEvent.putExtra(Constants.INTENT_EVENT_ENDTIME,
					Long.valueOf(-1));
			startActivity(toNewEvent);

			getActivity().overridePendingTransition(R.anim.slide_up_in,
					R.anim.hold);
			break;

		}

		return true;
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int position) {
		currentPage = position;
		
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

	private class ZeppaViewPagerAdapter extends FragmentPagerAdapter /* implements IconTabProvider */{

		private String[] tabOptions;
		// private int[] iconOptions = { R.drawable.ic_tab_calendar,
		// R.drawable.ic_tab_feed, R.drawable.ic_tab_agenda };

		private CalendarFragment calendarFragment;
		private FeedFragment feedFragment;
		private WatchingFragment watchingFragment;

		public ZeppaViewPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
			tabOptions = getResources()
					.getStringArray(R.array.home_tab_options);

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

		

		// @Override
		// public int getPageIconResId(int position) {
		// return iconOptions[position];
		// }

	}
}
