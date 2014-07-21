package com.ranger.bmaterials.work;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.ranger.bmaterials.app.AppManager;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.broadcast.AppMonitorReceiver;
import com.ranger.bmaterials.broadcast.BroadcaseSender;
import com.ranger.bmaterials.mode.BaseAppInfo;
import com.ranger.bmaterials.tools.install.AppSilentInstaller;
import com.ranger.bmaterials.ui.UpdatableAppListFragment;

public abstract class AbstractListLoader<T /* extends BaseAppInfo */> extends
		AsyncTaskLoader<List<T>> {
	boolean DEBUG = true;
	private List<T> mApps;
	private PackageIntentReceiver<T> mPackageObserver;
	String MYTAG = this.getClass().getSimpleName();
	String PREFIX = this.getClass().getSimpleName();

	public AbstractListLoader(Context context) {
		super(context);
	}

	public abstract List<T> loadData();

	public abstract boolean isPackageIntentReceiver();

	/**
	 * This is where the bulk of our work is done. This function is called in a
	 * background thread and should generate a new set of data to be published
	 * by the loader.
	 */
	@Override
	public List<T> loadInBackground() {
		// Retrieve all known applications.
		List<T> entries = loadData();

		/*
		 * if (entries != null) { // Sort the list. Collections.sort(entries); }
		 */
		// Log.e("DownloadLog", "Loader loadInBackground"+((entries !=
		// null)?entries.size():null));
		if (DEBUG)
			Log.i(MYTAG,
					"##loadInBackground:"
							+ ((entries != null) ? entries.size() : null));

		if (DEBUG)
			Log.e(AppSilentInstaller.TAG, PREFIX + " [loadInBackground]"
					+ ((entries != null) ? entries.size() : 0));

		return entries;
	}

	/**
	 * Called when there is new data to deliver to the client. The super class
	 * will take care of delivering it; the implementation here just adds a
	 * little more logic.
	 */
	@Override
	public void deliverResult(List<T> apps) {
		// if(debug)Log.i(MYTAG, "##deliverResult ");
		if (DEBUG)
			Log.e(AppSilentInstaller.TAG, PREFIX + " deliverResult "
					+ ((apps != null) ? apps.size() : 0));
		if (isReset()) {
			if (DEBUG)
				Log.i(AppSilentInstaller.TAG, "deliverResult isReset");
			// An async query came in while the loader is stopped. We
			// don't need the result.
			if (apps != null) {
				onReleaseResources(apps);
			}
		}
		List<T> oldApps = apps;
		mApps = apps;
		if (isStarted()) {
			if (DEBUG)
				Log.i(AppSilentInstaller.TAG, PREFIX
						+ " deliverResult isStarted deliverResult " + apps);
			if (DEBUG)
				Log.i(MYTAG,
						"##deliverResult  isStarted immediately deliver its results");
			// If the Loader is currently started, we can immediately
			// deliver its results.
			super.deliverResult(apps);
		} else {
			if (DEBUG)
				Log.i(AppSilentInstaller.TAG, PREFIX
						+ " deliverResult not Started");
			if (DEBUG)
				Log.i(MYTAG, "##deliverResult not Started");
		}

		// At this point we can release the resources associated with
		// 'oldApps' if needed; now that the new result is delivered we
		// know that it is no longer in use.
		if (oldApps != null) {
			onReleaseResources(oldApps);
		}
	}

	/**
	 * Handles a request to start the Loader.
	 */
	@Override
	protected void onStartLoading() {
		boolean takeContentChanged = takeContentChanged();

		if (DEBUG)
			Log.i(AppSilentInstaller.TAG, PREFIX + " onStartLoading");
		if (DEBUG)
			Log.i(MYTAG, "##onStartLoading null?" + (mApps == null)
					+ " takeContentChanged?" + takeContentChanged);
		if (mApps != null) {
			// If we currently have a result available, deliver it
			// immediately.
			deliverResult(mApps);
			if (DEBUG)
				Log.i(AppSilentInstaller.TAG, PREFIX
						+ " [onStartLoading] mApps != null deliverResult ");
		} else {
			/*
			 * try { //ArrayList<T> arrayList = new ArrayList<T>();
			 * deliverResult(null); } catch (Exception e) { e.printStackTrace();
			 * }
			 */
			if (DEBUG)
				Log.i(AppSilentInstaller.TAG, PREFIX
						+ "onStartLoading mApps == null not deliverResult ");
		}

		// Start watching for changes in the app data.
		if (isPackageIntentReceiver() && mPackageObserver == null) {
			mPackageObserver = new PackageIntentReceiver<T>(this);
		}

		if (takeContentChanged || mApps == null) {
			// If the data has changed since the last time it was loaded
			// or is not currently available, start a load.
			if (DEBUG)
				Log.i(MYTAG, "##onStartLoading forceLoad ");
			if (DEBUG)
				Log.i(AppSilentInstaller.TAG, PREFIX + "takeContentChanged "
						+ takeContentChanged + "mApps == null ?"
						+ (mApps == null) + " forceLoad");
			forceLoad();
		} else {
			if (DEBUG)
				Log.i(MYTAG, "##onStartLoading not forceLoad ");
			if (DEBUG)
				Log.i(AppSilentInstaller.TAG, PREFIX + " not forceLoad");
		}
	}

	/**
	 * Handles a request to stop the Loader.
	 */
	@Override
	protected void onStopLoading() {
		if (DEBUG)
			Log.i(MYTAG, "##onStopLoading ");
		if (DEBUG)
			Log.i(AppSilentInstaller.TAG, PREFIX + "onStopLoading");
		// Attempt to cancel the current load task if possible.
		cancelLoad();
	}

	/**
	 * Handles a request to cancel a load.
	 */
	@Override
	public void onCanceled(List<T> apps) {
		super.onCanceled(apps);
		if (DEBUG)
			Log.i(AppSilentInstaller.TAG, PREFIX + "onCanceled");
		if (DEBUG)
			Log.i(MYTAG, "##onCanceled ");
		// At this point we can release the resources associated with 'apps'
		// if needed.
		onReleaseResources(apps);
	}

	/**
	 * Handles a request to completely reset the Loader.
	 */
	@Override
	protected void onReset() {
		super.onReset();
		if (DEBUG)
			Log.i(AppSilentInstaller.TAG, PREFIX + "onReset");
		if (DEBUG)
			Log.i(MYTAG, "##onReset ");
		// Ensure the loader is stopped
		onStopLoading();

		// At this point we can release the resources associated with 'apps'
		// if needed.
		if (mApps != null) {
			onReleaseResources(mApps);
			mApps = null;
		}

		// Stop monitoring for changes.
		if (mPackageObserver != null) {
			try {
				getContext().unregisterReceiver(mPackageObserver);
			} catch (Exception e) {
			}
			mPackageObserver = null;
		}
	}

	/**
	 * Helper function to take care of releasing resources associated with an
	 * actively loaded data set.
	 */
	protected void onReleaseResources(List<T> apps) {
		// For a simple List<> there is nothing to do. For something
		// like a Cursor, we would close it here.
	}

	/**
	 * Helper class to look for interesting changes to the installed apps so
	 * that the loader can be updated.
	 */
	public static class PackageIntentReceiver<T /* extends BaseAppInfo */>
			extends BroadcastReceiver {
		final AbstractListLoader<T> mLoader;

		public PackageIntentReceiver(AbstractListLoader<T> loader) {
			mLoader = loader;
			IntentFilter filter = new IntentFilter(
					BroadcaseSender.ACTION_PACKAGE_ADDED);
			filter.addAction(BroadcaseSender.ACTION_PACKAGE_REMOVED);
			filter.addAction(BroadcaseSender.ACTION_PACKAGE_REPLACED);
			/*
			 * IntentFilter filter = new
			 * IntentFilter(Intent.ACTION_PACKAGE_ADDED);
			 * filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
			 * filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
			 */filter.addDataScheme("package");
			mLoader.getContext().registerReceiver(this, filter);

			// Register for events related to sdcard installation.
			IntentFilter sdFilter = new IntentFilter();
			sdFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
			sdFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
			mLoader.getContext().registerReceiver(this, sdFilter);

			IntentFilter ownFilter = new IntentFilter();
			ownFilter
					.addAction(BroadcaseSender.ACTION_INSTALLED_LIST_INITIALIZED);
			ownFilter
					.addAction(BroadcaseSender.ACTION_UPDATABLE_LIST_INITIALIZED);
			ownFilter.addAction(BroadcaseSender.ACTION_WHITELIST_INITIALIZED);
			mLoader.getContext().registerReceiver(this, ownFilter);

			IntentFilter downloadFilter = new IntentFilter();
			downloadFilter.addAction(BroadcaseSender.ACTION_DOWNLOAD_CHANGED);
			mLoader.getContext().registerReceiver(this, downloadFilter);

			IntentFilter installFilter = new IntentFilter();
			installFilter.addAction(BroadcaseSender.ACTION_INSTALL_CHANGED);
			mLoader.getContext().registerReceiver(this, installFilter);

			IntentFilter ignoredFilter = new IntentFilter(
					BroadcaseSender.ACTION_IGNORED_STATE_CHANGED);
			mLoader.getContext().registerReceiver(this, ignoredFilter);
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			// Tell the loader about the change.
			String action = intent.getAction();
			if (mLoader.DEBUG)
				Log.i("PackageIntentReceiver", "##onReceive " + action);

			if (BroadcaseSender.ACTION_PACKAGE_ADDED.equals(action)
			/* ||AppMonitorReceiver.ACTION_PACKAGE_REPLACED.equals(action) */) {
				mLoader.onReceveAppEvent(true);
			} else if (BroadcaseSender.ACTION_PACKAGE_REMOVED.equals(action)) {
				mLoader.onReceveAppEvent(false);
			} else if (BroadcaseSender.ACTION_INSTALLED_LIST_INITIALIZED
					.equals(action)
					|| BroadcaseSender.ACTION_WHITELIST_INITIALIZED
							.equals(action)) {
				mLoader.onInstalledListInitialized();
			} else if (BroadcaseSender.ACTION_UPDATABLE_LIST_INITIALIZED
					.equals(action)) {
				mLoader.onUpdatableListInitialized();
			} else if (BroadcaseSender.ACTION_DOWNLOAD_CHANGED.equals(action)) {
				boolean downloadOrOtherwise = intent.getBooleanExtra(
						BroadcaseSender.DOWNLOAD_CHANGED_ARG, false);
				mLoader.onDownloadChanged(downloadOrOtherwise);
			} else if (BroadcaseSender.ACTION_INSTALL_CHANGED.equals(action)) {
				mLoader.onInstallChanged();
			} else if (BroadcaseSender.ACTION_IGNORED_STATE_CHANGED
					.equals(action)) {
				boolean status = intent.getBooleanExtra(
						BroadcaseSender.ARG_IGNORED_STATE, false);
				String[] packages = intent
						.getStringArrayExtra(BroadcaseSender.ARG_IGNORED_STATE_CHANGED_PACKAGES);
				mLoader.onIgnoredStatusChanged(status, packages);
			}
		}
	}

	protected void onIgnoredStatusChanged(boolean ignored,
			String... packageNames) {
		// Log.i("UpdatableAppListFragment",
		// "OnAppStatusChangedListener onIgnoredStatusChanged");
		/*
		 * if(!ignored && getActivity() != null){
		 * getLoaderManager().restartLoader(0,null,
		 * UpdatableAppListFragment.this); //handler.sendEmptyMessage(100); }
		 */

	}

	protected void onReceveAppEvent(boolean addOrRemove) {
		// mLoader.onContentChanged();
		// forceLoad();
	}

	protected void onUpdatableListInitialized() {
		if (Constants.DEBUG)
			Log.i("Refresh", "Loader onUpdatableListInitialized");
	}

	protected void onInstalledListInitialized() {
		if (Constants.DEBUG)
			Log.i("Refresh", "Loader onInstalledIistInitialized");
	}

	protected void onDownloadChanged(boolean downloadOrOtherWise) {
		if (Constants.DEBUG)
			Log.i("DownloadLog", "Loader onDownloadChanged");
	}

	protected void onInstallChanged() {
		if (Constants.DEBUG)
			Log.e(AppSilentInstaller.TAG, "Loader onInstallChanged");
	}

	/*
	 * protected void checkInstalledGames(){ AppMananager manager =
	 * AppMananager.getInstance(getContext()); if(!manager.isWhiteListInited()){
	 * manager.initWhiteList(); } if(!manager.isInstalledListInited()){
	 * manager.loadAndSaveInstalledApps(); } }
	 */

}