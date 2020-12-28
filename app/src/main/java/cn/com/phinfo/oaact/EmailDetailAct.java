package cn.com.phinfo.oaact;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import cn.com.phinfo.adapter.EmailFileAttacheAdapter;
import cn.com.phinfo.adapter.EmailTopAttacheAdapter;
import cn.com.phinfo.adapter.EmailUserAdapter;
import cn.com.phinfo.entity.DataInstance;
import cn.com.phinfo.protocol.AttacheFileRun.AttacheFileItem;
import cn.com.phinfo.protocol.EmailAddStarRun;
import cn.com.phinfo.protocol.EmailAttacheRun;
import cn.com.phinfo.protocol.EmailAttacheRun.EmailAttacheResultBean;
import cn.com.phinfo.protocol.EmailContentRun;
import cn.com.phinfo.protocol.EmailDelRun;
import cn.com.phinfo.protocol.EmailInfoRun;
import cn.com.phinfo.protocol.EmailInfoRun.DataItem;
import cn.com.phinfo.protocol.EmailInfoRun.EmailInfoResultBean;
import cn.com.phinfo.protocol.EmailListRun.EmailItem;
import cn.com.phinfo.protocol.UnitandaddressRun.UnitandaddressItem;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.utils.FileUtils;
import com.heqifuhou.view.NoScrollListView;
import com.heqifuhou.view.PopupWindows;
import com.heqifuhou.view.PopupWindows.OnPopupWindowsItemListener;

