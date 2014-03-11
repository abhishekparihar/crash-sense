Project 106
==========
<br/>Here is the instruction to use this project in any other application to get the crash report generated when application crashes.
<br/>
<br/>From the project you need to create new jar file (in the bin folder in the application ) , if it dosen't exists . 
<br/>For that you have to follow these instructions.
<br/>
<br/>**STEP 1:** Import the project in eclipse.
<br/>**STEP 1:** Right click on the project and go to PORPERTIES then ANDROID.
<br/>**STEP 1:** Check IS LIBRARY field at the bottom.
<br/>**STEP 1:** Run the peoject, you will have the jar file generated in the bin folder in your applcation.
<br/>**STEP 1:** Copy the jar file and save it in some dierectory.
<br/>
<br/>Now to use the jar file in any other project you have follow these instructions.
<br/>
<br/>**STEP 1:** Create a new android application.
**STEP 2:** To use the jar file , first download the file “Project106_internal.jar”<br/>
**STEP 3:** Copy and paste the jar file in the libs folder of your project.<br/>
**STEP 4:** In your application create new class (name could be anything) in my case it’s “DemoApplication”.<br/>
**STEP 5:** Extend the class from MyApplication.<br/>
**STEP 6:** You need to mention the application name “DemoApplication” in the AndroidManifest file so that your application can acknowledge your application class.<br/>
**STEP 7:** You need to register at the server side and get the API key.<br/>
<br/>
	**Your code will look like this . (DemoApplication.java)**<br/>
<br/>
		public class DemoApplication extends MyApplication{

			public static final String API_KEY = "744cc8b6b27720e1be3fec9a5fb0a024";

			@Override
			protected String getKey() {
				return API_KEY;
			}
		}
<br/>
	**Your code will look like this . (AndroidManifest.xml)**<br/>
<br/>
		<application
<<<<<<< HEAD
			android:name=".DemoApplication"
			android:allowBackup="true"
			...
			android:theme="@style/AppTheme" >
			<activity
				…...
			</activity>
		</application>
=======
        android:name=".DemoApplication"
        android:allowBackup="true"
        ...
        android:theme="@style/AppTheme" >
        <activity
          …...
        </activity>
    </application>
>>>>>>> bf9d87581e323eaf8da26f82f26daaecef6f639d



