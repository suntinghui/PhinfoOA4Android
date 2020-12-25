package cn.com.phinfo.oaact;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import cn.com.phinfo.adapter.LikeAdapter;
import cn.com.phinfo.adapter.MyQThumbnailAdapter;
import cn.com.phinfo.adapter.ReportShareCommentAdapter;
import cn.com.phinfo.adapter.ShareVoteAdapter;
import cn.com.phinfo.protocol.CommentRun;
import cn.com.phinfo.protocol.CommentRun.ReportCommentResultBean;
import cn.com.phinfo.protocol.DoLikeRun;
import cn.com.phinfo.protocol.LURLInterface;
import cn.com.phinfo.protocol.LikeListRun;
import cn.com.phinfo.protocol.PollOptionsRun;
import cn.com.phinfo.protocol.LikeListRun.LikeResultBean;
import cn.com.phinfo.protocol.SearchChatterRun.PollOption;
import cn.com.phinfo.protocol.SearchChatterRun.SearchChatterItem;
import cn.com.phinfo.protocol.SearchReportRun.SearchItem;
import cn.com.phinfo.protocol.ShareDelRun;

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

//分享详情
public class ShareDetailAct extends HttpMyActBase implements
		OnItemClickListener, OnCheckedChangeListener, ViewPager.OnPageChangeListener {
	private static int ID_GETLIST = 0x10, ID_COMMENT = 0x11, ID_LIKE = 0x12,ID_LIKE_LIST = 0x13,
			ID_DEL_SHARE = 0x14,ID_SEND_NEW=0x15;
	private int page1 = 1, page2 = 1, page3 = 1;
	private RadioGroup tab, horizontalLine;
	private PullToRefreshListView list1, list2, list3;
	private RadioButton t1, t2, t3;
	private RadioButton l1, l2, l3;
	private ViewPager viewPager;
	
	private ListView voteLst;
	private TextView Name, date, sate, address, content,pollTitle,peopCount,voteBtn;
	private ImageView photo;
	private GridView thumbnail;
	private SearchChatterItem it;
	private MyQThumbnailAdapter myQThumbnailAdapter;
	private LikeAdapter reportLikeAdapter;
	private ReportShareCommentAdapter reportCommentAdapter;
	private PopupWindows popupWin;
	private View poll;
	private ShareVoteAdapter myShareVoteAdapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTitleView(R.layout.nav_f5f5f5_btn);
		this.addTextNav(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showPopWin();
			}
		}, "分享正文", R.drawable.imgmore);

		this.addViewFillInRoot(R.layout.share_detail);
		this.addBottomView(R.layout.report_detail_tools);
		String s = this.getIntent().getExtras().getString("SearchChatterItem");
		it = JSON.parseObject(s, SearchChatterItem.class);
		photo = (ImageView) this.findViewById(R.id.photo);
		thumbnail = (GridView) this.findViewById(R.id.thumbnail);
		Name = (TextView) this.findViewById(R.id.Name);
		date = (TextView) this.findViewById(R.id.date);
		sate = (TextView) this.findViewById(R.id.sate);
		address = (TextView) this.findViewById(R.id.address);
		content = (TextView) this.findViewById(R.id.content);
	    voteLst = (ListView) this.findViewById(R.id.voteLst);
		pollTitle =  (TextView) this.findViewById(R.id.pollTitle);
		peopCount =  (TextView) this.findViewById(R.id.peopCount);
		poll = this.findViewById(R.id.poll);
		voteBtn = (TextView) this.findViewById(R.id.vote_btn);

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
						Intent intent = new Intent(ShareDetailAct.this,
								CreateCommentAct.class);
						intent.putExtra("OBJECTID",it.getChatterId());
						intent.putExtra("PARENTID","");
						intent.putExtra("OBJTYPECODE", "6000");
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}
				});
		this.findViewById(R.id.goodBtn).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						quickHttpRequest(ID_LIKE, new DoLikeRun(it.getChatterId(),"6000"));
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
//					Intent intent = new Intent(ShareDetailAct.this,
//							CreateReportAct.class);
//					intent.putExtra("WORKLOGID", it.getChatterId());
//					intent.putExtra("CONTENT", it.getContentBody());
//					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//					startActivity(intent);

					return;
				} else if (pos == 1) {
					quickHttpRequest(ID_DEL_SHARE,
							new ShareDelRun(it.getChatterId()));
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
		date.setText(it.getCreatedOn().trim());
		if (ParamsCheckUtils.isNull(it.getLocation())) {
			address.setVisibility(View.GONE);
		} else {
			address.setVisibility(View.VISIBLE);
			address.setText(it.getLocation());
		}
		this.getAsyncAvatar(photo,
				LURLInterface.GET_AVATAR(it.getOwningUser()),
				it.getOwningUserName());
		//投票的
		if("30400".equals(it.getChatterTypeCode())){
			thumbnail.setVisibility(View.GONE);
			poll.setVisibility(View.VISIBLE);
			content.setText(it.getOwningUserName()+"发起了一个投票【"+it.getDescription()+"】");
			pollTitle.setText(it.getPoll().getTitle());
			peopCount.setText("参与人数:"+it.getPoll().getTotalPeoples());
			myShareVoteAdapter = new ShareVoteAdapter(!it.isHasOptions());
			voteLst.setAdapter(myShareVoteAdapter);
			myShareVoteAdapter.replaceListRef(it.getPoll().getOptions());
			//已经投了
			if(it.isHasOptions()){
				voteBtn.setText("已投票");
				voteBtn.setEnabled(false);
				voteLst.setOnItemClickListener(null);
			}else{
				voteBtn.setText("投票");
				voteBtn.setEnabled(true);
				voteLst.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						PollOption itOption = myShareVoteAdapter.getItem(arg2);
						//0是单选
						myShareVoteAdapter.setSel(itOption,!"0".equals(it.getPoll().getPollType()));
					}
				});
				voteBtn.setTag(it);
				voteBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						SearchChatterItem it = (SearchChatterItem)arg0.getTag();
					    quickHttpRequest(ID_SEND_NEW,new PollOptionsRun(it.getPoll().getPollId(),it.getPoll().getAllSelOptions()));
					}
				});
			}
		}
		else{
			thumbnail.setVisibility(View.VISIBLE);
			poll.setVisibility(View.GONE);
			content.setText(it.getDescription());
			thumbnail.setTag(it);
			myQThumbnailAdapter = new MyQThumbnailAdapter();
			thumbnail.setAdapter(myQThumbnailAdapter);
			myQThumbnailAdapter.replaceListRef(it.getThumbnailPicUrls());
			thumbnail.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// 将大图放进去可以显示
					SearchChatterItem it = (SearchChatterItem) arg0.getTag();
					List<FileItem> lstRef = it.getFileItemList();
					BitmapDataListInstanceUtils.getRefInstance().clear();
					BitmapDataListInstanceUtils.getRefInstance().addAll(lstRef);
					Intent i = new Intent(ShareDetailAct.this, PhotoAct.class);
					i.putExtra("isNoDel", true);
					i.putExtra("ID", arg2);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
				}
			});	
		}
		
