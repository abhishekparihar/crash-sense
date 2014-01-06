package weboapps.crashsense;

import android.util.Log;

/**
 * @author webonise
 *	This class uses the functionality of the Log to show messages 
 *	throughout the application. Debug mode is added to control enabling or 
 *	disabling the log entries throughout the application.  
 */
public class LOG {

	public static boolean DEBUG_MODE = true;

	public static void d(final String tag, String message) {
		if (DEBUG_MODE) {
			Log.d(tag, message);
		}
	}

	public static void v(final String tag, String message) {
		if (DEBUG_MODE) {
			Log.v(tag, message);
		}
	}

	public static void i(final String tag, String message) {
		if (DEBUG_MODE) {
			Log.i(tag, message);
		}
	}

	public static void w(final String tag, String message) {
		if (DEBUG_MODE) {
			Log.w(tag, message);
		}
	}

	public static void e(final String tag, String message) {
		if (DEBUG_MODE) {
			Log.e(tag, message);
		}
	}

}
