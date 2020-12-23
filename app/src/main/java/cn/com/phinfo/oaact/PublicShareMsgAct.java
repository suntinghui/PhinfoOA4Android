package cn.com.phinfo.oaact;


import com.heqifuhou.imgutils.MyBaseBitmapAct;
import com.heqifuhou.protocolbase.HttpResultBeanBase;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

//发布页面
public class PublicShareMsgAct extends MyBaseBitmapAct {
	private TextView text;
	private ViewGroup liRoot = null;
	private EditText edit;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTextNav(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
			}
		},"发布信息","发送");
		this.addViewFillInRoot(R.layout.act_pubmsg);
		liRoot = (ViewGroup) this.findViewById(R.id.liLayout);
		liRoot.addView(InitBitmapView());
		edit = (EditText) this.findViewById(R.id.edit);
	}
	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj, Object requestObj) {
		// TODO Auto-generated method stub
		
	}
}
