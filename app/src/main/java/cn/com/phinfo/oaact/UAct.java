package cn.com.phinfo.oaact;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.heqifuhou.actbase.MyActBase;

//
public class UAct extends MyActBase implements OnClickListener {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTitleView(R.layout.nav_white_btn);
		this.addTextNav("优盘");
		this.addViewFillInRoot(R.layout.act_u);
		this.findViewById(R.id.queryBtn).setOnClickListener(this);
		this.findViewById(R.id.aways).setOnClickListener(this);
		this.findViewById(R.id.myfile).setOnClickListener(this);
		this.findViewById(R.id.sharefile).setOnClickListener(this);
		this.findViewById(R.id.work).setOnClickListener(this);
		this.findViewById(R.id.queryBtn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startSearchUFileAct();
			}
		});
	}
	private void startSearchUFileAct(){
		Intent intent = new Intent(this,SearchUFileAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.queryBtn:
			break;
		case R.id.aways:
			startMyAwayFileListAct("latestuse","最新使用");
			break;
		case R.id.myfile:
			startUMyFileListAct();
			break;
		case R.id.sharefile:
			startMyAwayFileListAct("share","共享文件");
			break;
		case R.id.work:
			startMyOAAct();
			break;
		}
	}
	
	private void startMyOAAct(){
		Intent intent = new Intent(this, UMyOAFileListAct.class);
		intent.putExtra("srchType", "org");
		intent.putExtra("title", "协作OA");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}

	private void startMyAwayFileListAct(String srchType,String title) {
		Intent intent = new Intent(this, UMyAwayFileListAct.class);
		intent.putExtra("srchType", srchType);
		intent.putExtra("title", title);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}
	private void startUMyFileListAct() {
		Intent intent = new Intent(this, UMyFileListAct.class);
		intent.putExtra("srchType", "my");
		intent.putExtra("title", "我的文件");
		intent.putExtra("folderid", "10010000-0000-0000-0000-000000000001");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}
	
}
