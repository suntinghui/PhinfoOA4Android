package com.heqifuhou.imgutils;

import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import cn.com.phinfo.oaact.MyApplet;
import cn.com.phinfo.protocol.AttacheFileRun.AttacheFileItem;
import cn.com.phinfo.protocol.FileSearchRun.UFileItem;

import com.alibaba.fastjson.annotation.JSONField;
import com.heqifuhou.utils.BitmapUtil;
import com.heqifuhou.utils.ImageCompress;
import com.heqifuhou.utils.MD5Utils;
import com.heqifuhou.utils.ParamsCheckUtils;

public class FileItem extends AttacheFileItem {
	private String fileId;// 文件ID
	private String fileUrl;// 文件网络地址
	private String fileName;// 文件名
	private boolean isLocalFile=false;
	public boolean getIsLocalFile() {
		return isLocalFile;
	}
	public void setIsLocalFile(boolean isLocalFile) {
		this.isLocalFile = isLocalFile;
	}

	public String getFileId() {
		return fileId;
	}
	
	@JSONField(serialize=false)
	public boolean isEquals(FileItem it){
		if(fileId!=null){
			if(!fileId.equals(it.getFileId())){
				return false;
			}
		}
		if(fileUrl!=null){
			if(!fileUrl.equals(it.getFileUrl())){
				return false;
			}
		}
		if(fileName!=null){
			if(!fileName.equals(it.getFileName())){
				return false;
			}
		}
		return true;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	// 本地的
	private String fileLocalPath;// 本地处理地址
	public String getFileLocalPath() {
		return fileLocalPath;
	}
	public void setFileLocalPath(String fileLocalPath) {
		setFileExtension(getExtensionName(fileLocalPath));
		this.fileLocalPath = fileLocalPath;
	}
	@JSONField(serialize = false)
	public String getLocalFileUri() {
		return "file:///" + fileLocalPath;
	}
	
	public boolean hasUpload() {
		return !TextUtils.isEmpty(fileUrl);
	}



	// 生成压缩文件
	@JSONField(serialize = false)
	private transient String zipFileLocalPath;

	// 生成压缩的本地文件
	@JSONField(serialize = false)
	public File getZipLocal2File(int maxW,int maxH) {
		// 是否压缩
		if (!ParamsCheckUtils.isNull(this.zipFileLocalPath)) {
			File f = new File(this.zipFileLocalPath);
			if (f.exists()) {
				return f;
			}
		}
		if (!hasSdcard()) {
			return null;
		}
		tryMakeDir();
		// 写在本地
		// 文件名
		String fileName = getFileName(this.getFileLocalPath());
		if (TextUtils.isEmpty(fileName)) {
			return null;
		}
		// 压缩
		byte[] b = this.getZipLocal2Byte(maxW,maxH);
		if (b == null) {
			return null;
		}
		try {
			String path = pathDir + fileName;
			File file =new File(path);
			if(!file.exists()){
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(b);
			fos.flush();
			fos.close();
			this.zipFileLocalPath = path;
			return new File(this.zipFileLocalPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 本地的照片
	@JSONField(serialize = false)
	private static String pathDir = Environment.getExternalStorageDirectory()+File.separator
			+ MD5Utils.md5(MyApplet.getInstance().getPackageName())+File.separator;

	@JSONField(serialize = false)
	private static boolean tryMakeDir() {
		File tempFile = new File(pathDir);
		if (!tempFile.exists()) {
			boolean b = tempFile.mkdirs();
			return b;
		}
		return true;
	}

	@JSONField(serialize = false)
	private boolean hasSdcard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	// 压缩转成byte
	@JSONField(serialize = false)
	public byte[] getZipLocal2Byte(int maxW,int maxH) {
		try {
			Bitmap bit = ImageCompress.compressBitmap(this.fileLocalPath, maxW, maxH);
			return BitmapUtil.decodeToCompressSize(bit,300);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 压缩转成base64
	@JSONField(serialize = false)
	public String getZipLocal2Base64(int zipSize) {
		try {
			byte[] b = BitmapUtil.decodeToCompressSize(this.fileLocalPath,
					zipSize);
			return Base64.encodeToString(b, Base64.DEFAULT);
		} catch (Exception e) {
		}
		return null;
	}

	// 清除
	@JSONField(serialize = false)
	public static void clear() {
		tryMakeDir();
		delDir(new File(pathDir));
	}

	// 清除缓存
	@JSONField(serialize = false)
	private static void delDir(File f) {
		if (f.exists()&&f.isDirectory()) {
			for (File it : f.listFiles()) {
				if (it.isDirectory()) {
					delDir(it);
				} else {
					it.delete();
				}
			}
		}
	}
}
