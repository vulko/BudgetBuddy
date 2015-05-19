package testtask.avast.budgetbuddy.Screens;

import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
						try {
							BudgetTransaction transaction = new BudgetTransaction();
							transaction.setAmount(Double.valueOf( newItemValueEdit.getText().toString() ));
							transaction.setID(0);
							transaction.setGUID(AppController.getInstance().getGUID());
							transaction.setDesc("personal");
							transaction.setDeleted(false);
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
		mLoader = new TransactionCursorLoader(getActivity(), mDBHelper, SQL_LITE_READ_STATEMENT, null);

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
							BudgetTransaction transaction = null;
							try {
								transaction = AppController.getInstance().getBudgetModel().getTransaction(id);
								if (transaction != null) {
									transaction.setAmount( Double.valueOf( newValueEdit.getText().toString() ) );
									
									Loader< List<BudgetTransaction> > loader = getActivity().getSupportLoaderManager()
																		.getLoader(BudgetBuddyActivity.LOADER_ID);
									TransactionsDataLoader tloader = (TransactionsDataLoader) loader;
									tloader.update(transaction, new DBChangedListener() {									
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
								}
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
							// The following code wouldn't work after adding new transaction
							// as the model is not updated from this listfragment, as it uses
							// cursor loader... so
							// TODO: actually there should be a single DB facade, or this list fragment
							//       should use BudgetModel and update it, rather then updating DB and restarting CursorLoader
							//       TransactionDataLoader provides more simple way of handling DB, as it
							//       works with instances of BudgetTransaction class.
							//       CursorLoader for this ListFragment works with pure SQL statements, which is
							//       not good in OOP terms as it reduces abstraction
							
							/*BudgetTransaction transaction = null;
							transaction = AppController.getInstance().getBudgetModel().getTransaction(id);
							if (transaction != null) {
								Loader< List<BudgetTransaction> > loader = getActivity().getSupportLoaderManager()
																	.getLoader(BudgetBuddyActivity.LOADER_ID);
								TransactionsDataLoader tloader = (TransactionsDataLoader) loader;
								tloader.delete(transaction, new DBChangedListener() {									
									@Override
									public void onDBChanged() {
										// resfresh loader
										getLoaderManager().restartLoader(LOADER_ID, null, TransactionsListFragment.this);
										mAdapter.notifyDataSetChanged();
									}
								});
							}
							*/
							
							// TODO: the following workaround should be replaced with a proper DB facade design
							//       (see comments above)
							// remove item with a direct SQLLite request, instead of using a DB AsynkTaskLoader
							Loader<Cursor> loader = getLoaderManager().getLoader(LOADER_ID);
							TransactionCursorLoader cloader = (TransactionCursorLoader) loader;
							cloader.delete(TransactionDBController.TABLE_NAME,
									TransactionDBController.COLUMN_ID + "=" + String.valueOf(id),
									null, new DBChangedListener() {
										@Override
										public void onDBChanged() {
											// TODO: this is a workaround to update the main FragmentActivity
											//       as after back pressed BudgetBuddyActivity.onResume is not called
											getActivity().getSupportLoaderManager().restartLoader(BudgetBuddyActivity.LOADER_ID, null, (BudgetBuddyActivity) getActivity());
										}
									});
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
