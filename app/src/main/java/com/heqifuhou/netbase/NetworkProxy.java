package com.heqifuhou.netbase;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkProxy {
	public static String getProxy(Context paramContext) {
		try {
			ConnectivityManager localConnectivityManager = (ConnectivityManager) paramContext
					.getSystemService("connectivity");
			NetworkInfo localNetworkInfo = localConnectivityManager
					.getActiveNetworkInfo();
			if (localNetworkInfo == null)
				return null;
			if (localNetworkInfo.getType() == 1)
				return null;
			String str = localNetworkInfo.getExtraInfo();
			if (str.equals("cmnet"))
				return null;
			if (str.equals("cmwap"))
				return "10.0.0.172";
			if (str.equals("3gwap"))
				return "10.0.0.172";
			if (str.equals("3gnet"))
				return null;
			if (str.equals("uninet"))
				return null;
			if (str.equals("uniwap"))
				return null;
			if (str.equals("ctwap"))
				return "10.0.0.200";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
