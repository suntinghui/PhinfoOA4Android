package com.heqifuhou.tab;

import java.util.List;

import com.heqifuhou.actbase.HttpThreadListUtils;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.actbase.IHttpThreadListUtils;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.protocolbase.HttpThread;
import com.heqifuhou.protocolbase.HttpThread.IHttpRunnable;
import com.heqifuhou.protocolbase.HttpThread.IThreadResultListener;
import com.heqifuhou.tab.base.OnTabActivityResultListener;
import com.heqifuhou.utils.StaticReflectUtils;

import android.app.Activity;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

public abstract class ThreadMainTabActivityBase extends TabActivity implements
		IBroadcastAction, IHttpThreadListUtils, IThreadResultListener {
	private IHttpThreadListUtils utils;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		utils = new HttpThreadListUtils(this);
		registerReceiver();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Activity subActivity = getLocalActivityManager().getCurrentActivity();
		if (subActivity instanceof OnTabActivityResultListener) {
			((OnTabActivityResultListener) subActivity).onTabActivityResult(
					requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// 注释事件
	private void registerReceiver() {
		List<String> ls = StaticReflectUtils
				.getActionList(IBroadcastAction.class);
		IntentFilter intentFilter = new IntentFilter();
		for (String action : ls) {
			intentFilter.addAction(action);
		}
		this.registerReceiver(broadCastRev, intentFilter);
	}

	private final BroadcastReceiver broadCastRev = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ACTION_EXIT)) {
				((Activity) context).finish();
				return;
			}
			onBroadcastReceiverListener(context, intent);
		}
	};

	protected void onBroadcastReceiverListener(Context context, Intent intent) {
		// 注册事件
	};

	protected void onDestroy() {
		this.unregisterReceiver(broadCastRev);
		super.onDestroy();
	}

	protected void onResume() {
		super.onResume();
	}

	protected void onPause() {
		super.onPause();
	}

	@Override
	public boolean isRequest(int id) {
		return utils.isRequest(id);
	}

	@Override
	public HttpThread getThreadByID(int id) {
		return utils.getThreadByID(id);
	}

	@Override
	public HttpThread quickHttpRequest(int id, IHttpRunnable runnable) {
		return utils.quickHttpRequest(id, runnable);
	}

	@Override
	public HttpThread quickHttpRequest(int id, IHttpRunnable runnable,
			Object requestObj) {
		return utils.quickHttpRequest(id, runnable, requestObj);
	}

	@Override
	public HttpThread quickHttpRequest(int id, IHttpRunnable runnable,
			Object requestObj, boolean isRecy) {
		return utils.quickHttpRequest(id, runnable, requestObj, isRecy);
	}

	@Override
	public void removeHttpThread(int id) {
		utils.removeHttpThread(id);
	}

	@Override
	public void removeAndStopHttpThread(int id) {
		utils.removeAndStopHttpThread(id);
	}

	@Override
	public void clearAndStopHttpThread() {
		utils.clearAndStopHttpThread();
	}

	public void onHttpForResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		removeHttpThread(id);
		this.onHttpResult(id, obj, requestObj);
	}

	// 回调下面的执行的方法
	protected abstract void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj);
}
