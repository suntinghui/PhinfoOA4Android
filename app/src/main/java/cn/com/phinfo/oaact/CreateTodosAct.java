package cn.com.phinfo.oaact;

import java.io.File;
import java.util.IdentityHashMap;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.imgutils.BitmapDataListInstanceUtils;
import com.heqifuhou.imgutils.FileItem;
import com.heqifuhou.imgutils.MyBaseBitmapAct;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.protocolbase.HttpThread;
import com.heqifuhou.protocolbase.HttpThread.IHttpRunnable;
import com.heqifuhou.protocolbase.HttpThread.IThreadResultListener;
import com.heqifuhou.utils.ParamsCheckUtils;
import com.heqifuhou.view.NoScrollListView;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.com.phinfo.adapter.FilePickerAdapter;
import cn.com.phinfo.entity.FilePickerItem;
import cn.com.phinfo.entity.SelectItem;
import cn.com.phinfo.protocol.CreateInstanceRun;
import cn.com.phinfo.protocol.LURLInterface;
import cn.com.phinfo.protocol.UserInfoRun;
import cn.com.phinfo.protocol.CreateInstanceRun.CreateInstanceItem;
import cn.com.phinfo.protocol.CreateInstanceRun.CreateInstanceResultBean;
import cn.com.phinfo.protocol.ProcessSearchListRun.ProcessSearchItem;
import cn.com.phinfo.protocol.UserInfoRun.ItemUserInfo;
import cn.com.phinfo.protocol.UserInfoRun.UeserInfoResultBean;
import cn.com.phinfo.protocol.UploadRun;
import cn.qqtheme.framework.picker.FilePicker;
import cn.qqtheme.framework.picker.FilePicker.OnFilePickListener;

