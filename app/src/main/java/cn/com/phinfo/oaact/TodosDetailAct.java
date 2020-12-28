package cn.com.phinfo.oaact;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.HttpLoginMyActBase;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.adapterbase.ViewSyncPagerAdapter;
import com.heqifuhou.chrome.INetWebChromeClient;
import com.heqifuhou.chrome.INetWebViewClient;
import com.heqifuhou.chrome.MyWebView;
import com.heqifuhou.chrome.MyWebViewClient;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.heqifuhou.pulltorefresh.PullToRefreshMyWebView;
import com.heqifuhou.utils.FileUtils;
import com.heqifuhou.utils.ParamsCheckUtils;
import com.heqifuhou.view.ConfirmDialog;
import com.heqifuhou.view.ConfirmDialog.OnDialogOKListener;
import com.heqifuhou.view.EditDialog;
import com.heqifuhou.view.EditDialog.OnDialogEditOKListener;
import com.heqifuhou.view.PopupWindows;
import com.heqifuhou.view.PopupWindows.OnPopupWindowsItemListener;

import androidx.viewpager.widget.ViewPager;
import cn.com.phinfo.adapter.FileAttacheAdapter;
import cn.com.phinfo.protocol.AttacheFileRun;
import cn.com.phinfo.protocol.AttacheFileRun.AttacheFileItem;
import cn.com.phinfo.protocol.AttacheFileRun.AttacheFileResultBean;
import cn.com.phinfo.protocol.CancelSubmitRun;
import cn.com.phinfo.protocol.LURLInterface;
import cn.com.phinfo.protocol.PushMessageRun;
import cn.com.phinfo.protocol.TodosRun.TodosItem;
import cn.com.phinfo.view.TodosPopMenu;

