package com.heqifuhou.update.base;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

public class DownloadUtil {
	private HttpURLConnection conn = null;
	private InputStream is = null;
	private BufferedInputStream bis = null;
	private FileOutputStream fos = null;
	// //////////////////////////////////////////////
	/** 下载文件总长度 */
	private int totalSize;
	/** 下载文件进度 */
	private int progress;
	/** 下载文件 */
	private File downFile = null;
	// 下载
	private boolean bError = false;
	// 下载URL
	private String url = "";

	public final void close() {
		try {
			if (fos != null) {
				fos.close();
				fos = null;
			}
		} catch (Exception e) {
		}
		try {
			if (bis != null) {
				bis.close();
				bis = null;
			}
		} catch (Exception e) {
		}
		try {
			if (is != null) {
				is.close();
				is = null;
			}
		} catch (Exception e) {
		}
		try {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		} catch (Exception e) {
		}
	}

	public final String getDownLoadUrl() {
		return this.url;
	}

	/**
	 * 文件下载
	 * 
	 * @param downUrl
	 *            下载链接
	 * @return 下载的文件
	 */
	public File downloadFile(String downUrl) {
		this.url = downUrl;
		bError = false;
		// 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			try {
				URL url = new URL(downUrl);
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(1000 * 5);
				totalSize = conn.getContentLength();
				if (totalSize <= 0) {
					bError = true;
					return null;
				}
				progress = 0;
				is = conn.getInputStream();
				String filename = downUrl
						.substring(downUrl.lastIndexOf("/") + 1);// 获得文件名
				downFile = new File(Environment.getExternalStorageDirectory(),
						filename);
				int i = 0;
				do {
					if (downFile.exists()) {
						downFile = new File(
								Environment.getExternalStorageDirectory(), ++i
										+ filename);
					} else {
						break;
					}
				} while (true);
				fos = new FileOutputStream(downFile);
				bis = new BufferedInputStream(is);
				byte[] buffer = new byte[64];
				int len;
				while ((len = bis.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
					progress += len;
				}
				fos.flush();
				fos.close();
				fos = null;
				bis.close();
				bis = null;
				is.close();
				is = null;
				conn.disconnect();
				conn = null;
			} catch (Exception e) {
				bError = true;
				return null;
			} finally {
				close();
			}
		}
		return downFile;
	}

	/**
	 * 安装APK文件
	 * 
	 * @param apkfile
	 *            APK文件名
	 * @param mContext
	 */
	public static void installApk(Context mContext, File apkFile) {
		if (!apkFile.exists()) {
			return;
		}
		chmodPermission(apkFile.getPath());
		// 通过Intent安装APK文件
		Intent install = new Intent();
		install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_NO_HISTORY
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		install.setAction(android.content.Intent.ACTION_VIEW);
		install.setDataAndType(Uri.fromFile(apkFile),
				"application/vnd.android.package-archive");
		mContext.startActivity(install);
	}

	private static void chmodPermission(final String path) {
		String permission = "666";
		try {
			String command = "chmod " + permission + " " + path;
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(command);
		} catch (IOException e) {
		}
	}

	/** 监听下载进度 */
	public void setOnDownloadListener(IOnDownloadListener listener) {
		listener.updateNotification(bError, progress, totalSize, downFile);
	}

	/**
	 * 监听接口
	 */
	public interface IOnDownloadListener {
		/**
		 * 更新下载进度
		 * 
		 * @param progress
		 *            下载进度值
		 * @param totalSize
		 *            文件总大小
		 * @param downFile
		 *            下载的文件
		 */
		public void updateNotification(boolean bError, int progress,
				int totalSize, File downFile);
	}

}
