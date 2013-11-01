package fragments;

import com.minook.zeppa.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ContactFragment extends Fragment{

	// Global Variables:
	// Private
	private View layout;
	
		// -> User class, (info or object)
	
	// Constant
	
	
	/*
	 *  ---------------------- Override Methods ---------------------------
	 * */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		layout = inflater.inflate(R.layout.fragment_contact, container, false);
		
		
		
		return layout;
		
	}
	
	/*
	 * ----------------------- Private Methods -----------------------------
	 * */
	
	

	/*
	 * ----------------------- Private Classes ------------------------------
	 * */

}
