package cn.com.phinfo.oaact;

import java.io.File;
import java.util.IdentityHashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.phinfo.entity.DataInstance;
import cn.com.phinfo.protocol.AttacheFileRun.AttacheFileItem;
import cn.com.phinfo.protocol.EmailAttacheRun.EmailAttacheResultBean;
import cn.com.phinfo.protocol.EmailInfoRun.EmailInfoResultBean;
import cn.com.phinfo.protocol.SendMailRun;
import cn.com.phinfo.protocol.SendMailRun.SendMailResultBean;
import cn.com.phinfo.protocol.UnitandaddressRun.UnitandaddressItem;
import cn.com.phinfo.protocol.UploadLocalEmailRun;
import cn.com.phinfo.protocol.UploadUrlEmailRun;
import cn.qqtheme.framework.picker.FilePicker;
import cn.qqtheme.framework.picker.FilePicker.OnFilePickListener;

import com.album.PickOrTakeImageAct;
import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.adapterbase.MyAdapterBaseAbs;
import com.heqifuhou.hlistview.HorizontalListView;
import com.heqifuhou.imgutils.BitmapDataListInstanceUtils;
import com.heqifuhou.imgutils.FileItem;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.utils.ParamsCheckUtils;
import com.heqifuhou.utils.TakePhotosUtils;
import com.heqifuhou.view.FluidLayout;
import com.heqifuhou.view.PopupWindows;
import com.heqifuhou.view.PopupWindows.OnPopupWindowsItemListener;

