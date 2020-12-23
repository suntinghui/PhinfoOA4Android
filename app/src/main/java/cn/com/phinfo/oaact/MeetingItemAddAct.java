package cn.com.phinfo.oaact;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import cn.com.phinfo.protocol.MeetingItemAddRun;
import cn.com.phinfo.protocol.MeetingItemAddRun.MeetingItemResultBean;
import cn.com.phinfo.protocol.MeetingItemRun.MeetingItem;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.utils.ParamsCheckUtils;

public class MeetingItemAddAct extends HttpMyActBase {
	private static int ID_SUBMIT = 0x10;
	protected EditText subject, descripition;
	protected String id="";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTextNav(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				submit();
			}
		}, "添加议题", "提交");
		this.id = this.getIntent().getExtras().getString("ID");
		this.addViewFillInRoot(R.layout.act_metting_item_add);
		this.subject = (EditText) this.findViewById(R.id.subject);
		this.descripition = (EditText) this.findViewById(R.id.descripition);
	}

	protected void submit() {
		String _subject = subject.getText().toString().trim();
		String _descripition= descripition.getText().toString().trim();
		if(ParamsCheckUtils.isNull(_subject)){
			showToast("议题不能为空");
			return;
		}
		if(ParamsCheckUtils.isNull(_descripition)){
			showToast("议题内容不能为空");
			return;
		}
		this.quickHttpRequest(ID_SUBMIT, new MeetingItemAddRun(id,_subject,_descripition));
	}

	protected void onRefresh() {

	}



	public void onHttpForResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		super.onHttpForResult(id, obj, requestObj);

	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if (ID_SUBMIT == id) {
			if (obj.isOK()) {
				showToast("提交成功");
				MeetingItemResultBean o = (MeetingItemResultBean)obj;
				MeetingItem it = o.getData();
				Intent i = new Intent(IBroadcastAction.ACTION_MEETING_ITEM_ADD);
				i.putExtra("MeetingItem", JSON.toJSONString(it));
				sendBroadcast(i);
				this.finish();
			} else {
				this.showToast(obj.getMsg());
			}
		}
	}

	
}
