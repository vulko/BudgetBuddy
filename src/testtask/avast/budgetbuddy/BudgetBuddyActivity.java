package testtask.avast.budgetbuddy;

import testtask.avast.budgetbuddy.utils.DBOpenHelper;
import testtask.avast.budgetbuddy.model.BalanceUpdateListener;
import testtask.avast.budgetbuddy.model.BudgetModel;
import testtask.avast.budgetbuddy.model.BudgetTransaction;
import testtask.avast.budgetbuddy.model.UsernameChangedListener;
import testtask.avast.budgetbuddy.Screens.LoginFragment;
import testtask.avast.budgetbuddy.Screens.TransactionsListFragment;
import testtask.avast.budgetbuddy.Screens.TransactionsListFragment.ClientCursorAdapter;
import testtask.avast.budgetbuddy.SyncManager.BackendSyncManager;
import testtask.avast.budgetbuddy.controller.AppController;
import testtask.avast.budgetbuddy.controller.TransactionDBController;
import testtask.avast.budgetbuddy.controller.TransactionsDataLoader;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class BudgetBuddyActivity extends FragmentActivity implements
												LoaderManager.LoaderCallbacks< List<BudgetTransaction> >,
												BalanceUpdateListener {

	public static final int LOADER_ID = 0;
	private SQLiteDatabase mDatabase = null;
	private TransactionDBController mDBController = null;
	private DBOpenHelper mDBHelper = null;
	private EditText etBalance = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AppController.getInstance().getUserName(this) == null) { // if user didn't login yet or logged out
        	showLoginScreen();
        } 
        
        setContentView(R.layout.main_screen);
        
        etBalance = (EditText) findViewById(R.id.etBalance);
        final TextView tvHello = (TextView) findViewById(R.id.tvPersonName);
        AppController.getInstance().setUsernameChangedListener(new UsernameChangedListener() {		
			@Override
			public void onUsernameChanged() { // change hello text when user logs in
				tvHello.setText("Hello " + AppController.getInstance().getUserName(BudgetBuddyActivity.this));
			}
		});
        tvHello.setText("Hello " + AppController.getInstance().getUserName(this));
        final Button btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
        	
			@Override
			public void onClick(View v) {
				AppController.getInstance().logoutUser(BudgetBuddyActivity.this);
				showLoginScreen();
			}
		});

        final Button btnShowTransactionsList = (Button) findViewById(R.id.btnShowTransactionsList);
        btnShowTransactionsList.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showTransactionListScreen();
			}
		});

        final Button btnResetDBandInit = (Button) findViewById(R.id.btnResetDBandInit);
        btnResetDBandInit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				resetDBandInit();	
			}
		});
    }
    
    @Override
    public void onStart() {
    	super.onStart();

    	// listen to balance updates
    	AppController.getInstance().getBudgetModel().setBalanceUpdateListener(this);
		mDBHelper = new DBOpenHelper(this);
		mDatabase = mDBHelper.getWritableDatabase();        
		mDBController = new TransactionDBController(mDatabase);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	
 		etBalance.setText( "_" );
		// TODO: instead of restarting loader every time switch to a Notifier-Listener pattern,
		//       when data has been changed then restart, otherwise init loader
    	getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    			
    	BackendSyncManager syncManager = AppController.getInstance().createSyncManager(mDatabase);
    	syncManager.execute();
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    	
    	// remove listener
    	AppController.getInstance().getBudgetModel().setBalanceUpdateListener(null);
    	getSupportLoaderManager().destroyLoader(LOADER_ID);
    	mDBHelper.close();
    	mDatabase.close();
    	mDBController = null;
    	mDBHelper = null;
		mDatabase = null;
    }
    
    public void showLoginScreen() {
        Fragment loginFragment = new LoginFragment();
        loginFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(android.R.id.content, loginFragment).commit();                	
    }

    public void showTransactionListScreen() {
        Fragment listFragment = new TransactionsListFragment();
        listFragment.setArguments(getIntent().getExtras());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(android.R.id.content, listFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

	@Override
	public Loader<List<BudgetTransaction>> onCreateLoader(int arg0, Bundle arg1) {
		etBalance.setText( "_" );
		TransactionsDataLoader loader = new TransactionsDataLoader(this, mDBController, null, null, null, null, null);
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<List<BudgetTransaction>> loader, List<BudgetTransaction> list) {
		BudgetModel model = AppController.getInstance().getBudgetModel();
		model.clear();
		if( list != null && list.size() > 0 ) {
			for (BudgetTransaction budgetTransaction : list) {
				model.addTransaction(budgetTransaction);
			}
		}
		model.notifyBalanceUpdated();
	}

	@Override
	public void onLoaderReset(Loader<List<BudgetTransaction>> arg0) {
		etBalance.setText( "_" );
	}

	@Override
	public void onBalanceChanged() {
		etBalance.setText( String.valueOf(AppController.getInstance().getBudgetModel().getBalance()) );
	}
	
	public void resetDBandInit() {
		mDBHelper.clearDatabase(mDatabase);	
		List<BudgetTransaction> list = mDBController.read();
		if(list == null || list.size() == 0){
			mDBController.insert(new BudgetTransaction(AppController.getInstance().getGUID(), "personal", new Date().getTime(), 100, false, false));
			mDBController.insert(new BudgetTransaction(AppController.getInstance().getGUID(), "beauty", new Date().getTime(), 200, false, false));
			mDBController.insert(new BudgetTransaction(AppController.getInstance().getGUID(), "spa", new Date().getTime(), 300, false, false));
			mDBController.insert(new BudgetTransaction(AppController.getInstance().getGUID(), "vacation", new Date().getTime(), 400, false, false));
			mDBController.insert(new BudgetTransaction(AppController.getInstance().getGUID(), "shopping", new Date().getTime(), 500, false, false));
		}		
	}
    
}
