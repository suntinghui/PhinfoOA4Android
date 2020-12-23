package com.heqifuhou.utils;


public class FormatUtils {
	public static String getCentsToFloatStr(float d){
//		NumberFormat df=NumberFormat.getNumberInstance() ;
//		df.setMaximumFractionDigits(2);
//		String s =  df.format(d) ;
//		return s;
		float ds = d/ 100f;
		return getFloatToStr(ds);
	}
	public static String getFloatToStr(float ds){
		java.text.DecimalFormat   df   =new   java.text.DecimalFormat("0.00");  
		String s =  df.format(ds);
		return s;
	}
}
