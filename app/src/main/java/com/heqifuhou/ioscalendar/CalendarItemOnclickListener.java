package com.heqifuhou.ioscalendar;

import com.heqifuhou.ioscalendar.CalendarAdapter.CalendarItemViewHolder;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.com.phinfo.oaact.R;

public class CalendarItemOnclickListener implements OnItemClickListener {
	private Activity activity;
	public CalendarItemOnclickListener(Activity activity) {
		this.activity = activity;
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
//		CalendarItemViewHolder viewHolder = (CalendarItemViewHolder) view
//				.getTag();
//		// TODO Auto-generated method stub
//		// 点击任何一个item，得到这个item的日期(排除点击的是周日到周六(点击不响应))
//		int startPosition = CalendarActivity.calV.getStartPositon();
//		int endPosition = CalendarActivity.calV.getEndPosition();
//		// View defaultView = view;
//		// 设置当前点击日期背景颜色
//		CalendarActivity.calV.setCheckedBackground(view);
//		// 其他日期去除添加的背景色
//		if (null != CalendarActivity.previousOnclickDateView
//				&& CalendarActivity.previousOnclickDateView != view) {
//			CalendarActivity.calV.recoverStyle(
//					CalendarActivity.previousOnclickDateView,
//					CalendarActivity.previousOnclickDateViewPosition);
//		}
//		CalendarActivity.previousOnclickDateView = view;
//		int scheduleDay = CalendarActivity.calV.getDateByClickItem(
//				position).getDay(); // 这一天的阳历
//		// String scheduleLunarDay =
//		// calV.getDateByClickItem(position).split("\\.")[1];
//		// //这一天的阴历
//		int scheduleYear = CalendarActivity.calV.getShowYear();
//		int scheduleMonth = CalendarActivity.calV.getShowMonth();
//		CalendarActivity.previousOnclickDateViewPosition[0] = position;
//		CalendarActivity.previousOnclickDateViewPosition[1] = Integer
//				.valueOf(scheduleDay);
//		CalendarActivity.previousOnclickDateViewPosition[2] = new SpecialCalendar()
//				.getWeekdayOfMonth(scheduleYear, scheduleMonth);
//		/*
//		 * if (startPosition <= position + 7 && position <= endPosition - 7) {
//		 *
//		 * // Toast.makeText(CalendarActivity.this, "点击了该条目", //
//		 * Toast.LENGTH_SHORT).show(); }
//		 */
//		Log.d(TAG, scheduleYear + "-" + scheduleMonth + "-" + scheduleDay);
//
//		if (viewHolder.ifExistsAlert) {
//			// if(CalendarActivity.previousOnclickDateView == view) return;
//			resetItems(scheduleYear, scheduleMonth - 1, scheduleDay);
//		} else {
//			ListView listView = (ListView) activity
//					.findViewById(R.id.service_message_scroll_view_id);
//			listView.setAdapter(null);
//		}
	}

	/**
	 * 插入提醒项
	 *
	 */
	public void resetItems(int year, int month, int day) {
		// 搜索当日所有提醒
		// List<AlertSetting> alertSettings = AlertSettingDao.getInstance()
		// .selectAll(
		// activity.getContentResolver(),
		// AlertContentProvider.COLUMN_YEAR + "=" + year + " AND "
		// + AlertContentProvider.COLUMN_MONTH + "="
		// + month + " AND "
		// + AlertContentProvider.COLUMN_DAY + "=" + day);
		// ListView listView = (ListView) activity
		// .findViewById(R.id.service_message_scroll_view_id);
		// listView.setAdapter(new CalendarSearchAlertAdapter(context,
		// CalendarActivity.INSTANCE, alertSettings));
		/*
		 * listView.setOnItemClickListener(new OnItemClickListener() {
		 *
		 * public void onItemClick(AdapterView<?> parent, View view, int
		 * position, long id) { ViewHolder viewHolder = (ViewHolder)
		 * view.getTag();
		 * viewHolder.selectedTextView.setVisibility(View.VISIBLE);
		 * view.setBackgroundColor(getResources().getColor(
		 * android.R.color.transparent)); int length = parent.getCount();
		 * for(int i = 0 ; i < length ; i++){ if(i== position) continue;
		 * parent.getChildAt(i).findViewById(R.id.alert_calendar_selected_id
		 * ).setVisibility(View.GONE); } } });
		 */

	}

}
