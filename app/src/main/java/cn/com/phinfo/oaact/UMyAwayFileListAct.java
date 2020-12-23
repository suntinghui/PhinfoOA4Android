package cn.com.phinfo.oaact;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import cn.com.phinfo.oaact.base.UMyFileListBaseAct;

import com.heqifuhou.pulltorefresh.PullToRefreshListView;

public class UMyAwayFileListAct extends UMyFileListBaseAct{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	protected void init(){
		this.addTitleView(R.layout.nav_white_btn);
		this.addTextNav(title);
		this.addViewFillInRoot(R.layout.act_ufile_refresh);
		mList = (PullToRefreshListView) this.findViewById(R.id.refreshListView);
		this.findViewById(R.id.queryBtn).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						startSearchUFileAct();
					}
				});
	}


}