//		myQThumbnailAdapter = new MyQThumbnailAdapter();
//		thumbnail.setAdapter(myQThumbnailAdapter);
//		myQThumbnailAdapter.replaceListRef(it.getThumbnailPicUrls());
//		thumbnail.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//					long arg3) {
//				// 将大图放进去可以显示
//				SearchItem it = (SearchItem) arg0.getTag();
//				List<FileItem> lstRef = it.getFileItemList();
//				BitmapDataListInstanceUtils.getRefInstance().clear();
//				BitmapDataListInstanceUtils.getRefInstance().addAll(lstRef);
//				Intent i = new Intent(ShareDetailAct.this, PhotoAct.class);
//				i.putExtra("isNoDel", true);
//				i.putExtra("ID", arg2);
//				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				startActivity(i);
//			}
//		});
	}

	protected void onRefresh() {
		request2();
		request3();
	}

	private void request3() {
		this.quickHttpRequest(ID_LIKE_LIST, new LikeListRun(it.getChatterId(),
				String.valueOf(page3)));
	}

	private void request2() {
		this.quickHttpRequest(ID_COMMENT,
				new CommentRun(it.getChatterId(), String.valueOf(page2)));
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
		//添加评论
		if(ID_LIKE == id){
			if (obj.isOK()) {
				t3.setChecked(true);
				l3.setChecked(true);
				page3=1;
				request3();
			} else {
				showToast(obj.getMsg());
			}
		}
		//删除
		if (ID_DEL_SHARE == id) {
			if (obj.isOK()) {
				this.sendBroadcast(new Intent(IBroadcastAction.ACTION_SHARE));
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
		if(ID_SEND_NEW == id){
			if(obj.isOK()){
				this.sendBroadcast(new Intent(IBroadcastAction.ACTION_SHARE));
				finish();
				return;
			}else{
				showToast(obj.getMsg());
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
		if (IBroadcastAction.ACTION_SHARE.equals(intent.getAction())) {
			onRefresh();
			return;
		}
		if(IBroadcastAction.ACTION_SHARE_COMM.equals(intent.getAction())){
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
