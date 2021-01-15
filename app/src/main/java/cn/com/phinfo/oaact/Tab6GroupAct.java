package cn.com.phinfo.oaact;

import com.heqifuhou.ioscalendar.CalendarAct;
import com.heqifuhou.tab.MyActTabGroupBaseAbs;

public class Tab6GroupAct extends MyActTabGroupBaseAbs {
	@Override
	protected Class<?> getFirstChildTabActCls() {
		return CalendarAct.class;
	}
}
