package cn.com.phinfo.oaact;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.com.phinfo.adapter.EmailAdapter;
import cn.com.phinfo.oaact.base.SearchBaseAct;
import cn.com.phinfo.protocol.EmailListRun;
import cn.com.phinfo.protocol.LURLInterface;
import cn.com.phinfo.protocol.EmailListRun.EmailItem;
import cn.com.phinfo.protocol.EmailListRun.EmailListResultBean;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;

public class SearchEmailAct extends SearchBaseAct implements OnItemClickListener {
	private EmailAdapter adapter = null;
	private String BOX="";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(this.getIntent()!=null&&this.getIntent().getExtras()!=null){
			BOX = this.getIntent().getExtras().getString("BOX");
		}
		adapter = new EmailAdapter();
		refreshListView.setAdapter(adapter);
	}

	protected void onRefresh() {
		this.quickHttpRequest(ID_LIST, new EmailListRun(page,BOX,queryEdit.getText().toString().trim()));
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		EmailItem it = adapter.getItem(arg2 - 1);
		Intent intent = new Intent(this,EmailDetailAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("EmailItem", JSON.toJSONString(it));
		intent.putExtra("TITLE", "查询");
		this.startActivity(intent);
		this.finish();

	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj, Object requestObj) {
		if (ID_LIST == id) {
			if (obj.isOK()) {
				EmailListResultBean o = (EmailListResultBean) obj;
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
			} else {
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