//创建事件
public class CreateTodosAct extends MyBaseBitmapAct {
	private static int ID_PRO = 200;
	private static int ID_CREATE = 0x10,ID_IMG_ZIP = 0x12,ID_UPLOAD=0x13,ID_GETINFO=0x14;
	private ProcessSearchItem item;
	private LinearLayout photoli;
	private NoScrollListView scrollView;
	private SelectItem selPro = new SelectItem(0,"普通");
	private TextView priorityTxt;
	private FilePickerAdapter fileAdapter;
	private EditText deadlineTxt,descriptionTxt,mytitle;
	private CreateInstanceItem instanceItem;
	private ItemUserInfo userInfo;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BitmapDataListInstanceUtils.getRefInstance().clear();
		this.addViewFillInRoot(R.layout.act_createtodos);
		this.addTextNav(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				submitAction();
			}
		}, "", "提交");
		if (this.getIntent() != null && this.getIntent().getExtras() != null) {
			String s = this.getIntent().getExtras().getString("ProcessSearchItem");
			if (!ParamsCheckUtils.isNull(s)) {
				item = JSON.parseObject(s, ProcessSearchItem.class);
			}
		}
		mytitle = (EditText) this.findViewById(R.id.mytitle);
		photoli = (LinearLayout) this.findViewById(R.id.photoli);
		scrollView = (NoScrollListView) this.findViewById(R.id.scrollView);
		deadlineTxt = (EditText) this.findViewById(R.id.deadline);
		descriptionTxt = (EditText) this.findViewById(R.id.Description);
		scrollView.setEnabled(false);
		priorityTxt = (TextView) this.findViewById(R.id.priority_txt);
		priorityTxt.setText(selPro.getText());
		this.photoli.addView(InitBitmapView());
		// 紧级
		this.findViewById(R.id.accout).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(CreateTodosAct.this, SelectAct.class);
				intent.putExtra("DATA", "普通,紧急,特急");
				intent.putExtra("TITLE", "级别");
				startActivityForResult(intent, ID_PRO);
			}
		});
		//图片
		this.findViewById(R.id.imgli).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showDialog();
			}
		});
		// 附件
		this.findViewById(R.id.attcheli).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showFilePicker();
			}
		});
		//显示标题
		TextView txt = (TextView) this.findViewById(R.id.title);
		txt.setText(item.getName());
		//附件列表
		fileAdapter = new FilePickerAdapter();
		scrollView.setAdapter(fileAdapter);
		
		//提交
		this.findViewById(R.id.submit_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				submitAction();
			}
		});
		onRefresh();
	}
	private FilePicker picker;
	private void showFilePicker() {
		if(picker!=null&&picker.isShowing()){
			return;
		}
		FilePicker picker = new FilePicker(this, FilePicker.FILE);
		picker.setShowHideDir(false);
		picker.setOnFilePickListener(new OnFilePickListener() {
			@Override
			public void onFilePicked(String arg0) {
				fileAdapter.addToListBack(new FilePickerItem(arg0));
			}
		});
		// picker.setAllowExtensions(new String[]{".apk"});
		picker.show();
	}

	private void submitAction(){
		String deadline = deadlineTxt.getText().toString().trim();
		String name = mytitle.getText().toString().trim();
		int Prority = 0;
		if(selPro!=null){
			Prority = selPro.getId();
		}
		String processid = item.getProcessId();
		String businessUnitId = "";
		String Description = descriptionTxt.getText().toString().trim();
		this.quickHttpRequest(ID_CREATE, new CreateInstanceRun(name,processid,businessUnitId,deadline,Prority,Description));
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (ID_PRO == requestCode && resultCode == RESULT_OK) {
			selPro = (SelectItem) data.getExtras().getSerializable("SelectItem");
			priorityTxt.setText(selPro.getText());
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	protected void onRefresh() {
		this.quickHttpRequest(ID_GETINFO, new UserInfoRun());
	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj, Object requestObj) {
		if(ID_GETINFO== id){
			if(obj.isOK()){
				UeserInfoResultBean o = (UeserInfoResultBean) obj;
				if (!o.getData().isEmpty()) {
					userInfo = o.getData().get(0);
					//显示标题
					mytitle.setText(item.getName()+" "+userInfo.getDeptName()+" "+userInfo.getFullName());
				}
			}
		}
		//创建
		if(id == ID_CREATE){
			if(obj.isOK()){
				CreateInstanceResultBean o = (CreateInstanceResultBean)obj;
				instanceItem = o.getData();
				if(!uploadFile()){
					startHRLARYActt();
					this.finish();
				}
			}else{
				showToast(obj.getMsg());
			}
			return;
		}
		//上传附件
		if(ID_UPLOAD == id){
			if(obj.isOK()){
				showToast("提交成功!");
				startHRLARYActt();
				this.finish();
			}else{
				showToast(obj.getMsg());
			}
			return;
		}
	}
	
	private void startHRLARYActt(){
		Intent intent = new Intent(this,WebViewRefreshAct.class);
		intent.putExtra("TITLE",item.getName()+item.getCreatedByName());
		intent.putExtra("URL",LURLInterface.GET_CREATE_TODO_OK(instanceItem.getProcessInstanceId(), instanceItem.getWfRuleLogId()));
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
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
				String key = new String("Images[]");
				has.put(key, f);
				bRt = true;
			}else{
				return false;
			}
		}
		return bRt;
	}
	
	private boolean addAttache(IdentityHashMap<String, Object> has){
		List<FilePickerItem> lst = fileAdapter.getListRef();
		if(lst.isEmpty()){
			return false;
		}
		for (int i = 0; i < lst.size(); i++) {
			FilePickerItem it = lst.get(i);
			String key = new String("Attache[]");
			File f  = new File(it.getFilePicker());
			has.put(key, f);
		}
		return true;
	}
	
	private boolean isAttache(){
		return fileAdapter.getCount()>0;
		
	}
	private boolean isEmptyImg() {
		List<FileItem> lst = BitmapDataListInstanceUtils.getRefInstance()
				.getListRef();
		return lst.isEmpty();
	}
	
	private boolean uploadFile(){
		if(isEmptyImg()&&!isAttache()){
			return false;
		}
		IdentityHashMap<String, Object> has = new IdentityHashMap<String, Object>();
		//压缩
		final IHttpRunnable zipRunable = new IHttpRunnable() {
			@Override
			public HttpResultBeanBase onRun(HttpThread t) {
				IdentityHashMap<String, Object> has = (IdentityHashMap<String, Object>)t.getRequestObj();
				addImgToMap(has);
				addAttache(has);
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
						quickHttpRequest(ID_UPLOAD, new UploadRun(instanceItem.getProcessInstanceId(),has));
					}else{
						showToast("附件读取失败");
					}
				}
			}
		};
		this.quickHttpRequest(ID_IMG_ZIP, zipRunable,ziplistener,has,true);
		return true;
	}
	
	public void onDestroy() {
		super.onDestroy();
		BitmapDataListInstanceUtils.getRefInstance().clear();
	}
}
