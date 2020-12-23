/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.heqifuhou.utils;

import java.text.DecimalFormat;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;

/**
 * Class containing some static utility methods.
 */
public class SystemUtils {
	private static final String TAG = SystemUtils.class.getSimpleName();

	public static boolean hasFroyo() {
		// Can use static final constants like FROYO, declared in later versions
		// of the OS since they are inlined at compile time. This is guaranteed
		// behavior.
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}

	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
	}

	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
	}

	public static boolean hasJellyBean() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	}

	public static void hideSoftInput(Context ctx, View view) {
		InputMethodManager imm = (InputMethodManager) ctx
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public static void showSoftInput(Context ctx, View view) {
		InputMethodManager imm = (InputMethodManager) ctx
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
	}


	/**
	 * 返回byte的数据大小对应的文本
	 * 
	 * @param size
	 *            文件大小
	 * @return "size: error" 格式化后的文件大小
	 */
	public static String formatSize(long size) {
		DecimalFormat nFormat = new DecimalFormat("####.00");
		String sSzie = "< 1";
		double bsize = size;
		if (bsize < 1024) {
			sSzie = String.valueOf((int) bsize) + "B";
			return sSzie;
		}
		nFormat.setMaximumFractionDigits(1);
		double ksize = bsize / 1024;
		if (ksize < 1024) {
			sSzie = nFormat.format(ksize);
			sSzie = sSzie + "KB";
			return sSzie;
		}
		nFormat.setMaximumFractionDigits(2);
		double msize = ksize / 1024;
		if (msize < 1024) {
			sSzie = nFormat.format(msize);
			sSzie = sSzie + "MB";
			return sSzie;
		}
		double gsize = msize / 1024;
		if (gsize < 1024) {
			sSzie = nFormat.format(gsize);
			sSzie = sSzie + "GB";
			return sSzie;
		}
		double tsize = gsize / 1024;
		sSzie = nFormat.format(tsize);
		sSzie = sSzie + "TB";
		return sSzie;
	}

	/**
	 * 设置当前view按下效果阴影
	 */
	@SuppressLint("NewApi")
	public static void pressEffect(final View view) {
	    int sysVersion = Integer.parseInt(VERSION.SDK);
	    if (sysVersion>11) {
	        view.setOnTouchListener(new OnTouchListener() {
                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
				@Override
	            public boolean onTouch(View v, MotionEvent event) {
	                
	                switch (event.getAction()) {
	                case MotionEvent.ACTION_DOWN:
	                    view.setAlpha(0.5f);
	                    break;

	                case MotionEvent.ACTION_UP:
	                    view.setAlpha(1.0f);
	                    break;
	                }
	                return false;
	            }
	        });
	    }
    }
	
}
