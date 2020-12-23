package cn.com.phinfo.oaact;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class LBSAddressSelectAct extends LBSAddressAct {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTextNav(new OnClickListener() {
			@Override
			public void onClick(View arg0) { 
				startResult();
			}
		}, "选择地点", "确定");
	}
}
