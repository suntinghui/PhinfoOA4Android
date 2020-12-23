package cn.com.phinfo.oaact;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import cn.com.phinfo.adapter.SelectPersonAdapter;
import cn.com.phinfo.adapter.base.FriendsBaseAdapter;
import cn.com.phinfo.oaact.base.AddressBaseActAbs;
import cn.com.phinfo.protocol.AddressSearchRun;
import cn.com.phinfo.protocol.AddressSearchRun.AddressSearchResultBean;
import cn.com.phinfo.protocol.GroupRun.GroupItem;
import cn.com.phinfo.protocol.GroupAddUserRun;
import cn.com.phinfo.protocol.UnitandaddressRun.UnitandaddressItem;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.heqifuhou.utils.ParamsCheckUtils;


public class SelectAllGroupUsersAct extends AddressBaseActAbs {
	protected static int PERPAGE_SIZE = 15,SUBMIT_ID = 0x10,ID_GETLSIT=0x11;
	protected int pageNumber=1;
	private GroupItem it;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String text = this.getIntent().getExtras().getString("GroupItem");
		it = JSON.parseObject(text, GroupItem.class);
		this.addTextNav(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				submit();
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
	
	private void submit(){
		SelectPersonAdapter adapter = (SelectPersonAdapter)adapterFriends;
		String s = adapter.getUserAllIds();
		if(ParamsCheckUtils.isNull(s)){
			showToast("没有选择人员");
			return;
		}
		this.quickHttpRequest(SUBMIT_ID, new GroupAddUserRun(it.getGroupId(),s,"+"));
		
	}
	
	protected void onBroadcastReceiverListener(Context context, Intent intent) {
		super.onBroadcastReceiverListener(context, intent);
		if(intent.getAction().equals(IBroadcastAction.ACTION_ALL_USER_SEL)){
			finish();
		}
	};
	
	protected void onRefresh(){
		this.quickHttpRequest(ID_GETLSIT, new AddressSearchRun("", pageNumber));
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		UnitandaddressItem it= (UnitandaddressItem) adapterFriends.getItem(arg2-1);
		it.setIsbSel(!it.getIsbSel());
		adapterFriends.notifyDataSetChanged();
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
		if(ID_GETLSIT == id){
			if (obj.isOK()) {
				AddressSearchResultBean o = (AddressSearchResultBean) obj;
				if (pageNumber == 1) {
					adapterFriends.clear();
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
				refreshListView.setEmptyView(this.getEmptyView());
			} else {
				removeEmptyView();
			}	
			return;
		}

	}

	@Override
	public void onClick(View arg0) {
		startSearchAddressAct();
	}

	private void startSearchAddressAct() {
		Intent intent = new Intent(this, SearchGroupUsersAct.class);
		intent.putExtra("GroupItem", JSON.toJSONString(it));
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}
	@Override
	protected FriendsBaseAdapter onGetFriendsAdapter() {
		return new SelectPersonAdapter();
	}
}
