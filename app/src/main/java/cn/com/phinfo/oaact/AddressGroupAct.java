package cn.com.phinfo.oaact;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.com.phinfo.adapter.GroupAdapter;
import cn.com.phinfo.protocol.AddressGroupRun;
import cn.com.phinfo.protocol.GroupRun.GroupItem;
import cn.com.phinfo.protocol.GroupRun.GroupResultBean;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.HttpLoginMyActBase;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshListView;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;

public class AddressGroupAct extends HttpLoginMyActBase implements OnItemClickListener{
	protected static int ID_GETBUSINESSUNIT = 0x10;
	protected PullToRefreshListView refreshListView = null;
	private GroupAdapter adapter = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTextNav("我的小组");
		this.addViewFillInRoot(R.layout.act_address_group);
		refreshListView = (PullToRefreshListView) this.findViewById(R.id.refreshListView);
		this.findViewById(R.id.queryBtn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startSearchAddressAct();
			}
		});
		refreshListView.setMode(Mode.DISABLED);
		refreshListView.setOnItemClickListener(this);
		adapter = new GroupAdapter();
		refreshListView.setAdapter(adapter);
		this.onRefresh();
	}
	private void startSearchAddressAct() {
		Intent intent = new Intent(this, SearchAddressAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}
	protected void onRefresh(){
		this.quickHttpRequest(ID_GETBUSINESSUNIT, new AddressGroupRun());
	}

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
		GroupItem it = adapter.getItem(arg2-1);
		Intent intent = new Intent(this,AddressGroupContentAct.class);
		intent.putExtra("GroupItem", JSON.toJSONString(it));
		this.startActivity(intent);	
		
		
	}
}
