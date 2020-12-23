package com.heqifuhou.utils;

public class ParamsCheckUtils {
	public static boolean isNull(final String sz){
		if (sz == null || sz.trim().length() <= 0) {
			return true;
		}
		return false;
	}
	public static boolean isNull(final byte[] b){
		if (b == null || b.length <= 0) {
			return true;
		}
		return false;
	}
	public static boolean isNull(final Object sz){
		if (sz == null) {
			return true;
		}
		return false;
	}
	public static boolean isEquals(final String sz1,final String sz2,boolean bEqualsNull){
		if(bEqualsNull){
			if(sz1 == null&& sz2== null){
				return true;
			}
		}
		if(sz1!=null&&sz2!=null){
			return sz1.equals(sz2);
		}
		return false;	
	}
}
