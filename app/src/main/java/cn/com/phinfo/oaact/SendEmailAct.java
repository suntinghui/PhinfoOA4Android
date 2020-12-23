package cn.com.phinfo.oaact;


import java.util.List;

import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.draggridView.DragGridView;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.view.NoScrollGridView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.com.phinfo.adapter.NewsChanelAdapter;
import cn.com.phinfo.adapter.NewsChanelSelAdapter;
import cn.com.phinfo.db.NewsChanelAddressDB;
import cn.com.phinfo.protocol.NewsChannelRun;
import cn.com.phinfo.protocol.NewsChannelRun.HSelectItem;
import cn.com.phinfo.protocol.NewsChannelRun.NewsChannelResultBean;

//发送邮件
public class SendEmailAct extends HttpMyActBase implements OnItemClickListener{
	private static int ID_GETLIST= 0x10;
	private NoScrollGridView gridview;
	private DragGridView draggridview;
	private NewsChanelAdapter newsChanelAdapter = null;
	private NewsChanelSelAdapter newsChanelSelAdapter = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addViewFillInRoot(R.layout.act_add_news);
		this.findViewById(R.id.btn_del).setOnClickListener(onBackListener);
		gridview = (NoScrollGridView) this.findViewById(R.id.gridview);
		draggridview = (DragGridView) this.findViewById(R.id.draggridview);
		
		newsChanelAdapter = new NewsChanelAdapter();
		gridview.setAdapter(newsChanelAdapter);
		gridview.setOnItemClickListener(this);
		
		newsChanelSelAdapter = new NewsChanelSelAdapter();
		draggridview.setAdapter(newsChanelSelAdapter);
//		draggridview.setOnItemClickListener(this);
		onRefresh();
		this.findViewById(R.id.btnOk).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {	
				NewsChanelAddressDB.getInstance().saveToDB(newsChanelSelAdapter.getListRef());
				sendBroadcast(new Intent(IBroadcastAction.ACTION_CHANEL_CHANGE));
				finish();
			}
		});
	}
	
	protected void onRefresh(){
		this.quickHttpRequest(ID_GETLIST, new NewsChannelRun());
		List<HSelectItem> ls = NewsChanelAddressDB.getInstance().getFromDB();
		newsChanelSelAdapter.replaceListRef(ls);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		//频道推荐
		if(arg0.getAdapter()==newsChanelAdapter){
			HSelectItem it = newsChanelAdapter.getItem(arg2);
			newsChanelSelAdapter.addToListBack(it);
			return;
		}
	}
	public void onHttpForResult(int id, HttpResultBeanBase obj, Object requestObj) {
		super.onHttpForResult(id, obj, requestObj);
	
	}
	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj, Object requestObj) {
		if(ID_GETLIST == id){
			if(obj.isOK()){
				NewsChannelResultBean o = (NewsChannelResultBean)obj;
				newsChanelAdapter.replaceListRef(o.getListData());
			}else{
				this.showToast(obj.getMsg());
			}
		}
	}
}
