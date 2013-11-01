package fragments;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.minook.zeppa.R;

public class ContactsFragment extends Fragment{

	
	// ----------- Global Variables Bank ------------- \\
	// Private
	private View layout;
	private ListView contactList;
	
	// Constant
	
	// Debug
	private String Location = "ContactsFragment";		
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		final ActionBar actionBar = getActivity().getActionBar();
		
		layout = inflater.inflate(R.layout.fragment_contacts, container, false);
		contactList = (ListView) layout.findViewById(R.id.contactsListView);
		ContactListAdapter contactListAdapter = new ContactListAdapter();
		contactList.setAdapter(contactListAdapter);
		
		
		actionBar.setTitle(getResources().getText(R.string.contacts));
		
		
		return layout;
	}
	
	/*
	 *  -----------------  MY METHODS -----------------------
	 * NOTES:
	 * 
	 * */
	
	
	
	/*
	 *  ---------------  PRIVATE CLASSES ---------------------
	 * NOTES:
	 * 
	 * */

	private class ContactListAdapter extends BaseAdapter implements ListView.OnItemClickListener {

		// Hang onto the contact list here
		
		ContactListAdapter(){
			
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 3; // temp
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				Log.d(Location, "convertView == null");
				convertView = getLayoutInflater(null).inflate(R.layout.view_contactlist_item, parent, false);
			}
			
			TextView contactName = (TextView) convertView.findViewById(R.id.contacts_listitem_name);
			contactName.setText("Pete Schuette");
			
			// handle it from here
			
			return convertView;
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
}
