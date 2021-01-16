package cn.com.phinfo.protocol;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import com.heqifuhou.utils.ParamsCheckUtils;

import android.provider.Settings;
import android.util.Log;
import cn.com.phinfo.db.SettingDB;
import cn.com.phinfo.entity.DataInstance;
import cn.com.phinfo.oaact.MyApplet;

public class LURLInterface {
	static boolean bTest = false;
	static String URL_BASE;
	static String URL_WEB;

	public static void init() {
		URL_BASE = "http://" + SettingDB.getInstance().getCurrFromDB().getIp()
				+ "/rest?";
		URL_WEB = "http://" + SettingDB.getInstance().getCurrFromDB().getIp();
	}

	public static void setUrlBase(String url) {
		URL_BASE = "http://" + url + "/rest?";
		URL_WEB = "http://" + SettingDB.getInstance().getCurrFromDB().getIp();
	}

	public static String getUrlBase() {
		return URL_BASE;
	}

	public static String getUrlWeb() {
		return URL_WEB;
	}

	// 登陆
	public static String GET_URL_LOGIN(String name, String pwd) {
//		String rid = JPushInterface.getRegistrationID(MyApplet.getInstance()
//				.getApplicationContext());
		String rid = "";
		String ANDROID_ID = Settings.System.getString(MyApplet.getInstance()
				.getContentResolver(), Settings.System.ANDROID_ID);
		String encode = name;
		try {
			encode = java.net.URLEncoder.encode(name, "utf-8");
		} catch (UnsupportedEncodingException e) {
		}
		String login_URL = URL_BASE + "method=auth&userName=" + encode
				+ "&password=" + pwd + "&deviceid=" + ANDROID_ID
				+ "&registrationid=" + rid+"&OS=android";
		return login_URL;
	}

	//修攺手机号
	public static String GET_CHANGE_MOBILE(String mobileNumber){
		String token = DataInstance.getInstance().getToken();
		String url = URL_BASE +"method=sys.userinfo.updatemobilenumber&SessionKey="+token+"&mobileNumber="+mobileNumber;
		return url;
	}
	// 取邮件列表
	public static String GET_URL_EMAIL(int pageNumber, String ltags,
			String search) {
		String token = DataInstance.getInstance().getToken();
		int pageSize = 15;
		return URL_BASE
				+ String.format(
						"method=email.getlist&SessionKey=%s&pageSize=%d&pageNumber=%d&ltags=%s&search=%s",
						token, pageSize, pageNumber, ltags, search);
	}

	// 显示邮件
	public static String GET_URL_EMAIL_DETAIL(String id) {
		String token = DataInstance.getInstance().getToken();
		return URL_WEB
				+ String.format("/email/ViewEmail.aspx?id=%s&SessionKey=%s",
						id, token);
	}

	// todo
	public static String GET_URL_TODOS(int type, String search, int pageNumber) {
		switch (type) {
		case 0:
			return GET_URL_WAITINGTASKS(search, pageNumber);
		case 1:
			return GET_URL_WAITINREAD(search, pageNumber);
		case 2:
			return GET_URL_FINISHEDTASKS(search, pageNumber);
		case 3:
			return GET_URL_MYINSTANCE(search, pageNumber);
		}
		return "";
	}

	// 待办
	private static String GET_URL_WAITINGTASKS(String search, int pageNumber) {
		String token = DataInstance.getInstance().getToken();
		int pageSize = 15;
		return URL_BASE
				+ String.format(
						"method=flow.waitingtasks.getlist&SessionKey=%s&search=%s&pageSize=%d&pageNumber=%d",
						token, search, pageSize, pageNumber);
	}

	// 获取待阅
	private static String GET_URL_WAITINREAD(String search, int pageNumber) {
		String token = DataInstance.getInstance().getToken();
		int pageSize = 15;
		return URL_BASE
				+ String.format(
						"method=flow.waitingread.getlist&SessionKey=%s&search=%s&pageSize=%d&pageNumber=%d",
						token, search, pageSize, pageNumber);
	}
	//获取考勤设置与当天考勤数据
	public static String GET_attend_empdaily(){
		String token = DataInstance.getInstance().getToken();
		String url=  URL_BASE
				+ String.format(
						"method=hr.attend.empdaily.get&SessionKey=%s",
						token);
		return url;
		}
	
	// 获取已办
	private static String GET_URL_FINISHEDTASKS(String search, int pageNumber) {
		String token = DataInstance.getInstance().getToken();
		int pageSize = 15;
		return URL_BASE
				+ String.format(
						"method=flow.finishedtasks.getlist&SessionKey=%s&search=%s&pageSize=%d&pageNumber=%d",
						token, search, pageSize, pageNumber);
	}

	// 获取发起（包含草稿，运行，完成的）
	private static String GET_URL_MYINSTANCE(String search, int pageNumber) {
		String token = DataInstance.getInstance().getToken();
		int pageSize = 15;
		return URL_BASE
				+ String.format(
						"method=flow.myinstance.getlist&SessionKey=%s&search=%s&pageSize=%d&pageNumber=%d",
						token, search, pageSize, pageNumber);
	}

	// 首页消息列表
	public static String GET_URL_MESSAGE_STATICS_LIST() {
		String token = DataInstance.getInstance().getToken();
		return URL_BASE
				+ String.format(
				"method=message.statics.getlist&SessionKey=%s",
				token);
	}

	// 取用户信息
	public static String GET_URL_USERINFO() {
		String token = DataInstance.getInstance().getToken();
		return URL_BASE
				+ String.format("method=sys.userinfo.get&SessionKey=%s", token);
	}
	//创建会议议题
	public static String GET_URL_MEETING_ITEM_ADD(final String meetingId) {
		String token = DataInstance.getInstance().getToken();
		return URL_BASE
				+ String.format("method=meeting.item.add&SessionKey=%s&meetingId=%s", token,meetingId);
	}
	
