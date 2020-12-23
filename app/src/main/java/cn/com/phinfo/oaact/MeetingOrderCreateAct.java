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
import cn.com.phinfo.adapter.MeetingPickAdapter;
import cn.com.phinfo.adapter.SelectPersonGridAdapter;
import cn.com.phinfo.entity.DataInstance;
import cn.com.phinfo.protocol.RoomAppointRun.RoomAppointItem;
import cn.com.phinfo.protocol.RoomListRun.RoomListItem;
import cn.com.phinfo.protocol.RoomRoderCreateEditRun;
import cn.com.phinfo.protocol.RoomRoderCreateEditRun.RoomRoderCreateEditResultBean;
import cn.com.phinfo.protocol.UnitandaddressRun.UnitandaddressItem;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.utils.ParamsCheckUtils;
import com.heqifuhou.view.MyDateTimePick;
import com.heqifuhou.view.MyDateTimePick.OnDateTimePickListener;
import com.heqifuhou.view.MyGridView;
import com.heqifuhou.view.NoScrollListView;
import com.heqifuhou.view.PopupWindows;
import com.heqifuhou.view.PopupWindows.OnPopupWindowsItemListener;

public class MeetingOrderCreateAct extends HttpMyActBase implements OnClickListener,
		OnItemClickListener {
	private static int ID_SUBMIT = 0x10, ID_SEL_PERSON = 0x12;
	protected RoomListItem RoomIt;
	protected TextView ResourceId, startTxt, endTxt;
	protected EditText subject, descripition;
	protected MyGridView approverGrid;
	protected NoScrollListView attachsList;
	protected MeetingPickAdapter meetingAdapter = null;
	protected SelectPersonGridAdapter selectPersonAdapter = null;
	protected String id="";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTextNav(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				submit();
			}
		}, "预约会议室", "提交");
		this.addViewFillInRoot(R.layout.act_create_order_metting);
		this.ResourceId = (TextView) this.findViewById(R.id.ResourceId);
		this.startTxt = (TextView) this.findViewById(R.id.startTxt);
		this.endTxt = (TextView) this.findViewById(R.id.endTxt);
		this.subject = (EditText) this.findViewById(R.id.subject);
		this.descripition = (EditText) this.findViewById(R.id.descripition);
		this.approverGrid = (MyGridView) this.findViewById(R.id.approverGrid);
		this.attachsList = (NoScrollListView) this
				.findViewById(R.id.attachsList);
		this.findViewById(R.id.ScheduledStart).setOnClickListener(this);
		this.findViewById(R.id.ScheduledEnd).setOnClickListener(this);
		this.findViewById(R.id.attachs).setOnClickListener(this);
		this.meetingAdapter = new MeetingPickAdapter();
		this.attachsList.setAdapter(meetingAdapter);
		this.selectPersonAdapter = new SelectPersonGridAdapter();
		this.approverGrid.setAdapter(selectPersonAdapter);
		this.approverGrid.setOnItemClickListener(this);
		// 创建时显示数据
		if (this.getIntent() != null && this.getIntent().getExtras() != null) {
			String s = this.getIntent().getExtras().getString("RoomListItem");
			RoomIt = JSON.parseObject(s, RoomListItem.class);
			this.ResourceId.setText("会议室名:" + RoomIt.getName());
		}
	}

	protected void submit() {
		String start = this.startTxt.getText().toString().trim();
		String end = this.endTxt.getText().toString().trim();
		String _subject = subject.getText().toString().trim();
		String _descripition= descripition.getText().toString().trim();
		if(ParamsCheckUtils.isNull(_subject)){
			showToast("会议主题不能为空");
			return;
		}
		if(ParamsCheckUtils.isNull(_descripition)){
			showToast("会议内容不能为空");
			return;
		}
		if(ParamsCheckUtils.isNull(start)){
			showToast("占用开始时间不能为空");
			return;
		}
		if(ParamsCheckUtils.isNull(end)){
			showToast("占用结束时间不能为空");
			return;
		}
		String sTxt = (String) this.startTxt.getTag();
		long s = Long.parseLong(sTxt);
		long e = Long.parseLong((String) this.endTxt.getTag());
		if(e<s){
			showToast("开始时间不能小于结束时间");
			return;
		}
		String attachs = getAttachsList();
		String approver = toMenberList();
		this.quickHttpRequest(ID_SUBMIT, new RoomRoderCreateEditRun(id,RoomIt.getResourceOrgId(),start,end,_subject,_descripition,attachs,approver));
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
						startTxt.setTag(String.format("%04d%02d%02d%02d%02d", nResultYear, nResultMonthOfYear + 1,
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
						endTxt.setTag(String.format("%04d%02d%02d%02d%02d", nResultYear, nResultMonthOfYear + 1,
								nResultDayOfMonth, nResultHourOfDay,
								nResultMinute));
					}
				});
		endDateTimePick.show();
	}

	private PopupWindows attachsPop;

	private void startAttachsAction() {
		if (attachsPop != null && attachsPop.isShowing()) {
			return;
		}
		final String[] s = new String[] { "投影", "话筒", "电脑", "电子屏", "其它" };
		attachsPop = new PopupWindows(this, this.findViewById(R.id.root), s);
		attachsPop.show();
		attachsPop
				.setOnPopupWindowsItemListener(new OnPopupWindowsItemListener() {
					@Override
					public void onPopupWindowsItem(int pos) {
						meetingAdapter.addToListBack(s[pos]);
					}
				});
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
				RoomRoderCreateEditResultBean o = (RoomRoderCreateEditResultBean)obj;
				RoomAppointItem it = o.getData();
				Intent i = new Intent(IBroadcastAction.ACTION_CREATE_MEETING);
				i.putExtra("RoomAppointItem", JSON.toJSONString(it));
				sendBroadcast(i);
				this.finish();
			} else {
				this.showToast(obj.getMsg());
			}
		}
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
		case R.id.attachs:
			startAttachsAction();
			break;
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
	
	private String getAttachsList(){
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<meetingAdapter.getCount();i++){
			if (i > 0) {
				sb.append(",");
			}
			sb.append(meetingAdapter.getItem(i));
		}
		return sb.toString();
	}
}
