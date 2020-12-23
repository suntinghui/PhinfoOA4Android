package com.heqifuhou.chrome;

import android.webkit.WebView;

public interface INetWebChromeClient {
	public void onProgressChanged(WebView view, int newProgress);
	public void onReceivedTitle(WebView view, String title);
}
