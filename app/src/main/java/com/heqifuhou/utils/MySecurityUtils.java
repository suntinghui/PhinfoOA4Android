package com.heqifuhou.utils;

public class MySecurityUtils {
	public static byte[] getMD5Binary(byte[] source) {
		byte tmp[] = null;
		try {
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			md.update(source);
			tmp = md.digest();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tmp;
	}
	public static String bytes2Hex(byte[] bts) {
		String des = "";
		String tmp = null;
		for (int i = 0; i < bts.length; i++) {
			tmp = (Integer.toHexString(bts[i] & 0xFF));
			if (tmp.length() == 1) {
				des += "0";
			}
			des += tmp;
		}
		return des;
	}
}
