package com.heqifuhou.update;

import java.io.File;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.update.base.DownloadUtil;
import com.heqifuhou.update.base.DownloadUtil.IOnDownloadListener;

public class DownloadForceServices extends Service {
	private String downUrl= null;
	@Override
	public IBinder onBind(Intent intent) {
		return new MsgBinder();
	}

	@Override
	public void onDestroy() {
		cancelDownLoad();
		super.onDestroy();
	}


	public class MsgBinder extends Binder {
		public DownloadForceServices getService() {
			return DownloadForceServices.this;
		}
	}

	private DownloadUtil downLoad = null;
	private Thread downLoadThread = null;
	private OnDownSateListener onDownSateListener = null;
	public void download(final String downUrl,OnDownSateListener onDownSateListener)
	{
		this.downUrl = downUrl;
		cancelDownLoad();
		this.onDownSateListener = onDownSateListener;
		downLoad = new DownloadUtil();
		downLoad.setOnDownloadListener(onDownloadListener);
		try{
			downLoadThread = new Thread(new Runnable() {
				@Override
				public void run() {
					if(downLoadThread!=null&&!downLoadThread.isInterrupted()&&downLoad!=null){
						downLoad.downloadFile(downUrl);
					}	
				}
			});
			downLoadThread.start();
		}catch(Exception e){
		}
		//开始下载
		handlerUpdate.postDelayed(UpdateTick, 1500);
	}
	
	public void cancelDownLoad(){
		if(downLoad!=null){
			downLoad.close();
			downLoad= null;
		}
		if(downLoadThread!=null){
			downLoadThread.interrupt();
		}
		if(handlerUpdate!=null){
			handlerUpdate.removeCallbacks(UpdateTick);
			handlerUpdate.removeMessages(12);
		}
	}
	
	
	final Runnable UpdateTick = new Runnable() {
		@Override
		public void run() {
			downLoad.setOnDownloadListener(onDownloadListener);
		}
	};
	
	final private Handler handlerUpdate = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 12:
				DownloadUtil.installApk(DownloadForceServices.this, (File) msg.obj);
				state.setState(DownStateEnum.EnNormal);
				exit();
				stopSelf(-1);
				break;
			}
		}
	};
	public final String getDownUrl(){
		return downUrl;
	}
	public void setOnDownSateListener(OnDownSateListener onDownSateListener){
		this.onDownSateListener = onDownSateListener;
	}
	final private IOnDownloadListener onDownloadListener = new IOnDownloadListener() {
		@Override
		public void updateNotification(boolean bError, int progress,
				int totalSize, File downFile) {
			state.setFile(downFile);
			if (bError) {
				state.setState(DownStateEnum.EnError);	
				state.setRate(0);
				if(onDownSateListener!=null){
					onDownSateListener.onDownListener(DownStateEnum.EnError, 0, downFile);
				}
				return;
			}
			int rate = 0;
			// 计算百分比
			if (totalSize > 0) {
				rate = progress * 100 / totalSize;
				state.setState(DownStateEnum.EnDowing);
				state.setRate(rate);
				if(onDownSateListener!=null){
					onDownSateListener.onDownListener(DownStateEnum.EnDowing,rate, downFile);
				}
			} else if (totalSize == 0) {
				state.setState(DownStateEnum.EnDowing);
				state.setRate(rate);
				if(onDownSateListener!=null){
					onDownSateListener.onDownListener(DownStateEnum.EnDowing,rate, downFile);
				}
			}
			// 是否下载结束
			if (totalSize > 0 && null != downFile
					&& totalSize == (int) downFile.length()) {
				state.setState(DownStateEnum.EnOver);
				state.setRate(rate);
				if(onDownSateListener!=null){
					onDownSateListener.onDownListener(DownStateEnum.EnOver,rate, downFile);
				}
				Message msg = new Message();
				msg.what = 12;
				msg.obj = downFile;
				handlerUpdate.sendMessageAtTime(msg, 1500);
				return;
			}
			handlerUpdate.postDelayed(UpdateTick, 1500);
		}
	};
	
	
	public interface OnDownSateListener{
		//正在开载
		void onDownListener(DownStateEnum state,final int progress,final File file);
	}
	
	private StateItem state = new StateItem();
	public static class StateItem{
		public StateItem(){};
		public StateItem(DownStateEnum state,int rate,File file){
			this.state = state;
			this.rate = rate;
			this.file = file;
		}
		private int rate = 0;
		private File file = null;
		private DownStateEnum state = DownStateEnum.EnNormal;
		public int getRate() {
			return rate;
		}
		public void setRate(int rate) {
			this.rate = rate;
		}
		public File getFile() {
			return file;
		}
		public void setFile(File file) {
			this.file = file;
		}
		public DownStateEnum getState() {
			return state;
		}
		public void setState(DownStateEnum state) {
			this.state = state;
		}
	}
	public final StateItem getStateItem(){
		return state;
	}
	
	

	public enum DownStateEnum {
		EnNormal(0),
		EnError(1),//0=出错
		EnDowing(2),//1=进行中
		EnOver(3);//2=结速
		private int value = 0;
		private DownStateEnum(int value) {//必须是private的
	        this.value = value;
	    }
	    public int value() {
	        return this.value;
	    }
	}
	private void exit(){
		Intent intent = new Intent(IBroadcastAction.ACTION_EXIT);
		sendBroadcast(intent);
	}
}
