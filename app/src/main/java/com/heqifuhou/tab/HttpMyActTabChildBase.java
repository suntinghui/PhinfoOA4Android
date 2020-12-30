package com.heqifuhou.tab;

import com.heqifuhou.actbase.HttpLoginMyActBase;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.tab.base.AnimalResBase;
import com.heqifuhou.tab.base.OnTabActivityResultListener;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import cn.com.phinfo.oaact.R;

public class HttpMyActTabChildBase extends HttpLoginMyActBase implements
		AnimalResBase, OnTabActivityResultListener {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setDefaultAnimalRes();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;// 交由父类处理
		}
		return super.onKeyDown(keyCode, event);
	}
	protected void onResume(){
		super.onResume();
	}
	protected void onPause(){
		super.onPause();
	}
	public void startActivity(Intent intent) {
		try {
			Class<?> cls = Class.forName(intent.getComponent().getClassName());
			if (AnimalResBase.class.isAssignableFrom(cls)
					&& this.getParent() instanceof MyActTabGroupBaseAbs) {
				MyActTabGroupBaseAbs act = (MyActTabGroupBaseAbs) this
						.getParent();
				act.startSubActivity(intent, true);
				return;
			}
		} catch (Exception e) {
		}
		super.startActivityForResult(intent, -1);
		// super.startActivity(intent);
	}

	public void startActivityWithInTab(Intent intent) {
		startActivityForResultWithInTab(intent, -1);
	}

	public void startActivityForResultWithInTab(Intent intent, int requestCode) {
		try {
			Class<?> cls = Class.forName(intent.getComponent().getClassName());
			if (AnimalResBase.class.isAssignableFrom(cls)
					&& this.getParent() instanceof MyActTabGroupBaseAbs) {
				TabActivity act = (TabActivity) getTabActivity();
				act.startActivityForResult(intent, requestCode);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.startActivityForResult(intent, -1);
	}

	public void startActivityForResult(Intent intent, int requestCode) {
		try {
			if (this.getParent() instanceof MyActTabGroupBaseAbs) {
				TabActivity act = (TabActivity) getTabActivity();
				act.startActivityForResult(intent, requestCode);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.startActivityForResult(intent, -1);
	}

	protected TabActivity getTabActivity() {
		Activity actParent = this;
		while (actParent != null && !(actParent instanceof TabActivity)) {
			actParent = actParent.getParent();
		}
		return (TabActivity) actParent;
	}

	// private MainTabActivityBase getTabActivity(){
	// Activity actParent = this;
	// while(actParent!=null&&!(actParent instanceof MainTabActivityBase)){
	// actParent = actParent.getParent();
	// }
	// return (MainTabActivityBase) actParent;
	//
	// }

	public void finish() {
		try {
			if (this.getParent() instanceof MyActTabGroupBaseAbs) {
				MyActTabGroupBaseAbs act = (MyActTabGroupBaseAbs) this
						.getParent();
				act.onKeyBack();
				return;
			}
		} catch (Exception e) {
		}
		super.finish();
	}

	// 返回
	protected void goBack() {
		hideInputSoft();
		finish();
	}

	// ///////////////////////////////////////////////////////////////
	// 动画效果
	private int[] nPreAnimalRes = new int[] { -1, -1 };
	private int[] nNextAnimalRes = new int[] { -1, -1 };

	public void setPreInAndOutAnimalRes(final int nInAnimalRes,
			final int nOutAnimalRes) {
		this.nPreAnimalRes[0] = nInAnimalRes;
		this.nPreAnimalRes[1] = nOutAnimalRes;
	}

	public void setNextInAndOutAnimalRes(final int nInAnimalRes,
			final int nOutAnimalRes) {
		this.nNextAnimalRes[0] = nInAnimalRes;
		this.nNextAnimalRes[1] = nOutAnimalRes;
	}

	public int getPreInAnimalResID() {
		return nPreAnimalRes[0];
	}

	public int getPreOutAnimalResID() {
		return nPreAnimalRes[1];
	}

	public int getNextInAnimalResID() {
		return nNextAnimalRes[0];
	}

	public int getNextOutAnimalResID() {
		return nNextAnimalRes[1];
	}

	private void setDefaultAnimalRes() {
		this.setPreInAndOutAnimalRes(R.anim.in_lefttoright,
				R.anim.out_lefttoright);
		this.setNextInAndOutAnimalRes(R.anim.in_righttoleft,
				R.anim.out_righttoleft);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,Object requestObj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabActivityResult(int requestCode, int resultCode, Intent data) {
		onActivityResult(requestCode, resultCode, data);
	}

}
