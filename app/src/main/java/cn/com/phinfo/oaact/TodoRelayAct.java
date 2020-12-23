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
import cn.com.phinfo.protocol.NotifyReadRun;
import cn.com.phinfo.protocol.TodosRun.TodosItem;
import cn.com.phinfo.protocol.UnitandaddressRun.UnitandaddressItem;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.utils.ParamsCheckUtils;
import com.heqifuhou.view.MyGridView;


public class TodoRelayAct extends HttpMyActBase implements OnItemClickListener{
	private static final int ID_NOTIFY_READ = 0x16,ID_transferagent=0x17;
	protected static int ID_SUBMIT= 0x10,ID_SEL_PERSON = 0x12;
	private MyGridView approverGrid;
	protected SelectPersonGridAdapter selectPersonAdapter = null;
	protected EditText subject;
	private TodosItem it;
	private int NPOS = 0;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String title = this.getIntent().getExtras().getString("TITLE");
		this.addTextNav(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				submit(toMenberList(),subject.getText().toString());
			}
		}, title, "提交");
		this.addViewFillInRoot(R.layout.act_todo_relay);
		this.subject = (EditText) this.findViewById(R.id.subject);
		this.approverGrid = (MyGridView) this.findViewById(R.id.approverGrid);
		this.selectPersonAdapter = new SelectPersonGridAdapter();
		this.approverGrid.setAdapter(selectPersonAdapter);
		this.approverGrid.setOnItemClickListener(this);
		this.NPOS = this.getIntent().getExtras().getInt("NPOS");
		this.it = JSON.parseObject(this.getIntent().getExtras().getString("TodosItem"), TodosItem.class);
	}
	
	protected void onRefresh(){
	
	}

	private void submit(String menberList, String subject) {
		if(ParamsCheckUtils.isNull(menberList)){
			showToast("人员不能为空");
			return;
		}
		if(NPOS==0){
			quickHttpRequest(ID_NOTIFY_READ, new NotifyReadRun(it.getName(),it.getProcessInstanceId(),menberList,subject));
			return;
		}
		if(NPOS == 1){
			quickHttpRequest(ID_transferagent, new NotifyReadRun(it.getName(),it.getProcessInstanceId(),menberList,subject));
		}
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
		if(ID_transferagent == id){
			this.showToast(obj.getMsg());
			this.finish();
		}
		if(ID_NOTIFY_READ == id){
			this.showToast(obj.getMsg());
			this.finish();
		}
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
