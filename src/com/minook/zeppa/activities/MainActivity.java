package com.minook.zeppa.activities;

/**********************************************
 * @author Peter W. Schuette
 * Date: October 14, 2013
 * Location: Auckland New Zealand
 * Third time writing this from beginning
 * Zeppa, so the world will get out and do shit again...
 .....................DZ....................                                                
 ......ODDD.........DD:DDD.........ND8......                                                
 .......DDDDZ.....DDDD.:8DDD.....8DDD8......                                                
 .......+DDDD8.IDDDDD8..DDDDDD.DDDDDD.......                                                
 .........8DDDDDDDDDD...IDDDDDDDDDDN........                                                
 ..........8DDDDDDDD8....DDDDDDDDD..........                                                
 ........DDDDDDDDDDD.....ODDDDDDDDDD........                                                
 ......ODDDDDDDDDDDO......DDDDDDDDDDDD7.....                                                
 ....8NNNNNNNNNNNNN.......NNNNNNNNNNNNND$...                                                
 .....DD.............................8DD....                                                
 .....DDDD:........................DDDDZ....                                                
 .....=DDDD8D....................8DDDDD.....                                                
 ......DDDDDDD8.............. ZDDDDDDDI.....                                                
 ......:DDDDDDDDD............DDDDDDDDD......                                                
 .......DDDDDDDD.............DDDDDDDD?......                                                
 .......DDDDDDDO..............DDDDDDD.......                                                
 ........DDDDDD.......D+..... DDDDDD:.......                                                
 ........ZDDDD7.....DDDDN?.....DDDDD........                                                
 ....D,.DDDDDD....DDDDDDDDD+...$DDDDD...=...                                                
 ..+DDDDDDDDD,..DDDDDDDDDDDDD,. DDDDDDODDD..                                                
 ..:DDDDDDDDD.ODDDDDDDDDDDDDDD8.:DDDDDDDDD=.                                                
 .. D8DDDM.D8DDDDDDDDDDDDDDDDDDDDN.IDDDD8...                                                
 ..DDDN.DDD.......................NDD88DDD$.                                                
 .DDDD...D...........................D+.=DDDD                                                
 8DN.....................................,8D..                                              
 ..                                       ..                                                

 .NNNNNDDDDDDDNDD.DDNNDD  DDNN8D    .NNNNDD.                                                
 ...DD.     .DD.   .DD.   .D8.     ....D8....                                                
 ...DD.     .DD.   .DD.  .8...   .=DI .DD..DD .                                              
 ...DD.     .DD.   .DD. DO..    .D8.  .DN. .DD.                                              
 ...DD.     .DD.   .DD~D..     .7DD.. .DN. .+DD                                              
 ...DD.     .DD.   .DD.DDD      $DD   .DN. .+DD                                              
 ...DD.     .DD.   .DD..DDD..  ..DD.. .DN.  DD.                                              
 ...DD.     .DD.   .DD. ..DDD.   .8D...DN..D8 .                                              
 ...DD.     .DD.   .DD.   .8DD:.     ..DD...                                                
 .DDDDDN. .D8DDDD.D8DDDD  .8DDDDD~. .DDDDDD.

 Anyone can change the world, it's just a matter of doing it. 
 **/

import android.app.ActionBar;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.fragments.AccountFragment;
import com.minook.zeppa.fragments.ContactsFragment;
import com.minook.zeppa.fragments.FeedbackFragment;
import com.minook.zeppa.fragments.HomeFragment;
import com.minook.zeppa.fragments.InviteFragment;
import com.minook.zeppa.fragments.NotificationsFragment;

