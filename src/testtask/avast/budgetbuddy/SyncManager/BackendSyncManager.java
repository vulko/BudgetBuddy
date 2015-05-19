package testtask.avast.budgetbuddy.SyncManager;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import testtask.avast.budgetbuddy.controller.TransactionDBController;
import testtask.avast.budgetbuddy.model.BudgetTransaction;
import testtask.avast.budgetbuddy.utils.Proto.Transaction;

public class BackendSyncManager extends AsyncTask<Void, Void, Boolean> {
	
	private static final String TAG = "BackendSyncManager";
	private static final String BACKEND_API_URL = "http://bean-keeper.appspot.com/bk";
	// funny thing. the following String looks the same, but it is actually not.
	// it was copy-pasted from PDF opened in GMAIL, and if you copy paste it to browser
	// you won't see the "-" symbol... several hours spent looking for an issue... :)
	private static final String BACKEND_API_UR1 = "http://bean­keeper.appspot.com/bk";
	
	private SQLiteDatabase mDatabase = null;
	
	public BackendSyncManager(SQLiteDatabase db) {
		mDatabase = db;
	}
	
	public boolean syncWithBackend(BudgetTransaction transaction) {
		try {
			Transaction data = Transaction.newBuilder()
					.setDate(transaction.getTimestamp())
					.setDeleted(transaction.isDeleted())
					.setGuid(transaction.getGUID())
					.setKind(transaction.getDesc())
					.setValue(transaction.getAmount()).build();
		    
			URL destination = new URL(BACKEND_API_URL);
			BufferedOutputStream os = null;
			HttpURLConnection connection = null;
			connection = (HttpURLConnection) destination.openConnection();
			if (connection == null) {
				// TODO: apparently no internet connection. handle it.
				Log.e(TAG, "Connection to " + destination + "failed");
				
				return false;
			}
			connection.setDoOutput(true);
			connection.setChunkedStreamingMode(0);
			//connection.setRequestProperty("User-Agent", "android");
			os = new BufferedOutputStream(connection.getOutputStream());
			os.write(data.toByteArray());
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				// sync ok
				Log.e(TAG, "Sync ok" + connection.getResponseMessage());
				InputStream response = connection.getInputStream();			
				Transaction inData = Transaction.parseFrom(response);
				Log.e(TAG, data.getGuid() + " | " + data.getKind() + " | " + data.getDate() + " | " + data.getValue() + " | " + data.getDeleted());
				Log.e(TAG, inData.getGuid() + " | " + inData.getKind() + " | " + inData.getDate() + " | " + inData.getValue() + " | " + inData.getDeleted());
				
				//return true;
				return false;
			} else {
				// TODO: notify user that sync failed
				Log.e(TAG, "Error: " + connection.getResponseMessage());
				
				return false;
			}
			
/*			DefaultHttpClient httpClient = new DefaultHttpClient();
			
			//URI resolved = new URI("http", "bean-keeper.appspot.com", "/bk", "");
			//URI tmp = URIUtils.createURI("http", "bean­keeper.appspot.com", 80, "/bk", "", "");
			//HttpPost httpPost = new HttpPost("http://64.233.161.141:80/bk");
			HttpPost httpPost = new HttpPost(BACKEND_API_URL);
	        httpPost.setEntity( new ByteArrayEntity(data.toByteArray()) );	
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream is = httpEntity.getContent();
			Transaction inData = Transaction.parseFrom(is);
			Log.e(TAG, "responce: " + data.getGuid() + " | " + data.getKind() + " | " + data.getDate() + " | " + data.getValue() + " | " + data.getDeleted());
			Log.e(TAG, "responce: " + inData.getGuid() + " | " + inData.getKind() + " | " + inData.getDate() + " | " + inData.getValue() + " | " + inData.getDeleted());
			is.close();
			
			return false;
*/			
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return false;
		}
		
		//return true;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		// get all transactions that were not synched
		TransactionDBController dbControl = new TransactionDBController(mDatabase);
		List<BudgetTransaction> notSyncedList = dbControl.read(TransactionDBController.COLUMN_SYNCED + "= '0'",
															   null,
															   "",
															   "",
															   "");

		if (notSyncedList != null && notSyncedList.size() > 0) {
			for (BudgetTransaction transaction : notSyncedList) {
				if ( syncWithBackend(transaction) ) {
					// if sync success
					transaction.setSynced(true);
					if ( transaction.isDeleted() ) { // if marked for deletion, delete
						dbControl.delete(transaction);						
					} else {                         // otherwise mark as synced
						dbControl.update(transaction);						
					}
				}
			}
		} else {
			return false;
		}
		
		return true;
	}

}
