package cn.com.phinfo.oaact;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import cn.com.phinfo.adapter.LikeAdapter;
import cn.com.phinfo.adapter.MyQThumbnailAdapter;
import cn.com.phinfo.adapter.ReportShareCommentAdapter;
import cn.com.phinfo.protocol.LURLInterface;
import cn.com.phinfo.protocol.LikeListRun;
import cn.com.phinfo.protocol.LikeListRun.LikeResultBean;
import cn.com.phinfo.protocol.CommentRun;
import cn.com.phinfo.protocol.CommentRun.ReportCommentResultBean;
import cn.com.phinfo.protocol.ReportDelRun;
import cn.com.phinfo.protocol.DoLikeRun;
import cn.com.phinfo.protocol.SearchReportRun.SearchItem;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.imgutils.BitmapDataListInstanceUtils;
import com.heqifuhou.imgutils.FileItem;
import com.heqifuhou.imgutils.PhotoAct;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.heqifuhou.pulltorefresh.PullToRefreshListView;
import com.heqifuhou.utils.ParamsCheckUtils;
import com.heqifuhou.view.PopupWindows;
import com.heqifuhou.view.PopupWindows.OnPopupWindowsItemListener;

//工作详情
public class ReportDetailAct extends HttpMyActBase implements
		OnItemClickListener, OnCheckedChangeListener, OnPageChangeListener {
	private static int ID_GETLIST = 0x10, ID_COMMENT = 0x11, ID_LIKE = 0x12,ID_LIKE_LIST = 0x13,
			ID_DEL_REPORT = 0x14;
	private int page1 = 1, page2 = 1, page3 = 1;
	private RadioGroup tab, horizontalLine;
	private PullToRefreshListView list1, list2, list3;
	private RadioButton t1, t2, t3;
	private RadioButton l1, l2, l3;
	private ViewPager viewPager;

	private TextView Name, date, sate, address, content;
	private ImageView photo;
	private GridView thumbnail;
	private SearchItem it;
	private MyQThumbnailAdapter myQThumbnailAdapter;
	private LikeAdapter reportLikeAdapter;
	private ReportShareCommentAdapter reportCommentAdapter;
	private PopupWindows popupWin;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTitleView(R.layout.nav_f5f5f5_btn);
		this.addTextNav(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showPopWin();
			}
		}, "报告正文", R.drawable.imgmore);

		this.addViewFillInRoot(R.layout.report_detail);
		this.addBottomView(R.layout.report_detail_tools);
		String s = this.getIntent().getExtras().getString("SearchItem");
		it = JSON.parseObject(s, SearchItem.class);
		photo = (ImageView) this.findViewById(R.id.photo);
		thumbnail = (GridView) this.findViewById(R.id.thumbnail);
		Name = (TextView) this.findViewById(R.id.Name);
		date = (TextView) this.findViewById(R.id.date);
		sate = (TextView) this.findViewById(R.id.sate);
		address = (TextView) this.findViewById(R.id.address);
		content = (TextView) this.findViewById(R.id.content);

		tab = (RadioGroup) findViewById(R.id.radiogroup);
		t1 = (RadioButton) this.findViewById(R.id.t1);
		t2 = (RadioButton) this.findViewById(R.id.t2);
		t3 = (RadioButton) this.findViewById(R.id.t3);

		l1 = (RadioButton) this.findViewById(R.id.l1);
		l2 = (RadioButton) this.findViewById(R.id.l2);
		l3 = (RadioButton) this.findViewById(R.id.l3);

		horizontalLine = (RadioGroup) findViewById(R.id.horizontal_line);
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		list1 = (PullToRefreshListView) this.findViewById(R.id.list1);
		list2 = (PullToRefreshListView) this.findViewById(R.id.list2);
		list3 = (PullToRefreshListView) this.findViewById(R.id.list3);
		list1.setOnItemClickListener(this);
		list3.setOnItemClickListener(this);
		horizontalLine.setEnabled(false);
		tab.setOnCheckedChangeListener(this);
		ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(new View[] {
				list1, list2, list3 });
		viewPager.setAdapter(pagerAdapter);
		viewPager.setOnPageChangeListener(this);

		this.findViewById(R.id.commBtn).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(ReportDetailAct.this,
								CreateCommentAct.class);
						intent.putExtra("OBJECTID",it.getWorklogId());
						intent.putExtra("PARENTID","");
						intent.putExtra("OBJTYPECODE", "5500");
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}
				});
		this.findViewById(R.id.goodBtn).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						quickHttpRequest(ID_LIKE, new DoLikeRun(it.getWorklogId(),"5500"));
					}
				});
		this.initData();
		this.onRefresh();
		init2();
		init3();
	}

	private void showPopWin() {
		if (popupWin != null && popupWin.isShowing()) {
			return;
		}
		popupWin = new PopupWindows(this, this.findViewById(R.id.root),
				new String[] { "编辑内容", "删除" });
		popupWin.show();
		popupWin.setOnPopupWindowsItemListener(new OnPopupWindowsItemListener() {
			@Override
			public void onPopupWindowsItem(int pos) {
				if (pos == 0) {
					Intent intent = new Intent(ReportDetailAct.this,
							CreateReportAct.class);
					intent.putExtra("WORKLOGID", it.getWorklogId());
					intent.putExtra("TYPE", it.getWorklogType());
					intent.putExtra("CONTENT", it.getContentBody());
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);

					return;
				} else if (pos == 1) {
					quickHttpRequest(ID_DEL_REPORT,
							new ReportDelRun(it.getWorklogId()));
					return;
				}
			}
		});
	}

	private void init3() {
		reportLikeAdapter = new LikeAdapter();
		list3.setAdapter(reportLikeAdapter);
		list3.setOnItemClickListener(this);
		list3.setMode(Mode.BOTH);
		list3.setOnRefreshListener(new OnRefreshListener2() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				page3 = 1;
				request3();
				hideLoading(true);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				request3();
				hideLoading(true);
			}
		});
	}

	private void init2() {
		reportCommentAdapter = new ReportShareCommentAdapter();
		list2.setAdapter(reportCommentAdapter);
		list2.setOnItemClickListener(this);
		list2.setMode(Mode.BOTH);
		list2.setOnRefreshListener(new OnRefreshListener2() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				page2 = 1;
				request2();
				hideLoading(true);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				request2();
				hideLoading(true);
			}
		});
	}

	private void initData() {
		Name.setText(it.getOwningUserName());
		String s = "日";
		if (it.getWorklogType().equals("1")) {
			s = "日";
		} else if (it.getWorklogType().equals("2")) {
			s = "周";
		} else if (it.getWorklogType().equals("3")) {
			s = "月";
		}
		date.setText(it.getCreatedOn().trim() + " " + s + "报");
		content.setText(it.getContentBody());
		if (ParamsCheckUtils.isNull(it.getLocation())) {
			address.setVisibility(View.GONE);
		} else {
			address.setVisibility(View.VISIBLE);
			address.setText(it.getLocation());
		}
		this.getAsyncAvatar(photo,
				LURLInterface.GET_AVATAR(it.getOwningUser()),
				it.getOwningUserName());
		myQThumbnailAdapter = new MyQThumbnailAdapter();
		thumbnail.setAdapter(myQThumbnailAdapter);
		myQThumbnailAdapter.replaceListRef(it.getThumbnailPicUrls());
		thumbnail.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// 将大图放进去可以显示
//				SearchItem it = (SearchItem) arg0.getTag();
				List<FileItem> lstRef = it.getFileItemList();
				BitmapDataListInstanceUtils.getRefInstance().clear();
				BitmapDataListInstanceUtils.getRefInstance().addAll(lstRef);
				Intent i = new Intent(ReportDetailAct.this, PhotoAct.class);
				i.putExtra("isNoDel", true);
				i.putExtra("ID", arg2);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}
		});
	}

	protected void onRefresh() {
		request2();
		request3();
	}

	private void request3() {
		this.quickHttpRequest(ID_LIKE_LIST, new LikeListRun(it.getWorklogId(),
				String.valueOf(page3)));
	}

	private void request2() {
		this.quickHttpRequest(ID_COMMENT,
				new CommentRun(it.getWorklogId(), String.valueOf(page2)));
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

	}

	public void onHttpForResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		super.onHttpForResult(id, obj, requestObj);
		// 下拉请求完成
		list1.onRefreshComplete();
		list2.onRefreshComplete();
		list3.onRefreshComplete();
	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if (ID_GETLIST == id) {
			return;
		}
		//添加喜欢
		if(ID_LIKE == id){
			if (obj.isOK()) {
				t3.setChecked(true);
				l3.setChecked(true);
				page3=1;
				request3();
			} else {
				showToast(obj.getMsg());
			}
			return;
		}
		//删除
		if (ID_DEL_REPORT == id) {
			if (obj.isOK()) {
				this.sendBroadcast(new Intent(IBroadcastAction.ACTION_REPORT));
				this.finish();
			} else {
				showToast(obj.getMsg());
			}
			return;
		}
		//取评论
		if (ID_COMMENT == id) {
			if (obj.isOK()) {
				ReportCommentResultBean o = (ReportCommentResultBean) obj;
				if (page2 == 1) {
					reportCommentAdapter.clear();
				}
				reportCommentAdapter.addToListBack(o.getListData());
				page2++;
				if (o.getListData().size() < 15) {
					list2.setMode(Mode.PULL_FROM_START);
				} else {
					list2.setMode(Mode.BOTH);
				}
				if (reportCommentAdapter.getCount() <= 0) {
					list2.setEmptyView(this.getEmptyView());
				} else {
					this.removeEmptyView();
				}
			} else {
				showToast(obj.getMsg());
			}
			return;
		}
		//喜欢列表
		if (ID_LIKE_LIST == id) {
			if (obj.isOK()) {
				LikeResultBean o = (LikeResultBean) obj;
				if (page3 == 1) {
					reportLikeAdapter.clear();
				}
				reportLikeAdapter.addToListBack(o.getListData());
				page3++;
				if (o.getListData().size() < 15) {
					list3.setMode(Mode.PULL_FROM_START);
				} else {
					list3.setMode(Mode.BOTH);
				}
				if (reportLikeAdapter.getCount() <= 0) {
					list3.setEmptyView(this.getEmptyView());
				} else {
					this.removeEmptyView();
				}
			} else {
				showToast(obj.getMsg());
			}
			return;
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
		if (position == 0) {
			t1.setChecked(true);
			l1.setChecked(true);
		} else if (position == 1) {
			t2.setChecked(true);
			l2.setChecked(true);
		} else if (position == 2) {
			t3.setChecked(true);
			l3.setChecked(true);
		}
	}

	protected void onBroadcastReceiverListener(Context context, Intent intent) {
		if (IBroadcastAction.ACTION_REPORT.equals(intent.getAction())) {
			this.finish();
			return;
		}
		if(IBroadcastAction.ACTION_REPORT_COMM.equals(intent.getAction())){
			t2.setChecked(true);
			l2.setChecked(true);
			page2=1;
			this.request2();
			return;
		}
		super.onBroadcastReceiverListener(context, intent);
	};

	private static class ViewPagerAdapter extends PagerAdapter {
		private View[] views;

		public ViewPagerAdapter(View[] views) {
			this.views = views;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View view = views[position];
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null) {
				parent.removeAllViews();
			}
			container.addView(view);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(views[position]);
		}

		@Override
		public int getCount() {
			return views.length;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		switch (arg1) {
		case R.id.t1:
			l1.setChecked(true);
			viewPager.setCurrentItem(0);
			break;
		case R.id.t2:
			l2.setChecked(true);
			viewPager.setCurrentItem(1);
			break;
		case R.id.t3:
			l3.setChecked(true);
			viewPager.setCurrentItem(2);
			break;
		}
	}
}
