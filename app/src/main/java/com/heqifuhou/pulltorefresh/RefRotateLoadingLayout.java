package com.heqifuhou.pulltorefresh;

import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Orientation;

import android.content.Context;
import android.content.res.TypedArray;
import cn.com.phinfo.oaact.R;

public class RefRotateLoadingLayout extends RotateLoadingLayout {

	public RefRotateLoadingLayout(Context context, Mode mode,
			Orientation scrollDirection, TypedArray attrs) {
		super(context, mode, scrollDirection, attrs);
		String s = this.getResources().getString(R.string.pull_to_refresh_refreshing_label);
		setPullLabel(s);
		setReleaseLabel(s);
		setLastUpdatedLabel("");
	}
//	private View vRefRotate = null;
//
//	public RefRotateLoadingLayout(Context context, Mode mode,
//			Orientation scrollDirection, TypedArray attrs) {
//		super(context, mode, scrollDirection, attrs);
//		//init();
//	}
//
//	private void init() {
//		vRefRotate = LayoutInflater.from(getContext()).inflate(
//				R.layout.loading_foot, null);
//		this.addView(vRefRotate, LayoutParams.MATCH_PARENT,
//				LayoutParams.MATCH_PARENT);
//	}

}
