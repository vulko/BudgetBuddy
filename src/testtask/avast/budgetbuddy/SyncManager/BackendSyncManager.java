package testtask.avast.budgetbuddy.SyncManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;

import android.database.sqlite.SQLiteDatabase;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import testtask.avast.budgetbuddy.controller.TransactionDBController;
import testtask.avast.budgetbuddy.model.BudgetTransaction;

public class BackendSyncManager extends AsyncTask<Void, Void, Boolean> {
	
	private static final String TAG = "BackendSyncManager";
	private static final String BACKEND_API_URL = "http://bean­keeper.appspot.com/bk/";
	
	private SQLiteDatabase mDatabase = null;
	
	public BackendSyncManager(SQLiteDatabase db) {
		mDatabase = db;
	}
	
	public boolean syncWithBackend(BudgetTransaction transaction) {
		try {		
			AndroidHttpClient httpClient = AndroidHttpClient.newInstance("Android");
			URI resolved = new URI("http://bean-keeper.appspot.com/bk");
			//URI tmp = URIUtils.createURI("http", "bean­keeper.appspot.com", 80, "/bk", "", "");
			HttpPost httpPost = new HttpPost("http://74.125.205.141:80/bk");
	        httpPost.setEntity( new ByteArrayEntity(transaction.serialize()) );	
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream is = httpEntity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "n");
			}
			is.close();
		} catch (ClientProtocolException e) {
			// TODO handle exception
			return false;
		} catch (IOException e) {
			// TODO handle exception
			return false;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return false;
		}
		
		return true;
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
