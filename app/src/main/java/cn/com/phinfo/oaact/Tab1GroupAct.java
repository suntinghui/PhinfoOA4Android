package cn.com.phinfo.oaact;

import com.heqifuhou.tab.MyActTabGroupBaseAbs;

public class Tab1GroupAct extends MyActTabGroupBaseAbs {
	@Override
	protected Class<?> getFirstChildTabActCls() {
		return Tab1HomeAct.class;
	}
}
