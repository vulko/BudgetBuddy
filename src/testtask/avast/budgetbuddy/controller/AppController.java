package testtask.avast.budgetbuddy.controller;

import testtask.avast.budgetbuddy.SyncManager.BackendSyncManager;
import testtask.avast.budgetbuddy.model.BudgetModel;
import testtask.avast.budgetbuddy.model.UsernameChangedListener;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

public class AppController {

	// Singleton
	private static final AppController mInstance = new AppController();
	private AppController() {}
	public static AppController getInstance() { return mInstance; }
	
	
	// Model
	// TODO: after switching to CursorLoader for a ListFragment with transactions,
	//       this model is not that important. Should be refactored, which
	//       will require to change app architecture as well
	private BudgetModel mModel = new BudgetModel();
	public BudgetModel getBudgetModel() { return mModel; }
	
	
	// Sync manager
	public BackendSyncManager createSyncManager(SQLiteDatabase db) {
		return new BackendSyncManager(db);
	}
	
	
	// Login related
	// TODO: this probably should be moved out of this singleton to a model,
	//		 but since there's just 2 preferences...
	private String mUserName = null;
	// TODO: guid should be calculated from username
	private String mUUID = "6ea4f8dd-382f-44a6-bc0a-91f3c6b8216b";
	private final String USERNAME_PREF_KEY = "user";
	private UsernameChangedListener mListener = null;
	
	public String getGUID() { return mUUID; }
	
	public boolean loginUser(Activity mAct, String user) {
		if (mAct == null || user == null)
			return false;
		
		mUserName = user;
		// notify that user logged in
		mListener.onUsernameChanged();
		SharedPreferences prefs = mAct.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(USERNAME_PREF_KEY, user);
		
		return editor.commit();
	}
	
	public boolean logoutUser(Activity mAct) {
		if (mAct == null)
			return false;
		
		SharedPreferences prefs = mAct.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.remove(USERNAME_PREF_KEY);
		
		return editor.commit();		
	}
	
	public String getUserName(Activity mAct) {
		if (mUserName == null) {
			SharedPreferences prefs = mAct.getPreferences(Context.MODE_PRIVATE);
			mUserName = prefs.getString(USERNAME_PREF_KEY, null);
		}
		
		return mUserName;
	}
	
	public void setUsernameChangedListener(UsernameChangedListener listener) { mListener = listener; }
	
	
}
