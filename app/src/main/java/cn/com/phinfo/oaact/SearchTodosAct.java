package cn.com.phinfo.oaact;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.com.phinfo.adapter.TodosAdapter;
import cn.com.phinfo.oaact.base.SearchBaseAct;
import cn.com.phinfo.protocol.AddressSearchRun;
import cn.com.phinfo.protocol.TodosRun;
import cn.com.phinfo.protocol.TodosRun.TodosItem;
import cn.com.phinfo.protocol.TodosRun.TodosResultBean;

public class SearchTodosAct extends SearchBaseAct implements OnItemClickListener {
	private TodosAdapter adapter = null;
	private int Index;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Index = this.getIntent().getExtras().getInt("IDX");
		adapter = new TodosAdapter();
		refreshListView.setAdapter(adapter);
	}

	protected void onRefresh() {
		this.quickHttpRequest(Index, new TodosRun(Index,queryEdit.getText().toString().trim(),page));
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		TodosItem it = adapter.getItem(arg2-1);
		Intent intent = new Intent(this,TodosDetailAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("TodosItem", JSON.toJSONString(it));
		this.startActivity(intent);
		this.finish();

	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj, Object requestObj) {
		if (Index == id) {
			if (obj.isOK()) {
				TodosResultBean o = (TodosResultBean)obj;
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
				refreshListView.setEmptyView(this.getEmptyView(0xffffffff));
			} else {
				removeEmptyView();
			}
		}
	}
}
