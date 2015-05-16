package testtask.avast.budgetbuddy.utils;

import testtask.avast.budgetbuddy.controller.TransactionDBController;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "BudgetBuddy.db";
	private static final int DATABASE_VERSION = 1;
	// create table sql statement
	private static final String CREATE_COMMAND = "create table " + TransactionDBController.TABLE_NAME
			+ "(" + TransactionDBController.COLUMN_ID + " integer primary key autoincrement, "
			+ TransactionDBController.COLUMN_GUID + " text not null, " 
			+ TransactionDBController.COLUMN_DESC + " text not null, " 
			+ TransactionDBController.COLUMN_TIMESTAMP + " int, " 
			+ TransactionDBController.COLUMN_AMOUNT + " real, " 
			+ TransactionDBController.COLUMN_DELETED + " int" + ");";
	
	public DBOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_COMMAND);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TransactionDBController.TABLE_NAME);
		onCreate(db);
	}
	
}
