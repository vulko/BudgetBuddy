package testtask.avast.budgetbuddy.Screens;

import testtask.avast.budgetbuddy.R;
import testtask.avast.budgetbuddy.controller.AppController;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginFragment extends Fragment {
	
	static final String TAG = "LoginFragment";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.login_screen, container, false);
		
		// find person name EditText to save in Shared Prefs
		final EditText etPersonName = (EditText) view.findViewById(R.id.etPersonName);	
		// find login Button and handle click
		final Button btnLogin = (Button) view.findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new View.OnClickListener() {		
			
			@Override
			public void onClick(View v) {
				if ( !etPersonName.getText().toString().equals("") ) {
					if ( AppController.getInstance().loginUser(getActivity(), etPersonName.getText().toString()) ) {
						getActivity().getSupportFragmentManager().beginTransaction().remove(LoginFragment.this).commit();
					} else {
						// TODO: failed to save shared prefs, need to handle it
						Log.e(TAG, "failed to save shared prefs ");						
					}
				} else {
					// TODO: show message to user, that name field is empty
					Log.e(TAG, "user name is empty!");
				}
			}
		});
		
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	

}
