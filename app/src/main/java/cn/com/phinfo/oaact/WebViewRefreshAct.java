package cn.com.phinfo.oaact;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.DownloadListener;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.chrome.INetWebChromeClient;
import com.heqifuhou.chrome.INetWebViewClient;
import com.heqifuhou.chrome.MyWebView;
import com.heqifuhou.chrome.MyWebViewClient;
import com.heqifuhou.netbase.MyNetUtil;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.heqifuhou.pulltorefresh.PullToRefreshMyWebView;
import com.heqifuhou.utils.ParamsCheckUtils;

public class WebViewRefreshAct extends HttpMyActBase implements
		INetWebViewClient, INetWebChromeClient,DownloadListener ,OnRefreshListener<MyWebView> {
	private boolean bRefresh = false;//
	private PullToRefreshMyWebView mRefWebView = null;
	protected MyWebView mWebView;
	protected ProgressBar mBar = null;
	private String url = null,title = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		url = getIntentUrl();
		title = getIntentTitle();
		init();
        addTextNav(title);
        onRefresh(url);
	}
	
	protected void onRefresh(String url) {
		if (!MyNetUtil.IsCanConnectNet(this)) {
			this.setNetNotOpen();
			return;
		}
		if (!ParamsCheckUtils.isNull(url)) {
			mWebView.loadUrl(url);
		}
		this.setSuccessed();
	}

	private final String getIntentUrl() {
		String str = this.getIntent().getStringExtra("URL");
		if (ParamsCheckUtils.isNull(str)) {
			return "";
		}
		if (str.startsWith("http://")||str.startsWith("https://")) {
			return str;
		}
		return "http://" + str;
	}

	private final String getIntentTitle() {
		return this.getIntent().getStringExtra("TITLE");
	}

	protected void onBroadcastReceiverListener(Context context, Intent intent) {
		super.onBroadcastReceiverListener(context, intent);
	};

	private void init() {
		this.addViewFillInRoot(R.layout.act_browser_context);
		mBar = (ProgressBar) this.findViewById(R.id.title_progress_bar);
		mRefWebView = new PullToRefreshMyWebView(this, this, this);
		mRefWebView.setOnRefreshListener(this);
//		mRefWebView.setMode(Mode.PULL_FROM_START);
		mRefWebView.setMode(Mode.DISABLED);
		mWebView = mRefWebView.getRefreshableView();
		((ViewGroup) this.findViewById(R.id.browser_center)).addView(
				mRefWebView, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
	}


	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if ((keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack())) {
//			mWebView.goBack();
//			return true;
//		}
		return super.onKeyDown(keyCode, event);
	}



	@Override
	public void onLoadResource(WebView view, String url) {
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		mBar.setVisibility(View.GONE);
		this.hideLoading(true);
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		//成功的ID
		String refreshID = MyWebViewClient.getValueByName(url, "refreshID");
		if(!ParamsCheckUtils.isNull(refreshID)){
			//刷新待办列表页面
			Intent i = new Intent(IBroadcastAction.ACTION_DO_OK);
			i.putExtra("REFRESHID", refreshID);
			sendBroadcast(i);
		}
		// 退出
		if ("1".equals(MyWebViewClient.getValueByName(url, "exitInstacelist"))) {
			this.finish();
			return;
		}
		
		mBar.setVisibility(View.VISIBLE);
		mBar.setProgress(0);
		// 如果是下拉刷新，则不用显示中产是的
		if (!bRefresh) {
			this.showLoading();
		}
		bRefresh = false;
	}

	@Override
	public void onReceivedError(WebView view, int errorCode,
			String description, String failingUrl) {
		this.hideLoading(true);
	}

	@Override
	public void onReceivedHttpAuthRequest(WebView view,
			HttpAuthHandler handler, String host, String realm) {
	}

	@Override
	public void onReceivedSslError(WebView view, SslErrorHandler handler,
			SslError error) {
		this.hideLoading(true);
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		mBar.setProgress(0);
		return false;
	}

	@Override
	public void onProgressChanged(WebView view, int newProgress) {
		mBar.setProgress(newProgress);
	}

	@Override
	public void onReceivedTitle(WebView view, String title) {

	}

	protected void onDestroy() {
		if (mWebView != null) {
			mWebView.setVisibility(View.GONE);
		}
		super.onDestroy();
	}


	@Override
	public void onRefresh(PullToRefreshBase<MyWebView> refreshView) {
		mRefWebView.getRefreshableView().reload();
		bRefresh = true;
	}

	@Override
	public void onDownloadStart(String url, String userAgent,
			String contentDisposition, String mimetype, long contentLength) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		// TODO Auto-generated method stub
		
	}

}
