package testtask.avast.budgetbuddy.controller;

import java.util.List;

import android.content.Context;

import testtask.avast.budgetbuddy.model.BudgetTransaction;
import testtask.avast.budgetbuddy.model.DBChangedListener;

public class TransactionsDataLoader extends AbstractDataLoader< List<BudgetTransaction> > {
	
	private AbstractDBController<BudgetTransaction> mDBController;
	private String mSelection;
	private String[] mSelectionArgs;
	private String mGroupBy;
	private String mHaving;
	private String mOrderBy;

	public TransactionsDataLoader(Context ctx, AbstractDBController control, String selection, String[] selectionArgs, String groupBy,	String having, String orderBy) {
		super(ctx);
		
		mDBController = control;
		mSelection = selection;
		mSelectionArgs = selectionArgs;
		mGroupBy = groupBy;
		mHaving = having;
		mOrderBy = orderBy;
	}

	@Override
	protected List<BudgetTransaction> buildList() {
		List<BudgetTransaction> transactionsList = mDBController.read(mSelection, mSelectionArgs, mGroupBy, mHaving, mOrderBy);
		
		return transactionsList;
	}

	public void insert(BudgetTransaction obj, DBChangedListener listener) {
		new InsertTask(this, listener).execute(obj);
	}

	public void update(BudgetTransaction obj, DBChangedListener listener) {
		new UpdateTask(this, listener).execute(obj);
	}

	public void delete(BudgetTransaction obj, DBChangedListener listener) {
		new DeleteTask(this, listener).execute(obj);
	}

	private class InsertTask extends ContentChangeTask<BudgetTransaction, Void, Void> {
		private DBChangedListener listener = null;
		InsertTask(TransactionsDataLoader loader, DBChangedListener listener) {
			super(loader);
			this.listener = listener;
		}

		@Override
		protected Void doInBackground(BudgetTransaction... params) {
			mDBController.insert(params[0]);
			return (null);
		}
		
		@Override
		protected void onPostExecute(Void res) {
			super.onPostExecute(res);
			if (listener != null) {
				listener.onDBChanged();				
			}
		}
	}

	private class UpdateTask extends ContentChangeTask<BudgetTransaction, Void, Void> {
		private DBChangedListener listener = null;
		UpdateTask(TransactionsDataLoader loader, DBChangedListener listener) {
			super(loader);
			this.listener = listener;
		}

		@Override
		protected Void doInBackground(BudgetTransaction... params) {
			mDBController.update(params[0]);
			
			return (null);
		}

		@Override
		protected void onPostExecute(Void res) {
			super.onPostExecute(res);
			if (listener != null) {
				listener.onDBChanged();				
			}
		}
	}

	private class DeleteTask extends ContentChangeTask<BudgetTransaction, Void, Void> {
		private DBChangedListener listener = null;
		DeleteTask(TransactionsDataLoader loader, DBChangedListener listener) {
			super(loader);
			this.listener = listener;
		}
		
		@Override
		protected Void doInBackground(BudgetTransaction... params) {
			mDBController.delete(params[0]);
			
			return (null);
		}

		@Override
		protected void onPostExecute(Void res) {
			super.onPostExecute(res);
			if (listener != null) {
				listener.onDBChanged();				
			}
		}
	}
}
