package com.heqifuhou.netbase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class MyNetUtil {
	// 是否有网
	public static boolean IsCanConnectNet(Context context) {
		ConnectivityManager cManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			return true;
		}
		return false;
	}

	// 打开wifi和GPRS设置
	public static void SetSetting(Activity activity, final int nResultID) {
		if (android.os.Build.VERSION.SDK_INT > 13) {
			// 3.2以上打开设置界面，也可以直接用ACTION_WIRELESS_SETTINGS打开到wifi界面
			activity.startActivity(new Intent(
					android.provider.Settings.ACTION_SETTINGS));
		} else {
			activity.startActivity(new Intent(
					android.provider.Settings.ACTION_WIRELESS_SETTINGS));
		}
		// try {
		// activity.startActivityForResult(new Intent(
		// android.provider.Settings.ACTION_SETTINGS), nResultID);
		// } catch (Exception e) {
		// try {
		// activity.startActivityForResult(new Intent(
		// android.provider.Settings.ACTION_WIFI_SETTINGS),
		// nResultID);
		// } catch (Exception e1) {
		//
		// }
		// }
	}

	public static void SetWifi(Activity activity, final int nResultID) {
		try {
			activity.startActivityForResult(new Intent(
					android.provider.Settings.ACTION_WIFI_SETTINGS), nResultID);
		} catch (Exception e) {

		}
	}

	public static void SetWireless(Activity activity, final int nResultID) {
		try {
			activity.startActivityForResult(new Intent(
					android.provider.Settings.ACTION_WIRELESS_SETTINGS),
					nResultID);
		} catch (Exception e) {

		}
	}

}
