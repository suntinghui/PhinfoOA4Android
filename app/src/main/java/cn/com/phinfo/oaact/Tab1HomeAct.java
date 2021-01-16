package cn.com.phinfo.oaact;import java.io.Serializable;import java.util.ArrayList;import java.util.Iterator;import java.util.List;import java.util.Stack;import com.alibaba.fastjson.JSON;import com.heqifuhou.ioscalendar.CalendarAct;import com.heqifuhou.protocolbase.HttpResultBeanBase;import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;import com.heqifuhou.pulltorefresh.PullToRefreshListView;import com.heqifuhou.tab.HttpMyActTabChildBase;import com.heqifuhou.toprightmenu.MenuItem;import com.heqifuhou.toprightmenu.TopRightMenu;import android.content.Intent;import android.os.Bundle;import android.util.Log;import android.view.View;import android.view.View.OnClickListener;import android.widget.AdapterView;import android.widget.AdapterView.OnItemClickListener;import cn.com.phinfo.adapter.HomeTab1Adapter;import cn.com.phinfo.entity.DataInstance;import cn.com.phinfo.entity.HomeItem;import cn.com.phinfo.entity.HomeTab1Item;import cn.com.phinfo.protocol.LURLInterface;import cn.com.phinfo.protocol.MessageStaticsListRun;import cn.hutool.core.util.StrUtil;public class Tab1HomeAct extends HttpMyActTabChildBase implements OnItemClickListener{	private static int ID_MESSAGE_STATIC_LIST = 0x10;	private PullToRefreshListView pullToRefreshListView;	private HomeTab1Adapter adapter;	public void onCreate(Bundle savedInstanceState) {		super.onCreate(savedInstanceState);		this.addTextNav(new OnClickListener() {			@Override			public void onClick(View arg0) {				showPopMenu();			}		}, "消息",R.drawable.more_add);		this.hideBackNav();		this.addViewFillInRoot(R.layout.act_home);		pullToRefreshListView = (PullToRefreshListView) this.findViewById(R.id.refreshListView);		pullToRefreshListView.setMode(Mode.DISABLED);		adapter = new HomeTab1Adapter();		pullToRefreshListView.setAdapter(adapter);		pullToRefreshListView.setOnItemClickListener(this);	}	@Override	protected void onResume() {		super.onResume();		this.onRefresh();	}	private TopRightMenu mTopRightMenu=null;	private void showPopMenu(){		mTopRightMenu = new TopRightMenu(this);		//添加菜单项		List<MenuItem> menuItems = new ArrayList<>();		menuItems.add(new MenuItem(R.drawable.ic_h_q, "扫一扫"));//		menuItems.add(new MenuItem(R.drawable.ic_h_u, "优盘"));//		menuItems.add(new MenuItem(R.drawable.ic_h_e, "邮件"));		mTopRightMenu		        .setHeight(600)     //默认高度480		        .setWidth(420)      //默认宽度wrap_content		        .showIcon(true)     //显示菜单图标，默认为true		        .dimBackground(true)        //背景变暗，默认为true		        .needAnimationStyle(true)   //显示动画，默认为true		        .setAnimationStyle(R.style.TRM_ANIM_STYLE)		        .addMenuList(menuItems)		        .setOnMenuItemClickListener(new TopRightMenu.OnMenuItemClickListener() {		              @Override		              public void onMenuItemClick(int position) {		                 //二维码		                if(position==0){		            		Intent intent = new Intent(Tab1HomeAct.this,HomeCaptureAct.class);		            		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);		            		startActivity(intent);		                 }		                //优盘		                else if(position==1){		            		Intent intent = new Intent(Tab1HomeAct.this,UAct.class);		            		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);		            		startActivity(intent);		                 }		                //邮件		                else if(position==2){		            		Intent intent = new Intent(Tab1HomeAct.this,EmailAct.class);		            		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);		            		startActivity(intent);		                 }		              }		        })		        .showAsDropDown(this.findViewById(R.id.nav_btn_img), -225, 0);	//带偏移量	}		public void onHttpForResult(int id, HttpResultBeanBase obj, Object requestObj) {		super.onHttpForResult(id, obj, requestObj);		pullToRefreshListView.onRefreshComplete();// 下拉请求完成	}	protected void onRefresh() {		this.quickHttpRequest(ID_MESSAGE_STATIC_LIST, new MessageStaticsListRun());	}	@Override	protected void onHttpResult(int id, HttpResultBeanBase obj, Object requestObj) {		if (id == ID_MESSAGE_STATIC_LIST) {			if (obj.isOK()) {				adapter.clear();				MessageStaticsListRun.MessageStaticResultBean bean = (MessageStaticsListRun.MessageStaticResultBean)obj;				List<MessageStaticsListRun.MessageStaticItem> list = bean.getListDta();				Iterator<MessageStaticsListRun.MessageStaticItem> it = list.iterator();				while (it.hasNext()) {					MessageStaticsListRun.MessageStaticItem item = it.next();					if (!StrUtil.isEmpty(item.getTitle())) {						adapter.addToListBack(item);					}				}			} else {				showToast(obj.getMsg());			}		}	}	@Override	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {		MessageStaticsListRun.MessageStaticItem it = adapter.getItem(arg2-1);		if (it.getType().equalsIgnoreCase("web")) {			startWebAct(it.getTitle(), it.getLinkUrl());			return;		}		int id = 999;		try{			id = Integer.parseInt(it.getModuleId());		}catch (Exception e) {			e.printStackTrace();		}		switch (id) {			case 101://查看待办内容				startTodosActt();				break;			case 103: // 日程				startCalendarAct();				break;			case 104://查看新闻内容				startNewsActt(it,1);				break;			case 105: // 任务				break;			case 106://查看优盘内容				startUFileAct();				break;			case 107://查看邮件内容				startEmailAct();				break;			case 110://查看日志				startReprotAct();				break;			case 111:// 会议				startMeetingAct();				break;			case 702://通知			case 100202://通知公告				startNewsActt(it,2);				break;//			case 6000: // 社区////				break;			case 30300: // 问卷				GET_MYLIST();				break;			default:				Log.e("yao2", "点击了："+JSON.toJSONString(it));				showToast("暂未实现");				break;		}	}	private void startWebAct(final String title,final String url){		Intent intent = new Intent(this,WebViewRefreshAct.class);		intent.putExtra("TITLE",title);		intent.putExtra("URL",LURLInterface.Create(url));		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);		this.startActivity(intent);	}		private void startNewsActt(MessageStaticsListRun.MessageStaticItem _it,int contentTypeCode){		List<HomeItem> ls = new Stack<HomeItem>();//		for(int i=0;i<adapterlst.length;i++){//			ls.addAll(adapterlst[i].getNewsItemList());//		}		HomeItem it = new HomeItem();		it.setLabel(_it.getTitle());		Intent intent = new Intent(this,NewsAct.class);		intent.putExtra("NEWSLIST", (Serializable)ls);		intent.putExtra("HomeItem", it);		intent.putExtra("contentTypeCode", contentTypeCode);		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);		this.startActivity(intent);	}	private void startNewsActt(){		Intent intent = new Intent(this,NewsAct.class);		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);		this.startActivity(intent);	}	private void startReprotAct(){		Intent intent = new Intent(this,ReportAct.class);		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);		this.startActivity(intent);	}	private void startMeetingAct() {		Intent intent = new Intent(this,MeetingAssistantAct.class);		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);		this.startActivity(intent);	}	private void startTodosActt(){		Intent intent = new Intent(this,TodosAct.class);		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);		this.startActivity(intent);	}	private void startUFileAct(){		Intent intent = new Intent(this,UAct.class);		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);		this.startActivity(intent);	}	private void startEmailAct(){		Intent intent = new Intent(this,EmailAct.class);		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);		this.startActivity(intent);	}	private void startCalendarAct(){		Intent intent = new Intent(this, CalendarAct.class);		intent.putExtra("showBack", true);		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);		this.startActivity(intent);	}	// 问卷	public static String GET_MYLIST() {		String token = DataInstance.getInstance().getToken();		String url = LURLInterface.getUrlWeb()				+ String.format("/_ui/svry/mylist?SessionKey=%s", token);		return url;	}}