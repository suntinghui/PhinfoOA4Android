package cn.com.phinfo.oaact.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import cn.com.phinfo.adapter.BuildingAdapter;
import cn.com.phinfo.adapter.WIFISelAdapter;
import cn.com.phinfo.oaact.CheckInSettingMoreAct;
import cn.com.phinfo.oaact.CheckInWiFiAct;
import cn.com.phinfo.oaact.LBSBuildingAct;
import cn.com.phinfo.oaact.R;
import cn.com.phinfo.protocol.AttendsettingsListRun;
import cn.com.phinfo.protocol.AttendsettingsListRun.AttendSettingsItem;
import cn.com.phinfo.protocol.AttendsettingsListRun.AttendSettingsResultBean;
import cn.com.phinfo.protocol.AttendsettingsWifiListRun;
import cn.com.phinfo.protocol.AttendsettingsWifiListRun.AttendWifiSettingsItem;
import cn.com.phinfo.protocol.AttendsettingsWifiListRun.AttendWifiSettingsResultBean;
import cn.com.phinfo.protocol.AttentdsettingsRun;
import cn.com.phinfo.protocol.AttentdsettingsRun.AttentdSettingsData;
import cn.com.phinfo.protocol.AttentdsettingsRun.AttentdsettingsResultBean;
import cn.com.phinfo.protocol.AttentdsettingsRun.GlobalSettingsItem;
import cn.com.phinfo.protocol.DelAttendWiFIsettingsListRun;
import cn.com.phinfo.protocol.DelAttendsettingsListRun;
import cn.com.phinfo.protocol.HR_AttendsettingsRun;
import cn.com.phinfo.protocol.HrAttendrptRun;
import cn.com.phinfo.protocol.SysPrivilegeRun;
import cn.com.phinfo.protocol.SysPrivilegeRun.SySPrivilegeResultBean;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.view.ConfirmDialog;
import com.heqifuhou.view.ConfirmDialog.OnDialogOKListener;
import com.heqifuhou.view.NoScrollListView;
import com.heqifuhou.view.PopupDialog;
import com.heqifuhou.view.PopupDialog.OnDialogItemListener;

public class CheckIn3BaseAct extends LBSAct  {
	protected static int ID_GETLIST = 0x10, ID_GO_SETTING = 0x11,
			ID_CHECKIN = 0x12,ID_HrAttendrpt=0x13,ID_CHECKSETTING_SHOW = 0x14
			,ID_DIS = 0x15,ID_WIFI=0x16,ID_DELWIFI=0x17,ID_DELBUILD=0x18
			,ID_LIST_BUILD=0x19,ID_Attendrpt = 0x20;
	private View id_r1_checkin, id_r2_checkin, id_r3_checkin;
	private RadioGroup radioGroup;
	protected AttentdSettingsData attendssettnigs;
	private PopupDialog popDistanceRangeDialog;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTextNav("");
		this.addViewFillInRoot(R.layout.act_checkin);
		this.addBottomView(R.layout.check_tools_bar);
		this.id_r1_checkin = this.findViewById(R.id.id_r1_checkin);
		this.id_r2_checkin = this.findViewById(R.id.id_r2_checkin);
		this.id_r3_checkin = this.findViewById(R.id.id_r3_checkin);

