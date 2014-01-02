package weboapps.crashsense;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.myjson.Gson;
import com.google.myjson.JsonObject;

public class SendLogTask extends AsyncTask<String, String, String> {
	String[] strParams;
	ResponseModel mResponseModel;
	Context _mContext;
	NetworkDetector mNetworkDetector;
	public SendLogTask(NetworkDetector mNetworkDetector,Context context) {
		this.mNetworkDetector=mNetworkDetector;	
		this._mContext=(MyApplication)context;
	}

	@Override
	protected String doInBackground(String... params) {
		WebService webService = new WebService("http://10.0.1.109:3001/api/v1/error_reports");
		//
		String response = webService.webPost(getJsonFromString(params));	
		//
//		List<NameValuePair> nvParams = new ArrayList<NameValuePair>(7);
//		nvParams.add(new BasicNameValuePair("os", params[0])); 
//		nvParams.add(new BasicNameValuePair("os_version", params[1])); 
//		nvParams.add(new BasicNameValuePair("device_type", params[2])); 
//		nvParams.add(new BasicNameValuePair("error", params[3])); 
//		nvParams.add(new BasicNameValuePair("error_reason", params[4]));
//		nvParams.add(new BasicNameValuePair("error_description", params[5]));
//		nvParams.add(new BasicNameValuePair("api_key", params[6]));
//		String response = webService.webPost(nvParams);	
		cancel(true);
		mResponseModel = new Gson().fromJson(response, ResponseModel.class);
		if(mResponseModel.isStatus())
		{
			Log.v("result : true",mResponseModel.getMessage());
		}else{
			Log.v("result : false",mResponseModel.getMessage());
		}
		return null;
	}
	
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
        }catch (Exception uee) {
		}
        return json;
	}

	@Override
	protected void onPostExecute(String result) {
		 if(!mResponseModel.isStatus()){
			 Log.v("post",mResponseModel.getMessage());
		 }
	}
	@SuppressLint("NewApi")
	@Override
	protected void onCancelled(String result) {
		super.onCancelled(result);
		Log.v("cancel",null);
	}
}
