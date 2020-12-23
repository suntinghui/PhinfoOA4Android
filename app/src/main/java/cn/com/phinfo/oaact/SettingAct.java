package cn.com.phinfo.oaact;


import java.util.List;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.MyActBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;
import com.heqifuhou.pulltorefresh.PullToRefreshListView;
import com.heqifuhou.view.ConfirmDialog;
import com.heqifuhou.view.ConfirmDialog.OnDialogOKListener;
import com.heqifuhou.view.PopupWindows;
import com.heqifuhou.view.PopupWindows.OnPopupWindowsItemListener;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.com.phinfo.adapter.SettingAdapter;
import cn.com.phinfo.db.SettingDB;
import cn.com.phinfo.entity.IPListItem;
import cn.com.phinfo.protocol.LURLInterface;
//设置
public class SettingAct extends MyActBase implements OnItemClickListener{
	private PullToRefreshListView refresh = null;
	private SettingAdapter adapter=null;
	private PopupWindows pop;
	private ConfirmDialog confirm;
	private IPListItem selIt;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.addTextNav(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(SettingAct.this,AddSettingAct.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		},"选择链接","添加");
		this.addViewFillInRoot(R.layout.act_setting_list);
		refresh = (PullToRefreshListView) this.findViewById(R.id.id_setting);
		refresh.setMode(Mode.DISABLED);
		
		adapter = new SettingAdapter();
		refresh.setAdapter(adapter);
		refresh.setOnItemClickListener(this);
		if(SettingDB.getInstance().getFromDB().isEmpty()){
			Intent intent = new Intent(SettingAct.this,AddSettingAct.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	}
	
	private void showDialog() {
		if(pop!=null&&pop.isShowing()){
			return;
		}
		pop = new PopupWindows(this,refresh,new String[] { "选中","修攺",
				"删除" });
		pop.show();
		pop.setOnPopupWindowsItemListener(new OnPopupWindowsItemListener() {
			
			@Override
			public void onPopupWindowsItem(int pos) {
				//选中
				if(pos == 0){
					if(SettingDB.getInstance().setCurr(selIt)){
						LURLInterface.init();
					}				
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							finish();
						}
					});
				}
				//修攺
				if(pos == 1){
					Intent intent = new Intent(SettingAct.this,AddSettingAct.class);
					intent.putExtra("M", JSON.toJSONString(selIt));
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
				//删除
				if(pos == 2){
					if(confirm!=null&&confirm.isShowing()){
						return;
					}
					confirm = new ConfirmDialog(SettingAct.this, "确定删除么?", null);
					confirm.show();
					confirm.setOnDialogOKListener(new OnDialogOKListener() {
						@Override
						public void onOKItem(Object obj) {
							adapter.delItem(selIt);
							SettingDB.getInstance().delItem(selIt);
							selIt= null;
						}
					});
		
				}
			}
		});
	} 
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		selIt= adapter.getItem(arg2-1);
		showDialog();
	}
	
	public void onResume(){
		super.onResume();
		List<IPListItem> ls = SettingDB.getInstance().getFromDB();
		adapter.replaceListRef(ls);
	}
}
