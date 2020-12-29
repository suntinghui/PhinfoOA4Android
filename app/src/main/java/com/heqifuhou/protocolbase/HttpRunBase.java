
package com.heqifuhou.protocolbase;

import java.util.Map;

import com.heqifuhou.netbase.MyNetUtil;
import com.heqifuhou.protocolbase.HttpThread.IHttpRunnable;
import com.heqifuhou.utils.ParamsCheckUtils;

import cn.com.phinfo.oaact.MyApplet;
/**快速请求
请求：自定义
返回：自定义
**/
public class HttpRunBase implements IHttpRunnable {
	private IHttpPacketBase mPacket;
	private IHttpParserBase mParser;
	private String mUrlAction;

	public HttpRunBase(String urlAction, IHttpPacketBase packet,
			IHttpParserBase parser) {
		this.mPacket = packet;
		this.mParser = parser;
		this.mUrlAction = urlAction;
	}

	@Override
	public HttpResultBeanBase onRun(HttpThread t) throws Exception {
		// 判断是否有网络
		if (!MyNetUtil.IsCanConnectNet(MyApplet.getInstance())) {
			return null;
		}
		
		Map<String, Object> dataPacket = mPacket.getDataPacket();
		Map<String, Object> filePacket = mPacket.getUploadPacket();
		if (t.isStopRuning()) {
			return new HttpResultBeanBase();
		}
//		if (dataPacket.isEmpty()) {
//			return new HttpResultBeanBase();
//		}
//		byte[] b  = MyStaticHttpPostMultipart.postPairToByte(mUrlAction, dataPacket);
		byte[] b =  MyStaticHttpPostMultipart.postFileAndText(mUrlAction, dataPacket, filePacket);
//		byte[] b = MyStaticHttpPost.postJsonStream(mUrlAction, byteRequestPacket);
		if (t.isStopRuning()) {
			return new HttpResultBeanBase();
		}
		if (ParamsCheckUtils.isNull(b)) {
			return new HttpResultBeanBase();
		}
		String s = new String(b, "utf-8");
		HttpResultBeanBase obj  =  mParser.parserResult(s);
		//Log.i("TAG", "返回：" + s);
		if (t.isStopRuning()) {
			return new HttpResultBeanBase();
		}
		return obj;
	}

}
