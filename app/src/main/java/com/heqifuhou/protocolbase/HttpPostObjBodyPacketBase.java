package com.heqifuhou.protocolbase;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import com.heqifuhou.utils.MyDeviceBaseUtils;

import cn.com.phinfo.entity.DataInstance;
import cn.com.phinfo.oaact.MyApplet;

/**
 * 请求包，通过将此结构调用（自动完成） 这个类不protocol开放 包内调用文件，类不加public
 **/
class HttpPostObjBodyPacketBase implements IHttpPacketBase {
	private static String Platform;
	private static int Width,Height;
	private static int ClientVersionCode;
	private static String DeviceId;
	static {
		Platform=android.os.Build.VERSION.RELEASE; // android系统版本号
		Width = MyDeviceBaseUtils.getScreenW();
		Height =  MyDeviceBaseUtils.getScreenH();
		ClientVersionCode = MyDeviceBaseUtils.getCurrAppCode(MyApplet.getInstance());
		DeviceId = MyDeviceBaseUtils.getDeviceId(MyApplet.getInstance());
	}
	private Map<String, Object> dataBody;
	private IdentityHashMap<String, Object> fileBody;
	public HttpPostObjBodyPacketBase(Map<String, Object> dataBody,IdentityHashMap<String, Object> fileBody) {
		this.dataBody = dataBody;
		this.fileBody = fileBody;
	}
	public Map<String, Object> getDataPacket(){	
		return this.dataBody;
		//this.dataBody.put("Token", DataInstance.getInstance().getToken());
//		Map<String, Object> has = new HashMap<String,Object>();
//		if(this.dataBody!=null){
//			for (Map.Entry<String, Object> entry : this.dataBody.entrySet()) {
//				
//				
//				has.put(String.format("%s",entry.getKey()), entry.getValue());
//			}
//		}
//		return has;
	}
	public IdentityHashMap<String, Object> getUploadPacket(){
		return this.fileBody;
	}
}
