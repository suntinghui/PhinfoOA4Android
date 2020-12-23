package cn.com.phinfo.oaact;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.com.phinfo.adapter.TodoTypeListAdapter;
import cn.com.phinfo.protocol.TodoTypeListRun;
import cn.com.phinfo.protocol.TodoTypeListRun.TodoTypeItem;
import cn.com.phinfo.protocol.TodoTypeListRun.TodoTypeListResultBean;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.HttpLoginMyActBase;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;
import com.heqifuhou.pulltorefresh.PullToRefreshListView;

//事务列表
public class TodoTypeListAct extends HttpLoginMyActBase implements OnItemClickListener{
	private static int ID_GETLIST = 0x10;
	private PullToRefreshListView refresh;
	private TodoTypeListAdapter adapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTextNav("发起事务");
		this.addViewFillInRoot(R.layout.act_todotypelist);
		refresh = (PullToRefreshListView) this.findViewById(R.id.refresh);
		adapter = new TodoTypeListAdapter();
		refresh.setAdapter(adapter);
		refresh.setMode(Mode.DISABLED);
		refresh.setOnItemClickListener(this);
		this.findViewById(R.id.queryBtn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startSearchTodoTypeAct();
			}
		});

		onRefresh();
	}
	private void startSearchTodoTypeAct(){
		Intent intent = new Intent(this,SearchTodoTypeAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}
	
	protected void onRefresh(){
		this.quickHttpRequest(ID_GETLIST, new TodoTypeListRun());
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		TodoTypeItem it = adapter.getItem(arg2-1);
		Intent intent = new Intent(this,TodoTypeDetailListAct.class);
		intent.putExtra("TodoTypeItem", JSON.toJSONString(it));
		this.startActivity(intent);
	}
	
	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj, Object requestObj) {
		if(ID_GETLIST == id){
			if(obj.isOK()){
				TodoTypeListResultBean o = (TodoTypeListResultBean)obj;
				adapter.replaceListRef(o.getListData());
			}else{
				showToast(obj.getMsg());
			}
			if (adapter.getCount() <= 0) {
				refresh.setEmptyView(this.getLayoutInflater(R.layout.empty));
			}
			return;
		}
	}
}
