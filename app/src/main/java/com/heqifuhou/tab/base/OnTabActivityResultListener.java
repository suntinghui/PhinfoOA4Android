package com.heqifuhou.tab.base;

import android.content.Intent;

public interface OnTabActivityResultListener {
	void onTabActivityResult(int requestCode, int resultCode, Intent data);
}
