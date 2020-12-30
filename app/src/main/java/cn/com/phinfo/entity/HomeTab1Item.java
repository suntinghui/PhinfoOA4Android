package cn.com.phinfo.entity;

import java.io.Serializable;
import java.util.Random;

import com.alibaba.fastjson.annotation.JSONField;
import com.heqifuhou.utils.ParamsCheckUtils;

import cn.com.phinfo.oaact.R;

public class HomeTab1Item implements Serializable{
	private int id;
	private int nResID;
	private String text;
	private String des;
	private String badgeCount;
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
	public HomeTab1Item addClickCount() {
		this.clickCount += 1;
		return this;
	}
	

	public boolean getbCheck() {
		return bCheck;
	}
	public void setbCheck(boolean bCheck) {
		this.bCheck = bCheck;
	}

	public HomeTab1Item setBadgeCount(String badgeCount) {
		if (ParamsCheckUtils.isNull(badgeCount)) {
			this.badgeCount = "";
		} else {
			this.badgeCount = badgeCount;
		}

		return this;
	}

	public String getBadgeCount() {
//		return this.badgeCount;
		return new Random().nextInt(12)+"";
	}

	public HomeTab1Item(){
	}
	
	public HomeTab1Item(int id,int nResId,String text,String des){
		this.id = id;
		this.text = text;
		this.nResID = nResId;
		this.des = des;
	}
	public HomeTab1Item(int id,int nResId,String text){
		this(id,nResId,text,"");
	}
	public String getDes() {
		return des;
	}
	public void setDes(String des) {
		this.des = des;
	}
	public int getnResID() {
		return nResID;
	}
	public void setnResID(int nResID) {
		this.nResID = nResID;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	@JSONField(serialize=false)
	public static HomeTab1Item create(String moduleId){
		int id = Integer.parseInt(moduleId);

		switch(id){
		case 101:
			return new HomeTab1Item(101,R.drawable.daiban_selector,"待办事务","查看待办内容");
		case 102:
			return new HomeTab1Item(102,R.drawable.contacts_selector,"通讯录");
		case 103:
			return new HomeTab1Item(103,R.drawable.schedule_selector,"日程");
		case 104:
			return new HomeTab1Item(104,R.drawable.news_selector,"新闻公告","查看新闻内容");
		case 105:
			return new HomeTab1Item(105,R.drawable.task_selector,"任务","查看任务内容");
		case 106:
			return new HomeTab1Item(106,R.drawable.youpan_selector,"优盘","查看优盘内容");
		case 107:
			return new HomeTab1Item(107,R.drawable.mail_selector,"邮件","查看邮件内容");
		case 108:
			return new HomeTab1Item(108,R.drawable.bespeak_selector,"会议室预约");
		case 109:
			return new HomeTab1Item(109,R.drawable.questionnaire_selector,"问卷");
		case 110:
			return new HomeTab1Item(110,R.drawable.journal_selector,"日志","查看日志内容");
		case 111:
			return new HomeTab1Item(111,R.drawable.meeting_selector,"会议助手", "查看会议内容");
		case 201:
			return new HomeTab1Item(201,R.drawable.accounting_selector,"记账");
		case 202:
			return new HomeTab1Item(202,R.drawable.baoxiao_selector,"报销");
		case 203:
			return new HomeTab1Item(203,R.drawable.wages_selector,"工资查询");
		case 301:
			return new HomeTab1Item(301,R.drawable.qiandao_selector,"签到");
		case 302:
			return new HomeTab1Item(302,R.drawable.kaoqing_selector,"考勤打卡");
		case 303:
			return new HomeTab1Item(303,R.drawable.scheduling_selector,"我的排班");
		case 401:
			return new HomeTab1Item(401,R.drawable.guahao_selector,"挂号");
		case 402:
			return new HomeTab1Item(402,R.drawable.dingchan_selector,"订餐");
		case 501:
			return new HomeTab1Item(501,R.drawable.nursing_selector,"护理");
		case 502:
			return new HomeTab1Item(502,R.drawable.doctor_selector,"医生");
		case 601:
			return new HomeTab1Item(601,R.drawable.examination_selector,"考试");
		case 701:
			return new HomeTab1Item(701,R.drawable.report_selector,"医院经营");
		case 702:
			return new HomeTab1Item(702,R.drawable.news_selector,"通知","查看通知内容");
			default:
				break;
		}
		return null;
	}
}
