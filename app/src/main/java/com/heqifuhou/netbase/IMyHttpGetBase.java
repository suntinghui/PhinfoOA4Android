package com.heqifuhou.netbase;

import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

public interface IMyHttpGetBase extends IMyHttpRequestBase{
	HttpResponse getHttpToResponse(final String hostURL,
			final Map<String, String> headParams);

	byte[] getHttpToByte(final String hostURL,
			final Map<String, String> headParams);

	HttpEntity getHttpToEntity(final String hostURL,
			final Map<String, String> headParams);
}
