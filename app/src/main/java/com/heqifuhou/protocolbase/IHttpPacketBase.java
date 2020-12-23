package com.heqifuhou.protocolbase;

import java.util.IdentityHashMap;
import java.util.Map;


public interface IHttpPacketBase {
	 Map<String, Object> getDataPacket(); 
	 IdentityHashMap<String, Object> getUploadPacket();
}
