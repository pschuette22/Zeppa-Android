package fragments;

import android.accounts.AccountManager;
import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.minook.zeppa.MainActivity;
import com.minook.zeppa.R;

public class LoginFragment extends Fragment implements OnClickListener{

	View layout;
	Button signInButton;
	EditText emailText, passwordText;
	AccountManager accountManager;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		layout = inflater.inflate(R.layout.fragment_login, container, false);
		setHasOptionsMenu(true);
		
		signInButton = (Button) layout.findViewById(R.id.login_button);
		signInButton.setOnClickListener(this);
		
		emailText = (EditText) layout.findViewById(R.id.email_address_input);
		passwordText = (EditText) layout.findViewById(R.id.gaccount_password_input);
		
		final ActionBar actionBar = getActivity().getActionBar();
		actionBar.setNavigationMode(0);
		
		return layout;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_login, menu);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.login_button:
			String email = emailText.getText().toString();
			String password = passwordText.getText().toString();
			if(email.isEmpty() || password.isEmpty()){
				// throw an error message
			}
			
			else {
				boolean loginSuccess = attemptLogin(email, password);
				if(loginSuccess){
					MainActivity activity = (MainActivity) getActivity();
					activity.postLogin();
				}
				else {
					// throw login error here
				}
			}
			
			break;
			
		}
		
	}
	
	

	/*
	 * ----------------- MY METHODS ----------------------- 
	 * NOTES:
	 */
	

	private boolean attemptLogin(String email, String password){
		
		// place login logic here
		
		return true;
	}
	

}