	// 表单
	public static String GET_URL_HANDLEWFINSTANCE(String instanceId, String id) {
		String token = DataInstance.getInstance().getToken();
		return URL_WEB
				+ String.format(
						"/wf/HandleWfInstance.aspx?instanceId=%s&id=%s&SessionKey=%s",
						instanceId, id, token);
	}

	// 传阅
	public static String GET_URL_READLIST(String instanceId) {
		String token = DataInstance.getInstance().getToken();
		return URL_WEB
				+ String.format(
						"/_ui/wf/instance/readlist?InstanceId=%s&SessionKey=%s",
						instanceId, token);
	}
	public static String Create(String url) {
		String token = DataInstance.getInstance().getToken();
		url =  URL_WEB+url;
		if(url.indexOf("?")>0){
			return url+"&SessionKey="+token;
		}else{
			return url+"?SessionKey="+token;
		}
	}
	
	// 流程
	public static String GET_URL_MONITOR(String instanceId) {
		String token = DataInstance.getInstance().getToken();
		return URL_WEB
				+ String.format(
						"/_ui/wf/instance/monitor?InstanceId=%s&SessionKey=%s",
						instanceId, token);
	}

	// 撤销提交
	public static String GET_URL_CANCELSUBMIT(String Wfrulelogid,
			String processInstanceId) {
		String token = DataInstance.getInstance().getToken();
		return URL_BASE
				+ String.format(
						"method=flow.instance.cancelsubmit&SessionKey=%s&Wfrulelogid=%s&processInstanceId=%s",
						token, Wfrulelogid, processInstanceId);
	}

	// 转阅
	public static String GET_URL_notfiyread(String instanceName,
			String processInstanceId, String members, String Message) {
		String encode = Message;
		try {
			encode = java.net.URLEncoder.encode(Message, "utf-8");
		} catch (UnsupportedEncodingException e) {
		}
		String token = DataInstance.getInstance().getToken();
		return URL_BASE
				+ String.format(
						"method=flow.instance.notfiyread&SessionKey=%s&instanceName=%s&processInstanceId=%s&members=%s",
						token, instanceName, processInstanceId, members, encode);
	}

	// 转代理
	public static String GET_URL_TRANSFERAGENT(String Wfrulelogid,
			String processInstanceId, String UserId, String Message) {
		String encode = Message;
		try {
			encode = java.net.URLEncoder.encode(Message, "utf-8");
		} catch (UnsupportedEncodingException e) {
		}
		String token = DataInstance.getInstance().getToken();
		return URL_BASE
				+ String.format(
						"method=flow.instance.transferagent&SessionKey=%s&Wfrulelogid=%s&processInstanceId=%s&UserId=%s&Message=%s",
						token, Wfrulelogid, processInstanceId, UserId, encode);
	}

	// 催办
	public static String GET_URL_PUSHMESSAGE(String Wfrulelogid,
			String processInstanceId, String Message) {
		String token = DataInstance.getInstance().getToken();
		String encode = Message;
		try {
			encode = java.net.URLEncoder.encode(Message, "utf-8");
		} catch (UnsupportedEncodingException e) {
		}
		return URL_BASE
				+ String.format(
						"method=flow.instance.pushmessage&SessionKey=%s&Wfrulelogid=%s&processInstanceId=%s&Message=%s",
						token, Wfrulelogid, processInstanceId, encode);
	}

	// 获取流程正文与附件
	public static String GET_URL_ATTACHEFILE_GETLIST(String id) {
		String token = DataInstance.getInstance().getToken();
		return URL_BASE
				+ String.format(
						"method=flow.instance.attachfile.getlist&SessionKey=%s&id=%s",
						token, id);
	}

	// 获取流程分类
	public static String GET_URL_PROCESSCATEGORY_GETLIST(String search) {
		String token = DataInstance.getInstance().getToken();
		return URL_BASE
				+ String.format(
						"method=flow.processcategory.getlist&SessionKey=%s&search=%s",
						token, search);
	}

	// 搜索流程定义
	public static String GET_URL_PROCESS_SEARCH(String folderid, String search) {
		String token = DataInstance.getInstance().getToken();
		return URL_BASE
				+ String.format(
						"method=flow.process.search&SessionKey=%s&search=%s&folderid=%s",
						token, search, folderid);
	}

	// 获取部门
	public static String GET_URL_BUSINESSUNIT_GETLIST(String pageNumber,String parentId) {
		String token = DataInstance.getInstance().getToken();
		int pageSize = 15;
		return URL_BASE
				+ String.format(
						"method=sys.businessunit.getlist&SessionKey=%s&parentId=%s&pageNumber=%s&pageSize=%s",
						token, parentId,pageNumber,pageSize);
	}

	// 获取角色
	public static String GET_URL_ROLES(String search) {
		String token = DataInstance.getInstance().getToken();
		return URL_BASE
				+ String.format(
						"method=sys.roles.search&SessionKey=%s&search=%s",
						token, search);
	}

	// 获取我的小组
	public static String GET_URL_GROUP(String search) {
		String token = DataInstance.getInstance().getToken();
		return URL_BASE
				+ String.format(
						"method=sys.groups.search&SessionKey=%s&search=%s",
						token, search);
	}

	// 地址=》我的部门
	public static String GET_URL_unitandaddresslist(String parentId) {
		String token = DataInstance.getInstance().getToken();
		return URL_BASE
				+ String.format(
						"method=oa.unitandaddresslist.getlist&SessionKey=%s&parentId=%s",
						token, parentId);
	}