//邮件详情
public class EmailDetailAct extends HttpMyActBase implements
		OnItemClickListener ,OnClickListener{
	private static int ID_GETDETAIL = 0x10, ID_GET_CONTENTDETAIL = 0x11,
			ID_GET_ATTACHE = 0x12,ID_GET_ADDSTAR = 0x13,ID_GET_DEL=0x14;
	private EmailTopAttacheAdapter adapterEmailTop=null;
	private EmailUserAdapter  adapterUser=null;
	private EmailFileAttacheAdapter adapterEmailFileAttach = null;
	private EmailItem it;
	private NoScrollListView reveNameLst, attacheList,attacheListBotton;
	private TextView title, sendName, attachCount, lstSendName, date,txtview;
	private View detailView, allView,rootView,attacheTop;
	private PopupWindows repayPop;
	private WebView webview;
	private String TITLE;
	private DataItem dataItem = null;
	private String BOX;
	//信息
	private String emailContentTxt;
	private EmailAttacheResultBean emailAttacheList;
	private EmailInfoResultBean emailInfo;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BOX = this.getIntent().getExtras().getString("BOX");
		this.addTitleView(R.layout.nav_title_email_img);
		this.addBottomView(R.layout.nav_email_tools);
		TITLE = this.getIntent().getExtras().getString("TITLE");
		this.addTextNav(TITLE);
		String s = this.getIntent().getExtras().getString("EmailItem");
		it = JSON.parseObject(s, EmailItem.class);
		this.addViewFillInRoot(R.layout.act_email_detail);
		reveNameLst = (NoScrollListView) this.findViewById(R.id.reveNameLst);
		attacheList = (NoScrollListView) this.findViewById(R.id.attacheList);
		attacheListBotton = (NoScrollListView) this.findViewById(R.id.attacheListBotton);
		title = (TextView) this.findViewById(R.id.subtitle);
		sendName = (TextView) this.findViewById(R.id.sendName);
		attachCount = (TextView) this.findViewById(R.id.attachCount);
		lstSendName = (TextView) this.findViewById(R.id.lstSendName);
		date = (TextView) this.findViewById(R.id.date);
		detailView = this.findViewById(R.id.detailView);
		allView = this.findViewById(R.id.allView);
		rootView = this.findViewById(R.id.rootView);
		attacheTop = this.findViewById(R.id.attacheTop);
		webview = (WebView) this.findViewById(R.id.txtview);
		this.findViewById(R.id.detailBtn).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						allView.setVisibility(View.VISIBLE);
						detailView.setVisibility(View.GONE);
					}
				});
		this.findViewById(R.id.hideBtn).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						allView.setVisibility(View.GONE);
						detailView.setVisibility(View.VISIBLE);
					}
				});
		this.findViewById(R.id.navEmailBtnStar).setOnClickListener(this);
		this.findViewById(R.id.navEmailBtnDel).setOnClickListener(this);
		this.findViewById(R.id.navEmailBtnRepy).setOnClickListener(this);
		this.findViewById(R.id.navEmailBtnMore).setOnClickListener(this);
		
		adapterEmailTop = new EmailTopAttacheAdapter();
		attacheList.setAdapter(adapterEmailTop);
		attacheList.setOnItemClickListener(this);
		
		adapterUser = new EmailUserAdapter();
		reveNameLst.setAdapter(adapterUser);
		reveNameLst.setOnItemClickListener(this);
		
		adapterEmailFileAttach = new EmailFileAttacheAdapter();
		attacheListBotton.setAdapter(adapterEmailFileAttach);
		attacheListBotton.setOnItemClickListener(this);
		onRefresh();
	}

	protected void onRefresh() {
		this.quickHttpRequest(ID_GETDETAIL, new EmailInfoRun(it.getEmailId()));
		this.quickHttpRequest(ID_GET_CONTENTDETAIL,
				new EmailContentRun(it.getEmailId()));
		this.quickHttpRequest(ID_GET_ATTACHE,
				new EmailAttacheRun(it.getEmailId()));
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(arg0.getAdapter() == adapterEmailTop){
			return;
		}
		if(arg0.getAdapter() == adapterEmailFileAttach){
			/**
			AttacheFileItem attacheItem = adapterEmailFileAttach.getItem(arg2);
			Intent intent = new Intent(this, FileShowAct.class);
			intent.putExtra("AttacheFileItem", JSON.toJSONString(attacheItem));
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			 **/

			AttacheFileItem attacheItem = adapterEmailFileAttach.getItem(arg2);
			FileUtils.downloadAndOpenFile(this, attacheItem);
			
			return;
		}
	}

	public void onHttpForResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		super.onHttpForResult(id, obj, requestObj);

	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if (ID_GETDETAIL == id) {
			if (obj.isOK()) {
				emailInfo = (EmailInfoResultBean) obj;
				dataItem = emailInfo.getData();
				title.setText(emailInfo.getData().getSubject());
				sendName.setText(emailInfo.getData().getFromName());
				lstSendName.setText(emailInfo.getData().getFromName());
				date.setText(emailInfo.getData().getCreatedOn());
				if(emailInfo.getData().getToUsers().size()>0){
					adapterUser.replaceListRef(emailInfo.getData().getToUsers());
				}else{
					//至少得有一个自己啊，这儿没有就是服务器出错了
				}
				
			} else {
				this.showToast(obj.getMsg());
			}
			return;
		}
		if (ID_GET_CONTENTDETAIL == id) {
			emailContentTxt = obj.get2Str();
			webview.loadDataWithBaseURL(null, emailContentTxt, "text/html", "utf-8", null);
			return;
		}
		if (ID_GET_ATTACHE == id) {
			if (obj.isOK()) {
				emailAttacheList = (EmailAttacheResultBean)obj;
				if(emailAttacheList.getListData().size()>0){
					attachCount.setText(String.valueOf(emailAttacheList.getListData().size()));
					attachCount.setVisibility(View.VISIBLE);
					adapterEmailTop.replaceListRef(emailAttacheList.getListData());
					attacheTop.setVisibility(View.VISIBLE);
					adapterEmailFileAttach.replaceListRef(emailAttacheList.getListData());
				}else{
					attachCount.setVisibility(View.GONE);
					attacheTop.setVisibility(View.GONE);
				}
			} else {
				this.showToast(obj.getMsg());
			}
			return;
		}
		if(ID_GET_ADDSTAR == id){
			showToast(obj.getMsg());
			return;
		}
		if(ID_GET_DEL == id){
			//发送删除通知
			if(obj.isOK()){
				finish();
				Intent i = new Intent(IBroadcastAction.ACTION_EMAIL_DEL);
				i.putExtra("EmailItem", JSON.toJSONString(it));
				sendBroadcast(i);
			}
			showToast(obj.getMsg());
			return;
		}
	}

	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()){
		case R.id.navEmailBtnStar:
			addStarAction();
			break;
		case R.id.navEmailBtnDel:
			delAction();
			break;
		case R.id.navEmailBtnRepy:
			repayPopAction();
			break;
		case R.id.navEmailBtnMore:
			break;
		}
	}
	
	private void delAction(){
		this.quickHttpRequest(ID_GET_DEL, new EmailDelRun(it.getEmailId(),BOX));
	}
	//加星
	private void addStarAction(){
		this.quickHttpRequest(ID_GET_ADDSTAR, new EmailAddStarRun(it.getEmailId()));
	}
	
	private void repayPopAction(){
		if(repayPop!=null&&repayPop.isShowing()){
			return;
		}
		repayPop = new PopupWindows(this, rootView, new String[] { "转发","回复"});
		repayPop.show();
		repayPop.setOnPopupWindowsItemListener(new OnPopupWindowsItemListener() {
			@Override
			public void onPopupWindowsItem(int pos) {
				//转发
				if(pos==0){
					startForward();
					return;
				}
				//回复
				if(pos == 1){
					startRepy();
				}
			}
		});
	}

	
	//转发
	private void startForward(){
		DataInstance.getInstance().setEmailInfo(emailAttacheList, emailInfo, emailContentTxt);
		Intent intent = new Intent(this, CreateEmailAct.class);
		intent.putExtra("NTYPE", 2);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	//回复
	private void startRepy(){
		UnitandaddressItem it = new UnitandaddressItem();
		it.setFullName(dataItem.getFromName());
		it.setSystemUserId(dataItem.getFromUserId());
		//添加
		DataInstance.getInstance().addUnitandaddressItem(it);
		Intent intent = new Intent(this, CreateEmailAct.class);
		intent.putExtra("NTYPE", 3);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}
