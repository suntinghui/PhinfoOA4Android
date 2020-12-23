package cn.com.phinfo.oaact;

import java.util.List;
import java.util.Stack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import cn.com.phinfo.adapter.AddressFridesBaseAdapter;
import cn.com.phinfo.adapter.base.FriendsBaseAdapter;
import cn.com.phinfo.oaact.base.AddressBaseActAbs;
import cn.com.phinfo.protocol.UnitandaddressRun.UnitandaddressItem;
import cn.com.phinfo.utils.ContactsUtils;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.protocolbase.HttpThread;
import com.heqifuhou.protocolbase.HttpThread.IHttpRunnable;
import com.heqifuhou.protocolbase.HttpThread.IThreadResultListener;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;

public class AddressLocalAct extends AddressBaseActAbs {
	private static final int ID_GETLIST = 0x10;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTextNav("手机通讯录");
		refreshListView.setMode(Mode.DISABLED);
		this.findViewById(R.id.queryBtn).setVisibility(View.GONE);
		this.findViewById(R.id.filterEditBtn).setVisibility(View.VISIBLE);
		this.onRefresh();
	}

	protected void onRefresh() {
		final List<UnitandaddressItem> has = new Stack<UnitandaddressItem>();
		// 压缩
		final IHttpRunnable conRunable = new IHttpRunnable() {
			@Override
			public HttpResultBeanBase onRun(HttpThread t) {
				List<UnitandaddressItem> ls =  ContactsUtils.getInstance(AddressLocalAct.this).getRemoveDuplicate();
				has.addAll(ls);
				HttpResultBeanBase ret = new HttpResultBeanBase();
				ret.setOK();
				return ret;
			}
		};
		final IThreadResultListener ziplistener = new IThreadResultListener() {
			@Override
			public void onHttpForResult(int id, HttpResultBeanBase obj,
					Object requestObj) {
				refreshListView.onRefreshComplete();
				if (id == ID_GETLIST) {
					hideLoading();
					if (obj.isOK()) {
						adapterFriends.replaceListRef(has);
					} else {
						showToast("附件读取失败");
					}
				}
			}
		};
		this.quickHttpRequest(ID_GETLIST, conRunable, ziplistener, has, true);
	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		UnitandaddressItem it = (UnitandaddressItem) adapterFriends
				.getItem(arg2 - 1);
		Intent intent = new Intent(this, AddressDetailAct.class);
		intent.putExtra("ISAPPEND", false);
		intent.putExtra("UnitandaddressItem", JSON.toJSONString(it));
		this.startActivity(intent);
	}

	@Override
	protected FriendsBaseAdapter onGetFriendsAdapter() {
		return new AddressFridesBaseAdapter();
	}
}
