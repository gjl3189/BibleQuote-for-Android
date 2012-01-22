package com.BibleQuote.managers;

import com.BibleQuote.exceptions.OpenModuleException;
import com.BibleQuote.models.Module;
import com.BibleQuote.utils.Log;
import com.BibleQuote.utils.Task;

public class AsyncOpenModule extends Task {
	private final String TAG = "AsyncTaskChapterOpen";
	
	private Librarian librarian;
	private Module nextClosedModule = null;
	private Boolean isReload = false;
	private Exception exception;
	private Boolean isSuccess;
	
	public AsyncOpenModule(String message, Boolean isHidden, Librarian librarian, Boolean isReload) {
		super(message, isHidden);
		this.librarian = librarian;
		this.isReload = isReload;
	}

	
	@Override
	protected Boolean doInBackground(String... arg0) {
		isSuccess = false;
		if (isReload) {
			librarian.loadModules();
			isReload = false;
		}
		Module module = librarian.getClosedModule();
		if (module != null) {
			Log.i(TAG, String.format("Open module with moduleID=%1$s", module.getID()));
		try {
			module = librarian.openModule(module.getID(), module.getDataSourceID());
		} catch (OpenModuleException e) {
			module.setIsClosed(true);
			exception = e;
		}
			nextClosedModule = librarian.getClosedModule();
		}
		isSuccess = true;
		return true;
	}
	
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
	}

	public Exception getException() {
		return exception;
	}

	public Boolean isSuccess() {
		return isSuccess;
	}
	
	public Module getNextClosedModule() {
		return nextClosedModule;
	}
}
