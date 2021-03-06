package com.heqifuhou.utils;import java.util.List;import android.app.Activity;import android.app.ActivityManager;import android.app.ActivityManager.RunningAppProcessInfo;import android.content.Context;public class AppOnForegroundUtils {	public static boolean isAppOnForeground(Activity act) {		// Returns a list of application processes that are running on the		// device		ActivityManager activityManager = (ActivityManager) act.getApplicationContext()				.getSystemService(Context.ACTIVITY_SERVICE);		String packageName = act.getApplicationContext().getPackageName();		List<RunningAppProcessInfo> appProcesses = activityManager				.getRunningAppProcesses();		if (appProcesses == null)			return false;		for (RunningAppProcessInfo appProcess : appProcesses) {			// The name of the process that this object is associated with.			if (appProcess.processName.equals(packageName)					&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {				return true;			}		}		return false;	}}