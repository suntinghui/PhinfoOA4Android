package cn.com.phinfo.oaact;


import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.heqifuhou.utils.ParamsCheckUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import cn.com.phinfo.adapter.SelectPersonAdapter;
import cn.com.phinfo.adapter.base.FriendsBaseAdapter;
import cn.com.phinfo.entity.DataInstance;
import cn.com.phinfo.oaact.base.AddressBaseActAbs;
import cn.com.phinfo.protocol.GroupRun.GroupItem;
import cn.com.phinfo.protocol.GroupUserRun;
import cn.com.phinfo.protocol.UnitandaddressRun.UnitandaddressItem;
import cn.com.phinfo.protocol.UnitandaddressRun.UnitandaddressResultBean;


public class SelectGroupUsersAct extends AddressBaseActAbs {
	protected static int PERPAGE_SIZE = 15;
	protected GroupItem it;
	protected int pageNumber=1;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String s = this.getIntent().getExtras().getString("GroupItem");
		if(!ParamsCheckUtils.isNull(s)){
			this.it = JSON.parseObject(s, GroupItem.class);
		}
		this.addTextNav(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				sendBroadcast(new Intent(IBroadcastAction.ACTION_USRE_SEL));
			}
		},"选择联系人","完成");
		refreshListView.setOnRefreshListener(new OnRefreshListener2() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				pageNumber = 1;
				onRefresh();
				hideLoading(true);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				onRefresh();
				hideLoading(true);
			}
		});
		onRefresh();
	}
	
	protected void onBroadcastReceiverListener(Context context, Intent intent) {
		super.onBroadcastReceiverListener(context, intent);
		if(intent.getAction().equals(IBroadcastAction.ACTION_USRE_SEL)){
			finish();
		}
	};
	
	protected void onRefresh(){
		this.quickHttpRequest(0x10, new GroupUserRun(it.getGroupId(),pageNumber));
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		UnitandaddressItem it= (UnitandaddressItem) adapterFriends.getItem(arg2-1);
		it.setIsbSel(!it.getIsbSel());
		adapterFriends.notifyDataSetChanged();
		if(it.getIsbSel()){
			DataInstance.getInstance().addUnitandaddressItem(it);
		}else{
			DataInstance.getInstance().removeUnitandaddressItem(it);
		}
	}
	
	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj, Object requestObj) {
		if (obj.isOK()) {
			UnitandaddressResultBean o = (UnitandaddressResultBean)obj;
			if (pageNumber == 1) {
				adapterFriends.clear();
			}
			for(UnitandaddressItem it:o.getListData()){
				it.setIsbSel(DataInstance.getInstance().isContains(it));
			}
			adapterFriends.addToListBack(o.getListData());
			pageNumber++;
			if (o.getListData().size() < PERPAGE_SIZE) {
				refreshListView.setMode(Mode.PULL_FROM_START);
			} else {
				refreshListView.setMode(Mode.BOTH);
			}
		} else {
			showToast(obj.getMsg());
		}
		if (adapterFriends.getCount() <= 0) {
			refreshListView.setEmptyView(this.getEmptyView(0xff333333));
		} else {
			removeEmptyView();
		}
	}

	@Override
	public void onClick(View arg0) {
		startSearchAddressAct();
	}

	private void startSearchAddressAct() {
		Intent intent = new Intent(this, SelectSearchAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}
	@Override
	protected FriendsBaseAdapter onGetFriendsAdapter() {
		return new SelectPersonAdapter();
	}
}
