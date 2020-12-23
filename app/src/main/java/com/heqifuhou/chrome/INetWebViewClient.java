package com.heqifuhou.chrome;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;

public interface INetWebViewClient {
	public boolean shouldOverrideUrlLoading(WebView view, String url);
	public void onReceivedSslError(WebView view, SslErrorHandler handler,
			SslError error);
	public void onReceivedHttpAuthRequest(WebView view,
			HttpAuthHandler handler, String host, String realm);
	public void onPageFinished(WebView view, String url);
	public void onPageStarted(WebView view, String url, Bitmap favicon);
	public void onLoadResource(WebView view, String url);
	public void onReceivedError(WebView view, int errorCode,
			String description, String failingUrl);
}
