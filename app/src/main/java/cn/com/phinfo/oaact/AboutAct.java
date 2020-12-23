package cn.com.phinfo.oaact;


import android.os.Bundle;
import android.widget.TextView;

import com.heqifuhou.actbase.MyActBase;
import com.heqifuhou.utils.MyDeviceBaseUtils;

//关于页面
public class AboutAct extends MyActBase {
	private TextView text;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTextNav("关于");
		this.addViewFillInRoot(R.layout.act_about);
		text = (TextView) this.findViewById(R.id.text);
		text.setText("当前版本号:"+MyDeviceBaseUtils.getCurrAppVer(this));
	}
}
