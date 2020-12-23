package cn.com.phinfo.oaact;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import cn.com.phinfo.adapter.DefCommentAdapter;
import cn.com.phinfo.adapter.MeetingItemAdapter;
import cn.com.phinfo.adapter.MeetingPeoplesAdapter;
import cn.com.phinfo.protocol.EntityInfoRecRun;
import cn.com.phinfo.protocol.EntityInfoRecRun.EntityInfoRecItem;
import cn.com.phinfo.protocol.EntityInfoRecRun.EntityInfoRecResultBean;
import cn.com.phinfo.protocol.LURLInterface;
import cn.com.phinfo.protocol.MeetingCommentAddRun;
import cn.com.phinfo.protocol.MeetingCommentAddRun.MeetingCommentAddResultBean;
import cn.com.phinfo.protocol.MeetingDetailCommentsRun;
import cn.com.phinfo.protocol.MeetingDetailCommentsRun.MeetingDetailCommentsResultBean;
import cn.com.phinfo.protocol.MeetingItemRun;
import cn.com.phinfo.protocol.MeetingItemRun.MeetingItem;
import cn.com.phinfo.protocol.MeetingItemRun.MeetingItemResultBean;
import cn.com.phinfo.protocol.MeetingRecListRun.MeetingRecListItem;
import cn.com.phinfo.protocol.PeoplesListRun;
import cn.com.phinfo.protocol.PeoplesListRun.PeopLesItem;
import cn.com.phinfo.protocol.PeoplesListRun.PeoplesListResultBean;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.HttpLoginMyActBase;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.adapterbase.ViewSyncPagerAdapter;
import com.heqifuhou.diykeyboard.KeyMapDailog;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.heqifuhou.pulltorefresh.PullToRefreshListView;
import com.heqifuhou.pulltorefresh.PullToRefreshScrollView;
import com.heqifuhou.toprightmenu.MenuItem;
import com.heqifuhou.toprightmenu.TopRightMenu;
import com.heqifuhou.utils.ParamsCheckUtils;
import com.heqifuhou.view.CircleImageView;
import com.heqifuhou.view.NoScrollListView;

