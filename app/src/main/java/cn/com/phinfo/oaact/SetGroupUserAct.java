package cn.com.phinfo.oaact;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import cn.com.phinfo.adapter.GroupUserAdapter;
import cn.com.phinfo.protocol.GroupRun.GroupItem;
import cn.com.phinfo.protocol.GroupAddUserRun;
import cn.com.phinfo.protocol.GroupUserRun;
import cn.com.phinfo.protocol.UnitandaddressRun.UnitandaddressItem;
import cn.com.phinfo.protocol.UnitandaddressRun.UnitandaddressResultBean;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.HttpLoginMyActBase;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.heqifuhou.pulltorefresh.PullToRefreshListView;
import com.heqifuhou.view.ConfirmDialog;
import com.heqifuhou.view.ConfirmDialog.OnDialogOKListener;

public class SetGroupUserAct extends HttpLoginMyActBase implements
		OnItemClickListener {
	private int page = 1, PERPAGE_SIZE = 15,SUBMIT_ID=0x16,ID_LIST =0x17;
	private PullToRefreshListView refreshListView;
	private GroupItem it;
	private GroupUserAdapter adapter = null;
	private TextView groupName;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String text = this.getIntent().getExtras().getString("GroupItem");
		it = JSON.parseObject(text, GroupItem.class);
		this.addTextNav(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

			}
		}, "设置小组", "确定");
		this.addViewFillInRoot(R.layout.act_set_group);
		groupName = (TextView) this.findViewById(R.id.groupName);
		
		refreshListView = (PullToRefreshListView) this
				.findViewById(R.id.refreshListView);
		refreshListView.setMode(Mode.BOTH);
		adapter = new GroupUserAdapter();
		refreshListView.setAdapter(adapter);
		refreshListView.setOnItemClickListener(this);
		refreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						page = 1;
						onRefresh();
						hideLoading(true);
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						onRefresh();
						hideLoading(true);
					}
				});
		
		
		//添加成员
		this.findViewById(R.id.addGroup).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(SetGroupUserAct.this,SelectAllGroupUsersAct.class);
				intent.putExtra("GroupItem", JSON.toJSONString(it));
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});
		//设置组名
		groupName.setText(it.getName());
		onRefresh();
	}

	protected void onRefresh() {
		this.quickHttpRequest(ID_LIST, new GroupUserRun(it.getGroupId(), page));
	}
	protected void onBroadcastReceiverListener(Context context, Intent intent) {
		super.onBroadcastReceiverListener(context, intent);
		if(intent.getAction().equals(IBroadcastAction.ACTION_ALL_USER_SEL)){
			page=1;
			onRefresh();
			return;
		}
	};
	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if(id == ID_LIST){
			if(obj.isOK()){
				UnitandaddressResultBean o = (UnitandaddressResultBean)obj;
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
			}
			return;
		}
		if(SUBMIT_ID == id){
			if(obj.isOK()){
				page=1;
				onRefresh();
				sendBroadcast(new Intent(IBroadcastAction.ACTION_ALL_USER_SEL));
			}else{
				showToast(obj.getMsg());
			}
			return;
		}
	}

	private ConfirmDialog delDailog;
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		final UnitandaddressItem uit= adapter.getItem(arg2-1);
		if(delDailog!=null&&delDailog.isShowing()){
			return;
		}
		delDailog = new ConfirmDialog(this,"您确定要删除么？",null);
		delDailog.setOnDialogOKListener(new OnDialogOKListener() {
			@Override
			public void onOKItem(Object obj) {
				quickHttpRequest(SUBMIT_ID, new GroupAddUserRun(it.getGroupId(),uit.getUserId(),"-"));
			}
		});
		delDailog.show();
	}
}
