package cn.com.phinfo.oaact;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.com.phinfo.adapter.EmailAdapter;
import cn.com.phinfo.protocol.EmailListRun;
import cn.com.phinfo.protocol.EmailListRun.EmailItem;
import cn.com.phinfo.protocol.EmailListRun.EmailListResultBean;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.heqifuhou.pulltorefresh.PullToRefreshListView;

//邮件列表
public class EmailListAct extends HttpMyActBase implements OnItemClickListener {
	private int page;
	private PullToRefreshListView mList = null;
	private EmailAdapter adapter = null;
	private String BOX,TITLE;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TITLE = this.getIntent().getExtras().getString("TITLE");
		BOX = this.getIntent().getExtras().getString("BOX");
		this.addTitleView(R.layout.nav_title_email_img);
		this.addTextNav(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(EmailListAct.this, CreateEmailAct.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		},TITLE,R.drawable.ic_add);
		this.addViewFillInRoot(R.layout.act_email_refresh);
		mList = (PullToRefreshListView) this.findViewById(R.id.refreshListView);
		mList.setMode(Mode.BOTH);
		adapter = new EmailAdapter();
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
				startSearchEmailAct();
			}
		});
		onRefresh();
	}
	private void startSearchEmailAct() {
		Intent intent = new Intent(this, SearchEmailAct.class);
		intent.putExtra("BOX", BOX);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}
	protected void onRefresh() {
		this.quickHttpRequest(0x10, new EmailListRun(page,BOX));
	}
	
	protected void onBroadcastReceiverListener(Context context, Intent intent) {
		if(IBroadcastAction.ACTION_EMAIL_DEL.equals(intent.getAction())){
			//删除
			String s = intent.getExtras().getString("EmailItem");
			EmailItem it = JSON.parseObject(s, EmailItem.class);
			adapter.del(it);
			return;
		}
	};
	

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj, Object requestObj) {
		EmailListResultBean o = (EmailListResultBean) obj;
		if (page == 1) {
			adapter.clear();
		}
		adapter.addToListBack(o.getListData());
		page++;
		if (o.getListData().size()<15) {
			mList.setMode(Mode.PULL_FROM_START);
		} else {
			mList.setMode(Mode.BOTH);
		}
		if (adapter.getCount() <= 0) {
			mList.setEmptyView(this.getEmptyView());
		}else{
			this.removeEmptyView();
		}
	}
	public void onHttpForResult(int id, HttpResultBeanBase obj, Object requestObj) {
		super.onHttpForResult(id, obj, requestObj);
		mList.onRefreshComplete();
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		EmailItem it = adapter.getItem(arg2-1);
		Intent intent = new Intent(this,EmailDetailAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("EmailItem", JSON.toJSONString(it));
		intent.putExtra("TITLE", TITLE);
		intent.putExtra("BOX", BOX);
		this.startActivity(intent);
		adapter.setRead(it);
	}
}
