package testtask.avast.budgetbuddy.controller;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import testtask.avast.budgetbuddy.model.BudgetTransaction;

public class TransactionDBController extends AbstractDBController<BudgetTransaction> {
	
	public static final String TABLE_NAME = "transactions";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_GUID = "guid";
	public static final String COLUMN_DESC = "desc";
	public static final String COLUMN_TIMESTAMP = "timestamp";
	public static final String COLUMN_VALUE = "amount";
	public static final String COLUMN_DELETED = "deleted";
	public static final String COLUMN_SYNCED = "synced";

	public TransactionDBController(SQLiteDatabase database) {
		super(database);
	}

	@Override
	public boolean insert(BudgetTransaction obj) {
		if (obj == null)
			return false;

		long res = mDatabase.insert(TABLE_NAME, null, getContentValues(obj));
		
		return res != -1;
	}

	@Override
	public boolean delete(BudgetTransaction obj) {
		if (obj == null)
			return false;

		int res = mDatabase.delete(TABLE_NAME, COLUMN_ID + " = " + obj.getID(), null);
		
		return res != 0;
	}

	@Override
	public boolean update(BudgetTransaction obj) {
		if (obj == null)
			return false;

		int res = mDatabase.update(TABLE_NAME, getContentValues(obj), COLUMN_ID + " = " + obj.getID(), null);
		
		return res != 0;
	}

	@Override
	public List<BudgetTransaction> read() {
		Cursor cursor = mDatabase.query(TABLE_NAME, getAllColumns(), null, null, null, null, null);
		List<BudgetTransaction> transactions = new ArrayList<BudgetTransaction>();
		if ( cursor != null && cursor.moveToFirst() ) {
			while (!cursor.isAfterLast()) {
				transactions.add( getBudgetTransaction(cursor) );
				cursor.moveToNext();
			}
			cursor.close();
		}
		
		return transactions;
	}

	@Override
	public List<BudgetTransaction> read(String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
		Cursor cursor = mDatabase.query(TABLE_NAME, getAllColumns(), selection, selectionArgs, groupBy, having, orderBy);
		List<BudgetTransaction> transactions = new ArrayList<BudgetTransaction>();
		if ( cursor != null && cursor.moveToFirst() ) {
			while ( !cursor.isAfterLast() ) {
				transactions.add( getBudgetTransaction(cursor) );
				cursor.moveToNext();
			}
			cursor.close();
		}
		
		return transactions;
	}

	public String[] getAllColumns() {
		return new String[] { COLUMN_ID, COLUMN_GUID, COLUMN_DESC, COLUMN_TIMESTAMP, COLUMN_VALUE, COLUMN_DELETED, COLUMN_SYNCED };
	}

	public BudgetTransaction getBudgetTransaction(Cursor cursor) {
		if (cursor == null)
			return null;

		BudgetTransaction transaction = new BudgetTransaction();
		transaction.setID( cursor.getInt(cursor.getColumnIndex(COLUMN_ID)) );
		transaction.setGUID( cursor.getString(cursor.getColumnIndex(COLUMN_GUID)) );
		transaction.setDesc( cursor.getString(cursor.getColumnIndex(COLUMN_DESC)) );
		transaction.setTimestamp( cursor.getLong(cursor.getColumnIndex(COLUMN_TIMESTAMP)) );
		transaction.setAmount( cursor.getDouble(cursor.getColumnIndex(COLUMN_VALUE)) );
		transaction.setDeleted( cursor.getInt(cursor.getColumnIndex(COLUMN_DELETED)) > 0 );
		transaction.setSynced( cursor.getInt(cursor.getColumnIndex(COLUMN_SYNCED)) > 0 );
		
		return transaction;
	}

	public ContentValues getContentValues(BudgetTransaction obj) {
		if (obj == null)
			return null;

		ContentValues values = new ContentValues();
		values.put(COLUMN_GUID, obj.getGUID());
		values.put(COLUMN_DESC, obj.getDesc());
		values.put(COLUMN_TIMESTAMP, obj.getTimestamp());
		values.put(COLUMN_VALUE, obj.getAmount());
		values.put(COLUMN_DELETED, obj.isDeleted());
		values.put(COLUMN_SYNCED, obj.isSynced());
		return values;
	}

}
