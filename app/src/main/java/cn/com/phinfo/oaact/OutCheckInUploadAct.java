 package cn.com.phinfo.oaact;

import java.io.File;
import java.util.Calendar;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.TimeZone;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.com.phinfo.entity.DataInstance;
import cn.com.phinfo.protocol.OutCheckInUploadRun;
import cn.com.phinfo.protocol.UnitandaddressRun.UnitandaddressItem;

import com.heqifuhou.imgutils.BitmapDataListInstanceUtils;
import com.heqifuhou.imgutils.FileItem;
import com.heqifuhou.imgutils.MyBaseBitmapAct;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.protocolbase.HttpThread;
import com.heqifuhou.protocolbase.HttpThread.IHttpRunnable;
import com.heqifuhou.protocolbase.HttpThread.IThreadResultListener;
import com.umeng.analytics.MobclickAgent;

public class OutCheckInUploadAct extends MyBaseBitmapAct {
	private static int ID_SUBMIT = 0x10,ID_IMG_ZIP=0x12;
	private LinearLayout li;
	private EditText Descripiton;
	private TextView time,address,title;
	private String CotactName,saddress,CheckTime,bulding;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CotactName = this.getIntent().getExtras().getString("CotactName");
		bulding = this.getIntent().getExtras().getString("Bulding");
		BitmapDataListInstanceUtils.getRefInstance().clear();
		this.addTextNav("签到");
		this.addViewFillInRoot(R.layout.act_out_checkin);
		li  = (LinearLayout) this.findViewById(R.id.photoLi);
		time = (TextView) this.findViewById(R.id.time);
		title =  (TextView) this.findViewById(R.id.title);
		address = (TextView) this.findViewById(R.id.address);
		Descripiton = (EditText) this.findViewById(R.id.Description);
		li.addView(this.InitBitmapView());
		
		
		//定位时间
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		int year = calendar.get(Calendar.YEAR);
		int monthOfYear = calendar.get(Calendar.MONTH);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		int HourOfDay=calendar.get(Calendar.HOUR_OF_DAY);  
		int Minute=calendar.get(Calendar.MINUTE); 
		int second=calendar.get(Calendar.SECOND); 
		CheckTime = String.format("%04d-%02d-%02d %02d:%02d:%02d", year,monthOfYear,dayOfMonth+1,HourOfDay,Minute,second);
		time.setText(CheckTime);
		//显示地址
		saddress = this.getIntent().getExtras().getString("ADDRESS");
		address.setText(saddress);
		this.findViewById(R.id.btnSubmit).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				uploadFile();
			}
		});
		
		title.setText(bulding);
	}

	protected void onRefresh() {

	}
	private boolean uploadFile(){
		IdentityHashMap<String, Object> has = new IdentityHashMap<String, Object>();
		has.put("Location",saddress);
		has.put("ContactName",CotactName);
		has.put("CheckTime",CheckTime);
		has.put("BuildingName", bulding);
		has.put("Descripiton",Descripiton.getText().toString().trim());
		List<UnitandaddressItem> lst = DataInstance.getInstance()
				.getUnitandaddressItemList();
		String s = "";
		for (int i = 0; i < lst.size(); i++) {
			if(i>0){
				s +=",";
			}
			s += lst.get(i).getId();
		}
		has.put("ContactId",s);
		
		//压缩
		final IHttpRunnable zipRunable = new IHttpRunnable() {
			@Override
			public HttpResultBeanBase onRun(HttpThread t) {
				IdentityHashMap<String, Object> has = (IdentityHashMap<String, Object>)t.getRequestObj();
				addImgToMap(has);
				HttpResultBeanBase ret = new HttpResultBeanBase();
				ret.setOK();
				return ret;
			}
		};
		final IThreadResultListener ziplistener = new IThreadResultListener() {
			@Override
			public void onHttpForResult(int id, HttpResultBeanBase obj,
					Object requestObj) {
				if(id == ID_IMG_ZIP){
					hideLoading();
					if(obj.isOK()){
						IdentityHashMap<String, Object> has = (IdentityHashMap<String, Object>)requestObj;
						quickHttpRequest(ID_SUBMIT, new OutCheckInUploadRun(has));
					}else{
						showToast("附件读取失败");
					}
				}
			}
		};
		this.quickHttpRequest(ID_IMG_ZIP, zipRunable,ziplistener,has,true);
		return true;
	}
	private boolean addImgToMap(IdentityHashMap<String, Object> has) {
		//清除
		FileItem.clear();
		boolean bRt = false;
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
				String key = new String("Photo[]");
				has.put(key, f);
				bRt = true;
			}else{
				return false;
			}
		}
		return bRt;
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
				this.showToast("签到成功");
				this.finish();
			} else {
				this.showToast(obj.getMsg());
			}
		}
	}
	public void onDestroy() {
		super.onDestroy();
		BitmapDataListInstanceUtils.getRefInstance().clear();
	}
}
