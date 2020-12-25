package cn.com.phinfo.oaact;


import com.heqifuhou.actbase.HttpLoginMyActBase;
import com.heqifuhou.protocolbase.HttpResultBeanBase;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import cn.com.phinfo.entity.DataInstance;
import cn.com.phinfo.protocol.LURLInterface;

/**
 * 打开应用启动的页面
 * zhujun
 */
public class SplashAct extends HttpLoginMyActBase {
	private static final int OPNE_MAIN_ACT = 1;
	private static final int DELAYMILLIS = 3000;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState,false);
		LURLInterface.init();
		this.addViewFillInRoot(R.layout.act_splash);
		h.sendEmptyMessageDelayed(OPNE_MAIN_ACT, DELAYMILLIS);
		DataInstance.getInstance();
	}

	private Handler h = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case OPNE_MAIN_ACT:
				openMainAct();
				break;
			default:
				break;
			}
		}
	};

	//打开主页面
	private void openMainAct() {
		Intent intent = new Intent(this,MainTabAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
		if(!this.startActivityWithLogin(intent)){
			finish();
		}
//		this.startActivity(intent);
		
	}

	protected void onActivityLoginOkResult(Object data) {
		super.onActivityLoginOkResult(data);
		//登陆成功关闭当前页面
		finish();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//闪屏页面，登陆页面下一个页面关闭时，自动关闭
		if (ID_LOGIN == requestCode && resultCode == RESULT_CANCELED) {
			finish();
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		// TODO Auto-generated method stub
		
	}
}
