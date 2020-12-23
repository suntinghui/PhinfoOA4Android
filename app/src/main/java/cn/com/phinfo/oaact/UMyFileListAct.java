package cn.com.phinfo.oaact;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import cn.com.phinfo.oaact.base.UMyCanMoveFileListBaseAct;
import cn.com.phinfo.protocol.AttacheFileRun.AttacheFileItem;
import cn.com.phinfo.protocol.FileSearchRun.UFileItem;
import cn.com.phinfo.protocol.LURLInterface;
import cn.qqtheme.framework.picker.FilePicker;
import cn.qqtheme.framework.picker.FilePicker.OnFilePickListener;

import com.album.PickOrTakeImageAct;
import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.imgutils.BitmapDataListInstanceUtils;
import com.heqifuhou.imgutils.FileItem;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;
import com.heqifuhou.pulltorefresh.PullToRefreshListView;
import com.heqifuhou.utils.TakePhotosUtils;
import com.heqifuhou.view.PopupWindows;
import com.heqifuhou.view.PopupWindows.OnPopupWindowsItemListener;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class UMyFileListAct extends UMyCanMoveFileListBaseAct {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BitmapDataListInstanceUtils.getRefInstance().clear();
		mList.setMode(Mode.DISABLED);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		BitmapDataListInstanceUtils.getRefInstance().clear();
	}

	protected void init() {
		this.addTitleView(R.layout.nav_white_btn);
		this.addTextNav(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				popAction();
			}
		}, title, R.drawable.u_upload_f);
		this.addViewFillInRoot(R.layout.act_myufile);
		mList = (PullToRefreshListView) this.findViewById(R.id.refreshListView);
		this.findViewById(R.id.createBtn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(UMyFileListAct.this,
						UDirCreateAct.class);
				intent.putExtra("parentID", folderid);
				startActivity(intent);
			}
		});
	}

	private PopupWindows popMenu;

	private void popAction() {
		if (popMenu != null && popMenu.isShowing()) {
			return;
		}
		popMenu = new PopupWindows(this, this.findViewById(R.id.root),
				new String[] { "选择照片", "拍照", "选择文件" });
		popMenu.show();
		popMenu.setOnPopupWindowsItemListener(new OnPopupWindowsItemListener() {
			@Override
			public void onPopupWindowsItem(int pos) {
				// 照片
				if (pos == 0) {
					pickPhoto();
					return;
				}
				// "拍照"
				if (pos == 1) {
					takePhoto();
					return;
				}
				// 文件
				if (pos == 2) {
					showFilePicker();
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == TAKE_PICTURE) {
			UFileItem it = new UFileItem();
			it.setMyFileLocalPath(path);
			it.setIsLocalFile(true);
			adapter.addToListHead(it);
			// 显示
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
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
				UFileItem it = new UFileItem();
				it.setMyFileLocalPath(arg0);
				it.setIsLocalFile(true);
				adapter.addToListHead(it);
			}
		});
		picker.show();
	}

	protected void onResume() {
		super.onResume();
		mHandler.sendEmptyMessage(0);
	}

	protected void onStop() {
		super.onStop();
		if (mHandler.hasMessages(0)) {
			mHandler.removeMessages(0);
		}
	}

	private Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			adapter.notifyDataSetChanged();
			mHandler.sendEmptyMessageDelayed(0, 2000);
		}
	};

	protected void onBroadcastReceiverListener(Context context, Intent intent) {
		if (IBroadcastAction.ACTION_SEL_PHOTOS.equals(intent.getAction())) {
			List<FileItem> ls = BitmapDataListInstanceUtils.getRefInstance().getListRef();
			for(FileItem fit:ls){
				UFileItem it = new UFileItem();
				it.setMyFileLocalPath(fit.getFileLocalPath());
				it.setIsLocalFile(true);
				adapter.addToListHead(it);
			}
			BitmapDataListInstanceUtils.getRefInstance().clear();
		}
		if(IBroadcastAction.ACTION_U_CREATE.equals(intent.getAction())){
			String s = intent.getExtras().getString("UFileItem");
			UFileItem it = JSON.parseObject(s, UFileItem.class);
			adapter.addToListHead(it);
		}
	};
	@Override
	public void onUFileItemListener(UFileItem it) {
		// 本地文件
		if (it.getIsLocalFile()) {
			// it.initStartUploadUrl(LURLInterface.GET_URL_FILE_UPLOAD_CREATE(folderid));
			// it.toggle();
		} else {
			if (it.isFloder()) {
				dirPopAction(it);
			} else {
				filePopAction(it);
			}
		}
	};


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		final UFileItem it = adapter.getItem(arg2 - 1);
		if (it.getIsLocalFile()) {
			it.setOnAttacheUploadFileItemListener(new RequestCallBack<String>() {
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					showToast(it.getName() + "上传失败");
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					showToast(it.getName() + "上传成功");
					adapter.tryRemove(it);
					page = 1;
					onRefresh();
				}
			});
			it.uploadToggle(LURLInterface.GET_URL_FILE_UPLOAD_CREATE(folderid));
			adapter.notifyDataSetChanged();
			return;
		} else {
			if (it.isFloder()) {
				Intent intent = new Intent(this, UMyFileListAct.class);
				intent.putExtra("srchType", srchType);
				intent.putExtra("title", title);
				intent.putExtra("folderid", it.getId());
				this.startActivity(intent);
			} else {
				Intent intent = new Intent(this, FileShowAct.class);
				intent.putExtra("AttacheFileItem",
						JSON.toJSONString(AttacheFileItem.init(it)));
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		}
	}
}
