package com.heqifuhou.update;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.com.phinfo.oaact.R;

// 改为通用
public class UpdateProgressDialog extends Dialog {
	private TextView update_progress_btn,txtContext;
	private ProgressBar progress;
	private String des="";
	public UpdateProgressDialog(Context context,String des) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.des = des;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.update_progress_dialog);
		((TextView) findViewById(R.id.title)).setText("下载更新包");
		progress = ((ProgressBar) findViewById(R.id.progress));
		txtContext =  (TextView) findViewById(R.id.content);
		update_progress_btn =  (TextView) findViewById(R.id.update_progress_btn);
		txtContext.setText(des);
		setCancelable(false);
		setCanceledOnTouchOutside(false);
	}

	public void setOnClickListener(final String txt,android.view.View.OnClickListener listener) {
		update_progress_btn.setText(txt);
		update_progress_btn.setOnClickListener(listener);
	}
	public void setProgress(int n){
		progress.setProgress(n);
	}
	
	public void setTextContext(final String des){
		txtContext.setText(des);
	}
}
