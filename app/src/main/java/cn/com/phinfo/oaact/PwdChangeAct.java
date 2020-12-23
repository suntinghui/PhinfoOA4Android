package cn.com.phinfo.oaact;

import com.heqifuhou.actbase.HttpLoginMyActBase;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.utils.ParamsCheckUtils;
import com.heqifuhou.utils.SystemUtils;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import cn.com.phinfo.protocol.LoginRun.LoginResultBean;

//修改密码
public class PwdChangeAct extends HttpLoginMyActBase implements OnClickListener {
	private final int ID_CHANGE = 0x10;
	private EditText old_pwd_edit,new_pwd_edit;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, false);
		this.addTextNav("修改密码");
		this.addViewFillInRoot(R.layout.act_change_pwd);
		this.old_pwd_edit = (EditText) this.findViewById(R.id.old_pwd_edit);
		this.new_pwd_edit = (EditText) this.findViewById(R.id.new_pwd_edit);
		
		
		View v = this.findViewById(R.id.btn);
		v.setOnClickListener(this);
		SystemUtils.pressEffect(v);
	}

	@Override
	public void onClick(View v) {
		//登陆 
		if (v.getId() == R.id.btn) {
			changePwdAction();
		}
	}

	private void changePwdAction() {
		final String oldPwdstr = old_pwd_edit.getText().toString().trim();
		final String newPwdstr = new_pwd_edit.getText().toString().trim();
		if(ParamsCheckUtils.isNull(oldPwdstr)){
			this.showToast("旧密码不能为空");
			return;
		}
		if(ParamsCheckUtils.isNull(newPwdstr)){
			this.showToast("新密码不能为空");
			return;
		}
//		this.quickHttpRequest(ID_CHANGE,new UpdateUserPasswordRun(oldPwdstr,newPwdstr));
	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if(ID_CHANGE == id){
			if (obj.isOK()) {
				LoginResultBean o = (LoginResultBean) obj;
				LoginResultBean userBean = o;
//				DataInstance.getInstance().saveUser(userBean.getUsername(), new_pwd_edit.getText().toString().trim(), userBean);
				this.showToast("修改密码成功");
				this.finish();
			} else{
				this.showToast(obj.getMsg());
			} 
		}
	}


}
