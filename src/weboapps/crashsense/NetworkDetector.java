package weboapps.crashsense;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkDetector {
	private Context _context;
	public NetworkDetector(Context mContext){
		this._context= mContext;
	}
	
	public boolean isNetworkAvailable(){
		ConnectivityManager mConnectivityManager = (ConnectivityManager)_context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(mConnectivityManager!=null){
			NetworkInfo [] mNetworkInfos=mConnectivityManager.getAllNetworkInfo();
			if(mNetworkInfos!=null){
				for (int i = 0; i < mNetworkInfos.length; i++) {
					if(mNetworkInfos[i].getState()==NetworkInfo.State.CONNECTED){
						return true;
					}
				}
			}
		}
		return false;
	}
	
}
