package com.heqifuhou.tab;

import java.util.List;

import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.tab.base.OnTabActivityResultListener;
import com.heqifuhou.utils.StaticReflectUtils;

import android.app.Activity;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

public class MainTabActivityBase extends TabActivity implements IBroadcastAction {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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

	protected void onPause(){
		super.onPause();
	}
}
