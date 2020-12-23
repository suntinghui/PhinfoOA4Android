package com.heqifuhou.utils;

import java.util.Calendar;
import java.util.Date;

public class MyTimeUtils {
	public static int getDayCount(int mYear,int mMonth,int mDay){
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, mYear);
		c.set(Calendar.MONTH, mMonth);
		c.set(Calendar.DAY_OF_MONTH, mDay);
		Date  d = c.getTime();
		long l =  d.getTime()/(24 * 60 * 60 * 1000);//去掉小数造成的影响,换数据天
		return (int)l;
	}
	//相差的天数
	public static int differ(long oldSec,long newSec){
		//上次的某天
		Calendar aCalendar = Calendar.getInstance();
	    aCalendar.setTime(new Date(oldSec));
	    int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);
	    //当前天的
	    aCalendar.setTime(new Date(newSec));
	    int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
	    return day2-day1;
	}
}
