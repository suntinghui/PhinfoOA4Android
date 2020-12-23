package cn.com.phinfo.entity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import android.os.Environment;

import cn.com.phinfo.oaact.R;
import cn.com.phinfo.protocol.LURLInterface;

import com.alibaba.fastjson.annotation.JSONField;
import com.heqifuhou.utils.MD5Utils;
import com.heqifuhou.utils.ParamsCheckUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public abstract class AttacheFileItemBase {
	@JSONField(serialize = false)
	private HttpHandler handler = null;
	@JSONField(serialize = false)
	private RequestCallBack<File> downloadListener;
	@JSONField(serialize = false)
	private RequestCallBack<String> uploadListener;
	private int progress=0;
	public int getProgress() {
		return progress;
	}
	public void setProgress(int progress) {
		this.progress = progress;
	}
	@JSONField(serialize = false)
	public boolean isPic() {
		if ("jpg".equals(this._getFileExtension())
				|| "png".equals(this._getFileExtension())
				|| "jpeg".equals(this._getFileExtension())
				|| "bmp".equals(this._getFileExtension())
				|| "gif".equals(this._getFileExtension())) {
			return true;
		}
		return false;
	}
	@JSONField(serialize = false)
	public boolean isFloder(){
		return ParamsCheckUtils.isNull(this._getFileExtension());
	}
	@JSONField(serialize = false)
	public boolean isDoc() {
		if ("doc".equals(this._getFileExtension())
				|| "docx".equals(this._getFileExtension())) {
			return true;
		}
		return false;
	}
	@JSONField(serialize = false)
	public boolean isXls() {
		if ("xls".equals(this._getFileExtension())
				|| "xlsx".equals(this._getFileExtension())) {
			return true;
		}
		return false;
	}
	@JSONField(serialize = false)
	public boolean isPpt() {
		if ("ppt".equals(this._getFileExtension())
				|| "pptx".equals(this._getFileExtension())) {
			return true;
		}
		return false;
	}
	@JSONField(serialize = false)
	public boolean isTxt() {
		if ("txt".equals(this._getFileExtension())) {
			return true;
		}
		return false;
	}
	@JSONField(serialize = false)
	public boolean isPdf() {
		if ("pdf".equals(this._getFileExtension())) {
			return true;
		}
		return false;
	}
	@JSONField(serialize = false)
	protected HttpUtils http = new HttpUtils(60000);
	@JSONField(serialize = false)
	public void startDownLoad() {
		handler = http.download(_getLink(), getLocalPath(), true, false,
				new RequestCallBack<File>() {
					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
						setProgress((int) (current*100 / Math.max(1,total)));
						if (downloadListener != null) {
							downloadListener.onLoading(total, current, isUploading);
						}
					}

					@Override
					public void onSuccess(ResponseInfo<File> responseInfo) {
						setProgress(100);
						if (downloadListener != null) {
							downloadListener.onSuccess(responseInfo);
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						if (downloadListener != null) {
							downloadListener.onFailure(error, msg);
						}
					}
				});
	}
	@JSONField(serialize = false)
	private int bRun = 1;//0 成功 1未开始 2正在上传 3，失败,4停止
	@JSONField(serialize = false)
	public int isRunType(){
		return this.bRun;
	}
	
	@JSONField(serialize = false)
	public void uploadToggle(final String url){
		if(handler == null){
			startUpload(url);
			return;
		}else{
			stop();
		}
//		if(handler.isPaused()){
//			handler.resume();
//			bRun = true;
//		}else{
//			handler.pause();
//			bRun = false;
//		}
	}
	@JSONField(serialize = false)
	private String _localUrl;
	//上传
	@JSONField(serialize = false)
	public void startUpload(final String url){
		_localUrl = url;
		RequestParams params = new RequestParams();
        params.addBodyParameter("file",new File(_getFileLocalPath()));//这里才是重点上传文件的参数
        handler = http.send(HttpMethod.POST, _localUrl, params, new RequestCallBack<String>() {
			@Override
			public void onLoading(long total, long current,
					boolean isUploading) {
				setProgress((int) (current*100 / Math.max(1,total)));
				if(uploadListener!=null){
					uploadListener.onLoading(total, current, isUploading);
				}
			}
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
            	bRun=0;
            	if(uploadListener!=null){
            		uploadListener.onSuccess(responseInfo);
            	}
            }
            @Override
            public void onFailure(HttpException e, String s) {
            	bRun = 3;
            	if(uploadListener!=null){
            		uploadListener.onFailure(e, s);
            	}
            }  
        });
        bRun = 2;
	}
	@JSONField(serialize = false)
	public void pause() {
		if (handler != null) {
			handler.pause();
		}
	}
	
	@JSONField(serialize = false)
	public void stop() {
		if (handler != null) {
			handler.cancel();
			handler = null;
			bRun = 4;
		}
	}

	@JSONField(serialize = false)
	public void setOnAttacheFileItemListener(RequestCallBack<File> l) {
		this.downloadListener = l;
	}
	
	@JSONField(serialize = false)
	public void setOnAttacheUploadFileItemListener(RequestCallBack<String> l) {
		this.uploadListener = l;
	}

	@JSONField(serialize = false)
	public String getLocalPath() {
		String encode = MD5Utils.md5(_getLink());
		String extension = _getFileExtension();
		final String sfile = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ File.separator
				+ "FileDownloader"
				+ File.separator + encode + "." + extension;
		return sfile;
	}

	@JSONField(serialize = false)
	public StringBuilder local2SB() {
		StringBuilder result = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(
					this.getLocalPath())));// 构造一个BufferedReader类来读取文件
			String s = null;
			while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
			// result.append(System.lineSeparator()+s);
				result.append(s);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	@JSONField(serialize = false)
	protected abstract String _getLink();
	@JSONField(serialize = false)
	protected abstract String _getFileExtension();
	@JSONField(serialize = false)
	protected abstract String _getFileLocalPath();
	@JSONField(serialize = false)
	public int getImgResId(){
		if(isDoc()){
			return R.drawable.file_doc;
		}else if(isPic()){
			return R.drawable.file_pic;
		}else if(isXls()){
			return R.drawable.file_xls;
		}else if(isPdf()){
			return R.drawable.file_pdf;
		}else if(isPpt()){
			return R.drawable.file_ppt;
		}else if(isTxt()){
			return R.drawable.file_txt;
		}
		else if(isFloder()){
			return R.drawable.ic_floder;
		}
		
		return R.drawable.file_unkonwn;
	}
	
	@JSONField(serialize = false)
   public static String getExtensionName(String filename) {    
        if ((filename != null) && (filename.length() > 0)) {    
            int dot = filename.lastIndexOf('.');    
            if ((dot >-1) && (dot < (filename.length() - 1))) {    
                return filename.substring(dot + 1);    
            }    
        }    
        return filename;    
    }
	
	@JSONField(serialize = false)
	protected String getFileName(String pathandname) {
		int start = pathandname.lastIndexOf("/");
		if (start != -1) {
			return pathandname.substring(start + 1);
		} else {
			return null;
		}
	}
}
