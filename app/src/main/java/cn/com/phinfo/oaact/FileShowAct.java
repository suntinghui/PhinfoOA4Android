package cn.com.phinfo.oaact;

import java.io.File;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.phinfo.utils.OpenFilesUtils;

import com.alibaba.fastjson.JSON;
import com.artifex.mupdf.MuPDFCore;
import com.artifex.mupdf.MuPDFPageAdapter;
import com.artifex.mupdf.ReaderView;
import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.imgutils.FileItem;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.utils.ParamsCheckUtils;
import com.heqifuhou.view.DragImageView;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.tencent.smtt.sdk.TbsReaderView;

public class FileShowAct extends HttpMyActBase implements OnClickListener, TbsReaderView.ReaderCallback {
	private FileItem item;
	private DragImageView mView;
	private LinearLayout content;
	private TextView errTxt,name,txt;
	private View progress;
	private ProgressBar file_progress_bar;

	private TbsReaderView mTbsReaderView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mTbsReaderView = new TbsReaderView(this, this);

		String s = this.getIntent().getExtras().getString("AttacheFileItem");
		if (!ParamsCheckUtils.isNull(s)) {
			item = JSON.parseObject(s, FileItem.class);
		}
		this.addTextNav("文件查看详情");
		this.addViewFillInRoot(R.layout.act_fileshow);
		content = (LinearLayout) this.findViewById(R.id.content);
		content.addView(mTbsReaderView, new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));

		errTxt = (TextView) this.findViewById(R.id.errTxt);
		txt =  (TextView) this.findViewById(R.id.txt);
		name = (TextView) this.findViewById(R.id.name);
		progress = this.findViewById(R.id.progress);
		file_progress_bar = (ProgressBar) this.findViewById(R.id.file_progress_bar);
		
		this.addBottomView(R.layout.imge_show_tools_bar);
		if (item.isPic()) {
			showImg();
		}else  if(item.isPdf()){
			showPDF();
		}else {
			//showError();
		}
		this.findViewById(R.id.otherBtn).setOnClickListener(this);
		this.findViewById(R.id.sendBtn).setOnClickListener(this);
		this.findViewById(R.id.shareBtn).setOnClickListener(this);
		this.findViewById(R.id.moreBtn).setOnClickListener(this);
		
		name.setText(item.getName());
	}
	
	private void showError(){
		errTxt.setVisibility(View.VISIBLE);
		progress.setVisibility(View.VISIBLE);
	}
	
	

	private View loadPDF(final String path){
		try{
			MuPDFCore core = null;
			try {
				core = new MuPDFCore(this,path);
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			ReaderView mListView = new ReaderView(this);
			MuPDFPageAdapter adapter = new MuPDFPageAdapter(this, core);
			mListView.setAdapter(adapter);
			return mListView;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
    }

	/**
	 * 加载显示文件内容
	 */
	private void displayFile(String filePath) {
		String fileName = item.getName()+"."+item.getFileExtension();

		Bundle bundle = new Bundle();
		bundle.putString("filePath", filePath);
		bundle.putString("tempPath", Environment.getExternalStorageDirectory()
				.getPath());
		boolean result = mTbsReaderView.preOpen(parseFormat(fileName), false);
		if (result) {
			mTbsReaderView.openFile(bundle);
		} else {
			File file = new File(getLocalFile(fileName).getPath());
			if (file.exists()) {
				Intent openintent = new Intent();
				openintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				String type = getMIMEType(file);
				// 设置intent的data和Type属性。
				openintent.setDataAndType(/* uri */Uri.fromFile(file), type);
				// 跳转
				startActivity(openintent);
				finish();
			}
		}
	}

	private String parseFormat(String fileName) {
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}

	private File getLocalFile(String fileName) {
		return new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
	}

	private String getMIMEType(File file) {

		String type = "*/*";
		String fName = file.getName();
		// 获取后缀名前的分隔符"."在fName中的位置。
		int dotIndex = fName.lastIndexOf(".");
		if (dotIndex < 0) {
			return type;
		}
		/* 获取文件的后缀名 */
		String end = fName.substring(dotIndex, fName.length()).toLowerCase();
		if (end == "")
			return type;
		// 在MIME和文件类型的匹配表中找到对应的MIME类型。
		for (int i = 0; i < MIME_MapTable.length; i++) {
			if (end.equals(MIME_MapTable[i][0]))
				type = MIME_MapTable[i][1];
		}
		return type;
	}

	private final String[][] MIME_MapTable = {
			// {后缀名，MIME类型}
			{".3gp", "video/3gpp"},
			{".apk", "application/vnd.android.package-archive"},
			{".asf", "video/x-ms-asf"},
			{".avi", "video/x-msvideo"},
			{".bin", "application/octet-stream"},
			{".bmp", "image/bmp"},
			{".c", "text/plain"},
			{".class", "application/octet-stream"},
			{".conf", "text/plain"},
			{".cpp", "text/plain"},
			{".doc", "application/msword"},
			{".docx",
					"application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
			{".xls", "application/vnd.ms-excel"},
			{".xlsx",
					"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
			{".exe", "application/octet-stream"},
			{".gif", "image/gif"},
			{".gtar", "application/x-gtar"},
			{".gz", "application/x-gzip"},
			{".h", "text/plain"},
			{".htm", "text/html"},
			{".html", "text/html"},
			{".jar", "application/java-archive"},
			{".java", "text/plain"},
			{".jpeg", "image/jpeg"},
			{".jpg", "image/jpeg"},
			{".js", "application/x-javascript"},
			{".log", "text/plain"},
			{".m3u", "audio/x-mpegurl"},
			{".m4a", "audio/mp4a-latm"},
			{".m4b", "audio/mp4a-latm"},
			{".m4p", "audio/mp4a-latm"},
			{".m4u", "video/vnd.mpegurl"},
			{".m4v", "video/x-m4v"},
			{".mov", "video/quicktime"},
			{".mp2", "audio/x-mpeg"},
			{".mp3", "audio/x-mpeg"},
			{".mp4", "video/mp4"},
			{".mpc", "application/vnd.mpohun.certificate"},
			{".mpe", "video/mpeg"},
			{".mpeg", "video/mpeg"},
			{".mpg", "video/mpeg"},
			{".mpg4", "video/mp4"},
			{".mpga", "audio/mpeg"},
			{".msg", "application/vnd.ms-outlook"},
			{".ogg", "audio/ogg"},
			{".pdf", "application/pdf"},
			{".png", "image/png"},
			{".pps", "application/vnd.ms-powerpoint"},
			{".ppt", "application/vnd.ms-powerpoint"},
			{".pptx",
					"application/vnd.openxmlformats-officedocument.presentationml.presentation"},
			{".prop", "text/plain"}, {".rc", "text/plain"},
			{".rmvb", "audio/x-pn-realaudio"}, {".rtf", "application/rtf"},
			{".sh", "text/plain"}, {".tar", "application/x-tar"},
			{".tgz", "application/x-compressed"}, {".txt", "text/plain"},
			{".wav", "audio/x-wav"}, {".wma", "audio/x-ms-wma"},
			{".wmv", "audio/x-ms-wmv"},
			{".wps", "application/vnd.ms-works"}, {".xml", "text/plain"},
			{".z", "application/x-compress"},
			{".zip", "application/x-zip-compressed"}, {"", "*/*"}};

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	protected void onRefresh() {

	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj, Object requestObj) {

	}

	private void showProgress(long current){
		long total = item.getSize();
		long n = (current*100/Math.max(total,1));
		file_progress_bar.setProgress((int)n);
		txt.setText(n+"%");
	}
	
	private void showImg(){
		if(item.getIsLocalFile()){
			mView = new DragImageView(FileShowAct.this);
			replaceFillInRoot(mView);
			Bitmap bit = BitmapFactory.decodeFile(item.getFileLocalPath());
			if(bit!=null){
				mView.setmDrawable(new BitmapDrawable(bit));
			}else{
				showError();
			}
		}else{
			item.setOnAttacheFileItemListener(new RequestCallBack<File>() {
				@Override
				public void onLoading(long total, long current, boolean isUploading) {
					showProgress(current);
				}

				@Override
				public void onSuccess(ResponseInfo<File> responseInfo) {
					mView = new DragImageView(FileShowAct.this);
					replaceFillInRoot(mView);
					Bitmap bit = BitmapFactory.decodeFile(item.getLocalPath());
					if(bit!=null){
						mView.setmDrawable(new BitmapDrawable(bit));
					}else{
						showToast("下载失败");
						showError();
					}
				}

				@Override
				public void onFailure(HttpException error, String msg) {
					showToast("下载失败");
					showError();
				}
			});
			item.startDownLoad();
		}

	}
	private void showPDF(){
		if(item.getIsLocalFile()){
			//取读本地文件
			View  mWebView = loadPDF(item.getFileLocalPath());
			if(mWebView==null){
				showError();
			}else{
				replaceFillInRoot(mWebView);
			}
		}else{
			item.setOnAttacheFileItemListener(new RequestCallBack<File>() {
				@Override
				public void onLoading(long total, long current, boolean isUploading) {
					showProgress(current);
				}

				@Override
				public void onSuccess(ResponseInfo<File> responseInfo) {
					//取读本地文件
					View  mWebView = loadPDF(item.getLocalPath());
					if(mWebView==null){
						showError();
					}else{
						replaceFillInRoot(mWebView);
					}
				}

				@Override
				public void onFailure(HttpException error, String msg) {
					hideLoading(true);
					showToast("下载失败");
					showError();
				}
			});
			item.startDownLoad();
		}

	}
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.otherBtn:
			showOther();
			break;
		case R.id.sendBtn:
			break;
		case R.id.shareBtn:
			showShareFileAct();
			break;
		case R.id.moreBtn:
			showToast("暂未实现");
			break;
		}
	}
	
	private void showShareFileAct(){
		Intent intent = new Intent(this,ShareFileAct.class);
		intent.putExtra("AttacheFileItem", JSON.toJSONString(item));
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}
	

	private void showOther() {
		if(item.getIsLocalFile()){
			showOtherAct(item.getFileLocalPath());
		}else{
			item.setOnAttacheFileItemListener(new RequestCallBack<File>() {
				@Override
				public void onLoading(long total, long current, boolean isUploading) {
					showProgress(current);
				}
				@Override
				public void onSuccess(ResponseInfo<File> responseInfo) {
					showOtherAct(item.getLocalPath());
				}
				@Override
				public void onFailure(HttpException error, String msg) {
					showToast("下载失败");
				}
			});
			item.startDownLoad();
		}
	}
	private void showOtherAct(final String path){
		displayFile(path);
		/**
		Intent intent = null;
		if(item.isDoc()){
			intent = OpenFilesUtils.getWordFileIntent(path);
		}else if(item.isPdf()){
			intent = OpenFilesUtils.getPdfFileIntent(path);
		}else if(item.isXls()){
			intent = OpenFilesUtils.getExcelFileIntent(path);
		}else if(item.isPic()){
			intent = OpenFilesUtils.getImageFileIntent(path);
		}else if(item.isPpt()){
			intent = OpenFilesUtils.getPPTFileIntent(path);
		}else{
			intent = new Intent("android.intent.action.VIEW");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Uri uri = Uri.fromFile(new File(path));
			intent.setDataAndType(uri, "application/msword");
		}
		if(intent!=null){
			startActivity(intent);
			this.finish();
		}
		 **/
	}

	@Override
	public void onCallBackAction(Integer integer, Object o, Object o1) {

	}
}
