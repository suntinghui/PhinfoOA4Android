package com.heqifuhou.actbase;

import android.os.Bundle;
import android.view.KeyEvent;

import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.protocolbase.HttpThread;
import com.heqifuhou.protocolbase.HttpThread.IHttpRunnable;
import com.heqifuhou.protocolbase.HttpThread.IThreadResultListener;

public abstract class ThreadMyActBase extends MyActBase implements
		IThreadResultListener, IHttpThreadListUtils {
	private HttpThreadListUtils httpUtils = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		httpUtils = new HttpThreadListUtils(this);
	}

	@Override
	protected void onDestroy() {
		clearAndStopHttpThread();
		super.onDestroy();
	}

	public void finish() {
		clearAndStopHttpThread();
		super.finish();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			clearAndStopHttpThread();
		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean isRequest(int id) {
		return httpUtils.isRequest(id);
	}

	public HttpThread quickHttpRequest(int id, IHttpRunnable runnable) {
		return quickHttpRequest(id, runnable, null, false);
	}

	public HttpThread quickHttpRequest(int id, IHttpRunnable runnable,
			Object requestObj) {
		return quickHttpRequest(id, runnable, requestObj, false);
	}

	public HttpThread quickHttpRequest(int id, IHttpRunnable runnable,
			Object requestObj, boolean isRecy) {
		this.showLoading();
		HttpThread thread = httpUtils.quickHttpRequest(id, runnable,
				requestObj, isRecy);
		if (thread == null) {
			this.hideLoading();
		}
		return thread;
	}
	@Override
	public HttpThread quickHttpRequest(int id, IHttpRunnable runnable,
			IThreadResultListener listener, Object requestObj, boolean isRecy) {
		this.showLoading();
		HttpThread thread = httpUtils.quickHttpRequest(id, runnable, listener, requestObj, isRecy);
		if (thread == null) {
			this.hideLoading();
		}
		return thread;
	}
	public void removeHttpThread(int id) {
		httpUtils.removeHttpThread(id);
	}

	public void removeAndStopHttpThread(int id) {
		httpUtils.removeAndStopHttpThread(id);
	}

	public final void clearAndStopHttpThread() {
		httpUtils.clearAndStopHttpThread();
	}

	public HttpThread getThreadByID(int id) {
		return httpUtils.getThreadByID(id);
	}

	public void onHttpForResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		this.hideLoading();
		removeHttpThread(id);
		this.onHttpResult(id, obj, requestObj);
	}

	// 回调下面的执行的方法
	protected abstract void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj);

}
