package cn.com.phinfo.entity;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

import cn.com.phinfo.oaact.R;

public class HomeItem implements Serializable{
	private int id;
	private String name;
	private String label;
	private String icon;
	private String type;
	private String linkUrl;
	private String tag;
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public int getResIcon(){
		switch(name){
		case "wftasks":
			return R.drawable.daiban_selector;
		case "addresslist":
			return R.drawable.contacts_selector;
		case "calendar":
			return R.drawable.schedule_selector;
		case "notice":
		case "news":
			return R.drawable.news_selector;
		case "task":
			return R.drawable.task_selector;
		case "file":
			return R.drawable.youpan_selector;
		case "email":
			return R.drawable.mail_selector;
		case "meetingroom":
			return R.drawable.bespeak_selector;
		case "survey":
			return R.drawable.questionnaire_selector;
		case "log":
			return R.drawable.journal_selector;
		case "meeting":
			return R.drawable.meeting_selector;
		case "recordAccount":
			return R.drawable.accounting_selector;
		case "baoXiao":
			return R.drawable.baoxiao_selector;
		case "salaryquery":
			return R.drawable.wages_selector;
		case "checkIn":
			return R.drawable.qiandao_selector;
		case "attendCheck":
			return R.drawable.kaoqing_selector;
		case "workShifts":
			return R.drawable.scheduling_selector;
		case "appointDoctor":
			return R.drawable.guahao_selector;
		case "orderDinner":
			return R.drawable.dingchan_selector;
		case "nurseConnect":
			return R.drawable.nursing_selector;
		case "doctorConnect":
			return R.drawable.doctor_selector;
		case "onlinetest":
			return R.drawable.examination_selector;
		case "managerDashboard":
			return R.drawable.report_selector;
			default:
				return -1;
		}	
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getLinkUrl() {
		return linkUrl;
	}
	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}
	//此数用来记在本地，存储被点击的次数
	private int clickCount=0;
	//此处表示是否被选中为显示的
	private boolean bCheck = false;
	public void setClickCount(int clickCount) {
		this.clickCount = clickCount;
	}
	public int getClickCount() {
		return clickCount;
	}
	@JSONField(serialize=false)
	public HomeItem addClickCount() {
		this.clickCount += 1;
		return this;
	}
	public boolean getbCheck() {
		return bCheck;
	}
	public void setbCheck(boolean bCheck) {
		this.bCheck = bCheck;
	}
	public HomeItem(){
		
	}


	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
//	@JSONField(serialize=false)
//	public static HomeItem create(int id){
//		switch(id){
//		case 101:
//			return new HomeItem(101,R.drawable.daiban_selector,"待办事务","查看待办内容");
//		case 102:
//			return new HomeItem(102,R.drawable.contacts_selector,"通讯录");
//		case 103:
//			return new HomeItem(103,R.drawable.schedule_selector,"日程");
//		case 104:
//			return new HomeItem(104,R.drawable.news_selector,"新闻公告","查看新闻内容");
//		case 105:
//			return new HomeItem(105,R.drawable.task_selector,"任务","查看任务内容");
//		case 106:
//			return new HomeItem(106,R.drawable.youpan_selector,"优盘","查看优盘内容");
//		case 107:
//			return new HomeItem(107,R.drawable.mail_selector,"邮件","查看邮件内容");
//		case 108:
//			return new HomeItem(108,R.drawable.bespeak_selector,"会议室预约");
//		case 109:
//			return new HomeItem(109,R.drawable.questionnaire_selector,"问卷");
//		case 110:
//			return new HomeItem(110,R.drawable.journal_selector,"日志","查看日志内容");
//		case 111:
//			return new HomeItem(111,R.drawable.meeting_selector,"会议");
//		case 201:
//			return new HomeItem(201,R.drawable.accounting_selector,"记账");
//		case 202:
//			return new HomeItem(202,R.drawable.baoxiao_selector,"报销");
//		case 203:
//			return new HomeItem(203,R.drawable.wages_selector,"工资查询");
//		case 301:
//			return new HomeItem(301,R.drawable.qiandao_selector,"签到");
//		case 302:
//			return new HomeItem(302,R.drawable.kaoqing_selector,"考勤打卡");
//		case 303:
//			return new HomeItem(303,R.drawable.scheduling_selector,"我的排班");
//		case 401:
//			return new HomeItem(401,R.drawable.guahao_selector,"挂号");
//		case 402:
//			return new HomeItem(402,R.drawable.dingchan_selector,"订餐");
//		case 501:
//			return new HomeItem(501,R.drawable.nursing_selector,"护理");
//		case 502:
//			return new HomeItem(502,R.drawable.doctor_selector,"医生");
//		case 601:
//			return new HomeItem(601,R.drawable.examination_selector,"考试");
//		case 701:
//			return new HomeItem(701,R.drawable.report_selector,"医院经营");
//		case 702:
//			return new HomeItem(702,R.drawable.news_selector,"通知","查看通知内容");
//			default:
//				break;
//		}
//		return null;
//	}
}
