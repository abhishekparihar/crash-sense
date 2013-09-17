package weboapps.crashsense;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;

public class SendLogTask extends AsyncTask<String, String, String> {
	String[] strParams;
	public SendLogTask() {
	}

	@Override
	protected String doInBackground(String... params) {
		WebService webService = new WebService("http://192.168.10.148:3001/error_requests");
		List<NameValuePair> nvParams = new ArrayList<NameValuePair>(3);
		nvParams.add(new BasicNameValuePair("error_controller", params[0]));
		nvParams.add(new BasicNameValuePair("error", params[1]));
		nvParams.add(new BasicNameValuePair("error_info", params[2]));
		String response = webService.webPost(nvParams);
		return response;
	}
	
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}

}
