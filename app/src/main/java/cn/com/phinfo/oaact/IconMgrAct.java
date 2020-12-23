package cn.com.phinfo.oaact;

import java.util.LinkedHashSet;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.com.phinfo.adapter.IconMgrAdapter;
import cn.com.phinfo.db.AppMgrNeedShowDB;
import cn.com.phinfo.db.NetAppModule2DB;
import cn.com.phinfo.entity.HomeItem;
import cn.com.phinfo.protocol.AppModulesRun;
import cn.com.phinfo.protocol.AppModulesRun.AppModules;
import cn.com.phinfo.protocol.AppModulesRun.AppModulesResultBean;

import com.heqifuhou.actbase.HttpLoginMyActBase;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.view.MyGridView;

public class IconMgrAct extends HttpLoginMyActBase implements  OnItemClickListener {
	//不采用数组，是为了方便需求变更
	private LinearLayout li;
	private IconMgrAdapter[] adapterlst;
	private MyGridView[] myGride;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addTextNav("应用管理");
		this.addViewFillInRoot(R.layout.act_appmgr);
		this.li = (LinearLayout) this.findViewById(R.id.li);
		this.onRefresh();
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		IconMgrAdapter adapter = (IconMgrAdapter) arg0.getAdapter();
		HomeItem it = adapter.getItem(arg2);
		LinkedHashSet<Integer> has = AppMgrNeedShowDB.getInstance().getFromDB();
		if(it!=null){
			//选中的=>不选中
			if(it.getbCheck()){
				it.setbCheck(false);
				((BaseAdapter) arg0.getAdapter()).notifyDataSetChanged();
				has.remove(it.getId());
			}else{
				it.setbCheck(true);
				((BaseAdapter) arg0.getAdapter()).notifyDataSetChanged();
				has.add(it.getId());
			}
			AppMgrNeedShowDB.getInstance().saveToDB(has);
		}
	}
	
	protected void onRefresh() {
		this.quickHttpRequest(0x10, new AppModulesRun());
	}
	
	//将选中的选中
	private void trySetHomeItemInit(List<HomeItem> lst){
		LinkedHashSet<Integer> has = AppMgrNeedShowDB.getInstance().getFromDB();
		for (int i = 0; i < lst.size(); i++) {
			HomeItem it = lst.get(i);
			if(has.contains(it.getId())){
				it.setbCheck(true);
			}
		}
	}
	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if(0x10 == id){
			if(obj.isOK()){
				AppModulesResultBean o  = (AppModulesResultBean)obj;
				List<AppModules> ls = o.getListData();
				NetAppModule2DB.getInstance().saveToDB(ls);
				initLocalViews(ls);
			}else{
				this.showToast(obj.getMsg());
			}
			
		}
	}
	
	private void initLocalViews(List<AppModules> ls){
		this.li.removeAllViews();
		int n  = ls.size();
		myGride = new MyGridView[n];
		adapterlst = new IconMgrAdapter[n];
		for(int i=0;i<n;i++){
			View v = this.getLayoutInflater(R.layout.adapter_item_tab2);
			this.li.addView(v,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
			myGride[i] = (MyGridView) v.findViewById(R.id.appGrid);
			adapterlst[i] = new IconMgrAdapter(true);
			myGride[i].setAdapter(adapterlst[i]);
			myGride[i].setOnItemClickListener(this);
			AppModules it = ls.get(i);
			TextView applabel = (TextView) v.findViewById(R.id.applabel);
			applabel.setText(it.getLabel());
			trySetHomeItemInit(it.getItems());
			adapterlst[i].replaceListRef(it.getItems());
		}
	}
}
