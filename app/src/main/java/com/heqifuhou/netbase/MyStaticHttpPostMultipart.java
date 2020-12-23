package com.heqifuhou.netbase;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

public class MyStaticHttpPostMultipart {
	public static byte[] postFileAndText(final String hostURL,
			final Map<String, String> part, String fileUploadName, File file) {
		Map<String, File> f = new HashMap<String, File>();
		f.put(fileUploadName, file);
		return postFileAndText(hostURL, part, f);
	}

	public static byte[] postFileAndText(final String hostURL,
			final Map<String, String> part, final Map<String, File> file) {
		MultipartEntity reqEntity = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);
		for (Map.Entry<String, String> entry : part.entrySet()) {
			try {
				// 所有的数据都转成utf8
				reqEntity.addPart(
						entry.getKey(),
						new StringBody(entry.getValue(), Charset
								.forName("UTF-8")));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		for (Map.Entry<String, File> entry : file.entrySet()) {
			ContentBody cbFile = new FileBody(entry.getValue());
			reqEntity.addPart(entry.getKey(), cbFile);
		}
		MyHttpPost post = new MyHttpPost();
		Map<String, String> headParams = new HashMap<String, String>();
		if (post != null) {
			return post.postEntityToByte(hostURL, headParams, reqEntity);
		}
		return null;
	}

	public static byte[] postStreamAndText(final String hostURL,
			final Map<String, String> part, String fileUploadName,
			ContentBody contentBody) {
		MultipartEntity reqEntity = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);
		for (Map.Entry<String, String> entry : part.entrySet()) {
			try {
				// 所有的数据都转成utf8
				reqEntity.addPart(
						entry.getKey(),
						new StringBody(entry.getValue(), Charset
								.forName("UTF-8")));
			} catch (UnsupportedEncodingException e) {
			}
		}
		if (contentBody != null) {
			reqEntity.addPart(fileUploadName, contentBody);
		}
		MyHttpPost post = new MyHttpPost();
		Map<String, String> headParams = new HashMap<String, String>();
		if (post != null) {
			return post.postEntityToByte(hostURL, headParams, reqEntity);
		}
		return null;
	}

	public static byte[] postValuePairToByte(final String hostURL,
			ArrayList<BasicNameValuePair> postData) {
		// 对请求的数据进行UTF-8转码
		UrlEncodedFormEntity reqEntity;
		try {
			reqEntity = new UrlEncodedFormEntity(postData, HTTP.UTF_8);
			MyHttpPost post = new MyHttpPost();
			Map<String, String> headParams = new HashMap<String, String>();
			if (post != null) {
				return post.postEntityToByte(hostURL, headParams, reqEntity);
			}
		} catch (UnsupportedEncodingException e) {
		}

		return null;
	}
	
	public static byte[] postPairToByte(final String hostURL,
			final Map<String, String> part) {
		ArrayList<BasicNameValuePair> postData = new ArrayList<BasicNameValuePair>();
		for (Map.Entry<String, String> entry : part.entrySet()) {
			postData.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
		}
		return postValuePairToByte(hostURL,postData);
	}
	
//	public static byte[] postPairFileToByte(final String hostURL,
//			final Map<String, String> part,final Map<String, File> filemap) {
//		ArrayList<BasicNameValuePair> postData = new ArrayList<BasicNameValuePair>();
//		for (Map.Entry<String, String> entry : part.entrySet()) {
//			postData.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
//		}
//		for (Map.Entry<String, File> entry : filemap.entrySet()) {
//			postData.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
//		}
//		return postValuePairToByte(hostURL,postData);
//	}
}
