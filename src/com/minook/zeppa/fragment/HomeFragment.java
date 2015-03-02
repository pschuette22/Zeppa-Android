package com.minook.zeppa.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;
import com.astuetz.viewpager.extensions.PagerSlidingTabStrip.IconTabProvider;
import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.activity.MainActivity;
import com.minook.zeppa.activity.NewEventActivity;

public class HomeFragment extends Fragment implements OnClickListener,
		OnPageChangeListener {

	private ViewPager mainPager;
	private PagerSlidingTabStrip tabStrip;
	private ZeppaViewPagerAdapter zeppaPagerAdapter;
	private ImageButton addEvent;

	private View layout;
	// private int currentPage;

	// Constant
	private final int NUM_PAGES = 4;

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
		tabStrip = (PagerSlidingTabStrip) layout
				.findViewById(R.id.home_pager_tabs);

		tabStrip.setBackgroundColor(getResources().getColor(R.color.white));
		tabStrip.setIndicatorColor(getResources().getColor(R.color.teal));
		tabStrip.setIndicatorHeight(5);
		tabStrip.setViewPager(mainPager);
		tabStrip.setShouldExpand(true);
		tabStrip.setOnPageChangeListener(this);
		mainPager.setCurrentItem(1);
		addEvent = (ImageButton) layout.findViewById(R.id.home_add);
		addEvent.setOnClickListener(this);

		return layout;

	}

	@Override
	public void onResume() {
		super.onResume();
		MainActivity activity = (MainActivity) getActivity();
		activity.setNavigationItem(Constants.NAVIGATION_HOME_INDEX);
		activity.toolbar.getMenu().clear();

		CharSequence titleSequence = zeppaPagerAdapter.getPageTitle(mainPager
				.getCurrentItem());
		activity.toolbar.setTitle(titleSequence);

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.home_add) {
			Intent toNewEvent = new Intent(getActivity(),
					NewEventActivity.class);

			toNewEvent.putExtra(Constants.INTENT_EVENT_STARTTIME,
					Long.valueOf(-1));
			toNewEvent.putExtra(Constants.INTENT_EVENT_ENDTIME,
					Long.valueOf(-1));
			startActivity(toNewEvent);

			getActivity().overridePendingTransition(R.anim.slide_up_in,
					R.anim.hold);
		}

	}

	public void setCurrentPageToNotifications() {
		onPageSelected(3);
	}

	@Override
	public void onPageScrollStateChanged(int position) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int position) {
		MainActivity activity = (MainActivity) getActivity();
		CharSequence titleSequence = zeppaPagerAdapter.getPageTitle(position);
		activity.toolbar.setTitle(titleSequence);
	}

	/*
	 * ----------------- MY METHODS ----------------------- NOTES:
	 * 
	 * stop the trend of using a computer as a substitute for being active and
	 * social
	 */

	/*
	 * --------------- PRIVATE CLASSES --------------------- NOTES:
	 */

	private class ZeppaViewPagerAdapter extends FragmentPagerAdapter implements
			IconTabProvider {

		private String[] tabOptions;
		private int[] iconOptions = { R.drawable.ic_tab_calendar,
				R.drawable.ic_tab_feed, R.drawable.ic_tab_agenda,
				R.drawable.ic_tab_activity };

		private CalendarFragment calendarFragment;
		private FeedFragment feedFragment;
		private AgendaFragment watchingFragment;
		private ActivityFragment activityFragment;

		public ZeppaViewPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
			tabOptions = getResources()
					.getStringArray(R.array.home_tab_options);

		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
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
					watchingFragment = new AgendaFragment();
				return watchingFragment;

			case 3:
				if (activityFragment == null)
					activityFragment = new ActivityFragment();

				return activityFragment;
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

		@Override
		public int getPageIconResId(int position) {
			return iconOptions[position];
		}

	}

}
