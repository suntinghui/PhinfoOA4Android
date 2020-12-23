package cn.com.phinfo.oaact;


import java.io.File;
import java.util.IdentityHashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import cn.com.phinfo.protocol.UploadReportRun;
import cn.com.phinfo.protocol.UploadUpdateReportRun;

import com.baidu.mapapi.model.LatLng;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.imgutils.BitmapDataListInstanceUtils;
import com.heqifuhou.imgutils.FileItem;
import com.heqifuhou.imgutils.MyBaseBitmapAct;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.utils.ParamsCheckUtils;
import com.umeng.analytics.MobclickAgent;

public class CreateReportAct extends MyBaseBitmapAct {
	private int ID_LBS = 0x10,ID_REPORT=0x11,ID_SEND_NEW = 0x12,ID_SEND_UPDATE = 0x13;
	private ViewGroup liRoot = null;
	private EditText edit;
	private TextView visible,address;
	private LatLng currLatLng;
	private int WorklogType =0;
	private String Bulding="",visibleResult="0",groupidResult="";
	private String Worklogid;
	private boolean bEdit = false;
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BitmapDataListInstanceUtils.getRefInstance().clear();
		int type = this.getIntent().getExtras().getInt("TYPE",0);
		String title = "发送日报";
		WorklogType =3;
		if(type==2){
			title = "发送周报";
			WorklogType =2;
		}else if(type==3){
			title = "发送月报";
			WorklogType =1;
		}
		else if(type==4){
			title = "发送年报";
			WorklogType =0;
		}
		this.addTextNav(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(bEdit){
					sendUpdate();
				}else{
					sendNew();
				}
			}
		},title,"发送");
		this.addViewFillInRoot(R.layout.act_create_report);
		liRoot = (ViewGroup) this.findViewById(R.id.liLayout);
		liRoot.addView(InitBitmapView());
		edit = (EditText) this.findViewById(R.id.edit);
		
		address = (TextView) this.findViewById(R.id.address);
		visible = (TextView) this.findViewById(R.id.visible);
		//定位位置
		this.findViewById(R.id.addressbtn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(CreateReportAct.this, LBSAddressAct.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, ID_LBS);
			}
		});
		//是否可见
		this.findViewById(R.id.visibleBtn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(CreateReportAct.this, ReportVisibleAct.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, ID_REPORT);
			}
		});
		//如果不为空，那么就表示是要编辑的，只编辑内容，不编辑图片
		Worklogid = this.getIntent().getExtras().getString("WORKLOGID","");
		String content = this.getIntent().getExtras().getString("CONTENT", "");
		if(!ParamsCheckUtils.isNull(Worklogid)){
			liRoot.setVisibility(View.GONE);
			edit.setText(content);
			bEdit = true;
		}
	}
	//创建新
	public void sendNew(){
		String content = edit.getText().toString();
		if(ParamsCheckUtils.isNull(content)){
			showToast("内容不能为空");
			return;
		}
		IdentityHashMap<String, Object> has = new IdentityHashMap<String, Object>();
		has.put("content", content);
		has.put("WorklogType", WorklogType);
		has.put("location", Bulding);
		if(currLatLng!=null){
			has.put("Longitude", currLatLng.longitude);
			has.put("Latitude", currLatLng.latitude);
		}
		
		has.put("visible", visibleResult);
		has.put("groupid", groupidResult);
		
		List<FileItem> lst = BitmapDataListInstanceUtils.getRefInstance()
				.getListRef();
		int maxWidth=getWindowManager().getDefaultDisplay().getWidth();
		int maxHeight=getWindowManager().getDefaultDisplay().getHeight();
		for (int i = 0; i < lst.size(); i++) {
			FileItem it = lst.get(i);
			//压缩
			File f = it.getZipLocal2File(Math.min(maxWidth,800),Math.min(maxHeight,800));
			//如果没有压缩，就读源图
			if(f==null||!f.exists()){
				MobclickAgent.onEvent(this, it.getFileLocalPath()+"压缩失败");
				f  = new File(it.getFileLocalPath());
			}
			if (f!=null&&f.exists()) {
				String key = new String("pic[]");
				has.put(key, f);
			}
		}
		this.quickHttpRequest(ID_SEND_NEW, new UploadReportRun("",has));
	}
	//更新
	private void sendUpdate(){
		String content = edit.getText().toString();
		if(ParamsCheckUtils.isNull(content)){
			showToast("内容不能为空");
			return;
		}
		IdentityHashMap<String, Object> has = new IdentityHashMap<String, Object>();
		has.put("content", content);
		this.quickHttpRequest(ID_SEND_UPDATE, new UploadUpdateReportRun(Worklogid,has));
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ID_LBS && resultCode == RESULT_OK) {
			Bulding = data.getExtras().getString("Bulding");
			address.setText(Bulding);
			currLatLng = data.getExtras().getParcelable("LatLng");
			return;
		}
		//选择的东东
		if(requestCode == ID_REPORT&&resultCode == RESULT_OK){
			visibleResult = data.getExtras().getString("VISIBLE");
			groupidResult = data.getExtras().getString("GROUPID");
			if(visibleResult.equals("0")){
				visible.setText("所有人能看");
			}else if(visibleResult.equals("1")){
				visible.setText("仅自己可见");
			}else if(visibleResult.equals("3")){
				visible.setText("指定分组可见");
			}
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if(id==ID_SEND_NEW){
			if(obj.isOK()){
				showToast(obj.getMsg());
				this.finish();
				BitmapDataListInstanceUtils.getRefInstance().clear();
				this.sendBroadcast(new Intent(IBroadcastAction.ACTION_REPORT));
			}else{
				showToast(obj.getMsg());
			}
			return;
		}
		if(id==ID_SEND_UPDATE){
			if(obj.isOK()){
				showToast(obj.getMsg());
				this.sendBroadcast(new Intent(IBroadcastAction.ACTION_REPORT));
				this.finish();
			}else{
				showToast(obj.getMsg());
			}
			return;
		}
	}
	public void onDestroy() {
		super.onDestroy();
		BitmapDataListInstanceUtils.getRefInstance().clear();
	}
}
