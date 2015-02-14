package com.minook.zeppa.activity;

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
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.minook.zeppa.Constants;
import com.minook.zeppa.R;
import com.minook.zeppa.fragment.AccountFragment;
import com.minook.zeppa.fragment.FeedbackFragment;
import com.minook.zeppa.fragment.HomeFragment;
import com.minook.zeppa.fragment.MinglersFragment;
import com.minook.zeppa.fragment.SettingsFragment;
import com.minook.zeppa.mediator.MyZeppaUserMediator;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

public class MainActivity extends AuthenticatedFragmentActivity {

	// ----------- Global Variables Bank ------------- \\
	private final String TAG = getClass().getName();
	// Debug

	private DrawerLayout drawerLayout;
	private LinearLayout navigationCabinet;
	private ListView navigationList;
	private String[] navigationOptions;
	private ActionBarDrawerToggle drawerToggle;
	private NavigationItemAdapter navListAdapter;

	// Navigation options:
	private AccountFragment accountFragment;
	private HomeFragment homeFragment;
	private MinglersFragment contactsFragment;
	private FeedbackFragment feedbackFragment;
	private SettingsFragment settingsFragment;

	/*
	 * -------------- OVERRIDE EVENTS ---------------------- NOTES:
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ActionBar actionBar = getActionBar();

		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		navigationList = (ListView) findViewById(R.id.navigation_drawer_list);
		navigationCabinet = (LinearLayout) findViewById(R.id.navigation_cabinet);

		// Options Menu setup
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.drawable.ic_menu_navigation, R.string.navigation_open,
				R.string.navigation_closed) {

			public void onDrawerClosed(View view) {
				invalidateOptionsMenu();

			}

			public void onDrawerOpened(View view) {

				invalidateOptionsMenu();
			}

		};

		navigationOptions = getResources().getStringArray(
				R.array.navigation_options);
		if (navListAdapter == null)
			navListAdapter = new NavigationItemAdapter(this,
					R.layout.view_navlist_item, navigationOptions);

		navigationList.setAdapter(navListAdapter);
		navigationList.setOnItemClickListener(navListAdapter);
		navigationList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		drawerLayout.setDrawerListener(drawerToggle);

		int currentPage = currentPage();
		if (currentPage < 0) {
			selectItem(1, false);
			if(getIntent().getExtras().getBoolean(Constants.INTENT_NOTIFICATIONS));

		} else {
			selectItem(currentPage, false);
		}

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

		if (drawerLayout.isDrawerVisible(navigationCabinet)) {

			drawerLayout.closeDrawers();
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
		if (drawerLayout.isDrawerVisible(navigationCabinet)) {
			drawerLayout.closeDrawers();
			return;
		}

		super.onBackPressed();

	}

	/*
	 * ----------------- MY METHODS ----------------------- NOTES:
	 */

	private int currentPage() {
		Fragment current = getSupportFragmentManager().findFragmentById(
				R.id.content_frame);

		if (current == null) {
			return -1;
		} else if (current instanceof AccountFragment) {
			return 0;
		} else if (current instanceof HomeFragment) {
			return 1;
		} else if (current instanceof MinglersFragment) {
			return 2;
		} else if (current instanceof FeedbackFragment) {
			return 3;
		} else if (current instanceof SettingsFragment) {
			return 4;
		} else {
			Log.wtf(TAG, "Unrecognized fragment");
			return -1;
		}

	}

	public void setNavigationItem(int navigationIndex) {
		navigationList.setItemChecked(navigationIndex, true);
	}

	public void selectItem(int position, boolean addToBackStack) {

		if (position != currentPage()) {
			invalidateOptionsMenu();
			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();

			String transactionTitle;

			switch (position) {

			case 0:
				if (accountFragment == null)
					accountFragment = new AccountFragment();
				transaction.replace(R.id.content_frame, accountFragment);
				transactionTitle = Constants.NAVIGATION_ACCOUNT;
				break;

			case 1:
				if (homeFragment == null)
					homeFragment = new HomeFragment();
				transaction.replace(R.id.content_frame, homeFragment);
				transactionTitle = Constants.NAVIGATION_HOME;
				break;

			case 2:
				if (contactsFragment == null)
					contactsFragment = new MinglersFragment();
				transaction.replace(R.id.content_frame, contactsFragment);
				transactionTitle = Constants.NAVIGATION_CONTACTS;
				break;

			case 3:
				if (feedbackFragment == null)
					feedbackFragment = new FeedbackFragment();
				transaction.replace(R.id.content_frame, feedbackFragment);
				transactionTitle = Constants.NAVIGATION_FEEDBACK;
				break;

			case 4:
				if (settingsFragment == null)
					settingsFragment = new SettingsFragment();
				transaction.replace(R.id.content_frame, settingsFragment);
				transactionTitle = Constants.NAVIGATION_SETTINGS;
				break;

			default:
				Log.wtf(TAG, "Invalid Position");
				return;
			}

			if (addToBackStack) { // if this change should be added to back
									// stack
				transaction.addToBackStack(transactionTitle); // add to
																// backstack
			}

			transaction.commit();

			navigationList.setItemChecked(position, true);

		}

		drawerLayout.closeDrawer(navigationCabinet);

	}

	/*
	 * --------------- PRIVATE CLASSES ---------------------
	 */

	private class NavigationItemAdapter extends ArrayAdapter<String> implements
			ListView.OnItemClickListener {

		private int layoutRes;
		private String[] navOptions;

		public NavigationItemAdapter(Context context, int resource,
				String[] navOptions) {
			super(context, resource, navOptions);

			this.layoutRes = resource;
			this.navOptions = navOptions;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.d("TAG", "Making Naviagtion View " + position);
			if (position == 0) {

				convertView = getLayoutInflater().inflate(
						R.layout.view_navlist_profile, parent, false);
				ImageView profileImage = (ImageView) convertView
						.findViewById(R.id.navigation_profile_image);
				TextView profileName = (TextView) convertView
						.findViewById(R.id.navigation_profile_name);

				MyZeppaUserMediator mediator = ZeppaUserSingleton.getInstance()
						.getUserMediator();

				if (mediator != null) {
					mediator.setImageWhenReady(profileImage);
					profileName.setText(mediator.getDisplayName());
				} else {
					profileName.setText("Error Occured!");
				}

			} else {

				convertView = getLayoutInflater().inflate(layoutRes, parent,
						false);

				int iconResource = 0;
				switch (position) {

				case 1: // Home
					iconResource = R.drawable.ic_home;

					break;

				case 2: // Minglers
					iconResource = R.drawable.ic_minglers;

					break;

				case 3: // Feedback
					iconResource = R.drawable.ic_feedback;

					break;
				case 4: // Settings
					iconResource = R.drawable.ic_settings;
					break;

				default:
					iconResource = R.drawable.zeppa_logo;
				}

				ImageView iconImage = (ImageView) convertView
						.findViewById(R.id.navlistItemIcon);
				iconImage.setImageDrawable(getResources().getDrawable(
						iconResource));

				TextView optionText = (TextView) convertView
						.findViewById(R.id.navlistItemText);
				optionText.setText(navOptions[position]);

			}

			if (navigationList.getCheckedItemCount() > 0) {
				convertView
						.setSelected(navigationList.getCheckedItemPosition() == position);
			}

			return convertView;

		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			selectItem(position, true);
		}

	}
	
}
