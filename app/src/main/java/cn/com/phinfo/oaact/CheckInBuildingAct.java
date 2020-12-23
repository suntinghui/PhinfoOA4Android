package cn.com.phinfo.oaact;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.com.phinfo.adapter.BuildingAdapter;
import cn.com.phinfo.protocol.AttendsettingsListRun;
import cn.com.phinfo.protocol.AttendsettingsListRun.AttendSettingsItem;
import cn.com.phinfo.protocol.AttendsettingsListRun.AttendSettingsResultBean;
import cn.com.phinfo.protocol.DelAttendsettingsListRun;

import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.heqifuhou.pulltorefresh.PullToRefreshListView;
import com.heqifuhou.view.ConfirmDialog;
import com.heqifuhou.view.ConfirmDialog.OnDialogOKListener;

public class CheckInBuildingAct extends HttpMyActBase implements OnItemClickListener{
	private static int ID_LIST = 0x10,ID_DEL = 0x11,ID_ADD=0x12;
	private PullToRefreshListView mList = null;
	private BuildingAdapter adapter = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTextNav(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(CheckInBuildingAct.this, CheckInBuildingAddAct.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(i, ID_ADD);
			}
		},"办公地点管理","添加");
		this.addViewFillInRoot(R.layout.act_checkin_setting_wifi_build_list);
		mList = (PullToRefreshListView) this.findViewById(R.id.refreshListView);
		mList.setMode(Mode.PULL_FROM_START);
		adapter = new BuildingAdapter(null);
		mList.setAdapter(adapter);
		mList.setOnRefreshListener(new OnRefreshListener<ListView>(){
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				CheckInBuildingAct.this.onRefresh();
			}
		});
		mList.setOnItemClickListener(this);
		onRefresh();
	}
	protected void onRefresh() {
		this.quickHttpRequest(ID_LIST, new AttendsettingsListRun());
	}
	public void onHttpForResult(int id, HttpResultBeanBase obj, Object requestObj) {
		super.onHttpForResult(id, obj, requestObj);
		mList.onRefreshComplete();
	}
	
	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if(id==ID_LIST){
			AttendSettingsResultBean o = (AttendSettingsResultBean)obj;
			adapter.replaceListRef(o.getData());
			return;
		}
		if(id == ID_DEL)
		{
			if(obj.isOK()){
				adapter.tryRemove(requestObj);
				showToast("删除成功");
			}else{
				showToast(obj.getMsg());
			}
			return;
		}
	}
	private ConfirmDialog delConfirmDialog;
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		final AttendSettingsItem it  = adapter.getItem(arg2-1);
		if(delConfirmDialog!=null&&delConfirmDialog.isShowing()){
			return;
		}
		delConfirmDialog = new ConfirmDialog(this,"确定要删除么？",null);
		delConfirmDialog.setOnDialogOKListener(new OnDialogOKListener() {
			@Override
			public void onOKItem(Object obj) {
				quickHttpRequest(ID_DEL, new DelAttendsettingsListRun(it.getAttendlocationid()),it);
			}
		});
		delConfirmDialog.show();
		
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ID_ADD && resultCode == RESULT_OK) {
			onRefresh();
			return;
		}
	}
}
