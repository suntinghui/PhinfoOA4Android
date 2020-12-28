package cn.com.phinfo.oaact;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.pulltorefresh.PullToRefreshListView;
import com.heqifuhou.utils.FileUtils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import cn.com.phinfo.oaact.base.UMyCanMoveFileListBaseAct;
import cn.com.phinfo.protocol.AttacheFileRun.AttacheFileItem;
import cn.com.phinfo.protocol.FileSearchRun.UFileItem;

public class UMyOAFileListAct extends UMyCanMoveFileListBaseAct {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
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
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		UFileItem it = adapter.getItem(arg2 - 1);
		if (it.isFloder()) {
			Intent intent = new Intent(this, UMyOAFileListAct.class);
			intent.putExtra("srchType", srchType);
			intent.putExtra("title", title);
			intent.putExtra("folderid", it.getId());
			this.startActivity(intent);
		} else {
			/***
			Intent intent = new Intent(this, FileShowAct.class);
			intent.putExtra("AttacheFileItem",
					JSON.toJSONString(AttacheFileItem.init(it)));
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			***/

			AttacheFileItem attacheItem = AttacheFileItem.init(it);
			FileUtils.downloadAndOpenFile(this, attacheItem);
		}
	}
	
	protected void remove(final UFileItem it) {
		Intent intent = new Intent(this, UMySelectFileListAct.class);
		intent.putExtra("UFileItem",JSON.toJSONString(it));
		intent.putExtra("srchType","org");
		intent.putExtra("title","协作OA");
		intent.putExtra("folderid", "10010000-0000-0000-0000-000000000002");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);

	}
	
}
