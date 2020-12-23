package cn.com.phinfo.oaact;

import com.heqifuhou.tab.MyActTabGroupBaseAbs;

public class Tab3GroupAct extends MyActTabGroupBaseAbs {
	@Override
	protected Class<?> getFirstChildTabActCls() {
		return Tab3HomeAct.class;
	}
}
