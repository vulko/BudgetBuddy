package testtask.avast.budgetbuddy.Screens;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.ResourceCursorAdapter;
import testtask.avast.budgetbuddy.R;
import testtask.avast.budgetbuddy.controller.TransactionDBController;
import testtask.avast.budgetbuddy.controller.TransactionsDataLoader;
import testtask.avast.budgetbuddy.model.BudgetTransaction;
import testtask.avast.budgetbuddy.model.TransactionCursorLoader;
import testtask.avast.budgetbuddy.utils.DBOpenHelper;

public class TransactionsListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = "TransactionsListFragment";
	private static final int LOADER_ID = 0;
	private static final String SQL_LITE_READ_STATEMENT = "SELECT " + TransactionDBController.COLUMN_ID
			+ ", " + TransactionDBController.COLUMN_GUID
			+ ", " + TransactionDBController.COLUMN_DESC
			+ ", " + TransactionDBController.COLUMN_TIMESTAMP
			+ ", " + TransactionDBController.COLUMN_VALUE
			+ ", " + TransactionDBController.COLUMN_DELETED
			+ " FROM " + TransactionDBController.TABLE_NAME + " ORDER BY " + TransactionDBController.COLUMN_TIMESTAMP;
	
	private SQLiteDatabase mDatabase = null;
	private TransactionDBController mDBController = null;
	private DBOpenHelper mDBHelper = null;
	private CursorAdapter mAdapter = null;
	private TransactionCursorLoader mLoader = null;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
			
		mDBHelper = new DBOpenHelper(getActivity());
		mDatabase = mDBHelper.getWritableDatabase();
		mDBController = new TransactionDBController(mDatabase);
		mAdapter = new ClientCursorAdapter(getActivity(), R.layout.row, null, 0 );

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

		getLoaderManager().initLoader(LOADER_ID, null, this);
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		mLoader = new TransactionCursorLoader(getActivity(), mDBHelper, SQL_LITE_READ_STATEMENT, null);

		return mLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mLoader = (TransactionCursorLoader) loader;
	    mAdapter.changeCursor(cursor);

		if (isResumed()) {
			setListShown(true);
		} else {
			setListShownNoAnimation(true);
		}  
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdapter.changeCursor(null);;		
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
	
	public class ClientCursorAdapter extends ResourceCursorAdapter {

		public ClientCursorAdapter(Context context, int layout, Cursor c, int flags) {
			super(context, layout, c, flags);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			TextView id = (TextView) view.findViewById(R.id.title);
			id.setText(String.valueOf( cursor.getInt(cursor.getColumnIndex(TransactionDBController.COLUMN_ID)) ));

			TextView value = (TextView) view.findViewById(R.id.value);
			value.setText(cursor.getString(cursor.getColumnIndex(TransactionDBController.COLUMN_VALUE)));
		}
		
	}

}