//表单
public class TodosDetailAct extends HttpLoginMyActBase implements ViewPager.OnPageChangeListener, OnItemClickListener,
		OnCheckedChangeListener, OnRefreshListener<MyWebView>, INetWebViewClient, INetWebChromeClient {
	private static int ID_CANCEL = 0x10, ID_PUSHMSG = 0x12, ID_Attach = 0x13, ID_FORM = 0x14,ID_NOTIFY_READ=0x15,ID_transferagent=0x16;
	private TodosItem it;
	private ViewPager viewPager;
	private RadioButton[] rbtn = new RadioButton[4];
	private PopupWindows formPop, filePop;
	private PullToRefreshMyWebView[] mRefWebView = new PullToRefreshMyWebView[3];
	private MyWebView[] mWebView = new MyWebView[3];
	private ViewGroup[] viewArr = new ViewGroup[4];
	private ListView refreshList1, refreshList2;
	private FileAttacheAdapter attache1, attache2;
	private View navText;
	private ConfirmDialog delConfirm;
	private EditDialog editDialog;
	private TodosPopMenu todos = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.it = JSON.parseObject(this.getIntent().getExtras().getString("TodosItem"), TodosItem.class);
		addTitleView(this.getLayoutInflater(R.layout.nav_todo_detail_tab));
		View v = this.findViewById(R.id.nav_back);
		v.setOnClickListener(onBackListener);
		// 更多
		navText = this.findViewById(R.id.nav_btn);
		navText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				formAction();
			}
		});
		this.addViewFillInRoot(R.layout.act_todos_detail);
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		viewArr[0] = (ViewGroup) this.findViewById(R.id.view1);
		viewArr[1] = (ViewGroup) this.findViewById(R.id.view2);
		viewArr[2] = (ViewGroup) this.findViewById(R.id.view3);
		viewArr[3] = (ViewGroup) this.findViewById(R.id.view4);
		for (int i = 0; i < mRefWebView.length; i++) {
			mRefWebView[i] = new PullToRefreshMyWebView(this, this, this);
			mRefWebView[i].setMode(Mode.DISABLED);
			mRefWebView[i].setOnRefreshListener(this);
			mWebView[i] = mRefWebView[i].getRefreshableView();

			if (i >= 1) {
				viewArr[i + 1].addView(mRefWebView[i], LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			} else {
				viewArr[i].addView(mRefWebView[i], LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			}
		}
		refreshList1 = (ListView) this.findViewById(R.id.list1);
		refreshList2 = (ListView) this.findViewById(R.id.list2);

		rbtn[0] = (RadioButton) findViewById(R.id.my_tab1);
		rbtn[1] = (RadioButton) findViewById(R.id.my_tab2);
		rbtn[2] = (RadioButton) findViewById(R.id.my_tab3);
		rbtn[3] = (RadioButton) findViewById(R.id.my_tab4);

		ViewSyncPagerAdapter pagerAdapter = new ViewSyncPagerAdapter(viewArr);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setOnPageChangeListener(this);

		RadioGroup tab = (RadioGroup) findViewById(R.id.radiogroup);
		tab.setOnCheckedChangeListener(this);
		refreshList1.setOnItemClickListener(this);
		refreshList2.setOnItemClickListener(this);

		attache1 = new FileAttacheAdapter(false);
		attache2 = new FileAttacheAdapter(true);

		refreshList1.setAdapter(attache1);
		refreshList2.setAdapter(attache2);

		this.findViewById(R.id.btn_down).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				downloadAll();
			}
		});
		initRequest();
	}
	
	protected void onResume(){
		super.onResume();
		mHandler.sendEmptyMessage(0);
	}
	protected void onStop(){
		super.onStop();
		if(mHandler.hasMessages(0)){
			mHandler.removeMessages(0);
		}
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(todos!=null&&todos.onActivityResult(requestCode, resultCode, data)){
			return;
		}
		super.onActivityResult( requestCode,  resultCode,  data);
	}
	


	// 表单
	private void formAction() {
		if (formPop != null && formPop.isShowing()) {
			return;
		}
		formPop = new PopupWindows(this, viewPager, new String[] { "转发", "转代理", "催办", "撤回提交" });
		formPop.show();
		formPop.setOnPopupWindowsItemListener(new OnPopupWindowsItemListener() {
			@Override
			public void onPopupWindowsItem(int pos) {
				if (pos == 0) {
					// 转发
					showTodosDiaolog(0);
				} else if (pos == 1) {
					// 转代理
					showTodosDiaolog(1);
				} else if (pos == 2) {
					// 催办
					editDialog();
				} else if (pos == 3) {
					// 撤销
					delConfig();
				}
			}
		});
	}

	private void showTodosDiaolog(int npos){
		Intent intent = new Intent(TodosDetailAct.this, TodoRelayAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("NPOS", npos);
		intent.putExtra("TITLE", npos==0?"转发":"转代理");
		intent.putExtra("TodosItem", JSON.toJSONString(it));
		startActivity(intent);
//		
//		if(todos!=null&&todos.isShowing()){
//			return;
//		}
//		todos = new TodosPopMenu(this,viewPager, npos);
//		todos.setOnDialogTodoOKListener(new OnDialogTodoOKListener(){
//			@Override
//			public void onTodoOKItem(String txt, String menber,Object obj) {
//				Integer i = (Integer)obj;
//				if(i.intValue()==0){
//					quickHttpRequest(ID_NOTIFY_READ, new NotifyReadRun(it.getName(),it.getProcessInstanceId(),menber,txt));
//					return;
//				}
//				if(i.intValue() == 1){
//					quickHttpRequest(ID_transferagent, new NotifyReadRun(it.getName(),it.getProcessInstanceId(),menber,txt));
//				}
//			}
//		});
//		todos.show();
	}


	// 催办
	private void editDialog() {
		if (editDialog != null && editDialog.isShowing()) {
			return;
		}
		editDialog = new EditDialog(this, null);
		editDialog.show();
		editDialog.setOnDialogEditOKListener(new OnDialogEditOKListener() {
			@Override
			public void onEditOKItem(String txt, Object obj) {
				quickHttpRequest(ID_PUSHMSG, new PushMessageRun(it.getId(), it.getInstanceId(), txt));
			}
		});
	}

	// 撤销
	private void delConfig() {
		if (delConfirm != null && delConfirm.isShowing()) {
			return;
		}
		delConfirm = new ConfirmDialog(this, "确定撤销提交", null);
		delConfirm.show();
		delConfirm.setOnDialogOKListener(new OnDialogOKListener() {
			@Override
			public void onOKItem(Object obj) {
				quickHttpRequest(ID_CANCEL, new CancelSubmitRun(it.getId(), it.getInstanceId()));
			}
		});
	}

	private void initRequest() {
		String s1 = LURLInterface.GET_URL_HANDLEWFINSTANCE(it.getInstanceId(), it.getId());
		String s2 = LURLInterface.GET_URL_MONITOR(it.getInstanceId());
		String s3 = LURLInterface.GET_URL_READLIST(it.getInstanceId());
		mWebView[0].loadUrl(s1);
		mWebView[1].loadUrl(s2);
		mWebView[2].loadUrl(s3);
		this.quickHttpRequest(ID_Attach, new AttacheFileRun(it.getProcessInstanceId()));
	}

	protected void onRefresh() {
		int idx = viewPager.getCurrentItem();
	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj, Object requestObj) {
		// 取消
		if (id == ID_CANCEL) {
			this.showToast(obj.getMsg());
			return;
		}
		// 催办
		if (ID_PUSHMSG == id) {
			this.showToast(obj.getMsg());
			return;
		}
		// 附件列表
		if (ID_Attach == id) {
			if (obj.isOK()) {
				AttacheFileResultBean o = (AttacheFileResultBean) obj;
				attache1.replaceListRef(o.getFile());
				attache2.replaceListRef(o.getAttach());
			} else {
				this.showToast(obj.getMsg());
			}
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int position) {
		rbtn[position].setChecked(true);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		AttacheFileItem it = null;
		if (arg0.getAdapter() == attache1) {
			it = attache1.getItem(arg2);
		}
		if (arg0.getAdapter() == attache2) {
			it = attache2.getItem(arg2);
		}
		final AttacheFileItem attacheItem = it;
		if (filePop != null && filePop.isShowing()) {
			return;
		}
		filePop = new PopupWindows(this, viewPager, new String[] { "查看", "下载", "邮件", "共享" });
		filePop.show();
		filePop.setOnPopupWindowsItemListener(new OnPopupWindowsItemListener() {
			@Override
			public void onPopupWindowsItem(int pos) {
				if (pos == 0) {
					// 查看
					/**
					Intent intent = new Intent(TodosDetailAct.this, FileShowAct.class);
					intent.putExtra("AttacheFileItem", JSON.toJSONString(attacheItem));
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					 **/
					FileUtils.downloadAndOpenFile(TodosDetailAct.this, attacheItem);

				} else if (pos == 1) {
					// 下载
					attacheItem.startDownLoad();
				} else if (pos == 2) {
					// 邮件

				} else if (pos == 3) {
					// 共享

				}
			}
		});
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int checkedId) {
		switch (checkedId) {
		case R.id.my_tab1:
			viewPager.setCurrentItem(0);
			break;
		case R.id.my_tab2:
			viewPager.setCurrentItem(1);
			break;
		case R.id.my_tab3:
			viewPager.setCurrentItem(2);
			break;
		case R.id.my_tab4:
			viewPager.setCurrentItem(3);
			break;
		}
		// int idx = viewPager.getCurrentItem();
		// if (idx == 0) {
		// navText.setVisibility(View.VISIBLE);
		// } else {
		// navText.setVisibility(View.INVISIBLE);
		// }
	}

	@Override
	public void onRefresh(PullToRefreshBase<MyWebView> refreshView) {
		refreshView.getRefreshableView().reload();
	}

	@Override
	public void onProgressChanged(WebView view, int newProgress) {
	}

	@Override
	public void onReceivedTitle(WebView view, String title) {
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		return false;
	}

	@Override
	public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
		this.hideLoading(true);
	}

	@Override
	public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {

	}

	@Override
	public void onPageFinished(WebView view, String url) {
		this.hideLoading(true);

	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		//成功的ID
		String refreshID = MyWebViewClient.getValueByName(url, "refreshID");
		if(!ParamsCheckUtils.isNull(refreshID)){
			//刷新待办列表页面
			Intent i = new Intent(IBroadcastAction.ACTION_DO_OK);
			i.putExtra("REFRESHID", refreshID);
			sendBroadcast(i);
		}
		// 退出
		if ("1".equals(MyWebViewClient.getValueByName(url, "exitInstacelist"))) {
			this.finish();
			return;
		}
		this.showLoading();
	}

	@Override
	public void onLoadResource(WebView view, String url) {

	}

	@Override
	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		this.hideLoading(true);
	}

	private void downloadAll() {
		attache1.startAll();
		attache2.startAll();
	}
	private Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			attache1.notifyDataSetChanged();
			attache2.notifyDataSetChanged();
			mHandler.sendEmptyMessageDelayed(0, 200);
		}
	};
}
