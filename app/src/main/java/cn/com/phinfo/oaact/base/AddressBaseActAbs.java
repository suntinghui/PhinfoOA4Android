package cn.com.phinfo.oaact.base;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import cn.com.phinfo.adapter.base.FriendsBaseAdapter;
import cn.com.phinfo.oaact.R;
import cn.com.phinfo.oaact.SearchAddressAct;

import com.heqifuhou.actbase.HttpLoginMyActBase;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.heqifuhou.pulltorefresh.PullToRefreshListView;
import com.heqifuhou.sortlistview.ClearEditText;
import com.heqifuhou.sortlistview.GroupMemberBean;
import com.heqifuhou.sortlistview.SideBar;
import com.heqifuhou.sortlistview.SideBar.OnTouchingLetterChangedListener;

public abstract class AddressBaseActAbs extends HttpLoginMyActBase implements OnItemClickListener,SectionIndexer,OnClickListener{
	protected  PullToRefreshListView refreshListView;
	private SideBar sideBar;
	protected FriendsBaseAdapter adapterFriends;
	
	private ClearEditText mClearEditText;
	private LinearLayout titleLayout;
	private TextView title;
	private TextView tvNofriends;
	// 上次第一个可见元素，用于滚动时记录标识。
	private int lastFirstVisibleItem = -1;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addViewFillInRoot(R.layout.act_address_list);
		refreshListView = (PullToRefreshListView) this.findViewById(R.id.friendsrefresh);
		this.findViewById(R.id.queryBtn).setOnClickListener(this);
		initViews();
		
	}
	@Override
	public void onClick(View arg0) {
		startSearchAddressAct();
	}

	private void startSearchAddressAct() {
		Intent intent = new Intent(this, SearchAddressAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}
	protected void onRefresh() {
	
	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj, Object requestObj) {

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	
	}
	
	private void initViews() {
		titleLayout = (LinearLayout) findViewById(R.id.title_layout);
		title = (TextView) this.findViewById(R.id.title_layout_catalog);
		tvNofriends = (TextView) this
				.findViewById(R.id.title_layout_no_friends);
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		// 显示的dialog
		sideBar.setTextView((TextView) findViewById(R.id.dialog));
//		refreshListView.setMode(Mode.PULL_FROM_START);
		refreshListView.setMode(Mode.DISABLED);
		refreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						AddressBaseActAbs.this.onRefresh();
						hideLoading();
					}
				});

		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(sideBarOnTouchingLetterChangedListener);
		refreshListView.setOnItemClickListener(this);
		adapterFriends = onGetFriendsAdapter();
		refreshListView.setAdapter(adapterFriends);
		refreshListView.setOnScrollListener(sortOnScrollListener);

		mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
		// 根据输入框输入值的改变来过滤搜索
		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 这个时候不需要挤压效果 就把他隐藏掉
				titleLayout.setVisibility(View.GONE);
				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	// 点击字母的位置
	private OnTouchingLetterChangedListener sideBarOnTouchingLetterChangedListener = new OnTouchingLetterChangedListener() {
		@Override
		public void onTouchingLetterChanged(String s) {
			// 该字母首次出现的位置
			int position = adapterFriends.getPositionForSection(s.charAt(0));
			if (position != -1) {
				refreshListView.getRefreshableView().setSelection(position);
			}
		}
	};
	public void onHttpForResult(int id, HttpResultBeanBase obj, Object requestObj) {
		super.onHttpForResult(id, obj, requestObj);
		// 下拉请求完成
		refreshListView.onRefreshComplete();
	}
	private OnScrollListener sortOnScrollListener = new OnScrollListener() {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			if (adapterFriends.getCount() <= 0) {
				return;
			}
			Object iit = adapterFriends.getItem(firstVisibleItem);
			if(iit!=null){
				GroupMemberBean _it = (GroupMemberBean)iit ;
				if(_it.getIsDir()){
					titleLayout.setVisibility(View.GONE);
				}else{
					titleLayout.setVisibility(View.VISIBLE);
				}
			}

			int section = getSectionForPosition(firstVisibleItem);
			int nextSection = getSectionForPosition(firstVisibleItem + 1);
			int nextSecPosition = getPositionForSection(+nextSection);
			if (firstVisibleItem != lastFirstVisibleItem) {
				MarginLayoutParams params = (MarginLayoutParams) titleLayout
						.getLayoutParams();
				params.topMargin = 0;
				titleLayout.setLayoutParams(params);
				int pos = getPositionForSection(section);
				if (pos >= 0) {
					GroupMemberBean it = (GroupMemberBean) adapterFriends.getItem(pos);
					if (it != null) {
						title.setText(it.getSortLetters());
					}
		
				}
			}
			if (nextSecPosition == firstVisibleItem + 1) {
				View childView = view.getChildAt(0);
				if (childView != null) {
					int titleHeight = titleLayout.getHeight();
					int bottom = childView.getBottom();
					MarginLayoutParams params = (MarginLayoutParams) titleLayout
							.getLayoutParams();
					if (bottom < titleHeight) {
						float pushedDistance = bottom - titleHeight;
						params.topMargin = (int) pushedDistance;
						titleLayout.setLayoutParams(params);
					} else {
						if (params.topMargin != 0) {
							params.topMargin = 0;
							titleLayout.setLayoutParams(params);
						}
					}
				}
			}
			lastFirstVisibleItem = firstVisibleItem;
		}
	};

	// 根据输入框中的值来过滤数据并更新ListView
	private void filterData(String filterStr) {
		if (TextUtils.isEmpty(filterStr)) {
			tvNofriends.setVisibility(View.GONE);
			adapterFriends.reToOldListRef();
			return;
		}
		adapterFriends.reToOldListRef();
		adapterFriends.filterStr(filterStr);
		if (adapterFriends.getCount() <= 0) {
			tvNofriends.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	// 根据ListView的当前位置获取分类的首字母的Char ascii值
	public int getSectionForPosition(int position) {
		if (position >= 0 && position < adapterFriends.getCount()) {
			GroupMemberBean it = (GroupMemberBean) adapterFriends.getItem(position);
			return it.getSortLetters().charAt(0);
		}
		return -1;
	}

	// 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	public int getPositionForSection(int section) {
		for (int i = 0; i < adapterFriends.getCount(); i++) {
			GroupMemberBean it = (GroupMemberBean) adapterFriends.getItem(i);
			String sortStr = it.getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}
	

	protected abstract FriendsBaseAdapter onGetFriendsAdapter();
}
