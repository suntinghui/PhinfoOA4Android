package com.heqifuhou.ioscalendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.heqifuhou.adapterbase.MyAdapterBaseAbs;
import com.heqifuhou.ioscalendar.CalendarAdapter.DayNumber;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.com.phinfo.oaact.R;
import cn.com.phinfo.protocol.ActivityEventRun.ActivityEventItem;

/**
 * 日历gridview中的每一个item显示的textview
 *
 * @author Vincent Lee
 *
 */
public class CalendarAdapter extends MyAdapterBaseAbs<DayNumber> {
	private final static String TAG = CalendarAdapter.class.getCanonicalName();
	// 当月的提醒对象列表
	// private List<AlertSetting> alertSettings;
	private boolean isLeapyear = false; // 是否为闰年
	private int daysOfMonth = 0; // 某月的天数
	private int dayOfWeek = 0; // 具体某一天是星期几
	private int lastDaysOfMonth = 0; // 上一个月的总天数
	private int dayNumberLength = 42;
	private SpecialCalendar specialCalendar = null;
	private LunarCalendar lunarCalendar = null;
	private int currentYear;
	private int currentMonth;
	private int currentDay;
	private int currentFlag = -1; // 用于标记当天
	private int showYear; // 用于在头部显示的年份
	private int showMonth; // 用于在头部显示的月份
	private String animalsYear;
	private int leapMonth; // 闰哪一个月
	private String cyclical = ""; // 天干地支
	// 系统当前时间
	private int systemYear;
	private int systemMonth;
	private int systemDay;
	private int currPos=0;
	public CalendarAdapter(int jumpMonth, int jumpYear, int year_c, int month_c, int day_c) {
		Calendar calendar = Calendar.getInstance();
		systemYear = calendar.get(Calendar.YEAR);
		systemMonth = calendar.get(Calendar.MONTH);
		systemDay = calendar.get(Calendar.DAY_OF_MONTH);
		specialCalendar = new SpecialCalendar();
		lunarCalendar = new LunarCalendar();

		int stepYear = year_c + jumpYear;
		int stepMonth = month_c + jumpMonth;
		if (stepMonth > 0) {
			// 往下一个月滑动
			if (stepMonth % 12 == 0) {
				stepYear = year_c + stepMonth / 12 - 1;
				stepMonth = 12;
			} else {
				stepYear = year_c + stepMonth / 12;
				stepMonth = stepMonth % 12;
			}
		} else {
			// 往上一个月滑动
			stepYear = year_c - 1 + stepMonth / 12;
			stepMonth = stepMonth % 12 + 12;
			if (stepMonth % 12 == 0) {

			}
		}
		currentYear = stepYear; // 得到当前的年份
		currentMonth = stepMonth; // 得到本月
									// （jumpMonth为滑动的次数，每滑动一次就增加一月或减一月）
		currentDay = day_c; // 得到当前日期是哪天

		getCalendar(currentYear, currentMonth);
		this.currPos = currentFlag;
	}

	public int getCurrentMonth() {
		return currentMonth;
	}

	public int getCurrentYear() {
		return currentYear;
	}

	public int getCurrentDay() {
		return currentDay;
	}

	public DayNumber getToday(){
		if(currentFlag>=0&&currentFlag<this.getCount()){
			return this.getItem(currentFlag);
		}
		return null;
	}
	// @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = View.inflate(parent.getContext(), R.layout.calendar_item, null);
			viewHolder.convertView = convertView;
			// 阳历日期文本控件
			viewHolder.dateTextView = (TextView) convertView
					.findViewById(R.id.alert_date_text_id);
			// 阴历日期文本控件
			viewHolder.chinaDateTextView = (TextView) convertView
					.findViewById(R.id.alert_china_date_text_id);
			viewHolder.alert_date_selected_icon_id = convertView
					.findViewById(R.id.alert_date_selected_icon_id);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		DayNumber dayNumber = this.getItem(position);
		viewHolder.dateTextView .setText(String.valueOf(dayNumber.day));
		viewHolder.chinaDateTextView.setText(dayNumber.chinaDayString);
		viewHolder.alert_date_selected_icon_id.setVisibility(View.INVISIBLE);
	
