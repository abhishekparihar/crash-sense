package weboapps.crashsense;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream.GetField;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.text.format.DateFormat;

@SuppressLint("SimpleDateFormat")
public class MyApplication extends Application {

	private UncaughtExceptionHandler defaultUEH;
	private final static String TAG = GetField.class.getName();
	private final String strOs = "Android";
	private final String API_KEY = "111e3e86489477106e0160ec3c7eb54d";
	private final String strOsVersion = android.os.Build.VERSION.RELEASE;
	
	public MyApplication() {
		defaultUEH = Thread.getDefaultUncaughtExceptionHandler(); 
		Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
	} 
	
	@Override
	public void onCreate() {
		super.onCreate();
		LOG.v(TAG, "myapp");
	}

	private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {

		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			LOG.v("Crash Sense", ex.getMessage());
			LOG.v("Crash Sense localazied messgae", ex.getLocalizedMessage());
			
			StringWriter sw = new StringWriter(); 
			PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			LOG.v("Crash Sense", sw.toString());
			String[] params = new String[7];
			params[0] = strOs;
			params[1] =	strOsVersion ; 
			params[2] = getDeviceType();
			params[3] = ex.getClass().toString();
			params[4] = ex.getMessage();
			params[5] = sw.toString();
			params[6] = API_KEY;

			DataOperations dataOperations = new DataOperations(getApplicationContext());
			dataOperations.insertIntoTable(params);
			
			writeToFile(params);
			uploadCrashLogFromFile();
//			NetworkDetector mNetworkDetector= new NetworkDetector(getApplicationContext());
//			if(mNetworkDetector.isNetworkAvailable()){
//				LOG.e("network: available","make api call");
//				new SendLogTask(mNetworkDetector,getApplicationContext()).execute(params);
//			}else{
//				LOG.e("network: unavailable","writing to file");
//				writeToFile(params);
//				defaultUEH.uncaughtException(thread, ex);
//			}
		}

		private String getDeviceType() {
			StringBuilder mStringBuilder= new StringBuilder();
			mStringBuilder.append(android.os.Build.MANUFACTURER+" "+android.os.Build.MODEL);
			return mStringBuilder.toString();
		}
	};

	private void writeToFile(String[] params) {
		try {
			String strFileName=getFileName();  
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(strFileName, Context.MODE_PRIVATE));
			for (int i = 0; i < params.length; i++) {
				outputStreamWriter.write(params[i]+"\n---");
			}
			outputStreamWriter.close(); 
			OutputStreamWriter outputStreamWriterFileName = new OutputStreamWriter(openFileOutput("filename.txt", Context.MODE_APPEND));
			outputStreamWriterFileName.write(strFileName+"\n");
			outputStreamWriterFileName.close();
			LOG.i("File operation", "File write operation : Complete.");
		} catch (IOException e) {
			LOG.i("Exception", "File write failed: " + e.toString()); 
		}
	}
	
	private String getFileName() {
		SimpleDateFormat sdf= new SimpleDateFormat("EEEdMMMyyyy-HH:mm:ss");
		Date date = new Date();
		String strDate= sdf.format(date);
		strDate.trim();
		LOG.i("File Name: ",strDate);
		return strDate;
	}
	
	private void uploadCrashLogFromFile() {
		try {
			InputStream inputStream = openFileInput("filename.txt");
			if(inputStream!=null) {
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader= new BufferedReader(inputStreamReader);
				String strFilename;
				while((strFilename = bufferedReader.readLine())!= null) {
					LOG.v("filename",strFilename);
					String strFileContent=readFromFile(strFilename);
					String [] paramStrings= getStringArrayFromFileContent(strFileContent);
					NetworkDetector mNetworkDetector= new NetworkDetector(getApplicationContext());
					if(mNetworkDetector.isNetworkAvailable()){
						LOG.e("network: available","make api call");
						new SendLogTask(mNetworkDetector,getApplicationContext()).execute(paramStrings);
					}
					LOG.v("upload","upload complete");
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String readFromFile(String filename) {
		String ret = "";
		try {
			InputStream inputStream = openFileInput(filename);
			if (inputStream != null) {
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String receiveString = "";
				StringBuilder stringBuilder = new StringBuilder();
				while ((receiveString = bufferedReader.readLine()) != null) {
					stringBuilder.append(receiveString);
				}
				inputStream.close(); 
				ret = stringBuilder.toString();
			}
		} catch (FileNotFoundException e) {
			LOG.e("LOGin activity", "File not found: " + e.toString());
		} catch (IOException e) {
			LOG.e("LOGin activity", "Can not read file: " + e.toString());
		}
		return ret;
	}
	
	private String[] getStringArrayFromFileContent(String strFileContent) {
		String [] params= new String[7];
		params = strFileContent.split("---");
		return params;
	}

	
//	public static void registerUnsuccessfullResponce(String data) {
//		try {
//			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("errorReport.txt", Context.MODE_PRIVATE));
//			outputStreamWriter.write(data);
//			outputStreamWriter.append(data);
//			outputStreamWriter.close();
//		} catch (IOException e) {
//			LOG.i("Exception", "File write failed: " + e.toString());
//		}
//	}
}
