package com.heqifuhou.actbase;

import com.heqifuhou.netbase.MyNetUtil;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.protocolbase.HttpThread.IThreadResultListener;
import com.heqifuhou.textdrawable.IConfigBuilder;
import com.heqifuhou.textdrawable.TextDrawable;
import com.heqifuhou.utils.ParamsCheckUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import cn.com.phinfo.oaact.R;

//网络相关的功能全集中到这儿了
public abstract class HttpMyActBase extends ThreadMyActBase implements
		IThreadResultListener, IHttpThreadListUtils {
	private static final String ACTION_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
	private static final int SETTING_ID = 0x99999999;
	private View dataError, NetError;
	private boolean mIsRefreshWhenNetChange = false; // 是否需要刷新当网络发生变化时
	private boolean bCheckNet = true;
	private static boolean bShowMobile = false;// 是否显示过

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	protected void onCreate(Bundle savedInstanceState, boolean bCheckNet) {
		super.onCreate(savedInstanceState);
		this.bCheckNet = bCheckNet;
		init();
	}

	private final void init() {
		dataError = this.findViewById(R.id.my_form_base_data_error);
		NetError = this.findViewById(R.id.my_form_base_net_error);
		dataError.findViewById(R.id.error_net_btn).setOnClickListener(
				clickListener);
		NetError.findViewById(R.id.error_netsetting_btn).setOnClickListener(
				clickListener);
		if (bCheckNet) {
			this.registerReceiver(updateNet, new IntentFilter(
					ACTION_CONNECTIVITY_CHANGE));
			// 如果网络未打开
			if (!MyNetUtil.IsCanConnectNet(this)) {
				this.setNetNotOpen();
				return;
			}
			if (this.isMobile()&&!bShowMobile) {
				this.showToast("使用的是手机流量");
				bShowMobile = true;
			}
		} else {
			setSuccessed();
		}
	}

	// 取得数据成功
	protected final void setSuccessed() {
		dataError.setVisibility(View.GONE);
		NetError.setVisibility(View.GONE);
		mRootViewGroup.setVisibility(View.VISIBLE);
	}

	// 网络未设置
	protected final void setNetNotOpen() {
		dataError.setVisibility(View.GONE);
		NetError.setVisibility(View.VISIBLE);
		mRootViewGroup.setVisibility(View.GONE);
		hideLoading();
		mIsRefreshWhenNetChange = true;
	}

	// 服务器获取数据失败，请重新刷新
	protected final void set404() {
		dataError.setVisibility(View.VISIBLE);
		NetError.setVisibility(View.GONE);
		mRootViewGroup.setVisibility(View.GONE);
		hideLoading();
	}

	// 是否是Mobile
	private boolean isMobile() {
		try{
			ConnectivityManager cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			State mobile = cManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
					.getState();
			if (mobile == State.CONNECTED || mobile == State.CONNECTING) {
				return true;
			}
		}catch(Exception e){
			
		}

		return false;
	}

	// 网络变化事件
	private final BroadcastReceiver updateNet = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				ConnectivityManager cManager = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo info = cManager.getActiveNetworkInfo();
				switch (info.getState()) {
				case CONNECTED:// 网络连接上
					if (mIsRefreshWhenNetChange) {
						setSuccessed();
						onRefresh();
						mIsRefreshWhenNetChange = false;
					}
					if (!bShowMobile) {
						if (isMobile()) {
							showToast(R.string.mobile_net_txt);
							bShowMobile = true;
						} else {
							bShowMobile = false;
						}
					}
					break;
				default:
					break;
				}
			} catch (Exception e) {
			}
		}
	};

	// 刷新事件
	protected void onRefresh() {
	}

	private final OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.error_net_btn:
				onRefresh();
				break;
			case R.id.error_netsetting_btn:
				MyNetUtil.SetSetting(HttpMyActBase.this, SETTING_ID);
				break;
			default:
				break;
			}
		}

	};

	@Override
	protected void onDestroy() {
		if (bCheckNet) {
			this.unregisterReceiver(updateNet);
		}

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


	public void onHttpForResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		this.setSuccessed();
		super.onHttpForResult(id, obj, requestObj);
	}

	// 图片的获取
	// ////////////////////////////////////////////////////////////////
	private static DisplayImageOptions options;

	protected final void getAsyncBitMap(final ImageView imgView,
			final String picUrl, final int nRes) {
		initOptions(nRes);
		ImageLoader.getInstance().displayImage(picUrl, imgView, options);
	}
	protected final void getAsyncAvatar(final ImageView imgView, final String picUrl,final String name){
		String _name = name;
		if(ParamsCheckUtils.isNull(_name)){
			_name = "";
		}
		if(_name.length()>1){
			_name = _name.substring(_name.length()-2);
		}
//		Drawable drawable = TextDrawable.builder().beginConfig().width(100).height(100).endConfig().buildRound(_name,Color.parseColor("#FFd6eae9"));
		Drawable drawable=TextDrawable.builder()
                .buildRound(_name,Color.parseColor("#FFd6eae9"));
		if(!ParamsCheckUtils.isNull(picUrl)){
			options = new DisplayImageOptions.Builder()
			//加载中
			.showImageOnLoading(drawable)
			//加载为空时
			.showImageForEmptyUri(drawable)
			//加载失入时
			.showImageOnFail(drawable)
			//内存缓存
			.cacheInMemory(false)
			//硬盘中缓存
			.cacheOnDisc(true).build();
			ImageLoader.getInstance().displayImage(picUrl, imgView,options);
		}else{
			imgView.setImageDrawable(drawable);
		}
	}
	protected final void getAsyncBitMap(final ImageView imgView,
			final String picUrl, final int nRes,
			final OnImageLoadCompleteListener listener) {
		if (ParamsCheckUtils.isNull(picUrl)) {
			imgView.setImageResource(nRes);
			return;
		}
		initOptions(nRes);
		ImageLoader.getInstance().displayImage(picUrl, imgView, options,
				new ImageLoadingListener() {
					@Override
					public void onLoadingStarted(String arg0, View arg1) {
					}

					@Override
					public void onLoadingFailed(String arg0, View arg1,
							FailReason arg2) {
					}

					@Override
					public void onLoadingComplete(String arg0, View arg1,
							Bitmap arg2) {
						if (listener != null) {
							listener.onImageLoadComplete();
						}
					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
					}
				});
	}

	private final void initOptions(final int nRes) {
		options = new DisplayImageOptions.Builder()
		// 加载中
				.showStubImage(nRes)
				// 加载为空时
				.showImageForEmptyUri(nRes)
				// 加载失入时
				.showImageOnFail(nRes)
				// 内存缓存
				.cacheInMemory(true)
				// 硬盘中缓存
				.cacheOnDisc(true).build();
	}

	public interface OnImageLoadCompleteListener {
		void onImageLoadComplete();
	}

}
