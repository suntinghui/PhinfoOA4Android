package com.heqifuhou.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.MyActBase;
import com.heqifuhou.view.PopupDialog;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.ValueCallback;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;

import cn.com.phinfo.oaact.FileShowAct;
import cn.com.phinfo.protocol.AttacheFileRun;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class FileUtils {

    public static void downloadAndOpenFile(MyActBase ctx, @NotNull AttacheFileRun.AttacheFileItem item) {
        String suffix = item.getFileExtension();
        if (suffix.equalsIgnoreCase("jpg") || suffix.equalsIgnoreCase("png") || suffix.equalsIgnoreCase("jpeg") || suffix.equalsIgnoreCase("gif") || suffix.equalsIgnoreCase("bmp") ) {
            openImg(ctx, item);
        } else {
            openOtherFiles(ctx, item.getLink(), item.getName()+"."+item.getFileExtension());
        }
    }

    // 图片单独打开
    public static void openImg(MyActBase ctx, AttacheFileRun.AttacheFileItem item) {
        /**
        Intent intent = new Intent(ctx, FileShowAct.class);
        intent.putExtra("AttacheFileItem", JSON.toJSONString(item));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ctx.startActivity(intent);
         **/
        
    }

    public static void openOtherFiles(MyActBase ctx, String url, String fileName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        String path = file.getPath();

        FileDownloader.setup(ctx);

        FileDownloader.getImpl().create(url)
                .setPath(path)
                .setListener(new FileDownloadLargeFileListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                        ctx.showLoading();
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {

                    }

                    @Override
                    protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {

                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        ctx.hideLoading();

                        int openType = FileUtils.openFile(ctx, path);
                        Log.e("yao9", openType+"");

                        if (openType == 3) {
                            FileUtils.popUpdateTips(ctx);
                        }

                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {

                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {

                    }
                }).start();
    }

    public static int openFile(MyActBase ctx, String path) {
        HashMap<String,String> params = new HashMap<>();
        //“0”表示文件查看器使用默认的UI 样式。“1”表示文件查看器使用微信的UI 样式。不设置此key或设置错误值，则为默认UI 样式。
        params.put("style","1");
        //“true”表示是进入文件查看器，如果不设置或设置为“false”，则进入miniqb 浏览器模式。不是必须设置项
        params.put("local","true");
        //定制文件查看器的顶部栏背景色。格式为“#xxxxxx”，例“#2CFC47”;不设置此key 或设置错误值，则为默认UI 样式。
        params.put("topBarBgColor","#45B0E8");
        int type = QbSdk.openFileReader(ctx, path, params, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
                Log.e("yao8",s);
            }
        });

        /**
         * 1：用 QQ 浏览器打开
         * 2：用 MiniQB 打开
         * 3：调起阅读器弹框
         * -1：filePath 为空 打开失败
         */
        return type;
    }

    public static void popUpdateTips(MyActBase ctx) {
        try {
            new SweetAlertDialog(ctx, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("温馨提示")
                    .setContentText("预览文件需要QQ浏览器内核支持，强烈建议您安装QQ浏览器")
                    .setConfirmText("知道了")
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
