package cn.com.phinfo.oaact;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.IdentityHashMap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import cn.com.phinfo.entity.DataInstance;
import cn.com.phinfo.protocol.LURLInterface;
import cn.com.phinfo.protocol.UploadHeadImgRun;
import cn.com.phinfo.protocol.LoginRun.LoginResultBean;

import com.heqifuhou.actbase.HttpLoginMyActBase;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.utils.BitmapUtil;
import com.heqifuhou.utils.ParamsCheckUtils;
import com.heqifuhou.view.PhotoPopMenu;
import com.heqifuhou.view.PhotoPopMenu.OnPhotoDialogListener;
//个人头像
public class PersonPhoto extends HttpLoginMyActBase implements OnPhotoDialogListener{
	private final static int ID_Avator = 0x10;
	private PhotoPopMenu photoPopMenu;
	private ImageView photoView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState,false);
		this.addViewFillInRoot(R.layout.act_person);
		this.photoView = (ImageView) this.findViewById(R.id.photo);
		photoPopMenu = new PhotoPopMenu(this,this.photoView,this);
		this.photoView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				photoView.getViewTreeObserver().removeOnPreDrawListener(this);
				int width = photoView.getMeasuredWidth();
				ViewGroup.LayoutParams layoutParams = photoView.getLayoutParams();
				layoutParams.height = width;
				photoView.setLayoutParams(layoutParams);
				return true;
			}
		});
		this.addTextNav(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				photoPopMenu.showSelectDialog();
			}
		},"个人头像",R.drawable.more_white);
		LoginResultBean bean = DataInstance.getInstance().getUserBody() ;
		if(bean== null){
			return;
		}
		if(ParamsCheckUtils.isNull(bean.getNickname())||ParamsCheckUtils.isNull(bean.getNickname())){
			return;
		}
		this.getAsyncAvatar(this.photoView,LURLInterface.GET_AVATAR(bean.getUserid()),bean.getNickname());
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(photoPopMenu.onActivityResult(requestCode, resultCode, data)){
			return;
		}
		super.onActivityResult( requestCode,  resultCode,  data);
	}
	
	protected void onDestroy(){
		super.onDestroy();
		photoPopMenu.destory();
	}
	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if(ID_Avator==id){
			if(obj.isOK()){
				this.showToast("保存成功");
			}else{
				this.showToast(obj.getMsg());
			}
		}
		
	}

	@Override
	public void onPhotoDialog2Bitmap(final Bitmap bit) {
		this.photoView.setImageBitmap(bit);
		byte[] _bit =   BitmapUtil.decodeToCompressSize(bit,500);
		File f = this.getFileStreamPath("potho.jpg");
		FileOutputStream fi;
		try {
			if(f.exists()){
				f.delete();
			}
			fi = new FileOutputStream(f);
			fi.write(_bit);
			IdentityHashMap<String, Object> has = new IdentityHashMap<String, Object>();
			has.put("avatar", f);
			this.quickHttpRequest(ID_Avator, new UploadHeadImgRun(has));
			return;
		} catch (Exception e) {
			this.showToast("上传失败");
		}
//		this.showToast("上传失败");
//		final String potoBase64;
//		if(_bit == null){
//			potoBase64 = BitmapToBaseUtils.bitmapToBase64(bit);
//		}
//		else{
//			potoBase64 = Base64.encodeToString(_bit, Base64.DEFAULT);
//		}
	}
}
