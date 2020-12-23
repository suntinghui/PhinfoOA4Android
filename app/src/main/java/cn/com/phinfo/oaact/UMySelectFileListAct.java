package cn.com.phinfo.oaact;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.com.phinfo.adapter.UFileMyAdapter;
import cn.com.phinfo.protocol.FileSearchRun;
import cn.com.phinfo.protocol.FileSearchRun.FileSearchResultBean;
import cn.com.phinfo.protocol.FileSearchRun.UFileItem;
import cn.com.phinfo.protocol.MoveDirRun;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.heqifuhou.pulltorefresh.PullToRefreshListView;
import com.heqifuhou.utils.ParamsCheckUtils;

public class UMySelectFileListAct extends HttpMyActBase implements
		OnItemClickListener {
	private static final int ID_GETLST = 0x10,ID_MOVE = 0x12;
	private int page;
	private PullToRefreshListView mList = null;
	private UFileMyAdapter adapter = null;
	protected String folderid = "";
	private String srchType = "my";
	private String title = "我的文件";
	private UFileItem formIt;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		srchType = this.getIntent().getExtras().getString("srchType");
		title = this.getIntent().getExtras().getString("title");
		String s = this.getIntent().getExtras().getString("UFileItem");
		formIt = JSON.parseObject(s, UFileItem.class);
		folderid = this.getIntent().getExtras().getString("folderid");
		if (ParamsCheckUtils.isNull(folderid)) {
			folderid = "";
		}
		this.addTitleView(R.layout.nav_u_white_btn);
		this.addBottomView(R.layout.nav_select_u_tools);
		this.addTextNav(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		}, title, "取消");
		this.hideBackNav();
		this.addViewFillInRoot(R.layout.act_u_select_refresh);
		mList = (PullToRefreshListView) this.findViewById(R.id.refreshListView);
		mList.setMode(Mode.PULL_FROM_START);
		adapter = new UFileMyAdapter();
		mList.setAdapter(adapter);
		mList.setOnItemClickListener(this);
		mList.setOnRefreshListener(new OnRefreshListener2<ListView>() {
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
		// 创建
		findViewById(R.id.btnCreate).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(UMySelectFileListAct.this,
						UDirCreateAct.class);
				intent.putExtra("parentID", folderid);
				startActivity(intent);
			}
		});
		// 移动
		findViewById(R.id.btnMove).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String ObjectTypeCode = formIt.isFloder()?"100200":"100100";
				quickHttpRequest(ID_MOVE, new MoveDirRun(formIt.getId(), folderid, ObjectTypeCode));
			}
		});
		onRefresh();
	}

	protected void onRefresh() {
		this.quickHttpRequest(ID_GETLST, new FileSearchRun(srchType, folderid,
				page, ""));
	}
	protected void onBroadcastReceiverListener(Context context, Intent intent) {
		if(IBroadcastAction.ACTION_U_CREATE.equals(intent.getAction())){
			String s = intent.getExtras().getString("UFileItem");
			UFileItem it = JSON.parseObject(s, UFileItem.class);
			adapter.addToListHead(it);
		}
	};
	
	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if (id == ID_GETLST) {
			FileSearchResultBean o = (FileSearchResultBean) obj;
			if (page == 1) {
				adapter.clear();
			}
			adapter.addToListBack(o.getFolders());
			adapter.addToListBack(o.getFiles());
			page++;
			int n = o.getFolders().size() + o.getFiles().size();
			if (n <= 0) {
				mList.setMode(Mode.PULL_FROM_START);
			} else {
				mList.setMode(Mode.BOTH);
			}
			if (adapter.getCount() <= 0) {
				mList.setEmptyView(this.getEmptyView());
			}else{
				this.removeEmptyView();
			}
			return;
		}
		//移动
		if(ID_MOVE == id){
			if(obj.isOK()){
				showToast("移动成功");
				//添加
				adapter.addToListHead(formIt);
				//删除文件夹
				Intent i = new Intent(IBroadcastAction.ACTION_U_MOVE);
				i.putExtra("UFileItem", JSON.toJSONString(formIt));
				sendBroadcast(i);
				this.finish();
			}else{
				showToast(obj.getMsg());
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		UFileItem it = adapter.getItem(arg2 - 1);
		if (it.isFloder()) {
			Intent intent = new Intent(this, UMySelectFileListAct.class);
			intent.putExtra("UFileItem",JSON.toJSONString(formIt));
			intent.putExtra("srchType", srchType);
			intent.putExtra("title", title);
			intent.putExtra("folderid", it.getId());
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			this.startActivity(intent);
		} else {
			// Intent intent = new Intent(this, FileShowAct.class);
			// intent.putExtra("AttacheFileItem",
			// JSON.toJSONString(AttacheFileItem.init(it)));
			// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// startActivity(intent);
		}
	}
}


