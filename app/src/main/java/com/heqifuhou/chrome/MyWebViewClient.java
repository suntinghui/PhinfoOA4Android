package com.heqifuhou.chrome;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.utils.BaseUtil;
import com.heqifuhou.utils.ParamsCheckUtils;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import cn.com.phinfo.oaact.MyApplet;

public class MyWebViewClient extends WebViewClient {
	private static final String TEL_NO = "tel:";
	private static final String MAILTO_NO = "mailto:";
	private static final String GT_ACTION = "gtaction";
	private INetWebViewClient mNetWebView;
	private boolean loadingFinished = true;
	private int nReLoad = 0;
	private boolean redirect = false;
	// 离线资源
	private final Set<String> offlineResources = new HashSet<>();

	public MyWebViewClient(INetWebViewClient state) {
		this.mNetWebView = state;
		fetchOfflineResources();
	}

	private void fetchOfflineResources() {
		AssetManager am = MyApplet.getInstance().getAssets();
		try {
			String[] res = am.list("offline_res");
			if (res != null) {
				Collections.addAll(offlineResources, res);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		if (!ParamsCheckUtils.isNull(url) && url.length() > TEL_NO.length()
				&& TEL_NO.equals(url.substring(0, TEL_NO.length()))) {
			BaseUtil.Call(view.getContext(), url);
			return true;
		}
		if (!ParamsCheckUtils.isNull(url) && url.length() > MAILTO_NO.length()
				&& MAILTO_NO.equals(url.substring(0, MAILTO_NO.length()))) {
			BaseUtil.MailTo(view.getContext(), url);
			return true;
		}
		if (!ParamsCheckUtils.isNull(url)) {
			String actionValue = getValueByName(url, GT_ACTION);
			if (!ParamsCheckUtils.isNull(actionValue)) {
				Intent i = new Intent(IBroadcastAction.ACTION_GT_WEB_ACTION);
				i.putExtra("WEB_ACTION", actionValue);
				MyApplet.getInstance().sendBroadcast(i);
			}
		}
		if (!loadingFinished) {
			redirect = true;
		}
		loadingFinished = false;
		nReLoad = 0;
		if (mNetWebView != null
				&& !mNetWebView.shouldOverrideUrlLoading(view, url)) {
			view.loadUrl(url);
		}
		return true;
	}

	public static String getValueByName(String url, String name) {
		String result = "";
		int index = url.indexOf("?");
		String temp = url.substring(index + 1);
		String[] keyValue = temp.split("&");
		for (String str : keyValue) {
			if (str.contains(name)) {
				result = str.replace(name + "=", "");
				break;
			}
		}
		return result;
	}

	public void onReceivedSslError(WebView view, SslErrorHandler handler,
			SslError error) {
		handler.proceed();
		if (mNetWebView != null) {
			mNetWebView.onReceivedSslError(view, handler, error);
		}
	}

	@Override
	public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
		int lastSlash = url.lastIndexOf("/");
		if (lastSlash != -1) {
			String suffix = url.substring(lastSlash + 1);
			if (offlineResources.contains(suffix)) {
				String mimeType;
				if (suffix.endsWith(".js")) {
					mimeType = "application/x-javascript";
				} else if (suffix.endsWith(".css")) {
					mimeType = "text/css";
				} else {
					mimeType = "text/html";
				}
				try {
					InputStream is = MyApplet.getInstance().getAssets().open("offline_res/" + suffix);
					return new WebResourceResponse(mimeType, "UTF-8", is);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return super.shouldInterceptRequest(view, url);
	}

	public void onReceivedHttpAuthRequest(WebView view,
			HttpAuthHandler handler, String host, String realm) {
		if (mNetWebView != null) {
			mNetWebView.onReceivedHttpAuthRequest(view, handler, host, realm);
		}
	}

	public void onPageFinished(WebView view, String url) {
		if (!redirect) {
			loadingFinished = true;
		}
		if (loadingFinished && !redirect) {
			if (mNetWebView != null) {
				mNetWebView.onPageFinished(view, url);
			}
		} else {
			redirect = false;
		}
	}

	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		loadingFinished = false;
		if (mNetWebView != null) {
			mNetWebView.onPageStarted(view, url, favicon);
		}
	}

	public void onLoadResource(WebView view, String url) {
		if (mNetWebView != null) {
			mNetWebView.onLoadResource(view, url);
		}
	}

	public void onReceivedError(WebView view, int errorCode,
			String description, String failingUrl) {
		if (++nReLoad > 3) {
			nReLoad = 0;
			if (mNetWebView != null) {
				mNetWebView.onReceivedError(view, errorCode, description,
						failingUrl);
			}
		} else {
			view.loadUrl(failingUrl);
		}
	}
}
