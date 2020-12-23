package cn.com.phinfo.oaact;


import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.protocolbase.HttpResultBeanBase;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import cn.com.phinfo.adapter.RolesAdapter;
import cn.com.phinfo.oaact.base.SelectBaseAct;
import cn.com.phinfo.protocol.RolesRun;
import cn.com.phinfo.protocol.GroupRun.GroupItem;
import cn.com.phinfo.protocol.RolesRun.RolesItem;
import cn.com.phinfo.protocol.RolesRun.RolesResultBean;


public class SelectRolesAct extends SelectBaseAct{
	private RolesAdapter adapter = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTextNav("角色");
		adapter = new RolesAdapter();
		refreshListView.setAdapter(adapter);
		this.onRefresh();
	}

	protected void onRefresh(){
		this.quickHttpRequest(ID_GETBUSINESSUNIT, new RolesRun());
	}
	protected void onBroadcastReceiverListener(Context context, Intent intent) {
		super.onBroadcastReceiverListener(context, intent);
		if(intent.getAction().equals(IBroadcastAction.ACTION_USRE_SEL)){
			finish();
		}
	};
	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj, Object requestObj) {
		if(ID_GETBUSINESSUNIT == id){
			if(obj.isOK()){
				RolesResultBean o = (RolesResultBean)obj;
				adapter.replaceListRef(o.getListData());
			}else{
				this.showToast(obj.getMsg());
			}
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		RolesItem it  = adapter.getItem(arg2-1);
		Intent intent = new Intent(this,SelectRolesUsersAct.class);
		intent.putExtra("RolesItem", JSON.toJSONString(it));
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}
