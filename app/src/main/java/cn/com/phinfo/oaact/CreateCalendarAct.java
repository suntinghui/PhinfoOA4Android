package cn.com.phinfo.oaact;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import cn.com.phinfo.entity.DataInstance;
import cn.com.phinfo.entity.SelectItem;
import cn.com.phinfo.protocol.ActivityEventGetlistRun.ActivityEventGetlistItem;
import cn.com.phinfo.protocol.CreateCalendarRun;
import cn.com.phinfo.protocol.CreateCalendarRun.CreateCalendarResultBean;
import cn.com.phinfo.protocol.UnitandaddressRun.UnitandaddressItem;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.utils.ParamsCheckUtils;
import com.heqifuhou.view.MyDateTimePick;
import com.heqifuhou.view.MyDateTimePick.OnDateTimePickListener;
import com.heqifuhou.view.SlidButton;
import com.heqifuhou.view.SlidButton.OnChangedListener;

public class CreateCalendarAct extends HttpMyActBase implements OnClickListener {
	protected static int ID_SUBMIT = 0x10, ID_RepeatSelectPick = 0x11,
			ID_DisplayPick = 0x12, ID_ReminderTimePick = 0x13,
			ID_CalendarTypePick = 0x14, ID_SEL_PERSON = 0x15;
	protected TextView navBack;
	protected EditText title, Location, descripiton;
	protected SlidButton allday;
	protected TextView startTxt, endTxt, repeatTxt, displayStatusTxt,
			reminderTimeTxt, calendarTypeTxt, invteeTxt;
	protected boolean bChecked = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addViewFillInRoot(R.layout.act_create_calendar);
		this.addTitleView(R.layout.nav_create_calendar_btn);
		navBack = (TextView) this.findViewById(R.id.nav_back);
		navBack.setOnClickListener(onBackListener);
		this.findViewById(R.id.nav_btn).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						submit();
					}
				});
		title = (EditText) this.findViewById(R.id.title);
		Location = (EditText) this.findViewById(R.id.Location);
		descripiton = (EditText) this.findViewById(R.id.descripiton);
		allday = (SlidButton) this.findViewById(R.id.allday);
		startTxt = (TextView) this.findViewById(R.id.startTxt);
		endTxt = (TextView) this.findViewById(R.id.endTxt);
		repeatTxt = (TextView) this.findViewById(R.id.repeatTxt);
		displayStatusTxt = (TextView) this.findViewById(R.id.displayStatusTxt);
		reminderTimeTxt = (TextView) this.findViewById(R.id.reminderTimeTxt);
		calendarTypeTxt = (TextView) this.findViewById(R.id.calendarTypeTxt);
		invteeTxt = (TextView) this.findViewById(R.id.invteeTxt);
		this.findViewById(R.id.start).setOnClickListener(this);
		this.findViewById(R.id.end).setOnClickListener(this);
		this.findViewById(R.id.repeat).setOnClickListener(this);
		this.findViewById(R.id.displayStatus).setOnClickListener(this);
		this.findViewById(R.id.reminderTime).setOnClickListener(this);
		this.findViewById(R.id.calendarType).setOnClickListener(this);
		this.findViewById(R.id.invtee).setOnClickListener(this);
		allday.setNowChoose(false);
		allday.setOnChangedListener(new OnChangedListener() {
			@Override
			public void OnChanged(View v, boolean bChecked) {
				CreateCalendarAct.this.bChecked = bChecked;
			}
		});
	}

	private void submit(){
		final String subject = title.getText().toString().trim();
		final String ScheduledStart = startTxt.getText().toString().trim();
		final String ScheduledEnd = endTxt.getText().toString().trim();
		final String isAllDayEvent = bChecked?"1":"0";
		final String recurrenceType =repeatTxt.getTag()==null?"":repeatTxt.getTag().toString();
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
		this.quickHttpRequest(ID_SUBMIT, new CreateCalendarRun(subject, ScheduledStart, ScheduledEnd, isAllDayEvent, recurrenceType, location, _descripiton, calendarType, displayStatus, reminderTime, invtee));
	}

	protected void onRefresh() {

	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if (ID_SUBMIT == id) {
			if(obj.isOK()){
				showToast("提交成功");
				CreateCalendarResultBean o = (CreateCalendarResultBean)obj;
				ActivityEventGetlistItem it = o.getActivityEventGetlistItem();
				Intent i = new Intent(IBroadcastAction.ACTION_CREATE_CALENDAR);
				i.putExtra("ActivityEventGetlistItem", JSON.toJSONString(it));
				sendBroadcast(i);
				this.finish();
				return;
			}
			showToast(obj.getMsg());
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.start:
			startDateTimePick();
			break;
		case R.id.end:
			endDateTimePick();
			break;
		case R.id.repeat:
			startRepeatSelectPick();
			break;
		case R.id.displayStatus:
			startDisplayStatusPick();
			break;
		case R.id.reminderTime:
			startReminderTimePick();
			break;
		case R.id.calendarType:
			startCalendarTypePick();
			break;
		case R.id.invtee:
			startSelectPersionAct();
			break;
		}
	}

	private void startSelectPersionAct() {
		Intent intent = new Intent(this, SelectPersonAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intent, ID_SEL_PERSON);
	}

	private void startRepeatSelectPick() {
		Intent intent = new Intent(this, SelectAct.class);
		intent.putExtra("TITLE", "重复类型");
		intent.putExtra("DATA", "默认无,每天,每周,每月");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivityForResult(intent, ID_RepeatSelectPick);
	}

	private void startCalendarTypePick() {
		Intent intent = new Intent(this, SelectAct.class);
		intent.putExtra("TITLE", "日历类型");
		intent.putExtra("DATA", "工作,个人");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivityForResult(intent, ID_CalendarTypePick);
	}

	private void startDisplayStatusPick() {
		Intent intent = new Intent(this, SelectAct.class);
		intent.putExtra("TITLE", "状态类型");
		intent.putExtra("DATA", "正忙,空闲");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivityForResult(intent, ID_DisplayPick);
	}

	private void startReminderTimePick() {
		Intent intent = new Intent(this, SelectAct.class);
		intent.putExtra("TITLE", "提醒时间");
		intent.putExtra("DATA", "5分钟前,15分钟,30分钟,1小时前,2小时前,1天前");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivityForResult(intent, ID_ReminderTimePick);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (ID_RepeatSelectPick == requestCode && resultCode == RESULT_OK) {
			SelectItem it = (SelectItem) data.getExtras().getSerializable(
					"SelectItem");
			repeatTxt.setText(it.getText());
			repeatTxt.setTag(it.getId());
			return;
		}
		if (ID_DisplayPick == requestCode && resultCode == RESULT_OK) {
			SelectItem it = (SelectItem) data.getExtras().getSerializable(
					"SelectItem");
			displayStatusTxt.setText(it.getText());
			displayStatusTxt.setTag(it.getText());
			return;
		}
		if (ID_ReminderTimePick == requestCode && resultCode == RESULT_OK) {
			SelectItem it = (SelectItem) data.getExtras().getSerializable(
					"SelectItem");
			reminderTimeTxt.setText(it.getText());
			switch (it.getId()) {
			case 0:
				reminderTimeTxt.setTag(5);
				break;
			case 1:
				reminderTimeTxt.setTag(15);
				break;
			case 2:
				reminderTimeTxt.setTag(30);
				break;
			case 3:
				reminderTimeTxt.setTag(60);
				break;
			case 4:
				reminderTimeTxt.setTag(120);
				break;
			case 5:
				reminderTimeTxt.setTag(1440);
				break;
			}
			return;
		}
		if (ID_CalendarTypePick == requestCode && resultCode == RESULT_OK) {
			SelectItem it = (SelectItem) data.getExtras().getSerializable(
					"SelectItem");
			calendarTypeTxt.setText(it.getText());
			calendarTypeTxt.setTag(it.getText());
			return;
		}
		if (ID_SEL_PERSON == requestCode) {
			List<UnitandaddressItem> tags = DataInstance.getInstance()
					.getUnitandaddressItemList();
			if (tags.isEmpty()) {
				return;
			}
			String showName = "";
			String tag = "";
			for (int i = 0; i < tags.size(); i++) {
				UnitandaddressItem it = tags.get(i);
				if (i > 0) {
					showName += ",";
					tag += ",";
				}
				showName += it.GET_USER_NAME();
				tag += it.getSystemUserId();
			}
			invteeTxt.setText(showName);
			invteeTxt.setTag(tag);
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private MyDateTimePick startDateTimePick;

	private void startDateTimePick() {
		if (startDateTimePick != null && startDateTimePick.isShowing()) {
			return;
		}
		startDateTimePick = new MyDateTimePick(this,
				new OnDateTimePickListener() {
					@Override
					public void onDateTimePick(int nResultYear,
							int nResultMonthOfYear, int nResultDayOfMonth,
							int nResultHourOfDay, int nResultMinute) {
						String s = String.format("%04d-%02d-%02d %02d:%02d:00",
								nResultYear, nResultMonthOfYear+1,
								nResultDayOfMonth, nResultHourOfDay,
								nResultMinute);
						startTxt.setText(s);
					}
				});
		startDateTimePick.show();
	}

	private MyDateTimePick endDateTimePick;

	private void endDateTimePick() {
		if (endDateTimePick != null && endDateTimePick.isShowing()) {
			return;
		}
		endDateTimePick = new MyDateTimePick(this,
				new OnDateTimePickListener() {
					@Override
					public void onDateTimePick(int nResultYear,
							int nResultMonthOfYear, int nResultDayOfMonth,
							int nResultHourOfDay, int nResultMinute) {
						String s = String.format("%04d-%02d-%02d %02d:%02d:00",
								nResultYear, nResultMonthOfYear+1,
								nResultDayOfMonth, nResultHourOfDay,
								nResultMinute);
						endTxt.setText(s);
					}
				});
		endDateTimePick.show();
	}

	public void onDestroy() {
		super.onDestroy();
		DataInstance.getInstance().getUnitandaddressItemList().clear();
	}
}
