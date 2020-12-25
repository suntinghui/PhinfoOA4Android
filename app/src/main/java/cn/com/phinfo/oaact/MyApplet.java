package cn.com.phinfo.oaact;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.heqifuhou.actbase.IBroadcastAction;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.tencent.smtt.sdk.QbSdk;

public class MyApplet extends  Application implements Thread.UncaughtExceptionHandler{
	private static MyApplet instance = null;
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		initImageLoader(getApplicationContext());

		this.initQb();


//        JPushInterface.setDebugMode(true);
//        JPushInterface.init(this);
//        JPushInterface.setLatestNotificationNumber(getApplicationContext(),1);
//
//		SDKInitializer.initialize(getApplicationContext());
	}

	// ////////////////////////////////////////////////////
	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.discCacheSize(50 * 1024 * 1024).discCacheFileCount(100)
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	// /////////////////////////////////////////////////////
	public static MyApplet getInstance() {
		return instance;
	}

	private void initQb() {
		//搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。

		QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

			@Override
			public void onViewInitFinished(boolean arg0) {
				// TODO Auto-generated method stub
				//x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
				Log.d("app", " onViewInitFinished is " + arg0);
			}

			@Override
			public void onCoreInitFinished() {
				// TODO Auto-generated method stub
			}
		};
		//x5内核初始化接口
		QbSdk.initX5Environment(getApplicationContext(),  cb);
	}


	@Override
	public void onLowMemory() {
		System.gc();
		super.onLowMemory();
	}

	@Override
	public void uncaughtException(Thread arg0, Throwable arg1) {
		sendBroadcast(new Intent(IBroadcastAction.ACTION_EXIT));
		Intent i = new Intent(this, SplashAct.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
	}
}