	// 地址=》我的小组
	public static String GET_URL_addresslist_group(String search) {
		String token = DataInstance.getInstance().getToken();
		return URL_BASE
				+ String.format(
						"method=oa.addresslist.group.getlist&SessionKey=%s&search=%s",
						token, search);
	}

	// 地址=》我的小组详情
	public static String GET_URL_GROUPCONTACTS_GETLIST(final String groupId,
			String search) {
		String token = DataInstance.getInstance().getToken();
		return URL_BASE
				+ String.format(
						"method=oa.addresslist.groupcontacts.getlist&SessionKey=%s&search=%s&groupId=%s",
						token, search, groupId);
	}

	// 推荐新闻列表
	public static String GET_URL_RECOMMEND_GETLIST(int PageNumber,int contentTypeCode) {
		String token = DataInstance.getInstance().getToken();
		int pageSize = 15;
		String url =  URL_BASE
				+ String.format(
						"method=news.recommend.getlist&SessionKey=%s&PageNumber=%d&pageSize=%d&contentTypeCode=%d",
						token, PageNumber, pageSize,contentTypeCode);
		return url;
	}

	// 热点新闻列表
	public static String GET_URL_HOT_GETLIST(int PageNumber,int contentTypeCode) {
		String token = DataInstance.getInstance().getToken();
		int pageSize = 15;
		return URL_BASE
				+ String.format(
						"method=news.hot.getlist&SessionKey=%s&PageNumber=%d&pageSize=%d&contentTypeCode=%d",
						token, PageNumber, pageSize,contentTypeCode);
	}

	// 搜索新闻
	public static String GET_URL_NEWS_GETLIST(int PageNumber, String tag,
			String Search,int contentTypeCode) {
		String token = DataInstance.getInstance().getToken();
		int pageSize = 15;
		String url =  URL_BASE
				+ String.format(
						"method=news.getlist.search&SessionKey=%s&PageNumber=%d&pageSize=%d&Search=%s&Tag=%s&contentTypeCode=%d",
						token, PageNumber, pageSize, Search, tag,contentTypeCode);
		return url;
	}

	// 获取新闻频道（标签）
	public static String GET_URL_NEWS_channel() {
		String token = DataInstance.getInstance().getToken();
		return URL_BASE
				+ String.format("method=news.channel.getlist&SessionKey=%s",
						token);
	}

	// 获新闻的内容
	public static String GET_URL_NEWS_contentbody(final String id) {
		String token = DataInstance.getInstance().getToken();
		return URL_BASE
				+ String.format(
						"method=news.contentbody.get&SessionKey=%s&Id=%s",
						token, id);
	}

	// 喜欢/不喜欢
	public static String GET_URL_NEWS_LIKEACTION(final String id,
			final String action) {
		String token = DataInstance.getInstance().getToken();
		return URL_BASE
				+ String.format(
						"method=News.comment.like&SessionKey=%s&Id=%s&action=%s",
						token, id, action);
	}

	// 获取单个新闻基本信息
	public static String GET_URL_NEWS_basicinfo(final String id) {
		String token = DataInstance.getInstance().getToken();
		return URL_BASE
				+ String.format(
						"method=news.basicinfo.get&SessionKey=%s&Id=%s", token,
						id);
	}

	// 评论新闻新闻
	public static String GET_URL_NEWS_COMMENT_ADD(final String id,
			final String parentid, String comments) {
		String token = DataInstance.getInstance().getToken();
		return URL_BASE
				+ String.format(
						"method=news.comment.add&SessionKey=%s&Id=%s&parentid=%s&comments=%s",
						token, id, parentid, comments);
	}

	// 相关新闻
	public static String GET_URL_NEWS_RELATED_GETLIST(final String id) {
		String token = DataInstance.getInstance().getToken();
		return URL_BASE
				+ String.format(
						"method=news.related.getlist&SessionKey=%s&Id=%s",
						token, id);
	}

	// 新闻评论列表
	public static String GET_URL_NEWS_COMMENT_GETLIST(final String id,
			final String parentid, final int pageNumber) {
		String token = DataInstance.getInstance().getToken();
		int pageSize = 15;
		return URL_BASE
				+ String.format(
						"method=news.comment.getlist&SessionKey=%s&Id=%s&pageNumber=%d&pageSize=%d&parentid=%s",
						token, id, pageNumber, pageSize, parentid);
	}

	// 搜索通讯录
	public static String GET_URL_ADDRESSLIST(final String search,
			final int pageNumber) {
		String token = DataInstance.getInstance().getToken();
		int pageSize = 15;
	String url  = URL_BASE
				+ String.format(
						"method=oa.addresslist.search&SessionKey=%s&search=%s&pageNumber=%d&pageSize=%d",
						token, search, pageNumber, pageSize);
	return url;
	}

	// 发起事务
	public static String GET_URL_INSTANCE_CREATE(final String name,
			final String processid, final String businessUnitId,
			final String deadline, int Prority, String Description) {
		String encodeDescription = Description;
		try {
			encodeDescription = java.net.URLEncoder
					.encode(Description, "utf-8");
		} catch (UnsupportedEncodingException e) {
		}
		String encodeName = name;
		try {
			encodeName = java.net.URLEncoder.encode(name, "utf-8");
		} catch (UnsupportedEncodingException e) {
		}
		String token = DataInstance.getInstance().getToken();
		return URL_BASE
				+ String.format(
						"method=flow.instance.create&SessionKey=%s&name=%s&processid=%s&businessUnitId=%s&deadline=%s&Prority=%d&Description=%s",
						token, encodeName, processid, businessUnitId, deadline,
						Prority, encodeDescription);
	}

