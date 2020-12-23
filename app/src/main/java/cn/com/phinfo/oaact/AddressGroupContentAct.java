package cn.com.phinfo.oaact;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.protocolbase.HttpResultBeanBase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import cn.com.phinfo.adapter.AddressFridesBaseAdapter;
import cn.com.phinfo.adapter.base.FriendsBaseAdapter;
import cn.com.phinfo.oaact.base.AddressBaseActAbs;
import cn.com.phinfo.protocol.AddressGroupContactsRun;
import cn.com.phinfo.protocol.GroupRun.GroupItem;
import cn.com.phinfo.protocol.UnitandaddressRun.UnitandaddressItem;
import cn.com.phinfo.protocol.UnitandaddressRun.UnitandaddressResultBean;

public class AddressGroupContentAct extends AddressBaseActAbs {
	private static final int ID_GETLIST = 0x10;
	private GroupItem it = new GroupItem();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (this.getIntent() != null && this.getIntent().getExtras() != null) {
			String s = this.getIntent().getExtras().getString("GroupItem");
			it = JSON.parseObject(s, GroupItem.class);
		}
		this.addTextNav(it.getName());
		this.onRefresh();
	}

	protected void onRefresh() {
		this.quickHttpRequest(ID_GETLIST, new AddressGroupContactsRun(it.getGroupId()));
	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj, Object requestObj) {
		if (ID_GETLIST == id) {
			if (obj.isOK()) {
				adapterFriends.clear();
				UnitandaddressResultBean o = (UnitandaddressResultBean) obj;
				adapterFriends.addToListBack(o.getListData());
			} else {
				this.showToast(obj.getMsg());
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

	@Override
	protected FriendsBaseAdapter onGetFriendsAdapter() {
		return new AddressFridesBaseAdapter();
	}
}
