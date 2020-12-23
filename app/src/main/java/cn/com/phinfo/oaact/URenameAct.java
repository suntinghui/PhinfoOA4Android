package cn.com.phinfo.oaact;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import cn.com.phinfo.protocol.FileSearchRun.UFileItem;
import cn.com.phinfo.protocol.RenameDirRun;
import cn.com.phinfo.protocol.RenameFileRun;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.utils.ParamsCheckUtils;

public class URenameAct extends HttpMyActBase {
	private EditText text;
	private UFileItem it;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		String s = this.getIntent().getExtras().getString("UFileItem");
		it = JSON.parseObject(s, UFileItem.class);
		super.onCreate(savedInstanceState);
		this.addTextNav(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				submit();
			}
		},"重命名","完成");
		this.addViewFillInRoot(R.layout.act_u_rename);
		text = (EditText) this.findViewById(R.id.subname);
	}
	
	private void submit(){
		String name = text.getText().toString().trim();
		if(ParamsCheckUtils.isNull(name)){
			showToast("新的名字不能为空");
			return;
		}
		if(it.isFloder()){
			this.quickHttpRequest(0x10, new RenameDirRun(it.getId(), name));
		}else{
			this.quickHttpRequest(0x10, new RenameFileRun(it.getId(), name));
		}
	}
	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if(id == 0x10){
			if(obj.isOK()){
				finish();
				Intent i = new Intent(IBroadcastAction.ACTION_U_RENAME);
				it.setName(text.getText().toString().trim());
				i.putExtra("UFileItem", JSON.toJSONString(it));
				sendBroadcast(i);
			}else{
				showToast(obj.getMsg());
			}
		}
	}
}
