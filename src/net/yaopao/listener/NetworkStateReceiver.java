package net.yaopao.listener;

import net.yaopao.assist.AutoLogin;
import net.yaopao.assist.Variables;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkStateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager connectMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobNetInfo = connectMgr
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifiNetInfo = connectMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
			Log.v("wy", "unconnect");
			Variables.network=0;
		} else {
			Log.v("wy", "connect");
			Variables.network=1;
			 if (Variables.uid!=0) {
		     	 new AutoLogin().execute("");
				}
		}
	}
}