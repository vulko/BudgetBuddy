package testtask.avast.budgetbuddy;

import testtask.avast.budgetbuddy.Screens.LoginFragment;
import testtask.avast.budgetbuddy.Screens.TransactionsListFragment;
import testtask.avast.budgetbuddy.controller.AppController;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class BudgetBuddyActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AppController.getInstance().getUserName(this) == null) { // if user didn't login yet or logged out
        	showLoginScreen();
        } 
        // temporary!
        else {
            Fragment listFragment = new TransactionsListFragment();
            listFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, listFragment).commit();                	        	
        }
        
        setContentView(R.layout.main_screen);
        
        final TextView tvHello = (TextView) findViewById(R.id.tvPersonName);
        tvHello.setText("Hello " + AppController.getInstance().getUserName(this));
        final Button btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
        	
			@Override
			public void onClick(View v) {
				AppController.getInstance().logoutUser(BudgetBuddyActivity.this);
				showLoginScreen();
			}
		});
    }
    
    public void onResume() {
    	super.onResume();
    }
    
    public void showLoginScreen() {
        Fragment loginFragment = new LoginFragment();
        loginFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(android.R.id.content, loginFragment).commit();                	
    }
    
}