	// 上传
	public static String GET_URL_UPLOAD(final String pid) {
		String token = DataInstance.getInstance().getToken();
		return URL_BASE
				+ String.format(
						"method=file.attachfiles.upload&SessionKey=%s&pid=%s",
						token, pid);
	}

	// 提交报告内容
	public static String GET_URL_REPORT_UPLOAD(final String worklogid) {
		String token = DataInstance.getInstance().getToken();
		return URL_BASE
				+ String.format(
						"method=worklog.reportcontent.send&SessionKey=%s&worklogid=%s",
						token, worklogid);
	}
	// 文件上传
	public static String GET_URL_FILE_UPLOAD_CREATE(final String folderid) {
		String token = DataInstance.getInstance().getToken();
		return URL_BASE
				+ String.format(
						"method=file.upload.create&SessionKey=%s&folderid=%s",
						token, folderid);
	}
	//会议议题
	public static String GET_URL_MEETING_ITEM_GETLIST(final String id) {
		String token = DataInstance.getInstance().getToken();
		return URL_BASE
				+ String.format(
						"method=meeting.items.getlist&SessionKey=%s&id=%s",
						token, id);
	}
	//会议邀请
	public static String GET_URL_MEETING_PEOPLES_INVITE(final String meetingId) {
		String token = DataInstance.getInstance().getToken();
		return URL_BASE
				+ String.format(
						"method=meeting.peoples.invite&SessionKey=%s&meetingId=%s",
						token, meetingId);
	}
	
	// 上传Email
	public static String GET_URL_UPLOAD_LOCAL_EMAIL(final String pid) {
		String token = DataInstance.getInstance().getToken();
		return URL_BASE
				+ String.format(
						"method=email.attachfiles.upload&SessionKey=%s&pid=%s",
						token, pid);
	}

	public static String GET_URL_UPLOAD_URL_EMAIL(final String mailid,
			final String fileids) {
		String token = DataInstance.getInstance().getToken();
		return URL_BASE
				+ String.format(
						"method=email.attachfiles.addfromfile&SessionKey=%s&fileids=%s&mailid=%s",
						token, fileids, mailid);
	}

	// 搜索用户
	public static String GET_URL_USER_SEARCH(final String search,
			final int pageNumber, final String businessUnitId) {
		String token = DataInstance.getInstance().getToken();
		int PageSize = 15;
		return URL_BASE
				+ String.format(
						"method=sys.users.search&SessionKey=%s&search=%s&businessUnitId=%s&pageNumber=%d&PageSize=%d",
						token, search, businessUnitId, pageNumber, PageSize);
	}

	// 工资查询
	public static String GET_URL_HRLARY() {
		String token = DataInstance.getInstance().getToken();
		return URL_WEB
				+ String.format("/_ui/hr/salary/query?sessionKey=%s", token);
	}

	// 发单成功跳转
	public static String GET_CREATE_TODO_OK(final String instanceId,
			final String id) {
		String token = DataInstance.getInstance().getToken();
		return URL_WEB
				+ String.format(
						"/wf/HandleWfInstance.aspx?sessionKey=%s&source=i&instanceId=%s&id=%s",
						token, instanceId, id);
	}

	// 新闻附件
	public static String GET_NEWS_ATTACHEFILES(final String pid) {
		String token = DataInstance.getInstance().getToken();
		return URL_BASE
				+ String.format(
						"method=file.attachfiles.getlist&SessionKey=%s&pid=%s",
						token, pid);
	}

	// 获取小组下的用户
	public static String GET_GROUPUSRS_GETLIST(final String groupId,
			int pageNumber, String search) {
		String token = DataInstance.getInstance().getToken();
		int pageSize = 15;
		String url = URL_BASE
				+ String.format(
						"method=sys.groupusers.getlist&SessionKey=%s&groupId=%s&pageNumber=%d&pageSize=%d&search=%s",
						token, groupId, pageNumber, pageSize, search);
		return url;
	}

	// 获取角色下的用户
	public static String GET_ROLEUSERS_GETLIST(final String roleid,
			int pageNumber, String search) {
		String token = DataInstance.getInstance().getToken();
		int pageSize = 15;
		String url = URL_BASE
				+ String.format(
						"method=sys.roleusers.getlist&SessionKey=%s&roleid=%s&pageNumber=%d&pageSize=%d&search=%s",
						token, roleid, pageNumber, pageSize, search);
		return url;
	}

	// 搜索流程
	public static String GET_INSTANCES_SEARCH(int pageNumber, String search) {
		String encode = search;
		try {
			encode = java.net.URLEncoder.encode(search, "utf-8");
		} catch (UnsupportedEncodingException e) {
		}
		String token = DataInstance.getInstance().getToken();
		int pageSize = 15;
		String url = URL_BASE
				+ String.format(
						"method=flow.instances.search&SessionKey=%s&pageNumber=%d&pageSize=%d&search=%s",
						token, pageNumber, pageSize, encode);
		return url;
	}

	// 文件类型
	public static String GET_FILES_SEARCH(String srchType, String Folderid,
			int pageNumber, String search) {
		String encode = search;
		try {
			encode = java.net.URLEncoder.encode(search, "utf-8");
		} catch (UnsupportedEncodingException e) {
		}
		String token = DataInstance.getInstance().getToken();
		int pageSize = 15;
		String url = URL_BASE
				+ String.format(
						"method=file.files.search&SessionKey=%s&pageNumber=%d&pageSize=%d&search=%s&srchType=%s&Folderid=%s",
						token, pageNumber, pageSize, encode, srchType, Folderid);
		return url;
	}

	// 邮件基本信息
	public static String GET_EMAIL_INFO_GET(String Id) {
		String token = DataInstance.getInstance().getToken();
		String url = URL_BASE
				+ String.format("method=email.info.get&SessionKey=%s&Id=%s",
						token, Id);
		return url;
	}

