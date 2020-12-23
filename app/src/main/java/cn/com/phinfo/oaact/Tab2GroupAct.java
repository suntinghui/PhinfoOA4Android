package cn.com.phinfo.oaact;

import com.heqifuhou.tab.MyActTabGroupBaseAbs;

public class Tab2GroupAct extends MyActTabGroupBaseAbs {
	@Override
	protected Class<?> getFirstChildTabActCls() {
		return Tab2HomeAct.class;
	}
}