package com.heqifuhou.imgutils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.actbase.MyActBase;
import com.heqifuhou.adapterbase.MyImgAdapterBaseAbs;
import com.heqifuhou.imgutils.AlbumHelper.ImageItem;
import com.heqifuhou.imgutils.ImageGridAct.ImageGridAdapter.TextCallback;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.phinfo.oaact.R;

public class ImageGridAct extends MyActBase {
	public static final String EXTRA_IMAGE_LIST = "imagelist";
	private List<ImageItem> dataList;
	private  GridView gridView;
	private ImageGridAdapter adapter;
	private TextView bt;

	private  final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0){
				showToast("最多选择"+PopMenuUtils.nPicMax+"张图片");
			}
		}
	};

	protected void onStop(){
		super.onStop();
		//这个单例没有用了，干掉吧。
		AlbumHelper.getInstance().setNull();
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_image_grid);
		int position = this.getIntent().getExtras().getInt(PicAct.EXTRA_IMAGE_LIST);
		AlbumHelper.getInstance().init(getApplicationContext());
		dataList = AlbumHelper.getInstance().getImagesBucketList(false).get(position).imageList;	
		initView();
	
		bt = (TextView) findViewById(R.id.bt);
		bt.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				HashMap<String,FileItem> hash = adapter.getSelHashMapRef();
				Collection<FileItem> c = hash.values();
				Iterator<FileItem> it = c.iterator();
				for (; it.hasNext();) {
					FileItem _it = it.next();
					_it.setIsLocalFile(true);
					BitmapDataListInstanceUtils.getRefInstance().add(_it);
		     	}
				finish();
				//发送选中照片
				sendBroadcast(new Intent(IBroadcastAction.ACTION_SEL_PHOTOS));
			}

		});
	}
	private void initView() {
		gridView = (GridView) findViewById(R.id.gridview);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new ImageGridAdapter(ImageGridAct.this, dataList,
				mHandler);
		gridView.setAdapter(adapter);
		adapter.setTextCallback(new TextCallback() {
			public void onListen(int count) {
				bt.setText("完成" + "(" + count + ")");
			}
		});
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				adapter.notifyDataSetChanged();
			}
		});

	}
	
	public static class ImageGridAdapter extends MyImgAdapterBaseAbs<ImageItem> {

		private TextCallback textcallback = null;
		private Activity act;
		private HashMap<String,FileItem> mList = new HashMap<String,FileItem>();
		private Handler mHandler;
		public HashMap<String,FileItem> getSelHashMapRef(){
			return mList;
		}
		public static interface TextCallback {
			public void onListen(int count);
		}

		public void setTextCallback(TextCallback listener) {
			textcallback = listener;
		}

		public ImageGridAdapter(Activity act, List<ImageItem> list, Handler mHandler) {
			this.mHandler = mHandler;
			this.act = act;
			this.replaceListRef(list);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final Holder holder;
			if (convertView == null) {
				holder = new Holder();
				convertView = View.inflate(act, R.layout.item_image_grid, null);
				holder.iv = (ImageView) convertView.findViewById(R.id.image);
				holder.selected = (ImageView) convertView
						.findViewById(R.id.isselected);
				holder.text = (TextView) convertView
						.findViewById(R.id.item_image_grid_text);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			final ImageItem item = this.getItem(position);
			holder.iv.setTag(item.imagePath);
			this.getAsyncBitMap(holder.iv,item.getThumbnailPath());
			if (item.isSelected) {
				holder.selected.setImageResource(R.drawable.zsht_success);  
				holder.text.setBackgroundResource(R.drawable.bgd_relatly_line);
			} else {
				holder.selected.setImageResource(-1);
				holder.text.setBackgroundColor(0x00000000);
			}
			holder.iv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int size = BitmapDataListInstanceUtils.getRefInstance().getListRef().size();
					if(size+mList.size()<PopMenuUtils.nPicMax){
						item.isSelected = !item.isSelected;
						if(item.isSelected){
							FileItem it = new FileItem();
							it.setFileName(item.imageName);
							it.setFileLocalPath(item.imagePath);
							mList.put(item.imagePath, it);
							
							holder.selected
							.setImageResource(R.drawable.zsht_success);
						    holder.text.setBackgroundResource(R.drawable.bgd_relatly_line);
						}else{
							mList.remove(item.imagePath);
							holder.selected.setImageResource(-1);
							holder.text.setBackgroundColor(0x00000000);	
						}
						if (textcallback != null){
						    textcallback.onListen(mList.size());
						}
					}else{
						Message message = Message.obtain(mHandler, 0);
						message.sendToTarget();
					}
				}

			});

			return convertView;
		}

		private class Holder {
			private ImageView iv;
			private ImageView selected;
			private TextView text;
		}
	}
}
