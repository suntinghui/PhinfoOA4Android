package com.heqifuhou.actbase;

import com.heqifuhou.utils.StaticReflectUtils.FilterAction;

//要求 static final 
public interface IBroadcastAction {
	@FilterAction(isSupport = true)
	static final String ACTION_EXIT = "com.phinfo.sls.exit";
	// 通知选中了照片
	@FilterAction(isSupport = true)
	static final String ACTION_SEL_PHOTOS = "com.phinfo.sls.select.photos";
	// 通知ORDER发生变化
	@FilterAction(isSupport = true)
	static final String ACTION_ORDER_STATE = "com.phinfo.sls.order.state";
	// 通知TODOS发生变化
	@FilterAction(isSupport = true)
	static final String ACTION_TODOS_STATE = "com.phinfo.sls.todos.state";

	@FilterAction(isSupport = true)
	static final String ACTION_GT_WEB_ACTION = "com.phinfo.sls.web.gt.action";

	@FilterAction(isSupport = true)
	static final String ACTION_CHANEL_CHANGE = "com.phinfo.sls.chanel.change.action";

	@FilterAction(isSupport = true)
	static final String ACTION_USRE_SEL = "com.phinfo.sls.usersel.action";

	// 待办完成了
	@FilterAction(isSupport = true)
	static final String ACTION_DO_OK = "com.phinfo.sls.do.ok.action";

	// 邮件删除
	@FilterAction(isSupport = true)
	static final String ACTION_EMAIL_DEL = "com.phinfo.sls.do.email_del";

	// U重命名
	@FilterAction(isSupport = true)
	static final String ACTION_U_RENAME = "com.phinfo.sls.do.u.rename";

	// U目录创建
	@FilterAction(isSupport = true)
	static final String ACTION_U_CREATE = "com.phinfo.sls.do.u.create";

	// U目录移动
	@FilterAction(isSupport = true)
	static final String ACTION_U_MOVE = "com.phinfo.sls.do.u.move";

	// 创建日历
	@FilterAction(isSupport = true)
	static final String ACTION_CREATE_CALENDAR = "com.phinfo.sls.do.u.createcalendar";
	// 会议邀请
	@FilterAction(isSupport = true)
	static final String ACTION_MEETING_INVITE = "com.phinfo.sls.do.u.meeeing.invite";

	// 议题添加
	@FilterAction(isSupport = true)
	static final String ACTION_MEETING_ITEM_ADD = "com.phinfo.sls.do.u.meeeing.item.add";

	// 删除日历
	@FilterAction(isSupport = true)
	static final String ACTION_DEL_CALENDAR = "com.phinfo.sls.do.u.delcalendar";
	// 创建日历
	@FilterAction(isSupport = true)
	static final String ACTION_CREATE_MEETING = "com.phinfo.sls.do.u.meeting.createcalendar";

	// 签到打卡成功
	@FilterAction(isSupport = true)
	static final String ACTION_CHECKIN = "com.phinfo.sls.do.u.meeting.checkin_action";

	// 更新日志
	@FilterAction(isSupport = true)
	static final String ACTION_SETTING_CHECKING = "com.phinfo.sls.do.u.setting.checkin_action";

	// 更新Report
	@FilterAction(isSupport = true)
	static final String ACTION_REPORT = "com.phinfo.sls.do.u.setting.report_action";
	// 更新分享
	@FilterAction(isSupport = true)
	static final String ACTION_SHARE = "com.phinfo.sls.do.u.setting.share_action";
	// 选中人
	@FilterAction(isSupport = true)
	static final String ACTION_ALL_USER_SEL = "com.phinfo.sls.all.user.sel";

	// 添加组
	@FilterAction(isSupport = true)
	static final String ACTION_ADD_GROUP = "com.phinfo.sls.all.add.group";
	
	// 更新Report
	@FilterAction(isSupport = true)
	static final String ACTION_REPORT_COMM = "com.phinfo.sls.do.u.setting.report_action_comm";
	// 更新分享
	@FilterAction(isSupport = true)
	static final String ACTION_SHARE_COMM = "com.phinfo.sls.do.u.setting.share_action_comm";
}
