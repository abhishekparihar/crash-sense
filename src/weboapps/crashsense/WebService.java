package weboapps.crashsense;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class WebService {
	
	private final String TAG =getClass().getName();
	private DefaultHttpClient httpClient;
	private HttpContext localContext;
	private String ret;
	private HttpResponse response = null;
	private HttpPost httpPost = null;
	private String webServiceUrl;
	Context _mContext;
	String [] params;

	public WebService(String serviceName, Context _mContext, String[] params) {
		HttpParams myParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(myParams, 15000);
		HttpConnectionParams.setSoTimeout(myParams, 15000);
		httpClient = new DefaultHttpClient(myParams);
		localContext = new BasicHttpContext();
		webServiceUrl = serviceName;
		Log.i(TAG,"Web service url: "+webServiceUrl);
		this._mContext = _mContext;
		this.params = params;
	} 

	public String webInvoke(String methodName, Map<String, Object> params) {
		JSONObject jsonObject = new JSONObject();
		for (Map.Entry<String, Object> param : params.entrySet()) {
			try {
				jsonObject.put(param.getKey(), param.getValue());
			} catch (JSONException e) {
			}
		}
		return webInvoke(methodName, jsonObject.toString(), "json");
	}

	private String webInvoke(String methodName, String data, String contentType) {
		ret = null;
		httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY,CookiePolicy.RFC_2109);
		httpPost = new HttpPost(webServiceUrl + methodName);
		response = null;
		StringEntity tmp = null;
		httpPost.setHeader("Content-type", "application/json");
		if (contentType != null) {
			httpPost.setHeader("Content-Type", contentType);
		} else {
			httpPost.setHeader("Content-Type","application/x-www-form-urlencoded");
		}
		try {
			tmp = new StringEntity(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		httpPost.setEntity(tmp);
		try {
			response = httpClient.execute(httpPost, localContext);
			if (response != null) {
				ret = EntityUtils.toString(response.getEntity());
			}
		} catch (Exception e) {
		}
		return ret;
	}

	public String webPost(JSONObject jsonObject) {
		String postUrl = webServiceUrl;
		httpPost = new HttpPost(postUrl);
		StringEntity se;
		try {
			se = new StringEntity(jsonObject.toString());
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
			httpPost.setEntity(se);
			response = httpClient.execute(httpPost);
			ret = EntityUtils.toString(response.getEntity());
			Log.i(TAG, "Response received is: " + ret);
		} catch (UnsupportedEncodingException e) {
			
		} catch (ClientProtocolException e) {
			
		} catch (SocketTimeoutException e) {
			
			/*In case if the connection is slow then and connection time out occurs
			then you need to wirte the data in the file .*/
			
			Log.i(TAG,"Connection problem: Connection timed out" );
			((MyApplication) _mContext).writeToFile(params);
		} catch (ConnectTimeoutException e) {
			Log.i(TAG,"Connection problem: Connection timed out" );
		} catch (IOException e) {
		}

		return ret;
	}

	public InputStream postHttpStream(String urlString) throws IOException {
		InputStream in = null;
		int response = -1;
		URL url = new URL(urlString);
		URLConnection conn = url.openConnection();
		if (!(conn instanceof HttpURLConnection)) throw new IOException("Not an HTTP connection");
		try {
			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setAllowUserInteraction(false);
			httpConn.setInstanceFollowRedirects(true);
			httpConn.setRequestMethod("POST");
			httpConn.connect();
			response = httpConn.getResponseCode();
			if (response == HttpURLConnection.HTTP_OK) {
				in = httpConn.getInputStream();
			}
		} catch (Exception e) {
			throw new IOException("Error connecting");
		}
		return in;
	}

	public void clearCookies() {
		httpClient.getCookieStore().clear();
	}

	public void abort() {
		try {
			if (httpClient != null) {
				httpPost.abort();
			}
		} catch (Exception e) {
		}
	}

	public String webGET(String methodName, List<NameValuePair> nameValuePairs) {
		String result = null;
		if (nameValuePairs != null && !nameValuePairs.isEmpty()) {
			methodName += "?";
			Iterator<NameValuePair> iter = nameValuePairs.iterator();
			while (iter.hasNext()) {
				NameValuePair nvp = iter.next();
				methodName += nvp.getName();
				methodName += "=";
				methodName += nvp.getValue();
				if (iter.hasNext())
					methodName += "&";
			}
		}

		HttpGet httpGet = new HttpGet(methodName);
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		httpGet.setHeader("accept", "text/mobile");
		HttpClient httpclient = new DefaultHttpClient();
		try {
			result = httpclient.execute(httpGet, responseHandler);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public HttpResponse webGetHeader(String methodName,
			List<NameValuePair> params) {
		String postUrl = webServiceUrl + methodName;
		httpPost = new HttpPost(postUrl);
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params));
		} catch (UnsupportedEncodingException uee) {
		}
		try {
			response = httpClient.execute(httpPost);
		} catch (Exception e) {
			if (e.getMessage() != null) {
			} else {
			}
		}
		return response;
	}

	public String getResponseString(HttpResponse response) {
		try {
			ret = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			if (e.getMessage() != null) {
			} else {
			}
		}
		return ret;
	}
}
