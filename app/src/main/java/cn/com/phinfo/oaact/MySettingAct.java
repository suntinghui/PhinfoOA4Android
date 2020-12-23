package cn.com.phinfo.oaact;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import cn.com.phinfo.protocol.SysUpdateRun;
import cn.com.phinfo.protocol.SysUpdateRun.AppDownLoadResultBean;

import com.heqifuhou.actbase.HttpLoginMyActBase;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.update.DonloadForceUtils;
import com.heqifuhou.utils.MyDeviceBaseUtils;
import com.heqifuhou.utils.ParamsCheckUtils;

public class MySettingAct extends HttpLoginMyActBase implements OnClickListener {
	private static int ID_UPDATE = 0x10;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, false);
		this.addTextNav("设置");
		this.addViewFillInRoot(R.layout.act_mysetting);
		this.findViewById(R.id.about).setOnClickListener(this);
		this.findViewById(R.id.update).setOnClickListener(this);
		DonloadForceUtils.getInstance(this).registerDonloadServer();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.about:
			startAboutAction();
			break;
		case R.id.update:
			this.quickHttpRequest(ID_UPDATE, new SysUpdateRun());
			break;
		default:
			break;
		}
	}

	private void startAboutAction() {
		Intent intent = new Intent(this, AboutAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
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
				}else{
					showToast("已经是最新版本，无需升级");
				}
			}
		}

	}
}
