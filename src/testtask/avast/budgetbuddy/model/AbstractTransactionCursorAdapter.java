package testtask.avast.budgetbuddy.model;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
 
public abstract class AbstractTransactionCursorAdapter extends AsyncTaskLoader<Cursor> {
	
	Cursor mCursor = null;
	abstract protected Cursor buildCursor();

	public AbstractTransactionCursorAdapter(Context context) {
		super(context);
	}

	/**
	 * Runs on a worker thread, loading in our data. Delegates the real work to
	 * concrete subclass' buildCursor() method.
	 */
	@Override
	public Cursor loadInBackground() {
		Cursor cursor = buildCursor();
		if (cursor != null)
			cursor.getCount();

		return (cursor);
	}

	/**
	 * Runs on the UI thread, routing the results from the background thread to
	 * whatever is using the Cursor (e.g., a CursorAdapter).
	 */
	@Override
	public void deliverResult(Cursor newCursor) {
		if ( isReset() ) {
			if (newCursor != null)
				newCursor.close();

			return;
		}

		Cursor oldCursor = mCursor;
		mCursor = newCursor;

		if ( isStarted() ) {
			super.deliverResult(newCursor);
		}

		if ( oldCursor != null && oldCursor != newCursor && !oldCursor.isClosed() ) {
			oldCursor.close();
		}
	}

	/**
	 * Starts an asynchronous load of the list data. When the result is ready
	 * the callbacks will be called on the UI thread. If a previous load has
	 * been completed and is still valid the result may be passed to the
	 * callbacks immediately.
	 * 
	 * Must be called from the UI thread.
	 */
	@Override
	protected void onStartLoading() {
		if ( mCursor != null ) {
			deliverResult(mCursor);
		}

		if ( takeContentChanged() || mCursor == null ) {
			forceLoad();
		}
	}

	/**
	 * Must be called from the UI thread, triggered by a call to stopLoading().
	 */
	@Override
	protected void onStopLoading() {
		// Attempt to cancel the current load task if possible.
		cancelLoad();
	}

	/**
	 * Must be called from the UI thread, triggered by a call to cancel(). Here,
	 * we make sure our Cursor is closed, if it still exists and is not already
	 * closed.
	 */
	@Override
	public void onCanceled(Cursor cursor) {
		if ( cursor != null && !cursor.isClosed() ) {
			cursor.close();
		}
	}

	/**
	 * Must be called from the UI thread, triggered by a call to reset(). Here,
	 * we make sure our Cursor is closed, if it still exists and is not already
	 * closed.
	 */
	@Override
	protected void onReset() {
		super.onReset();

		// Ensure the loader is stopped
		onStopLoading();

		if ( mCursor != null && !mCursor.isClosed() ) {
			mCursor.close();
		}

		mCursor = null;
	}
}