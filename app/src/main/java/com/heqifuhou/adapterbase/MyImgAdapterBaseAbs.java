package com.heqifuhou.adapterbase;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import cn.com.phinfo.oaact.R;

import com.heqifuhou.textdrawable.TextDrawable;
import com.heqifuhou.utils.ParamsCheckUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public abstract class MyImgAdapterBaseAbs<E> extends MyAdapterBaseAbs<E> {
	private static DisplayImageOptions options;
	protected final DisplayImageOptions getDisplayImageOptions(final int nRes){
			options = new DisplayImageOptions.Builder()
			//加载中
			.showStubImage(nRes)
			//加载为空时
			.showImageForEmptyUri(nRes)
			//加载失入时
			.showImageOnFail(nRes)
			//内存缓存
			.cacheInMemory(false)
			//硬盘中缓存
			.cacheOnDisc(true).build();
		return options;
	}
	public MyImgAdapterBaseAbs() {
		super();
	}
	protected final void getAsyncBitMap(final ImageView imgView, final String picUrl)
	{
		this.getAsyncBitMap(imgView, picUrl,R.drawable.default_image_layer);//,R.drawable.default_img);
	}
	protected final void getAsyncBitMap(final ImageView imgView, final String picUrl,
			final int nRes) {
		if(!ParamsCheckUtils.isNull(picUrl)){
			ImageLoader.getInstance().displayImage(picUrl, imgView, getDisplayImageOptions(nRes));
		}else{
			imgView.setImageResource(nRes);
		}
	}
	final ImageLoadingListener listener = new ImageLoadingListener(){

		@Override
		public void onLoadingStarted(String imageUri, View view) {
		}
		@Override
		public void onLoadingFailed(String imageUri, View view,
				FailReason failReason) {
			if(imgRetryUrlCache.containsKey(view)){
				String name = imgRetryUrlCache.get(view);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				getAsyncAvatar((ImageView)view,imageUri,name);
				imgRetryUrlCache.remove(view);
			}
			
		}

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if(imgRetryUrlCache.containsKey(view)){
				imgRetryUrlCache.remove(view);
			}
		}

		@Override
		public void onLoadingCancelled(String imageUri, View view) {
			if(imgRetryUrlCache.containsKey(view)){
				imgRetryUrlCache.remove(view);
			}
		}  
      };
	protected final HashMap<Object,String> imgRetryUrlCache = new HashMap<Object,String>();
	protected final void getAsyncAvatar(final ImageView imgView, final String picUrl,final Drawable drawable){
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
			ImageLoader.getInstance().displayImage(picUrl, imgView,options,listener);
		}else{
			imgView.setImageDrawable(drawable);
		}
	}
	
	private ExecutorService executorService = Executors.newFixedThreadPool(5);   
	protected final void getAsyncAvatar(final ImageView imgView, final String picUrl,final String name){
		if(!ParamsCheckUtils.isNull(picUrl)){
			imgRetryUrlCache.put(imgView,name);
		}
        executorService.submit(new Runnable() {
            public void run() {
                try {
                	String _name = name;
            		if(ParamsCheckUtils.isNull(name)){
            			_name = "";
            		}
            		if(name.length()>1){
            			_name = name.substring(name.length()-2);
            		}
            		Drawable drawable= TextDrawable.builder()
                            .buildRound(_name,Color.parseColor("#FFd6eae9"));
            		getAsyncAvatar(imgView,picUrl,drawable);
                } catch (Exception e) {
                }
            }
        });
	}
}
