package cn.com.phinfo.oaact.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.com.phinfo.adapter.UFileAttacheAdapter;
import cn.com.phinfo.adapter.UFileAttacheAdapter.OnUFileItemClickListener;
import cn.com.phinfo.oaact.CreateUFileEmailAct;
import cn.com.phinfo.oaact.FileShowAct;
import cn.com.phinfo.oaact.R;
import cn.com.phinfo.oaact.SearchUFileAct;
import cn.com.phinfo.oaact.UMyAwayFileListAct;
import cn.com.phinfo.oaact.UMySelectFileListAct;
import cn.com.phinfo.oaact.URenameAct;
import cn.com.phinfo.protocol.AttacheFileRun.AttacheFileItem;
import cn.com.phinfo.protocol.FileSearchRun;
import cn.com.phinfo.protocol.FileSearchRun.FileSearchResultBean;
import cn.com.phinfo.protocol.FileSearchRun.UFileItem;
import cn.com.phinfo.protocol.UDirDelRun;
import cn.com.phinfo.protocol.UFileDelRun;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.imgutils.FileItem;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.heqifuhou.pulltorefresh.PullToRefreshListView;
import com.heqifuhou.utils.FileUtils;
import com.heqifuhou.utils.ParamsCheckUtils;
import com.heqifuhou.view.ConfirmDialog;
import com.heqifuhou.view.ConfirmDialog.OnDialogOKListener;
import com.heqifuhou.view.PopupWindows;
import com.heqifuhou.view.PopupWindows.OnPopupWindowsItemListener;

