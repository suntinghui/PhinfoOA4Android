package cn.com.phinfo.oaact;


import android.content.Context;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.phinfo.protocol.AttacheFileRun.AttacheFileItem;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.utils.ParamsCheckUtils;

public class ShareFileAct extends HttpMyActBase implements OnClickListener{
	private TextView text,title;
	private ImageView img;
	private AttacheFileItem item;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String s = this.getIntent().getExtras().getString("AttacheFileItem");
		if (!ParamsCheckUtils.isNull(s)) {
			item = JSON.parseObject(s, AttacheFileItem.class);
		}
		this.addTextNav("链接分享");
		this.addViewFillInRoot(R.layout.act_fileshare);
		title  = (TextView) this.findViewById(R.id.title);
		text = (TextView) this.findViewById(R.id.text);
		img = (ImageView) this.findViewById(R.id.img);
		img.setImageResource(item.getImgResId());
		title.setText(item.getName());
		text.setText(item.getLink());
		this.findViewById(R.id.copyBtn).setOnClickListener(this);
		this.findViewById(R.id.shareBtn).setOnClickListener(this);
	}
	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()){
		case R.id.copyBtn:
			copyBtnAction();
			break;
		case R.id.shareBtn:
			break;
		}
	}
	private void copyBtnAction(){
		  // 从API11开始android推荐使用android.content.ClipboardManager
        // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(item.getLink());
        this.showToast("复制成功，可以发给朋友们了。");
	}
}
