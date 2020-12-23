package com.heqifuhou.netbase;


import java.util.HashMap;
import java.util.Map;
public class MyStaticHttpPost {	
	public static byte[] postStream(final String hostURL,final Map<String,String> headParams ,final byte[] szByte) {
		MyHttpPost post = new MyHttpPost();
		if(post!=null){
			return post.postByteToByte(hostURL, headParams, szByte);
		}
		return null;
	}
	
	
	static public byte[] postJsonStream(final String hostURL,final byte[] szByte)
	{
		Map<String, String> headParams = new HashMap<String, String>();
		headParams.put("Content-Type", "application/json");
		return postStream(hostURL,headParams, szByte);
	}
	
	static public byte[] postOctStream(final String hostURL,final byte[] szByte)
	{
		Map<String, String> headParams = new HashMap<String, String>();
		headParams.put("Content-Type", "application/oct-stream");
		return postStream(hostURL,headParams, szByte);
	}
//	public static byte[] postFileAndText(final String hostURL,final Map<String, String> part,String fileUploadName,File file)
//	{
//		Map<String,File> f = new HashMap<String,File>();
//		f.put(fileUploadName, file);
//		return postFileAndText(hostURL,part,f);
//	}
//	public static byte[] postFileAndText(final String hostURL,final Map<String, String> part,final Map<String,File> file)
//	{
//		MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
//		for (Map.Entry<String, String> entry : part.entrySet()) {
//			try {
//				//所有的数据都转成utf8
//				reqEntity.addPart(entry.getKey(),new StringBody(entry.getValue(),Charset.forName("UTF-8")));
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
//		}
//		for (Map.Entry<String, File> entry : file.entrySet()) {
//			ContentBody cbFile = new FileBody(entry.getValue());
//			reqEntity.addPart(entry.getKey(),cbFile);
//		}
//		MyHttpPost post = new MyHttpPost();
//		Map<String, String> headParams = new HashMap<String, String>();
//		if(post!=null){
//			return post.postEntityToByte(hostURL, headParams, reqEntity);
//		}
//		return null;
//	}
//	public static byte[] postStreamAndText(final String hostURL,final Map<String, String> part,String fileUploadName,ContentBody contentBody)
//	{
//		MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
//		for (Map.Entry<String, String> entry : part.entrySet()) {
//			try {
//				//所有的数据都转成utf8
//				reqEntity.addPart(entry.getKey(),new StringBody(entry.getValue(),Charset.forName("UTF-8")));
//			} catch (UnsupportedEncodingException e) {
//			}
//		}
//		if(contentBody!=null){
//			reqEntity.addPart(fileUploadName,contentBody);
//		}
//		MyHttpPost post = new MyHttpPost();
//		Map<String, String> headParams = new HashMap<String, String>();
//		if(post!=null){
//			return post.postEntityToByte(hostURL, headParams, reqEntity);
//		}
//		return null;
//	}
}
