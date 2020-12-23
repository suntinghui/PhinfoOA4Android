package cn.com.phinfo.oaact;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.com.phinfo.adapter.ReportAdapter;
import cn.com.phinfo.oaact.base.SearchBaseAct;
import cn.com.phinfo.protocol.NewsDefaultListRun.NewsItem;
import cn.com.phinfo.protocol.NewsDefaultListRun.NewsResultBean;
import cn.com.phinfo.protocol.SearchReportRun.SearchReportResultBean;
import cn.com.phinfo.protocol.NewsListRun;
import cn.com.phinfo.protocol.SearchReportRun;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;


public class SearchReportAct extends SearchBaseAct implements OnItemClickListener{
	private ReportAdapter adapter=null;
	private String scope,owningUser;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.owningUser = this.getIntent().getExtras().getString("owningUser");
		this.scope = this.getIntent().getExtras().getString("scope");
		adapter = new ReportAdapter(this);
		refreshListView.setAdapter(adapter);
	}
	

	protected void onRefresh(){
		this.quickHttpRequest(ID_LIST, new SearchReportRun("0",scope,queryEdit.getText().toString().trim(),page,owningUser));
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		
	}

	
	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj, Object requestObj) {
		if(ID_LIST == id){	
			if(obj.isOK()){
				SearchReportResultBean o = (SearchReportResultBean)obj;
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
			}else{
				showToast(obj.getMsg());
			}
			if (adapter.getCount() <= 0) {
				refreshListView.setEmptyView(this.getEmptyView());
			} else {
				removeEmptyView();
			}
		}
	}
}
