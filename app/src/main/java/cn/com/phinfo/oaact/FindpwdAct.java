package cn.com.phinfo.oaact;

import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.utils.ParamsCheckUtils;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
public class FindpwdAct  extends HttpMyActBase implements OnClickListener {
	private EditText userNameEdit;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addTextNav("找回密码");
        this.addViewFillInRoot(R.layout.act_findpwd);
        this.userNameEdit = (EditText) this.findViewById(R.id.usr_name_edit);
       this.findViewById(R.id.btn).setOnClickListener(this);
    }

	@Override
	public void onClick(View arg0) {
		if(arg0.getId() == R.id.btn){
			findPwdAction();
			return;
		}
	}
	
	private void findPwdAction() {
		final String usrName = userNameEdit.getText().toString().trim();
		if (ParamsCheckUtils.isNull(usrName)) {
			this.showToast("手机号不能为空");
			return;
		}
//		this.quickHttpRequest(0x10, new ResetPasswordRun(usrName));
	}
	
	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if(obj.isOK()){
			finish();
		}
		showToast(obj.getMsg());
	}

}
