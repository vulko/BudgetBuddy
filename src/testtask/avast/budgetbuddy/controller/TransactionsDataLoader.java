package testtask.avast.budgetbuddy.controller;

import java.util.List;

import android.content.Context;

import testtask.avast.budgetbuddy.model.BudgetTransaction;

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

	public void insert(BudgetTransaction obj) {
		new InsertTask(this).execute(obj);
	}

	public void update(BudgetTransaction obj) {
		new UpdateTask(this).execute(obj);
	}

	public void delete(BudgetTransaction obj) {
		new DeleteTask(this).execute(obj);
	}

	private class InsertTask extends ContentChangeTask<BudgetTransaction, Void, Void> {
		InsertTask(TransactionsDataLoader loader) { super(loader); }

		@Override
		protected Void doInBackground(BudgetTransaction... params) {
			mDBController.insert(params[0]);
			return (null);
		}
	}

	private class UpdateTask extends ContentChangeTask<BudgetTransaction, Void, Void> {
		UpdateTask(TransactionsDataLoader loader) { super(loader); }

		@Override
		protected Void doInBackground(BudgetTransaction... params) {
			mDBController.update(params[0]);
			
			return (null);
		}
	}

	private class DeleteTask extends ContentChangeTask<BudgetTransaction, Void, Void> {
		DeleteTask(TransactionsDataLoader loader) {	super(loader); }

		@Override
		protected Void doInBackground(BudgetTransaction... params) {
			mDBController.delete(params[0]);
			
			return (null);
		}
	}
}
