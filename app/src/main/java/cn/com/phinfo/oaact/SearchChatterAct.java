package cn.com.phinfo.oaact;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.com.phinfo.adapter.ShareMsgListAdapter;
import cn.com.phinfo.entity.DataInstance;
import cn.com.phinfo.oaact.base.SearchBaseAct;
import cn.com.phinfo.protocol.PollOptionsRun;
import cn.com.phinfo.protocol.SearchChatterRun;
import cn.com.phinfo.protocol.SearchChatterRun.SearchChatterItem;
import cn.com.phinfo.protocol.SearchChatterRun.SearchChatterResultBean;

import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;


public class SearchChatterAct extends SearchBaseAct implements OnItemClickListener{
	private static final int ID_SEND_NEW = 0x21;
	private ShareMsgListAdapter adapter=null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		adapter = new ShareMsgListAdapter(this,new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SearchChatterItem it = (SearchChatterItem)arg0.getTag();
			    quickHttpRequest(ID_SEND_NEW,new PollOptionsRun(it.getPoll().getPollId(),it.getPoll().getAllSelOptions()));
			}
		});
		refreshListView.setAdapter(adapter);
	}
	

	protected void onRefresh(){
		this.quickHttpRequest(ID_LIST, new SearchChatterRun(queryEdit.getText().toString().trim(),page,DataInstance.getInstance().getUserBody().getUserid()));
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		
	}

	
	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj, Object requestObj) {
		if(ID_LIST == id){
			if(obj.isOK()){
				SearchChatterResultBean o = (SearchChatterResultBean)obj;
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
		if(ID_SEND_NEW == id){
			if(obj.isOK()){
				this.sendBroadcast(new Intent(IBroadcastAction.ACTION_SHARE));
				finish();
				return;
			}else{
				showToast(obj.getMsg());
			}
		}
	}
}