		if (currentFlag == position) {
			setCheckedBackground(viewHolder,dayNumber);
		} else {
			recoverStyle(viewHolder,dayNumber,position);
			if(this.currPos == position){
				setSelBackground(viewHolder,dayNumber);
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

	public void setCurrPos(int pos){
		this.currPos = pos;
		this.notifyDataSetChanged();
	}
	//设置选中时的背景色
	public void setSelBackground(ViewHolder viewHolder,DayNumber dayNumber) {
		viewHolder.convertView.setBackgroundResource(R.drawable.calendar_bg_sel);
		viewHolder.dateTextView.setTextColor(Color.WHITE);
		viewHolder.chinaDateTextView.setTextColor(Color.WHITE);
	}
	//设置选中时的背景色
	public void setCheckedBackground(ViewHolder viewHolder,DayNumber dayNumber) {
		viewHolder.convertView.setBackgroundResource(R.drawable.calendar_bg_curr);
		viewHolder.dateTextView.setTextColor(Color.WHITE);
		viewHolder.chinaDateTextView.setTextColor(Color.WHITE);
	}
	
	public void recoverStyle(ViewHolder viewHolder,DayNumber dayNumber,int position){
		viewHolder.convertView.setBackgroundResource(android.R.color.transparent);	
		viewHolder.dateTextView.setTextColor(0xffb1b1b);// 当月字体设黑
		boolean bBack = false;
		viewHolder.chinaDateTextView.setTextColor(viewHolder.dateTextView.getTextColors());// 当月字体设黑
		if (position < daysOfMonth + dayOfWeek && position >= dayOfWeek) {
			// 当前月信息显示
			bBack = true;
			viewHolder.dateTextView.setTextColor(0xff666666);// 当月字体设黑
			viewHolder.chinaDateTextView.setTextColor(viewHolder.dateTextView.getTextColors());// 当月字体设黑
			if (position % 7 == 0 || position % 7 == 6) {
				// 当前月信息显示
				bBack = true;
				viewHolder.dateTextView.setTextColor(0xffb1b1b1);// 当月字体设黑
				viewHolder.chinaDateTextView.setTextColor(viewHolder.dateTextView.getTextColors());// 当月字体设黑
			}
		}
		if(bBack&&dayNumber.getSel()){
			viewHolder.alert_date_selected_icon_id.setVisibility(View.VISIBLE);
		}
	}

	// 得到某年的某月的天数且这月的第一天是星期几
	public void getCalendar(int year, int month) {
		isLeapyear = specialCalendar.isLeapYear(year); // 是否为闰年
		daysOfMonth = specialCalendar.getDaysOfMonth(isLeapyear, month); // 某月的总天数
		dayOfWeek = specialCalendar.getWeekdayOfMonth(year, month); // 某月第一天为星期几
		lastDaysOfMonth = specialCalendar.getDaysOfMonth(isLeapyear, month - 1); // 上一个月的总天数
		getDayNumber(year, month);
	}

	// 将一个月中的每一天的值添加入数组dayNumber中
	private void getDayNumber(int year, int month) {
		int j = 1;
		String lunarDay = "";
		for (int i = 0; i < dayNumberLength; i++) {
			DayNumber dayNumber = new DayNumber();
			dayNumber.setYear(year);
			if (i < dayOfWeek) { // 前一个月
				int temp = lastDaysOfMonth - dayOfWeek + 1;
				lunarDay = lunarCalendar.getLunarDate(year, month - 1, temp + i, false);
				dayNumber.setMonth(month - 1);
				dayNumber.setDay(temp + i);
				dayNumber.setChinaDayString(lunarDay);

			} else if (i < daysOfMonth + dayOfWeek) { // 本月
				int day = i - dayOfWeek + 1; // 得到的日期
				lunarDay = lunarCalendar.getLunarDate(year, month, i - dayOfWeek + 1, false);
				dayNumber.setMonth(month);
				dayNumber.setDay(day);
				dayNumber.setChinaDayString(lunarDay);
				// 对于当前月才去标记当前日期
				if (systemYear == year && systemMonth + 1 == month && systemDay == day) {
					// 标记当前日期
					currentFlag = i;
				}
				setShowYear(year);
				setShowMonth(month);
				setAnimalsYear(lunarCalendar.animalsYear(year));
				setLeapMonth(lunarCalendar.leapMonth == 0 ? 0 : lunarCalendar.leapMonth);
				setCyclical(lunarCalendar.cyclical(year));
			} else { // 下一个月
				lunarDay = lunarCalendar.getLunarDate(year, month + 1, j, false);
				dayNumber.setMonth(month + 1);
				dayNumber.setDay(j);
				dayNumber.setChinaDayString(lunarDay);
				j++;
			}
			this.addToListBackWithOutNotifyData(dayNumber);
		}
		String abc = "";
		for (int i = 0; i < dayNumberLength; i++) {
			abc = abc + this.getItem(i).getYear() + "-" + this.getItem(i).getMonth() + "-" + this.getItem(i).getDay()
					+ " " + this.getItem(i).getChinaDayString();
			if ((i + 1) % 7 == 0) {
				abc = abc + "\n";
			}
		}
	}

	public void setStatus(List<ActivityEventItem> lst){
		for(int i=0;i<this.getCount();i++){
			DayNumber it = this.getItem(i);
			it.setSel(isFound(lst, it.getTime()));
		}
		this.notifyDataSetChanged();
	}
	
	public boolean isFound(List<ActivityEventItem> lst,long l)
	{
		for(int i=0;i<lst.size();i++){
			ActivityEventItem it = lst.get(i);
			if(it.isBetween(l)){
				return true;
			}
		}
		return false;
	}

	/**
	 * 在点击gridView时，得到这个月中第一天的位置
	 *
	 * @return
	 */
	public int getStartPositon() {
		return dayOfWeek + 7;
	}

	/**
	 * 在点击gridView时，得到这个月中最后一天的位置
	 *
	 * @return
	 */
	public int getEndPosition() {
		return (dayOfWeek + daysOfMonth + 7) - 1;
	}

	public int getShowYear() {
		return showYear;
	}

	public void setShowYear(int showYear) {
		this.showYear = showYear;
	}

	public int getShowMonth() {
		return showMonth;
	}

	public void setShowMonth(int showMonth) {
		this.showMonth = showMonth;
	}

	public String getAnimalsYear() {
		return animalsYear;
	}

	public void setAnimalsYear(String animalsYear) {
		this.animalsYear = animalsYear;
	}

	public int getLeapMonth() {
		return leapMonth;
	}

	public void setLeapMonth(int leapMonth) {
		this.leapMonth = leapMonth;
	}

	public String getCyclical() {
		return cyclical;
	}

	public void setCyclical(String cyclical) {
		this.cyclical = cyclical;
	}

	public static class CalendarItemViewHolder {
		public boolean ifExistsAlert = false;
	}
	
	public static class ViewHolder{
		View convertView;
		TextView dateTextView,chinaDateTextView;
		View alert_date_selected_icon_id;
	}

	public static class DayNumber {
		private int year;
		private int month;
		private int day;
		private int week;
		private String chinaDayString;
		//当前是否有数据
		private boolean sel=false;
		public boolean getSel() {
			return sel;
		}
		public String getYMD()
		{
			return String.format("%04d-%02d-%02d", year,
					month, day);
		}
		public void setSel(boolean sel) {
			this.sel = sel;
		}

		public int getYear() {
			return year;
		}

		public void setYear(int year) {
			this.year = year;
		}

		public int getMonth() {
			return month;
		}
	

		public void setMonth(int month) {
			if (month < 0) {
				this.year -= 1;
				this.month = 12;
				return;
			}

			if (month > 12) {
				this.year += 1;
				this.month = month - 12;
				return;
			}
			this.month = month;
		}

		public int getDay() {
			return day;
		}

		public void setDay(int day) {
			this.day = day;
		}

		public int getWeek() {
			return week;
		}

		public void setWeek(int week) {
			this.week = week;
		}

		public String getChinaDayString() {
			return chinaDayString;
		}

		public void setChinaDayString(String chinaDayString) {
			this.chinaDayString = chinaDayString;
		}
		
		public long  getTime(){
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date d = sdf.parse(String.format("%04d-%02d-%02d 00:00:00", this.year,this.month,this.day));
				return d.getTime();
			} catch (ParseException e) {
				return 0;
			}
		}
	}
}
