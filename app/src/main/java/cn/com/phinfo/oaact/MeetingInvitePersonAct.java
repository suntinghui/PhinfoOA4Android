package cn.com.phinfo.oaact;


import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import cn.com.phinfo.adapter.SelectPersonGridAdapter;
import cn.com.phinfo.entity.DataInstance;
import cn.com.phinfo.protocol.MeetingPeoplesInviteRun;
import cn.com.phinfo.protocol.UnitandaddressRun.UnitandaddressItem;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.utils.ParamsCheckUtils;
import com.heqifuhou.view.MyGridView;


public class MeetingInvitePersonAct extends HttpMyActBase implements OnItemClickListener{
	private static int ID_SUBMIT= 0x10,ID_SEL_PERSON = 0x12;
	private MyGridView approverGrid;
	protected SelectPersonGridAdapter selectPersonAdapter = null;
	protected String id="";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTextNav(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				submit();
			}
		}, "邀请参会", "提交");
		this.addViewFillInRoot(R.layout.act_meeting_invite);
		this.id = this.getIntent().getExtras().getString("ID");
		this.approverGrid = (MyGridView) this.findViewById(R.id.approverGrid);
		this.selectPersonAdapter = new SelectPersonGridAdapter();
		this.approverGrid.setAdapter(selectPersonAdapter);
		this.approverGrid.setOnItemClickListener(this);
	}
	
	protected void onRefresh(){
	
	}

	private void submit(){
		String approver = toMenberList();
		this.quickHttpRequest(ID_SUBMIT, new MeetingPeoplesInviteRun(id,approver));
	}
	
	private String toMenberList() {
		StringBuilder sb = new StringBuilder();
		List<UnitandaddressItem> ls = DataInstance.getInstance()
				.getUnitandaddressItemList();
		for (int i = 0; i < ls.size(); i++) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(ls.get(i).getSystemUserId());
		}
		return sb.toString();
	}
	
	public void onHttpForResult(int id, HttpResultBeanBase obj, Object requestObj) {
		super.onHttpForResult(id, obj, requestObj);
	
	}
	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj, Object requestObj) {
		if(ID_SUBMIT == id){
			if(obj.isOK()){
				Intent i = new Intent(IBroadcastAction.ACTION_MEETING_INVITE);
				sendBroadcast(i);
				this.showToast(obj.getMsg());
				this.finish();
			}else{
				this.showToast(obj.getMsg());
			}
			
		}
	}
	
	protected void onDestroy() {
		super.onDestroy();
		DataInstance.getInstance().getUnitandaddressItemList().clear();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (arg0.getAdapter() == selectPersonAdapter) {
			if (selectPersonAdapter.isImgShow(arg2)) {
				// 选人
				startSelectPersionAct();
			} else {

			}
		}
	}
	private void startSelectPersionAct() {
		Intent intent = new Intent(this, SelectPersonAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intent, ID_SEL_PERSON);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (ID_SEL_PERSON == requestCode) {
			List<UnitandaddressItem> ls = DataInstance.getInstance()
					.getUnitandaddressItemList();
			selectPersonAdapter.replaceListRef(ls);
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
