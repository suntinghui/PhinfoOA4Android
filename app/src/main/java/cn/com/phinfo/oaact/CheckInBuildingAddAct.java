package cn.com.phinfo.oaact;


import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import cn.com.phinfo.oaact.base.LBSAct;
import cn.com.phinfo.protocol.AddAttendsettingsRun;

import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.utils.ParamsCheckUtils;

public class CheckInBuildingAddAct extends LBSAct{
	private static int ID_ADD = 0x10;
	private TextView Name,Location;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTextNav("添加办公地点");
		this.addViewFillInRoot(R.layout.act_checkin_add_build);
		Name = (TextView) this.findViewById(R.id.Name);
		Location  = (TextView) this.findViewById(R.id.Location);
		this.findViewById(R.id.btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				submit();
			}
		});
		this.showLoading();
		showAddress();
	}
	
	public void showAddress(){
		if (loc == null) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					showAddress();
				}
			},200);
			return;
		}
		this.hideLoading();
		Name.setText(loc.getBuildingName());
		Location.setText(loc.getAddress().address);
	}
	
	private void submit(){
		if(loc==null){
			showToast("定位地址失败");
		}
		String _name = Name.getText().toString().trim();
		String _Location = Location.getText().toString().trim();
		if(ParamsCheckUtils.isNull(_name)){
			showToast("建筑名不能为空");
			return;
		}
		if(ParamsCheckUtils.isNull(_Location)){
			showToast("地址不能为空");
			return;
		}
		this.quickHttpRequest(ID_ADD, new AddAttendsettingsRun(_name,_Location,String.valueOf(loc.getLongitude()),String.valueOf(loc.getLatitude())));
	}
	protected void onRefresh() {	
	}
	
	public void onHttpForResult(int id, HttpResultBeanBase obj, Object requestObj) {
		super.onHttpForResult(id, obj, requestObj);
	}
	
	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if(id==ID_ADD){
			if(obj.isOK()){
				this.setResult(RESULT_OK);
				this.finish();
				return;
			}else{
				showToast(obj.getMsg());
			}
		}
	}
}
