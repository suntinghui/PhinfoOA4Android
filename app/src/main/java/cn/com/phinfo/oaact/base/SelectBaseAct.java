package cn.com.phinfo.oaact.base;


import com.heqifuhou.actbase.HttpLoginMyActBase;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;
import com.heqifuhou.pulltorefresh.PullToRefreshListView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.com.phinfo.oaact.R;


public class SelectBaseAct extends HttpLoginMyActBase implements OnItemClickListener{
	protected static int ID_GETBUSINESSUNIT = 0x10;
	protected PullToRefreshListView refreshListView = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTextNav("部门");
		this.addViewFillInRoot(R.layout.act_select);
		refreshListView = (PullToRefreshListView) this.findViewById(R.id.refreshListView);
		refreshListView.setMode(Mode.DISABLED);
		refreshListView.setOnItemClickListener(this);
	}

	protected void onRefresh(){
		
	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj, Object requestObj) {

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}
}
