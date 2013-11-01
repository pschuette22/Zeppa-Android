package fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.minook.zeppa.R;

public class WatchingFragment extends Fragment{

	// Global Variables
	// Dynamic
	View layout;
	
	// Constants
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		
		layout = inflater.inflate(R.layout.fragment_watching, container, false);
		
		return layout;
	}
	
	

}
