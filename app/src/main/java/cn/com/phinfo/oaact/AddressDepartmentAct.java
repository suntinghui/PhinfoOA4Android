package cn.com.phinfo.oaact;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.utils.ParamsCheckUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import cn.com.phinfo.adapter.AddressDepartmentAdapter;
import cn.com.phinfo.adapter.AddressFridesBaseAdapter;
import cn.com.phinfo.adapter.base.DepartmentBaseAdapter;
import cn.com.phinfo.adapter.base.FriendsBaseAdapter;
import cn.com.phinfo.oaact.base.AddressBaseActAbs;
import cn.com.phinfo.protocol.UnitandaddressRun;
import cn.com.phinfo.protocol.UnitandaddressRun.UnitandaddressItem;
import cn.com.phinfo.protocol.UnitandaddressRun.UnitandaddressResultBean;

public class AddressDepartmentAct extends AddressBaseActAbs {
	private static final int ID_GETBUSINESSUNIT = 0x10;
	private UnitandaddressItem addressItem = new UnitandaddressItem();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (this.getIntent() != null && this.getIntent().getExtras() != null) {
			String s = this.getIntent().getExtras().getString("UnitandaddressItem");
			addressItem = JSON.parseObject(s, UnitandaddressItem.class);
		}
		if (ParamsCheckUtils.isNull(addressItem.getName())) {
			this.addTextNav("部门");
		} else {
			this.addTextNav(addressItem.getName());
		}
		this.onRefresh();
	}

	protected void onRefresh() {
		this.quickHttpRequest(ID_GETBUSINESSUNIT, new UnitandaddressRun(addressItem.getId()));
	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj, Object requestObj) {
		if (ID_GETBUSINESSUNIT == id) {
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
		if (it.isAddressList()) {
			Intent intent = new Intent(this, AddressDetailAct.class);
			intent.putExtra("UnitandaddressItem", JSON.toJSONString(it));
			this.startActivity(intent);
		} else {
			Intent intent = new Intent(this, AddressDepartmentAct.class);
			intent.putExtra("UnitandaddressItem", JSON.toJSONString(it));
			this.startActivity(intent);
		}
	}

	@Override
	protected FriendsBaseAdapter onGetFriendsAdapter() {
		return new AddressFridesBaseAdapter();
	}
}
