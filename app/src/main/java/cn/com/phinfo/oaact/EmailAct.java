package cn.com.phinfo.oaact;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.protocolbase.HttpResultBeanBase;

//邮件列
public class EmailAct extends HttpMyActBase implements OnClickListener{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTitleView(R.layout.nav_title_email_img);
		this.addTextNav(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(EmailAct.this, CreateEmailAct.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		},"邮件",R.drawable.ic_add);
		this.addViewFillInRoot(R.layout.act_email);
		this.findViewById(R.id.mailRev).setOnClickListener(this);
		this.findViewById(R.id.starRev).setOnClickListener(this);
		this.findViewById(R.id.draft).setOnClickListener(this);
		this.findViewById(R.id.send).setOnClickListener(this);
		this.findViewById(R.id.allmail).setOnClickListener(this);
		this.findViewById(R.id.queryBtn).setOnClickListener(this);
		
	}

	protected void onRefresh() {
	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj, Object requestObj) {
	
	}

	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()){
		case R.id.mailRev://收件箱
			startEmailList("收件箱","Inbox");
			break;
		case R.id.starRev://星标邮件
			startEmailList("星标邮件","star");
			break;
		case R.id.draft://草稿箱
			startEmailList("草稿箱","draft");
			break;
		case R.id.send://已发送
			startEmailList("已发送","Sent");
			break;
		case R.id.allmail://群发邮件
			startEmailList("群发邮件","group");
			break;
		case R.id.queryBtn://查询
			startSearchEmailAct();
			break;
		}
	}
	
	private void startEmailList(final String title,final String box){
		Intent intent = new Intent(this,EmailListAct.class);
		intent.putExtra("BOX", box);
		intent.putExtra("TITLE", title);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}
	private void startSearchEmailAct() {
		Intent intent = new Intent(this, SearchEmailAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}

	
}