	// 获取邮件内容
	public static String GET_EMAIL_CONTENTBODY_GET(String Id) {
		String token = DataInstance.getInstance().getToken();
		String url = URL_BASE
				+ String.format(
						"method=email.contentbody.get&SessionKey=%s&Id=%s",
						token, Id);
		return url;
	}

	// 获取邮件附件
	public static String GET_EMAIL_ATTACHEFILES_GET(String Id) {
		String token = DataInstance.getInstance().getToken();
		String url = URL_BASE
				+ String.format(
						"method=email.attachfiles.getlist&SessionKey=%s&Id=%s",
						token, Id);
		return url;
	}

	// 加星
	public static String GET_EMAIL_ADD(String Id) {
		String token = DataInstance.getInstance().getToken();
		String url = URL_BASE
				+ String.format("method=email.star.add&SessionKey=%s&Id=%s",
						token, Id);
		return url;
	}

	// 删除
	public static String GET_EMAIL_DEL(String Id, String mailbox) {
		String token = DataInstance.getInstance().getToken();
		String url = URL_BASE
				+ String.format(
						"method=email.mailbox.delete&SessionKey=%s&Id=%s&mailbox=%s",
						token, Id, mailbox);
		return url;
	}

	// 发邮件
	public static String GET_EMAIL_SEND() {
		String token = DataInstance.getInstance().getToken();
		String url = URL_BASE
				+ String.format("method=email.componse.send&SessionKey=%s",
						token);
		return url;
	}

	// 重命名DIR
	public static String GET_DIR_RENAME(final String Id, final String name) {
		String token = DataInstance.getInstance().getToken();
		String encode = name;
		try {
			encode = java.net.URLEncoder.encode(name, "utf-8");
		} catch (UnsupportedEncodingException e) {
		}
		String url = URL_BASE
				+ String.format(
						"method=file.directoryinfo.rename&SessionKey=%s&Id=%s&name=%s",
						token, Id, encode);
		return url;
	}

	// 重命名FILE
	public static String GET_FILE_RENAME(final String Id, final String name) {
		String encode = name;
		try {
			encode = java.net.URLEncoder.encode(name, "utf-8");
		} catch (UnsupportedEncodingException e) {
		}
		String token = DataInstance.getInstance().getToken();
		String url = URL_BASE
				+ String.format(
						"method=file.fileinfo.rename&SessionKey=%s&Id=%s&name=%s",
						token, Id, encode);
		return url;
	}

	// 删除FILE
	public static String GET_FILE_DEL(final String Id) {
		String token = DataInstance.getInstance().getToken();
		String url = URL_BASE
				+ String.format("method=file.delete&SessionKey=%s&Id=%s",
						token, Id);
		return url;
	}

	// 删除目录
	public static String GET_DIR_DEL(final String Id) {
		String token = DataInstance.getInstance().getToken();
		String url = URL_BASE
				+ String.format(
						"method=file.directory.delete&SessionKey=%s&Id=%s",
						token, Id);
		return url;
	}

	//上传
	public static String GET_URL_uploadavatar() {
		String token = DataInstance.getInstance().getToken();
		String url = URL_BASE
				+ String.format(
						"method=sys.user.uploadavatar&SessionKey=%s",
						token);
		return url;
	}
	// 创建文件夹
	public static String GET_DIR_CREATE(final String ParentId, final String Name) {
		String token = DataInstance.getInstance().getToken();
		String encode = Name;
		try {
			encode = java.net.URLEncoder.encode(Name, "utf-8");
		} catch (UnsupportedEncodingException e) {
		}
		String url = URL_BASE
				+ String.format(
						"method=file.directory.create&SessionKey=%s&ParentId=%s&Name=%s",
						token, ParentId, encode);
		return url;
	}

	// 移动文件夹
	public static String GET_DIR_MOVE(final String Id,
			final String targetFolderId, String ObjectTypeCode) {
		String token = DataInstance.getInstance().getToken();
		String url = URL_BASE
				+ String.format(
						"method=file.directoryfile.move&SessionKey=%s&Id=%s&targetFolderId=%s&ObjectTypeCode=%s",
						token, Id, targetFolderId, ObjectTypeCode);
		return url;
	}

	// 我的问卷
	public static String GET_MYLIST() {
		String token = DataInstance.getInstance().getToken();
		String url = URL_WEB
				+ String.format("/_ui/svry/mylist?SessionKey=%s", token);
		return url;
	}
	//二维码
	public static String GET_QCODE(final String id){
		String token = DataInstance.getInstance().getToken();
		String url = URL_WEB
				+ String.format("/_ui/meeting/ui/qcode?sessionKey=%s&id=%s",token,id);
		return url;
	}
	//主页二维码
	public static String GET_HOME_QCODE(){
		String token = DataInstance.getInstance().getToken();
		String url = URL_WEB
				+ String.format("/_ui/meeting/ui/qcode?sessionKey=%s",token);
		return url;
	}
	// 我的排班
	public static String GET_MYSHIFTS() {
		String token = DataInstance.getInstance().getToken();
		return URL_WEB
				+ String.format("/_ui/hr/attend/myshifts?sessionKey=%s", token);
	}

	// 我的签到
	public static String GET_checkin() {
		String token = DataInstance.getInstance().getToken();
		String url = URL_BASE
				+ String.format("method=hr.attend.out.checkin&SessionKey=%s",
						token);
		return url;
	}

