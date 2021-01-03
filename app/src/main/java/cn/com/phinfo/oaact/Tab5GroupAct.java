package cn.com.phinfo.oaact;

import com.heqifuhou.tab.MyActTabGroupBaseAbs;

public class Tab5GroupAct extends MyActTabGroupBaseAbs {
	@Override
	protected Class<?> getFirstChildTabActCls() {
		return AddressAct.class;
	}
}
