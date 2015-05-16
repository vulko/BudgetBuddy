package testtask.avast.budgetbuddy.model;

public class BudgetTransaction {

	private int id;
	private String mGUID;
	private String mDesc;
	private long mTimestamp;
	private double mAmount;
	private boolean isDeleted;
	
	public BudgetTransaction() {
	}
	
	public BudgetTransaction(String guid, String desc, long timestamp, double amount, boolean delete) {
		mGUID = guid;
		mDesc = desc;
		mTimestamp = timestamp;
		mAmount = amount;
		isDeleted = delete;
	}
	
	public int getID() {
		return id;
	}
	
	public void setID(int _id) {
		id = _id;
	}

	public String getGUID() {
		return mGUID;
	}

	public void setGUID(String mGUID) {
		this.mGUID = mGUID;
	}

	public String getDesc() {
		return mDesc;
	}

	public void setDesc(String mDesc) {
		this.mDesc = mDesc;
	}

	public long getTimestamp() {
		return mTimestamp;
	}

	public void setTimestamp(long mTimestamp) {
		this.mTimestamp = mTimestamp;
	}

	public double getAmount() {
		return mAmount;
	}

	public void setAmount(double mAmount) {
		this.mAmount = mAmount;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	
	@Override
	public String toString() {
		return String.valueOf(mAmount);	
	}

}
