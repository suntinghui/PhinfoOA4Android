package cn.com.phinfo.oaact;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.HttpLoginMyActBase;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.adapterbase.ViewSyncPagerAdapter;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.heqifuhou.pulltorefresh.PullToRefreshListView;

import androidx.viewpager.widget.ViewPager;
import cn.com.phinfo.adapter.TodosAdapter;
import cn.com.phinfo.protocol.TodosRun;
import cn.com.phinfo.protocol.TodosRun.TodosItem;
import cn.com.phinfo.protocol.TodosRun.TodosResultBean;

//待办
public class TodosAct extends HttpLoginMyActBase
		implements ViewPager.OnPageChangeListener, OnItemClickListener, OnCheckedChangeListener {
	private static int PERPAGE_SIZE = 15;
	private ViewPager viewPager;
	private RadioButton[] rbtn = new RadioButton[4];
	private PullToRefreshListView[] refreshList = new PullToRefreshListView[4];
	private TodosAdapter[] adapter = new TodosAdapter[4];
	private int[] pageNumber = new int[4];
	private boolean[] pageInit = new boolean[4];
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addTitleView(this.getLayoutInflater(R.layout.nav_title_tab));
		View v = this.findViewById(R.id.nav_back);
		v.setOnClickListener(onBackListener);
		this.findViewById(R.id.nav_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startTodoTypeListAct();
			}
		});
		this.addViewFillInRoot(R.layout.act_todos);
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		refreshList[0] = (PullToRefreshListView) findViewById(R.id.list1);
		refreshList[1] = (PullToRefreshListView) findViewById(R.id.list2);
		refreshList[2] = (PullToRefreshListView) findViewById(R.id.list3);
		refreshList[3] = (PullToRefreshListView) findViewById(R.id.list4);

		rbtn[0] = (RadioButton) findViewById(R.id.my_tab1);
		rbtn[1] = (RadioButton) findViewById(R.id.my_tab2);
		rbtn[2] = (RadioButton) findViewById(R.id.my_tab3);
		rbtn[3] = (RadioButton) findViewById(R.id.my_tab4);

		ViewSyncPagerAdapter pagerAdapter = new ViewSyncPagerAdapter(refreshList);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setOnPageChangeListener(this);
		
		RadioGroup tab = (RadioGroup) findViewById(R.id.radiogroup);
		tab.setOnCheckedChangeListener(this);
		for(int i=0;i<refreshList.length;i++){
			adapter[i] = new TodosAdapter();
			refreshList[i].setAdapter(adapter[i]);
			refreshList[i].setOnItemClickListener(this);
			refreshList[i].setOnRefreshListener(new OnRefreshListener2() {
				@Override
				public void onPullDownToRefresh(PullToRefreshBase refreshView) {
					pageNumber[viewPager.getCurrentItem()] = 1;
					onRefresh();
					hideLoading(true);
				}

				@Override
				public void onPullUpToRefresh(PullToRefreshBase refreshView) {
					onRefresh();
					hideLoading(true);
				}
			});
		}
		initRefresh();
		this.findViewById(R.id.queryBtn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(TodosAct.this,SearchTodosAct.class);
				intent.putExtra("IDX", viewPager.getCurrentItem());
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
	}
	
	private void startTodoTypeListAct(){
		Intent intent = new Intent(this,TodoTypeListAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}
	private void initRefresh(){
		for(int i=0;i<refreshList.length;i++){
			pageNumber[i]=1;
			pageInit[i]=false;
		}
		this.quickHttpRequest(0, new TodosRun(0,"",pageNumber[0]));
		pageInit[0] = true;
	}
    protected void onRefresh() {
    	int idx = viewPager.getCurrentItem();
    	this.quickHttpRequest(idx, new TodosRun(idx,"",pageNumber[idx]));
    }

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj, Object requestObj) {
		if(obj.isOK()){
			TodosResultBean o = (TodosResultBean)obj;
			if(pageNumber[viewPager.getCurrentItem()] == 1){
				adapter[id].clear();
			}
			adapter[id].addToListBack(o.getListData());
			pageNumber[viewPager.getCurrentItem()]++;
			if(o.getListData().size()<PERPAGE_SIZE){
				refreshList[id].setMode(Mode.PULL_FROM_START);
			}else{
				refreshList[id].setMode(Mode.BOTH);
			}
		}else{
			this.showToast(obj.getMsg());
		}
		if (adapter[id].getCount() <= 0) {
			refreshList[id].setEmptyView(this.getLayoutInflater(R.layout.empty));
		}
	}

	public void onHttpForResult(int id, HttpResultBeanBase obj, Object requestObj) {
		super.onHttpForResult(id, obj, requestObj);
		// 下拉请求完成
		int idx = viewPager.getCurrentItem();
		refreshList[idx].onRefreshComplete();
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
		TodosItem it = adapter[viewPager.getCurrentItem()].getItem(arg2-1);
		Intent intent = new Intent(this,TodosDetailAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("TodosItem", JSON.toJSONString(it));
		this.startActivity(intent);
		
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
		int idx = viewPager.getCurrentItem();
		if(!pageInit[idx]){
			pageNumber[idx] = 1;
			this.onRefresh();
			pageInit[idx] = true;
		}
	}
	
	protected void onBroadcastReceiverListener(Context context, Intent intent) {
		super.onBroadcastReceiverListener(context, intent);
		if(intent.getAction().equals(IBroadcastAction.ACTION_DO_OK)){
			String refreshID  = intent.getExtras().getString("REFRESHID");
			adapter[0].delByID(refreshID);
		}
	};
	
}
