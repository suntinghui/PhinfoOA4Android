package com.heqifuhou.ioscalendar;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HDataItem {
	private int year;
	private int month;
	private int day;
	private boolean sel;
	public HDataItem(){
		
	}
	public HDataItem(int y,int m,int d){
		this(y,m,d,false);
	}
	public HDataItem(int y,int m,int d,boolean sel){
		this.year = y;
		this.month = m;
		this.day = d;
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
		this.month = month;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int d) {
		this.day = d;
	}
	public boolean getSel() {
		return sel;
	}
	public void setSel(boolean sel) {
		this.sel = sel;
	}
	
	public String getYYYYMMDD2China(){
		String date = String.format("%04d年%02d月%02d日", year,
				month+1, day);
		return date;
	}
	public String getYMD()
	{
		return String.format("%04d-%02d-%02d", year,
				month+1, day);
	}
	public String getDate(){
		String date = String.format("%04d-%02d-%02d 00:00:00", year,
				month+1, day);
		return date;
	}
	public String getMonth2China(){
		return getMonth2China(month);
	}
	public static String getMonth2China(int month){
		switch(month){
		case 0:return "一";
		case 1:return "二";
		case 2:return "三";
		case 3:return "四";
		case 4:return "五";
		case 5:return "六";
		case 6:return "七";
		case 7:return "八";
		case 8:return "九";
		case 9:return "十";
		case 10:return "十一";
		case 11:return "十一";
		}
		return "";
	}
	public long getTime(){
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date d = sdf.parse(getDate());
			long l =  d.getTime();
			return l;
		}catch(Exception e){
			return 0;
		}
	}
}
