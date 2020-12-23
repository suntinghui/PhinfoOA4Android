package com.heqifuhou.utils;


import java.io.Serializable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.heqifuhou.utils.CopyObjectUtils.CanCopy;

public class SerializeBase implements Serializable{
	@JSONField(serialize = false)
	@CanCopy(isCanCopy=false)
	public SerializeBase fromJSON(String jsonMsg){
		SerializeBase base = JSON.parseObject(jsonMsg,this.getClass());
		CopyObjectUtils.copySameBeanProperties(this, base);
		return base;
	}
	@CanCopy(isCanCopy=false)
	@JSONField(serialize = false)
	public String toJSON(){
		return JSON.toJSONString(this);
	}
}
