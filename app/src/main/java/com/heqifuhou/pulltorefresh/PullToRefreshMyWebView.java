package com.heqifuhou.pulltorefresh;

import com.heqifuhou.chrome.INetWebChromeClient;
import com.heqifuhou.chrome.INetWebViewClient;
import com.heqifuhou.chrome.MyWebChromeClient;
import com.heqifuhou.chrome.MyWebView;
import com.heqifuhou.chrome.MyWebViewClient;
import com.heqifuhou.chrome.MyWebViewDownLoadListener;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.webkit.DownloadListener;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import cn.com.phinfo.oaact.R;

public class PullToRefreshMyWebView extends PullToRefreshBase<MyWebView>
		implements INetWebViewClient, INetWebChromeClient {
	private Activity act;
	private INetWebViewClient ViewClient;
	private INetWebChromeClient chromeClient;

	public PullToRefreshMyWebView(Activity act, INetWebViewClient ViewClient,
			INetWebChromeClient chromeClient) {
		super(act);
		setOnRefreshListener(defaultOnRefreshListener);
		this.act = act;
		this.ViewClient = ViewClient;
		this.chromeClient = chromeClient;
		this.setMode(Mode.PULL_FROM_START);
	}
	//??��??
	private static final OnRefreshListener<MyWebView> defaultOnRefreshListener = new OnRefreshListener<MyWebView>() {
		@Override
		public void onRefresh(PullToRefreshBase<MyWebView> refreshView) {
			refreshView.getRefreshableView().reload();
		}
	};

	@Override
	public final Orientation getPullToRefreshScrollDirection() {
		return Orientation.VERTICAL;
	}

	@Override
	protected boolean isReadyForPullStart() {
		return mRefreshableView.getScrollY() == 0;
	}

	@Override
	protected boolean isReadyForPullEnd() {
		double exactContentHeight = Math.floor(mRefreshableView
				.getContentHeight() * mRefreshableView.getScale());
		return mRefreshableView.getScrollY() >= (exactContentHeight - mRefreshableView
				.getHeight());
	}

	@Override
	protected void onPtrRestoreInstanceState(Bundle savedInstanceState) {
		super.onPtrRestoreInstanceState(savedInstanceState);
		mRefreshableView.restoreState(savedInstanceState);
	}

	@Override
	protected void onPtrSaveInstanceState(Bundle saveState) {
		super.onPtrSaveInstanceState(saveState);
		mRefreshableView.saveState(saveState);
	}

	@Override
	protected MyWebView createRefreshableView(Context context,
			AttributeSet attrs) {
		WebViewClient ViewClient = new MyWebViewClient(this);
		WebChromeClient chromeClient = new MyWebChromeClient(this);
		DownloadListener downLoadListerner = new MyWebViewDownLoadListener(act);
	//	mRefreshableView.setWebChromeClient(chromeClient);
		MyWebView webView;
		if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
			webView = new InternalWebViewSDK9(context, ViewClient,chromeClient,downLoadListerner);
		} else {
			webView = new MyWebView(context, ViewClient,chromeClient,downLoadListerner);
		}
		webView.setId(R.id.webview);
		return webView;
	}


	@TargetApi(9)
	final class InternalWebViewSDK9 extends MyWebView {

		// WebView doesn't always scroll back to it's edge so we add some
		// fuzziness
		static final int OVERSCROLL_FUZZY_THRESHOLD = 2;

		// WebView seems quite reluctant to overscroll so we use the scale
		// factor to scale it's value
		static final float OVERSCROLL_SCALE_FACTOR = 1.5f;

		public InternalWebViewSDK9(Context context, WebViewClient ViewClient,
				WebChromeClient chromeClient, DownloadListener downLoadListerner) {
			super(context, ViewClient, chromeClient, downLoadListerner);
		}

		@Override
		protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
				int scrollY, int scrollRangeX, int scrollRangeY,
				int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

			final boolean returnValue = super.overScrollBy(deltaX, deltaY,
					scrollX, scrollY, scrollRangeX, scrollRangeY,
					maxOverScrollX, maxOverScrollY, isTouchEvent);

			// Does all of the hard work...
			OverscrollHelper.overScrollBy(PullToRefreshMyWebView.this, deltaX,
					scrollX, deltaY, scrollY, getScrollRange(),
					OVERSCROLL_FUZZY_THRESHOLD, OVERSCROLL_SCALE_FACTOR,
					isTouchEvent);

			return returnValue;
		}

		private int getScrollRange() {
			return (int) Math
					.max(0,
							Math.floor(mRefreshableView.getContentHeight()
									* mRefreshableView.getScale())
									- (getHeight() - getPaddingBottom() - getPaddingTop()));
		}
	}

	@Override
	public void onProgressChanged(WebView view, int newProgress) {
		if (newProgress == 100) {
			onRefreshComplete();
		}
		if(this.chromeClient!=null){
			this.chromeClient.onProgressChanged(view, newProgress);
		}
	}

	@Override
	public void onReceivedTitle(WebView view, String title) {
		if(this.chromeClient!=null){
			this.chromeClient.onReceivedTitle(view, title);
		}
		
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		if(this.ViewClient!=null){
			return this.ViewClient.shouldOverrideUrlLoading(view, url);
		}
		return false;
	}

	@Override
	public void onReceivedSslError(WebView view, SslErrorHandler handler,
			SslError error) {
		if(this.ViewClient!=null){
			this.ViewClient.onReceivedSslError(view, handler, error);
		}
		
	}

	@Override
	public void onReceivedHttpAuthRequest(WebView view,
			HttpAuthHandler handler, String host, String realm) {
		if(this.ViewClient!=null){
			this.ViewClient.onReceivedHttpAuthRequest(view, handler, host, realm);
		}
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		if(this.ViewClient!=null){
			this.ViewClient.onPageFinished(view, url);
		}
		
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		if(this.ViewClient!=null){
			this.ViewClient.onPageStarted(view, url, favicon);
		}
		
	}

	@Override
	public void onLoadResource(WebView view, String url) {
		if(this.ViewClient!=null){
			this.ViewClient.onLoadResource(view, url);
		}
		
	}

	@Override
	public void onReceivedError(WebView view, int errorCode,
			String description, String failingUrl) {
		if(this.ViewClient!=null){
			this.ViewClient.onReceivedError(view, errorCode, description, failingUrl);
		}
		
	}
}
