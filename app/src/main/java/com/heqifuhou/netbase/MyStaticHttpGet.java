package com.heqifuhou.netbase;

import java.util.HashMap;
import java.util.Map;

public class MyStaticHttpGet {
	public static byte[] getStream(final String hostURL,final Map<String, String> headParams) {
		MyHttpGet httpGet = new MyHttpGet();
		if (httpGet != null) {
			return httpGet.getHttpToByte(hostURL, headParams);
		}
		return null;
	}

	public static byte[] getPngPic(final String hostUrl) {
		Map<String, String> headParams = new HashMap<String, String>();
		headParams.put("Accept", "image/x-png");
		return getStream(hostUrl, headParams);
	}

	public static byte[] getOctStream(final String hostUrl) {
		Map<String, String> headParams = new HashMap<String, String>();
		headParams.put("Content-Type", "application/oct-stream");
		return getStream(hostUrl, headParams);
	}

}
