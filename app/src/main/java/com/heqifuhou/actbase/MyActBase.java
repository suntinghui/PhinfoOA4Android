package com.heqifuhou.actbase;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import cn.com.phinfo.oaact.R;

import com.heqifuhou.utils.StaticReflectUtils;
import com.umeng.analytics.MobclickAgent;

public class MyActBase extends FragmentActivity implements IBroadcastAction {
	private ViewGroup mTitleGroup = null;// 标题
	protected ViewGroup mRootViewGroup = null;// 内容区
	protected ViewGroup mBottomGroup = null;// 标题
	private LayoutInflater mInflater = null;
	private View mLoading = null;// 加载
	private int mLoadingCount = 0;
	private View navRoot = null;

	public void onCreate(Bundle savedInstanceState) {
		try {
			if (savedInstanceState != null) {
				this.getIntent().putExtras(savedInstanceState);
			}
		} catch (Exception e) {
		}
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		registerReceiver();
		mInflater = (LayoutInflater) (getParent() != null ? this.getParent()
				: this).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		setContentView();
		mRootViewGroup = (ViewGroup) this
				.findViewById(R.id.my_form_base_context);
		mTitleGroup = (ViewGroup) this.findViewById(R.id.my_form_base_title);
		mBottomGroup = (ViewGroup) this.findViewById(R.id.my_form_base_bottom);
		mLoading = this.findViewById(R.id.my_form_base_loading);
	}

	protected void setContentView() {
		this.setContentView(R.layout.form_base);
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

	protected final void addViewFillInRoot(View v) {
		mRootViewGroup.addView(v, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
	}

	protected final void replaceFillInRoot(View v) {
		mRootViewGroup.removeAllViews();
		this.addViewFillInRoot(v);
	}

	protected final void replaceFillInRoot(final int nRes) {
		mRootViewGroup.removeAllViews();
		this.addViewFillInRoot(nRes);
	}

	private View liRoot = null;

	protected final void addViewFillInRoot(final int nRes) {
		liRoot = getLayoutInflater(nRes);
		addViewFillInRoot(liRoot);
	}

	// 影藏根目录
	protected void hideRootView() {
		if (liRoot != null) {
			if (liRoot instanceof ViewGroup) {
				ViewGroup vg = (ViewGroup) liRoot;
				for (int i = 0; i < vg.getChildCount(); i++) {
					vg.getChildAt(i).setVisibility(View.INVISIBLE);
				}
			} else {
				liRoot.setVisibility(View.INVISIBLE);
			}
		}
	}

	// 显示根目录
	protected void showRootView() {
		if (liRoot != null) {
			if (liRoot instanceof ViewGroup) {
				ViewGroup vg = (ViewGroup) liRoot;
				for (int i = 0; i < vg.getChildCount(); i++) {
					vg.getChildAt(i).setVisibility(View.VISIBLE);
				}
			} else {
				liRoot.setVisibility(View.VISIBLE);
			}
		}
	}

	// 添加标题
	protected final void addTitleView(final View v) {
		if (v != null) {
			showNav();
			mTitleGroup.removeAllViews();
			this.navRoot = v;
			mTitleGroup.addView(v, LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT);
		} else {
			hideNav();
		}
	}

	// 添加标题
	protected final void addTitleView(final int nRes) {
		View v = getLayoutInflater(nRes);
		addTitleView(v);
	}

	// 添加下面的菜单
	protected final void addBottomView(final int nRes) {
		View v = getLayoutInflater(nRes);
		addBottomView(v);
	}

	// 添加下面的菜单
	protected final void addBottomView(View v) {
		if (v != null) {
			mBottomGroup.setVisibility(View.VISIBLE);
			mBottomGroup.addView(v, new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT));
		} else {
			mBottomGroup.setVisibility(View.GONE);
		}
	}

	// 隐藏标题
	protected final void hideNav() {
		mTitleGroup.setVisibility(View.GONE);
	}

	protected final void showNav() {
		mTitleGroup.setVisibility(View.VISIBLE);
	}

	protected final void hideBottom() {
		mBottomGroup.setVisibility(View.GONE);
	}

	protected final void showBottom() {
		mBottomGroup.setVisibility(View.VISIBLE);
	}

	// ///////////////////////////////////////////////////////////////////////////
	protected void onDestroy() {
		this.unregisterReceiver(broadCastRev);
		super.onDestroy();
	}

	public void onConfigurationChanged(Configuration newConfig) {
		try {
			super.onConfigurationChanged(newConfig);
			if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				// land
			} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				// port
			}
		} catch (Exception ex) {
		}
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

