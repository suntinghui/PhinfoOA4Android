package com.heqifuhou.chrome;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ZoomButtonsController;

import cn.com.phinfo.oaact.MyApplet;

public class MyWebView extends WebView {
	public MyWebView(Context context, WebViewClient ViewClient,
			WebChromeClient chromeClient, DownloadListener downLoadListerner) {
		super(context);
		init(ViewClient, chromeClient, downLoadListerner);
	}

	public MyWebView(Context context, AttributeSet attrs,
			WebViewClient ViewClient, WebChromeClient chromeClient,
			DownloadListener downLoadListerner) {
		super(context, attrs);
		init(ViewClient, chromeClient, downLoadListerner);
	}

	public MyWebView(Context context, AttributeSet attrs, int defStyle,
			WebViewClient ViewClient, WebChromeClient chromeClient,
			DownloadListener downLoadListerner) {
		super(context, attrs, defStyle);
		init(ViewClient, chromeClient, downLoadListerner);
	}

	private void init(WebViewClient client, WebChromeClient chromeClient,
			DownloadListener downLoadListerner) {
		initWebView(client, chromeClient, downLoadListerner);
	}

	public void loadUrl(final String url) {
		super.loadUrl(url);
		// this.requestFocus();
		// this.requestFocusFromTouch();
	}

	@SuppressLint("NewApi")
	private void initWebView2(WebViewClient client, WebChromeClient chromeClient, DownloadListener downLoadListerner) {
		WebSettings settings = this.getSettings();
		// 设置WebView支持JavaScript
		settings.setJavaScriptEnabled(true);
		//支持自动适配
		settings.setUseWideViewPort(true);
		settings.setLoadWithOverviewMode(true);
		settings.setSupportZoom(true);  //支持放大缩小
		settings.setBuiltInZoomControls(true); //显示缩放按钮
		settings.setBlockNetworkImage(true);// 把图片加载放在最后来加载渲染
		settings.setAllowFileAccess(true); // 允许访问文件
		settings.setSaveFormData(true);
		settings.setGeolocationEnabled(true);
		settings.setDomStorageEnabled(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);/// 支持通过JS打开新窗口
		settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
		settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		//设置不让其跳转浏览器
		this.setWebViewClient(new WebViewClient() {
			@Override
			public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
				Log.e("yao4",url);

				if (url.contains("ckeditor.js?t=4.4.4.5")) {
					InputStream is = null;
					try {
						is = MyApplet.getInstance().getAssets().open("ckeditor.js");
						Log.e("yao4", is.toString());
					} catch (IOException e) {
						e.printStackTrace();
					}
					return new WebResourceResponse("application/x-javascript", "UTF-8", is);
				}

				return super.shouldInterceptRequest(view, url);
			}
		});

		// 添加客户端支持
		this.setWebChromeClient(new WebChromeClient());
		// mWebView.loadUrl(TEXTURL);

		//不加这个图片显示不出来
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
			this.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		}
		this.getSettings().setBlockNetworkImage(false);

		//允许cookie 不然有的网站无法登陆
		CookieManager mCookieManager = CookieManager.getInstance();
		mCookieManager.setAcceptCookie(true);
		mCookieManager.setAcceptThirdPartyCookies(this, true);
	}

	@SuppressLint("NewApi")
	private void initWebView(WebViewClient client, WebChromeClient chromeClient, DownloadListener downLoadListerner) {
		this.setWebViewClient(client);
		this.setWebChromeClient(chromeClient);
		this.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
		WebSettings webSettings = this.getSettings();
		webSettings.setUseWideViewPort(true); 
		webSettings.setLoadWithOverviewMode(true); 

		// 屏幕密度
		// int screenDensity = getResources().getDisplayMetrics().densityDpi;
		// WebSettings.ZoomDensity zoomDensity = WebSettings.ZoomDensity.MEDIUM;
		// switch (screenDensity) {
		// case DisplayMetrics.DENSITY_LOW:
		// zoomDensity = WebSettings.ZoomDensity.CLOSE;
		// break;
		// case DisplayMetrics.DENSITY_MEDIUM:
		// zoomDensity = WebSettings.ZoomDensity.MEDIUM;
		// break;
		// case DisplayMetrics.DENSITY_HIGH:
		// zoomDensity = WebSettings.ZoomDensity.FAR;
		// break;
		// }
		// webSettings.setDefaultZoom(zoomDensity);

		/*
		 * 缩放页面方式1. webResetPwd.setInitialScale(55);
		 */
		/*
		 * 缩放页面方式2. 会出现放大缩小的按钮 websetting.setSupportZoom(true);
		 * websetting.setBuiltInZoomControls(true);
		 */
		/*
		 * 缩小页面方式3. websetting.setDefaultZoom(WebSettings.ZoomDensity.FAR);
		 */
		// 缩放页面方式4.
//		webSettings.setUseWideViewPort(true);
//		webSettings.setLoadWithOverviewMode(true);

		/* 缩放页面方式5. 会将页面元素在一列中显示出来 */
		// webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

		webSettings.setJavaScriptEnabled(true);
		webSettings.setUseWideViewPort(true); //支持自动适配
		webSettings.setAllowFileAccess(true);
		webSettings.setSavePassword(false);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setSaveFormData(false);
		// webSettings.setSupportZoom(false);
		this.setDownloadListener(downLoadListerner);
		// webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

		//不加这个图片显示不出来
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
			this.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		}
		this.getSettings().setBlockNetworkImage(false);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			webSettings.setDisplayZoomControls(false);
			this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}else{
			setZoomControlGone(this);
		}
	}

	public void setZoomControlGone(View view) {
		Class classType;
		Field field;
		try {
			classType = WebView.class;
			field = classType.getDeclaredField("mZoomButtonsController");
			field.setAccessible(true);
			ZoomButtonsController mZoomButtonsController = new ZoomButtonsController(
					view);
			mZoomButtonsController.getZoomControls().setVisibility(View.GONE);
			try {
				field.set(view, mZoomButtonsController);
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			}
		} catch (Exception e) {
		}
	}
}
