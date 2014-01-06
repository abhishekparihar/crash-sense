package weboapps.crashsense;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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
import java.util.Scanner;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.text.format.DateFormat;

@SuppressLint("SimpleDateFormat")
public class MyApplication extends Application {

	private UncaughtExceptionHandler defaultUEH;
	private final static String TAG = GetField.class.getName();
	private final String strOs = "Android";
	private final String API_KEY = "2f418511c853c7c0b1d168d94f97ab2c";
	private final String FILE_URL = "/data/data/weboapps.crashsense/files/";
	private final String strOsVersion = android.os.Build.VERSION.RELEASE;
	private static boolean IS_UPLOAD_FROM_FILE = false;
	OutputStreamWriter outputStreamWriterFileName;
	private NetworkDetector mNetworkDetector;
	private String strFilename;
	private FileInputStream inputStream;
	private InputStreamReader inputStreamReader;
	private BufferedReader bufferedReader;
	
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

			try { 
				outputStreamWriterFileName = new OutputStreamWriter(openFileOutput("filename.txt", Context.MODE_APPEND));
			} catch (FileNotFoundException e) { 
				e.printStackTrace();
			}
			mNetworkDetector= new NetworkDetector(getApplicationContext()); 
			if(mNetworkDetector.isNetworkAvailable()){
				uploadCrashLog(params);
			}else{ 
				LOG.e("network: unavailable","writing to file");
				writeToFile(params); 
			}
			defaultUEH.uncaughtException(thread, ex);
		}
	};

	private void uploadCrashLog(String[] params) {
		LOG.e("uploading","uploading error");  
		new SendLogTask(mNetworkDetector,getApplicationContext()).execute(params);			
	}

	private String getDeviceType() { 
		StringBuilder mStringBuilder= new StringBuilder();
		mStringBuilder.append(android.os.Build.MANUFACTURER+" "+android.os.Build.MODEL);
		return mStringBuilder.toString();
	}

	private void writeToFile(String[] params) {  
		try {
			String strFileName=getFileName();  
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(strFileName, Context.MODE_PRIVATE));
			for (int i = 0; i < params.length; i++) {
				outputStreamWriter.write(params[i]+"\n---");
			}
			outputStreamWriter.close(); 
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
			inputStream = openFileInput("filename.txt");
			if(inputStream!=null) {
				inputStreamReader = new InputStreamReader(inputStream);  
				bufferedReader= new BufferedReader(inputStreamReader); 
				if((strFilename = bufferedReader.readLine())!= null) {
					IS_UPLOAD_FROM_FILE=true;
					String strFileContent=getFileContent(strFilename);
					String [] paramStrings= getStringArrayFromFileContent(strFileContent);
					new SendLogTask(mNetworkDetector,getApplicationContext()).execute(paramStrings);
				}
				else {
					IS_UPLOAD_FROM_FILE = false;
				}
			}
			else{
				LOG.i("No file", "No file to upload");
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void onResponseReceived() {
		LOG.e("response","upload complete");
		if(IS_UPLOAD_FROM_FILE) { 
			clearFileRecord();
		}
		uploadCrashLogFromFile();
	}
	
	private void clearFileRecord() {
		LOG.v("file record","cleaning file record, file name is : "+strFilename);  
		File file = new File(FILE_URL+strFilename);
		boolean deleted = file.delete();
		if(deleted){
			LOG.i("file deleted: ","true"); 
			deleteFileEntryFromFilename(FILE_URL+"filename.txt", strFilename);
		}else {
			LOG.i("file deleted: ","false");
		}
	}
	
	public void deleteFileEntryFromFilename(String file, String lineToRemove) {
		try {
		  File inFile = new File(file);
		  if (!inFile.isFile()) {
		    LOG.i("delete file entry","Parameter is not an existing file");
		    return;
		  }
		  File tempFile = new File(inFile.getAbsolutePath() + ".tmp");
		  BufferedReader br = new BufferedReader(new FileReader(file));
		  PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
		  String line = null;
		  while ((line = br.readLine()) != null) {
		    if (!line.trim().equals(lineToRemove)) {
		      pw.println(line);
		      pw.flush();
		    }
		  }
		  pw.close();
		  br.close();
		  if (!inFile.delete()) {
		    LOG.i("delete file entry","Could not delete file");
		    return;
		  }
		  if (!tempFile.renameTo(inFile))
		    LOG.i("delete file entry","Could not rename file");
		}
		catch (FileNotFoundException ex) {
		  ex.printStackTrace();
		}
		catch (IOException ex) {
		  ex.printStackTrace();
		}
	}
	
	private String getFileContent(String filename) {
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
		LOG.i("api_key" ,":"+params[6]+":");
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
