package cn.com.phinfo.oaact;


import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.HttpLoginMyActBase;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;
import com.heqifuhou.pulltorefresh.PullToRefreshListView;
import com.heqifuhou.utils.ParamsCheckUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.com.phinfo.adapter.TodoTypeDetailListAdapter;
import cn.com.phinfo.protocol.ProcessSearchListRun;
import cn.com.phinfo.protocol.ProcessSearchListRun.ProcessSearchItem;
import cn.com.phinfo.protocol.ProcessSearchListRun.ProcessSearchResultBean;
import cn.com.phinfo.protocol.TodoTypeListRun.TodoTypeItem;

//事务列表
public class TodoTypeDetailListAct extends HttpLoginMyActBase implements OnItemClickListener{
	private static int ID_GETITEM=0x12;
	private PullToRefreshListView refresh;
	private TodoTypeDetailListAdapter adapter;
	private TodoTypeItem item=null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTextNav("文件查看详情");
		if(this.getIntent()!=null&&this.getIntent().getExtras()!=null){
			String s = this.getIntent().getExtras().getString("TodoTypeItem");
			if(!ParamsCheckUtils.isNull(s)){
				item = JSON.parseObject(s,TodoTypeItem.class);
			}
		}
		this.addTextNav(item.getName());
		this.addViewFillInRoot(R.layout.act_todotypelist);
		refresh = (PullToRefreshListView) this.findViewById(R.id.refresh);
		adapter = new TodoTypeDetailListAdapter();
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
		this.quickHttpRequest(ID_GETITEM, new ProcessSearchListRun(item.getItemId()));
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		ProcessSearchItem it = adapter.getItem(arg2-1);
		Intent intent = new Intent(this,CreateTodosAct.class);
		intent.putExtra("ProcessSearchItem", JSON.toJSONString(it));
		this.startActivity(intent);
	}
	
	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj, Object requestObj) {
		if(ID_GETITEM == id){
			if(obj.isOK()){
				ProcessSearchResultBean o = (ProcessSearchResultBean)obj;
				adapter.replaceListRef(o.getListData());
			}else{
				showToast(obj.getMsg());
			}
			if (adapter.getCount() <= 0) {
				refresh.setEmptyView(this.getLayoutInflater(R.layout.empty));
			}
		}
	}
}