	// 生成view
	protected final View getLayoutInflater(final int nRes) {
		return mInflater.inflate(nRes, null);
	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK) {
	// return false;// 交由父类处理
	// }
	// return super.onKeyDown(keyCode, event);
	// }
	// /////////////////////////////////////////////////////////////////////////////////////////////
	private final String getResString(final int nResTxt) {
		String t = "";
		if (nResTxt > 0) {
			t = getResources().getString(nResTxt);
		}
		return t;
	}

	protected final void addTextNav(final OnClickListener navBack,
			final OnClickListener rightBtnListener, final String titleText,
			final String btnText, final int nResBtn) {
		if (navRoot == null) {
			if (nResBtn > 0) {
				View v = getLayoutInflater(R.layout.nav_img_btn);
				this.addTitleView(v);
			}else{
				View v = getLayoutInflater(R.layout.nav_title_btn);
				this.addTitleView(v);
			}
		}
		if (nResBtn > 0) {
			View v = navRoot.findViewById(R.id.nav_btn_img);
			if(v!=null&&v instanceof ImageView){
				ImageView btnimg = (ImageView)v;
				btnimg.setOnClickListener(rightBtnListener);
				btnimg.setImageResource(nResBtn);
				btnimg.setVisibility(View.VISIBLE);
			}
		}else{
			View v = navRoot.findViewById(R.id.nav_btn);
			if(v!=null&&v instanceof TextView){
				TextView txt = (TextView) v;
				txt.setText(btnText);
				txt.setOnClickListener(rightBtnListener);
				txt.setVisibility(View.VISIBLE);
			}
		}
		View v = navRoot.findViewById(R.id.nav_back);
		if(v!=null){
			v.setOnClickListener(navBack);
		}
		v =  navRoot.findViewById(R.id.nav_text);
		if(v!=null&&v instanceof TextView){
			TextView navText = (TextView)v; 
			navText.setText(titleText);
		}
	}

	protected final void addTextNav(final OnClickListener rightBtnListener,
			final String titleText, final int nResBtn) {
		this.addTextNav(onBackListener, rightBtnListener, titleText, "",
				nResBtn);
	}

	protected final void addTextNav(final OnClickListener rightBtnListener,
			final String titleText, final String btnText) {
		this.addTextNav(onBackListener, rightBtnListener, titleText, btnText,
				-1);
	}

	protected final void hideBackNav() {
		View v = this.findViewById(R.id.nav_back);
		if (v != null) {
			v.setVisibility(View.INVISIBLE);
		}
	}

	protected final void addTextNav(final String titleText) {
		this.addTextNav(onBackListener, null, titleText, "", -1);
	}

	protected final OnClickListener onBackListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// hideInputSoft();
			finish();
		}
	};

	protected final OnClickListener rightBtnListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

		}
	};

	// /////////////////////////////////////////////////////////////////////////////////////////////

	// 弱显示3S
	public final void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	public final void showToast(final int nRes) {
		String t = "";
		if (nRes > 0) {
			t = getResources().getString(nRes);
		}
		showToast(t);
	}

	// ///////////////////////////////////////////////////////////////////////
	// 显示正载加载
	public final void showLoading() {
		mLoadingCount++;
		mLoading.setVisibility(View.VISIBLE);
	}

	// 不显示加载项
	public final void hideLoading() {
		this.hideLoading(false);
	}

	// 不显示加载项
	public final void hideLoading(boolean bForceHide) {
		mLoadingCount--;
		if (bForceHide) {
			mLoadingCount = 0;
		}
		if (mLoadingCount <= 0) {
			mLoading.setVisibility(View.GONE);
		}
	}

	// ///////////////////////////////////////////////////////////////////////
	protected final void exit() {
		this.sendBroadcast(new Intent(IBroadcastAction.ACTION_EXIT));
	}

	// ///////////////////////////////////////////////////////////////////////
	// 隐藏输入法
	protected final void hideInputSoft() {
		try {
			// View view = getWindow().peekDecorView();
			// if (view != null && getCurrentFocus() != null
			// && getCurrentFocus().getApplicationWindowToken() != null) {
			InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			if (im.isActive()) {
				// 显示或者隐藏输入法
				im.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}
			// im.hideSoftInputFromWindow(getCurrentFocus()
			// .getApplicationWindowToken(),
			// InputMethodManager.HIDE_NOT_ALWAYS);
			// }
		} catch (Exception e) {
		}

	}

	// 关闭出入法窗口
	protected final void hideInputSoft(View v) {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		// 接受软键盘输入的编辑文本或其它视图
		inputMethodManager.showSoftInput(v, InputMethodManager.SHOW_FORCED);
	}

	// 打开输入法
	protected final void showInputSoft(View v) {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// 接受软键盘输入的编辑文本或其它视图
		inputMethodManager.showSoftInput(v, InputMethodManager.SHOW_FORCED);
	}

	// 打开输入法
	protected final void showInputSoft() {
		try {
			// View view = getWindow().peekDecorView();
			// if (view != null && getCurrentFocus() != null
			// && getCurrentFocus().getApplicationWindowToken() != null) {
			InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			if (!im.isActive()) {
				im.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}
			// }
		} catch (Exception e) {
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		try {
			if (this.getIntent() != null
					&& this.getIntent().getExtras() != null) {
				Bundle b = this.getIntent().getExtras();
				savedInstanceState.putAll(b);
			}
		} catch (Exception e) {
		}

	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	// /////////////////////////////////////////////////////////////////
	// 退出登陆
	private boolean bExit = false;

	protected boolean onKeyBack() {
		if (bExit) {
			exit();
			finish();
			// DataInstance.getInstance().setNull2DB();
			return true;
		}
		bExit = true;
		mHandler.sendEmptyMessageDelayed(0x100, 2000);
		this.showToast("若要退出请再按一下");
		return true;
	}

	private Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			bExit = false;
		}
	};

	protected final TextView getEmptyView(String str, int color) {
		if (emptyView != null) {
			removeEmptyView();
		} else {
			View v = this.getLayoutInflater(R.layout.empty);
			emptyView = (TextView) v.findViewById(R.id.empty);
		}
		emptyView.setText(str);
		emptyView.setTextColor(color);
		return emptyView;
	}

	protected final void removeEmptyView() {
		if (emptyView != null) {
			if (emptyView.getParent() != null) {
				((ViewGroup) emptyView.getParent()).removeView(emptyView);
			}
		}
	}

	private TextView emptyView;

	protected final TextView getEmptyView(int color) {
		return getEmptyView("没有内容,下拉刷新试试", color);
	}

	protected final TextView getEmptyView() {
		return getEmptyView("没有内容,下拉刷新试试", 0xff333333);
	}

	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
