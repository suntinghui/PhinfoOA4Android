package com.heqifuhou.netbase;

import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

public interface IMyHttpPostBase extends IMyHttpRequestBase {
	HttpResponse postEntityToResponse(final String hostURL,
			final Map<String, String> headParams, final HttpEntity entity);
	byte[] postEntityToByte(final String hostURL,
			final Map<String, String> headParams, final HttpEntity entity);
	HttpEntity postEntityToEntity(final String hostURL,
			final Map<String, String> headParams, final HttpEntity entity);
	
	
	HttpResponse postByteToResponse(final String hostURL,
			final Map<String, String> headParams, final byte[] request);
	byte[] postByteToByte(final String hostURL,
			final Map<String, String> headParams, final byte[] request);
	HttpEntity postByteToEntity(final String hostURL,
			final Map<String, String> headParams, final byte[] request);
}
