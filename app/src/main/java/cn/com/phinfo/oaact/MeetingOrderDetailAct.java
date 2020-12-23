package cn.com.phinfo.oaact;


import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import cn.com.phinfo.entity.DataInstance;
import cn.com.phinfo.protocol.EntityInfoRun;
import cn.com.phinfo.protocol.EntityInfoRun.EntityInfoItem;
import cn.com.phinfo.protocol.EntityInfoRun.EntityInfoResultBean;
import cn.com.phinfo.protocol.UnitandaddressRun.UnitandaddressItem;

import com.heqifuhou.protocolbase.HttpResultBeanBase;


public class MeetingOrderDetailAct extends MeetingOrderCreateAct{
	private static int ID_GETLIST= 0x9;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTextNav(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				submit();
			}
		}, "预约会议室", "修攺");
		this.id = this.getIntent().getExtras().getString("ID");
		this.addViewFillInRoot(R.layout.act_create_order_metting);
		onRefresh();
	}
	
	protected void onRefresh(){
		this.quickHttpRequest(ID_GETLIST, new EntityInfoRun(id));
	}


	public void onHttpForResult(int id, HttpResultBeanBase obj, Object requestObj) {
		super.onHttpForResult(id, obj, requestObj);
	
	}
	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj, Object requestObj) {
		if(ID_GETLIST == id){
			if(obj.isOK()){
				EntityInfoResultBean o = (EntityInfoResultBean)obj;
				EntityInfoItem d = o.getData();
				startTxt.setTag(d.getScheduledStart2Format());
				startTxt.setText(d.getScheduledStart());
				
				endTxt.setTag(d.getScheduledEnd2Format());
				endTxt.setText(d.getScheduledEnd());
				subject.setText(d.getSubject());
				descripition.setText(d.getDescription());
				meetingAdapter.addToListBack(d.getAttache2List());
				List<UnitandaddressItem> lst = d.getApprovers2List();
				selectPersonAdapter.addToListBack(lst);
				DataInstance.getInstance().addUnitandaddressItem(lst);
			}else{
				this.showToast(obj.getMsg());
			}
			return;
		}
		super.onHttpResult(id, obj, requestObj);
	}
}
