package com.heqifuhou.update;

import java.io.File;
import java.util.List;
import java.util.Stack;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;

import com.heqifuhou.update.DownloadForceServices.DownStateEnum;
import com.heqifuhou.update.DownloadForceServices.OnDownSateListener;
import com.heqifuhou.update.UpdateTipDialog.OnUpdateDialogButtonClickListener;
import com.heqifuhou.update.base.DownloadUtil;

public class DonloadForceUtils {
	public static DonloadForceUtils instance;
	private UpdateTipDialog dialog = null;
	private String downLoadUrl;
	private Activity act;
	private List<OnCancelDownladListener> listernerList = new Stack<OnCancelDownladListener>();
	public interface OnCancelDownladListener{
		void onCancelDownladListener();
	}
	public static DonloadForceUtils getInstance(Activity act){
		if(instance==null){
			instance = new DonloadForceUtils(act);
		}
		instance.act = act;
		return instance;
	}
	
	public void addCancelDownladListener(OnCancelDownladListener listerner){
		listernerList.add(listerner);
	}
	public void removeCancelDownladListener(OnCancelDownladListener listerner){
		listernerList.remove(listerner);
	}
	public void clearListener(){
		listernerList.clear();
	}
	private DonloadForceUtils(Activity act){
		this.act = act;
	}
	
	public void registerDonloadServer(){
		// 注册service
		Intent intent = new Intent(act, DownloadForceServices.class);
		act.startService(intent); // 如果先调用startService,则在多个服务绑定对象调用unbindService后服务仍不会被销毁
		act.bindService(intent, conn, Context.BIND_AUTO_CREATE);
	}
	
	public  void updateDialog(final String  url) {
		if(dialog!=null&&dialog.isShowing()){
			return;
		}
		dialog = new UpdateTipDialog(act,true,"你当前版本不再使用，请升级至新版本....");
		dialog.setOnClickListener(new OnUpdateDialogButtonClickListener() {
			//忽视升级
			@Override
			public void onIgnoreBtnClick() {
				//不可能走到这儿
			}
			//升级
			@Override
			public void onDownloadBtnClick() {
				// 强制升级
				downLoadUrl = url;
				sendUpdateHander(url);
			}
			@Override
			public void onCancelBtnClick() {
				onListener();
			}
		});
		dialog.show();
	}
	
	private void onListener(){
		for(int i=0;i<listernerList.size();i++){
			listernerList.get(i).onCancelDownladListener();
		}
			
	}

	// 升级进度条
	private UpdateProgressDialog pBar;
	private DownloadForceServices downloadForceServices = null;

	// 强制升级
	private void sendUpdateHander(final String downUrl) {
		// 一切初始化，关掉一切
		closeDialog();
		cancelDownLoad();
		Message msg = new Message();
		msg.what = 11;
		msg.obj = downUrl;
		handlerUpdate.sendMessageAtTime(msg, 1500);
	}

	private void showUpdateTipDialog(final String downUrl) {
		if(pBar!=null&&pBar.isShowing()){
			return;
		}
		pBar = new UpdateProgressDialog(act, "请稍候...");
		pBar.show();
		pBar.setOnClickListener("取消", barCancle);
		if (downloadForceServices != null) {
			downloadForceServices.download(downUrl, onDownSateListener);
		}
	}

	final private OnDownSateListener onDownSateListener = new OnDownSateListener() {

		@Override
		public void onDownListener(DownStateEnum state, int progress,
				final File file) {
			if (state == DownStateEnum.EnError) {
				pBar.setTextContext("由于网络原因下载未完成");
				pBar.setOnClickListener("重新下载", downRetry);
				return;
			}
			if (state == DownStateEnum.EnDowing) {
				pBar.setProgress(progress);
				pBar.setTextContext("正在下载...");
				pBar.setOnClickListener("取消", barCancle);
				return;
			}
			if (state == DownStateEnum.EnOver) {
				pBar.setProgress(progress);
				pBar.setTextContext("下载完成");
				pBar.setOnClickListener("现在安装", new OnClickListener() {
					@Override
					public void onClick(View v) {
						closeDialog();
						cancelDownLoad();
						onListener();
						DownloadUtil.installApk(act, file);
					}
				});
			}
		}

	};
	final private Handler handlerUpdate = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 11:
				showUpdateTipDialog((String) msg.obj);
				break;
			}
		}
	};

	// 取消下载
	final private OnClickListener barCancle = new OnClickListener() {
		@Override
		public void onClick(View v) {
			closeDialog();
			cancelDownLoad();
			updateDialog(downLoadUrl);
		}
	};
	// 重新下载
	final private OnClickListener downRetry = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (downloadForceServices != null) {
				downloadForceServices.cancelDownLoad();
				// 重新进入强制下载
				sendUpdateHander(downloadForceServices.getDownUrl());
			}
		}
	};

	private ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			if (downloadForceServices != null) {
				downloadForceServices.cancelDownLoad();
				downloadForceServices.setOnDownSateListener(null);
			}
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// 返回一个MsgService对象
			downloadForceServices = ((DownloadForceServices.MsgBinder) service)
					.getService();
		}
	};

	private void cancelDownLoad() {
		if (downloadForceServices != null) {
			downloadForceServices.cancelDownLoad();
			downloadForceServices.setOnDownSateListener(null);
		}
	}

	private void closeDialog() {
		if (downloadForceServices != null) {
			downloadForceServices.setOnDownSateListener(null);
		}
		if (handlerUpdate != null) {
			handlerUpdate.removeMessages(11);
		}
		if (pBar != null && pBar.isShowing()) {
			pBar.dismiss();
			pBar = null;
		}
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
			dialog = null;
		}
	}
}