//写邮件
public class CreateEmailAct extends HttpMyActBase implements OnClickListener {
	protected static int ID_NOTIFY = 0x10,ID_SENDEMAIL =0x11, ID_IMG_ZIP=0x12,ID_UPLOAD=0x13,ID_UPLOAD_URL=0x14;
	protected FluidLayout userList;
	protected EditText subname,content;
	protected View btnAttachList;
	protected HorizontalListView attacheList;
	protected ImgAdapter adapter = null;
	protected int Forward = 0;
	private PopupWindows delPop;
	//邮件批次发送ID，即发送返回的 sendBatchId
	protected String SendBatchId;
	protected int NTYPE = 1;//1发送邮件;Forward=2转发邮件;回复邮件3
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if(this.getIntent()!=null&&this.getIntent().getExtras()!=null){
			NTYPE = this.getIntent().getExtras().getInt("NTYPE");
		}
		super.onCreate(savedInstanceState);
		this.addTitleView(R.layout.nav_title_email_txt);
		this.addTextNav(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				sendMail();
			}
		}, "写邮件", "发送");
		this.addViewFillInRoot(R.layout.act_create_email);
		userList = (FluidLayout) this.findViewById(R.id.userList);
		subname = (EditText) this.findViewById(R.id.subname);
		content = (EditText) this.findViewById(R.id.content);
		btnAttachList = this.findViewById(R.id.btnAttachList);
		attacheList = (HorizontalListView) this.findViewById(R.id.attacheList);

		this.findViewById(R.id.btnAdd).setOnClickListener(this);
		this.findViewById(R.id.attacheAdd).setOnClickListener(this);
		this.findViewById(R.id.btnCreate1).setOnClickListener(this);
		this.findViewById(R.id.btnCreate2).setOnClickListener(this);
		this.findViewById(R.id.btnCreate3).setOnClickListener(this);
		this.findViewById(R.id.btnCreate4).setOnClickListener(this);

		adapter = new ImgAdapter();
		attacheList.setAdapter(adapter);
		attacheList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				FileItem it = adapter.getItem(arg2);
				delPopAction(it);
			}
		});
		adapter.replaceListRef(BitmapDataListInstanceUtils.getRefInstance()
				.getListRef());
		//回复
		if(NTYPE == 3){
			genTag();
		}
		//转发
		else if(NTYPE==2){
			EmailInfoResultBean emailInfo = DataInstance.getInstance().getEmailInfo();
			EmailAttacheResultBean emailAttacheList = DataInstance.getInstance().getEmailAttacheList();
			String emailContentTxt = DataInstance.getInstance().getEmailContentTxt();
			
			content.setText(emailContentTxt);
			subname.setText("转发:"+emailInfo.getData().getSubject());
			List<AttacheFileItem> ls = emailAttacheList.getListData();
			for(int i=0;i<ls.size();i++){
				AttacheFileItem it = ls.get(i);
				FileItem _it = JSON.parseObject(JSON.toJSONString(it),FileItem.class);
				_it.setIsLocalFile(false);
				BitmapDataListInstanceUtils.getRefInstance().add(_it);
				adapter.notifyDataSetChanged();
			}
			if(!ls.isEmpty()){
				showAttacheView();
			}
			
			Forward = 1;
		}
	}

	protected void onRefresh() {
	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if(id == ID_SENDEMAIL){
			if(obj.isOK()){
				SendMailResultBean o = (SendMailResultBean)obj;
				SendBatchId = o.getSendMailItem().getSendBatchId();
				if(!uploadLocalFile()){
					if(!uploadUrlFile()){
						showToast("发送成功!");
						this.finish();
					}
				}
			}
			else{
				showToast(obj.getMsg());
			}
			return;
		}
		//上传附件
		if(ID_UPLOAD == id){
			if(obj.isOK()){
				if(!uploadUrlFile()){
					showToast("发送成功!");
					this.finish();
				}
			}
			else{
				showToast(obj.getMsg());
			}
			return;
		}
		//上传云附件
		if(ID_UPLOAD_URL == id){
			if(obj.isOK()){
				showToast("发送成功!");
				this.finish();
			}else{
				showToast(obj.getMsg());
			}
			return;
		}
	}

	//本地服务
	private boolean uploadLocalFile(){
		List<FileItem> lst = BitmapDataListInstanceUtils.getRefInstance()
				.getListRef();
		IdentityHashMap<String, Object> has = new IdentityHashMap<String, Object>();
		for (int i = 0; i < lst.size(); i++) {
			FileItem it = lst.get(i);
			if(it.getIsLocalFile()){
				String key = new String("Attache[]");
				File f  = new File(it.getFileLocalPath());
				has.put(key, f);
			}
		}
		if(has.isEmpty()){
			return false;
		}
		quickHttpRequest(ID_UPLOAD, new UploadLocalEmailRun(SendBatchId,has));
		return true;
	}
	//云服务
	private boolean uploadUrlFile(){
		String fileids="";
		List<FileItem> lst = BitmapDataListInstanceUtils.getRefInstance()
				.getListRef();
		for (int i = 0; i < lst.size(); i++) {
			FileItem it = lst.get(i);
			if(!it.getIsLocalFile()){
				if(!ParamsCheckUtils.isNull(fileids)){
					fileids +=",";
				}
				fileids +=it.getId();
			}
		}
		if(ParamsCheckUtils.isNull(fileids)){
			return false;
		}
		quickHttpRequest(ID_UPLOAD_URL, new UploadUrlEmailRun(SendBatchId,fileids));
		return true;
	}
	
	public void onDestroy() {
		super.onDestroy();
		BitmapDataListInstanceUtils.getRefInstance().clear();
		DataInstance.getInstance().getUnitandaddressItemList().clear();
		DataInstance.getInstance().setEmailInfo(null, null, "");
	}
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.attacheAdd:
			showAttacheView();
			break;
		case R.id.btnAdd:
			startSelectPersionAct();
			break;
		case R.id.btnCreate1:
			pickPhoto();
			break;
		case R.id.btnCreate2:
			takePhoto();
			break;
		case R.id.btnCreate3:
			showFilePicker();
			break;
		case R.id.btnCreate4:
			
			break;
		}
	}

	private void genTag() {
		userList.removeAllViews();
		userList.setGravity(Gravity.TOP);
		List<UnitandaddressItem> tags = DataInstance.getInstance().getUnitandaddressItemList();
		for (int i = 0; i < tags.size(); i++) {
			UnitandaddressItem it = tags.get(i);
			TextView tv = new TextView(this);
			tv.setText(it.GET_USER_NAME());
			tv.setTextSize(13);
			tv.setBackgroundResource(R.drawable.text_bg);
			tv.setTextColor(Color.parseColor("#666666"));
			FluidLayout.LayoutParams params = new FluidLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			params.setMargins(12, 12, 12, 12);
			userList.addView(tv, params);
		}
	}
	
	private void sendMail(){
		String _subname = subname.getText().toString().trim();
		if(ParamsCheckUtils.isNull(_subname)){
			showToast("标题不能为空");
			return;
		}
		String toUserids="";
		List<UnitandaddressItem> tags = DataInstance.getInstance().getUnitandaddressItemList();
		for (int i = 0; i < tags.size(); i++) {
			UnitandaddressItem it = tags.get(i);
			if(i>0){
				toUserids+=",";
			}
			toUserids+=it.getSystemUserId();
		}
		if(tags.isEmpty()){
			showToast("收件人不能为空");
			return;
		}

		int priority =0;
		int emailStatus =1;
		this.quickHttpRequest(ID_SENDEMAIL, new SendMailRun(_subname,content.getText().toString().trim(),toUserids,emailStatus,priority,Forward));
	}

	private void startSelectPersionAct() {
		Intent intent = new Intent(this, SelectPersonAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intent, ID_NOTIFY);
	}

	public void pickPhoto() {
		Intent intent = new Intent(this, PickOrTakeImageAct.class);
		startActivity(intent);
	}

	private static final int TAKE_PICTURE = 0x156;
	private String path = "";

	public void takePhoto() {
		path = TakePhotosUtils.takePhoto(this, TAKE_PICTURE);
	}

	private FilePicker picker;

	private void showFilePicker() {
		if (picker != null && picker.isShowing()) {
			return;
		}
		FilePicker picker = new FilePicker(this, FilePicker.FILE);
		picker.setShowHideDir(false);
		picker.setOnFilePickListener(new OnFilePickListener() {
			@Override
			public void onFilePicked(String arg0) {
				FileItem it = new FileItem();
				it.setFileLocalPath(arg0);
				it.setIsLocalFile(true);
				BitmapDataListInstanceUtils.getRefInstance().add(it);
				adapter.notifyDataSetChanged();
			}
		});
		// picker.setAllowExtensions(new String[]{".apk"});
		picker.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == TAKE_PICTURE) {
			FileItem it = new FileItem();
			it.setFileLocalPath(path);
			it.setIsLocalFile(true);
			BitmapDataListInstanceUtils.getRefInstance().add(it);
			adapter.notifyDataSetChanged();
			// 显示
			return;
		}
		if (ID_NOTIFY == requestCode) {
			genTag();
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	protected void showAttacheView() {
		if (btnAttachList.getVisibility() == View.VISIBLE) {
			btnAttachList.setVisibility(View.GONE);
		} else {
			btnAttachList.setVisibility(View.VISIBLE);
		}
	}

	protected void onBroadcastReceiverListener(Context context, Intent intent) {
		if (IBroadcastAction.ACTION_SEL_PHOTOS.equals(intent.getAction())) {
			adapter.notifyDataSetChanged();
			return;
		}
	};

	protected class ImgAdapter extends MyAdapterBaseAbs<FileItem> {
		public class Holder {
			ImageView image;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			Holder viewHolder = null;
			if (arg1 == null) {
				viewHolder = new Holder();
				arg1 = LayoutInflater.from(arg2.getContext()).inflate(
						R.layout.item_email_item, null);
				viewHolder.image = (ImageView) arg1
						.findViewById(R.id.item_grida_image);
				arg1.setTag(viewHolder);
			} else {
				viewHolder = (Holder) arg1.getTag();
			}
			AttacheFileItem f = this.getItem(arg0);
			viewHolder.image.setImageResource(f.getImgResId());
			return arg1;
		}
	}
	
	private void delPopAction(final FileItem it){
		if(delPop!=null&&delPop.isShowing()){
			return;
		}
		delPop = new PopupWindows(this, this.findViewById(R.id.root), new String[] { "删除","查看"});
		delPop.show();
		delPop.setOnPopupWindowsItemListener(new OnPopupWindowsItemListener() {
			@Override
			public void onPopupWindowsItem(int pos) {
				//删除
				if(pos==0){
					BitmapDataListInstanceUtils.getRefInstance().Del(it);
					adapter.notifyDataSetChanged();
					return;
				}
				//查看
				if(pos == 1){
					showFile(it);
				}
			}
		});
	}
	
	private void showFile(final FileItem it){
		String s = JSON.toJSONString(it);
		Intent intent = new Intent(this, FileShowAct.class);
		intent.putExtra("AttacheFileItem",s );
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}
