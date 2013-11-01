package fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.minook.zeppa.R;

public class FeedFragment extends Fragment{

	// ----------- Global Variables Bank ------------- \\
	// Private
	private View layout;
	private ListView feedList;
	
	//Constants
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		layout = inflater.inflate(R.layout.fragment_feed, container, false);
		feedList = (ListView) layout.findViewById(R.id.feedListView);
		
		FeedListAdapter flAdapter = new FeedListAdapter();
		feedList.setAdapter(flAdapter);
		
		
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
	
	private class FeedListAdapter extends BaseAdapter implements ListView.OnItemClickListener {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
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
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			
		}
		
	}

	
	
}
