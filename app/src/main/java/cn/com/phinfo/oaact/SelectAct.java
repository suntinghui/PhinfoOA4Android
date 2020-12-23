package cn.com.phinfo.oaact;

import java.util.List;
import java.util.Stack;

import com.heqifuhou.actbase.MyActBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;
import com.heqifuhou.pulltorefresh.PullToRefreshListView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.com.phinfo.adapter.SelectAdapter;
import cn.com.phinfo.entity.SelectItem;

//选择问题
public class SelectAct extends MyActBase implements OnItemClickListener {
	private PullToRefreshListView mList = null;
	private SelectAdapter adapter = null;
	private String TITLE;
	private String DATA;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TITLE = this.getIntent().getExtras().getString("TITLE");
		DATA = this.getIntent().getExtras().getString("DATA");
		this.addTextNav(TITLE);
		this.addViewFillInRoot(R.layout.act_select);
		mList = (PullToRefreshListView) this.findViewById(R.id.refreshListView);
		mList.setMode(Mode.DISABLED);
		adapter = new SelectAdapter();
		mList.setAdapter(adapter);
		mList.setOnItemClickListener(this);
		onRefresh();
	}

	protected void onRefresh() {
		List<SelectItem> ls = new Stack<SelectItem>();
		String[] s = DATA.split(",");
		for(int i=0;i<s.length;i++){
			SelectItem it = new SelectItem();
			it.setId(i);
			it.setText(s[i]);
			ls.add(it);
		}
		adapter.replaceListRef(ls);
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		SelectItem it = adapter.getItem(arg2-1);
		Intent data = new Intent();
		data.putExtra("SelectItem", it);
		setResult(RESULT_OK, data);
		finish();
	}
}