public class MeetingDetailAct extends HttpLoginMyActBase implements
		OnCheckedChangeListener, OnPageChangeListener,OnItemClickListener {
	private static final int PERPAGE_SIZE = 15;
	private static final int ID_GETLIST = 0x10,ID_TAB1=0x11,ID_GETCOMMENT=0x12,ID_NEWS_ADD=0x13
			,ID_T3T1 = 0x14,ID_T3T2 = 0x15,ID_T3T3 = 0x16,ID_T3T4=0x17,ID_MEETING_ITEM = 0x18;
	private String id;
	private RadioGroup tab, horizontalLine;
	private RadioButton tab1, tab2, tab3;
	private ViewPager viewPager;
	private View[] idformeetingdetailr1 = new View[3];
	//tab1
	private CircleImageView icon_avatar;
	private TextView name,startDate,endDate,subject,des;
	private NoScrollListView refreshListView;
	private PullToRefreshScrollView scrollview;
	private DefCommentAdapter commentAdapter = null;
	private int pageNumber = 1;
	private KeyMapDailog dialog;
	private MeetingRecListItem meetingRecItem;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.id = this.getIntent().getExtras().getString("ID");
		this.meetingRecItem = (MeetingRecListItem) this.getIntent().getExtras().getSerializable("MeetingRecListItem");
		this.addTitleView(R.layout.nav_f5f5f5_meeting_detail);
		this.addTextNav(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showPopMenu();
			}
		}, "会议", "更多");
		this.addViewFillInRoot(R.layout.act_meeting_detail);
		this.addBottomView(R.layout.meeting_detail_tools_bar);
		horizontalLine = (RadioGroup) findViewById(R.id.horizontal_line);
		tab = (RadioGroup) findViewById(R.id.radiogroup);
		tab1 = (RadioButton) findViewById(R.id.tab1);
		tab2 = (RadioButton) findViewById(R.id.tab2);
		tab3 = (RadioButton) findViewById(R.id.tab3);
		idformeetingdetailr1[0] = this.findViewById(R.id.id_for_meeting_detail_r1);
		idformeetingdetailr1[1] = this.findViewById(R.id.id_for_meeting_detail_r2);
		idformeetingdetailr1[2] = this.findViewById(R.id.id_for_meeting_detail_r3);

		tab.setOnCheckedChangeListener(this);
		horizontalLine.setEnabled(false);
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		changeLine(0);
		ViewSyncPagerAdapter pagerAdapter = new ViewSyncPagerAdapter(idformeetingdetailr1);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setOnPageChangeListener(this);
		this.initTab1();
		this.initTab2();
		this.initTab3();
		onRefresh();
	}
	
	private TopRightMenu mTopRightMenu=null;
	private void showPopMenu(){
		mTopRightMenu = new TopRightMenu(this);
		//添加菜单项
		List<MenuItem> menuItems = new ArrayList<>();
		menuItems.add(new MenuItem(R.drawable.ic_invite, "邀请人员"));
		menuItems.add(new MenuItem(R.drawable.ic_code, "二维码"));
		menuItems.add(new MenuItem(R.drawable.ic_ask, "添加议题"));
		menuItems.add(new MenuItem(R.drawable.ic_check, "签到"));
		mTopRightMenu
		        .setHeight(600)     //默认高度480
		        .setWidth(420)      //默认宽度wrap_content
		        .showIcon(true)     //显示菜单图标，默认为true
		        .dimBackground(true)        //背景变暗，默认为true
		        .needAnimationStyle(true)   //显示动画，默认为true
		        .setAnimationStyle(R.style.TRM_ANIM_STYLE)
		        .addMenuList(menuItems)
		        .setOnMenuItemClickListener(new TopRightMenu.OnMenuItemClickListener() {
		              @Override
		              public void onMenuItemClick(int position) {
		            	  //邀请人员
		                 if(position==0){
		                	 showInvite();
		                 }
		                 //二维码
		                 else if(position==1){
		                	 Intent i= new Intent(MeetingDetailAct.this,WebViewRefreshAct.class);
		                	 i.putExtra("TITLE","二维码");
		                	 i.putExtra("URL", LURLInterface.GET_QCODE(id));
		                	 startActivity(i);
		                 }
		                 //添加议题
		                 else if(position==2){
		                	 createInvite();
		                 }
		                 //签到
		                 else if(position==3){
		                	 startCheck();
		                 }
		              }
		        })
		        .showAsDropDown(this.findViewById(R.id.nav_btn), -225, 0);	//带偏移量
//		      		.showAsDropDown(moreBtn)
	}
	//签到
	private void startCheck(){
		Intent intent = new Intent(this,CaptureAct.class);
		intent.putExtra("ID",this.id);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	//邀请人员
	private void showInvite(){
		 Intent i= new Intent(MeetingDetailAct.this,MeetingInvitePersonAct.class);
		 i.putExtra("ID", this.id);
    	 startActivity(i);
	}
	//创建会议议题
	private void createInvite(){
		 Intent i= new Intent(MeetingDetailAct.this,MeetingItemAddAct.class);
		 i.putExtra("ID", this.id);
    	 startActivity(i);
		
	}
	
	private MeetingItemAdapter t2adapter;
	private PullToRefreshListView t2refreshListView;
	private void initTab2(){
		t2refreshListView = (PullToRefreshListView) this.findViewById(R.id.t2refreshListView);
		t2refreshListView.setMode(Mode.DISABLED);
		t2adapter = new MeetingItemAdapter();
		t2refreshListView.setAdapter(t2adapter);
	}
	
	
	private ListView t3refreshListView1,t3refreshListView2,t3refreshListView3,t3refreshListView4;
	private MeetingPeoplesAdapter adapterP1,adapterP2,adapterP3,adapterP4;
	private TextView t3State1,t3State2,t3State3,t3State4;
	private void initTab3(){
		t3refreshListView1= (ListView) this.findViewById(R.id.t3refreshListView1);
		t3refreshListView2= (ListView) this.findViewById(R.id.t3refreshListView2);
		t3refreshListView3= (ListView) this.findViewById(R.id.t3refreshListView3);
		t3refreshListView4= (ListView) this.findViewById(R.id.t3refreshListView4);
		adapterP1 = new MeetingPeoplesAdapter();
		adapterP2 = new MeetingPeoplesAdapter();
		adapterP3 = new MeetingPeoplesAdapter();
		adapterP4 = new MeetingPeoplesAdapter();
		t3refreshListView1.setAdapter(adapterP1);
		t3refreshListView2.setAdapter(adapterP2);
		t3refreshListView3.setAdapter(adapterP3);
		t3refreshListView4.setAdapter(adapterP4);
		final RadioGroup t3tab = (RadioGroup) findViewById(R.id.t3radiogroup);
		final RadioGroup t3horizontalLine = (RadioGroup) findViewById(R.id.t3horizontal_line);
		((CompoundButton) t3horizontalLine.getChildAt(0)).setChecked(true);
		t3horizontalLine.setEnabled(false);
		t3tab.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int checkedId) {
				switch (checkedId) {
				case R.id.t3tab1:
					((CompoundButton) t3horizontalLine.getChildAt(0)).setChecked(true);
					t3refreshListView1.setVisibility(View.VISIBLE);
					t3refreshListView2.setVisibility(View.GONE);
					t3refreshListView3.setVisibility(View.GONE);
					t3refreshListView4.setVisibility(View.GONE);
					break;
				case R.id.t3tab2:
					((CompoundButton) t3horizontalLine.getChildAt(1)).setChecked(true);
					t3refreshListView1.setVisibility(View.GONE);
					t3refreshListView2.setVisibility(View.VISIBLE);
					t3refreshListView3.setVisibility(View.GONE);
					t3refreshListView4.setVisibility(View.GONE);
					break;
				case R.id.t3tab3:
					((CompoundButton) t3horizontalLine.getChildAt(2)).setChecked(true);
					t3refreshListView1.setVisibility(View.GONE);
					t3refreshListView2.setVisibility(View.GONE);
					t3refreshListView3.setVisibility(View.VISIBLE);
					t3refreshListView4.setVisibility(View.GONE);
					break;
				case R.id.t3tab4:
					((CompoundButton) t3horizontalLine.getChildAt(3)).setChecked(true);
					t3refreshListView1.setVisibility(View.GONE);
					t3refreshListView2.setVisibility(View.GONE);
					t3refreshListView3.setVisibility(View.GONE);
					t3refreshListView4.setVisibility(View.VISIBLE);
					break;
				}
			}
		});
		
		t3State1 = (TextView) this.findViewById(R.id.t3State1);
		t3State2 = (TextView) this.findViewById(R.id.t3State2);
		t3State3 = (TextView) this.findViewById(R.id.t3State3);
		t3State4 = (TextView) this.findViewById(R.id.t3State4);
		t3State1.setText(ParamsCheckUtils.isNull(meetingRecItem.getInviteQty())?"0":meetingRecItem.getInviteQty());
		t3State2.setText(ParamsCheckUtils.isNull(meetingRecItem.getAcceptQty())?"0":meetingRecItem.getAcceptQty());
		t3State3.setText(ParamsCheckUtils.isNull(meetingRecItem.getJoinQty())?"0":meetingRecItem.getJoinQty());
		t3State4.setText(ParamsCheckUtils.isNull(meetingRecItem.getSpeakQty())?"0":meetingRecItem.getSpeakQty());
	}
	
	private void initTab1(){
		this.icon_avatar = (CircleImageView) this.findViewById(R.id.icon_avatar);
		name  = (TextView) this.findViewById(R.id.name);
		startDate= (TextView) this.findViewById(R.id.startDate);
		endDate= (TextView) this.findViewById(R.id.endDate);
		subject= (TextView) this.findViewById(R.id.subject);
		des= (TextView) this.findViewById(R.id.des);
		refreshListView = (NoScrollListView) this.findViewById(R.id.refreshListView);
		this.scrollview = (PullToRefreshScrollView) idformeetingdetailr1[0].findViewById(R.id.scrollview);	
		this.scrollview.setMode(Mode.PULL_FROM_END);
		refreshListView.setOnItemClickListener(this);
		commentAdapter = new DefCommentAdapter();
		refreshListView.setAdapter(commentAdapter);
		this.scrollview.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh(PullToRefreshBase refreshView) {
				getComment();
				hideLoading(true);
			}
		});
		
		this.findViewById(R.id.edit_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog = new KeyMapDailog("", new KeyMapDailog.SendBackListener() {
					@Override
					public void sendBack(final String inputText) {
						dialog.hideProgressdialog();
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								quickHttpRequest(ID_NEWS_ADD, new MeetingCommentAddRun(id, "", inputText));
								dialog.dismiss();
							}
						}, 10);
					}
				});
				dialog.show(getSupportFragmentManager(), "dialog");
			}
		});
		
		this.getComment();
	}
	
	private void getComment() {
		this.quickHttpRequest(ID_GETCOMMENT, new MeetingDetailCommentsRun(id,pageNumber,""));
	}

	// 切换TAB线条
	private void changeLine(int index) {
		((RadioButton) horizontalLine.getChildAt(index)).setChecked(true);
	}

	protected void onRefresh() {
		this.quickHttpRequest(ID_TAB1, new EntityInfoRecRun(id));
		this.quickHttpRequest(ID_T3T1, new PeoplesListRun("1",id));
		this.quickHttpRequest(ID_T3T2, new PeoplesListRun("2",id));
		this.quickHttpRequest(ID_T3T3, new PeoplesListRun("3",id));
		this.quickHttpRequest(ID_T3T4, new PeoplesListRun("4",id));
		this.quickHttpRequest(ID_MEETING_ITEM, new MeetingItemRun(id));
	}

	public void onHttpForResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		super.onHttpForResult(id, obj, requestObj);

	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if(id == ID_TAB1){
			if(obj.isOK()){
				EntityInfoRecResultBean o = (EntityInfoRecResultBean)obj;
				EntityInfoRecItem d = o.getData();
				name.setText(d.getOwningUserName());
				this.getAsyncAvatar(icon_avatar, LURLInterface.GET_AVATAR(d.getOwningUser()),d.getOwningUserName());
				startDate.setText("开始时间:"+d.getScheduledStart());
				endDate.setText("结束时间:"+d.getScheduledEnd());
				subject.setText(d.getName());
				des.setText(d.getMeetingContent());
			}else{
				this.showToast(obj.getMsg());
			}
			return;
		}
		if (ID_GETCOMMENT == id) {
			if (obj.isOK()) {
				MeetingDetailCommentsResultBean o = (MeetingDetailCommentsResultBean) obj;
				if (pageNumber == 1) {
					commentAdapter.clear();
				}
				commentAdapter.addToListBack(o.getListData());
				pageNumber++;
				if (o.getListData().size() < PERPAGE_SIZE) {
					this.scrollview.setMode(Mode.DISABLED);
				} else {
					scrollview.setMode(Mode.PULL_FROM_END);
				}
				refreshListView.post(new Runnable() {
					@Override
					public void run() {
						commentAdapter.notifyDataSetChanged();
					}
				});
			} else {
				this.showToast(obj.getMsg());
			}
			if (commentAdapter.getCount() <= 0) {
				refreshListView.setVisibility(View.GONE);
			} else {
				refreshListView.setVisibility(View.VISIBLE);
			}
			return;
		}
		if (ID_NEWS_ADD == id) {
			if (obj.isOK()) {
				MeetingCommentAddResultBean o = (MeetingCommentAddResultBean)obj;
//				commentAdapter.addToListHead(CommentItem.init(o));
				pageNumber = 1;
				this.getComment();
				this.showToast("评论提交成功");
			} else {
				this.showToast(obj.getMsg());
			}
			return;
		}
		
		if(ID_T3T1 == id||ID_T3T2 == id||ID_T3T3 == id||ID_T3T4 == id){
			if(obj.isOK()){
				PeoplesListResultBean o = (PeoplesListResultBean)obj;
				List<PeopLesItem > ls = o.getListData();
				MeetingPeoplesAdapter adapterDef = null;
				ListView ns = null;
				TextView state = null;
				if(ID_T3T1 == id){
					adapterDef  = adapterP1;
					ns =t3refreshListView1;
					state = t3State1;
				}else if(ID_T3T2 == id){
					adapterDef  = adapterP2;
					ns =t3refreshListView2;
					state = t3State2;
				}else if(ID_T3T3 == id){
					adapterDef  = adapterP3;
					ns =t3refreshListView3;
					state = t3State3;
				}else if(ID_T3T4 == id){
					adapterDef  = adapterP4;
					ns =t3refreshListView4;
					state = t3State4;
				}
				adapterDef.replaceListRef(ls);
//				state.setText(String.valueOf(adapterDef.getCount()));
				if (adapterDef.getCount() <= 0) {
					ns.setEmptyView(this.getEmptyView());
				} else {
					this.removeEmptyView();
				}
			}
			return;
		}
		if(ID_MEETING_ITEM==id){
			if(obj.isOK()){
				MeetingItemResultBean o = (MeetingItemResultBean)obj;
				t2adapter.replaceListRef(o.getData());
			}
			if (t2adapter.getCount() <= 0) {
				t2refreshListView.setEmptyView(this.getEmptyView());
			} else {
				this.removeEmptyView();
			}
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int position) {
		hideBottom();
		if (position == 0) {
			showBottom();
			tab1.setChecked(true);
		} else if (position == 1) {
			tab2.setChecked(true);
		} else {
			tab3.setChecked(true);
		}
		changeLine(position);
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int checkedId) {
		hideBottom();
		switch (checkedId) {
		case R.id.tab1:
			changeLine(0);
			showBottom();
			viewPager.setCurrentItem(0);
			break;
		case R.id.tab2:
			changeLine(1);
			viewPager.setCurrentItem(1);
			break;
		case R.id.tab3:
			changeLine(2);
			viewPager.setCurrentItem(2);
			break;
		}
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}
	
	protected void onBroadcastReceiverListener(Context context, Intent intent) {
		if (IBroadcastAction.ACTION_MEETING_INVITE.equals(intent.getAction())) {
			onRefresh();
			return;
		}
		if(IBroadcastAction.ACTION_MEETING_ITEM_ADD.equals(intent.getAction())){
			String s = intent.getExtras().getString("MeetingItem");
			MeetingItem it = JSON.parseObject(s, MeetingItem.class);
			t2adapter.addToListHead(it);
			return;
		}
	}
	

}
