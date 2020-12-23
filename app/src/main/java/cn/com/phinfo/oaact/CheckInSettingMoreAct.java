package cn.com.phinfo.oaact;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import cn.com.phinfo.protocol.AttentdsettingsRun.GlobalSettingsItem;
import cn.com.phinfo.protocol.HR_AttendsettingsRun;
import cn.com.phinfo.protocol.HR_AttendsettingsRun.AttendsettingsResultBean;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.view.PopupDialog;
import com.heqifuhou.view.PopupDialog.OnDialogItemListener;
import com.heqifuhou.view.SlidButton;

//高级设置
public class CheckInSettingMoreAct extends HttpMyActBase implements  OnClickListener {
	private TextView FlexTime,LateTime,AbsenceTime,RemindCheckIn,RemindCheckOut,AdvancedCheckIn;
	private SlidButton RemindLeaveWorkCheck,NeedPhoto,FastCheckIn;
	private PopupDialog popDialog;
	private GlobalSettingsItem globalSettingsItem;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String s = this.getIntent().getExtras().getString("GlobalSettingsItem");
		globalSettingsItem = JSON.parseObject(s, GlobalSettingsItem.class);
		this.addTextNav("高级设置");
		this.addViewFillInRoot(R.layout.act_checkin_more_setting);
		this.findViewById(R.id.FlexTimeBtn).setOnClickListener(this);
		this.findViewById(R.id.LateTimeBtn).setOnClickListener(this);
		this.findViewById(R.id.AbsenceTimeBtn).setOnClickListener(this);
		this.findViewById(R.id.RemindCheckInBtn).setOnClickListener(this);
		this.findViewById(R.id.RemindCheckOutBtn).setOnClickListener(this);
		this.findViewById(R.id.AdvancedCheckInBtn).setOnClickListener(this);
		this.findViewById(R.id.ok).setOnClickListener(this);
		
		
		FlexTime = (TextView) this.findViewById(R.id.FlexTime);
		LateTime = (TextView) this.findViewById(R.id.LateTime);
		AbsenceTime = (TextView) this.findViewById(R.id.AbsenceTime);
		RemindCheckIn = (TextView) this.findViewById(R.id.RemindCheckIn);
		RemindCheckOut = (TextView) this.findViewById(R.id.RemindCheckOut);
		AdvancedCheckIn = (TextView) this.findViewById(R.id.AdvancedCheckIn);
		
		RemindLeaveWorkCheck = (SlidButton) this.findViewById(R.id.RemindLeaveWorkCheck);
		NeedPhoto = (SlidButton) this.findViewById(R.id.NeedPhoto);
		FastCheckIn = (SlidButton) this.findViewById(R.id.FastCheckIn);
		
		//初始化数据
		FlexTime.setText(globalSettingsItem.getFlexTime()+"分钟");
		LateTime.setText(globalSettingsItem.getLateTime()+"分钟");
		AbsenceTime.setText(globalSettingsItem.getAbsenceTime()+"分钟");
		RemindCheckIn.setText(globalSettingsItem.getRemindCheckIn()+"分钟");
		RemindCheckOut.setText(globalSettingsItem.getRemindCheckOut()+"分钟");
		AdvancedCheckIn.setText(globalSettingsItem.getAdvancedCheckIn()+"分钟");
		