public abstract class UMyFileListBaseAct extends HttpMyActBase implements
		OnItemClickListener, OnUFileItemClickListener {
	private static final int ID_DEL = 0x10, ID_GETLST = 0x11;
	protected int page;
	protected PullToRefreshListView mList = null;
	protected UFileAttacheAdapter adapter = null;
	protected String srchType, title;
	protected String folderid = "";
	protected PopupWindows repayPop;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		srchType = this.getIntent().getExtras().getString("srchType");
		title = this.getIntent().getExtras().getString("title");
		folderid = this.getIntent().getExtras().getString("folderid");
		if (ParamsCheckUtils.isNull(folderid)) {
			folderid = "";
		}
		this.init();
		mList.setMode(Mode.PULL_FROM_START);
		adapter = new UFileAttacheAdapter();
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
		// 事件
		adapter.setOnUFileItemClickListener(this);
		onRefresh();
	}

	protected abstract void init();

	// 文件夹
	protected void dirPopAction(final UFileItem it) {
		if (repayPop != null && repayPop.isShowing()) {
			return;
		}
		repayPop = new PopupWindows(this, this.findViewById(R.id.root),
				new String[] { "重命名", "移动", "删除" });
		repayPop.show();
		repayPop.setOnPopupWindowsItemListener(new OnPopupWindowsItemListener() {
			@Override
			public void onPopupWindowsItem(int pos) {
				switch (pos) {
				case 0:// 重命名
					renameFile(it);
					break;
				case 1:// 移动
					remove(it);
					break;
				case 2:// 删除
					delFile(it);
					break;
				}
			}
		});
	}

	// 文件
	protected void filePopAction(final UFileItem it) {
		if (repayPop != null && repayPop.isShowing()) {
			return;
		}
		repayPop = new PopupWindows(this, this.findViewById(R.id.root),
				new String[] { "发送给联系人", "发邮件" });
		repayPop.show();
		repayPop.setOnPopupWindowsItemListener(new OnPopupWindowsItemListener() {
			@Override
			public void onPopupWindowsItem(int pos) {
				switch (pos) {
				case 0:// 发送给联系人
					break;
				case 1:// 发邮件
					sendEmail(it);
					break;
				}
			}
		});
	}

	protected void sendEmail(final UFileItem _it) {
		FileItem it = new FileItem();
		it.setId(_it.getId());
		it.setFileExtension(_it.getFileExtension());
		it.setName(_it.getName());
		it.setLink(_it.getLink());
		it.setIsLocalFile(false);
		Intent intent = new Intent(this, CreateUFileEmailAct.class);
		intent.putExtra("FileItem", JSON.toJSONString(it));
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}

	protected void remove(final UFileItem it) {
		Intent intent = new Intent(this, UMySelectFileListAct.class);
		intent.putExtra("UFileItem",JSON.toJSONString(it));
		intent.putExtra("srchType","my");
		intent.putExtra("title","我的文件");
		intent.putExtra("folderid", "10010000-0000-0000-0000-000000000001");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);

	}

	protected ConfirmDialog confirmDialog;

	// 删除
	protected void delFile(final UFileItem it) {
		if (confirmDialog != null && confirmDialog.isShowing()) {
			return;
		}
		confirmDialog = new ConfirmDialog(this, "确定删除吗?", null);
		confirmDialog.setOnDialogOKListener(new OnDialogOKListener() {
			@Override
			public void onOKItem(Object obj) {
				if (it.isFloder()) {
					quickHttpRequest(ID_DEL, new UDirDelRun(it.getId()), it);
				} else {
					quickHttpRequest(ID_DEL, new UFileDelRun(it.getId()), it);
				}
			}
		});
		confirmDialog.show();

	}

	// 重命名
	protected void renameFile(final UFileItem it) {
		Intent intent = new Intent(this, URenameAct.class);
		intent.putExtra("UFileItem", JSON.toJSONString(it));
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}

	public void onHttpForResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		mList.onRefreshComplete();
		super.onHttpForResult(id, obj, requestObj);
	}

	protected void onRefresh() {
		this.quickHttpRequest(ID_GETLST, new FileSearchRun(srchType, folderid,
				page, ""));
	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if (ID_GETLST == id) {
			FileSearchResultBean o = (FileSearchResultBean) obj;
			if (page == 1) {
				adapter.clear();
			}
			adapter.addToListBack(o.getFolders());
			adapter.addToListBack(o.getFiles());
			page++;
			int n = o.getFolders().size() + o.getFiles().size();
			if (n < 15) {
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
		// 删除
		if (ID_DEL == id) {
			if (obj.isOK()) {
				UFileItem it = (UFileItem) requestObj;
				adapter.tryRemove(it);
				showToast("删除成功");
			} else {
				showToast(obj.getMsg());
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		UFileItem it = adapter.getItem(arg2 - 1);
		if (it.isFloder()) {
			Intent intent = new Intent(this, UMyAwayFileListAct.class);
			intent.putExtra("srchType", srchType);
			intent.putExtra("title", title);
			intent.putExtra("folderid", it.getId());
			this.startActivity(intent);
		} else {
			/**
			Intent intent = new Intent(this, FileShowAct.class);
			intent.putExtra("AttacheFileItem",
					JSON.toJSONString(AttacheFileItem.init(it)));
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			 **/

			AttacheFileItem attacheItem = AttacheFileItem.init(it);
			Log.e("yao", JSON.toJSONString(attacheItem));
			FileUtils.downloadAndOpenFile(this, attacheItem);


		}
	}

	protected void onBroadcastReceiverListener(Context context, Intent intent) {
		if (IBroadcastAction.ACTION_U_RENAME.equals(intent.getAction())) {
			String s = intent.getExtras().getString("UFileItem");
			UFileItem it = JSON.parseObject(s, UFileItem.class);
			adapter.reName(it);
			return;
		}
		// 删除移动
		if (IBroadcastAction.ACTION_U_MOVE.equals(intent.getAction())) {
			String s = intent.getExtras().getString("UFileItem");
			UFileItem it = JSON.parseObject(s, UFileItem.class);
			adapter.del(it);
		}
	}

	@Override
	public void onUFileItemListener(UFileItem it) {
		if (it.isFloder()) {
			dirPopAction(it);
		} else {
			filePopAction(it);
		}
	};
	
	//查询
	protected void startSearchUFileAct() {
		Intent intent = new Intent(this, SearchUFileAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}

}
