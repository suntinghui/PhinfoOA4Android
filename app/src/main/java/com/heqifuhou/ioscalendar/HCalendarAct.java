package com.heqifuhou.ioscalendar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.ViewFlipper;
import cn.com.phinfo.adapter.HBCalendarAdapter;
import cn.com.phinfo.oaact.CalendarDetailAct;
import cn.com.phinfo.oaact.CreateCalendarAct;
import cn.com.phinfo.oaact.R;
import cn.com.phinfo.protocol.ActivityEventGetlistRun;
import cn.com.phinfo.protocol.ActivityEventGetlistRun.ActivityEventGetlistItem;
import cn.com.phinfo.protocol.ActivityEventGetlistRun.ActivityEventGetlistResultBean;
import cn.com.phinfo.protocol.ActivityEventRun;
import cn.com.phinfo.protocol.ActivityEventRun.ActivityEventResultBean;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.ioscalendar.CalendarAdapter.DayNumber;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.heqifuhou.pulltorefresh.PullToRefreshListView;

public class HCalendarAct extends HttpMyActBase implements OnGestureListener {
	private static final int ID_GETDATA = 0x10,ID_GETCUR_DATA = 0x12;
	private int pageNumber = 1;
	private DayNumber itDayNumber;
	private ViewFlipper flipper1 = null;
	private GridView gridView = null;
	private GestureDetector gestureDetector = null;
	private HDateAdapter dateAdapter;
	private TextView tvDate,tvWeek;
	private TextView navBack;
	private PullToRefreshListView refreshListView;
	private HBCalendarAdapter hbAdapter = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTitleView(R.layout.nav_calendar_btn);
		navBack = (TextView) this.findViewById(R.id.nav_back);
		navBack.setOnClickListener(onBackListener);
		this.findViewById(R.id.nav_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startCreateCalendarAct();
			}
		});
		itDayNumber = JSON.parseObject(this.getIntent().getExtras().getString("DayNumber"),DayNumber.class);
		this.addViewFillInRoot(R.layout.act_hcalendar);
		refreshListView = (PullToRefreshListView) this.findViewById(R.id.refreshListView);
		hbAdapter = new HBCalendarAdapter();
		refreshListView.setAdapter(hbAdapter);
		refreshListView.setMode(Mode.PULL_FROM_START);
		refreshListView.setOnRefreshListener(new OnRefreshListener2() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				pageNumber=1;
				getDate();
				
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				getDate();
			}
		});
		refreshListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				ActivityEventGetlistItem it = hbAdapter.getItem(arg2-1);
				Intent intent = new Intent(HCalendarAct.this,CalendarDetailAct.class);
				intent.putExtra("ID", it.getId());
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				HCalendarAct.this.startActivity(intent);
			}
		});
		tvDate = (TextView) findViewById(R.id.tv_date);
		tvWeek = (TextView) findViewById(R.id.tvWeek);
		gestureDetector = new GestureDetector(this);
		flipper1 = (ViewFlipper) findViewById(R.id.flipper1);
		dateAdapter = new HDateAdapter(itDayNumber.getYear(), itDayNumber.getMonth()-1, itDayNumber.getDay());
		addGridView();
		gridView.setAdapter(dateAdapter);
		flipper1.addView(gridView, 0);
		showState();
		this.onRefresh();
	}
	private void startCreateCalendarAct(){
		Intent intent = new Intent(this,CreateCalendarAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}
	private void showState(){
		tvWeek.setText(String.valueOf(dateAdapter.getWeekOfYear())+"周");
		tvDate.setText(dateAdapter.getPosItem().getYYYYMMDD2China());
		navBack.setText(dateAdapter.getPosItem().getMonth2China()+ "月");
		hbAdapter.setCurrDate(dateAdapter.getPosItem());
	}
	
	protected void onRefresh(){
		HDataItem beginIt = dateAdapter.getItem(0);
		HDataItem endIt = dateAdapter.getItem(dateAdapter.getCount()-1);
		String startTime = String.format("%04d-%02d-%02d", beginIt.getYear(),beginIt.getMonth()+1,beginIt.getDay());
		String endTime = String.format("%04d-%02d-%02d", endIt.getYear(),endIt.getMonth()+1,endIt.getDay());
		this.quickHttpRequest(ID_GETDATA, new ActivityEventRun(startTime, endTime));
	}

	private void addGridView() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		gridView = new GridView(this);
		gridView.setNumColumns(7);
		gridView.setGravity(Gravity.CENTER_VERTICAL);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		gridView.setVerticalSpacing(1);
		gridView.setHorizontalSpacing(1);
		gridView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				HDataItem it = dateAdapter.getItem(position);
				dateAdapter.setSeclection(position);
				dateAdapter.notifyDataSetChanged();
				tvDate.setText(dateAdapter.getPosItem().getYYYYMMDD2China());
				pageNumber = 1;
				getDate();
			}
		});
		gridView.setLayoutParams(params);
	}
	
	private void getDate(){
		 HDataItem it= dateAdapter.getPosItem();
		 if(it!=null&&it.getSel()){
			 String date = it.getYMD();
			 quickHttpRequest(ID_GETCUR_DATA, new ActivityEventGetlistRun(pageNumber,date));
		 }else{
			 hbAdapter.clear();
		 }
	}

	@Override
	public boolean onDown(MotionEvent e) {

		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}


	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		int gvFlag = 0;
		if (e1.getX() - e2.getX() > 80) {
			// 向左滑
			addGridView();
			gridView.setAdapter(dateAdapter);
			dateAdapter.addWeek();
			showState();
			gvFlag++;
			flipper1.addView(gridView, gvFlag);
			this.flipper1.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.in_righttoleft));
			this.flipper1.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.out_righttoleft));
			this.flipper1.showNext();
			flipper1.removeViewAt(0);
			onRefresh();
			return true;
		} else if (e1.getX() - e2.getX() < -80) {
			addGridView();
			gridView.setAdapter(dateAdapter);
			dateAdapter.decWeek();
			showState();
			gvFlag++;
			flipper1.addView(gridView, gvFlag);
			this.flipper1.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.in_lefttoright));
			this.flipper1.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.out_lefttoright));
			this.flipper1.showPrevious();
			flipper1.removeViewAt(0);
			onRefresh();
			return true;
		}
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return this.gestureDetector.onTouchEvent(event);
	}
	public void onHttpForResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		refreshListView.onRefreshComplete();
		super.onHttpForResult(id, obj, requestObj);
	}
	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if(id == ID_GETDATA){
			if(obj.isOK()){
				ActivityEventResultBean o = (ActivityEventResultBean)obj;
				dateAdapter.setStatus(o.getListData());
				HDataItem it =  dateAdapter.getPosItem();
				if(it!=null&&it.getSel()){
					pageNumber=1;
					getDate();
				}
			}
		}
		if(id == ID_GETCUR_DATA){
			if(obj.isOK()){
				ActivityEventGetlistResultBean o = (ActivityEventGetlistResultBean)obj;
				if(pageNumber == 1){
					hbAdapter.clear();
				}
				hbAdapter.addToListBack(o.getListData());
				pageNumber++;
				if(o.getListData().size()<15){
					refreshListView.setMode(Mode.PULL_FROM_START);
				}else{
					refreshListView.setMode(Mode.BOTH);
				}
			}
			if(hbAdapter.getCount()<=0){
				refreshListView.setEmptyView(this.getEmptyView());
			}else{
				this.removeEmptyView();
			}
		}
	}
	
	protected void onBroadcastReceiverListener(Context context, Intent intent) {
		if (IBroadcastAction.ACTION_CREATE_CALENDAR.equals(intent.getAction())) {
			String s = intent.getExtras().getString("ActivityEventGetlistItem");
			ActivityEventGetlistItem it = JSON.parseObject(s, ActivityEventGetlistItem.class);
			hbAdapter.addToListBack(it);
			return;
		}
		if(IBroadcastAction.ACTION_DEL_CALENDAR.equals(intent.getAction())){
			String id = intent.getExtras().getString("ID");
			hbAdapter.removeItem(id);
			//有可能一下子就删了好几天的，重新刷一下界面
			onRefresh();
			return;
		}
	}

}
