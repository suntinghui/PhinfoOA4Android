package cn.com.phinfo.oaact;

import com.heqifuhou.tab.MyActTabGroupBaseAbs;

public class Tab4GroupAct extends MyActTabGroupBaseAbs {
	@Override
	protected Class<?> getFirstChildTabActCls() {
		return Tab4HomeAct.class;
	}
}
