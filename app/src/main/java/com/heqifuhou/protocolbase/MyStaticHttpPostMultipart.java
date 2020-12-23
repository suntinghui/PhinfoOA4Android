package com.heqifuhou.protocolbase;

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

import com.heqifuhou.netbase.MyHttpPost;
import com.heqifuhou.utils.ParamsCheckUtils;

public class MyStaticHttpPostMultipart {
	public static byte[] postFileAndText(final String hostURL,
			final Map<String, Object> part, String fileUploadName, File file) {
		Map<String, Object> f = new HashMap<String, Object>();
		f.put(fileUploadName, file);
		return postFileAndText(hostURL, part, f);
	}

	private static void addMultipart(MultipartEntity reqEntity,Map<String, Object> part,final String key){
		for (Map.Entry<String, Object> entry : part.entrySet()) {
			try {
				String _key = "";
				if(ParamsCheckUtils.isNull(key)){
					_key = entry.getKey();
				}else{
					_key = String.format("%s[%s]", key,entry.getKey());
				}
				if(entry.getValue() instanceof String){
					// 所有的数据都转成utf8
					reqEntity.addPart(
							_key,
							new StringBody((String)entry.getValue(), Charset
									.forName("UTF-8")));
				}else if(entry.getValue() instanceof Map){
					Map<String, Object> _part = (Map<String, Object>)entry.getValue();
					addMultipart(reqEntity,_part,_key);
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}
	public static byte[] postFileAndText(final String hostURL,
			final Map<String, Object> part, final Map<String, Object> file) {
		MultipartEntity reqEntity = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);
		if(part!=null){
			addMultipart(reqEntity,part,"");
//			for (Map.Entry<String, Object> entry : part.entrySet()) {
//				try {
//					// 所有的数据都转成utf8
//					reqEntity.addPart(
//							entry.getKey(),
//							new StringBody((String)entry.getValue(), Charset
//									.forName("UTF-8")));
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				}
//			}
		}
		if(file!=null){
			for (Map.Entry<String, Object> entry : file.entrySet()) {
				if(entry.getValue() instanceof File){
					ContentBody cbFile = new FileBody((File)entry.getValue());
					reqEntity.addPart(entry.getKey(), cbFile);
				}else if(entry.getValue() instanceof String){
					try {
						// 所有的数据都转成utf8
						reqEntity.addPart(
								entry.getKey(),
								new StringBody((String)entry.getValue(), Charset
										.forName("UTF-8")));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}else{
					//doonting
				}
			}
		}
		MyHttpPost post = new MyHttpPost();
		Map<String, String> headParams = new HashMap<String, String>();
		//headParams.put("Content-Type", "application/json");
		//headParams.put("Content-Type", "application/oct-stream");
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
	
	
	
//	NSArray * AFQueryStringPairsFromKeyAndValue(NSString *key, id value) {
//	    NSMutableArray *mutableQueryStringComponents = [NSMutableArray array];
//
//	    NSSortDescriptor *sortDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"description" ascending:YES selector:@selector(compare:)];
//
//	    if ([value isKindOfClass:[NSDictionary class]]) {
//	        NSDictionary *dictionary = value;
//	        // Sort dictionary keys to ensure consistent ordering in query string, which is important when deserializing potentially ambiguous sequences, such as an array of dictionaries
//	        for (id nestedKey in [dictionary.allKeys sortedArrayUsingDescriptors:@[ sortDescriptor ]]) {
//	            id nestedValue = dictionary[nestedKey];
//	            if (nestedValue) {
//	                [mutableQueryStringComponents addObjectsFromArray:AFQueryStringPairsFromKeyAndValue((key ? [NSString stringWithFormat:@"%@[%@]", key, nestedKey] : nestedKey), nestedValue)];
//	            }
//	        }
//	    } else if ([value isKindOfClass:[NSArray class]]) {
//	        NSArray *array = value;
//	        for (id nestedValue in array) {
//	            [mutableQueryStringComponents addObjectsFromArray:AFQueryStringPairsFromKeyAndValue([NSString stringWithFormat:@"%@[]", key], nestedValue)];
//	        }
//	    } else if ([value isKindOfClass:[NSSet class]]) {
//	        NSSet *set = value;
//	        for (id obj in [set sortedArrayUsingDescriptors:@[ sortDescriptor ]]) {
//	            [mutableQueryStringComponents addObjectsFromArray:AFQueryStringPairsFromKeyAndValue(key, obj)];
//	        }
//	    } else {
//	        [mutableQueryStringComponents addObject:[[AFQueryStringPair alloc] initWithField:key value:value]];
//	    }
//
//	    return mutableQueryStringComponents;
//	}
}
