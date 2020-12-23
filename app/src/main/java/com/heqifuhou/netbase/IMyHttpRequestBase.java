package com.heqifuhou.netbase;

import java.util.List;

import org.apache.http.cookie.Cookie;

public interface IMyHttpRequestBase {
	void close();
	List<Cookie> getResponseCookies();
	void addRequestCookie(Cookie cookie);
}