	// 我的签到
	public static String GET_attenddetail(final int pageNumber,
			final String startDate, final String endDate,
			final String attendType, final String checkType, final String userId) {
		String token = DataInstance.getInstance().getToken();
		int pageSize = 15;
		String url = URL_BASE
				+ String.format(
						"method=hr.attenddetail.search&SessionKey=%s&pageNumber=%d&pageSize=%d&startDate=%s&endDate=%s&attendType=%s&checkType=%s&userId=%s",
						token, pageNumber, pageSize, startDate, endDate,
						attendType, checkType, userId);
		return url;
	}

	// 取状态列表
	public static String GET_URL_ScheduleStatus() {
		String token = DataInstance.getInstance().getToken();
		return "";
	}


	// 取得日程状态列表
	public static String GET_URL_Activity_Event(String startTime, String endTime) {
		String token = DataInstance.getInstance().getToken();
		String url = URL_BASE
				+ String.format(
						"method=Activity.events.search&SessionKey=%s&startTime=%s&endTime=%s",
						token, startTime, endTime);
		return url;

	}

	// 获取日程列表
	public static String GET_URL_Activity_Event_GETLIST(int pageNumber,
			String date, String Search) {
		final int pageSize = 15;
		String token = DataInstance.getInstance().getToken();
		String url = URL_BASE
				+ String.format(
						"method=Activity.event.getlist&SessionKey=%s&pageSize=%s&pageNumber=%d&date=%s&Search=%s",
						token, pageSize, pageNumber, date, Search);
		return url;
	}
	// 获取单个日程信息
	public static String GET_URL_EVENT_GET(final String id) {
		String token = DataInstance.getInstance().getToken();
		String url = URL_BASE
				+ String.format("method=activity.event.get&SessionKey=%s&id=%s",
						token,id);
		return url;

	}
//	
//	// 获取考勤规则
//	public static String GET_URL_ATTENTDSETTINGS_GET() {
//		String token = DataInstance.getInstance().getToken();
//		String url = URL_BASE
//				+ String.format("method=hr.attendsettings.get&SessionKey=%s",
//						token);
//		return url;
//
//	}
	//hr.attendsettings.get
	//删除日程
	public static String GET_URL_EVENT_DEL(String id){
		String token = DataInstance.getInstance().getToken();
		String url = URL_BASE
				+ String.format("method=activity.event.delete&SessionKey=%s&id=%s",
						token,id);
		return url;
	}
	//会议评论列表
	public static String GET_URL_COMMENTS_GETLIST(String id,int pageNumber,String search){
		String pageSize ="15";
		String token = DataInstance.getInstance().getToken();
		String url = URL_BASE
				+ String.format("method=object.comments.getlist&SessionKey=%s&id=%s&pageNumber=%d&pageSize=%s&search=%s",
						token,id,pageNumber,pageSize,search);
		return url;
	}
	
	
	// 创建日程
	public static String GET_URL_CREATECALENDAR() {
		String token = DataInstance.getInstance().getToken();
		String url = URL_BASE
				+ String.format("method=activity.event.create&SessionKey=%s",
						token);
		return url;

	}

	//会议讨论
	public static String GET_URL_MEETINTG_COMMENT_ADD(final String id,final String parentid) {
		String token = DataInstance.getInstance().getToken();
		String url = URL_BASE
				+ String.format("method=object.comment.add&SessionKey=%s&id=%s&parentid=%s&objTypeCode=20015",
						token,id,parentid);
		return url;
	}
	

	// 获取会议资源列表
	public static String GET_URL_ROOM_GETLIST() {
		String token = DataInstance.getInstance().getToken();
		String url = URL_BASE
				+ String.format("method=resource.room.getlist&SessionKey=%s",
						token);
		return url;

	}

	// 获取会议室预定情况
	public static String GET_URL_ROOMAPPOINT_GETLIST(final String startTime,
			final String endTime) {
		String token = DataInstance.getInstance().getToken();
		String url = URL_BASE
				+ String.format(
						"method=resource.roomappoint.getlist&SessionKey=%s&startTime=%s&endTime=%s",
						token, startTime, endTime);
		return url;

	}

	// 删除
	public static String GET_URL_ROOMORDER_DEL(final String Id) {
		String token = DataInstance.getInstance().getToken();
		String url = URL_BASE
				+ String.format(
						"method=resource.roomorder.delete&SessionKey=%s&Id=%s",
						token, Id);
		return url;

	}

	// 获取单个会议室预约信息
	public static String GET_URL_ENTITY_INFO_GET(final String ObjTypeCode,final String Id) {
		String token = DataInstance.getInstance().getToken();
		String url = URL_BASE
				+ String.format(
						"method=entity.info.get&SessionKey=%s&ObjTypeCode=%s&Id=%s",
						token,ObjTypeCode, Id);
		return url;

	}

	//创建会议
	public static String GET_URL_MEETING_INFO_ADD() {
		String token = DataInstance.getInstance().getToken();
		String url = URL_BASE
				+ String.format("method=meeting.info.add&SessionKey=%s",
						token);
		return url;
	}
	public static String GET_URL_ROOM_ORDER() {
		String token = DataInstance.getInstance().getToken();
		String url = URL_BASE
				+ String.format("method=resource.room.order&SessionKey=%s",
						token);
		return url;
	}
	// 会议
	public static String GET_URL_MEETING_REC(final String startTime,final String endTime,final String search) {
		String token = DataInstance.getInstance().getToken();
		String url = URL_BASE
				+ String.format("method=meetings.recs.search&SessionKey=%s&startTime=%s&endTime=%s&search=%s",
						token,startTime,endTime,search);
		return url;
	}
	//获取参加会议人员
	public static String GET_URL_PEOPLES_GETLIST(final String status,final String id) {
		String token = DataInstance.getInstance().getToken();
		String url = URL_BASE
				+ String.format("method=meeting.peoples.getlist&SessionKey=%s&status=%s&id=%s&objectTypeCode=5002",
						token,status,id);
		return url;
	}
	
