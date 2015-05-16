package testtask.avast.budgetbuddy.controller;

import android.os.AsyncTask;
import android.support.v4.content.Loader;

public abstract class ContentChangeTask<T1, T2, T3> extends AsyncTask<T1, T2, T3> {
	
	private Loader<?> mLoader = null;

	protected ContentChangeTask(Loader<?> loader) {
		mLoader = loader;
	}

	@Override
	protected void onPostExecute(T3 param) {
		mLoader.onContentChanged();
	}
	
}
