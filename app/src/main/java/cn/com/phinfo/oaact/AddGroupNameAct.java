package cn.com.phinfo.oaact;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import cn.com.phinfo.protocol.GroupCreateRun;

import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.utils.ParamsCheckUtils;

//添加组
public class AddGroupNameAct extends HttpMyActBase {
	private EditText text;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTextNav("添加组");
		this.addViewFillInRoot(R.layout.act_add_group);
		text = (EditText) this.findViewById(R.id.usr_name_edit);
		this.findViewById(R.id.btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				submit();
			}
		});
	}
	private void submit(){
		String s = text.getText().toString().trim();
		if(ParamsCheckUtils.isNull(s)){
			showToast("组名不能为空");
			return;
		}
		this.quickHttpRequest(0x10, new GroupCreateRun(s));
	}
	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if(obj.isOK()){
			sendBroadcast(new Intent(IBroadcastAction.ACTION_ADD_GROUP));
			this.finish();
		}else{
			showToast(obj.getMsg());
		}
	}
}
