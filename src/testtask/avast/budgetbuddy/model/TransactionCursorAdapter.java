package testtask.avast.budgetbuddy.model;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;

import testtask.avast.budgetbuddy.controller.ContentChangeTask;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TransactionCursorAdapter extends AbstractTransactionCursorAdapter {
	
	SQLiteOpenHelper mDataBase = null;
	String rawQuery = null;
	String[] args = null;

	/**
	 * Creates a TransactionCursorAdapter for an SQLite DB. See
	 * {@link TransactionCursorAdapter#rawQuery(SQLiteDatabase, String, String[])
	 * SQLiteDatabase.rawQuery()} for documentation on the meaning of the
	 * parameters. These will be passed as-is to that call.
	 */
	public TransactionCursorAdapter(Context context, SQLiteOpenHelper db, String rawQuery, String[] args) {
		super(context);
		
		this.mDataBase = db;
		this.rawQuery = rawQuery;
		this.args = args;
	}

	/**
	 * Runs on a worker thread and performs the actual database query to
	 * retrieve the Cursor.
	 */
	@Override
	protected Cursor buildCursor() {
		return (mDataBase.getReadableDatabase().rawQuery(rawQuery, args));
	}

	/**
	 * Writes a semi-user-readable roster of contents to supplied output.
	 */
	@Override
	public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
		super.dump(prefix, fd, writer, args);
		
		writer.print(prefix);
		writer.print("rawQuery=");
		writer.println(rawQuery);
		writer.print(prefix);
		writer.print("args=");
		writer.println(Arrays.toString(args));
	}

	public void insert(String table, String nullColumnHack, ContentValues values) {
		buildInsertTask(this).execute(mDataBase, table, nullColumnHack, values);
	}

	public void update(String table, ContentValues values, String whereClause,
			String[] whereArgs) {
		buildUpdateTask(this)
				.execute(mDataBase, table, values, whereClause, whereArgs);
	}

	public void replace(String table, String nullColumnHack,
			ContentValues values) {
		buildReplaceTask(this).execute(mDataBase, table, nullColumnHack, values);
	}

	public void delete(String table, String whereClause, String[] whereArgs) {
		buildDeleteTask(this).execute(mDataBase, table, whereClause, whereArgs);
	}

	public void execSQL(String sql, Object[] bindArgs) {
		buildExecSQLTask(this).execute(mDataBase, sql, bindArgs);
	}

	protected ContentChangeTask buildInsertTask(TransactionCursorAdapter loader) {
		return (new InsertTask(loader));
	}

	protected ContentChangeTask buildUpdateTask(TransactionCursorAdapter loader) {
		return (new UpdateTask(loader));
	}

	protected ContentChangeTask buildReplaceTask(TransactionCursorAdapter loader) {
		return (new ReplaceTask(loader));
	}

	protected ContentChangeTask buildDeleteTask(TransactionCursorAdapter loader) {
		return (new DeleteTask(loader));
	}

	protected ContentChangeTask buildExecSQLTask(TransactionCursorAdapter loader) {
		return (new ExecSQLTask(loader));
	}

	protected static class InsertTask extends ContentChangeTask {
		InsertTask(TransactionCursorAdapter loader) {
			super(loader);
		}

		@Override
		protected Void doInBackground(Object... params) {
			SQLiteOpenHelper db = (SQLiteOpenHelper) params[0];
			String table = (String) params[1];
			String nullColumnHack = (String) params[2];
			ContentValues values = (ContentValues) params[3];

			db.getWritableDatabase().insert(table, nullColumnHack, values);

			return (null);
		}
	}

	protected static class UpdateTask extends ContentChangeTask {
		UpdateTask(TransactionCursorAdapter loader) {
			super(loader);
		}

		@Override
		protected Void doInBackground(Object... params) {
			SQLiteOpenHelper db = (SQLiteOpenHelper) params[0];
			String table = (String) params[1];
			ContentValues values = (ContentValues) params[2];
			String where = (String) params[3];
			String[] whereParams = (String[]) params[4];

			db.getWritableDatabase().update(table, values, where, whereParams);

			return (null);
		}
	}

	protected static class ReplaceTask extends ContentChangeTask {
		ReplaceTask(TransactionCursorAdapter loader) {
			super(loader);
		}

		@Override
		protected Void doInBackground(Object... params) {
			SQLiteOpenHelper db = (SQLiteOpenHelper) params[0];
			String table = (String) params[1];
			String nullColumnHack = (String) params[2];
			ContentValues values = (ContentValues) params[3];

			db.getWritableDatabase().replace(table, nullColumnHack, values);

			return (null);
		}
	}

	protected static class DeleteTask extends ContentChangeTask {
		DeleteTask(TransactionCursorAdapter loader) {
			super(loader);
		}

		@Override
		protected Void doInBackground(Object... params) {
			SQLiteOpenHelper db = (SQLiteOpenHelper) params[0];
			String table = (String) params[1];
			String where = (String) params[2];
			String[] whereParams = (String[]) params[3];

			db.getWritableDatabase().delete(table, where, whereParams);

			return (null);
		}
	}

	protected static class ExecSQLTask extends ContentChangeTask {
		ExecSQLTask(TransactionCursorAdapter loader) {
			super(loader);
		}

		@Override
		protected Void doInBackground(Object... params) {
			SQLiteOpenHelper db = (SQLiteOpenHelper) params[0];
			String sql = (String) params[1];
			Object[] bindParams = (Object[]) params[2];

			db.getWritableDatabase().execSQL(sql, bindParams);

			return (null);
		}
	}
	
}
