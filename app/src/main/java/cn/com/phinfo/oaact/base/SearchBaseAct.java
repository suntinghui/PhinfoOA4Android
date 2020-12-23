package cn.com.phinfo.oaact.base;

import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.heqifuhou.pulltorefresh.PullToRefreshListView;
import com.heqifuhou.utils.ParamsCheckUtils;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import cn.com.phinfo.oaact.R;

public abstract class SearchBaseAct extends HttpMyActBase implements OnItemClickListener {
	protected static int PERPAGE_SIZE = 15;
	protected static int ID_LIST = 0x10;
	protected PullToRefreshListView refreshListView = null;
	protected int page;
	protected EditText queryEdit;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTextNav("");
		this.addViewFillInRoot(R.layout.act_search_base);
		this.findViewById(R.id.my_form_base_context).setBackgroundColor(0x00000000);
		this.queryEdit = (EditText) this.findViewById(R.id.queryEdit);
		refreshListView = (PullToRefreshListView) this.findViewById(R.id.refreshListView);
		refreshListView = (PullToRefreshListView) this.findViewById(R.id.refreshListView);
		refreshListView.setMode(Mode.BOTH);
		refreshListView.setOnItemClickListener(this);
		refreshListView.setOnRefreshListener(new OnRefreshListener2() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				page = 1;
				onRefresh();
				hideLoading(true);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				onRefresh();
				hideLoading(true);
			}
		});
		// 查询
		this.findViewById(R.id.queryBtn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String squery = queryEdit.getText().toString().trim();
				if (ParamsCheckUtils.isNull(squery)) {
					showToast("请输入关键字");
					return;
				}
				page = 1;
				onRefresh();
			}
		});
	}

	protected void onRefresh() {

	}

	public void onHttpForResult(int id, HttpResultBeanBase obj, Object requestObj) {
		super.onHttpForResult(id, obj, requestObj);
		refreshListView.onRefreshComplete();
	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj, Object requestObj) {

	}
}