		RemindLeaveWorkCheck.setNowChoose(globalSettingsItem.getRemindLeaveWorkCheck());
		NeedPhoto.setNowChoose(globalSettingsItem.getNeedPhoto());
		FastCheckIn.setNowChoose(globalSettingsItem.getFastCheckIn());
	
	}
	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()){
		case R.id.FlexTimeBtn:
			if(showdialog()){
				popDialog.setOnDialogItemListener(new OnDialogItemListener() {
					@Override
					public void onDialogItem(int pos, Object obj) {
						String[] ItemList = (String[])obj;
						FlexTime.setText(ItemList[pos]);
						globalSettingsItem.setFlexTime(String.valueOf(pos));
					}
				});
			}
			break;
		case R.id.LateTimeBtn:
			if(showdialog()){
				popDialog.setOnDialogItemListener(new OnDialogItemListener() {
					@Override
					public void onDialogItem(int pos, Object obj) {
						String[] ItemList = (String[])obj;
						LateTime.setText(ItemList[pos]);
						globalSettingsItem.setLateTime(String.valueOf(pos));
					}
				});
			}
			break;
		case R.id.AbsenceTimeBtn:
			if(showdialog()){
				popDialog.setOnDialogItemListener(new OnDialogItemListener() {
					@Override
					public void onDialogItem(int pos, Object obj) {
						String[] ItemList = (String[])obj;
						AbsenceTime.setText(ItemList[pos]);
						globalSettingsItem.setAbsenceTime(String.valueOf(pos));
					}
				});
			}
			break;
		case R.id.RemindCheckInBtn:
			if(showdialog()){
				popDialog.setOnDialogItemListener(new OnDialogItemListener() {
					@Override
					public void onDialogItem(int pos, Object obj) {
						String[] ItemList = (String[])obj;
						RemindCheckIn.setText(ItemList[pos]);
						globalSettingsItem.setRemindCheckIn(String.valueOf(pos));
					}
				});
			}
			break;
		case R.id.RemindCheckOutBtn:
			if(showdialog()){
				popDialog.setOnDialogItemListener(new OnDialogItemListener() {
					@Override
					public void onDialogItem(int pos, Object obj) {
						String[] ItemList = (String[])obj;
						RemindCheckOut.setText(ItemList[pos]);
						globalSettingsItem.setRemindCheckOut(String.valueOf(pos));
					}
				});
			}
			break;
		case R.id.AdvancedCheckInBtn:
			if(showAdvancedCheckInDialog()){
				popDialog.setOnDialogItemListener(new OnDialogItemListener() {
					@Override
					public void onDialogItem(int pos, Object obj) {
						String[] ItemList = (String[])obj;
						AdvancedCheckIn.setText(ItemList[pos]);
						String[] array = new String[]{"10","20","30","40","50","60","70","180"};
						globalSettingsItem.setAdvancedCheckIn(array[pos]);
					}
				});
			}
			break;
		case R.id.ok:
			globalSettingsItem.setFastCheckIn(FastCheckIn.getNowChoose());
			globalSettingsItem.setRemindLeaveWorkCheck(RemindLeaveWorkCheck.getNowChoose());
			globalSettingsItem.setNeedPhoto(NeedPhoto.getNowChoose());
			this.quickHttpRequest(0x10,new HR_AttendsettingsRun(globalSettingsItem));
			break;
		}
	}
	//显示0-180分钟的
	private boolean showdialog(){
		if(popDialog!=null&&popDialog.isShowing()){
			return false;
		}
		String[] ItemList = new String[181];
		ItemList[0] ="关闭";
		for(int i=1;i<=180;i++){
			ItemList[i]=String.valueOf(i)+"分钟";
		}
		popDialog = new PopupDialog(this, ItemList, ItemList);
		popDialog.show();
		return true;
	}
	private boolean showAdvancedCheckInDialog(){
		if(popDialog!=null&&popDialog.isShowing()){
			return false;
		}
		String[] ItemList = new String[9];
		ItemList[0] ="关闭";
		ItemList[1] = "10分钟";
		ItemList[2] = "20分钟";
		ItemList[3] = "30分钟";
		ItemList[4] = "40分钟";
		ItemList[5] = "50分钟";
		ItemList[6] = "60分钟";
		ItemList[7] = "70分钟";
		ItemList[8] = "180分钟";
		popDialog = new PopupDialog(this, ItemList, ItemList);
		popDialog.show();
		return true;
	}
	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if(obj.isOK()){
			AttendsettingsResultBean o = (AttendsettingsResultBean)obj;
			showToast("提交成功");
			sendBroadcast(new Intent(IBroadcastAction.ACTION_SETTING_CHECKING));
			this.finish();
		}else{
			showToast(obj.getMsg());
		}
		
	}
	
}
