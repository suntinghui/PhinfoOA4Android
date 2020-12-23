package cn.com.phinfo.oaact;


import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.com.phinfo.adapter.WIFIAddAdapter;
import cn.com.phinfo.protocol.AddWIFIAttendsettingsRun;
import cn.com.phinfo.protocol.AttendsettingsWifiListRun;
import cn.com.phinfo.protocol.AttendsettingsWifiListRun.AttendWifiSettingsItem;
import cn.com.phinfo.protocol.AttendsettingsWifiListRun.AttendWifiSettingsResultBean;

import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.heqifuhou.pulltorefresh.PullToRefreshListView;

public class CheckInWiFiAct extends HttpMyActBase implements OnItemClickListener{
	private static int ID_WIFI=0x11,ID_ADD=0x12,ID_DELWIFI=0x13;
	private PullToRefreshListView mList = null;
	private WIFIAddAdapter adapter = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTextNav("附近WIFI列表");
		this.addViewFillInRoot(R.layout.act_checkin_setting_wifi_build_list);
		mList = (PullToRefreshListView) this.findViewById(R.id.refreshListView);
		mList.setMode(Mode.DISABLED);
		adapter = new WIFIAddAdapter();
		mList.setAdapter(adapter);
		mList.setOnRefreshListener(new OnRefreshListener<ListView>(){
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				CheckInWiFiAct.this.onRefresh();
			}
		});
		mList.setOnItemClickListener(this);
		onRefresh();
	}

	protected void onRefresh() {
		this.quickHttpRequest(ID_WIFI, new AttendsettingsWifiListRun());
	}
	public void onHttpForResult(int id, HttpResultBeanBase obj, Object requestObj) {
		super.onHttpForResult(id, obj, requestObj);
		mList.onRefreshComplete();
	}
	
	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		//取WIFI列表
		if(ID_WIFI == id){
			AttendWifiSettingsResultBean o = (AttendWifiSettingsResultBean)obj;
			adapter.clear();
			WifiManager wifiManager = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);  
			List<ScanResult>  listb = wifiManager.getScanResults(); 
			for( int i=0;i<listb.size();i++){  
	            ScanResult scanResult = listb.get(i);
	            AttendWifiSettingsItem it  = new AttendWifiSettingsItem();
	            it.setAddress(scanResult.BSSID);
	            it.setName(scanResult.SSID.replace("\"",""));
	            if(!o.isCon(it)){
	            	adapter.addToListBack(it);
	            }  
	        } 
			return;
		}
		if(ID_ADD == id){
			if(obj.isOK()){
				showToast("添加成功");
				adapter.tryRemove(requestObj);
				sendBroadcast(new Intent(IBroadcastAction.ACTION_SETTING_CHECKING));
			}else{
				AttendWifiSettingsItem it = (AttendWifiSettingsItem)requestObj;
				adapter.changeState(it);
				showToast(obj.getMsg());
			}
			return;
		}
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		AttendWifiSettingsItem it = adapter.getItem(arg2-1);
		adapter.changeState(it);
		this.quickHttpRequest(ID_ADD, new AddWIFIAttendsettingsRun(it.getName(),it.getAddress()),it);
	}
}
