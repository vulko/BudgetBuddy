package testtask.avast.budgetbuddy.controller;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class AppController {

	// Singleton
	private static final AppController mInstance = new AppController();
	private AppController() {}
	public static AppController getInstance() { return mInstance; }
	
	
	// Login related
	// TODO: this probably should be moved out of this singleton to a model,
	//		 but since there's just 2 preferences...
	private String mUserName = null;
	private String mUUID = null;
	private final String USERNAME_PREF_KEY = "user";
	
	public boolean loginUser(Activity mAct, String user) {
		if (mAct == null || user == null)
			return false;
		
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
	
	
}
