package com.heqifuhou.protocolbase;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;


public class HttpThread extends Thread {
	private byte[] sLock = new byte[0];
	private boolean bStop = false;
	private IHttpRunnable target = null;
	private IThreadResultListener listener = null;
	private int mID = 0;
	private Object mRequestObj = null;
	public HttpThread(int id, IHttpRunnable runnable, IThreadResultListener l,Object mObject) {
		super();
		this.mID = id;
		this.target = runnable;
		this.listener = l;
		this.mRequestObj = mObject;
	}
	public void setRequestObj(Object mObject){
		synchronized (sLock) {
			this.mRequestObj = mObject;
		}
	}
	//静态快速调用方式
	public static HttpThread quickHttpRequest(int id, IHttpRunnable runnable, IThreadResultListener l,Object obj)
	{
		HttpThread http = new HttpThread(id,runnable,l,obj);
		http.start();
		return http;
	}
	public Object getRequestObj(){
		synchronized (sLock) {
			return mRequestObj;
		}
	}
	public final int getID(){
		return mID;
	}
	public void stopRuning(int id) {
		synchronized (sLock) {
			if (mID != id) {
				return;
			}
			stopRuning();
		}
	}

	public void stopRuning() {
		try {
			synchronized (sLock) {
				interrupt();
				bStop = true;
				mHandler.removeMessages(0);
			}
		} catch (Exception e) {
		}
	}

	public boolean isRuning(int id) {
		synchronized (sLock) {
			if (mID != id) {
				return false;
			}
			return isRuning();
		}
	}

	public boolean isRuning() {
		synchronized (sLock) {
			State st = this.getState();
			return (st != Thread.State.TERMINATED && st != Thread.State.NEW);
		}
	}

	public boolean isStopRuning(int id) {
		synchronized (sLock) {
			if (mID != id) {
				return false;
			}
			return isStopRuning();
		}
	}

	public boolean isStopRuning() {
		synchronized (sLock) {
			return bStop;
		}
	}

	@Override
	public void run() {
		Object objResult = null;
		int n = 0;
		try {
			do {
				if (isStopRuning()) {
					return;
				}
				if (target != null) {
					try {
						objResult = target.onRun(this);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (isStopRuning()) {
					return;
				}
				if (objResult != null) {
					return;
				}
				try {
					Thread.sleep(300);
				} catch (Exception e) {
				}
			} while (n++ < 2 && !isStopRuning());
		} catch (Exception e) {
		} finally {
			mHandler.removeMessages(0);
			if (!isStopRuning() && objResult !=null) {
				mHandler.sendMessage(mHandler.obtainMessage(0, mID, 0,
						objResult));
			}
		}

	}

	private final Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			if (!isStopRuning()) {
				if (listener != null) {
					listener.onHttpForResult(msg.arg1, (HttpResultBeanBase)msg.obj,mRequestObj);
				}
			}
		}
	};

	// ////////////////////////////////////////////////////////////////////////////////
	//实现Run
	public interface IHttpRunnable {
		HttpResultBeanBase onRun(HttpThread t) throws Exception;
	}

	//回调返回值
	public interface IThreadResultListener {
		void onHttpForResult(int id, HttpResultBeanBase obj,Object requestObj);
	}
}
