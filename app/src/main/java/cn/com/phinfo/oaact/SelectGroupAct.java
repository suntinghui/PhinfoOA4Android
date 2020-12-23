package cn.com.phinfo.oaact;


import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.protocolbase.HttpResultBeanBase;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import cn.com.phinfo.adapter.GroupAdapter;
import cn.com.phinfo.oaact.base.SelectBaseAct;
import cn.com.phinfo.protocol.GroupRun;
import cn.com.phinfo.protocol.GroupRun.GroupItem;
import cn.com.phinfo.protocol.GroupRun.GroupResultBean;


public class SelectGroupAct extends SelectBaseAct{
	private GroupAdapter adapter = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTextNav("我的组");
		adapter = new GroupAdapter();
		refreshListView.setAdapter(adapter);
		this.onRefresh();
	}

	protected void onRefresh(){
		this.quickHttpRequest(ID_GETBUSINESSUNIT, new GroupRun());
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
				GroupResultBean o = (GroupResultBean)obj;
				adapter.replaceListRef(o.getListData());
			}else{
				this.showToast(obj.getMsg());
			}
		}
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		GroupItem it  = adapter.getItem(arg2-1);
		Intent intent = new Intent(this,SelectGroupUsersAct.class);
		intent.putExtra("GroupItem", JSON.toJSONString(it));
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
}
