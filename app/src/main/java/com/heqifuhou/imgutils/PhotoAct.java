package com.heqifuhou.imgutils;

import com.heqifuhou.actbase.MyActBase;
import com.heqifuhou.adapterbase.PagerAdapterBase;
import com.heqifuhou.utils.ImageToGalleryUtils;
import com.heqifuhou.utils.ParamsCheckUtils;
import com.heqifuhou.view.PopupDialog;
import com.heqifuhou.view.PopupDialog.OnDialogItemListener;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.ViewPager;
import cn.com.phinfo.oaact.R;

//查看已选择照片
public class PhotoAct extends MyActBase {
	private ViewPager pager;
	private MyPageAdapter adapter;
	private int indexPager;
	private ViewGroup photo_relativeLayout;
	private boolean isNoDel = false;
	private LinearLayout indicator;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addViewFillInRoot(R.layout.act_photo);
		if (this.getIntent() != null && this.getIntent().getExtras() != null) {
			isNoDel = this.getIntent().getExtras().getBoolean("isNoDel", false);
			this.getIntent().getExtras().getSerializable("BitmapUploadItem");
		}
		photo_relativeLayout = (RelativeLayout) findViewById(R.id.photo_relativeLayout);
		photo_relativeLayout.setBackgroundColor(0x70000000);
		findViewById(R.id.photo_bt_exit).setOnClickListener(photoListener);
		indicator = (LinearLayout) findViewById(R.id.indicator);
		if (isNoDel) {
			findViewById(R.id.photo_bt_del).setVisibility(View.INVISIBLE);
		} else {
			findViewById(R.id.photo_bt_del).setOnClickListener(photoListener);
		}
		pager = (ViewPager) findViewById(R.id.viewpager);
		pager.setOnPageChangeListener(pageChangeListener);
		adapter = new MyPageAdapter();// 构造adapter
		adapter.replaceListRef(BitmapDataListInstanceUtils.getRefInstance()
				.getListRef());

		pager.setAdapter(adapter);// 设置适配器
		indexPager = getIntent().getIntExtra("ID", 0);
		addPoint();
		if (indexPager >= 0 && indexPager < adapter.getCount()) {
			pager.setCurrentItem(indexPager);
		}
	}

	protected void onPause() {
		super.onPause();
	}

	protected void onResume() {
		super.onResume();
		adapter.notifyDataSetChanged();
	}

	private final ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
		public void onPageSelected(int arg0) {// 页面选择响应函数
			indexPager = arg0;
			selPoint();
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {// 滑动中。。。

		}

		public void onPageScrollStateChanged(int arg0) {// 滑动状态改变

		}
	};

	// 显示点
	private void selPoint() {
		for (int i = 0; i < adapter.getCount(); i++) {
			ImageView imageView = (ImageView) indicator.getChildAt(i);
			imageView.setSelected(indexPager == i);
		}
	}

	// 添加几个点
	private void addPoint() {
		indicator.removeAllViews();
		for (int i = 0; i < adapter.getCount(); i++) {
			ImageView imageView = new ImageView(this);
			imageView
					.setBackgroundResource(R.drawable.home_banner_indicator_selector);
			imageView.setSelected(i == 0);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			if (i != 0) {
				params.leftMargin = 15;
			}
			indicator.addView(imageView, params);
		}
	}

	private OnClickListener photoListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.photo_bt_exit:
				finish();
				break;
			case R.id.photo_bt_del:
				if (BitmapDataListInstanceUtils.getRefInstance() != null) {
					BitmapDataListInstanceUtils.getRefInstance().Del(indexPager);
					adapter.notifyDataSetChanged();
					if (BitmapDataListInstanceUtils.getRefInstance()
							.getListRef().size() <= 0) {
						finish();
					}
				}
				break;
			}
		}
	};

	private final class MyPageAdapter extends PagerAdapterBase<FileItem> {
		public void destroyItem(View arg0, int arg1, Object arg2) {// 销毁view对象
			((ViewPager) arg0).removeView((View) arg2);
		}

		public void finishUpdate(View arg0) {
		}

		public Object instantiateItem(final View arg0, int arg1) {// 返回view对象
			ImageView img = null;
			try {
				FileItem it = this.getListRef().get(arg1);
				img = new ImageView(arg0.getContext());// 构造textView对象
				img.setBackgroundColor(0xff000000);

				img.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
						LayoutParams.FILL_PARENT));
				((ViewPager) arg0).addView(img);
				if (ParamsCheckUtils.isNull(it.getFileUrl())
						|| !it.getFileUrl().startsWith("http:")) {
					this.getAsyncBitMap(img, it.getLocalFileUri());
				} else {
					this.getAsyncBitMap(img, it.getFileUrl());
				}
				img.setOnLongClickListener(new OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						//选择键
						PopupDialog pop = new PopupDialog(PhotoAct.this,v,new String[]{"保存图片"});
						pop.setOnDialogItemListener(new OnDialogItemListener() {
							@Override
							public void onDialogItem(int pos, Object obj) {
								if(pos == 0){
									try{
										ImageView _img = (ImageView)obj;
										_img.setDrawingCacheEnabled(true);
										Bitmap bitmap=((BitmapDrawable)_img.getDrawable()).getBitmap();   
										ImageToGalleryUtils.saveImageToGallery(PhotoAct.this, bitmap);
										_img.setDrawingCacheEnabled(false);
									}catch(Exception e){
										
									}

								}
							}
						});
						pop.show();
						return true;
					}
				});
			} catch (Exception e) {
			}
			return img;
		}

		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
	}

}
