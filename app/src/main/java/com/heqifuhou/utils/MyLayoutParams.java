package com.heqifuhou.utils;

import android.content.Context;

public class MyLayoutParams {
	public static int dp2px(Context context, int dp) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

//	public static setLayouParams(Context mContext,View view,int maxDp) {
//		int maxHeight = dp2px(mContext, maxDp);
//		int height = (int) ((float) view.getWidth()
//				/ drawable.getMinimumWidth() * drawable.getMinimumHeight());
//		if (height > maxHeight)
//			height = maxHeight;
//		view.setLayoutParams(new LayoutParams(
//				LayoutParams.MATCH_PARENT, height));
//
//	}
}
