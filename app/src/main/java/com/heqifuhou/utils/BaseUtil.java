package com.heqifuhou.utils;

import java.lang.reflect.Field;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.webkit.MimeTypeMap;

public class BaseUtil {
	static public boolean checkApkExist(Context context, String packageName) {
		if (packageName == null || "".equals(packageName))
			return false;
		try {
			ApplicationInfo info = context.getPackageManager()
					.getApplicationInfo(packageName,
							PackageManager.GET_UNINSTALLED_PACKAGES);
			if (info != null) {
				return true;
			}
		} catch (NameNotFoundException e) {
		}
		return false;
	}

	static public boolean checkApkExist(Context context, Intent intent) {
		List<ResolveInfo> list = context.getPackageManager()
				.queryIntentActivities(intent, 0);
		if (list.size() > 0) {
			return true;
		}
		return false;
	}

	// 调用WEB浏览器
	// 示例String url = "http://www.www.com"
	public static void OpenWebBrowser(Activity activity, String url) {
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		activity.startActivity(intent);
	}

	public static void OpenWebBrowser(Activity activity, String url,
			int nRequestCode) {
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		activity.startActivityForResult(intent, nRequestCode);
	}

	// 打电话
	public static boolean Call(Context context, String szTelNo) {
		try {
			String sz = "tel:" + szTelNo;
			Uri uri = Uri.parse(sz);
			Intent intent = new Intent(Intent.ACTION_DIAL, uri);
			context.startActivity(intent);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	static public int getRid(Context context, String paramString1,
			String paramString2) {
		try {
			Class localClass = Class.forName(context.getPackageName() + ".R$"
					+ paramString1);
			Field localField = localClass.getField(paramString2);
			String Sz = localField.get(localField.getName()).toString();
			int i = Integer.parseInt(Sz);
			return i;
		} catch (Exception localException) {
		}
		return 0;
	}

	static public void MailTo(Context context, String url) {
		Uri uri = Uri.parse(url);
		Intent it = new Intent(Intent.ACTION_SENDTO, uri);
		context.startActivity(it);
	}

	static public void playAudio(Context context,String url) {
//		String type = null;
//		if (end.equals("avi") || end.equals("mp4") || end.equals("mov")
//				|| end.equals("flv") || end.equals("3gp") || end.equals("m4v")
//				|| end.equals("wmv") || end.equals("rm") || end.equals("rmvb")
//				|| end.equals("mkv") || end.equals("ts") || end.equals("webm")) {
//			// video
//			type = "video/*";
//		} else if (end.equals("mid") || end.equals("midi") || end.equals("mp3")
//				|| end.equals("wav") || end.equals("wma") || end.equals("amr")
//				|| end.equals("ogg") || end.equals("m4a")) {
//			// audio
//			type = "audio/*";
//		}
//		Intent it = new Intent();
//		it.setAction(Intent.ACTION_VIEW);
//		it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		Uri uri = Uri.parse(url);//此url就是流媒体文件的下载地址
//		it.setDataAndType(uri, "audio/*");//type的值是&nbsp;"video/*"或者&nbsp;"audio/*"
//		context.startActivity(it);
//		try{
//			Intent intent = new Intent();
//			Uri uri = Uri.parse(url);
//			intent.setDataAndType(uri, "audio/*");
//			intent.setAction(Intent.ACTION_VIEW);
//			context.startActivity(intent);	
//		}catch(Exception e){
//			e.printStackTrace();
//		}
		try{
		    String extension = MimeTypeMap.getFileExtensionFromUrl(url);  
		    String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);  
		    Intent mediaIntent = new Intent(Intent.ACTION_VIEW);  
		    mediaIntent.setDataAndType(Uri.parse(url), mimeType);  
		    context.startActivity(mediaIntent);  
		}catch(Exception e){
		}

	}
	
}
