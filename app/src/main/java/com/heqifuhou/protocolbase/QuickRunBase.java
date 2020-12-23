package com.heqifuhou.protocolbase;


/**快速请求
请求：自定义
返回：结构(javabean)
**/
public class QuickRunBase extends HttpRunBase{
	public QuickRunBase(String urlAction,IHttpPacketBase packet
			,Class<? extends HttpResultBeanBase> cls) {
		super(urlAction, packet==null?new HttpPostObjBodyPacketBase(null,null):packet, new FastHttpResultParserBase(cls));
	}
}
