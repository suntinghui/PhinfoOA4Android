package cn.com.phinfo.oaact;


import com.alibaba.fastjson.JSON;

import android.os.Bundle;
import cn.com.phinfo.protocol.RolesRun.RolesItem;
import cn.com.phinfo.protocol.RolesUserRun;


public class SelectRolesUsersAct extends SelectGroupUsersAct {
	private RolesItem it;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.it = JSON.parseObject(this.getIntent().getExtras().getString("RolesItem"), RolesItem.class);
		super.onCreate(savedInstanceState);
	}

	
	protected void onRefresh(){
		this.quickHttpRequest(0x10, new RolesUserRun(it.getRoleId(),pageNumber));
	}
}
