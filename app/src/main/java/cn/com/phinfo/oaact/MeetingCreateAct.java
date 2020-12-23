package cn.com.phinfo.oaact;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.TextView;
import cn.com.phinfo.adapter.SelectPersonGridAdapter;
import cn.com.phinfo.entity.DataInstance;
import cn.com.phinfo.entity.SelectItem;
import cn.com.phinfo.protocol.MeetingCreateEditRun;
import cn.com.phinfo.protocol.MeetingCreateEditRun.MeetingCreateEditResultBean;
import cn.com.phinfo.protocol.RoomAppointRun.RoomAppointItem;
import cn.com.phinfo.protocol.RoomListRun.RoomListItem;
import cn.com.phinfo.protocol.UnitandaddressRun.UnitandaddressItem;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.utils.ParamsCheckUtils;
import com.heqifuhou.view.MyDateTimePick;
import com.heqifuhou.view.MyDateTimePick.OnDateTimePickListener;
import com.heqifuhou.view.MyGridView;

public class MeetingCreateAct extends HttpMyActBase implements OnClickListener,
		OnItemClickListener {
	private static int ID_SUBMIT = 0x10, ID_SEL_PERSON = 0x12,
			ID_ReminderTimePick = 0x13, ID_MeetingRoomSelect = 0x14,
			ID_LBSAddressSelect = 0x15;
	protected TextView startTxt, endTxt, inTxt, outTxt, approverTxt,
			reminderTimeTxt;
	protected EditText subject, descripition;
	protected MyGridView approverGrid;
	protected SelectPersonGridAdapter selectPersonAdapter = null;
	protected String id = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTitleView(R.layout.nav_f5f5f5_btn);
		this.addTextNav("创建会议室");
		this.addViewFillInRoot(R.layout.act_create_metting);
		this.startTxt = (TextView) this.findViewById(R.id.startTxt);
		this.endTxt = (TextView) this.findViewById(R.id.endTxt);
		this.inTxt = (TextView) this.findViewById(R.id.inTxt);
		this.outTxt = (TextView) this.findViewById(R.id.outTxt);
		this.reminderTimeTxt = (TextView) this
				.findViewById(R.id.reminderTimeTxt);
		this.approverTxt = (TextView) this.findViewById(R.id.approverTxt);
		this.subject = (EditText) this.findViewById(R.id.subject);
		this.descripition = (EditText) this.findViewById(R.id.descripition);

		this.approverGrid = (MyGridView) this.findViewById(R.id.approverGrid);
		this.findViewById(R.id.ScheduledStart).setOnClickListener(this);
		this.findViewById(R.id.ScheduledEnd).setOnClickListener(this);
		this.findViewById(R.id.inMetting).setOnClickListener(this);
		this.findViewById(R.id.outMeeting).setOnClickListener(this);
		this.findViewById(R.id.submit_btn).setOnClickListener(this);
		this.findViewById(R.id.reminderTime).setOnClickListener(this);

		this.selectPersonAdapter = new SelectPersonGridAdapter();
		this.approverGrid.setAdapter(selectPersonAdapter);
		this.approverGrid.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.ScheduledStart:
			startDateTimePick();
			break;
		case R.id.ScheduledEnd:
			endDateTimePick();
			break;
		case R.id.inMetting:
			startMeetingRoomSelectAct();
			break;
		case R.id.outMeeting:
			startLBSAddressSelectAct();
			break;
		case R.id.submit_btn:
			submit();
			break;
		case R.id.reminderTime:
			startReminderTimePick();
			break;
		}
	}

	private void startLBSAddressSelectAct() {
		Intent intent = new Intent(this, LBSAddressSelectAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivityForResult(intent, ID_LBSAddressSelect);
	}

	private void startMeetingRoomSelectAct() {
		Intent intent = new Intent(this, MeetingRoomSelectAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivityForResult(intent, ID_MeetingRoomSelect);
	}

	private void startReminderTimePick() {
		Intent intent = new Intent(this, SelectAct.class);
		intent.putExtra("TITLE", "提醒时间");
		intent.putExtra("DATA", "5分钟前,15分钟,30分钟,1小时前,2小时前,1天前");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivityForResult(intent, ID_ReminderTimePick);
	}

	protected void submit() {
		String _RoomId = inTxt.getTag().toString().trim();
		String _start = this.startTxt.getText().toString().trim();
		String _end = this.endTxt.getText().toString().trim();
		String _subject = subject.getText().toString().trim();
		String _descripition = descripition.getText().toString().trim();
		String _reminderTimeTxt = this.reminderTimeTxt.getTag().toString().trim();
		String _Location = outTxt.getText().toString().trim();
		
		if (ParamsCheckUtils.isNull(_subject)) {
			showToast("会议主题不能为空");
			return;
		}
		if (ParamsCheckUtils.isNull(_descripition)) {
			showToast("会议内容不能为空");
			return;
		}
		if (ParamsCheckUtils.isNull(_start)) {
			showToast("占用开始时间不能为空");
			return;
		}
		if (ParamsCheckUtils.isNull(_end)) {
			showToast("占用结束时间不能为空");
			return;
		}
		String sTxt = (String) this.startTxt.getTag();
		long s = Long.parseLong(sTxt);
		long e = Long.parseLong((String) this.endTxt.getTag());
		if (e < s) {
			showToast("开始时间不能小于结束时间");
			return;
		}
		String invtee = toMenberList();
		this.quickHttpRequest(ID_SUBMIT, new MeetingCreateEditRun(_RoomId,_start,_end,_subject,_descripition,_reminderTimeTxt,_Location,invtee));
	}

	protected void onRefresh() {

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
								nResultYear, nResultMonthOfYear + 1,
								nResultDayOfMonth, nResultHourOfDay,
								nResultMinute);
						startTxt.setText(s);
						startTxt.setTag(String.format("%04d%02d%02d%02d%02d",
								nResultYear, nResultMonthOfYear + 1,
								nResultDayOfMonth, nResultHourOfDay,
								nResultMinute));
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
								nResultYear, nResultMonthOfYear + 1,
								nResultDayOfMonth, nResultHourOfDay,
								nResultMinute);
						endTxt.setText(s);
						endTxt.setTag(String.format("%04d%02d%02d%02d%02d",
								nResultYear, nResultMonthOfYear + 1,
								nResultDayOfMonth, nResultHourOfDay,
								nResultMinute));
					}
				});
		endDateTimePick.show();
	}

	public void onHttpForResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		super.onHttpForResult(id, obj, requestObj);

	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if (ID_SUBMIT == id) {
			if (obj.isOK()) {
				showToast("提交成功");
				sendBroadcast(new Intent(IBroadcastAction.ACTION_CREATE_MEETING));
				this.finish();
			} else {
				this.showToast(obj.getMsg());
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (arg0.getAdapter() == selectPersonAdapter) {
			if (selectPersonAdapter.isImgShow(arg2)) {
				// 选人
				startSelectPersionAct();
			} else {

			}
		}
	}

	private void startSelectPersionAct() {
		Intent intent = new Intent(this, SelectPersonAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intent, ID_SEL_PERSON);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (ID_SEL_PERSON == requestCode) {
			List<UnitandaddressItem> ls = DataInstance.getInstance()
					.getUnitandaddressItemList();
			selectPersonAdapter.replaceListRef(ls);
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

		if (ID_MeetingRoomSelect == requestCode && resultCode == RESULT_OK) {
			RoomListItem it = (RoomListItem) data.getExtras().getSerializable(
					"RoomListItem");
			inTxt.setText(it.getName());
			inTxt.setTag(it.getOrganizationId());
			return;
		}
		if (requestCode == ID_LBSAddressSelect && resultCode == RESULT_OK) {
			outTxt.setText(data.getExtras().getString("Bulding"));
//			data.getExtras().getString("ADDRESS")
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	protected void onDestroy() {
		super.onDestroy();
		DataInstance.getInstance().getUnitandaddressItemList().clear();
	}

	private String toMenberList() {
		StringBuilder sb = new StringBuilder();
		List<UnitandaddressItem> ls = DataInstance.getInstance()
				.getUnitandaddressItemList();
		for (int i = 0; i < ls.size(); i++) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(ls.get(i).getSystemUserId());
		}
		return sb.toString();
	}
}
