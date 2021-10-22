package de.jeisfeld.breathtraining;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import java.util.Locale;

/**
 * Utility class to retrieve base application resources.
 */
public class Application extends android.app.Application {
	/**
	 * A utility field to store a context statically.
	 */
	@SuppressLint("StaticFieldLeak")
	private static Context mContext;
	/**
	 * The default tag for logging.
	 */
	public static final String TAG = "BreathTraining.JE";
	/**
	 * The default locale.
	 */
	@SuppressLint("ConstantLocale")
	private static final Locale DEFAULT_LOCALE = Locale.getDefault();

	@Override
	public final void onCreate() {
		super.onCreate();
		Application.mContext = getApplicationContext();
	}

	/**
	 * Retrieve the application context.
	 *
	 * @return The (statically stored) application context
	 */
	public static Context getAppContext() {
		return Application.mContext;
	}

	/**
	 * Get a resource string.
	 *
	 * @param resourceId the id of the resource.
	 * @param args       arguments for the formatting
	 * @return the value of the String resource.
	 */
	public static String getResourceString(final int resourceId, final Object... args) {
		return Application.getAppContext().getResources().getString(resourceId, args);
	}

	/**
	 * Retrieve the version number of the app.
	 *
	 * @return the app version.
	 */
	public static int getVersion() {
		PackageInfo pInfo;
		try {
			pInfo = Application.getAppContext().getPackageManager().getPackageInfo(Application.getAppContext().getPackageName(), 0);
			return pInfo.versionCode;
		}
		catch (NameNotFoundException e) {
			Log.e(Application.TAG, "Did not find application version", e);
			return 0;
		}
	}
}
