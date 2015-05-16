package testtask.avast.budgetbuddy.model;

import java.util.Map;
import java.util.Hashtable;

public class BudgetModel {
	
	BalanceUpdateListener listener;

	private Map<Integer, BudgetTransaction> mTransactionsMap = new Hashtable<Integer, BudgetTransaction>();
	private double currentBalance = 0;
	
	public double getBalance() {
		return currentBalance;
	}
	
	public void addTransaction(BudgetTransaction transaction) {
		mTransactionsMap.put(transaction.getID(), transaction);
		if ( !transaction.isDeleted() ) {
			currentBalance += transaction.getAmount();
		}
	}
	
	public BudgetTransaction getTransaction(int id) {
		return mTransactionsMap.get(id);
	}
	
	public void clear() {
		mTransactionsMap.clear();
	}
	
	public void setBalanceUpdateListener(BalanceUpdateListener listener) {
		this.listener = listener;		
	}
	
	public void notifyBalanceUpdated() {
		if(listener != null)
			listener.onBalanceChanged();
	}
	
}
