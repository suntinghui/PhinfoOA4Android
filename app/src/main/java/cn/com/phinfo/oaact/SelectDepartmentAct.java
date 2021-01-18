package cn.com.phinfo.oaact;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;

import cn.com.phinfo.adapter.AddressFridesBaseAdapter;
import cn.com.phinfo.adapter.SelectPersonAdapter;
import cn.com.phinfo.adapter.base.FriendsBaseAdapter;
import cn.com.phinfo.entity.DataInstance;
import cn.com.phinfo.oaact.base.AddressBaseActAbs;
import cn.com.phinfo.protocol.BusinessUnitRun;
import cn.com.phinfo.protocol.UnitandaddressRun;
import cn.com.phinfo.protocol.UnitandaddressRun.UnitandaddressItem;
import cn.com.phinfo.protocol.UnitandaddressRun.UnitandaddressResultBean;
import cn.hutool.core.util.StrUtil;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.OnRefreshListener2;


public class SelectDepartmentAct extends AddressBaseActAbs	{
	private static int PERPAGE_SIZE = 15;
	private static final int ID_GETLIST = 0x10,ID_RQ=0x12;
	private int pageNumber=1;
	private UnitandaddressItem addressItem = new UnitandaddressItem();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (this.getIntent() != null && this.getIntent().getExtras() != null) {
			String s = this.getIntent().getExtras().getString("UnitandaddressItem");
			addressItem = JSON.parseObject(s, UnitandaddressItem.class);
		}
		this.addTextNav(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				sendBroadcast(new Intent(IBroadcastAction.ACTION_USRE_SEL));
			}
		},"选择联系人","完成");
		
//		refreshListView.setOnRefreshListener(new OnRefreshListener2() {
//			@Override
//			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
//				pageNumber = 1;
//				onRefresh();
//				hideLoading(true);
//			}
//
//			@Override
//			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
//				onRefresh();
//				hideLoading(true);
//			}
//		});
		onRefresh();
	}
	protected void onBroadcastReceiverListener(Context context, Intent intent) {
		super.onBroadcastReceiverListener(context, intent);
		if(intent.getAction().equals(IBroadcastAction.ACTION_USRE_SEL)){
			finish();
		}
	};
	protected void onRefresh() {
		this.quickHttpRequest(ID_GETLIST, new UnitandaddressRun(addressItem.getId()));
	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj, Object requestObj) {
		if (ID_GETLIST == id) {
			if (obj.isOK()) {

				adapterFriends.clear();
				UnitandaddressResultBean o = (UnitandaddressResultBean) obj;
				adapterFriends.addToListBack(o.getListData());




//				UnitandaddressResultBean o = (UnitandaddressResultBean)obj;
//				if (pageNumber == 1) {
//					adapterFriends.clear();
//				}
//				for(UnitandaddressItem it:o.getListData()){
//					it.setIsbSel(DataInstance.getInstance().isContains(it));
//				}
//				adapterFriends.addToListBack(o.getListData());
//				pageNumber++;
//				if (o.getListData().size() < PERPAGE_SIZE) {
//					refreshListView.setMode(Mode.PULL_FROM_START);
//				} else {
//					refreshListView.setMode(Mode.BOTH);
//				}
			} else {
				this.showToast(obj.getMsg());
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
		UnitandaddressItem it = (UnitandaddressItem) adapterFriends.getItem(arg2 - 1);

		if (it.isAddressList()) {
			if (DataInstance.getInstance().isContains(it)) {
				view.findViewById(R.id.checkedImage).setVisibility(View.GONE);
				it.setIsbSel(false);
				DataInstance.getInstance().removeUnitandaddressItem(it);
			} else {
				view.findViewById(R.id.checkedImage).setVisibility(View.VISIBLE);
				it.setIsbSel(true);
				DataInstance.getInstance().addUnitandaddressItem(it);
			}

		} else {
			Intent intent = new Intent(this, SelectDepartmentAct.class);
			intent.putExtra("UnitandaddressItem", JSON.toJSONString(it));
			this.startActivity(intent);
		}


//		UnitandaddressItem it = (UnitandaddressItem) adapterFriends.getItem(arg2 - 1);
//		if (it.isAddressList()) {
//			it.setIsbSel(!it.getIsbSel());
//			adapterFriends.notifyDataSetChanged();
//			if(it.getIsbSel()){
//				DataInstance.getInstance().addUnitandaddressItem(it);
//			}else{
//				DataInstance.getInstance().removeUnitandaddressItem(it);
//			}
//		} else {
//			Intent intent = new Intent(this, SelectDepartmentAct.class);
//			intent.putExtra("UnitandaddressItem", JSON.toJSONString(it));
//			this.startActivity(intent);
//		}
	}
	
	@Override
	public void onClick(View arg0) {
		startSearchAddressAct();
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(ID_RQ == requestCode&&resultCode==RESULT_OK){
			this.finish();
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void startSearchAddressAct() {
		Intent intent = new Intent(this, SearchUserAddressAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivityForResult(intent,ID_RQ);
	}
	@Override
	protected FriendsBaseAdapter onGetFriendsAdapter() {
//		return new SelectPersonAdapter();
		return new AddressFridesBaseAdapter();
	}
}
