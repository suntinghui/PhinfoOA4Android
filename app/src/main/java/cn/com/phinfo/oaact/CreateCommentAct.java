package cn.com.phinfo.oaact;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import cn.com.phinfo.protocol.DoCommentAddRun;

import com.heqifuhou.actbase.HttpLoginMyActBase;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.utils.ParamsCheckUtils;

public class CreateCommentAct extends HttpLoginMyActBase {
	private int ID_SEND_NEW = 0x12;
	private EditText edit;
	private String objectid;
	private String parentid;
	private String objTypeCode;
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.objectid = this.getIntent().getExtras().getString("OBJECTID","");
		this.parentid = this.getIntent().getExtras().getString("PARENTID","");
		this.objTypeCode = this.getIntent().getExtras().getString("OBJTYPECODE", "");
		this.addTextNav(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				sendNew();
			}
		},"发表评论","发送");
		this.addViewFillInRoot(R.layout.act_create_report_comment);
		edit = (EditText) this.findViewById(R.id.edit);
	}
	//创建新
	public void sendNew(){
		String content = edit.getText().toString();
		if(ParamsCheckUtils.isNull(content)){
			showToast("内容不能为空");
			return;
		}
		this.quickHttpRequest(ID_SEND_NEW, new DoCommentAddRun(this.objTypeCode,this.objectid,this.parentid,content));
	}
	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if(id==ID_SEND_NEW){
			if(obj.isOK()){
				showToast(obj.getMsg());
				this.finish();
				if("5500".equals(this.objTypeCode)){
					this.sendBroadcast(new Intent(IBroadcastAction.ACTION_REPORT_COMM));
				}else if("6000".equals(this.objTypeCode)){
					this.sendBroadcast(new Intent(IBroadcastAction.ACTION_SHARE_COMM));
				}
			}else{
				showToast(obj.getMsg());
			}
			return;
		}

	}
}
