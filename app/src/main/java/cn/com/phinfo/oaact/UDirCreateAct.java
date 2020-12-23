package cn.com.phinfo.oaact;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import cn.com.phinfo.protocol.FileSearchRun.UFileItem;
import cn.com.phinfo.protocol.UDirCreateRun;
import cn.com.phinfo.protocol.UDirCreateRun.UDirCreateResultBean;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.utils.ParamsCheckUtils;

public class UDirCreateAct extends HttpMyActBase {
	private EditText text;
	private String parentID;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		parentID = this.getIntent().getExtras().getString("parentID");
		super.onCreate(savedInstanceState);
		this.addTextNav(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				submit();
			}
		},"新建文件夹","完成");
		this.addViewFillInRoot(R.layout.act_u_create);
		text = (EditText) this.findViewById(R.id.subname);
	}
	
	private void submit(){
		String name = text.getText().toString().trim();
		if(ParamsCheckUtils.isNull(name)){
			showToast("文件夹名字不能为空");
			return;
		}
		this.quickHttpRequest(0x10, new UDirCreateRun(parentID, name));
	}
	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if(id == 0x10){
			if(obj.isOK()){
				UDirCreateResultBean o = (UDirCreateResultBean)obj;
				UFileItem it  = o.getData();
				Intent i = new Intent(IBroadcastAction.ACTION_U_CREATE);
				i.putExtra("UFileItem", JSON.toJSONString(it));
				sendBroadcast(i);
				finish();
			}else{
				showToast(obj.getMsg());
			}
		}
	}
}
