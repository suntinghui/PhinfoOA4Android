package com.heqifuhou.netbase;

import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.util.EntityUtils;


public class MyHttpPost extends MyHttpRequestBase implements IMyHttpPostBase{
	@Override
	public byte[] postByteToByte(String hostURL,Map<String, String> headParams, byte[] request) {
		return postEntityToByte(hostURL,headParams,new ByteArrayEntity(request));
	}

	@Override
	public HttpEntity postByteToEntity(String hostURL,Map<String, String> headParams, byte[] request) {
		return postEntityToEntity(hostURL,headParams,new ByteArrayEntity(request));
	}

	@Override
	public HttpResponse postByteToResponse(String hostURL,Map<String, String> headParams, byte[] request) {
		return postEntityToResponse(hostURL,headParams,new ByteArrayEntity(request));
	}

	@Override
	public byte[] postEntityToByte(String hostURL, Map<String, String> headParams, HttpEntity entity) {
		try{
			HttpEntity entityRt =  postEntityToEntity(hostURL,headParams,entity);
			return EntityUtils.toByteArray(entityRt);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			close();
		}
		return null;
	}

	@Override
	public HttpEntity postEntityToEntity(String hostURL, Map<String, String> headParams, HttpEntity entity) {
		try{
			HttpResponse response = postEntityToResponse(hostURL,headParams,entity);
			if (response != null
					&& response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return response.getEntity();
			}
		}catch(Exception e){
		}finally{
			
		}
		return null;
	}

	@Override
	public HttpResponse postEntityToResponse(String hostURL,Map<String, String> headParams, HttpEntity entity) {
		try{
			HttpPost httpRequest  = new HttpPost(hostURL);
			addHeadToHttpUriRequest(httpRequest,headParams);
			if(entity!=null){
				httpRequest.setEntity(entity);
			}
			return requestHttpUriToHttResponse(httpRequest);
		}catch(Exception e){	
		}
		return null;
	}
}