	// 获取某一天的会议列表
	public static String GET_URL_MEETING_REC_GETLIST(final String date,final String userId) {
		String token = DataInstance.getInstance().getToken();
		String url = URL_BASE
				+ String.format("method=meeting.recs.getlist&SessionKey=%s&date=%s&userId=%s",
						token,date,userId);
		return url;
	}
	// 删除会议
	public static String GET_URL_DEL(final String Id) {
		String token = DataInstance.getInstance().getToken();
		String url = URL_BASE
				+ String.format("method=entity.info.delete&SessionKey=%s&ObjTypeCode=20015&Id=%s",
						token,Id);
		return url;
	}

	// 查询我参加的会议
	public static String GET_URL_MY_MEETING_LIST(int currentPage) {
		String token = DataInstance.getInstance().getToken();
		String url = URL_BASE
				+ String.format("method=meeting.my.getlist&SessionKey=%s&pageNumber=%d&pageSize=25", token, currentPage);
		return url;
	}

	// 接受或拒绝会议
	// 拒绝时必须填写desc，接受时可不填写。
	// 接受为1， 拒绝为2
	public static String GET_URL_OPER_MEETING(String meetingId, String desc, String status) {
		String token = DataInstance.getInstance().getToken();
		String url = URL_BASE
				+ String.format("method=meeting.people.status&SessionKey=%s&Id=%s&descripiton=%s&status=%s", token, meetingId, desc, status);
		return url;
	}
	
	//客户端升级
	public static String GET_URL_SYSUPDATE(String ver){
		String token = DataInstance.getInstance().getToken();
		String url =  URL_BASE+String.format("method=getappversion&SessionKey=%s&version=%s",token,ver);
		return url;
	}
	//模块可配置
	public static String GET_UR_APP_MODULES(){
		String token = DataInstance.getInstance().getToken();
		String url =  URL_BASE+String.format("method=app.module.user.getlist&SessionKey=%s",token);
		return url;
	}
	
	//取头像
	public static String GET_AVATAR(String UserId){
		if(ParamsCheckUtils.isNull(UserId)){
			return "";
		}
		String token = DataInstance.getInstance().getToken();
		String url =  URL_BASE+String.format("method=sys.user.avatar.get&SessionKey=%s&UserId=%s&r=%s",token,UserId,UUID.randomUUID().toString());
		Log.v("URL", url);
		return url;
	}
	
	//上下班打卡
	public static String GET_WORK_CHECKIN(){
		String token = DataInstance.getInstance().getToken();
		String url =  URL_BASE+String.format("method=hr.attend.work.checkin&SessionKey=%s",token);
		return url;
	}
	//月汇总统计
	public static String GET_HR_ATTENDRPT(final String AttendYear,final String AttendMonth){
		String token = DataInstance.getInstance().getToken();
		String url =  URL_BASE+String.format("method=hr.attendrpt.search&SessionKey=%s&AttendYear=%s&AttendMonth=%s",token,AttendYear,AttendMonth);
		return url;
	}
	//查看是否有考勤权限
	public static String GET_SYS_PRIVILEGE_GET(){
		String token = DataInstance.getInstance().getToken();
		String url =  URL_BASE+String.format("method=sys.privilege.get&SessionKey=%s",token);
		return url;
	}
	//取得wifi地址
	public static String GET_ATTENDWIFIS_GETLIST(){
		String token = DataInstance.getInstance().getToken();
		String url =  URL_BASE+String.format("method=hr.attendwifis.getlist&SessionKey=%s",token);
		return url;
	}
	//保存打卡的设置
	public static String GET_HR_ATTENDSETTINGS_SAVE(){
		String token = DataInstance.getInstance().getToken();
		String url =  URL_BASE+String.format("method=hr.attendsettings.save&SessionKey=%s",token);
		return url;
	}
	//获取考勤地址列表
	public static String GET_HR_ATTENDSETTINGS_GETLIST(){
		String token = DataInstance.getInstance().getToken();
		String url =  URL_BASE+String.format("method=hr.attendlocations.getlist&SessionKey=%s",token);
		return url;
	}
	//删除考勤地址
	public static String GET_INFO_DEL(String id){
		String token = DataInstance.getInstance().getToken();
		String url =  URL_BASE+String.format("method=entity.info.delete&SessionKey=%s&Id=%s&ObjTypeCode=30092",token,id);
		return url;
	}
	//添加考勤地址
	public static String GET_INFO_ADD(){
		String token = DataInstance.getInstance().getToken();
		String url =  URL_BASE+String.format("method=entity.create&SessionKey=%s",token);
		return url;
	}
	//获取WIFI地址列表
	public static String GET_HR_ATTENDSETTINGS_WIFI_GETLIST(){
		String token = DataInstance.getInstance().getToken();
		String url =  URL_BASE+String.format("method=hr.attendwifis.getlist&SessionKey=%s",token);
		return url;
	}
	//删除WIFI考勤地址
	public static String GET_INFO_DEL_WIFI(String id){
		String token = DataInstance.getInstance().getToken();
		String url =  URL_BASE+String.format("method=entity.info.delete&SessionKey=%s&Id=%s&ObjTypeCode=30093",token,id);
		return url;
	}
	//获取某一天打卡记录，如果当前有缺卡会自动补齐
	public static String GET_HR_ATTENDDETAIL_GETDAILY(String attendDate,String attendType){
		String token = DataInstance.getInstance().getToken();
		String userId = DataInstance.getInstance().getUserBody().getUserid();
		String url =  URL_BASE+String.format("method=hr.attenddetail.getdaily&SessionKey=%s&attendDate=%s&attendType=%s&userId=%s",token,attendDate,attendType,userId);
		return url;
	}
	//查询月考勤汇总数据
	public static String GETattendrpt_serarch(final String AttendYear,final String AttendMonth,final String userId){
		String token = DataInstance.getInstance().getToken();
		String url =  URL_BASE+String.format("method=hr.attendrpt.search&SessionKey=%s&AttendYear=%s&AttendMonth=%s",token,AttendYear,AttendMonth);
		return url;
	}
	//日志报告内容
	public static String GET_REPORT_SEARCH(String worklogtypecode,String scope,String search,int pageNumber,String owningUser){
		String token = DataInstance.getInstance().getToken();
		String url =  URL_BASE+String.format("method=worklog.reports.search&SessionKey=%s&worklogtypecode=%s&scope=%s&search=%s&pageNumber=%d&pageSize=%d&owningUser=%s",token,worklogtypecode,scope,search,pageNumber,15,owningUser);
		return url;
	}
	//分享
	public static String GET_CHATTER_SEARCH(String search,int pageNumber,String createdBy){
		String token = DataInstance.getInstance().getToken();
		String url =  URL_BASE+String.format("method=chatter.status.search&SessionKey=%s&search=%s&pageNumber=%d&pageSize=%d&createdBy=%s",token,search,pageNumber,15,createdBy);
		return url;
	}
	//为小组添加用户
	public static String GET_GROUP_ADDUSER(String groupId,String Userid,String action){
		String token = DataInstance.getInstance().getToken();
		String url =  URL_BASE+String.format("method=sys.group.adduser&SessionKey=%s&groupId=%s&Userid=%s&action=%s",token,groupId,Userid,action);
		return url;
	}
	//创建小组
	public static String GET_GROUP_CREATE(String groupname){
		String token = DataInstance.getInstance().getToken();
		String url =  URL_BASE+String.format("method=sys.group.create&SessionKey=%s&name=%s",token,groupname);
		return url;
	}
	

