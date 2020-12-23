package com.heqifuhou.chrome;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.DownloadListener;

public class MyWebViewDownLoadListener implements DownloadListener{
	Activity act;
	public MyWebViewDownLoadListener(Activity act)
	{
		this.act = act;
	}
	@Override
	public void onDownloadStart(String url, String userAgent,
			String contentDisposition, String mimetype, long contentLength) {
		try{
			Uri uri = Uri.parse(url);  
	        Intent intent = new Intent(Intent.ACTION_VIEW, uri);  
	        act.startActivity(intent);  
		}catch(Exception e){
		}
	}  
}
