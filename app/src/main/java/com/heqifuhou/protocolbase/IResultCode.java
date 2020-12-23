package com.heqifuhou.protocolbase;

import com.alibaba.fastjson.annotation.JSONField;

public interface IResultCode {
	@JSONField(serialize=false)
	static final int CODE_200 = 200;//成功
	@JSONField(serialize=false)
	static final int CODE_801 = 801;//token失效
	@JSONField(serialize=false)
	static final int CODE_401 = 401;
}
