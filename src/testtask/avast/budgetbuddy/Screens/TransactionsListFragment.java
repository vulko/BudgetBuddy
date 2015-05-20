package testtask.avast.budgetbuddy.Screens;

import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.ResourceCursorAdapter;
import android.text.InputType;
import android.text.method.DateTimeKeyListener;
import testtask.avast.budgetbuddy.BudgetBuddyActivity;
import testtask.avast.budgetbuddy.R;
import testtask.avast.budgetbuddy.controller.AbstractDataLoader;
import testtask.avast.budgetbuddy.controller.AppController;
import testtask.avast.budgetbuddy.controller.TransactionDBController;
import testtask.avast.budgetbuddy.controller.TransactionsDataLoader;
import testtask.avast.budgetbuddy.model.BudgetTransaction;
import testtask.avast.budgetbuddy.model.DBChangedListener;
import testtask.avast.budgetbuddy.model.TransactionCursorLoader;
import testtask.avast.budgetbuddy.utils.DBOpenHelper;

public class TransactionsListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = "TransactionsListFragment";
	private static final int LOADER_ID = 1;
    static final int INTERNAL_EMPTY_ID = 16711681;
    static final int INTERNAL_LIST_CONTAINER_ID = 16711683;
	
	private SQLiteDatabase mDatabase = null;
	private TransactionDBController mDBController = null;
	private DBOpenHelper mDBHelper = null;
	private CursorAdapter mAdapter = null;
	private TransactionCursorLoader mLoader = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.transaction_list_screen, container, false);
        view.findViewById(R.id.list_container_id).setId(INTERNAL_LIST_CONTAINER_ID);
	    
	    return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        getView().findViewById(R.id.btnAddTransaction).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) { // ON ADD ITEM
				AlertDialog dialog;
				final EditText newItemValueEdit = new EditText(getActivity());
				newItemValueEdit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setMessage("Set amount value").setTitle("Add new transaction");
				builder.setView(newItemValueEdit);
				builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO: as there's no single and nice facade for DB, use a data loader method
						//       need to make a single and nice DB facade, instead of an AsyncLoader (TransactionsDataLoader).
						try {
							BudgetTransaction transaction = new BudgetTransaction();
							transaction.setAmount(Double.valueOf( newItemValueEdit.getText().toString() ));
							transaction.setID(0);
							transaction.setGUID(AppController.getInstance().getGUID());
							transaction.setDesc("personal");
							transaction.setDeleted(false);
							transaction.setSynced(false);
							// init with current time
							transaction.setTimestamp(new Date().getTime());
							Loader< List<BudgetTransaction> > loader = getActivity().getSupportLoaderManager()
									.getLoader(BudgetBuddyActivity.LOADER_ID);
							TransactionsDataLoader tloader = (TransactionsDataLoader) loader;
							tloader.insert(transaction, new DBChangedListener() {									
								@Override
								public void onDBChanged() {
									// resfresh loader
									getLoaderManager().restartLoader(LOADER_ID, null, TransactionsListFragment.this);
									mAdapter.notifyDataSetChanged();
									// TODO: this is a workaround to update the main FragmentActivity
									//       as after back pressed BudgetBuddyActivity.onResume is not called
									getActivity().getSupportLoaderManager().restartLoader(BudgetBuddyActivity.LOADER_ID, null, (BudgetBuddyActivity) getActivity());
								}
							});
						} catch (NumberFormatException e) {
							// TODO: handle incorrect input
							Toast.makeText(getActivity(), "Incorrect input type", Toast.LENGTH_LONG).show();
						}
					}
				});
				builder.setNegativeButton("no", null);
				dialog = builder.create();
				dialog.show();					
			}
		});
		

		mDBHelper = new DBOpenHelper(getActivity());
		mDatabase = mDBHelper.getWritableDatabase();
		mDBController = new TransactionDBController(mDatabase);
		mAdapter = new ClientCursorAdapter(getActivity(), R.layout.transaction_list_row, null, 0 );
		setListAdapter(mAdapter);
		//setListShown(false);
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		mLoader = new TransactionCursorLoader(getActivity(), mDBHelper, TransactionDBController.SQL_DONT_READ_DELETED_STATEMENT, null);

		return mLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mLoader = (TransactionCursorLoader) loader;
	    mAdapter.changeCursor(cursor);

		if (isResumed()) {
			//setListShown(true);
		} else {
			setListShownNoAnimation(true);
		}  
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdapter.changeCursor(null);		
	}
	
	@Override
	public void onResume() {
		super.onResume();		
	
		// TODO: instead of restarting loader every time switch to a Notifier-Listener pattern
		//       when data has been changed then restart, otherwise init 
		getLoaderManager().restartLoader(LOADER_ID, null, this);
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
			final int id = cursor.getInt(cursor.getColumnIndex(TransactionDBController.COLUMN_ID));

			TextView value = (TextView) view.findViewById(R.id.amount);
			value.setText(String.valueOf( cursor.getDouble(cursor.getColumnIndex(TransactionDBController.COLUMN_VALUE)) ));
			
			Button btnEdit = (Button) view.findViewById(R.id.btnEditTransaction);
			btnEdit.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					AlertDialog dialog;
					final EditText newValueEdit = new EditText(getActivity());
					newValueEdit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setMessage("Set new value").setTitle("Edit transaction");
					builder.setView(newValueEdit);
					builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {					
						@Override
						public void onClick(DialogInterface dialog, int which) { // ON EDIT ITEM
							try {
								final double newValue = Double.valueOf(newValueEdit.getText().toString());

								// TODO: this should be moved out to a DB facade
								AsyncTask<Void, Void, List<BudgetTransaction>> task = new AsyncTask<Void, Void, List<BudgetTransaction>>() {
									@Override
									protected List<BudgetTransaction> doInBackground(Void... params) {
										return mDBController.read(TransactionDBController.COLUMN_ID + "= '" + id + "'",
			   										  			  null,
			   										  			  "",
			   										  			  "",
																  "");
									}
									
									@Override
									protected void onPostExecute(List<BudgetTransaction> list) {
										if (list != null && list.size() > 0) {
											// update value and mark as not synced
											list.get(0).setAmount(newValue);
											list.get(0).setSynced(false);
											mDBController.update(list.get(0));

											// update list of transactions
											getLoaderManager().restartLoader(LOADER_ID, null, TransactionsListFragment.this);
											mAdapter.notifyDataSetChanged();
											// this will update balance value and model
											getActivity().getSupportLoaderManager().restartLoader(BudgetBuddyActivity.LOADER_ID, null, (BudgetBuddyActivity) getActivity());
										}
									}
								};
								task.execute();
							} catch (NumberFormatException e) {
								// TODO: handle incorrect input
								Toast.makeText(getActivity(), "Incorrect input type", Toast.LENGTH_LONG).show();
							}
						}
					});
					builder.setNegativeButton("no", null);
					dialog = builder.create();
					dialog.show();					
				}
			});

			Button btnRemove = (Button) view.findViewById(R.id.btnRemoveTransaction);
			btnRemove.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) { // ON REMOVE ITEM
					AlertDialog dialog;
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setMessage("Are you sure?").setTitle("Deleting transaction");
					builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {					
						@Override
						public void onClick(DialogInterface dialog, int which) {												
							// TODO: this should be moved out to a DB facade
							AsyncTask<Void, Void, List<BudgetTransaction>> task = new AsyncTask<Void, Void, List<BudgetTransaction>>() {
								@Override
								protected List<BudgetTransaction> doInBackground(Void... params) {
									return mDBController.read(TransactionDBController.COLUMN_ID + "= '" + id + "'",
		   										  			  null,
		   										  			  "",
		   										  			  "",
															  "");
								}
								
								@Override
								protected void onPostExecute(List<BudgetTransaction> list) {
									if (list != null && list.size() > 0) {
										// mark as deleted and synced and syncmanager will take care of the rest
										list.get(0).setDeleted(true);
										list.get(0).setSynced(false);
										mDBController.update(list.get(0));

										// update list of transactions
										getLoaderManager().restartLoader(LOADER_ID, null, TransactionsListFragment.this);
										mAdapter.notifyDataSetChanged();
										// this will update balance value and model
										getActivity().getSupportLoaderManager().restartLoader(BudgetBuddyActivity.LOADER_ID, null, (BudgetBuddyActivity) getActivity());
									}
								}
							};
							task.execute();
						}
					});
					builder.setNegativeButton("no", null);
					dialog = builder.create();
					dialog.show();
				}
			});
		}
		
	}

}
