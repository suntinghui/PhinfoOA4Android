package cn.com.phinfo.oaact;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import cn.com.phinfo.adapter.ReportAdapter;
import cn.com.phinfo.entity.DataInstance;
import cn.com.phinfo.protocol.SearchReportRun;
import cn.com.phinfo.protocol.SearchReportRun.SearchItem;
import cn.com.phinfo.protocol.SearchReportRun.SearchReportResultBean;
import cn.com.phinfo.view.ReportPopWindows;
import cn.com.phinfo.view.ReportPopWindows.OnPopupWindowsItemListener;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.heqifuhou.pulltorefresh.PullToRefreshListView;

//报告
public class ReportAct extends HttpMyActBase implements OnItemClickListener{
	private int page=1,PERPAGE_SIZE=15;
	private PullToRefreshListView mList = null;
	private ReportAdapter adapter = null;
	private RadioGroup radioGroup = null;
	private String scope = "all";
	private String owningUser="";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTextNav("报告");
		this.addViewFillInRoot(R.layout.act_report);
		this.addBottomView(R.layout.nav_report_tools_btn);
		mList = (PullToRefreshListView) this.findViewById(R.id.refreshListView);
		mList.setMode(Mode.BOTH);
		adapter = new ReportAdapter(this);
		mList.setAdapter(adapter);
		mList.setOnItemClickListener(this);
		mList.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				page = 1;
				onRefresh();
				hideLoading(true);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				onRefresh();
				hideLoading(true);
			}
		});
		this.findViewById(R.id.queryBtn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(ReportAct.this,SearchReportAct.class);
				intent.putExtra("scope", scope);
				intent.putExtra("owningUser", owningUser);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				ReportAct.this.startActivity(intent);
			}
		});
		this.findViewById(R.id.my_add).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showReportPopWindows();
			}
		});
		radioGroup = (RadioGroup) this.findViewById(R.id.raidogroup);
		radioGroup.setOnCheckedChangeListener(ocheckedListener);
		onRefresh();
	}
	
	// 处理按钮组的事件
	private final OnCheckedChangeListener ocheckedListener = new OnCheckedChangeListener() {
		public void onCheckedChanged(RadioGroup myFlowRadioGroup,
				int checkedId) {
			switch (checkedId) {
			case R.id.home_btn:
				page = 1;
				owningUser="";
				scope = "all";
				onRefresh();
				break;
			case R.id.my_btn:
				page = 1;
				owningUser=DataInstance.getInstance().getUserBody().getUserid();
				scope = "me";
				onRefresh();
				break;
			}
		}
	};
	protected void onRefresh(){
		
		this.quickHttpRequest(0x10, new SearchReportRun("0",scope,"",page,owningUser));
	}
	//弹窗
	private ReportPopWindows report=null;
	private void showReportPopWindows(){
		if(report!=null&&report.isShowing()){
			return;
		}
		report = new ReportPopWindows(this,this.findViewById(R.id.reprotroot));
		report.show();
		report.setOnPopupWindowsItemListener(new OnPopupWindowsItemListener() {
			@Override
			public void onPopupWindowsItem(int pos) {
				Intent intent = new Intent(ReportAct.this,CreateReportAct.class);
				intent.putExtra("TYPE", pos);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				ReportAct.this.startActivity(intent);
			}
		});
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		SearchItem it = adapter.getItem(arg2-1);
		Intent intent = new Intent(this,ReportDetailAct.class);
		intent.putExtra("SearchItem", JSON.toJSONString(it));
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	protected void onBroadcastReceiverListener(Context context, Intent intent) {
		if(IBroadcastAction.ACTION_REPORT.equals(intent.getAction())){
			page=1;
			onRefresh();
			return;
		}
		if(IBroadcastAction.ACTION_REPORT_COMM.equals(intent.getAction())){
			page=1;
			onRefresh();
			return;
		}
		super.onBroadcastReceiverListener(context, intent);
	};
	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if(id == 0x10){
			if(obj.isOK()){
				SearchReportResultBean o = (SearchReportResultBean)obj;
				if (page == 1) {
					adapter.clear();
				}
				adapter.addToListBack(o.getListData());
				page++;
				if (o.getListData().size() < PERPAGE_SIZE) {
					mList.setMode(Mode.PULL_FROM_START);
				} else {
					mList.setMode(Mode.BOTH);
				}
			}else{
				showToast(obj.getMsg());
			}
			if (adapter.getCount() <= 0) {
				mList.setEmptyView(this.getEmptyView());
			} else {
				removeEmptyView();
			}
		}
	}
}
