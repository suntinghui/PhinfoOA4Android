package cn.com.phinfo.oaact;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.com.phinfo.adapter.TodoTypeDetailListAdapter;
import cn.com.phinfo.oaact.base.SearchBaseAct;
import cn.com.phinfo.protocol.ProcessSearchListRun.ProcessSearchItem;
import cn.com.phinfo.protocol.ProcessSearchListRun.ProcessSearchResultBean;
import cn.com.phinfo.protocol.SearchTodoTypesRun;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;


public class SearchTodoTypeAct extends SearchBaseAct implements OnItemClickListener{
	private TodoTypeDetailListAdapter todoTypeAdapter=null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		todoTypeAdapter = new TodoTypeDetailListAdapter();
		refreshListView.setAdapter(todoTypeAdapter);
	}
	

	protected void onRefresh(){
		this.quickHttpRequest(ID_LIST, new SearchTodoTypesRun(page,queryEdit.getText().toString().trim()));
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		ProcessSearchItem it = todoTypeAdapter.getItem(arg2-1);
		Intent intent = new Intent(this,CreateTodosAct.class);
		intent.putExtra("ProcessSearchItem", JSON.toJSONString(it));
		this.startActivity(intent);
		this.finish();
	}

	
	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj, Object requestObj) {
		if(ID_LIST == id){
			if(obj.isOK()){
				ProcessSearchResultBean o = (ProcessSearchResultBean)obj;
				if(page==1){
					todoTypeAdapter.clear();
				}
				todoTypeAdapter.addToListBack(o.getListData());
				page++;
				if(o.getListData().size()<PERPAGE_SIZE){
					refreshListView.setMode(Mode.PULL_FROM_START);
				}else{
					refreshListView.setMode(Mode.BOTH);
				}
			}else{
				showToast(obj.getMsg());
			}
			if (todoTypeAdapter.getCount() <= 0) {
				refreshListView.setEmptyView(this.getEmptyView());
			}else{
				removeEmptyView();
			}
		}
	}
}
