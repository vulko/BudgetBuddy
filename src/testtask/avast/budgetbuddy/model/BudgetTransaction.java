package testtask.avast.budgetbuddy.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class BudgetTransaction implements Serializable {

	/**
	 * Serial version
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * This field corresponds to and ID of a DB row
	 */
	private transient int id;
	
	/**
	 * TRUE if this transaction was isSynced with backend
	 */
	private transient boolean isSynced = false;
	
	/**
	 * A string containing a transaction GUID:
	 */
	private String mGUID;
	
	/**
	 * A description of the transaction
	 */
	private String mDesc;
	
	/**
	 * A date and time of the transaction defined as the number of milliseconds since 1970
	 */
	private long mTimestamp;
	
	/**
	 * An amount of money transferred by the transaction
	 */
	private double mAmount;
	
	/**
	 * If TRUE the transaction is deleted. It mustn�t be neither counted into the account
     * balance nor displayed to the user
	 */
	private boolean isDeleted;
	
	public BudgetTransaction() {
	}
	
	public BudgetTransaction(String guid, String desc, long timestamp, double amount, boolean delete, boolean synced) {
		mGUID = guid;
		mDesc = desc;
		mTimestamp = timestamp;
		mAmount = amount;
		isDeleted = delete;
		isSynced = synced;
	}
	
	/**
	 * @return ID of a DB row with current transaction
	 */
	public int getID() {
		return id;
	}
	
	public void setID(int _id) {
		id = _id;
	}

	/**
	 * @return A string containing a transaction GUID:
	 */
	public String getGUID() {
		return mGUID;
	}

	public void setGUID(String mGUID) {
		this.mGUID = mGUID;
	}

	/**
	 * @return A description of the transaction
	 */
	public String getDesc() {
		return mDesc;
	}

	public void setDesc(String mDesc) {
		this.mDesc = mDesc;
	}

	/**
	 * @return LONG date and time of the transaction defined as the number of milliseconds since 1970
	 */
	public long getTimestamp() {
		return mTimestamp;
	}

	public void setTimestamp(long mTimestamp) {
		this.mTimestamp = mTimestamp;
	}

	/**
	 * @return amount of money transferred by the transaction
	 */
	public double getAmount() {
		return mAmount;
	}

	public void setAmount(double mAmount) {
		this.mAmount = mAmount;
	}

	/**
	 * @return TRUE if transaction was deleted
	 */
	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	
	/**
	 * @return amount of money transferred by the transaction
	 */
	@Override
	public String toString() {
		return String.valueOf(mAmount);	
	}

	/**
	 * This is the default implementation of readObject.
	 */
	private void readObject(ObjectInputStream inStream) throws ClassNotFoundException, IOException {
		// always perform the default de-serialization first
		inStream.defaultReadObject();
	}

	/**
	 * This is the default implementation of writeObject.
	 */
	private void writeObject(ObjectOutputStream outStream) throws IOException {
		outStream.defaultWriteObject();
	}

	/**
	 * @return returns a serialized object as byte[]
	 */
	public byte[] serialize() {
		ByteArrayOutputStream boStream = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(boStream);
			out.writeObject(this);
		} catch (IOException e) {
			// TODO: handle or add throws declaration
		} finally {
			try {
				boStream.close();
				if (out != null)
					out.close();
			} catch (IOException ex) {
				// TODO: handle or add throws declaration
			}
		}
		
		return boStream.toByteArray();
	}

	
	/**
	 * @return TRUE if this transaction was isSynced with backend
	 */
	public boolean isSynced() {
		return isSynced;
	}

	/**
	 * @return sets isSynced flag (for synchronization with backend)
	 */
	public void setSynced(boolean synced) {
		this.isSynced = synced;
	}

}
