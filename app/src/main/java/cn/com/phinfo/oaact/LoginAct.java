package cn.com.phinfo.oaact;

import com.heqifuhou.actbase.HttpLoginMyActBase;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.utils.ParamsCheckUtils;
import com.heqifuhou.utils.SystemUtils;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import cn.com.phinfo.db.SettingDB;
import cn.com.phinfo.entity.DataInstance;
import cn.com.phinfo.protocol.LURLInterface;
import cn.com.phinfo.protocol.LoginRun;
import cn.com.phinfo.protocol.LoginRun.LoginResultBean;

//登录
public class LoginAct extends HttpLoginMyActBase implements OnClickListener {
	private final int ID_REGISTER = 0x20;
	private EditText userNameEdit;
	private EditText pwdEdit;
	private boolean bCkecked = true;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, false);
		LURLInterface.init();
		this.addViewFillInRoot(R.layout.act_login);
		this.hideNav();
		this.userNameEdit = (EditText) this.findViewById(R.id.usr_name_edit);
		this.pwdEdit = (EditText) this.findViewById(R.id.usr_pwd_edit);
		this.findViewById(R.id.login_find_password).setOnClickListener(this);
		this.findViewById(R.id.btn_server).setOnClickListener(this);
		this.findViewById(R.id.btn_setting).setOnClickListener(this);
		this.findViewById(R.id.eye_btn).setOnClickListener(this);

		
		View v = this.findViewById(R.id.btn);
		v.setOnClickListener(this);
		SystemUtils.pressEffect(v);
		//用户名和密码
		userNameEdit.setText(DataInstance.getInstance().getName());
		pwdEdit.setText(DataInstance.getInstance().getPwd());
		
//		userNameEdit.setText("ge");
//		pwdEdit.setText("666666");
		if(SettingDB.getInstance().getFromDB().isEmpty()){
			Intent intent = new Intent(this,SettingAct.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	}

	@Override
	public void onClick(View v) {
		//登陆
		if (v.getId() == R.id.btn) {
			loginAction();
			return;
		}
		//忘记密码
		if(v.getId() == R.id.login_find_password){
			Intent intent = new Intent(this,FindpwdAct.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return;
		}
		//设置
		if(v.getId() == R.id.btn_setting){
			Intent intent = new Intent(this,SettingAct.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return;
		}
		//切换密码是否隐藏
		if(v.getId() == R.id.eye_btn){
			bCkecked = !bCkecked;
			if(!bCkecked){
				//隐藏
				this.pwdEdit.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
			}else{
				//显示密码
				this.pwdEdit.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			}
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (ID_REGISTER == requestCode && resultCode == RESULT_OK) {
			// 登录成功改变这个页面的状态
			this.setResult(RESULT_OK);
			finish();
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
//	
//	//打开主页面
//	private void openMainAct() {
//		Intent intent = new Intent(this,MainAct.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
//		if(!this.startActivityWithLogin(intent)){
//			finish();
//		}
//		
//	}

	private void loginAction() {
		if(ParamsCheckUtils.isNull(LURLInterface.getUrlBase())){
			this.showToast("请在设置里选择主机");
			return;
		}

		final String usrName = userNameEdit.getText().toString().trim();
		final String pwd = pwdEdit.getText().toString().trim();

		if (ParamsCheckUtils.isNull(usrName)) {
			this.showToast("用户名不能为空");
			return;
		}

		if (ParamsCheckUtils.isNull(pwd)) {
			this.showToast("密码不能为空");
			return;
		}
		this.quickHttpRequest(0x10, new LoginRun(usrName, pwd));
	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if (obj.isOK()) {
			LoginResultBean o = (LoginResultBean) obj;
			save2DB(o);
			this.setResult(RESULT_OK);
			this.finish();
		} else {
			this.showToast(obj.getMsg());
		}
	}

	private void save2DB(LoginResultBean o) {
		final String usrName = userNameEdit.getText().toString().trim();
		final String pwd = pwdEdit.getText().toString().trim();
		// 把body的json直接转换成对象
		LoginResultBean userBean = o;
		DataInstance.getInstance().saveUser(usrName, pwd, userBean);
	}
	

}
