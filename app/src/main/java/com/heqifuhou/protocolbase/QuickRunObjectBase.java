package com.heqifuhou.protocolbase;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * 快速请求 请求：结构(javabean) 返回：结构(javabean)
 **/
public class QuickRunObjectBase extends QuickRunBase {
	public QuickRunObjectBase(String urlAction,
			Map<String, Object> dataHashMap, IdentityHashMap<String, Object> fileHashMap,
			Class<? extends HttpResultBeanBase> cls) {
		super(urlAction,
				new HttpPostObjBodyPacketBase(dataHashMap, fileHashMap), cls);
	}

	
	public QuickRunObjectBase(String urlAction, Map<String, Object> dataHashMap) {
		this(urlAction, dataHashMap, null);
	}

	public QuickRunObjectBase(String urlAction,
			Map<String, Object> dataHashMap,
			Class<? extends HttpResultBeanBase> cls) {
		this(urlAction, dataHashMap, null, cls);
	}

	public QuickRunObjectBase(String urlAction) {
		this(urlAction, null, null, null);
	}

	public QuickRunObjectBase(String urlAction,
			Class<? extends HttpResultBeanBase> cls) {
		this(urlAction, null, null, cls);
	}


}
