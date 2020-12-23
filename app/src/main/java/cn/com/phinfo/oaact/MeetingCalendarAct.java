package cn.com.phinfo.oaact;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import cn.com.phinfo.adapter.MeetingRecAdapter;
import cn.com.phinfo.adapter.MeetingRecListAdapter;
import cn.com.phinfo.protocol.DelMeetingRun;
import cn.com.phinfo.protocol.MeetingRecListRun;
import cn.com.phinfo.protocol.MeetingRecListRun.MeetingRecListItem;
import cn.com.phinfo.protocol.MeetingRecListRun.MeetingRecListResultBean;
import cn.com.phinfo.protocol.MettingRecRun;
import cn.com.phinfo.protocol.MettingRecRun.MettingRecResultBean;

import com.heqifuhou.actbase.HttpLoginMyActBase;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.ioscalendar.CalendarAdapter.DayNumber;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.view.ConfirmDialog;
import com.heqifuhou.view.ConfirmDialog.OnDialogOKListener;
import com.heqifuhou.view.PopupWindows;
import com.heqifuhou.view.PopupWindows.OnPopupWindowsItemListener;

public class MeetingCalendarAct extends HttpLoginMyActBase implements
		OnItemClickListener {
	private static final int ID_GET_MEETING = 0x11, ID_GET_MEETING_REC = 0x12,
			ID_DEL = 0x13;
	private GestureDetector gestureDetector = null;
	public static MeetingRecAdapter calAdapter = null;
	private ViewFlipper flipper = null;
	private GridView gridView = null;
	private static int jumpMonth = 0; // 每次滑动，增加或减去�?个月,默认�?0（即显示当前月）
	private static int jumpYear = 0; // 滑动跨越�?年，则增加或者减去一�?,默认�?0(即当前年)
	private int year_c = 0;
	private int month_c = 0;
	private int day_c = 0;
	private String currentDate = "";
	/** 每次添加gridview到viewflipper中时给的标记 */
	private int gvFlag = 0;
	// 修改后的上一个点击的日期
	public static View previousOnclickDateView;
	// 修改前的上一个点击的日期位置
	public static int[] previousOnclickDateViewPosition = new int[3];
	private LinearLayout rootCalenderLinearLayout;
	private ListView listView;
	private MeetingRecListAdapter adapterList;
	private TextView titleDate;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTitleView(R.layout.nav_f5f5f5_btn);
		this.addViewFillInRoot(R.layout.act_meeting_calendar);
		titleDate = (TextView) this.findViewById(R.id.titleDate);
		rootCalenderLinearLayout = (LinearLayout) findViewById(R.id.root_calendar_layout_id);
		this.addTextNav(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MeetingCalendarAct.this,
						MeetingCreateAct.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		}, "会议", R.drawable.ic_add);
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
		currentDate = sdf.format(date); // 当期日期
		year_c = Integer.parseInt(currentDate.split("-")[0]);
		month_c = Integer.parseInt(currentDate.split("-")[1]);
		day_c = Integer.parseInt(currentDate.split("-")[2]);
		initCalendar();
		listView = (ListView) findViewById(R.id.listView);
		adapterList = new MeetingRecListAdapter();
		listView.setAdapter(adapterList);
		listView.setOnItemClickListener(this);
		onRefresh();
	}

	protected void onRefresh() {
		Calendar ca = Calendar.getInstance();
		ca.set(Calendar.YEAR, calAdapter.getShowYear());
		ca.set(Calendar.MONTH, calAdapter.getShowMonth() - 1);
		ca.set(Calendar.DAY_OF_MONTH, 1);// 向前保证有7天
		ca.add(Calendar.DAY_OF_MONTH, -7);// 向前保证有7天
		String startTime = String.format("%04d-%02d-%02d",
				ca.get(Calendar.YEAR), ca.get(Calendar.MONTH) + 1,
				ca.get(Calendar.DAY_OF_MONTH));

		ca.set(Calendar.YEAR, calAdapter.getShowYear());
		ca.set(Calendar.MONTH, calAdapter.getShowMonth() - 1);
		ca.set(Calendar.DAY_OF_MONTH,
				ca.getActualMaximum(Calendar.DAY_OF_MONTH));
		ca.add(Calendar.DAY_OF_MONTH, 14);// 向后保证有7天
		String EndTime = String.format("%04d-%02d-%02d", ca.get(Calendar.YEAR),
				ca.get(Calendar.MONTH) + 1, ca.get(Calendar.DAY_OF_MONTH));
		this.quickHttpRequest(ID_GET_MEETING_REC, new MettingRecRun(startTime,
				EndTime));
	}

	private void initCalendar() {
		gestureDetector = new GestureDetector(this, new MyGestureListener());
		flipper = (ViewFlipper) findViewById(R.id.flipper);
		flipper.removeAllViews();
		calAdapter = new MeetingRecAdapter(jumpMonth, jumpYear, year_c,
				month_c, day_c);
		addGridView();
		gridView.setAdapter(calAdapter);
		flipper.addView(gridView, 0);
		addTextToTopTextView();
	}

	private class MyGestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			int gvFlag = 0; // 每次添加gridview到viewflipper中时给的标记
			if (e1.getX() - e2.getX() > 120) {
				// 像左滑动
				enterNextMonth(gvFlag);
				return true;
			} else if (e1.getX() - e2.getX() < -120) {
				// 向右滑动
				enterPrevMonth(gvFlag);
				return true;
			}
			return false;
		}
	}

	// 下一个月
	private void enterNextMonth(int gvFlag) {
		addGridView();
		jumpMonth++; // 下一个月
		calAdapter = new MeetingRecAdapter(jumpMonth, jumpYear, year_c,
				month_c, day_c);
		gridView.setAdapter(calAdapter);
		addTextToTopTextView(); // 移动到下�?月后，将当月显示在头标题�?
		gvFlag++;
		flipper.addView(gridView, gvFlag);
		flipper.setInAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_left_in));
		flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_left_out));
		flipper.showNext();
		flipper.removeViewAt(0);
		onRefresh();
	}

	// 上一个月
	private void enterPrevMonth(int gvFlag) {
		addGridView();
		jumpMonth--; // 上一个月
		calAdapter = new MeetingRecAdapter(jumpMonth, jumpYear, year_c,
				month_c, day_c);
		gridView.setAdapter(calAdapter);
		gvFlag++;
		addTextToTopTextView(); // 移动到上�?月后，将当月显示在头标题�?
		flipper.addView(gridView, gvFlag);

		flipper.setInAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_right_in));
		flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_right_out));
		flipper.showPrevious();
		flipper.removeViewAt(0);
		onRefresh();
	}

	protected void onBroadcastReceiverListener(Context context, Intent intent) {
		if (IBroadcastAction.ACTION_CREATE_MEETING.equals(intent.getAction())) {
			// String s = intent.getExtras().getString("RoomAppointItem");
			// RoomAppointItem it = JSON.parseObject(s, RoomAppointItem.class);
			// 此处省点事，直接刷新页面吧。
			onRefresh();
			return;
		}
	}

	public void addTextToTopTextView() {
		StringBuffer textDate = new StringBuffer();
		textDate.append(calAdapter.getShowYear()).append("年")
				.append(calAdapter.getShowMonth()).append("月");
		titleDate.setText(textDate.toString());
	}

	private void addGridView() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		int Width = rootCalenderLinearLayout.getMeasuredWidth();
		int numberColumns = 7;
		gridView = new GridView(this);
		gridView.setNumColumns(numberColumns);
		gridView.setColumnWidth(Width / numberColumns);
		gridView.setGravity(Gravity.CENTER_VERTICAL);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		// 去除gridView边框
		gridView.setVerticalSpacing(1);
		gridView.setHorizontalSpacing(1);
		gridView.setOnTouchListener(new OnTouchListener() {
			// 将gridview中的触摸事件回传给gestureDetector
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return MeetingCalendarAct.this.gestureDetector
						.onTouchEvent(event);
			}
		});
		gridView.setOnItemClickListener(this);
	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if (ID_GET_MEETING_REC == id) {
			if (obj.isOK()) {
				MettingRecResultBean meetingRec = (MettingRecResultBean) obj;
				calAdapter.setRoomStatus(meetingRec.getListData());
				getDataByDate(calAdapter.getToday());
			} else {
				showToast(obj.getMsg());
			}
			return;
		}
		if (ID_GET_MEETING == id) {
			if (obj.isOK()) {
				MeetingRecListResultBean o = (MeetingRecListResultBean) obj;
				adapterList.replaceListRef(o.getListData());
			} else {
				showToast(obj.getMsg());
			}
			return;
		}
		if (ID_DEL == id) {
			if (obj.isOK()) {
				showToast("删除成功");
				this.onRefresh();
				// adapterList.tryRemove(requestObj);
			} else {
				showToast(obj.getMsg());
			}
		}
	}

	private void getDataByDate(DayNumber it) {
		if (it == null) {
			return;
		}
		this.quickHttpRequest(ID_GET_MEETING,
				new MeetingRecListRun(it.getYMD()));
	}
	private PopupWindows popDialog;
	private ConfirmDialog confirmDialog;
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (parent.getAdapter() == calAdapter) {
			DayNumber it = calAdapter.getItem(position);
			calAdapter.setCurrPos(position);
			getDataByDate(it);
		}else{
			if (popDialog != null && popDialog.isShowing()) {
				return;
			}
			final MeetingRecListItem it = adapterList.getItem(position);
			popDialog = new PopupWindows(this, this.findViewById(R.id.root),
					new String[] { "会议详情", "删除" });
			popDialog
					.setOnPopupWindowsItemListener(new OnPopupWindowsItemListener() {
						@Override
						public void onPopupWindowsItem(int pos) {
							if (pos == 0) {
								// 查看详情
								Intent intent = new Intent(MeetingCalendarAct.this,
										MeetingDetailAct.class);
								intent.putExtra("MeetingRecListItem", it);
								intent.putExtra("ID", it.getValueId());
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent);
							} else {
								// 删除
								if (confirmDialog != null
										&& confirmDialog.isShowing()) {
									return;
								}
								confirmDialog = new ConfirmDialog(
										MeetingCalendarAct.this, "您确定要删除么？", null);
								confirmDialog
										.setOnDialogOKListener(new OnDialogOKListener() {
											@Override
											public void onOKItem(Object obj) {
												quickHttpRequest(
														ID_DEL,
														new DelMeetingRun(
																it.getValueId()),
														it);
											}
										});
								confirmDialog.show();
							}
						}
					});
			popDialog.show();
		}
	}

	

}
