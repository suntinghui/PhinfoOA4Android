package com.heqifuhou.netbase;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;


public class MyHttpGet extends MyHttpRequestBase implements IMyHttpGetBase{
	@Override
	public byte[] getHttpToByte(String hostURL, Map<String, String> headParams) {
		try{
			HttpEntity entityRt =  getHttpToEntity(hostURL,headParams);
			return EntityUtils.toByteArray(entityRt);
		}catch(Exception e){	
		}finally{
			close();
		}
		return null;
	}

	@Override
	public HttpEntity getHttpToEntity(String hostURL,Map<String, String> headParams) {
		HttpResponse response = getHttpToResponse(hostURL,headParams);
		if (response != null
				&& response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			return response.getEntity();
		}
		return null;
	}

	@Override
	public HttpResponse getHttpToResponse(String hostURL,Map<String, String> headParams) {
		HttpUriRequest httpRequest = new HttpGet(hostURL);
		addHeadToHttpUriRequest(httpRequest, headParams);
		return requestHttpUriToHttResponse(httpRequest);	
	}
}
