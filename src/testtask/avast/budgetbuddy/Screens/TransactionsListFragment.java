package testtask.avast.budgetbuddy.Screens;

import java.util.List;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.ArrayAdapter;
import testtask.avast.budgetbuddy.controller.TransactionDBController;
import testtask.avast.budgetbuddy.controller.TransactionsDataLoader;
import testtask.avast.budgetbuddy.model.BudgetTransaction;
import testtask.avast.budgetbuddy.utils.DBOpenHelper;

public class TransactionsListFragment extends ListFragment implements LoaderManager.LoaderCallbacks< List<BudgetTransaction> > {

	private static final String TAG = "TransactionsListFragment";
	private static final int LOADER_ID = 1;
	
	private SQLiteDatabase mDatabase;
	private TransactionDBController mDBController;
	private DBOpenHelper mDBHelper;
	private ArrayAdapter<BudgetTransaction> mAdapter;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
			
		// setHasOptionsMenu(true);
		mDBHelper = new DBOpenHelper(getActivity());
		mDatabase = mDBHelper.getWritableDatabase();
		mDBController = new TransactionDBController(mDatabase);
		mAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1);
		setEmptyText("loading data");
		setListAdapter(mAdapter);
		setListShown(false);
		
		List<BudgetTransaction> list = mDBController.read();
		if(list == null || list.size() == 0){
			mDBController.insert(new BudgetTransaction("", "", 0, 100, false));
			mDBController.insert(new BudgetTransaction("", "", 0, 200, false));
			mDBController.insert(new BudgetTransaction("", "", 0, 300, false));
			mDBController.insert(new BudgetTransaction("", "", 0, 400, false));
			mDBController.insert(new BudgetTransaction("", "", 0, 500, false));
		}

		Log.e(TAG, "+++ Calling initLoader()! +++");
		if (getLoaderManager().getLoader(LOADER_ID) == null) {
			Log.e(TAG, "+++ Initializing the new Loader... +++");
		} else {
			Log.e(TAG, "+++ Reconnecting with existing Loader (id '1')... +++");
		}

		// Initialize a Loader with id '1'. If the Loader with this id already
		// exists, then the LoaderManager will reuse the existing Loader.
		getLoaderManager().initLoader(LOADER_ID, null, this);
	}
	
	@Override
	public Loader<List<BudgetTransaction>> onCreateLoader(int arg0, Bundle arg1) {
		TransactionsDataLoader loader = new TransactionsDataLoader(getActivity(), mDBController, null, null, null, null, null);

		return loader;
	}

	@Override
	public void onLoadFinished(Loader<List<BudgetTransaction>> loader, List<BudgetTransaction> list) {
		mAdapter.clear();
		for (BudgetTransaction transaction : list) {
			mAdapter.add(transaction);
		}
		if (isResumed()) {
			setListShown(true);
		} else {
			setListShownNoAnimation(true);
		}
	}

	@Override
	public void onLoaderReset(Loader<List<BudgetTransaction>> arg0) {
		mAdapter.clear();		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mDBHelper.close();
		mDatabase.close();
		mDBController = null;
		mDBHelper = null;
		mDatabase = null;
	}

}
