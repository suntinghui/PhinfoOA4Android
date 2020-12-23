package cn.com.phinfo.oaact;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import cn.com.phinfo.db.LoginDB;
import cn.com.phinfo.entity.DataInstance;
import cn.com.phinfo.protocol.ChangMobileRun;

import com.heqifuhou.actbase.HttpLoginMyActBase;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.utils.ParamsCheckUtils;

public class ChangeMobileAct extends HttpLoginMyActBase implements OnClickListener {
	private EditText usr_name_edit;
	private String MOBILE="";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.replaceFillInRoot(R.layout.act_change_mobile);
		MOBILE = this.getIntent().getExtras().getString("MOBILE");
		this.addTextNav("修改密码");
		usr_name_edit = (EditText) this.findViewById(R.id.usr_name_edit);
	    this.findViewById(R.id.btn).setOnClickListener(this);
	    usr_name_edit.setText(MOBILE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn:
			submit();
			break;

		default:
			break;
		}

	}

	private void submit() {
		String mobile = usr_name_edit.getText().toString().trim();
		if (ParamsCheckUtils.isNull(mobile)) {
			this.showToast("手机号码不能为空");
			return;
		}
		this.quickHttpRequest(0x10, new ChangMobileRun(mobile));
	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,Object requestObj) {
		if (obj.isOK()) {
//			String mobile = usr_name_edit.getText().toString().trim();
//			String pwd = DataInstance.getInstance().getPwd();
//			DataInstance.getInstance().saveUser(mobile,pwd,LoginDB.getInstance().getUserBean());
			this.showToast("修改成功");
			this.finish();
		} else {
			this.showToast("修改失败");
		}
	}

}
