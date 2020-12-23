package cn.com.phinfo.oaact;

import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.protocolbase.HttpResultBeanBase;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

//注册
public class RegisterAct extends HttpMyActBase implements OnClickListener {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTextNav("注册");
//		this.addViewFillInRoot(R.layout.act_register1);  
//		TextView tip = (TextView) this.findViewById(R.id.tip);
//		tip.setText(Html
//				.fromHtml("<font color=\"#65bbd3\">《<u>用户注册协议</u>》</font>"));
//		tip.setOnClickListener(this);
	    this.onRefresh();
	}
	
	protected void onRefresh(){
		//所有的结构列表
//		this.quickHttpRequest(ID_GET_HOSPTIALS, new FetchHospitalsRun());
	}

	@Override
	public void onClick(View v) {
		// 注册
		if (v.getId() == R.id.btn) {
			nextAction();
			return;
		}
	
	}
	//下一步
	private void nextAction(){

	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
	}


}
