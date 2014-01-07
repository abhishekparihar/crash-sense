package weboapps.crashsense;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class SendLogTask extends AsyncTask<String, String, String> {
	String[] strParams;
	Context _mContext;
	NetworkDetector mNetworkDetector;
	public SendLogTask(NetworkDetector mNetworkDetector,Context context) {
		this.mNetworkDetector=mNetworkDetector;	
		this._mContext=(MyApplication)context;
	}
 
	@Override
	protected String doInBackground(String... params) {
		WebService webService = new WebService("http://stage106.weboapps.com/api/v1/error_reports",_mContext,params);
		String response = webService.webPost(getJsonFromString(params));
		ResponseModel mResponseModel = setResponseModel(response);
		if(mResponseModel.isStatus())
		{
			Log.v("result : true",mResponseModel.getMessage());
			((MyApplication) _mContext).onResponseReceived();
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
            Log.i("json in web",json.toString());
        }catch (Exception uee) {
		}
        return json;
	}


	private ResponseModel setResponseModel(String response) {
		ResponseModel mResponseModel = null;
		try {
			JSONObject jsonObject= new JSONObject(response);
			mResponseModel= new ResponseModel();
			mResponseModel.setStatus(jsonObject.getBoolean("status"));
			mResponseModel.setMessage(jsonObject.getString("message"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return mResponseModel;
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
