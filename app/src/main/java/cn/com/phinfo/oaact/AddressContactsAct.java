package cn.com.phinfo.oaact;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.OnRefreshListener2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import cn.com.phinfo.adapter.AddressFridesBaseAdapter;
import cn.com.phinfo.adapter.base.FriendsBaseAdapter;
import cn.com.phinfo.oaact.base.AddressBaseActAbs;
import cn.com.phinfo.protocol.AddressSearchRun;
import cn.com.phinfo.protocol.AddressSearchRun.AddressSearchResultBean;
import cn.com.phinfo.protocol.UnitandaddressRun.UnitandaddressItem;

public class AddressContactsAct extends AddressBaseActAbs {
	private static int PERPAGE_SIZE = 15;
	private static final int ID_GETLIST = 0x10;
	private int pageNumber=1;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTextNav("联系人");
		
		refreshListView.setMode(Mode.BOTH);
		refreshListView.setOnRefreshListener(new OnRefreshListener2() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				pageNumber = 1;
				onRefresh();
				hideLoading();
				
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				onRefresh();
				hideLoading();
			}
		});
		this.onRefresh();
	}

	protected void onRefresh() {
		this.quickHttpRequest(ID_GETLIST, new AddressSearchRun("", pageNumber));
	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj, Object requestObj) {
		if (ID_GETLIST == id) {
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
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		UnitandaddressItem it = (UnitandaddressItem) adapterFriends.getItem(arg2 - 1);
		Intent intent = new Intent(this, AddressDetailAct.class);
		intent.putExtra("UnitandaddressItem", JSON.toJSONString(it));
		this.startActivity(intent);
	}
	public void onHttpForResult(int id, HttpResultBeanBase obj, Object requestObj) {
		super.onHttpForResult(id, obj, requestObj);
		refreshListView.onRefreshComplete();
	}
	@Override
	protected FriendsBaseAdapter onGetFriendsAdapter() {
		return new AddressFridesBaseAdapter();
	}

}
