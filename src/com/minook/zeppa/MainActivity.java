package com.minook.zeppa;

/**********************************************
 * @author Peter W. Schuette
 * Date: October 14, 2013
 * Location: Auckland New Zealand
 * Third time restarting writing this
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
 .DD.     .DD.   .DD.   .D8.     ....D8....                                                
 .DD.     .DD.   .DD.  .8...   .=DI .DD..DD .                                              
 .DD.     .DD.   .DD. DO..    .D8.  .DN. .DD.                                              
 .DD.     .DD.   .DD~D..     .7DD.. .DN. .+DD                                              
 .DD.     .DD.   .DD.DDD      $DD   .DN. .+DD                                              
 .DD.     .DD.   .DD..DDD..  ..DD.. .DN.  DD.                                              
 .DD.     .DD.   .DD. ..DDD.   .8D...DN..D8 .                                              
 .DD.     .DD.   .DD.   .8DD:.     ..DD...                                                
 .DDDDDN. .D8DDDD.D8DDDD  .8DDDDD~. .DDDDDD.

 Anyone can change the world, it's just a matter of doing it. 
 **/

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import fragments.ContactsFragment;
import fragments.HomeFragment;
import fragments.LoginFragment;

public class MainActivity extends FragmentActivity {

	// ----------- Global Variables Bank ------------- \\
	// Debug
	// private String TAG = "TagMainActivity";

	// Private
	private DrawerLayout navigationLayout;
	private ListView navigationList;
	private String[] navigationOptions;

	// Constant
	private HomeFragment homeFragment;
	private ContactsFragment contactsFragment;
	private LoginFragment loginFragment;


	/*
	 * -------------- OVERRIDE EVENTS ---------------------- NOTES:
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if (savedInstanceState != null) {
			launchIntoApp();
		} else {

			launchLogin();
		}

	}


	@Override
	public void onBackPressed() {

		super.onBackPressed();
	}

	/*
	 * ----------------- MY METHODS ----------------------- NOTES:
	 */
	public void postLogin() {
		launchIntoApp();
	}

	private void launchLogin() {
		loginFragment = new LoginFragment();
		FragmentManager fm = getSupportFragmentManager();
		fm.beginTransaction().replace(R.id.content_frame, loginFragment).commit();

	}

	private void launchIntoApp() {

		// initialize dynamic variables:
		navigationLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		navigationList = (ListView) findViewById(R.id.drawer_list);
		navigationOptions = getResources().getStringArray(
				R.array.navigation_options);

		// initialize fragments
		homeFragment = new HomeFragment();
		contactsFragment = new ContactsFragment();

		FragmentManager fm = getSupportFragmentManager();
		fm.beginTransaction().remove(loginFragment)
				.add(R.id.content_frame, homeFragment).addToBackStack("HomeFragment").commit();

		// Determine if there are notifications:
		getUpdates();

		// Options Menu setup
		navigationList.setAdapter(new DrawerItemAdapter(this,
				R.layout.view_navlist_item));

		// Final Layout Setup
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		navigationList.setSelection(0);

	}


	private boolean getUpdates() {
		int updateCount = 0;
		// get unseen events
		// get unread activity

		return (updateCount > 0);
	}

	private void selectItem(int position) {

		final ActionBar actionBar = getActionBar();

		Fragment fragment = null;
		CharSequence title = "";
		switch (position) {
		case 0:
			title = getResources().getText(R.string.app_name);
			fragment = homeFragment;
			break;
		case 1:
			title = getResources().getText(R.string.contacts);
			fragment = contactsFragment;
			break;

		case 2:
			// handle for activity

			break;
		case 3:
			// handle my account

			break;

		case 4:
			// handle hall of fame

			break;
		}

		// Bundle args = new Bundle();
		// args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
		// fragment.setArguments(args);

		// Insert the fragment by replacing any existing fragment
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
		//
		// // Highlight the selected item, update the title, and close the
		// drawer
		navigationList.setItemChecked(position, true);
		actionBar.setTitle(title);
		navigationLayout.closeDrawer(navigationList);
	}

	/*
	 * --------------- PRIVATE CLASSES --------------------- NOTES:
	 */

	private class DrawerItemAdapter extends ArrayAdapter<String> implements
			ListView.OnItemClickListener {

		private int layoutRes;
		Context context;

		public DrawerItemAdapter(Context context_, int resource_) {
			super(context_, resource_);
			layoutRes = resource_;
			context = context_;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			super.getView(position, convertView, parent);

			if (convertView == null) {
				LayoutInflater inflater = ((Activity) context)
						.getLayoutInflater();
				convertView = inflater.inflate(layoutRes, parent, false);
			}

			TextView optionText = (TextView) convertView
					.findViewById(R.id.navlistItemText);
			optionText.setText(navigationOptions[position]);

			return convertView;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			selectItem(position);
		}

	}

}
