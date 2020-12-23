package com.heqifuhou.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import cn.com.phinfo.oaact.MyApplet;

public class MyDeviceBaseUtils {
	static public String getCurrAppVer(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			if (pi.versionName == null || pi.versionName.length() <= 0) {
				return "";
			}
			return pi.versionName;
		} catch (Exception e) {

		}
		return "";
	}

	static public int getCurrAppCode(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			return pi.versionCode;
		} catch (Exception e) {

		}
		return 0;
	}

	// ȡ����MAC
	static public String getLocalMacAddress(Context context) {
		try {
			WifiManager wifi = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();
			return info.getMacAddress();
		} catch (Exception e) {

		}
		return "";

	}

	static public String GetDeviceIMEI(Context activity) {
		try {
			TelephonyManager tm = (TelephonyManager) activity
					.getSystemService(Context.TELEPHONY_SERVICE);
			return tm.getDeviceId();
		} catch (Exception e) {

		}
		return "";
	}

	static public String GetDeviceIMSI(Context context) {
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			return tm.getSubscriberId();
		} catch (Exception e) {

		}
		return "";
	}

	static public String getDeviceICCID(Context activity) {
		try {
			TelephonyManager tm = (TelephonyManager) activity
					.getSystemService(Context.TELEPHONY_SERVICE);
			return tm.getSimSerialNumber();
		} catch (Exception e) {

		}
		return "";
	}

	static public String getAndroidID(Context activity) {
		try {
			return android.provider.Settings.Secure.getString(
					activity.getContentResolver(),
					android.provider.Settings.Secure.ANDROID_ID);
		} catch (Exception e) {
		}
		return "";
	}

	public static String GetDevicePhoneNumber(Context activity) {
		TelephonyManager tm = (TelephonyManager) activity
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (tm != null) {
			return tm.getLine1Number();
		}
		return null;
	}

	// ȡ������С
	public static final int getScreenW() {
		DisplayMetrics dm = MyApplet.getInstance().getResources()
				.getDisplayMetrics();
		return dm.widthPixels;
	}

	public static final int getScreenH() {
		DisplayMetrics dm = MyApplet.getInstance().getResources()
				.getDisplayMetrics();
		return dm.heightPixels;
	}

	// ϵͳ���?��
	static public void StartShareApp(Context context,
			final String szChooserTitle, final String title, final String msg) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, title);
		intent.putExtra(Intent.EXTRA_TEXT, msg);
		context.startActivity(Intent.createChooser(intent, szChooserTitle));
	}

	// DeviceId
	public static String getDeviceId(Context context) {
			String device_id = GetDeviceIMEI(context);
			if(ParamsCheckUtils.isNull(device_id)){
				device_id = getLocalMacAddress(context);
			}
			if(ParamsCheckUtils.isNull(device_id)){
				try{
				device_id = android.provider.Settings.Secure.getString(
						context.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);
				}catch(Exception e){
					device_id = "";
				}
			}
			return device_id;
	}
}
