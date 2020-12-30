package com.heqifuhou.protocolbase;

import com.heqifuhou.netbase.MyHttpPost;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MyStaticHttpPostMultipart {

   public static byte[] postFileAndText(final String hostURL, final Map<String, Object> datas, final Map<String, Object> files) {
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        multipartEntityBuilder.setMode(HttpMultipartMode.RFC6532);

       // 发送的数据
       try{
           if (datas != null) {
               Iterator iterator = datas.entrySet().iterator();
               while (iterator.hasNext()) {
                   Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
                   multipartEntityBuilder.addTextBody(entry.getKey(), entry.getValue(), ContentType.create("text/plain", Charset.forName("UTF-8")));
               }
           }
       } catch (Exception e) {
            e.printStackTrace();
       }

       // 发送的文件
        if (files != null) {
            for (Map.Entry<String, Object> entry : files.entrySet()) {

                if (entry.getValue() instanceof File) {
                    multipartEntityBuilder.addBinaryBody(entry.getKey(), (File) entry.getValue());

                } else if (entry.getValue() instanceof String) {
                    multipartEntityBuilder.addTextBody(entry.getKey(), (String)entry.getValue(), ContentType.create("text/plain", Charset.forName("UTF-8")));
                }
            }
        }

       HttpEntity httpEntity = multipartEntityBuilder.build();

        MyHttpPost post = new MyHttpPost();
        Map<String, String> headParams = new HashMap<String, String>();
        if (post != null) {
            return post.postEntityToByte(hostURL, headParams, httpEntity);
        }

        return null;
    }

}
