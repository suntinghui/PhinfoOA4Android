package cn.com.phinfo.oaact;

import java.util.List;

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
import android.widget.TextView;
import cn.com.phinfo.adapter.HorizontalAdapter;
import cn.com.phinfo.adapter.NewsAdapter;
import cn.com.phinfo.db.NewsChanelAddressDB;
import cn.com.phinfo.entity.HomeItem;
import cn.com.phinfo.protocol.NewsChannelRun;
import cn.com.phinfo.protocol.NewsChannelRun.HSelectItem;
import cn.com.phinfo.protocol.NewsChannelRun.NewsChannelResultBean;
import cn.com.phinfo.protocol.NewsDefaultListRun;
import cn.com.phinfo.protocol.NewsDefaultListRun.NewsItem;
import cn.com.phinfo.protocol.NewsDefaultListRun.NewsResultBean;
import cn.com.phinfo.protocol.NewsHotListRun;
import cn.com.phinfo.protocol.NewsListRun;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.hlistview.HorizontalListView;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.heqifuhou.pulltorefresh.PullToRefreshListView;
import com.heqifuhou.utils.ParamsCheckUtils;

public class NewsAct extends HttpMyActBase implements OnItemClickListener {
	private static int PERPAGE_SIZE = 15;
	private static int ID_GETNEWS = 0x10, ID_GETLIST = 0x11;
	private HorizontalListView scrollbar;
	private HorizontalAdapter hAdapter = null;
	private PullToRefreshListView refreshListView = null;
	private NewsAdapter newsAdapter = null;
	private int page;
	private HSelectItem currIt;
	private RadioGroup radioGroup;
	private List<HomeItem> newsList;
	private HomeItem needSel;
	private int contentTypeCode = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (this.getIntent() != null && this.getIntent().getExtras() != null) {
			contentTypeCode = this.getIntent().getExtras()
					.getInt("contentTypeCode");
			newsList = (List<HomeItem>) this.getIntent().getExtras()
					.getSerializable("NEWSLIST");
			needSel = (HomeItem) this.getIntent().getExtras()
					.getSerializable("HomeItem");
		}
		this.addBottomView(R.layout.news_tools_bar);
		this.addViewFillInRoot(R.layout.act_news);
		TextView title = (TextView) this.findViewById(R.id.title);
		if (contentTypeCode == 1) {
			title.setText("新闻");
		} else {
			title.setText("通知");
		}
		scrollbar = (HorizontalListView) this.findViewById(R.id.TagList);
		hAdapter = new HorizontalAdapter();
		scrollbar.setAdapter(hAdapter);
		scrollbar.setOnItemClickListener(this);

