package com.heqifuhou.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;

public class ResUtils {
	public static ColorStateList  getColorStateList(Context c,int colorID){
		Resources resource = (Resources)c.getResources();  
		ColorStateList csl = (ColorStateList) resource.getColorStateList(colorID); 
		return csl;
	}
	
	public static  String getResString(Context c,final int nResTxt) {
		String t = "";
		if (nResTxt > 0) {
			t = c.getResources().getString(nResTxt);
		}
		return t;
	}
 
}
