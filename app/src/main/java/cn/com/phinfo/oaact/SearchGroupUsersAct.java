package cn.com.phinfo.oaact;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.com.phinfo.adapter.SelectPersonAdapter;
import cn.com.phinfo.oaact.base.SearchBaseAct;
import cn.com.phinfo.protocol.AddressSearchRun;
import cn.com.phinfo.protocol.GroupAddUserRun;
import cn.com.phinfo.protocol.AddressSearchRun.AddressSearchResultBean;
import cn.com.phinfo.protocol.GroupRun.GroupItem;
import cn.com.phinfo.protocol.UnitandaddressRun.UnitandaddressItem;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;

public class SearchGroupUsersAct extends SearchBaseAct implements OnItemClickListener {
	private static int SUBMIT_ID  = 0x10;
	private SelectPersonAdapter adapter = null;
	private GroupItem groupIt;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String text = this.getIntent().getExtras().getString("GroupItem");
		groupIt = JSON.parseObject(text, GroupItem.class);
		adapter = new SelectPersonAdapter();
		refreshListView.setAdapter(adapter);
	}

	protected void onRefresh() {
		this.quickHttpRequest(ID_LIST, new AddressSearchRun(queryEdit.getText().toString().trim(), page));
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		UnitandaddressItem it = adapter.getItem(arg2 - 1);
		this.quickHttpRequest(SUBMIT_ID, new GroupAddUserRun(groupIt.getGroupId(),it.getUserId(),"+"));
		sendBroadcast(new Intent(IBroadcastAction.ACTION_ALL_USER_SEL));
		this.finish();
	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj, Object requestObj) {
		if(SUBMIT_ID == id){
			if(obj.isOK()){
				sendBroadcast(new Intent(IBroadcastAction.ACTION_ALL_USER_SEL));
				this.finish();
			}else{
				showToast(obj.getMsg());
			}
			return;
		}
		if (ID_LIST == id) {
			if (obj.isOK()) {
				AddressSearchResultBean o = (AddressSearchResultBean) obj;
				if (page == 1) {
					adapter.clear();
				}
				adapter.addToListBack(o.getListData());
				page++;
				if (o.getListData().size() < PERPAGE_SIZE) {
					refreshListView.setMode(Mode.PULL_FROM_START);
				} else {
					refreshListView.setMode(Mode.BOTH);
				}
			} else {
				showToast(obj.getMsg());
			}
			if (adapter.getCount() <= 0) {
				refreshListView.setEmptyView(this.getEmptyView());
			} else {
				removeEmptyView();
			}
		}
	}
}