		this.findViewById(R.id.nav_back).setOnClickListener(onBackListener);
		// 添加
		this.findViewById(R.id.moreBlack).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(NewsAct.this, AddNewsAct.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);

					}
				});
		// 查询
		this.findViewById(R.id.filterBtn).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(NewsAct.this, SearchNewsAct.class);
						intent.putExtra("HSelectItem", JSON.toJSONString(currIt));
						intent.putExtra("contentTypeCode", contentTypeCode);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}
				});
		radioGroup = (RadioGroup) this.findViewById(R.id.main_radio);
		radioGroup.setOnCheckedChangeListener(ocheckedListener);
		refreshListView = (PullToRefreshListView) this
				.findViewById(R.id.refreshListView);
		newsAdapter = new NewsAdapter();
		refreshListView.setAdapter(newsAdapter);
		refreshListView = (PullToRefreshListView) this
				.findViewById(R.id.refreshListView);
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
		// 先显示两个
		reloadHList();
		// 如果找的项目已经存在
		if (!trySetActivate()) {
			// 加载数据
			reloadListData();
		}
		requsetChannelList();
	}

	// 处理按钮组的事件
	private final OnCheckedChangeListener ocheckedListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup arg0, int arg1) {
			switch (arg1) {
			case R.id.home_btn:
				currIt = hAdapter.setHSelectItemDefSel();
				page = 1;
				onRefresh();
				break;
			case R.id.video_btn:
			case R.id.fllow_btn:
			case R.id.subscribe_btn:
				RadioButton r = (RadioButton) findViewById(R.id.home_btn);
				r.setChecked(true);
				showToast("建设中");
				break;
			}
		}
	};

	protected void onBroadcastReceiverListener(Context context, Intent intent) {
		super.onBroadcastReceiverListener(context, intent);
		if (intent.getAction().equals(IBroadcastAction.ACTION_CHANEL_CHANGE)) {
			reloadHList();
			reloadListData();
		}
	};

	// 重新初始化模过来的数据
	private void reloadHList() {
		// 初始化数据
		hAdapter.clear();
		hAdapter.addToListBack(currIt = HSelectItem.initDefault().setbSel(true));
		hAdapter.addToListBack(HSelectItem.initHot());
		List<HSelectItem> ls = NewsChanelAddressDB.getInstance().getFromDB();
		hAdapter.addToListBack(ls);
	}

	private void reloadListData() {
		newsAdapter.clear();
		page = 1;
		onRefresh();
	}

	// 请求
	private boolean requsetChannelList() {
		// 判断一下是不是第一次加载，如果是的，那么就加载所有的
		List<HSelectItem> ls = NewsChanelAddressDB.getInstance().getFromDB();
		if (ls.isEmpty()) {
			this.quickHttpRequest(ID_GETLIST, new NewsChannelRun());
		} else {
			if (newsList == null) {
				return false;
			}
			// 如果发现请求的这个ID在这个里面没有，那么没有办法了，只能重新请求网络加上了
			for (HomeItem it : newsList) {
				if (!isInLocal(it.getLabel())) {
					this.quickHttpRequest(ID_GETLIST, new NewsChannelRun());
					return true;
				}
			}
		}
		return false;
	}

	protected void onRefresh() {
		if (this.isRequest(ID_GETNEWS)) {
			removeAndStopHttpThread(ID_GETNEWS);
			this.hideLoading();
		}
		// 推荐
		if (currIt.getItemid().equals(String.valueOf(-1))) {
			this.quickHttpRequest(ID_GETNEWS, new NewsDefaultListRun(page,
					contentTypeCode));
			return;
		}
		// 热点
		if (currIt.getItemid().equals(String.valueOf(-2))) {
			this.quickHttpRequest(ID_GETNEWS, new NewsHotListRun(page,
					contentTypeCode));
			return;
		}
		// 选中的
		this.quickHttpRequest(ID_GETNEWS,
				new NewsListRun(page, currIt.getName(), contentTypeCode));
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// 头部的
		if (arg0.getAdapter() == hAdapter) {
			currIt = hAdapter.getItem(arg2);
			hAdapter.setHSelectItemSel(currIt);
			page = 1;
			onRefresh();
			return;
		}
		// 新闻
		NewsItem it = newsAdapter.getItem(arg2 - 1);
		Intent intent = new Intent(NewsAct.this, NewsDetailAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("NewsItem", JSON.toJSONString(it));
		startActivity(intent);

	}

	public void onHttpForResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		super.onHttpForResult(id, obj, requestObj);
		refreshListView.onRefreshComplete();
	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if (ID_GETNEWS == id) {
			if (obj.isOK()) {
				NewsResultBean o = (NewsResultBean) obj;
				if (page == 1) {
					newsAdapter.clear();
				}
				newsAdapter.addToListBack(o.getListData());
				page++;
				if (o.getListData().size() < PERPAGE_SIZE) {
					refreshListView.setMode(Mode.PULL_FROM_START);
				} else {
					refreshListView.setMode(Mode.BOTH);
				}
			} else {
				showToast(obj.getMsg());
			}
			if (newsAdapter.getCount() <= 0) {
				refreshListView.setEmptyView(this.getEmptyView());
			} else {
				removeEmptyView();
			}
			return;
		}
		if (ID_GETLIST == id) {
			if (obj.isOK()) {
				NewsChannelResultBean o = (NewsChannelResultBean) obj;
				List<HSelectItem> netLs = o.getListData();
				if(NewsChanelAddressDB.getInstance().getFromDB().isEmpty()){
					if(addAllNetDatal(netLs)){
						reloadHList();
					}
				}else{
					if (addData2ListBack(netLs)) {
						reloadHList();
					}	
				}
				int n = scrollbar.getSelectedItemPosition();
				// 查找并且设置为激活
				if (trySetActivate()) {
					reloadListData();
				} else {
					if (n >= 0 && n < hAdapter.getCount()) {
						scrollbar.setSelection(n);
						hAdapter.setHSelectItemSel(hAdapter.getItem(n));
					}
				}
			}
		}
	}

	//取得的数据更新进去
	private boolean addAllNetDatal(List<HSelectItem> netLs){
		List<HSelectItem> oldLs =  hAdapter.getListRef();
		List<HSelectItem> ls = NewsChanelAddressDB
				.getInstance().getFromDB();
		for(HSelectItem old : oldLs){
			for (HSelectItem netIt : netLs) {
				//如果不相等，就要全部加进去
				if(!old.getName().equals(netIt.getName())){
					ls.add(netIt);
				}
			}
		}
		if(ls.isEmpty()){
			return false;
		}
		NewsChanelAddressDB.getInstance().saveToDB(ls);
		return true;
	}
	private boolean addData2ListBack(List<HSelectItem> netLs) {
		//如果是全部添加的
		if (newsList == null) {
			return false;
		}
		boolean bFound = false;
		for (HomeItem needIt : newsList) {
			for (HSelectItem netIt : netLs) {
				// 在网络里找到了
				if (needIt.getLabel().equals(netIt.getName())) {
					// 本地也没有，将他加进去
					if (!isInLocal(needIt.getLabel())) {
						// 取得数据添加常用进去
						List<HSelectItem> ls = NewsChanelAddressDB
								.getInstance().getFromDB();
						ls.add(netIt);
						NewsChanelAddressDB.getInstance().saveToDB(ls);
						bFound = true;
					}
				}
			}
		}
		return bFound;
	}

	private boolean trySetActivate() {
		if (needSel == null || ParamsCheckUtils.isNull(needSel.getTag())) {
			return false;
		}
		for (int i = 0; i < hAdapter.getCount(); i++) {
			HSelectItem it = hAdapter.getItem(i);
			if (it.getName().equals(needSel.getTag())) {
				currIt = it;
				// 未选中
				if (!currIt.isbSel()) {
					scrollbar.setSelection(i);
					hAdapter.setHSelectItemSel(currIt);
				}
				return true;
			}
		}
		return false;
	}

	// 看看首页的在不在本地列表里显示
	private boolean isInLocal(final String Name) {
		List<HSelectItem> localLs = NewsChanelAddressDB.getInstance()
				.getFromDB();
		for (HSelectItem it1 : localLs) {
			if (it1.getName().equals(Name)) {
				return true;
			}
		}
		return false;
	}
}