		radioGroup = (RadioGroup) this.findViewById(R.id.main_radio);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				switch (arg1) {
				case R.id.check_btn:
					addTextNav("打卡");
					id_r1_checkin.setVisibility(View.VISIBLE);
					id_r2_checkin.setVisibility(View.GONE);
					id_r3_checkin.setVisibility(View.GONE);
					break;
				case R.id.history_btn:
					id_r1_checkin.setVisibility(View.GONE);
					id_r2_checkin.setVisibility(View.VISIBLE);
					id_r3_checkin.setVisibility(View.GONE);
					addTextNav("统计");
					break;
				case R.id.setting_btn:
					id_r1_checkin.setVisibility(View.GONE);
					id_r2_checkin.setVisibility(View.GONE);
					id_r3_checkin.setVisibility(View.VISIBLE);
					addTextNav("设置");
					break;
				}
			}
		});
		this.inittab3();
	}
	
	//tab3
	private WIFISelAdapter wifiAdapter = null;
	private BuildingAdapter buildAdapter;
	private NoScrollListView wifiList,buildrefreshListView;
	private TextView s_buildsize;
	private ConfirmDialog delConfirmDialog;
	private void inittab3(){
		wifiList = (NoScrollListView) this.findViewById(R.id.wifiRefreshListView);
		wifiAdapter = new WIFISelAdapter(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final AttendWifiSettingsItem it  = (AttendWifiSettingsItem)arg0.getTag();
				if(delConfirmDialog!=null&&delConfirmDialog.isShowing()){
					return;
				}
				delConfirmDialog = new ConfirmDialog(CheckIn3BaseAct.this,"确定要删除么？",null);
				delConfirmDialog.setOnDialogOKListener(new OnDialogOKListener() {
					@Override
					public void onOKItem(Object obj) {
						quickHttpRequest(ID_DELWIFI, new DelAttendWiFIsettingsListRun(it.getAttendwifiid()),it);
					}
				});
				delConfirmDialog.show();
				
			}
		});
		wifiList.setAdapter(wifiAdapter);
		
		buildrefreshListView = (NoScrollListView) this.findViewById(R.id.buildrefreshListView);
		buildAdapter = new BuildingAdapter(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final AttendSettingsItem it  = (AttendSettingsItem)arg0.getTag();
				if(delConfirmDialog!=null&&delConfirmDialog.isShowing()){
					return;
				}
				delConfirmDialog = new ConfirmDialog(CheckIn3BaseAct.this,"确定要删除么？",null);
				delConfirmDialog.setOnDialogOKListener(new OnDialogOKListener() {
					@Override
					public void onOKItem(Object obj) {
						quickHttpRequest(ID_DELBUILD, new DelAttendsettingsListRun(it.getAttendlocationid()),it);
					}
				});
				delConfirmDialog.show();
			}
		});
		buildrefreshListView.setAdapter(buildAdapter);
		
		
		s_buildsize= (TextView) this.findViewById(R.id.s_buildsize);
		final OnClickListener clickListener = new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				switch(arg0.getId()){
				case R.id.s_wifi_add:
					Intent i = new Intent(CheckIn3BaseAct.this, CheckInWiFiAct.class);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
					break;
				case R.id.s_more:
					Intent intent = new Intent(CheckIn3BaseAct.this, CheckInSettingMoreAct.class);
					intent.putExtra("GlobalSettingsItem", JSON.toJSONString(attendssettnigs.getGlobalSettings()));
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					break;
				case R.id.s_build_add:
					Intent i1 = new Intent(CheckIn3BaseAct.this, LBSBuildingAct.class);
					i1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i1);
					break;
				case R.id.s_build_size_btn:
					showDistanceRangeDialog();
					break;
				}
			}
		};
		this.findViewById(R.id.s_wifi_add).setOnClickListener(clickListener);
		this.findViewById(R.id.s_build_size_btn).setOnClickListener(clickListener);
		this.findViewById(R.id.s_build_add).setOnClickListener(clickListener);
		this.findViewById(R.id.s_more).setOnClickListener(clickListener);
		
	}
	


	private boolean showDistanceRangeDialog(){
		if(popDistanceRangeDialog!=null&&popDistanceRangeDialog.isShowing()){
			return false;
		}
		String[] ItemList = new String[3];
		ItemList[0] = "300米";
		ItemList[1] = "500米";
		ItemList[2] = "1000米";
		popDistanceRangeDialog = new PopupDialog(this, ItemList, ItemList);
		popDistanceRangeDialog.show();
		popDistanceRangeDialog.setOnDialogItemListener(new OnDialogItemListener() {
			@Override
			public void onDialogItem(int pos, Object obj) {	
				long[] array = new long[]{300,500,1000};
				String s = JSON.toJSONString(attendssettnigs.getGlobalSettings());
				GlobalSettingsItem it = JSON.parseObject(s, GlobalSettingsItem.class);
				it.setDistanceRange(array[pos]);
				quickHttpRequest(ID_DIS,new HR_AttendsettingsRun(it),array[pos]);
			}
		});
		return true;
	}

	protected void onRefresh() {
		this.quickHttpRequest(ID_GO_SETTING, new AttentdsettingsRun());
		this.quickHttpRequest(ID_HrAttendrpt, new HrAttendrptRun("17","4"));
		this.quickHttpRequest(ID_CHECKSETTING_SHOW, new SysPrivilegeRun());
		this.quickHttpRequest(ID_WIFI, new AttendsettingsWifiListRun());
		this.quickHttpRequest(ID_LIST_BUILD, new AttendsettingsListRun());
	}


	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		//修攺成功
		if(ID_DIS == id){
			if(obj.isOK()){
				long s = (long)requestObj;
				s_buildsize.setText(s+"米");
				attendssettnigs.getGlobalSettings().setDistanceRange(s);
			}else{
				showToast(obj.getMsg());
			}
		}
		else if (ID_GO_SETTING == id) {
			if (obj.isOK()) {
				attendssettnigs = ((AttentdsettingsResultBean) obj).getData();
				s_buildsize.setText(attendssettnigs.getGlobalSettings().getDistanceRange()+"米");
//				adapter.setAttentdSettingsData(attendssettnigs);
			} else {
				//重新请求规则 TODO
				this.quickHttpRequest(ID_GO_SETTING, new AttentdsettingsRun());
				showToast("读取规则失败");
			}
		}
		else if(ID_HrAttendrpt == id){
			return;
		}
		//是否显示设置按钮
		else if(ID_CHECKSETTING_SHOW == id){
			if(obj.isOK()){
				SySPrivilegeResultBean o = (SySPrivilegeResultBean)obj;
				if(o.getData().getAttendAdmin()){
					findViewById(R.id.setting_btn).setVisibility(View.VISIBLE);
				}
			}
			return;
		}
		//取WIFI列表
		if(ID_WIFI == id){
			if(obj.isOK()){
				AttendWifiSettingsResultBean o = (AttendWifiSettingsResultBean)obj;
				wifiAdapter.replaceListRef(o.getData());
			}
			return;
		}
		//删除wifi
		if(ID_DELWIFI == id){
			if(obj.isOK()){
				wifiAdapter.tryRemove(requestObj);
				onRefresh();
				showToast("删除成功");
			}else{
				showToast(obj.getMsg());
			}
			return;
		}
		//建筑列表
		if(ID_LIST_BUILD==id){
			if(obj.isOK()){
				AttendSettingsResultBean o = (AttendSettingsResultBean)obj;
				buildAdapter.replaceListRef(o.getData());
			}
		}
		//删除建筑
		if(ID_DELBUILD == id){
			if(obj.isOK()){
				buildAdapter.tryRemove(requestObj);
				onRefresh();
				showToast("删除成功");
			}else{
				showToast(obj.getMsg());
			}
		}
	}


	protected void onDestroy() {
		super.onDestroy();
	}

	protected void onBroadcastReceiverListener(Context context, Intent intent) {
		if(IBroadcastAction.ACTION_SETTING_CHECKING.equals(intent.getAction())){
			onRefresh();
			return;
		}
		super.onBroadcastReceiverListener(context, intent);
	};

}
