package weboapps.crashsense;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author webonise
 *	This is the class extended from the AsyncTask which is used to run the web service 
 *	process in the background. This class makes a web service request and passes a 
 *	string value along with the request. The received response which is in json format
 *	parsed and value is added to the model. On receiving success response the control is 
 *	redirected to the application.
 */
public class SendLogTask extends AsyncTask<String, String, String> {
	
	private final String TAG =getClass().getName();
	String[] strParams;
	Context _mContext;
	
	/**
	 * @param context context of the application.
	 *  Constructor to get the context of the application.
	 */
	public SendLogTask(Context context) {
		this._mContext=(MyApplication)context;
	}
 
	@Override
	protected String doInBackground(String... params) {
		WebService webService = new WebService(Constants.BASE_URL+"/api/v1/error_reports",_mContext,params);
		String response = webService.webPost(getJsonFromString(params));
		try {
			JSONObject jsonObject= new JSONObject(response);
			if(jsonObject.getBoolean("status"))
			{
				Log.i(TAG,"Response: received successfully.");
				((MyApplication) _mContext).onResponseReceived();
			}else{
				Log.i(TAG,"Response: not received.");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param params array of string having the device details and crash log details.
	 * @return a json object having the same detail in string array format. 
	 */
	private JSONObject getJsonFromString(String[] params) {
		JSONObject json = new JSONObject();
		JSONObject jsonError = new JSONObject();
        try {
        	jsonError.put("os", params[0]);
        	jsonError.put("os_version", params[1]);
        	jsonError.put("device_type", params[2]);
        	jsonError.put("error", params[3]);
        	jsonError.put("error_reason", params[4]);
        	jsonError.put("error_decsription", params[5]);
            json.put("api_key", params[6]);
            json.put("error", jsonError);
            Log.i(TAG,"Sending prameters: parameters converted to json.");
        }catch (Exception uee) {
		}
        return json;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
	}
	
	@SuppressLint("NewApi")
	@Override
	protected void onCancelled(String result) {
		super.onCancelled(result);
		Log.v("cancel",null);
	}
}