public class MainActivity extends AuthenticatedFragmentActivity implements
		OnGlobalLayoutListener {

	// ----------- Global Variables Bank ------------- \\
	// Debug
	private boolean isConnected;

	// Private
	private DrawerLayout navigationLayout;
	private ListView navigationList;
	private String[] navigationOptions;
	private ActionBarDrawerToggle drawerToggle;
	private int currentPage;

	private HomeFragment homeFragment;
	private ContactsFragment contactsFragment;
	private NotificationsFragment notificationsFragment;
	private AccountFragment accountFragment;
	private FeedbackFragment feedbackFragment;
	private InviteFragment inviteFragment;

	private Fragment currentFragment;

	/*
	 * -------------- OVERRIDE EVENTS ---------------------- NOTES:
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		isConnected = Constants.IS_CONNECTED;

		navigationLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		navigationList = (ListView) findViewById(R.id.drawer_list);
		navigationOptions = getResources().getStringArray(
				R.array.navigation_options);

		FragmentManager fm = getSupportFragmentManager();
		homeFragment = new HomeFragment();

		fm.beginTransaction().add(R.id.content_frame, homeFragment).commit();

		// Options Menu setup
		drawerToggle = new ActionBarDrawerToggle(this, navigationLayout,
				R.drawable.ic_menubars, R.string.navigation_open,
				R.string.navigation_closed) {

			public void onDrawerClosed(View view) {
				invalidateOptionsMenu();

			}

			public void onDrawerOpened(View view) {

				if (navigationList.getCheckedItemCount() == 0) {
					navigationList.setItemChecked(currentPage, true);
				}
				invalidateOptionsMenu();
			}

		};

		final ActionBar actionBar = getActionBar();

		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);

		navigationList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		DrawerItemAdapter navListAdapter = new DrawerItemAdapter(this,
				R.layout.view_navlist_item, navigationOptions);
		navigationList.setAdapter(navListAdapter);
		navigationList.setOnItemClickListener(navListAdapter);
		navigationLayout.setDrawerListener(drawerToggle);

		navigationList.getViewTreeObserver().addOnGlobalLayoutListener(this);

		try {
			boolean launchNotifications = getIntent().getExtras().getBoolean(
					Constants.INTENT_NOTIFICATIONS);
			if (launchNotifications)
				selectItem(2, true);
		} catch (NullPointerException ex) {
			ex.printStackTrace();
			selectItem(0, true);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (drawerToggle.onOptionsItemSelected(item)) {
			// Selected navigation item
			return true;
		}

		if (navigationLayout.isDrawerVisible(navigationList)) {
			navigationLayout.closeDrawers();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	@Override
	public void onBackPressed() {
		// Close Drawer if Open
		if (navigationLayout.isDrawerVisible(navigationList)) {
			navigationLayout.closeDrawers();
			return;
		}

		// if it is in the calendar view and a day is open
		else if (currentPage == 0 && homeFragment.didHandleDayView())
			return;

		super.onBackPressed();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onGlobalLayout() {
		navigationList.getViewTreeObserver().removeGlobalOnLayoutListener(this);
		navigationList.setItemChecked(currentPage, true);
	}

	/*
	 * ----------------- MY METHODS ----------------------- NOTES:
	 */

	public void selectItem(int position, boolean addToBackStack) {

		if (position != currentPage) {
			invalidateOptionsMenu();
			Fragment fragment = new Fragment();
			String transactionTitle = null;
			currentPage = position;

			switch (position) {
			case 0:
				if (homeFragment == null)
					homeFragment = new HomeFragment();
				fragment = homeFragment;
				transactionTitle = Constants.NAVIGATION_HOME;
				break;

			case 1:
				if (contactsFragment == null)
					contactsFragment = new ContactsFragment();
				fragment = contactsFragment;
				transactionTitle = Constants.NAVIGATION_CONTACTS;
				break;

			case 2:
				if (notificationsFragment == null)
					notificationsFragment = new NotificationsFragment();
				fragment = notificationsFragment;
				transactionTitle = Constants.NAVIGATION_ACTIVITY;
				break;

			case 3:
				if (accountFragment == null)
					accountFragment = new AccountFragment();
				fragment = accountFragment;
				transactionTitle = Constants.NAVIGATION_ACCOUNT;
				break;

			case 4:
				if (feedbackFragment == null)
					feedbackFragment = new FeedbackFragment();
				fragment = feedbackFragment;
				transactionTitle = Constants.NAVIGATION_FEEDBACK;
				break;

			case 5:
				if (inviteFragment == null)
					inviteFragment = new InviteFragment();
				fragment = inviteFragment;
				transactionTitle = Constants.NAVIGATION_INVITE;
				break;

			default:
				if (homeFragment == null)
					homeFragment = new HomeFragment();
				fragment = homeFragment;
				transactionTitle = Constants.NAVIGATION_HOME;
				break;

			}

			// Insert the fragment by replacing any existing fragment
			// Add to backstack with transaction title

			if (addToBackStack) {
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.content_frame, fragment)
						.addToBackStack(transactionTitle).commit();
			} else {
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.content_frame, fragment).commit();
			}
			navigationList.setItemChecked(position, true);

		}

		navigationLayout.closeDrawer(navigationList);

	}

	public boolean isConnected() {
		return isConnected;
	}

	public void setCurrentFragment(Fragment fragment) {
		this.currentFragment = fragment;
	}

	/*
	 * --------------- PRIVATE CLASSES ---------------------
	 */

	private class DrawerItemAdapter extends ArrayAdapter<String> implements
			ListView.OnItemClickListener {

		private int layoutRes;
		String[] navOptions;

		public DrawerItemAdapter(Context context_, int resource_,
				String[] navOptions_) {
			super(context_, resource_, navOptions_);
			layoutRes = resource_;
			navOptions = navOptions_;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View row = convertView;
			if (row == null) {
				row = getLayoutInflater().inflate(layoutRes, parent, false);
			}

			int iconResource = 0;
			switch (position) {
			case 0:
				iconResource = R.drawable.ic_menu_home;
				break;
			case 1:
				iconResource = R.drawable.ic_menu_allfriends;
				break;
			case 2:
				iconResource = R.drawable.ic_popup_reminder;
				break;

			case 3:
				iconResource = R.drawable.ic_menu_account_list;
				break;

			case 4:
				iconResource = R.drawable.ic_menu_send;
				break;
			case 5:
				iconResource = R.drawable.ic_menu_invite;
				break;

			default:
				iconResource = R.drawable.ic_launcher;
			}

			ImageView iconImage = (ImageView) row
					.findViewById(R.id.navlistItemIcon);
			iconImage
					.setImageDrawable(getResources().getDrawable(iconResource));

			TextView optionText = (TextView) row
					.findViewById(R.id.navlistItemText);
			optionText.setText(navOptions[position]);

			return row;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position, true);
		}

	}

}
