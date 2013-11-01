package fragments;


import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.minook.zeppa.R;


public class HomeFragment extends Fragment{

	
	// ----------- Global Variables Bank ------------- \\
	// Private
	private ViewPager mainPager;
	private CalendarFragment calendarFragment;
	private FeedFragment feedFragment;
	private WatchingFragment watchingFragment;
	private ZeppaViewPagerAdapter zeppaPagerAdapter;
	
	// Constant
	final int NUM_PAGES = 3;
	
	
	/*
	 *  --------------  OVERRIDE METHODS -------------------
	 * NOTES:
	 * 
	 * */
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);
	
		// initialize dynamic variables:
		final ActionBar actionBar = getActivity().getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		mainPager = new ViewPager(getActivity());
		mainPager.setId(1234567890);
		
		calendarFragment = new CalendarFragment();
		feedFragment = new FeedFragment();
		watchingFragment = new WatchingFragment();
		
		FragmentManager manager = getActivity().getSupportFragmentManager();
		zeppaPagerAdapter = new ZeppaViewPagerAdapter(manager);
		mainPager.setAdapter(zeppaPagerAdapter);
	
		// Listeners
		
		mainPager.setOnPageChangeListener(new OnPageChangeListener(){

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				
			}

			@Override
			public void onPageSelected(int pos) {
				actionBar.selectTab(actionBar.getTabAt(pos));
			}
			
		});
		
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
				refreshView(actionBar);
				
			}

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				mainPager.setCurrentItem(tab.getPosition(), true);
				
			}

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
				
			}
		};

		// Set up the action bar:
		if(actionBar.getTabCount() == 0){
			actionBar.addTab(actionBar.newTab().setText(R.string.calendar_title).setTabListener(tabListener));
			actionBar.addTab(actionBar.newTab().setText(R.string.feed_title).setTabListener(tabListener), true);
			actionBar.addTab(actionBar.newTab().setText(R.string.watching_title).setTabListener(tabListener));
		}
		
		// Final formatting before showing user
		actionBar.setSelectedNavigationItem(1);
		mainPager.setCurrentItem(1);
		setHasOptionsMenu(true);

		return mainPager;
	
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_home, menu);
	}

	
	/*
	 *  -----------------  MY METHODS -----------------------
	 * NOTES:
	 * 
	 * */


	private void refreshView(ActionBar actionBar){ 
		// Makes calls to the appropriate 
		switch(actionBar.getSelectedTab().getPosition())
		{
		case 0:
			// calendar tab
			
		case 1:
			// feed tab
			
		case 2:
			// watching tab
			
		}
	}
	
	
	/*
	 *  ---------------  PRIVATE CLASSES ---------------------
	 * NOTES:
	 * 
	 * */

	private class ZeppaViewPagerAdapter extends FragmentPagerAdapter{

		public ZeppaViewPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
			
		}

		@Override
		public Fragment getItem(int pos) {
			switch(pos){
			case 0:
				return calendarFragment;
			case 1:
				return feedFragment;
			case 2:
				return watchingFragment;
			}
			return null;
		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}
		
	}
}
