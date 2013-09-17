package weboapps.crashsense;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class MyApplication extends Application {
	
	 private UncaughtExceptionHandler defaultUEH;
	 
	 @Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	 
	 private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
		
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			Log.v("Crash Sense", ex.getMessage());
			Log.v("Crash Sense localazied messgae", ex.getLocalizedMessage());
			//Log.v("Crash Sense stack trace", ex.getStackTrace().);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			Log.v("Crash Sense", sw.toString());
			
			String[] params = new String[3];
			params[0] = ex.getClass().toString();
			params[1] = ex.getMessage();
			params[2] = sw.toString();
			new SendLogTask().execute(params);
			writeToFile(sw.toString());
			defaultUEH.uncaughtException(thread, ex);
			
		}
	};
	
	public MyApplication() {
        defaultUEH = Thread.getDefaultUncaughtExceptionHandler();

        // setup handler for uncaught exception 
        Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
    }
	
	private void writeToFile(String data) {
	    try {
	        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("errorReport.txt", Context.MODE_PRIVATE));
	        outputStreamWriter.write(data);
	        outputStreamWriter.append(data);
	        outputStreamWriter.close();
	    }
	    catch (IOException e) {
	        Log.e("Exception", "File write failed: " + e.toString());
	    } 
	}
	
	private String readFromFile() {

	    String ret = "";

	    try {
	        InputStream inputStream = openFileInput("errorReport.txt");

	        if ( inputStream != null ) {
	            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
	            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	            String receiveString = "";
	            StringBuilder stringBuilder = new StringBuilder();

	            while ( (receiveString = bufferedReader.readLine()) != null ) {
	                stringBuilder.append(receiveString);
	            }

	            inputStream.close();
	            ret = stringBuilder.toString();
	        }
	    }
	    catch (FileNotFoundException e) {
	        Log.e("login activity", "File not found: " + e.toString());
	    } catch (IOException e) {
	        Log.e("login activity", "Can not read file: " + e.toString());
	    }

	    return ret;
	}
}
