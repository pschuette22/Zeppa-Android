package fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.minook.zeppa.R;

public class CalendarFragment extends Fragment{

	//Global Variables
	// Dynamic
	View layout;
	
	// Constants
	
	
	// handles the creating of the view
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		layout = inflater.inflate(R.layout.fragment_calendar, container, false);
		
		// TODO: Everything
		
		return layout;
	}

	
	
}
