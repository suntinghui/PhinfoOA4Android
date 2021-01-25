package com.heqifuhou.ioscalendar;

import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.ViewFlipper;
import cn.com.phinfo.oaact.CreateCalendarAct;
import cn.com.phinfo.oaact.R;
import cn.com.phinfo.protocol.ActivityEventRun;
import cn.com.phinfo.protocol.ActivityEventRun.ActivityEventResultBean;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.ioscalendar.CalendarAdapter.DayNumber;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.view.MyGridView;

public class CalendarAct extends HttpMyActBase implements OnItemClickListener{
	private static final int ID_GETDATA = 0x10;
	private CalendarAdapter[] adapter = new CalendarAdapter[2];
	private MyGridView[] gridView = new MyGridView[2];
	private LinearLayout rootCalenderLinearLayout;
	private GestureDetector gestureDetector;
	private ViewFlipper flipper;
	private int year_c, month_c, day_c;
	private static int jumpMonth = 0, jumpYear = 0;
	private TextView navBack;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addViewFillInRoot(R.layout.alert_calendar_layout);
		this.addTitleView(R.layout.nav_calendar_btn);
		navBack = (TextView) this.findViewById(R.id.nav_back);
		navBack.setOnClickListener(onBackListener);
		this.findViewById(R.id.nav_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startCreateCalendarAct();
			}
		});

		boolean showBack = this.getIntent().getBooleanExtra("showBack", false);
		if (showBack) {
			navBack.setOnClickListener(onBackListener);
			Drawable drawable =  getResources().getDrawable(R.drawable.back_red);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),	drawable.getMinimumHeight());
			navBack.setCompoundDrawables(drawable,null,null,null);
			navBack.setCompoundDrawablePadding(5);

		} else {
			navBack.setOnClickListener(null);
		}

		initCalendar();
		onRefresh();
	}
	private void startCreateCalendarAct(){
		Intent intent = new Intent(this,CreateCalendarAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}
	protected void onRefresh(){
		Calendar ca = Calendar.getInstance();
		ca.set(Calendar.YEAR, adapter[0].getShowYear());
		ca.set(Calendar.MONTH, adapter[0].getShowMonth()-1);
		ca.set(Calendar.DAY_OF_MONTH, 1);
		ca.add(Calendar.DAY_OF_MONTH, -7);
		
		//向前保证有7天
		String startTime = String.format("%04d-%02d-%02d", ca.get(Calendar.YEAR),ca.get(Calendar.MONTH)+1,ca.get(Calendar.DAY_OF_MONTH));
		
		ca = Calendar.getInstance();
		ca.set(Calendar.YEAR, adapter[1].getShowYear());
		ca.set(Calendar.MONTH, adapter[1].getShowMonth()-1);
		ca.set(Calendar.DAY_OF_MONTH,
				ca.getActualMaximum(Calendar.DAY_OF_MONTH));
		ca.add(Calendar.DAY_OF_MONTH, 14);//向前保证有14天
		String EndTime = String.format("%04d-%02d-%02d",ca.get(Calendar.YEAR),ca.get(Calendar.MONTH)+1,ca.get(Calendar.DAY_OF_MONTH));
		this.quickHttpRequest(ID_GETDATA, new ActivityEventRun(startTime, EndTime));
	}
	
	private void initCalendar() {
		rootCalenderLinearLayout = (LinearLayout) findViewById(R.id.root_calendar_layout_id);
		flipper = (ViewFlipper) findViewById(R.id.flipper);
		flipper.removeAllViews();
		// 当前时间
		Calendar calendar = Calendar.getInstance();
		year_c = calendar.get(Calendar.YEAR);
		month_c = calendar.get(Calendar.MONTH) + 1;
		day_c = calendar.get(Calendar.DAY_OF_MONTH);
		View v = addGridView();
		flipper.addView(v, 0,new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,android.view.ViewGroup.LayoutParams.FILL_PARENT));
	}
    @Override  
    public boolean dispatchTouchEvent(MotionEvent ev) {  
    	gestureDetector.onTouchEvent(ev); 
        return super.dispatchTouchEvent(ev);  
    } 
	
	private View addGridView() {
		// 手势
		gestureDetector = new GestureDetector(this, new MyGestureListener());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		int Width = rootCalenderLinearLayout.getMeasuredWidth();
		int numberColumns = 7;
		View v = this.getLayoutInflater(R.layout.calendar_gride);
		TextView[] txt = {
				(TextView) v.findViewById(R.id.curr_month1)
				,(TextView) v.findViewById(R.id.curr_month2)
		};
		
		for (int i = 0; i < 2; i++) {
			int nRes[] = { R.id.curr_gride1, R.id.curr_gride2 };
			gridView[i] = (MyGridView) v.findViewById(nRes[i]);
			gridView[i].setNumColumns(numberColumns);
			gridView[i].setColumnWidth(Width / numberColumns);
			gridView[i].setGravity(Gravity.CENTER_VERTICAL);
			gridView[i].setSelector(new ColorDrawable(Color.TRANSPARENT));
			// 去除gridView边框
			gridView[i].setVerticalSpacing(16);
			gridView[i].setHorizontalSpacing(2);
			gridView[i].setOnItemClickListener(this);
			gridView[i].setLayoutParams(params);
			
			adapter[i] = new CalendarAdapter(jumpMonth+i, jumpYear, year_c, month_c, day_c);
			txt[i].setText(String.format("%2d月",adapter[i].getCurrentMonth()));
			navBack.setText(String.format("%4d年", adapter[i].getCurrentYear()));
			gridView[i].setAdapter(adapter[i]);
		}
		return v;
	}

	private class MyGestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (e1.getY() - e2.getY() > 30) {			// 向上滑动
				enterNextMonth();
				return true;
			} else if (e1.getY() - e2.getY() < -30) {
				// 下
				enterPrevMonth();
				return true;
			}
			return false;
		}
	}

	// 移动到下个月
	private void enterNextMonth() {
		 jumpMonth+=2; // 下一个月
		 View v = addGridView(); // 添加gridView
		 flipper.addView(v, 1,new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,android.view.ViewGroup.LayoutParams.FILL_PARENT));
		 flipper.setInAnimation(AnimationUtils.loadAnimation(this,
		 R.anim.slide_in_from_bottom));
		 flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
		 R.anim.slide_out_to_top));
		 flipper.showNext();
		 flipper.removeViewAt(0);
		 onRefresh();
	}

	// 移动到上个月
	private void enterPrevMonth() {
		 jumpMonth-=2; // 上一个月
		 View v  = addGridView(); // 添gridView
		 flipper.addView(v, 0,new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,android.view.ViewGroup.LayoutParams.FILL_PARENT));
		 flipper.setInAnimation(AnimationUtils.loadAnimation(this,
		 R.anim.slide_in_from_top));
		 flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
		 R.anim.slide_out_to_bottom));
		 flipper.showPrevious();
		 flipper.removeViewAt(1);
		 onRefresh();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		CalendarAdapter adapter = (CalendarAdapter) arg0.getAdapter();
		DayNumber it = adapter.getItem(arg2);
		adapter.setCurrPos(arg2);
		Intent intent = new Intent(this,HCalendarAct.class);
		intent.putExtra("DayNumber", JSON.toJSONString(it));
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}
	protected void onBroadcastReceiverListener(Context context, Intent intent) {
		if (IBroadcastAction.ACTION_CREATE_CALENDAR.equals(intent.getAction())) {
			onRefresh();
			return;
		}
		if(IBroadcastAction.ACTION_DEL_CALENDAR.equals(intent.getAction())){
			//有可能一下子就删了好几天的，重新刷一下界面
			onRefresh();
			return;
		}
	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if(id == ID_GETDATA){
			if(obj.isOK()){
				ActivityEventResultBean o = (ActivityEventResultBean)obj;
				adapter[0].setStatus(o.getListData());
				adapter[1].setStatus(o.getListData());
			}
		}
		
	}
}
