package cn.com.phinfo.oaact;


import com.alibaba.fastjson.JSON;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.com.phinfo.adapter.NewsAdapter;
import cn.com.phinfo.oaact.base.SearchBaseAct;
import cn.com.phinfo.protocol.NewsChannelRun.HSelectItem;
import cn.com.phinfo.protocol.NewsDefaultListRun.NewsItem;
import cn.com.phinfo.protocol.NewsDefaultListRun.NewsResultBean;
import cn.com.phinfo.protocol.NewsListRun;


public class SearchNewsAct extends SearchBaseAct implements OnItemClickListener{
	private NewsAdapter newsAdapter=null;
	private HSelectItem currIt;
	private int contentTypeCode=1;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		contentTypeCode = this.getIntent().getExtras().getInt("contentTypeCode");
		this.currIt = JSON.parseObject(this.getIntent().getExtras().getString("HSelectItem"), HSelectItem.class);
		newsAdapter = new NewsAdapter();
		refreshListView.setAdapter(newsAdapter);
	}
	

	protected void onRefresh(){
		this.quickHttpRequest(ID_LIST, new NewsListRun(page,currIt.getItemid(),queryEdit.getText().toString().trim(),contentTypeCode));
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		//新闻
		NewsItem it = newsAdapter.getItem(arg2-1);
		Intent intent = new Intent(SearchNewsAct.this,NewsDetailAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("NewsItem", JSON.toJSONString(it));
		startActivity(intent);
		this.finish();
		
	}

	
	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj, Object requestObj) {
		if(ID_LIST == id){
			if(obj.isOK()){
				NewsResultBean o = (NewsResultBean)obj;
				if(page==1){
					newsAdapter.clear();
				}
				newsAdapter.addToListBack(o.getListData());
				page++;
				if(o.getListData().size()<PERPAGE_SIZE){
					refreshListView.setMode(Mode.PULL_FROM_START);
				}else{
					refreshListView.setMode(Mode.BOTH);
				}
			}else{
				showToast(obj.getMsg());
			}
			if (newsAdapter.getCount() <= 0) {
				refreshListView.setEmptyView(this.getEmptyView());
			}else{
				removeEmptyView();
			}
		}
	}
}
