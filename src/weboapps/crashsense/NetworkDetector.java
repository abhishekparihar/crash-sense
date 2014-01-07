package weboapps.crashsense;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author webonise
 *	This class is to initialize all the network object to check if the network is 
 *	available or not using function "isNetworkAvailable()".
 */
public class NetworkDetector {
	private Context _context;
	
	/**
	 * @param mContext is context
	 * 	Constructor to get the context of the application and use it in this class.
	 */
	public NetworkDetector(Context mContext){
		this._context= mContext;
	}
	
	/**
	 * @return true if the network is available in the device.
	 * 			false if the network is not available in the device.
	 */
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
