package com.heqifuhou.imgutils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import cn.com.phinfo.oaact.R;

import com.album.PickOrTakeImageAct;
import com.heqifuhou.adapterbase.MyImgAdapterBaseAbs;
import com.heqifuhou.utils.TakePhotosUtils;
import com.heqifuhou.view.PopupWindows;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PopMenuUtils implements IPopMenuUtils {
	private boolean bTakePic = true;
	public static int nPicMax = Integer.MAX_VALUE;
	private Activity act = null;
	private View view;
	private ImgAdapter adapter;
	private PopupWindows pop=null;
	private GridView noScrollgridview;
	public PopMenuUtils(Activity _act) {
		this.act = _act;
		view = act.getLayoutInflater().inflate(R.layout.act_photo_layout, null);
		noScrollgridview = (GridView) view.findViewById(R.id.noScrollgridview);
		noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new ImgAdapter();
		noScrollgridview.setAdapter(adapter);
		adapter.replaceListRef(BitmapDataListInstanceUtils.getRefInstance()
				.getListRef());
		noScrollgridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (adapter.isImgShow(arg2)) {
					showDialog();
				} else {
					Intent intent = new Intent(act, PhotoAct.class);
					intent.putExtra("ID", arg2);
					act.startActivity(intent);
				}
			}
		});
	
	}

	public void setMaxPic(int nPic) {
		this.nPicMax = nPic;
	}
	
	public void setNumColumns(int numColumns){
		noScrollgridview.setNumColumns(numColumns);
	}

	public void showDialog(){

		if(bTakePic){
			int hasCount = BitmapDataListInstanceUtils.getRefInstance().getListRef().size();
			Intent intent = new Intent(act, PickOrTakeImageAct.class);
			intent.putExtra(PickOrTakeImageAct.EXTRA_NUMS, nPicMax-hasCount);
			act.startActivity(intent);
//			if(pop!=null&&pop.isShowing()){
//				return;
//			}
//			pop = new PopupWindows(act,view,new String[]{"拍照","从相册中选取"});
//			pop.show();
//			pop.setOnPopupWindowsItemListener(new OnPopupWindowsItemListener() {
//				@Override
//				public void onPopupWindowsItem(int pos) {
//					if(pos == 0){
//						takePhoto();
//					}else if(pos == 1){
//						pickPhoto();
//					}
//				}
//			});
		}
		else{
			takePhoto();
		}
	}

	public void takePic(boolean bTakePic){
		this.bTakePic = bTakePic;
	}
	public void pickPhoto() {
		Intent intent = new Intent(act, PicAct.class);
		act.startActivity(intent);
	}
	
	public View InitBitmapView() {
		return InitBitmapView(true);
	}
	public View InitBitmapView(boolean bTakePic) {
		this.bTakePic = bTakePic;
		return view;
	}

	public void onNotifyDataSetChanged() {
		adapter.notifyDataSetChanged();
	}

	private  class  ImgAdapter extends MyImgAdapterBaseAbs<FileItem>{
		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			Holder holder = null;
			if (arg1 == null) {
				holder = new Holder();
				arg1 = LayoutInflater.from(arg2.getContext()).inflate(
						R.layout.item_published_grida, null);
				holder.image = (ImageView) arg1.findViewById(R.id.item_grida_image);
				holder.btn_del = arg1.findViewById(R.id.btn_del);
				arg1.setTag(holder);
			} else {
				holder = (Holder) arg1.getTag();
			}
			holder.btn_del.setVisibility(View.GONE);
			if (arg0 < super.getCount()) {
				FileItem f = this.getItem(arg0);
				if (f.hasUpload()) {
					this.getAsyncBitMap(holder.image, f.getFileUrl(),
							R.drawable.default_img);
				} else {
					this.getAsyncBitMap(holder.image, f.getLocalFileUri(),
							R.drawable.default_img);
				}
				holder.btn_del.setVisibility(View.VISIBLE);
				holder.btn_del.setTag(f);
				holder.btn_del.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						FileItem ff = (FileItem) arg0.getTag();
						BitmapDataListInstanceUtils.getRefInstance().Del(ff);
						notifyDataSetChanged();
					}
				});
			} else {
				ImageLoader.getInstance().cancelDisplayTask(holder.image);
				holder.image.setImageResource(R.drawable.icon_addpic);
				if (arg0 == nPicMax) {
					holder.image.setVisibility(View.GONE);
				}
			}
			
			return arg1;
		}

		public int getCount() {
			if(super.getCount()>=nPicMax){
				return super.getCount();
			}
			return (super.getCount() + 1);
		}
		
		public boolean isImgShow(int idx){
			if(idx>=0&&idx<super.getCount()){
				return false;
			}
			return true;
		}
		
		public  class Holder{
			ImageView image;
			View btn_del ;
		} 
		
	};

	private static final int TAKE_PICTURE = 0x156;
	private String path = "";

	public void takePhoto() {
		path = TakePhotosUtils.takePhoto(act, TAKE_PICTURE);
	}

	public void onBitmapUtilsMyActivityResult(int requestCode, int resultCode,
			Intent data) {
		if(resultCode == Activity.RESULT_OK){
			switch (requestCode) {
			case TAKE_PICTURE:
				if (adapter.getCount() - 1 < nPicMax) {
					FileItem it = new FileItem();
					it.setFileLocalPath(path);
					adapter.addToListBack(it);
				}
				break;
			}	
		}

	}
}
