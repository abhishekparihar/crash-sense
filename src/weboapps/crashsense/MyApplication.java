package weboapps.crashsense;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class MyApplication extends Application {

	private UncaughtExceptionHandler defaultUEH;
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
	}

	private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {

		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			StringWriter sw = new StringWriter(); 
			PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			String[] params = new String[7];
			params[0] = Constants.OS;
			params[1] =	Constants.OS_VERSION ; 
			params[2] = getDeviceType();
			params[3] = ex.getClass().toString();  
			params[4] = ex.getMessage();
			params[5] = sw.toString(); 
			params[6] = Constants.API_KEY;

			mNetworkDetector= new NetworkDetector(getApplicationContext()); 
			if(mNetworkDetector.isNetworkAvailable()){
				Log.e("network: available","uploading file");
				new SendLogTask(mNetworkDetector,getApplicationContext()).execute(params);
			}else{ 
				Log.e("network: unavailable","writing to file");
				writeToFile(params); 
			}
			defaultUEH.uncaughtException(thread, ex);
		}
	};

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
			OutputStreamWriter outputStreamWriterFileName = new OutputStreamWriter(openFileOutput("filename.txt", Context.MODE_APPEND));
			outputStreamWriter.close(); 
			outputStreamWriterFileName.write(strFileName+"\n");
			outputStreamWriterFileName.close();
			Log.i("File operation", "File write operation : Complete."); 
		} catch (IOException e) {
			Log.i("Exception", "File write failed: " + e.toString()); 
		} 
	}
	
	private String getFileName() {
		SimpleDateFormat sdf= new SimpleDateFormat("EEEdMMMyyyy-HH:mm:ss");  
		Date date = new Date();
		String strDate= sdf.format(date); 
		strDate.trim();
		Log.i("File Name: ",strDate);
		return strDate;
	}
	
	private void uploadCrashLogFromFile() { 
		try {
			inputStream = openFileInput("filename.txt");
			if(inputStream!=null) {
				inputStreamReader = new InputStreamReader(inputStream);  
				bufferedReader= new BufferedReader(inputStreamReader); 
				if((strFilename = bufferedReader.readLine())!= null) {
					Constants.IS_UPLOAD_FROM_FILE=true;
					String strFileContent=getFileContent(strFilename);
					String [] paramStrings= getStringArrayFromFileContent(strFileContent);
					new SendLogTask(mNetworkDetector,getApplicationContext()).execute(paramStrings);
				}
				else {
					Constants.IS_UPLOAD_FROM_FILE = false;
				}
			}
			else{
				Log.i("No file", "No file to upload");
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void onResponseReceived() {
		Log.e("response","upload complete");
		if(Constants.IS_UPLOAD_FROM_FILE) { 
			clearFileRecord();
		}
		uploadCrashLogFromFile();
	}
	
	private void clearFileRecord() {
		Log.v("file record","cleaning file record, file name is : "+strFilename);  
		File file = new File(Constants.FILE_URL+strFilename);
		boolean deleted = file.delete();
		if(deleted){
			Log.i("file deleted: ","true"); 
			deleteFileEntryFromFilename(Constants.FILE_URL+"filename.txt", strFilename);
		}else {
			Log.i("file deleted: ","false");
		}
	}
	
	public void deleteFileEntryFromFilename(String file, String lineToRemove) {
		try {
		  File inFile = new File(file);
		  if (!inFile.isFile()) {
		    Log.i("delete file entry","Parameter is not an existing file");
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
		    Log.i("delete file entry","Could not delete file");
		    return;
		  }
		  if (!tempFile.renameTo(inFile))
		    Log.i("delete file entry","Could not rename file");
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
			Log.e("Login activity", "File not found: " + e.toString());
		} catch (IOException e) {
			Log.e("Login activity", "Can not read file: " + e.toString());
		}
		return ret;
	}
	
	private String[] getStringArrayFromFileContent(String strFileContent) {
		String [] params= new String[7];
		params = strFileContent.split("---");
		Log.i("api_key" ,":"+params[6]+":");
		return params;
	}
}
