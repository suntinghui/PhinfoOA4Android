package cn.com.phinfo.oaact;


import java.util.List;

import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.heqifuhou.pulltorefresh.PullToRefreshScrollView;
import com.heqifuhou.utils.ParamsCheckUtils;
import com.heqifuhou.view.NoScrollListView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.TextView;
import cn.com.phinfo.adapter.SelectPersonAdapter;
import cn.com.phinfo.entity.DataInstance;
import cn.com.phinfo.protocol.AddressSearchRun;
import cn.com.phinfo.protocol.LURLInterface;
import cn.com.phinfo.protocol.AddressSearchRun.AddressSearchResultBean;
import cn.com.phinfo.protocol.UnitandaddressRun.UnitandaddressItem;


public class SelectPersonAct extends HttpMyActBase implements OnClickListener,OnItemClickListener{
	private static int PERPAGE_SIZE = 15;
	private PullToRefreshScrollView  scrollview;
	private NoScrollListView refresh;
	private SelectPersonAdapter adapter=null;
	private int pageNumber = 1;
	private TextView btnSelect;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTextNav("选择联系人");
		this.addViewFillInRoot(R.layout.act_select_person);
		refresh = (NoScrollListView) this.findViewById(R.id.refresh);
		scrollview = (PullToRefreshScrollView) this.findViewById(R.id.scrollview);
		this.addBottomView(R.layout.nav_select_tools);
		
		this.findViewById(R.id.department).setOnClickListener(this);
		this.findViewById(R.id.role).setOnClickListener(this);
		this.findViewById(R.id.often).setOnClickListener(this);
		this.findViewById(R.id.queryBtn).setOnClickListener(this);
		adapter = new SelectPersonAdapter();
		refresh.setAdapter(adapter);
		refresh.setOnItemClickListener(this);
		btnSelect = (TextView) this.findViewById(R.id.btnSelect);
		btnSelect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//提交
				if(DataInstance.getInstance().getUnitandaddressItemList().isEmpty()){
					showToast("没有选择人");
//					setResult(RESULT_CANCELED);
				}else{
					setResult(RESULT_OK);
					finish();
				}
				
			}
		});
		scrollview.setMode(Mode.DISABLED);
//		scrollview.setOnRefreshListener(new OnRefreshListener2() {
//			@Override
//			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
//				pageNumber = 1;
//				onRefresh();
//				hideLoading(true);
//			}
//			@Override
//			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
//				onRefresh();
//				hideLoading(true);
//			}
//		});
	}

	
	protected void onResume(){
		super.onResume();
		showCount();
		showList();
	}
	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()){
		case R.id.department:
			startDepartmentAct();
			break;
		case R.id.role:
			startRolesAct();
			break;
		case R.id.often:
			startGroupAct();
			break;
		case R.id.queryBtn:
			startSearchAddressAct();
			break;
		}
	}
	private void startSearchAddressAct() {
		Intent intent = new Intent(this, SearchUserAddressAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}


	private void startGroupAct(){
		Intent intent = new Intent(this,SelectGroupAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}
	private void startDepartmentAct(){
		Intent intent = new Intent(this,SelectDepartmentAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}
	
	private void startRolesAct(){
		Intent intent = new Intent(this,SelectRolesAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}
	protected void onRefresh(){
//		String search = queryEdit.getText().toString().trim();
//		this.quickHttpRequest(0X10, new AddressSearchRun(search,pageNumber)); 
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		UnitandaddressItem it= (UnitandaddressItem) adapter.getItem(arg2);
		it.setIsbSel(!it.getIsbSel());
		if(it.getIsbSel()){
			DataInstance.getInstance().addUnitandaddressItem(it);
		}else{
			DataInstance.getInstance().removeUnitandaddressItem(it);
			adapter.tryRemove(it);
		}
		showCount();
	}
	

	public void onHttpForResult(int id, HttpResultBeanBase obj, Object requestObj) {
		super.onHttpForResult(id, obj, requestObj);
		scrollview.onRefreshComplete();
	}
	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj, Object requestObj) {
		super.onHttpForResult(id, obj, requestObj);
//		if(id == 0X10){
//			if (obj.isOK()) {
//				AddressSearchResultBean o = (AddressSearchResultBean)obj;
//				if (pageNumber == 1) {
//					adapter.clear();
//				}
//				for(UnitandaddressItem it:o.getListData()){
//					it.setIsbSel(DataInstance.getInstance().isContains(it));
//				}
//				adapter.addToListBack(o.getListData());
//				pageNumber++;
//				if (o.getListData().size() < PERPAGE_SIZE) {
//					scrollview.setMode(Mode.PULL_FROM_START);
//				} else {
//					scrollview.setMode(Mode.BOTH);
//				}
//			} else {
//				showToast(obj.getMsg());
//			}
//			if (adapter.getCount() <= 0) {
//				refresh.setEmptyView(this.getEmptyView(0xff333333));
//			} else {
//				removeEmptyView();
//			}
//		}
	}
	
	private void showList(){
		List<UnitandaddressItem> ls = DataInstance.getInstance().getUnitandaddressItemList();
		adapter.replaceListRef(ls);
		if(!ls.isEmpty()){
			refresh.setEmptyView(this.getEmptyView(0xff333333));
		}else{
			removeEmptyView();
		}
	}
	private void showCount(){
		int count = DataInstance.getInstance().getUnitandaddressItemList().size();
		btnSelect.setText("确定("+count+"人)");
	}
}
