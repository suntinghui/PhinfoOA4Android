package com.heqifuhou.ioscalendar;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import cn.com.phinfo.oaact.R;
import cn.com.phinfo.protocol.ActivityEventRun.ActivityEventItem;

import com.heqifuhou.adapterbase.MyAdapterBaseAbs;

public class HDateAdapter extends MyAdapterBaseAbs<HDataItem> {
	private Calendar calendar;
	private int nPos =0;
	private String currYMD;
	public HDateAdapter(int year, int month, int day) {
		calendar =  getCalendarDate(year,month,day);
		this.initWeek();
		this.initDefault(year, month, day);
		this.initCurrDay();
	}
	
	private void initCurrDay(){
		Calendar cal = Calendar.getInstance();
		int y = cal.get(Calendar.YEAR);
		int m = cal.get(Calendar.MONTH);
		int d = cal.get(Calendar.DAY_OF_MONTH);
		currYMD = String.format("%04d-%02d-%02d", y,m+1,d);
	}
	private void initDefault(int year, int month, int day){
		for(int i=0;i<this.getCount();i++){
			HDataItem it = this.getItem(i);
			if(it.getYear() == year&&it.getMonth()==month&&it.getDay() == day){
				nPos = i;
				break;
			}
		}
	}
	//当前是第几周
	public int getWeekOfYear(){
		Calendar c=Calendar.getInstance();
        int i = c.get(Calendar.WEEK_OF_YEAR);
        return i;
	}
	private void initWeek(){
		this.clear();
		for(int i=0;i<7;i++){
			if(i>0){
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			}
			int y = calendar.get(Calendar.YEAR);
			int m = calendar.get(Calendar.MONTH);
			int d = calendar.get(Calendar.DAY_OF_MONTH);
			this.addToListBack(new HDataItem(y,m,d));
		}
	}
	public void addWeek(){
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		this.initWeek();
	}
	public void decWeek(){
		calendar.add(Calendar.DAY_OF_MONTH, -13);
		this.initWeek();
	}
	
	public HDataItem getPosItem(){
		if(this.nPos>=0&&this.nPos<this.getCount()){
			return this.getItem(this.nPos);
		}
		return null;
	}
	//根据传入的时间计算他的日历
	private Calendar getCalendarDate(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		Date d = cal.getTime();
		cal.setTime(d);
		// Calendar默认周日为第一天, 所以设置为1
		cal.set(Calendar.DAY_OF_WEEK, 1);
		// 如果要返回00点0分0秒
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.item_calendar, null);
			holder.tvCalendar = (TextView) convertView.findViewById(R.id.tv_calendar);
			holder.alert =  convertView.findViewById(R.id.alert_date_selected_icon_id);
			holder.convertView = convertView;
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		HDataItem it = this.getItem(position);
		holder.tvCalendar.setText(String.valueOf(it.getDay()));
		//计算是不是今天
		if (it.getSel()) {
			holder.alert.setVisibility(View.VISIBLE);
		}else{
			holder.alert.setVisibility(View.INVISIBLE);
		}
		//计算是不是今天
		if(currYMD.equals(it.getYMD())){
			holder.tvCalendar.setSelected(true);
			holder.tvCalendar.setTextColor(Color.WHITE);
			holder.convertView.setBackgroundResource(R.drawable.circle_message);
		}else{
			if(nPos ==position){
				holder.tvCalendar.setSelected(true);
				holder.tvCalendar.setTextColor(Color.WHITE);
				holder.convertView.setBackgroundResource(R.drawable.calendar_bg_sel);
			}else {
				holder.tvCalendar.setSelected(false);
				holder.tvCalendar.setTextColor(Color.BLACK);
				holder.convertView.setBackgroundColor(Color.TRANSPARENT);
			}
		}
		final View view = convertView;
		convertView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				view.getViewTreeObserver().removeOnPreDrawListener(this);
				int height = view.getMeasuredHeight();
				ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
				layoutParams.width = height;
				view.setLayoutParams(layoutParams);
				return true;
			}
		});
		return convertView;
	}
	class Holder{
		TextView tvCalendar;
		View alert,convertView;
	}
	public void setSeclection(int pos){
		if(nPos>=0&&nPos<this.getCount()){
			nPos = pos;
			this.notifyDataSetChanged();
		}
	}

	public void setStatus(List<ActivityEventItem> lst) {
		for (int i = 0; i < this.getCount(); i++) {
			HDataItem it = this.getItem(i);
			it.setSel(isFound(lst, it.getTime()));
		}
		this.notifyDataSetChanged();
	}

	public boolean isFound(List<ActivityEventItem> lst, long l) {
		for (int i = 0; i < lst.size(); i++) {
			ActivityEventItem it = lst.get(i);
			if (it.isBetween(l)) {
				return true;
			}
		}
		return false;
	}

}
