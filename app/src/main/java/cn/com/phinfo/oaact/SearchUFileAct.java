package cn.com.phinfo.oaact;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.com.phinfo.adapter.UFileAttacheAdapter;
import cn.com.phinfo.oaact.base.SearchBaseAct;
import cn.com.phinfo.protocol.AttacheFileRun.AttacheFileItem;
import cn.com.phinfo.protocol.FileSearchRun;
import cn.com.phinfo.protocol.FileSearchRun.FileSearchResultBean;
import cn.com.phinfo.protocol.FileSearchRun.UFileItem;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;

public class SearchUFileAct extends SearchBaseAct implements
		OnItemClickListener {
	private UFileAttacheAdapter adapter = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		adapter = new UFileAttacheAdapter();
		refreshListView.setAdapter(adapter);
	}

	protected void onRefresh() {
		this.quickHttpRequest(0x10, new FileSearchRun("", "", page, queryEdit
				.getText().toString().trim()));
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		UFileItem it = adapter.getItem(arg2 - 1);
		Intent intent = new Intent(this, FileShowAct.class);
		intent.putExtra("AttacheFileItem",
				JSON.toJSONString(AttacheFileItem.init(it)));
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		this.finish();

	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if (ID_LIST == id) {
			if (obj.isOK()) {
				FileSearchResultBean o = (FileSearchResultBean) obj;
				if (page == 1) {
					adapter.clear();
				}
//				adapter.addToListBack(o.getFolders());
				adapter.addToListBack(o.getFiles());
				page++;
//				int n = o.getFolders().size() + o.getFiles().size();
				if (o.getFiles().size() < PERPAGE_SIZE) {
					refreshListView.setMode(Mode.PULL_FROM_START);
				} else {
					refreshListView.setMode(Mode.BOTH);
				}
			} else {
				showToast(obj.getMsg());
			}
			if (adapter.getCount() <= 0) {
				refreshListView.setEmptyView(this.getEmptyView(0xffffffff));
			} else {
				removeEmptyView();
			}
		}
	}
}
