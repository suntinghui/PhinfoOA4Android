package cn.com.phinfo.oaact;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import cn.com.phinfo.entity.DataInstance;
import cn.com.phinfo.protocol.CreateCalendarRun;
import cn.com.phinfo.protocol.EventDetailDelRun;
import cn.com.phinfo.protocol.EventGetDetailRun;
import cn.com.phinfo.protocol.EventGetDetailRun.EventGetDetailItem;
import cn.com.phinfo.protocol.EventGetDetailRun.EventGetDetailResultBean;
import cn.com.phinfo.protocol.UnitandaddressRun.UnitandaddressItem;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.utils.ParamsCheckUtils;
import com.heqifuhou.view.ConfirmDialog;
import com.heqifuhou.view.ConfirmDialog.OnDialogOKListener;

public class CalendarDetailAct extends CreateCalendarAct implements OnClickListener {
	private static final int  ID_GET_DETAIL = 0x30,ID_CANCEL=0x31;
	private String id;
	private boolean bEdit = true;;
	private TextView navBtn;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.id = this.getIntent().getExtras().getString("ID");
		this.addViewFillInRoot(R.layout.act_create_calendar);
		this.addTitleView(R.layout.nav_create_calendar_btn);
		this.addBottomView(R.layout.calendar_detail_tools_bar);
		navBack = (TextView) this.findViewById(R.id.nav_back);
		navBtn = (TextView) this.findViewById(R.id.nav_btn);
		navBtn.setVisibility(View.INVISIBLE);
		navBack.setOnClickListener(onBackListener);
		navBtn.setText("编辑");
		this.findViewById(R.id.nav_btn).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						bEdit = !bEdit;
						if(!bEdit){
							setClickEnable(bEdit);
							navBtn.setText("提交");
						}else{
							submit();
						}
					}
				});
		TextView nav_title = (TextView) this.findViewById(R.id.nav_title);
		nav_title.setText("事件详细资料");
		setClickEnable(false);
		this.findViewById(R.id.delBtn).setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View arg0) {
				//删除
				delDailog();
			}
		});
		onRefresh();
	}
	private ConfirmDialog delConfirm;
	private void delDailog(){
		if (delConfirm != null && delConfirm.isShowing()) {
			return;
		}
		delConfirm = new ConfirmDialog(this, "确定要删除吗？", null);
		delConfirm.show();
		delConfirm.setOnDialogOKListener(new OnDialogOKListener() {
			@Override
			public void onOKItem(Object obj) {
				quickHttpRequest(ID_CANCEL, new EventDetailDelRun(id));
			}
		});
	}
	
	private void setClickEnable(boolean clickable){
		this.findViewById(R.id.start).setClickable(clickable);
		this.findViewById(R.id.end).setClickable(clickable);
		this.findViewById(R.id.repeat).setClickable(clickable);
		this.findViewById(R.id.displayStatus).setClickable(clickable);
		this.findViewById(R.id.reminderTime).setClickable(clickable);
		this.findViewById(R.id.calendarType).setClickable(clickable);
		this.findViewById(R.id.invtee).setClickable(clickable);
		allday.setClickable(clickable);
	}

	private void submit(){
		final String subject = title.getText().toString().trim();
		final String ScheduledStart = startTxt.getText().toString().trim();
		final String ScheduledEnd = endTxt.getText().toString().trim();
		final String isAllDayEvent = bChecked?"1":"0";
		final String isRecurrence =repeatTxt.getTag()==null?"":repeatTxt.getTag().toString();
		final String location = Location.getText().toString().trim();
		final String _descripiton  = descripiton.getText().toString().trim();
		final String calendarType = calendarTypeTxt.getText().toString();
		final String displayStatus = displayStatusTxt.getText().toString();
		final String reminderTime = reminderTimeTxt.getTag()==null?"":reminderTimeTxt.getTag().toString();
		final String invtee = invteeTxt.getTag()==null?"":invteeTxt.getTag().toString();
		if(ParamsCheckUtils.isNull(subject)){
			showToast("标题不能为空");
			return;
		}
		if(ParamsCheckUtils.isNull(location)){
			showToast("位置不能为空");
			return;
		}
		if(ParamsCheckUtils.isNull(calendarType)){
			showToast("日历类型不能为空");
			return;
		}
		if(ParamsCheckUtils.isNull(displayStatus)){
			showToast("状态不能为空");
			return;
		}
		this.quickHttpRequest(ID_SUBMIT, new CreateCalendarRun(subject, ScheduledStart, ScheduledEnd, isAllDayEvent, isRecurrence, location, _descripiton, calendarType, displayStatus, reminderTime, invtee));
	}

	protected void onRefresh() {
		this.quickHttpRequest(ID_GET_DETAIL, new EventGetDetailRun(id));
	}

	@Override
	protected void onHttpResult(int index, HttpResultBeanBase obj, Object requestObj) {
		if(ID_GET_DETAIL == index){
			if(obj.isOK()){
				EventGetDetailResultBean o = (EventGetDetailResultBean)obj;
				Log.e("yao3", JSON.toJSONString(o));
				showInfo(o.getData());
			}
			return;
		}
		//删除事件
		if(ID_CANCEL== index){
			if(obj.isOK()){
				Intent i = new Intent(IBroadcastAction.ACTION_DEL_CALENDAR);
				i.putExtra("ID", id);
				sendBroadcast(i);
				showToast("删除成功");
				this.finish();
			}else{
				showToast(obj.getMsg());
			}
			return;
		}
	}

	private void showInfo(EventGetDetailItem it){
		if(it==null){
			return;
		}
		title.setText(it.getSubject());
		Location.setText(it.getLocation());
		descripiton.setText(it.getDescription());

		repeatTxt.setTag(it.getRecurrenceType());
		if("true".equals(it.getIsalldayevent().toLowerCase())){
			allday.setNowChoose(true);
		}else{
			allday.setNowChoose(false);
		}
		
		startTxt.setText(it.getScheduledstart());
		endTxt.setText(it.getScheduledend());
		
		//不重复
		if("0".equals(it.getRecurrenceType2())){
			repeatTxt.setText("默认无");
		}
		//每天
		else if("1".equals(it.getRecurrenceType2())){
			repeatTxt.setText("每天");
		}
		//每周
		else if("2".equals(it.getRecurrenceType2())){
			repeatTxt.setText("每周");
		}
		//每月
		else if("3".equals(it.getRecurrenceType2())){
			repeatTxt.setText("每月");
		}
		
		if("0".equals(it.getDisplaystatus())){
			displayStatusTxt.setText("正忙");
		}else{
			displayStatusTxt.setText("空闲");
		}
		displayStatusTxt.setTag(displayStatusTxt.getText().toString().trim());
		
		reminderTimeTxt.setTag(it.getReminderTime());
		if("5".equals(it.getReminderTime())){
			reminderTimeTxt.setText("5分钟前");
		}else if("15".equals(it.getReminderTime())){
			reminderTimeTxt.setText("15分钟");
		}else if("30".equals(it.getReminderTime())){
			reminderTimeTxt.setText("30分钟");
		}else if("60".equals(it.getReminderTime())){
			reminderTimeTxt.setText("1小时前");
		}else if("120".equals(it.getReminderTime())){
			reminderTimeTxt.setText("2小时前");
		}else if("1440".equals(it.getReminderTime())){
			reminderTimeTxt.setText("1天前");
		}
		
		calendarTypeTxt.setTag(it.getCalendardType());
		calendarTypeTxt.setText(it.getCalendardType());
		
		String showName = "";
		String tag = "";
		for(int i=0;i<it.getInvtee().size();i++){
			UnitandaddressItem iv = it.getInvtee().get(i);
			if (i > 0) {
				showName += ",";
				tag += ",";
			}
			showName += iv.GET_USER_NAME();
			tag += iv.getSystemUserId();
		}

		DataInstance.getInstance().addUnitandaddressItem(it.getInvtee());
		invteeTxt.setText(showName);
		invteeTxt.setTag(tag);	
	}
}
