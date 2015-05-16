package testtask.avast.budgetbuddy.controller;

import java.util.List;
import android.database.sqlite.SQLiteDatabase;

public abstract class AbstractDBController<T> {
	
	protected SQLiteDatabase mDatabase;

	public AbstractDBController(SQLiteDatabase database) { mDatabase = database; }

	public abstract boolean insert(T obj);
	public abstract boolean delete(T obj);
	public abstract boolean update(T obj);

	public abstract List read();
	public abstract List read(String selection, String[] selectionArgs, String groupBy, String having, String orderBy);
}
