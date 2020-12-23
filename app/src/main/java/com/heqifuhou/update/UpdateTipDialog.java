package com.heqifuhou.update;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import cn.com.phinfo.oaact.R;

import com.heqifuhou.view.MyDialog;

// 改为通用
public class UpdateTipDialog extends MyDialog {
	private OnUpdateDialogButtonClickListener listener;
	private OnBackKey onCancelListener = null;
	private TextView ignoreBtn, rightBtn;
	private TextView txtContext;
	private boolean bIsforce;
	private String des;
	public UpdateTipDialog(Activity act, boolean bIsforce,String des) {
		super(act);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.bIsforce = bIsforce;
		this.des = des;
		this.des = des.replace("\\r\\n","\r\n");
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.update_tip_dialog);
		((TextView) findViewById(R.id.title)).setText("更新提醒");
		txtContext = ((TextView) findViewById(R.id.content));
		ignoreBtn = (TextView) findViewById(R.id.edit_ignore_btn);
		rightBtn = (TextView) findViewById(R.id.edit_save_btn);
		rightBtn.setText("下载");
		txtContext.setText(des);
		setCanceledOnTouchOutside(false);
		this.setVisible(View.VISIBLE, View.VISIBLE);
		if (bIsforce) {
			ignoreBtn.setText("取消");
		} else {
			ignoreBtn.setText("以后");
		}
		ignoreBtn.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
				if(!bIsforce){
					listener.onIgnoreBtnClick();
				}else{
					listener.onCancelBtnClick();
				}
				
			}
		});
		rightBtn.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
				listener.onDownloadBtnClick();
			}
		});
	}

	public void setOnClickListener(OnUpdateDialogButtonClickListener listener) {
		this.listener = listener;
	}
	public void setOnCancelWhenForce(OnBackKey onBackKey){
		this.onCancelListener = onBackKey;
	}
	private void setVisible(int ignore, int right) {
		ignoreBtn.setVisibility(ignore);
		rightBtn.setVisibility(right);
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(bIsforce){
				if(this.onCancelListener!=null){
					if(this.onCancelListener.onBackKey()){
						return true;
					}
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	public interface OnBackKey{
		boolean onBackKey();
	}

	public interface OnUpdateDialogButtonClickListener {
		void onIgnoreBtnClick();
		void onCancelBtnClick();
		void onDownloadBtnClick();
	}
}
