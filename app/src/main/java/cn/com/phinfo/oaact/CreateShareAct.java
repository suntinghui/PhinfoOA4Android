package cn.com.phinfo.oaact;


import java.io.File;
import java.util.IdentityHashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import cn.com.phinfo.protocol.UploadShareRun;

import com.baidu.mapapi.model.LatLng;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.imgutils.BitmapDataListInstanceUtils;
import com.heqifuhou.imgutils.FileItem;
import com.heqifuhou.imgutils.MyBaseBitmapAct;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.utils.ParamsCheckUtils;
import com.umeng.analytics.MobclickAgent;

public class CreateShareAct extends MyBaseBitmapAct {
	private int ID_LBS = 0x10,ID_REPORT=0x11;
	private ViewGroup liRoot = null;
	private EditText edit;
	private TextView visible,address;
	private LatLng currLatLng;
	private String Bulding="",visibleResult="0",groupidResult="";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BitmapDataListInstanceUtils.getRefInstance().clear();
		int type = this.getIntent().getExtras().getInt("TYPE",0);
		String title = "发送文字";
		if(type==2){
			title = "";
		}
		this.addTextNav(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				send();
			}
		},title,"发送");
		this.addViewFillInRoot(R.layout.act_create_report);
		liRoot = (ViewGroup) this.findViewById(R.id.liLayout);
		liRoot.addView(InitBitmapView());
		if(type==1){
			liRoot.setVisibility(View.GONE);
		}
		edit = (EditText) this.findViewById(R.id.edit);
		
		address = (TextView) this.findViewById(R.id.address);
		visible = (TextView) this.findViewById(R.id.visible);
		//定位位置
		this.findViewById(R.id.addressbtn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(CreateShareAct.this, LBSAddressAct.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, ID_LBS);
			}
		});
		//是否可见
		this.findViewById(R.id.visibleBtn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(CreateShareAct.this, ReportVisibleAct.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, ID_REPORT);
			}
		});
	}
	
	public void send(){
		String content = edit.getText().toString();
		if(ParamsCheckUtils.isNull(content)){
			showToast("内容不能为空");
			return;
		}
		IdentityHashMap<String, Object> has = new IdentityHashMap<String, Object>();
		has.put("status", content);
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
		this.quickHttpRequest(0x10, new UploadShareRun(has));
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
		if(id==0x10){
			if(obj.isOK()){
				showToast(obj.getMsg());
				this.finish();
				BitmapDataListInstanceUtils.getRefInstance().clear();
				this.sendBroadcast(new Intent(IBroadcastAction.ACTION_SHARE));
			}else{
				showToast(obj.getMsg());
			}
		}
	}
	public void onDestroy() {
		super.onDestroy();
		BitmapDataListInstanceUtils.getRefInstance().clear();
	}
}