	// 提交朋友的圈子
	public static String GET_URL_REPORT_UPLOAD() {
		String token = DataInstance.getInstance().getToken();
		return URL_BASE
				+ String.format(
						"method=chatter.status.update&SessionKey=%s",
						token);
	}
	
	//评论列表
	public static String GET_URL_COMMENTS_GETLIST(final String id,final String pageNumber){
		String token = DataInstance.getInstance().getToken();
		String url =  URL_BASE
				+ String.format(
						"method=object.comments.getlist&SessionKey=%s&id=%s&pageNumber=%s&pageSize=15",
						token,id,pageNumber);
		return url;
	}
	//获取点赞人员列表
	public static String GET_URL_REPORT_LIKE_GETLIST(final String id,final String pageNumber){
		String token = DataInstance.getInstance().getToken();
		String url =  URL_BASE
				+ String.format(
						"method=worklog.peoples.like.getlist&SessionKey=%s&id=%s&pageNumber=%s&pageSize=15",
						token,id,pageNumber);
		return url;
	}
	//删除报告内容
	public static String GET_URL_REPORT_DEL(final String id){
		String token = DataInstance.getInstance().getToken();
		String url =  URL_BASE
				+ String.format(
						"method=worklog.report.delete&SessionKey=%s&id=%s&ObjTypeCode=5500",
						token,id);
		return url;
	}
	//编辑报告内容
	public static String GET_URL_REPORT_UPDATE(final String id){
		String token = DataInstance.getInstance().getToken();
		String url =  URL_BASE
				+ String.format(
						"method=worklog.reportcontent.update&SessionKey=%s&id=%s",
						token,id);
		return url;
	}
	//添加评论
	public static String GET_URL_COMMENT_ADD(final String objTypeCode,final String objectid,final String parentid){
		String token = DataInstance.getInstance().getToken();
		String url =  URL_BASE
				+ String.format(
						"method=object.comment.add&SessionKey=%s&objectid=%s&parentid=%s&objTypeCode=%s",
						token,objectid,parentid,objTypeCode);
		return url;
	}
	//喜欢评论
	public static String GET_URL_LIKE(final String objectid,final String objTypeCode){
		String token = DataInstance.getInstance().getToken();
		String url =  URL_BASE
				+ String.format(
						"method=object.comment.like&SessionKey=%s&id=%s&action=like&objTypeCode=%s",
						token,objectid,objTypeCode);
		return url;
	}
	//删除分享内容
	public static String GET_URL_SHARE_DEL(final String id){
		String token = DataInstance.getInstance().getToken();
		String url =  URL_BASE
				+ String.format(
						"method=chatter.status.destroy&SessionKey=%s&id=%s",
						token,id);
		return url;
	}
	
	//获取点赞人员列表
	public static String GET_URL_LIKE_GETLIST(final String id,final String pageNumber){
		String token = DataInstance.getInstance().getToken();
		String url =  URL_BASE
				+ String.format(
						"method=object.likes.getlist&SessionKey=%s&id=%s&pageNumber=%s&pageSize=15",
						token,id,pageNumber);
		return url;
	}
	
	//发布投票
	public static String GET_URL_POLL_UPDATE(){
		String token = DataInstance.getInstance().getToken();
		String url =  URL_BASE
				+ String.format(
						"method=chatter.poll.update&SessionKey=%s",token);
		return url;
	}
	
	//投票
	public static String GET_URL_POLL_RESPONSE(final String pollid,final String optionids){
		String token = DataInstance.getInstance().getToken();
		String url =  URL_BASE
				+ String.format(
						"method=chatter.poll.response&SessionKey=%s&pollid=%s&optionids=%s",token,pollid,optionids);
		return url;
	}
	
	
	
	public static boolean isTest() {
		return false;
	}
}
