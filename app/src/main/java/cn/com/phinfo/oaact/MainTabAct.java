package cn.com.phinfo.oaact;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

import androidx.annotation.NonNull;
import cn.com.phinfo.entity.DataInstance;
import cn.com.phinfo.protocol.SysUpdateRun;
import cn.com.phinfo.protocol.SysUpdateRun.AppDownLoadResultBean;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.protocolbase.HttpThread;
import com.heqifuhou.protocolbase.HttpThread.IHttpRunnable;
import com.heqifuhou.protocolbase.HttpThread.IThreadResultListener;
import com.heqifuhou.tab.ThreadLoginMainTabActivityBase;
import com.heqifuhou.update.DonloadForceUtils;
import com.heqifuhou.update.DonloadForceUtils.OnCancelDownladListener;
import com.heqifuhou.utils.AppOnForegroundUtils;
import com.heqifuhou.utils.MyDeviceBaseUtils;
import com.heqifuhou.utils.ParamsCheckUtils;
import com.heqifuhou.view.BadgeView;
import com.heqifuhou.view.ConfirmDialog;
import com.heqifuhou.view.MyFlowRadioGroup;
import com.heqifuhou.view.MyFlowRadioGroup.OnCheckedChangeListener;
import com.umeng.analytics.MobclickAgent;

public class MainTabAct extends ThreadLoginMainTabActivityBase implements
		IBroadcastAction, IThreadResultListener {
	private static final int ID_PUSH = 0x10;
	private static final int ID_CONFIG = 0x20;
	private static final int ID_UPDATE = 0x21;
	private MyFlowRadioGroup radioGroup = null;
	private ConfirmDialog updateDialog = null;
	public static TabHost tabHost;
	private final String[] tagList = { "tab1", "tab2", "tab3", "tab4", "tab5" };
	private BadgeView badge0;
	private View badgeV0;
	private boolean isBackGroud = true;// 是否在后台
	private boolean bExit = false;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return onKeyBack();
		}
		return super.onKeyDown(keyCode, event);
	}

	private boolean onKeyBack() {
		if (bExit) {
			exit();
			finish();
			DataInstance.getInstance().setNull();
			return true;
		}
		bExit = true;
		mHandler.sendEmptyMessageDelayed(0x100, 2000);
		Toast.makeText(this, "若要退出请再按一下", Toast.LENGTH_LONG).show();
		return true;
	}

	private void exit() {
		Intent intent = new Intent(IBroadcastAction.ACTION_EXIT);
		this.sendBroadcast(intent);
	}

	private Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			bExit = false;
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.act_tabmain);
		this.update();
		this.initView();
		DonloadForceUtils.getInstance(this).addCancelDownladListener(
				new OnCancelDownladListener() {
					@Override
					public void onCancelDownladListener() {
					}
				});
		DonloadForceUtils.getInstance(this).registerDonloadServer();

		this.methodRequiresTwoPermission();

	}

	@Override
	public void onNewIntent(Intent arg0) {
		super.onNewIntent(arg0);
		setIntent(arg0);
	}

	public void update() {
		this.quickHttpRequest(ID_UPDATE, new SysUpdateRun());
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		// JPushInterface.onResume(this);
		if (backGroundHandler.hasMessages(0x10)) {
			backGroundHandler.removeMessages(0x10);
		}
		if (isBackGroud) {
			isBackGroud = false;
			this.remove1Px();
			// 从后台切换到前台时，试着请求更新。
			requestAction();
		}
	}

	private void requestAction() {
		// 发送推送
		// this.quickHttpRequest(ID_PUSH, new SetjpushidRun());
	}

	@Override
	protected void onStop() {
		super.onStop();
		MobclickAgent.onPause(this);
		// JPushInterface.onPause(this);
		if (!backGroundHandler.hasMessages(0x10)) {
			backGroundHandler.sendEmptyMessageDelayed(0x10, 2 * 1000);
		}
	}

	final private Handler backGroundHandler = new Handler() {
		public void handleMessage(Message msg) {
			// 进入后台了(关闭推送）
			if (!AppOnForegroundUtils.isAppOnForeground(MainTabAct.this)) {
				isBackGroud = true;
				add1Pix();
			} else {
				if (!backGroundHandler.hasMessages(0x10)) {
					backGroundHandler.sendEmptyMessageDelayed(0x10, 5 * 1000);
				}
			}
		}
	};

	private void initView() {
		final Class<?>[] actList = { Tab1GroupAct.class, Tab6GroupAct.class, Tab2GroupAct.class, Tab5GroupAct.class, Tab4GroupAct.class };
		tabHost = this.getTabHost();
		for (int i = 0; i < actList.length; i++) {
			Intent intent = new Intent(this, actList[i]);

			TabSpec ts = tabHost.newTabSpec(tagList[i])
					.setIndicator(tagList[i])
					.setContent(intent);
			tabHost.addTab(ts);
		}

		badgeV0 = findViewById(R.id.radio_button0_badge);

		badge0 = new BadgeView(this, badgeV0);

		radioGroup = (MyFlowRadioGroup) this.findViewById(R.id.main_radio);
		radioGroup.setOnCheckedChangeListener(ocheckedListener);
	}

	// 处理按钮组的事件
	private final OnCheckedChangeListener ocheckedListener = new OnCheckedChangeListener() {
		public void onCheckedChanged(MyFlowRadioGroup myFlowRadioGroup,	int checkedId) {
			switch (checkedId) {
				case R.id.radio_button0: // 消息
					tabHost.setCurrentTabByTag(tagList[0]);
					break;
				case R.id.radio_button6: // 日程
					tabHost.setCurrentTabByTag(tagList[1]);
					break;
				case R.id.radio_button1: // 工作
					tabHost.setCurrentTabByTag(tagList[2]);
					break;
				case R.id.radio_button5:// 通讯录
					tabHost.setCurrentTabByTag(tagList[3]);
					break;
				case R.id.radio_button3:// 我的
					tabHost.setCurrentTabByTag(tagList[4]);
					break;
			}
		}
	};

	protected void onBroadcastReceiverListener(Context context, Intent intent) {
		super.onBroadcastReceiverListener(context, intent);
	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if (id == ID_PUSH) {
			return;
		}
		// 升级
		if (id == ID_UPDATE) {
			if (obj instanceof AppDownLoadResultBean) {
				AppDownLoadResultBean o = (AppDownLoadResultBean) obj;
				if(MyDeviceBaseUtils.getCurrAppVer(this).equals(o.getVersion())){
					return;
				}
				String downLoadUrl = o.getDownloadUrl();
				if (!ParamsCheckUtils.isNull(downLoadUrl)) {
					DonloadForceUtils.getInstance(this).updateDialog(
							downLoadUrl);
				}
			}
		}
	}

	@Override
	public HttpThread quickHttpRequest(int id, IHttpRunnable runnable,
			IThreadResultListener listener, Object requestObj, boolean isRecy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		// Forward results to EasyPermissions
		EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
	}

	private static final int RC_WRITE_EXTERNAL_STORAGE = 100;

	@AfterPermissionGranted(RC_WRITE_EXTERNAL_STORAGE)
	private void methodRequiresTwoPermission() {
		String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
		if (EasyPermissions.hasPermissions(this, perms)) {
			// Already have permission, do the thing
			//表明已经授权，可以进行用户授予权限的操作
		} else {
			// Do not have permissions, request them now
			//弹出一个对话框进行提示用户
			EasyPermissions.requestPermissions(this, "请授予权限，否则影响部分使用功能", RC_WRITE_EXTERNAL_STORAGE, perms);

		}
	}
}
