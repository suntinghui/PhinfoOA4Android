package com.heqifuhou.netbase;

import android.util.Log;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import cn.com.phinfo.oaact.MyApplet;

public class MyHttpRequestBase implements IMyHttpRequestBase{
	private DefaultHttpClient httpClient = null;
	private HttpUriRequest httpRequest = null;
	private List<Cookie> responseCookies =null;
	private Stack<Cookie> requestCookies = new Stack<Cookie>();
	private void initHttpClient()
	{
		HttpParams httpParams = new BasicHttpParams();
		httpParams.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, Charset.forName("UTF-8"));//關鍵的一句,讓API識別到charset
		HttpConnectionParams.setConnectionTimeout(httpParams, 30 * 1000);
		HttpConnectionParams.setSoTimeout(httpParams, 30 * 1000);
		HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
		httpClient = new DefaultHttpClient(httpParams);
		String sz = NetworkProxy.getProxy(MyApplet.getInstance().getApplicationContext());
		if(sz != null){
			HttpHost localObject = new HttpHost(sz, 80);
			httpClient.getParams().setParameter("http.route.default-proxy", localObject);
		}
		
		
	}
	public final void close() {
		try {
			if(httpRequest != null)
			{
				httpRequest.abort();
				httpRequest = null;
			}
		} catch (Throwable t) {
		}
		try{
			if(httpClient!=null)
			{
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
		}catch(Exception e){	
		}
	}
	public final List<Cookie> getResponseCookies(){
		return responseCookies;
	}
	public final void addRequestCookie(Cookie cookie){
		this.requestCookies.add(cookie);
	}
	protected final HttpResponse requestHttpUriToHttResponse(HttpUriRequest httpRequest) 
	{
		HttpResponse response = null;
		try {
			initHttpClient();
			this.httpRequest = httpRequest;
			addCookie();
			// 请求
			response = httpClient.execute(httpRequest);
			responseCookies =httpClient.getCookieStore().getCookies();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return response;
	}
	private void addCookie()
	{
		if(requestCookies != null){
			for(Cookie cookie:requestCookies){
				httpClient.getCookieStore().addCookie(cookie);
			}
		}
	}
	protected static void addHeadToHttpUriRequest(HttpUriRequest httpRequest,final Map<String, String> headParams)
	{
		if (headParams != null) {
			for (Map.Entry<String, String> entry : headParams.entrySet()) {
				httpRequest.addHeader(entry.getKey(), entry.getValue());
			}
		}
	}
}
