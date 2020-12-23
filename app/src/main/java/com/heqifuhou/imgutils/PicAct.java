package com.heqifuhou.imgutils;

import java.util.List;

import com.heqifuhou.actbase.MyActBase;
import com.heqifuhou.adapterbase.MyImgAdapterBaseAbs;
import com.heqifuhou.imgutils.AlbumHelper.ImageBucket;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.phinfo.oaact.R;

//选择照片时，所有的照片
public class PicAct extends MyActBase {
	private List<ImageBucket> dataList;
	private GridView gridView;
	private ImageBucketAdapter adapter;// 自定义的适配
	public static final String EXTRA_IMAGE_LIST = "imagelist";
	public static Bitmap bimap;

	/**
	 * 初始化数据
	 */
	private void initData() {
		dataList = AlbumHelper.getInstance().getImagesBucketList(false);	
		bimap=BitmapFactory.decodeResource(
				getResources(),
				R.drawable.icon_addpic);
	}

	/**
	 * 初始化view视图
	 */
	private void initView() {
		gridView = (GridView) findViewById(R.id.gridview);
		adapter = new ImageBucketAdapter();
		gridView.setAdapter(adapter);

		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(PicAct.this,
						ImageGridAct.class);
				intent.putExtra(PicAct.EXTRA_IMAGE_LIST,position);
				startActivity(intent);
				finish();
			}

		});
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addViewFillInRoot(R.layout.act_image_bucket);
		AlbumHelper.getInstance().init(getApplicationContext());
		initData();
		initView();
		if(gridView.getCount()<=0){
			View v = this.getLayoutInflater(R.layout.empty);
			TextView txt = (TextView) v.findViewById(R.id.empty);
			txt.setTextColor(0xff666666);
			txt.setText("没有图片");
			gridView.setEmptyView(v);
		}
		gridView.setVisibility(View.VISIBLE);
	}

	public class ImageBucketAdapter extends MyImgAdapterBaseAbs<ImageBucket> {
		public ImageBucketAdapter() {
			this.addToListBack(dataList);
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			Holder holder;
			if (arg1 == null) {
				holder = new Holder();
				arg1 = View.inflate(PicAct.this, R.layout.item_image_bucket, null);
				holder.iv = (ImageView) arg1.findViewById(R.id.image);
				holder.selected = (ImageView) arg1.findViewById(R.id.isselected);
				holder.name = (TextView) arg1.findViewById(R.id.name);
				holder.count = (TextView) arg1.findViewById(R.id.count);
				arg1.setTag(holder);
			} else {
				holder = (Holder) arg1.getTag();
			}
			ImageBucket item = dataList.get(arg0);
			holder.count.setText(Html.fromHtml(String.format("总数<font color=blue>%s</font>张",String.valueOf(item.imageList.size())+"")));
			holder.name.setText(item.bucketName);
			holder.selected.setVisibility(View.GONE);
			if (item.imageList != null && item.imageList.size() > 0) {
				this.getAsyncBitMap(holder.iv, item.imageList.get(0).getThumbnailPath());
			} else {
				holder.iv.setImageBitmap(null);
			}
			return arg1;
		}
		private class Holder {
			private ImageView iv;
			private ImageView selected;
			private TextView name;
			private TextView count;
		}
	}
}
