package cn.com.phinfo.oaact;

import java.io.File;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

public class FileShowAct extends HttpMyActBase implements OnClickListener {
	private FileItem item;
	private DragImageView mView;
	private LinearLayout content;
	private TextView errTxt,name,txt;
	private View progress;
	private ProgressBar file_progress_bar;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String s = this.getIntent().getExtras().getString("AttacheFileItem");
		if (!ParamsCheckUtils.isNull(s)) {
			item = JSON.parseObject(s, FileItem.class);
		}
		this.addTextNav("文件查看详情");
		this.addViewFillInRoot(R.layout.act_fileshow);
		content = (LinearLayout) this.findViewById(R.id.content);
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
			showError();
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
			
		}
		return null;
    }


	
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
	}
}
